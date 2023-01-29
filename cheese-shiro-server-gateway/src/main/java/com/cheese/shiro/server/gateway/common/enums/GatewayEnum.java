package com.cheese.shiro.server.gateway.common.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * cheese gateway枚举
 * todo:gateway网关完成后，完善此处配置
 *
 * @author sobann
 */
public enum GatewayEnum {

    /**
     * zuul assign配置
     */
    ZUUL_ASSIGN("zuul", "assign", "com.cheese.shiro.server.gateway.config.zuul.ZuulAssignHandlerConfiguration"),

    /**
     * zuul filter配置
     */
    ZUUL_FILTER("zuul", "filter", "com.cheese.shiro.server.gateway.config.zuul.ShiroZuulFilterConfiguration"),

    /**
     * zuul log配置
     */
    ZUUL_LOG("zuul", "log", "com.cheese.shiro.server.gateway.config.zuul.ZuulLogHandlerConfiguration"),
    /**
     * gateway assign配置
     */
    GATEWAY_FILTER("gateway", "assign", ""),

    /**
     * gateway filter配置
     */
    GATEWAY_ASSIGN("gateway", "filter", ""),

    /**
     * gateway log配置
     */
    GATEWAY_LOG("gateway", "log", "");


    GatewayEnum(String type, String part, String fullClassName) {
        this.type = type;
        this.part = part;
        this.fullClassName = fullClassName;
    }

    private final String part;
    private final String type;
    private final String fullClassName;

    public String getFullClassName() {
        return this.fullClassName;
    }

    public String getType() {
        return this.type;
    }

    public String getPart() {
        return this.part;
    }

    /**
     * 根据网关类型及组成获取符合条件的枚举
     *
     * @param type
     * @return
     */
    public static GatewayEnum parse(String type, String part) {
        GatewayEnum[] values = GatewayEnum.values();
        for (GatewayEnum gatewayEnum : values) {
            if (gatewayEnum.getType().equals(type) && gatewayEnum.getPart().equals(part)) {
                return gatewayEnum;
            }
        }
        throw new IllegalArgumentException("无法获取到符合条件的枚举，网关类型:" + type + ",组成类型:" + part);
    }

    /**
     * 根据网关类型获取全部配置项
     *
     * @param type
     * @return
     */
    public static List<GatewayEnum> parseEnumList(String type) {
        return Arrays.stream(GatewayEnum.values()).filter(item -> item.getType().equals(type)).collect(Collectors.toList());
    }

}
