package com.cheese.shiro.server.gateway.register.http;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * 网关对jersey的bean实例进行注册的配置
 * @author sobann
 */
@ApplicationPath("/gateway")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(JerseyServerConfigResource.class);
    }
}
