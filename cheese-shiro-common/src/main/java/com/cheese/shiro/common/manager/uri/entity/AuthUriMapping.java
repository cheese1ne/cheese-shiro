package com.cheese.shiro.common.manager.uri.entity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 接口权限验证信息，由接入的服务向中心传递
 *
 * @author sobann
 */
public class AuthUriMapping extends UriMapping implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 多重校验
     */
    private List<AuthInfo> auths = new ArrayList<>();

    public AuthUriMapping() {
    }


    public AuthUriMapping(List<AuthInfo> auths) {
        this.auths = auths;
    }

    public AuthUriMapping(Set<String> patterns, Set<String> requestMethods, List<AuthInfo> auths) {
        super(patterns, requestMethods);
        this.auths = auths;
    }

    public AuthUriMapping(AuthInfo authInfo) {
        if(this.auths==null){
            this.auths = new ArrayList<>();
        }
        this.auths.add(authInfo);
    }

    public List<AuthInfo> getAuths() {
        return auths;
    }

    public void setAuths(List<AuthInfo> auths) {
        this.auths = auths;
    }

    public void addAuthInfo(AuthInfo authInfo){
        if(this.auths==null){
            this.auths = new ArrayList<>();
        }
        this.auths.add(authInfo);
    }

    public void addUriMapping(UriMapping uriMapping){
        super.setPatterns(uriMapping.getPatterns());
        super.setRequestMethods(uriMapping.getRequestMethods());
    }
}
