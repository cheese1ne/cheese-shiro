package com.cheese.shiro.server.gateway.log;


import com.cheese.shiro.common.domain.GatewayLogContent;

/**
 * 网关日志输出 默认实现方式为 DefaultLogWriter
 * 可根据kafka或es自行进行实现
 * @author sobann
 */
public interface LogWriter {
    /**
     * 日志写入
     * @param gatewayLogContent
     */
    void write(GatewayLogContent gatewayLogContent);
}
