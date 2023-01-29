package com.cheese.shiro.common.manager.token;

import com.cheese.shiro.common.component.SyncComponent;
import com.cheese.shiro.common.config.ShiroConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JsonWebToken实现
 * token配置采用与gateway同步
 * @author sobann
 */
public class JwtSyncTokenManager extends JwtTokenManager implements SyncComponent {
    private static final Logger logger = LoggerFactory.getLogger(JwtSyncTokenManager.class);
    @Override

    public void sync(ShiroConfig shiroConfig) {
        String id = getId();
        if(StringUtils.isBlank(id) || !id.equals(shiroConfig.getTokenId())){
            setId(shiroConfig.getTokenId());
            logger.info("Sync TokenManager TokenId  :{}",shiroConfig.getTokenId());
        }
        String key = getKey();
        if(StringUtils.isBlank(key) || !key.equals(shiroConfig.getTokenKey())){
            setKey(shiroConfig.getTokenKey());
            logger.info("Sync TokenManager TokenKey  :{}",shiroConfig.getTokenKey());
        }
        String tokenName = getTokenName();
        if(StringUtils.isBlank(tokenName) || !tokenName.equals(shiroConfig.getTokenName())){
            setTokenName(shiroConfig.getTokenName());
            logger.info("Sync TokenManager tokenName  :{}",shiroConfig.getTokenName());
        }
        long expireMins = getExpire();
        if(expireMins != shiroConfig.getExpire()){
            setExpire(shiroConfig.getExpire());
            logger.info("Sync TokenManager expireMins  :{}",shiroConfig.getExpire());
        }
    }
}
