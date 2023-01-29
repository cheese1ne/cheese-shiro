package com.cheese.shiro.server.core.manager.identifier;


import com.cheese.shiro.common.perm.Permission;
import com.cheese.shiro.common.service.entity.RulePerm;

import java.util.Collection;
import java.util.Set;

/**
 * 权限标识符管理器
 *
 * @author sobann
 */
public abstract class IdentifierManager {

    protected static final String PART_DIVIDER_TOKEN = ":";

    protected Permission permission;

    public IdentifierManager(Permission permission){
        this.permission = permission;
    }

    public IdentifierManager(){
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }

    /**
     * 检查 ownIdentifier 是否包含 targetIdentifier
     * 所有有关权限限制的操作最终都会到这个方法里面进行判断来告知shiro用户到底有没有对应的权限
     * @param ownIdentifier 拥有权限
     * @param targetIdentifier 目标权限
     * @return
     */
    public boolean implies(String ownIdentifier, String targetIdentifier){
        return permission.implies(ownIdentifier,targetIdentifier);
    }

    /**
     * identifier 是否包含 entity
     *
     * @param identifier
     * @param entity
     * @return
     */
    public boolean impliesEntity(String identifier, String entity){
        return permission.impliesEntity(identifier,entity);
    }


    /**
     * 检查权限列表中包含目标权限的rule规则
     * 一般情况是关于角色的rule规则，例：project:get,update
     * 也有可能为临时授权数据的权限，例：hash:project:self:数据id,数据id
     * @param perms
     * @param identifier
     * @return
     */
    public abstract Set<String> getImplyRules(Collection<? extends RulePerm> perms, String identifier);


    /**
     * identifier 结构，返回scope ，若无scope则返回entity
     * entity:grant:scope
     * entity:update
     * @param identifier
     * @return
     */
    public abstract String getOperateScope(String identifier);

    /**
     * 创建权限标识符
     * @param entity
     * @param action
     * @return
     */
    public abstract String createIdentifier(String entity,String action);

    /**
     * 创建权限标识符
     * @param entity
     * @param actions
     * @param scope
     * @return
     */
    public abstract String createIdentifier(String entity, String actions, String scope);
}
