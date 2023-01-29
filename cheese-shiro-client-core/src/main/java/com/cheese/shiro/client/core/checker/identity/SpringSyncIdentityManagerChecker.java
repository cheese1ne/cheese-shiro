package com.cheese.shiro.client.core.checker.identity;

import com.cheese.shiro.common.component.SyncComponent;
import com.cheese.shiro.common.config.ShiroConfig;
import com.cheese.shiro.common.manager.identity.IdentityManager;
import org.apache.commons.lang.StringUtils;

/**
 * 配置身份管理器信息同步
 *
 * @author sobann
 */
public class SpringSyncIdentityManagerChecker extends SpringIdentityManagerChecker implements SyncComponent {
    @Override
    public void sync(ShiroConfig shiroConfig) {
        String tracerName = null;
        String defaultIdentity = null;
        try {
            tracerName = IdentityManager.getContextTracerName();
            defaultIdentity = IdentityManager.getDefaultIdentity();
        } catch (Exception e) {
            IdentityManager.configure(shiroConfig.getContextTracerName(), shiroConfig.getDefaultIdentity());
            return;
        }
        if (StringUtils.isBlank(tracerName) || !tracerName.equals(shiroConfig.getContextTracerName()) || StringUtils.isBlank(defaultIdentity) || !defaultIdentity.equals(shiroConfig.getDefaultIdentity())) {
            IdentityManager.configure(shiroConfig.getContextTracerName(), shiroConfig.getDefaultIdentity());
        }
    }
}
