package com.cheese.shiro.common.manager.token;


import com.cheese.shiro.common.exception.TokenErrorException;
import com.cheese.shiro.common.exception.TokenExpiredException;

/**
 * 令牌管理器
 * 方法定义
 * @author sobann
 */
public interface TokenManager {

    /**
     * 获取token名称
     * @return
     */
    String getTokenName();

    /**
     * 创建新token,默认有效期
     * @param context
     * @return
     */
    String createNewToken(String context);

    /**
     * 解析token ,获取context
     * @param token
     * @return
     * @throws TokenExpiredException
     * @throws TokenErrorException
     */
    String parseToken(String token) throws TokenExpiredException, TokenErrorException;

    /**
     * 创建token,指定时间
     * @param context
     * @param mins
     * @return
     */
    String createNewToken(String context, long mins);
}

