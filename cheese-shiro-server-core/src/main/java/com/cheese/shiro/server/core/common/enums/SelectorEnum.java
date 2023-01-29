package com.cheese.shiro.server.core.common.enums;

/**
 * 数据转换选择器枚举
 *
 * @author sobann
 */
public enum SelectorEnum {

    /**
     * 默认数据转换器选择器
     */
    DEFAULT("default", "com.cheese.shiro.server.core.sql.DefaultSelectorAutoConfiguration"),

    /**
     * 多数据源数据转换器选择器
     */
    MULTI_DRUID("multiDruid", "com.cheese.shiro.server.core.sql.MultiDruidDataSourceSelectorAutoConfiguration");


    SelectorEnum(String type, String fullClassName) {
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
        SelectorEnum[] values = SelectorEnum.values();
        for (SelectorEnum selectorEnum : values) {
            if (selectorEnum.type.equals(type)) {
                return selectorEnum.getFullClassName();
            }
        }
        throw new IllegalArgumentException("未知的selector类型：" + type);
    }

}
