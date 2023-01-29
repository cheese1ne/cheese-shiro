package com.cheese.shiro.server.gateway.handler.token;

import com.alibaba.fastjson.JSON;
import com.cheese.shiro.common.Context;
import com.cheese.shiro.common.domain.R;
import com.cheese.shiro.common.enums.Status;
import com.cheese.shiro.common.exception.TokenErrorException;
import com.cheese.shiro.common.exception.TokenExpiredException;
import com.cheese.shiro.common.manager.identity.IdentityManager;
import com.cheese.shiro.common.manager.token.TokenManager;
import com.cheese.shiro.common.manager.uri.UriManager;
import com.cheese.shiro.server.gateway.handler.Handler;
import com.cheese.shiro.server.gateway.props.CookieProps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于token的检查和发放
 * 前置：检查token，解析token中的identityContext,放入上下文，供后续使用或修改
 * 后置：发放token，将identityContext存入token
 * @Login order 200
 * @author sobann
 */
public abstract class TokenHandler extends Handler {

    private static final Logger logger = LoggerFactory.getLogger(TokenHandler.class);

    protected String unLoginContent = JSON.toJSONString(R.failed(Status.Unauth.getMsg()));

    protected String tokenExpiredContent = JSON.toJSONString(R.failed((Status.TokenExpired.getMsg())));

    protected String contentType = "application/json;charset=UTF-8";

    public void setUnLoginContent(String unLoginContent) {
        this.unLoginContent = unLoginContent;
    }

    public void setTokenExpiredContent(String tokenExpiredContent) {
        this.tokenExpiredContent = tokenExpiredContent;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    protected Boolean enableChecker;

    protected TokenManager tokenManager;

    protected UriManager uriManager;

    protected CookieProps cookieProps;


    public void setEnableChecker(Boolean enableChecker) {
        this.enableChecker = enableChecker;
    }

    public void setTokenManager(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    public void setUriManager(UriManager uriManager) {
        this.uriManager = uriManager;
    }

    public void setCookieProps(CookieProps cookieProps) {
        this.cookieProps = cookieProps;
    }

    @Override
    public int order() {
        return 200;
    }

    /**
     * 检查token
     *
     * @param context
     * @return
     */
    @Override
    public boolean preHandle(Context context) {
        String tokenName = tokenManager.getTokenName();
        String token = getToken(context, tokenName);
        String identityContext = null;

        //本次请求是否通过
        boolean isOk = false;

        boolean tokenIsError = false;
        boolean tokenIsExpired = false;
        boolean tokenIsOk = false;
        boolean tokenIsExist = false;

        //存在token 正式用户
        if (!StringUtils.isBlank(token)) {
            tokenIsExist = true;
            try {
                identityContext = tokenManager.parseToken(token);
                context.setIdentityContext(IdentityManager.getCoder().decode(identityContext));
                tokenIsOk = true;
            } catch (TokenExpiredException e) {
                tokenIsExpired = true;
            } catch (TokenErrorException e) {
                tokenIsError = true;
            } catch (Exception e) {
                logger.error("IdentityManager is Error ", e);
                return false;
            }
        }

        //token通过
        if (tokenIsOk) {
            //进行后续检查
            isOk = doWithTokenIsOk(token, context);
        }

        //未通过，进行其他凭证检查
        if (!isOk) {
            isOk = doWithOtherCredentials(context);
        }

        //仍然没有通过,进行处理
        if (!isOk) {
            //本次请求是否强制登陆，
            /*
                 通过上下文获取serviceId，将当前请求与添加了@Login的pattern列表进行对比，
                 若包含，则代表需要登录，当前线程存入对应的token错误信息
             */
            Boolean needLogin = isNeedLogin(context);
            //令牌出错
            if (tokenIsError) {
                isOk = doWithTokenIsError(context, needLogin);
                //令牌过期
            } else if (tokenIsExpired) {
                isOk = doWithTokenIsExpired(context, needLogin);
                //令牌不存在
            } else if (!tokenIsExist) {
                isOk = doWithTokenNotExist(context, needLogin);
            }
        }
        return isOk;
    }

    /**
     * 根据最新 identityContext
     * 发放新token
     *
     * @param context
     */
    @Override
    public void postHandle(Context context) {
        Object identityContext = context.getIdentityContext();
        //身份获取成功，发放新的token
        if (identityContext != null) {
            try {
                String newToken = tokenManager.createNewToken(IdentityManager.getCoder().encode(identityContext));
                setToken(context, tokenManager.getTokenName(), newToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean doWithTokenNotExist(Context context, Boolean needLogin) {
        if (needLogin) {
            context.setErrorContent(unLoginContent);
            context.setErrorContentType(contentType);
        }
        return !needLogin;
    }

    public boolean doWithTokenIsExpired(Context context, Boolean needLogin) {
        if (needLogin) {
            if (needLogin) {
                context.setErrorContent(tokenExpiredContent);
                context.setErrorContentType(contentType);
            }
        }
        return !needLogin;
    }

    public boolean doWithTokenIsError(Context context, Boolean needLogin) {
        if (needLogin) {
            context.setErrorContent(unLoginContent);
            context.setErrorContentType(contentType);
        }
        return !needLogin;
    }

    /**
     * 判断当前请求是否需要登陆
     *
     * @param context
     * @return
     */
    public Boolean isNeedLogin(Context context) {
        return uriManager.isMatch(context);
    }

    public String getToken(Context context, String tokenName) {
        //首先从请求头中获取
        String token = null;
        token = context.getRequestHeader(tokenName);

        //从请求参数中获取
        if (StringUtils.isBlank(token) || "null".equalsIgnoreCase(token)) {
            token = context.getRequestParam(tokenName);
        }
        //最后从cooike中获取
        if (StringUtils.isBlank(token) || "null".equalsIgnoreCase(token)) {
            token = context.getRequestCookie(tokenName);
        }
        return token;
    }

    /**
     * 将token给予response
     *
     * @param context
     * @param tokenName
     * @param newToken
     */
    public void setToken(Context context, String tokenName, String newToken) {
        context.addResponseHeader(tokenName, newToken);
        if (cookieProps.isEnable()) {
            context.addCookie(tokenName, newToken, cookieProps.getDomain(), cookieProps.getPath());
        }
    }

    /**
     * 使用其他信息鉴定身份
     *
     * @param context
     * @return
     */
    public abstract boolean doWithOtherCredentials(Context context);

    /**
     * token检查通过时，进行其他检查
     *
     * @param token
     * @param context
     * @return
     */
    public abstract boolean doWithTokenIsOk(String token, Context context);

}
