package com.cheese.shiro.server.gateway.handler.log;


import com.cheese.shiro.common.Context;
import com.cheese.shiro.common.manager.uri.UriManager;
import com.cheese.shiro.common.manager.uri.entity.GatewayLogUriMapping;
import com.cheese.shiro.server.gateway.handler.Handler;
import com.cheese.shiro.server.gateway.log.LogWriter;

import java.util.Date;

/**
 * 抽象的日志处理器 通用记录请求到达链条的开始时间
 * order 50
 * @author sobann
 */
public abstract class AbstractLogHandler extends Handler {

    public static final String START_TIME = "LogStartTime";

    protected LogWriter logWriter;

    protected UriManager<GatewayLogUriMapping> uriManager;

    protected String gatewayNode;

    public void setGatewayNode(String gatewayNode) {
        this.gatewayNode = gatewayNode;
    }

    public void setUriManager(UriManager<GatewayLogUriMapping> uriManager) {
        this.uriManager = uriManager;
    }

    public void setLogWriter(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    @Override
    public boolean preHandle(Context context) {
        context.setAttribute(START_TIME, new Date());
        return true;
    }

    @Override
    public int order() {
        return 50;
    }

}
