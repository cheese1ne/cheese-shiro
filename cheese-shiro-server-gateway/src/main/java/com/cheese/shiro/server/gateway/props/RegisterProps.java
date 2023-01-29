package com.cheese.shiro.server.gateway.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 权限服务注册配置项
 * @author sobann
 */
@Component
@ConfigurationProperties("cheese.shiro.server.register")
public class RegisterProps {
    /**
     * 检查周期
     */
    private int checkInterval = 60;
    /**
     * 检查线程数量
     */
    private int checkThreadNum = 2;

    public int getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
    }

    public int getCheckThreadNum() {
        return checkThreadNum;
    }

    public void setCheckThreadNum(int checkThreadNum) {
        this.checkThreadNum = checkThreadNum;
    }
}
