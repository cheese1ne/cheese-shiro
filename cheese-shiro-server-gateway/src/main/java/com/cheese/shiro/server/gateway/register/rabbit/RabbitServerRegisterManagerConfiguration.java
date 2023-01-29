package com.cheese.shiro.server.gateway.register.rabbit;

import com.cheese.shiro.server.gateway.props.IdentityProps;
import com.cheese.shiro.server.gateway.props.TokenProps;
import com.cheese.shiro.server.gateway.register.ServerRegisterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class RabbitServerRegisterManagerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RabbitServerRegisterManagerConfiguration.class);

    /**
     * 选择使用rabbitMQ
     * @param rabbitTemplate
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ServerRegisterManager serverConfigListener(TokenProps tokenProps, IdentityProps identityProps, RabbitTemplate rabbitTemplate){
        RabbitServerRegisterListener listener = new RabbitServerRegisterListener(rabbitTemplate);
        listener.setIdentityProps(identityProps);
        listener.setTokenProps(tokenProps);
        logger.info("prepare to initialize RabbitServerConfigListener");
        return listener;
    }
}
