package com.cheese.shiro.common.manager.token;

/**
 * token管理器
 * 通用数据字段配置，如有需要自行继承
 *
 * @author sobann
 */
public abstract class AbstractTokenManager implements TokenManager {
    /**
     * token密钥
     */
    private String key;
    /**
     * token发放id
     */
    private String id;
    /**
     * 过期时间
     */
    private long expire;
    /**
     * token参数名称
     */
    private String tokenName;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    @Override
    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }


}
