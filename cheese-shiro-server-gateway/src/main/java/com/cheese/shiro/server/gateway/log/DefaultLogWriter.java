package com.cheese.shiro.server.gateway.log;

import com.alibaba.fastjson.JSON;
import com.cheese.shiro.common.domain.GatewayLogContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 网关日志默认输出方式：通过Spring自带的日志记录方式进行输出
 * @author sobann
 */
public class DefaultLogWriter implements LogWriter {
    private static final Logger logger = LoggerFactory.getLogger(DefaultLogWriter.class);
    @Override
    public void write(GatewayLogContent gatewayLogContent) {
        logger.info("GatewayLog : {}",JSON.toJSONString(gatewayLogContent));
    }
}
