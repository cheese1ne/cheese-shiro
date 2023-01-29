package com.cheese.shiro.server.core.authorization;


import com.cheese.shiro.common.service.entity.RulePerm;

import java.util.Collection;

/**
 * 用户授权顶层接口
 * @author sobann
 */
public interface Realm {
    /**
     * 获取用户的权限规则
     * @param identity 用户id
     * @param app 服务标识
     * @return
     */
    Collection<? extends RulePerm> getPermByIdentity(String identity, String app);
}
