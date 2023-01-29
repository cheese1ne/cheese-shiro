package com.cheese.shiro.server.gateway.host;

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

import java.util.concurrent.TimeUnit;

/**
 * 主机信息缓存管理器 redis 实现
 * @author sobann
 */
public class RedisCacheHostManager implements CacheHostManager {
    private static final Logger logger = LoggerFactory.getLogger(RedisCacheHostManager.class);
    private static final String PREFIX ="AssignRoute:";

    private RedisTemplate<String,Host> hostRedisTemplate;

    private int ttl;

    public RedisCacheHostManager(RedisTemplate<String, Host> hostRedisTemplate, int ttl) {
        this.hostRedisTemplate = hostRedisTemplate;
        this.ttl = ttl;
    }

    public RedisCacheHostManager(RedisConnectionFactory redisConnectionFactory, int ttl) {
        this.ttl = ttl;
        this.hostRedisTemplate = buildTempalte(redisConnectionFactory);
        this.hostRedisTemplate.afterPropertiesSet();
    }

    @Override
    public Host gainCache(String assignKey) {
        String cacheKey = getcacheKey(assignKey);
        Host host = hostRedisTemplate.opsForValue().get(cacheKey);
        if(host!=null){
            logger.info("Get Cache Assign Route :AssignKey={}, Host={}, port={}",assignKey,host.getHost(),host.getPort());
        }
        return host;
    }

    @Override
    public void createCache(String assignKey, Host host) {
        hostRedisTemplate.opsForValue().set(getcacheKey(assignKey),host, ttl, TimeUnit.SECONDS);
        logger.info("Cache Assign Route :AssignKey={}, Host={}, port={}",assignKey,host.getHost(),host.getPort());
    }

    @Override
    public void clearCache(String assignKey) {
        hostRedisTemplate.delete(getcacheKey((String)assignKey));
    }

    public String getcacheKey(String assignKey){
        return PREFIX +assignKey;
    }


    public static RedisTemplate<String,Host> buildTempalte(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Host> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        template.setKeySerializer(redisSerializer);
        template.setHashKeySerializer(redisSerializer);
        Jackson2JsonRedisSerializer<Host> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        return template;
    }
}
