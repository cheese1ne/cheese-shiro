package com.cheese.shiro.server.core.manager.identifier;

import com.cheese.shiro.common.perm.Permission;
import com.cheese.shiro.common.service.entity.RulePerm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 使用shiro WildcardPermission 逻辑代码
 *
 * @author sobann
 */
public class ShiroIdentifierManager extends IdentifierManager {

    public ShiroIdentifierManager() {
    }

    public ShiroIdentifierManager(Permission permission) {
        super(permission);
    }

    @Override
    public Set<String> getImplyRules(Collection<? extends RulePerm> perms, String identifier) {
        if (perms == null || perms.size() == 0) {
            return new HashSet<>();
        }
        //通过标识符identifier过滤出符合条件的RulePerm，取出rule规则
        return perms.stream().filter(
                perm -> perm.isValid() && implies(perm.getIdentifier(), identifier)
        ).map(RulePerm::getRule).collect(Collectors.toSet());
    }

    @Override
    public String getOperateScope(String identifier) {
        String[] split = identifier.split(PART_DIVIDER_TOKEN);
        return split.length > 2 ? split[2] : split[0];
    }

    @Override
    public String createIdentifier(String entity, String actions) {
        return entity + PART_DIVIDER_TOKEN + actions;
    }

    @Override
    public String createIdentifier(String entity, String actions, String scope) {
        return entity + PART_DIVIDER_TOKEN + actions + PART_DIVIDER_TOKEN + scope;
    }

}
