package com.cheese.shiro.client.core.register.rabbit;

import com.cheese.shiro.client.core.props.RegisterProp;
import com.cheese.shiro.client.core.register.ClientRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitmq注册器配置类
 * @author sobann
 */
@Configuration
public class RabbitClientRegisterConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(RabbitClientRegisterConfiguration.class);

    /**
     * @param rabbitTemplate
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public ClientRegister clientRegister(RabbitTemplate rabbitTemplate,
                                         RegisterProp registerProp) {
        RabbitClientRegister register = new RabbitClientRegister(rabbitTemplate);
        register.setService(registerProp.getService());
        logger.info("prepare to initialize RabbitClientRegister");
        return register;
    }
}
