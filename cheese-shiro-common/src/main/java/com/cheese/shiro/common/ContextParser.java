package com.cheese.shiro.common;

/**
 * context 对象解析器
 * identity : 用户权限校验使用的 全局唯一值
 * primary  : 业务对应的用户唯一值
 * subject  : 用户主体对象，长期不变值，name,email,telephone等，
 *
 * @author sobann
 */
public interface ContextParser {
    /**
     * 从 context 对象中获取对应业务用户id
     *
     * @param context
     * @return
     */
    Object getPrimary(Object context);

    /**
     * 从 context中获取 identity 权限校验使用,全局唯一
     *
     * @param context
     * @return
     */
    String getIdentity(Object context);

    /**
     * 从 context 中获取 业务用户总体 如 userInfo对象，须保证user对象中均为长期不变值
     *
     * @param context
     * @return
     */
    Object getSubject(Object context);
}
