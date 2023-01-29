package com.cheese.shiro.common;

/**
 * 顺序处理接口
 *
 * @author sobann
 */
public interface Order {
    /**
     * 用于定义方法处理顺序
     *
     * @return
     */
    int order();
}
