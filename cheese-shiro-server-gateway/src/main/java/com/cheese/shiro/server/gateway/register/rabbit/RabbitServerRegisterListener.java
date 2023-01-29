package com.cheese.shiro.server.gateway.register.rabbit;

import com.cheese.shiro.common.config.ServerConfig;
import com.cheese.shiro.common.constant.QueuePool;
import com.cheese.shiro.common.domain.ClientInstance;
import com.cheese.shiro.server.gateway.register.ServerRegisterListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 服务注册器单个实例的Rabbit实现
 *
 * @author sobann
 */
@RabbitListener(bindings = @QueueBinding(value = @Queue(value = QueuePool.CONFIG_INFO_SERVER + "." + "${spring.application.name}" + "." + "${spring.cloud.client.ip-address}" + "." + "${server.port}", autoDelete = "true"), exchange = @Exchange(value = QueuePool.CONFIG_INFO_SERVER_FANOUT, type = ExchangeTypes.FANOUT)))
public class RabbitServerRegisterListener extends ServerRegisterListener {
    private static final Logger logger = LoggerFactory.getLogger(RabbitServerRegisterListener.class);
    private RabbitTemplate rabbitTemplate;

    public RabbitServerRegisterListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    public void syncConfigToClient(ServerConfig shiroConfig) {
        String json = getCoder().encode(shiroConfig);
        rabbitTemplate.convertAndSend(QueuePool.CONFIG_INFO_CLIENT_FANOUT, "", json);
    }

    @Override
    public void register(ClientInstance clientInstance) {
        registerInstance(clientInstance);
        if (clientInstance.isStartUp()) {
            syncConfigToClient(false);
        }
    }

    @RabbitHandler
    public void onMessage(String message) {
        ClientInstance clientInstance = (ClientInstance) getCoder().decode(message);
        register(clientInstance);
    }


}
