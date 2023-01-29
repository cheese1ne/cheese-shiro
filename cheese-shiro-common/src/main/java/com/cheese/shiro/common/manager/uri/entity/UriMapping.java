package com.cheese.shiro.common.manager.uri.entity;

import java.io.Serializable;
import java.util.Set;

/**
 * uri 映射
 * 映射的路径格式
 * 允许的请求方法 集合
 *
 * @author sobann
 */
public class UriMapping implements Serializable {
    private static final long serialVersionUID = 1329336476700607988L;
    /**
     * 资源路径集合
     */
    private Set<String> patterns;
    /**
     * 请求方法集合
     */
    private Set<String> requestMethods;

    public UriMapping() {
    }

    public UriMapping(Set<String> patterns, Set<String> requestMethods) {
        this.patterns = patterns;
        this.requestMethods = requestMethods;
    }

    public Set<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Set<String> patterns) {
        this.patterns = patterns;
    }

    public Set<String> getRequestMethods() {
        return requestMethods;
    }

    public void setRequestMethods(Set<String> requestMethods) {
        this.requestMethods = requestMethods;
    }
}
