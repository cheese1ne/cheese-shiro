package com.cheese.shiro.server.gateway.host;

import java.io.Serializable;

/**
 * 主机实体
 * @author sobann
 */
public class Host implements Serializable {
    private static final long serialVersionUID = -2755398854862254353L;
    private String host;
    private int port;

    public Host() {
    }

    public Host(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
