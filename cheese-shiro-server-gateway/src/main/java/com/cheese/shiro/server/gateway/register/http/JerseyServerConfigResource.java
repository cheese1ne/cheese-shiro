package com.cheese.shiro.server.gateway.register.http;

import com.cheese.shiro.common.config.ServerConfig;
import com.cheese.shiro.common.config.WebServerConfig;
import com.cheese.shiro.common.domain.ClientInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * jersey控制层暴露接口
 * @author sobann
 */
@Singleton
@Path("/config")
public class JerseyServerConfigResource {
    private static final Logger logger = LoggerFactory.getLogger(JerseyServerConfigResource.class);
    @Autowired
    private HttpServerRegisterListener httpServerConfigListener;

    /**
     * 客户端实例注册
     * @param content
     * @return
     */
    @Path("/register")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String register(@RequestBody String content){
        ClientInstance clientInstance = (ClientInstance)httpServerConfigListener.getCoder().decode(content);
        httpServerConfigListener.register(clientInstance);
        ServerConfig serverConfig = httpServerConfigListener.getServerConfig();
        List<String> servers = httpServerConfigListener.getServers();
        WebServerConfig webServerConfig = new WebServerConfig(serverConfig);
        webServerConfig.setServers(servers);
        return httpServerConfigListener.getCoder().encode(webServerConfig);
    }

    /**
     * 数据同步
     * @return
     */
    @GET
    @Path("/sync")
    @Produces(MediaType.APPLICATION_JSON)
    public String getServerConfig(){
        ServerConfig serverConfig = httpServerConfigListener.getServerConfig();
        WebServerConfig webServerConfig = new WebServerConfig(serverConfig);
        return httpServerConfigListener.getCoder().encode(webServerConfig);
    }

    /**
     * 服务刷新
     * @param service
     * @return
     */
    @GET
    @Path("/refresh/{service}")
    public boolean refresh(@PathParam("service") String service){
        logger.info("Try to Refresh Instance Config For {}",service);
        httpServerConfigListener.refreshInstanceConfig(service);
        return true;
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean health(){
        return true;
    }
}
