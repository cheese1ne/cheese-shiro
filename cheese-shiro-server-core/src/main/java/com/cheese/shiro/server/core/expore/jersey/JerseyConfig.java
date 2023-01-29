package com.cheese.shiro.server.core.expore.jersey;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * 权限服务对jersey的bean实例进行注册的配置
 *
 * @author sobann
 */
@ApplicationPath("/shiro")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(JerseyServerResource.class);
    }
}
