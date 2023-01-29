package com.cheese.shiro.session.manager;

import com.cheese.shiro.common.manager.session.Session;
import com.cheese.shiro.common.manager.session.SessionStoreManager;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 用户会话管理器的redis实现
 * @author sobann
 */
public class RedisSessionStoreManager implements SessionStoreManager {
    public final String PreFix;

    private long ttlMin = 120;

    private RedisTemplate<String, Session> template;

    public RedisSessionStoreManager(RedisConnectionFactory redisConnectionFactory) {
        this.template = buildTemplate(redisConnectionFactory);
        this.template.afterPropertiesSet();
        PreFix = "Session";
    }

    public RedisSessionStoreManager(RedisTemplate<String, Session> template) {
        this.template = template;
        PreFix = "Session";
    }

    public RedisSessionStoreManager(String preFix, RedisConnectionFactory redisConnectionFactory) {
        PreFix = preFix;
        this.template = buildTemplate(redisConnectionFactory);
        this.template.afterPropertiesSet();
    }

    public RedisSessionStoreManager(String preFix, RedisTemplate<String, Session> template) {
        PreFix = preFix;
        this.template = template;
    }

    @Override
    public void setExpire(long min) {
        this.ttlMin = min;
    }

    @Override
    public long getExpire() {
        return this.ttlMin;
    }


    private String getCachceKey(String key) {
        return PreFix + ":" + key;
    }

    private String getCachePropKeys(String key) {
        return PreFix + "_prop:" + key;
    }

    @Override
    public Session get(String key) {
        String cacheKey = getCachceKey(key);
        return template.opsForValue().get(cacheKey);
    }

    @Override
    public void save(String key, Session session) {
        String cacheKey = getCachceKey(key);
        template.opsForValue().set(cacheKey, session, ttlMin, TimeUnit.MINUTES);
        String cachePropKeys = getCachePropKeys(key);
        setExpire(cachePropKeys);
    }

    @Override
    public void clear(String key) {
        String cacheKey = getCachceKey(key);
        String cachePropKeys = getCachePropKeys(key);
        template.delete(Arrays.asList(cacheKey, cachePropKeys));
    }

    @Override
    public Collection<Session> getLives() {
        List<String> keys = keys(PreFix + ":*");
        return template.opsForValue().multiGet(keys);
    }

    public void scan(String pattern, Consumer<byte[]> consumer) {
        template.executeWithStickyConnection((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(1000).match(pattern).build())) {
                cursor.forEachRemaining(consumer);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    public List<String> keys(String pattern) {
        List<String> keys = new ArrayList<>();
        this.scan(pattern, item -> {
            keys.add(new String(item, StandardCharsets.UTF_8));
        });
        return keys;
    }

    @Override
    public int getLiveNum() {
        List<String> keys = keys(PreFix + "*");
        return keys.size();
    }

    @Override
    public Boolean isLive(String key) {
        String cacheKey = getCachceKey(key);
        return template.hasKey(cacheKey);
    }

    @Override
    public Object getProp(String key, String prop) {
        String cacheKey = getCachePropKeys(key);
        return template.opsForHash().get(cacheKey, prop);
    }

    @Override
    public void saveProp(String key, String prop, Object value) {
        String cacheKey = getCachePropKeys(key);
        template.opsForHash().put(cacheKey, prop, value);
        setExpire(cacheKey);
    }

    @Override
    public Object removeProp(String key, String prop) {
        String cacheKey = getCachePropKeys(key);
        Object value = template.opsForHash().get(cacheKey, prop);
        template.opsForHash().delete(cacheKey, prop);
        return value;
    }

    @Override
    public Map<String, Object> getProps(String key) {
        String cacheKey = getCachePropKeys(key);
        Map<Object, Object> entries = template.opsForHash().entries(cacheKey);
        HashMap<String, Object> map = new HashMap<>();
        if (entries.size() != 0) {
            entries.forEach((hashKey, value) -> map.put(hashKey.toString(), value));
        }
        return map;
    }

    @Override
    public void saveProps(String key, Map<String, Object> props) {
        String cacheKey = getCachePropKeys(key);
        template.opsForHash().putAll(cacheKey, props);
        setExpire(cacheKey);
    }


    private void setExpire(String key) {
        template.expire(key, ttlMin, TimeUnit.MINUTES);
    }

    public static RedisTemplate<String, Session> buildTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Session> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        template.setKeySerializer(redisSerializer);
        template.setHashKeySerializer(redisSerializer);
        Jackson2JsonRedisSerializer<Session> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        return template;
    }


}
