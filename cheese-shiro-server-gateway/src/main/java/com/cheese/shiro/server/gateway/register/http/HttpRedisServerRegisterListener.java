package com.cheese.shiro.server.gateway.register.http;

import com.cheese.shiro.common.domain.ClientInstance;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 使用redis进行缓存，多实例实现
 * @author sobann
 */
public class HttpRedisServerRegisterListener extends HttpServerRegisterListener {
    private static final Logger logger = LoggerFactory.getLogger(HttpRedisServerRegisterListener.class);

    private final static String KEY = "InstanceConfig";
    private final static String SERVER_KEY = "GatewayServer";
    private final static String LATEST = "latest";
    private final static String EXPIRE = "expire";

    /**
     * redis缓存操作
     */
    private RedisTemplate<String, ClientInstance> redisTemplate;

    /**
     * http请求操作
     */
    private RestTemplate restTemplate;

    private final ScheduledExecutorService scheduler;
    /**
     * ip:port
     */
    private String server;
    /**
     * 检查间隔,单位：秒
     */
    private int cacheCheckInterval;
    /**
     * 过期周期=检查间隔+10s
     */
    private int expirePeriod;

    public HttpRedisServerRegisterListener(RedisConnectionFactory redisConnectionFactory, String server, int threadNum, int cacheCheckInterval) {
        this(redisConnectionFactory, server, new RestTemplate(), threadNum, cacheCheckInterval);
    }

    public HttpRedisServerRegisterListener(RedisConnectionFactory redisConnectionFactory, String server, RestTemplate restTemplate, int threadNum, int cacheCheckInterval) {
        this.redisTemplate = buildTempalte(redisConnectionFactory);
        this.redisTemplate.afterPropertiesSet();
        this.server = server;
        this.restTemplate = restTemplate;
        this.scheduler = Executors.newScheduledThreadPool(threadNum);
        this.cacheCheckInterval = cacheCheckInterval;
        this.expirePeriod = cacheCheckInterval + 10;
    }


    /**
     * 使用redis缓存进行更新
     *
     * @param service
     */
    @Override
    public void refreshInstanceConfig(String service) {
        Object value = redisTemplate.opsForHash().get(KEY, service);
        if (value != null) {
            ClientInstance clientInstance = (ClientInstance) value;
            registerInstance(clientInstance);
            logger.info("Refresh InstanceConfig For {} From Cache", clientInstance.getService());
        }
    }

    @Override
    public void register(ClientInstance clientInstance) {
        super.register(clientInstance);
        //更新redis缓存
        redisTemplate.opsForHash().put(KEY, clientInstance.getService(), clientInstance);
        //通知其他网关实例进行更新(异步)
        scheduler.schedule(new RefreshTask(clientInstance.getService()), 0, TimeUnit.SECONDS);
    }

    public Date getCacheExpireDate() {
        return new Date(System.currentTimeMillis() + expirePeriod * 1000);
    }


    private List<String> getOtherInstance() {
        Date now = new Date();
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(SERVER_KEY);
        List<String> others = new ArrayList<>();
        entries.forEach(
                (key, value) -> {
                    String instance = (String) key;
                    Date expireDate = (Date) value;
                    if (!key.equals(server) && now.before(expireDate)) {
                        others.add(instance);
                    }
                }
        );
        logger.info("Find Other Alive Gateway Server :{}", others);
        return others;
    }

    private String getRefreshUrl(String server, String service) {
        return "http://" + server + "/gateway/config/refresh/" + service;
    }

    /**
     * 通知服务刷新
     * 通过HTTP方式告知其他服务
     * 例如：http://192.168.84.106:80001/gateway/config/refresh/perm
     * 详见 JerseyServerConfigResource.refresh方法
     * @param server
     * @param service
     */
    private boolean notifyRefresh(String server, String service) {
        try {
            String refreshUrl = getRefreshUrl(server, service);
            restTemplate.getForObject(refreshUrl, Boolean.class);
            logger.info("Success To Notify Server={},service={} Refresh ", server, service);
            return true;
        } catch (RestClientException e) {
            logger.info("Fail To Notify Server={},service={} Refresh ", server, service);
            return false;
        }
    }

