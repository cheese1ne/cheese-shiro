package com.cheese.shiro.server.gateway.common.enums;

/**
 * cheese gateway 注册方式枚举
 *
 * @author sobann
 */
public enum RegisterTypeEnum {

    /**
     * HTTP方式注册，基于jersey暴露注册相关端口
     */
    HTTP("http", "com.cheese.shiro.server.gateway.register.http.HttpServerRegisterManagerConfiguration"),

    /**
     * KAFKA方式注册，基于消息队列的方式异步注册
     */
    KAFKA("kafka", "com.cheese.shiro.server.gateway.register.kafka.KafkaServerRegisterManagerConfiguration"),

    /**
     * RABBIT方式注册，基于消息队列的方式异步注册
     */
    RABBIT("rabbit", "com.cheese.shiro.server.gateway.register.rabbit.RabbitServerRegisterManagerConfiguration");


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
     * 根据网关注册方式获取配置项全类名
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
        throw new IllegalArgumentException("不合法的网关注册方式:" + type);
    }

}
