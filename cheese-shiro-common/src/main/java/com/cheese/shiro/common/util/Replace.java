package com.cheese.shiro.common.util;

/**
 * 字段替换结构
 * rule规则字段，替换为es对应字段
 * @author sobann
 */
public interface Replace {
    /**
     * 获取es对应字段
     * @param key
     * @return
     */
    String get(String key);
}
