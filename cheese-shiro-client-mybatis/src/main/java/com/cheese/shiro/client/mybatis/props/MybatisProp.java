package com.cheese.shiro.client.mybatis.props;

import com.cheese.shiro.client.core.props.AuthProp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 权限mybatis设置
 */
@Component
@Configuration("cheese.shiro.mybatis")
public class MybatisProp {
    /**
     * 默认开启参数
     */
    private  String authParam ="auth";
    /**
     * 是否开启 sql无效化
     */
    private boolean enableSqlInValid = true;
    /**
     * 是否 开启 sql 校验
     */
    private boolean enableSqlAuth = true;
    /**
     * 默认 app
     */
    private String defaultApp = "*";

    public MybatisProp(@Autowired(required = false) AuthProp authProp) {
        if (Objects.nonNull(authProp)) {
            defaultApp = authProp.getDefaultApp();
        }
    }

    public String getAuthParam() {
        return authParam;
    }

    public void setAuthParam(String authParam) {
        this.authParam = authParam;
    }

    public boolean isEnableSqlInValid() {
        return enableSqlInValid;
    }

    public void setEnableSqlInValid(boolean enableSqlInValid) {
        this.enableSqlInValid = enableSqlInValid;
    }

    public boolean isEnableSqlAuth() {
        return enableSqlAuth;
    }

    public void setEnableSqlAuth(boolean enableSqlAuth) {
        this.enableSqlAuth = enableSqlAuth;
    }

    public String getDefaultApp() {
        return defaultApp;
    }

    public void setDefaultApp(String defaultApp) {
        this.defaultApp = defaultApp;
    }
}
