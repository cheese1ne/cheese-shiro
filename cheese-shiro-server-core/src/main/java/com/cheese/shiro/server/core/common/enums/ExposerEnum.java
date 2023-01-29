package com.cheese.shiro.server.core.common.enums;

/**
 * 鉴权服务暴露方式枚举
 *
 * @author sobann
 */
public enum ExposerEnum {

    /**
     * DUBBO SOA服务暴露
     */
    DUBBO("dubbo", "com.cheese.shiro.server.core.expore.dubbo.DubboResourceAutoConfiguration"),
    /**
     * HTTP 接口暴露(springMvc)
     */
    MVC("mvc", "com.cheese.shiro.server.core.expore.mvc.SpringMvcResourceAutoConfiguration"),
    /**
     * HTTP 接口暴露(jersey)
     */
    JERSEY("jersey", "com.cheese.shiro.server.core.expore.jersey.JerseyResourceAutoConfiguration");


    ExposerEnum(String type, String fullClassName) {
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
     * 根据selector类型获取全类名
     *
     * @param type
     * @return
     */
    public static String parseType(String type) {
        ExposerEnum[] values = ExposerEnum.values();
        for (ExposerEnum selectorEnum : values) {
            if (selectorEnum.type.equals(type)) {
                return selectorEnum.getFullClassName();
            }
        }
        throw new IllegalArgumentException("未知的selector类型：" + type);
    }

}
