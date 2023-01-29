package com.cheese.shiro.client.core.checker.identity;

import com.cheese.shiro.common.manager.identity.IdentityManagerChecker;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PreDestroy;

/**
 * bean初始化后,开始加载检查任务
 * 检查身份配置项是否完整
 *
 * @author sobann
 */
public class SpringIdentityManagerChecker extends IdentityManagerChecker implements InitializingBean {

    public SpringIdentityManagerChecker() {
    }

    public SpringIdentityManagerChecker(int threadNum, int interval) {
        super(threadNum, interval);
    }

    @PreDestroy
    @Override
    public void close() {
        super.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.start();
    }
}
