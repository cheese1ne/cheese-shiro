package com.cheese.shiro.server.gateway.register.kafka;

import com.cheese.shiro.server.gateway.props.IdentityProps;
import com.cheese.shiro.server.gateway.props.TokenProps;
import com.cheese.shiro.server.gateway.register.ServerRegisterManager;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * KAFKA的服务注册管理器配置类
 * @author sobann
 */
@Configuration
public class KafkaServerRegisterManagerConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(KafkaServerRegisterManagerConfiguration.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String KafkaBootStrapServers;
    /**
     * 选择使用Kafka
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ServerRegisterManager serverConfigListener(TokenProps tokenProps, IdentityProps identityProps){
        KafkaServerRegisterListener listener = new KafkaServerRegisterListener(createTemplate());
        listener.setIdentityProps(identityProps);
        listener.setTokenProps(tokenProps);
        logger.info("prepare to initialize KafkaServerRegisterListener");
        return listener;
    }



    /**
     * 监听器默认工厂
     * @return
     */
    @Bean("shiroContainerFactory")
    public KafkaListenerContainerFactory consumerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        Map<String, Object> consumerProps = consumerProps();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory(consumerProps));
        logger.info("prepare to initialize KafkaListenerContainerFactory");
        return factory;
    }

    /**
     * shiro消息通讯，自动提交，只消费最新消息
     * @return
     */
    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaBootStrapServers);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    private KafkaTemplate<String, String> createTemplate() {
        Map<String, Object> senderProps = senderProps();
        ProducerFactory<String, String> pf = new DefaultKafkaProducerFactory(senderProps);
        KafkaTemplate<String, String> template = new KafkaTemplate<>(pf);
        return template;
    }

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaBootStrapServers);
        props.put(ProducerConfig.ACKS_CONFIG,"1");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

}
