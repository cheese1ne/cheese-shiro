package com.cheese.shiro.server.gateway.register.kafka;

import com.cheese.shiro.common.config.ServerConfig;
import com.cheese.shiro.common.constant.QueuePool;
import com.cheese.shiro.common.domain.ClientInstance;
import com.cheese.shiro.server.gateway.register.ServerRegisterListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 服务注册器单个实例的KAFKA实现
 *
 * @author sobann
 */
public class KafkaServerRegisterListener extends ServerRegisterListener {

    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaServerRegisterListener(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void syncConfigToClient(ServerConfig shiroConfig) {
        String json = getCoder().encode(shiroConfig);
        kafkaTemplate.send(QueuePool.CONFIG_INFO_CLIENT_FANOUT, json);
    }

    @Override
    public void register(ClientInstance clientInstance) {
        registerInstance(clientInstance);
        if (clientInstance.isStartUp()) {
            syncConfigToClient(false);
        }
    }


    @KafkaListener(topics = QueuePool.CONFIG_INFO_SERVER_FANOUT, id = QueuePool.CONFIG_INFO_SERVER + "." + "${spring.cloud.client.ip-address}" + "." + "${server.port}", containerFactory = "shiroContainerFactory")
    public void onMessage(String message) {
        ClientInstance clientInstance = (ClientInstance) getCoder().decode(message);
        register(clientInstance);
    }
}
