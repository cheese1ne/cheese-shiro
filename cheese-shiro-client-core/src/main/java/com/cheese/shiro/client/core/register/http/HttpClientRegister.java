package com.cheese.shiro.client.core.register.http;

import com.cheese.shiro.client.core.checker.register.ServiceRegisterStatusChecker;
import com.cheese.shiro.client.core.register.ClientRegister;
import com.cheese.shiro.common.config.ServerConfig;
import com.cheese.shiro.common.config.WebServerConfig;
import com.cheese.shiro.common.domain.ClientInstance;
import com.cheese.shiro.common.exception.ServiceNotRegisteredException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 将 ClientInstance 序列化后，注册至网关
 *
 * @author sobann
 */
public class HttpClientRegister extends ClientRegister {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientRegister.class);
    private ServiceRegisterStatusChecker serviceRegisterStatusChecker;
    private RestTemplate restTemplate;
    private List<String> servers;
    private ScheduledExecutorService scheduler;
    private int registerInterval;
    private int syncInterval;
    private boolean serverDynamic;


    public HttpClientRegister(ServiceRegisterStatusChecker serviceRegisterStatusChecker,RestTemplate restTemplate, String servers, int threadNum, int registerInterval, int syncInterval, boolean serverDynamic) {
        this.serviceRegisterStatusChecker = serviceRegisterStatusChecker;
        this.restTemplate = restTemplate;
        this.scheduler = Executors.newScheduledThreadPool(threadNum, new ThreadFactoryBuilder().setNameFormat("ShiroClient-Sync-%d").setDaemon(true).build());
        this.registerInterval = registerInterval;
        this.syncInterval = syncInterval;
        this.servers = Arrays.asList(servers.split(","));
        if (this.servers.size() == 0) {
            throw new RuntimeException("Can Not Find Any Available Gateway Servers !");
        }
        this.serverDynamic = serverDynamic;
    }

    private String getRegisterUrl(String server) {
        return "http://" + server + "/gateway/config/register";
    }

    private String getConfigUrl(String server) {
        return "http://" + server + "/gateway/config/sync";
    }

    private String getCheckHealthUrl(String server) {
        return "http://" + server + "/gateway/config/health";
    }

    @PreDestroy
    public void destory() {
        scheduler.shutdownNow();
    }

    @Override
    public void registerToServer(ClientInstance clientInstance) {
        String content = getCoder().encode(clientInstance);
        RegisterTask registerTask = new RegisterTask(clientInstance, content);
        scheduler.schedule(registerTask, 0, TimeUnit.SECONDS);
    }

    @Override
    public void refreshConfigWithServer(ServerConfig serverConfig) {
        syncConfigWithServer(serverConfig.getShiroConfig());
    }

    class RegisterTask implements Runnable {

        private ClientInstance clientInstance;
        private String registerContent;
        //当前可用服务
        private volatile String currentServer;

        public RegisterTask(ClientInstance clientInstance, String registerContent) {
            this.clientInstance = clientInstance;
            this.registerContent = registerContent;
        }

        @Override
        public void run() {
            //查找可用的服务
            if (currentServer == null) {
                currentServer = getAvailableServer();
            }
            //如果没有可用服务，延后进行
            if (currentServer == null) {
                scheduler.schedule(this, registerInterval, TimeUnit.MILLISECONDS);
            } else {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                HttpEntity<String> httpEntity = new HttpEntity<>(registerContent, headers);
                String registerUrl = getRegisterUrl(currentServer);
                try {
                    //检查服务是否注册至注册中心
                    if (!serviceRegisterStatusChecker.check()){
                        throw new ServiceNotRegisteredException(clientInstance.getService());
                    }
                    logger.info("Try To Register Service {} to Server {}", clientInstance.getService(), registerUrl);
                    String body = restTemplate.exchange(registerUrl, HttpMethod.PUT, httpEntity, String.class).getBody();
                    logger.info("Success To Register Service {} to Server {}", clientInstance.getService(), registerUrl);
                    //更新客户端配置
                    WebServerConfig webServerConfig = (WebServerConfig) getCoder().decode(body);
                    refreshConfigWithServer(webServerConfig);
                    refreshServers(webServerConfig);
                    if (clientInstance.isStartUp() && syncInterval > 0) {
                        //开启心跳同步客户端配置
                        ConfigSyncTask syncTask = new ConfigSyncTask(currentServer);
                        scheduler.schedule(syncTask, syncInterval, TimeUnit.MILLISECONDS);
                    }
                } catch (Exception e) {
                    logger.warn("Error To Register Service {} to Server {}, the error reason is {}", clientInstance.getService(), registerUrl, e.getMessage());
                    //注册未成功，清空执行服务，在下次任务进行时，确认可用服务
                    currentServer = null;
                    logger.info("Clean Current Server For Register");
                    //再次进行注册
                    scheduler.schedule(this, registerInterval, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    class ConfigSyncTask implements Runnable {
        //当前可用的服务
        private volatile String currentServer;

        public ConfigSyncTask(String currentServer) {
            this.currentServer = currentServer;
        }

        @Override
        public void run() {
            //查找可用服务
            if (currentServer == null) {
                currentServer = getAvailableServer();
            }
            //存在可用服务，进行同步
            if (currentServer != null) {
                String configUrl = getConfigUrl(currentServer);
                try {
                    logger.info("Try to Sync Config With Server :{}", configUrl);
                    String config = restTemplate.getForObject(configUrl, String.class);
                    WebServerConfig webServerConfig = (WebServerConfig) getCoder().decode(config);
                    refreshConfigWithServer(webServerConfig);
                    refreshServers(webServerConfig);
                } catch (RestClientException e) {
                    logger.warn("Has Error In Sync Config With Server :{}", configUrl);
                    currentServer = null;
                    logger.info("Clean Current Server For Sync");
                }
            }
            //持续进行同步
            scheduler.schedule(this, syncInterval, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 更新服务列表
     *
     * @param webServerConfig
     */
    private void refreshServers(WebServerConfig webServerConfig) {
        //当开启动态服务列表时，进行更新
        if (!serverDynamic) {
            return;
        }
        List<String> newServers = webServerConfig.getServers();
        if (!CollectionUtils.isEmpty(newServers)) {
            for (String server : newServers) {
                if (!servers.contains(server)) {
                    servers.add(server);
                }
            }
        }
    }

    /**
     * 查找多配置下的可用服务
     *
     * @return
     */
    private String getAvailableServer() {
        if (servers.size() == 1) {
            String server = servers.get(0);
            logger.info("There is Only One Server Config : {}", server);
            return server;
        }
        for (String server : servers) {
            if (isServerAvailable(server)) {
                logger.info("Find Server {} is Available", server);
                return server;
            } else {
                logger.warn("Find Server {} is Unavailable", server);
            }
        }
        logger.error("Can Not Find Any Availbale Server From {}", servers);
        return null;
    }

    /**
     * 确认服务是否可用
     *
     * @param server
     * @return
     */
    private boolean isServerAvailable(String server) {
        String healthUrl = getCheckHealthUrl(server);
        try {
            restTemplate.getForObject(healthUrl, Boolean.class);
            return true;
        } catch (RestClientException e) {
            return false;
        }
    }
}
