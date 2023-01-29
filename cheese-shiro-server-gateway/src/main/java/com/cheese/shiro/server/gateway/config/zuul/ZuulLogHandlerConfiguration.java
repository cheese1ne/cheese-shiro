package com.cheese.shiro.server.gateway.config.zuul;

import com.cheese.shiro.common.anno.GatewayLog;
import com.cheese.shiro.common.manager.uri.entity.GatewayLogUriMapping;
import com.cheese.shiro.server.gateway.handler.log.ZuulLogHandler;
import com.cheese.shiro.server.gateway.log.DefaultLogWriter;
import com.cheese.shiro.server.gateway.log.LogWriter;
import com.cheese.shiro.server.gateway.register.ServerRegisterManager;
import com.cheese.shiro.server.gateway.uri.ServerUriManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * zuul网关日志处理器配置
 * @author sobann
 */
@Configuration
public class ZuulLogHandlerConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ZuulLogHandlerConfiguration.class);
    @Value("${spring.cloud.client.ip-address}")
    private String ip;
    @Value("${server.port}")
    private String port;

    /************************配置 @GatewayLog 管理器******************************************/
    @Bean
    @ConditionalOnMissingBean
    public LogWriter logWriter(){
        logger.info("prepare to initialize defaultLogWriter");
        return new DefaultLogWriter();
    }


    @Bean
    @ConditionalOnMissingBean
    public ZuulLogHandler zuulLogHandler(ServerRegisterManager serverRegisterManager, LogWriter logWriter){
        ZuulLogHandler zuulLogHandler = new ZuulLogHandler();
        zuulLogHandler.setGatewayNode(ip+":"+port);
        zuulLogHandler.setUriManager(new ServerUriManager<GatewayLogUriMapping>(serverRegisterManager, GatewayLog.REGISTER_KEY));
        zuulLogHandler.setLogWriter(logWriter);
        return zuulLogHandler;
    }
}
