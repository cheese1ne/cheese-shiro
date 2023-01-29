package com.cheese.shiro.client.core.register.kafka;

import com.cheese.shiro.client.core.register.ClientRegister;
import com.cheese.shiro.common.config.ServerConfig;
import com.cheese.shiro.common.constant.QueuePool;
import com.cheese.shiro.common.domain.ClientInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 使用kafka进行注册和配置同步
 *
 * @author sobann
 */
public class KafkaClientRegister extends ClientRegister {
    private static final Logger logger = LoggerFactory.getLogger(KafkaClientRegister.class);

    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaClientRegister(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void registerToServer(ClientInstance clientInstance) {
        String message = getCoder().encode(clientInstance);
        kafkaTemplate.send(QueuePool.CONFIG_INFO_SERVER_FANOUT, message);
        logger.info("Register To Server:{}", message);
    }

    @KafkaListener(topics = QueuePool.CONFIG_INFO_CLIENT_FANOUT,
            id = QueuePool.CONFIG_INFO_CLIENT + "." + "${spring.cloud.client.ip-address}" + "." + "${server.port}",
            containerFactory = "shiroContainerFactory")
    public void onMessage(String json) {
        logger.info("Get ServerConfig From Server :{}", json);
        ServerConfig serverConfig = (ServerConfig) getCoder().decode(json);
        refreshConfigWithServer(serverConfig);
    }

    @Override
    public void refreshConfigWithServer(ServerConfig serverConfig) {
        //刷新客户端配置
        syncConfigWithServer(serverConfig.getShiroConfig());
        //如果服务端为启动状态，进行注册
        if (serverConfig.isStartUp()) {
            registerToServer(false);
        }
    }
}
