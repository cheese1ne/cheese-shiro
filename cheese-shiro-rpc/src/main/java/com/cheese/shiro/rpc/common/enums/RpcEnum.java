package com.cheese.shiro.rpc.common.enums;

/**
 * cheese rpc枚举
 *
 * @author sobann
 */
public enum RpcEnum {

    /**
     * feign
     */
    FEIGN("feign","com.cheese.shiro.rpc.feign.ShiroFeignAutoConfiguration"),
    /**
     * dubbo
     */
    DUBBO("dubbo","com.cheese.shiro.rpc.dubbo.ShiroDubboAutoConfiguration"),
    /**
     * mock
     */
    MOCK("mock","com.cheese.shiro.rpc.mock.ShiroMockAutoConfiguration");

    RpcEnum(String type, String fullClassName) {
        this.type = type;
        this.fullClassName = fullClassName;
    }

    private final String type;
    private final String fullClassName;

    public String getFullClassName() {
        return this.fullClassName;
    }

    public String getType(){
        return this.type;
    }

    /**
     * 根据rpc类型获取全类名
     *
     * @param type
     * @return
     */
    public static String parseType(String type) {
        RpcEnum[] values = RpcEnum.values();
        for (RpcEnum rpcEnum : values) {
            if (rpcEnum.type.equals(type)) {
                return rpcEnum.getFullClassName();
            }
        }
        throw new IllegalArgumentException("未知的rpc类型：" + type);
    }

}
