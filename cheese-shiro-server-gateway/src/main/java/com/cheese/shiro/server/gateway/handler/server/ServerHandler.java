package com.cheese.shiro.server.gateway.handler.server;

import com.alibaba.fastjson.JSON;
import com.cheese.shiro.common.Context;
import com.cheese.shiro.common.domain.R;
import com.cheese.shiro.common.enums.Status;
import com.cheese.shiro.common.manager.uri.UriManager;
import com.cheese.shiro.server.gateway.handler.Handler;
import org.apache.commons.lang.StringUtils;

/**
 * 服务间远程调用时使用，用于检验发起调用方服务器的身份
 * 从request中获取检验参数，shiroServerHeader中获取头部信息，判断是否为shiroServerIdentity
 * 对@ServerUri 检查
 * order 100
 * @author sobann
 */
public class ServerHandler extends Handler {

    /**
     * 服务间远程调用头部信息
     */
    private String shiroServerHeader;
    /**
     * 服务间远程调用的校验值
     */
    private String shiroServerIdentity;

    protected UriManager uriManager;

    protected String contentType = "application/json;charset=UTF-8";

    protected String forbiddenContent =  JSON.toJSONString(R.failed(Status.Forbidden.getMsg()));

    public void setForbiddenContent(String forbiddenContent) {
        this.forbiddenContent = forbiddenContent;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setUriManager(UriManager uriManager) {
        this.uriManager = uriManager;
    }

    public String getShiroServerIdentity() {
        return shiroServerIdentity;
    }

    public void setShiroServerIdentity(String shiroServerIdentity) {
        this.shiroServerIdentity = shiroServerIdentity;
    }

    public String getShiroServerHeader() {
        return shiroServerHeader;
    }

    public void setShiroServerHeader(String shiroServerHeader) {
        this.shiroServerHeader = shiroServerHeader;
    }

    public Boolean isServerRequest(String currentServerIndentity){
        if(StringUtils.isBlank(currentServerIndentity)){
            return false;
        }
        if(currentServerIndentity.equalsIgnoreCase(getShiroServerIdentity())){
            return  true;
        }else{
            return false;
        }
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public boolean preHandle(Context context) {
        boolean forServer = uriManager.isMatch(context);
        if(!forServer){
            return true;
        }
        String requestHeader = context.getRequestHeader(shiroServerHeader);
        Boolean serverRequest = isServerRequest(requestHeader);
        if(!serverRequest){
            context.setErrorContent(forbiddenContent);
            context.setErrorContentType(contentType);
        }
        return serverRequest;
    }

}
