package com.cheese.shiro.common.perm;

/**
 * 权限匹配
 * @author sobann
 */
public interface Permission {

    /**
     * 检查 ownIdentifier 是否包含 targetIdentifier
     * @param ownIdentifier 拥有权限
     * @param targetIdentifier 目标权限
     * @return
     */
    boolean implies(String ownIdentifier,String targetIdentifier);

    /**
     * 检查某一权限 能否对某实体进行操作
     * @param identifier 权限标识
     * @param entity 实体
     * @return
     */
    boolean impliesEntity(String identifier, String entity);
}