    /**
     * 通知服务刷新
     *
     * @param servers
     * @param service
     */
    public void notifyOthersRefresh(Collection<String> servers, String service) {
        if (!CollectionUtils.isEmpty(servers)) {
            for (String server : servers) {
                boolean refresh = notifyRefresh(server, service);
                //刷新不成功，标识过期
                if (!refresh) {
                    markServerExpireForCluster(server);
                }
            }
        } else {
            logger.info("NO Other Gateway Server Alive");
        }
    }

    public void notifyOthersRefresh(String service) {
        List<String> otherInstance = getOtherInstance();
        notifyOthersRefresh(otherInstance, service);
    }


    /**
     * 使用缓存，进行配置初始化
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        //注册服务
        registerServerForCluster(server);
        //强制初始化缓存
        initCache();
        //开启缓存定时检查
        scheduler.scheduleAtFixedRate(new CacheCheckTask(), cacheCheckInterval, cacheCheckInterval, TimeUnit.SECONDS);
        //开启集群实例检查
        scheduler.scheduleAtFixedRate(new ClusterInstanceTask(), 0, 5 * cacheCheckInterval, TimeUnit.SECONDS);
    }

    private void initCache() {
        List<Object> values = redisTemplate.opsForHash().values(KEY);
        for (Object value : values) {
            ClientInstance clientInstance = (ClientInstance) value;
            //将redis中保存的服务缓存实例信息保存到共享变量ServerRegisterListener中的instances中
            registerInstance(clientInstance);
            logger.info("Initialize InstanceConfig For {} From Redis", clientInstance.getService());
        }
    }


    /**
     * 服务关闭前，清除实例列表
     */
    @PreDestroy
    public void removeFromCluster() {
        redisTemplate.opsForHash().delete(SERVER_KEY, server);
        logger.info("Delete {} From Gateway Server Cluster List", server);
    }

    /**
     * 将服务注册至服务列表
     *
     * @param server
     */
    private void registerServerForCluster(String server) {
        redisTemplate.opsForHash().put(SERVER_KEY, server, getCacheExpireDate());
        logger.info("Register {} In Gateway Server Cluster List", server);
    }

    /**
     * 标注服务过期
     *
     * @param server
     */
    private void markServerExpireForCluster(String server) {
        redisTemplate.opsForHash().put(SERVER_KEY, server, new Date());
        logger.info("Mark {} In Gateway Server Cluster Expire List", server);
    }

    /**
     * 通知刷新任务
     *
     */
    class RefreshTask implements Runnable {
        private String service;

        public RefreshTask(String service) {
            this.service = service;
        }

        @Override
        public void run() {
            notifyOthersRefresh(service);
        }
    }

    /**
     * 缓存检查任务
     * 用于网关实例自身更新redis的有效期
     * redis中的数据格式为hash为 GatewayServer 网关ip:port 过期时间
     */
    class CacheCheckTask implements Runnable {
        @Override
        public void run() {
            Date expireDate = (Date) redisTemplate.opsForHash().get(SERVER_KEY, server);
            if (expireDate == null || expireDate.before(new Date())) {
                initCache();
            }
            expireDate = getCacheExpireDate();
            redisTemplate.opsForHash().put(SERVER_KEY, server, expireDate);
        }
    }

    /**
     * 集群检查任务，移除过期实例
     * 若网关实例ip:port不同于配置且已过期，删除redis中的网关配置信息
     */
    class ClusterInstanceTask implements Runnable {
        @Override
        public void run() {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(SERVER_KEY);
            Date now = new Date();
            entries.forEach(
                    (key, value) -> {
                        String instance = (String) key;
                        Date expireDate = (Date) value;
                        if (!server.equals(instance) && expireDate.before(now)) {
                            redisTemplate.opsForHash().delete(SERVER_KEY, instance);
                        }
                    }
            );
        }
    }


    public static RedisTemplate<String, ClientInstance> buildTempalte(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ClientInstance> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        template.setKeySerializer(redisSerializer);
        template.setHashKeySerializer(redisSerializer);
        Jackson2JsonRedisSerializer<ClientInstance> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        return template;
    }

}
