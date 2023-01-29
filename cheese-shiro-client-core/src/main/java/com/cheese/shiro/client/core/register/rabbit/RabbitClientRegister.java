package com.cheese.shiro.client.core.register.rabbit;

import com.cheese.shiro.client.core.register.ClientRegister;
import com.cheese.shiro.common.config.ServerConfig;
import com.cheese.shiro.common.constant.QueuePool;
import com.cheese.shiro.common.domain.ClientInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 使用rabbitmq进行服务注册和配置同步
 *
 * @author sobann
 */
@RabbitListener(bindings = @QueueBinding(value = @Queue(value = QueuePool.CONFIG_INFO_CLIENT + "." + "${spring.application.name}" + "." + "${spring.cloud.client.ip-address}" + "." + "${server.port}", autoDelete = "true"), exchange = @Exchange(value = QueuePool.CONFIG_INFO_CLIENT_FANOUT, type = ExchangeTypes.FANOUT)))
public class RabbitClientRegister extends ClientRegister {
    private static final Logger logger = LoggerFactory.getLogger(RabbitClientRegister.class);

    private RabbitTemplate rabbitTemplate;

    public RabbitClientRegister(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void registerToServer(ClientInstance clientInstance) {
        String message = getCoder().encode(clientInstance);
        rabbitTemplate.convertAndSend(QueuePool.CONFIG_INFO_SERVER_FANOUT, "", message);
        logger.info("Register To Server:{}", message);
    }

    @RabbitHandler
    public void onMessage(String json) {
        logger.info("Get ServerConfig From Server :{}", json);
        ServerConfig serverConfig = (ServerConfig) getCoder().decode(json);
        refreshConfigWithServer(serverConfig);
    }

    @Override
    public void refreshConfigWithServer(ServerConfig serverConfig) {
        //刷新客户端配置
        syncConfigWithServer(serverConfig.getShiroConfig());
        //如果服务端为刚启动，重新进行注册
        if (serverConfig.isStartUp()) {
            registerToServer(false);
        }
    }
}
