package com.cheese.shiro.client.core.common.enums;

/**
 * cheese 权限客户端注册类型枚举
 *
 * @author sobann
 */
public enum RegisterTypeEnum {

    /**
     * HTTP配置
     */
    HTTP("http", "com.cheese.shiro.client.core.register.http.HttpClientRegisterConfiguration"),

    /**
     * RABBIT配置
     */
    RABBIT("rabbit", "com.cheese.shiro.client.core.register.rabbit.RabbitClientRegisterConfiguration"),

    /**
     * KAFKA配置
     */
    KAFKA("kafka", "com.cheese.shiro.client.core.register.kafka.KafkaClientRegisterConfiguration");


    RegisterTypeEnum(String type, String fullClassName) {
        this.type = type;
        this.fullClassName = fullClassName;
    }

    private final String type;
    private final String fullClassName;

    public String getFullClassName() {
        return this.fullClassName;
    }

    public String getType() {
        return this.type;
    }


    /**
     * 根据注册类型获取配置项
     *
     * @param type
     * @return
     */
    public static String parse(String type) {
        RegisterTypeEnum[] values = RegisterTypeEnum.values();
        for (RegisterTypeEnum registerTypeEnum : values) {
            if (registerTypeEnum.type.equals(type)) {
                return registerTypeEnum.fullClassName;
            }
        }
        throw new IllegalArgumentException("无法获取到符合条件的配置项:" + type);
    }


}
