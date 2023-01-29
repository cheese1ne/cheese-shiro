package com.cheese.shiro.session.manager;

import com.cheese.shiro.common.component.SyncComponent;
import com.cheese.shiro.common.config.ShiroConfig;
import com.cheese.shiro.common.manager.session.Session;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RedisSessionStoreManager的子类
 * 实现与网关同步Session的功能
 * @author sobann
 */
public class RedisSyncSessionStoreManager extends RedisSessionStoreManager implements SyncComponent {

    public RedisSyncSessionStoreManager(RedisConnectionFactory redisConnectionFactory) {
        super(redisConnectionFactory);
    }

    public RedisSyncSessionStoreManager(RedisTemplate<String, Session> template) {
        super(template);
    }

    public RedisSyncSessionStoreManager(String preFix, RedisConnectionFactory redisConnectionFactory) {
        super(preFix, redisConnectionFactory);
    }

    public RedisSyncSessionStoreManager(String preFix, RedisTemplate<String, Session> template) {
        super(preFix, template);
    }

    @Override
    public void sync(ShiroConfig shiroConfig) {
        long ttl = getExpire();
        long expire = shiroConfig.getExpire();
        if(ttl != expire){
            setExpire(expire);
            logger.info("sync session expire by RedisSyncSessionStoreManager");
        }
    }
}
