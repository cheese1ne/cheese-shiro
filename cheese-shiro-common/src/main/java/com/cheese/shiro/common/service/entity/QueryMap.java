package com.cheese.shiro.common.service.entity;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 条件hash表
 * @author sobann
 */
public class QueryMap implements Serializable {
    private boolean all = false;
    // scope:query
    private Map<String, List<String>> scopesMap = new HashMap<>();
    private Map<String, List<String>> props = new HashMap<>();
    private Map<String,Boolean> scopeIsStr = new HashMap<>();

    public QueryMap() {
    }

    public QueryMap(boolean all) {
        this.all = all;
    }

    public QueryMap(boolean all, Map<String, List<String>> scopes) {
        this.all = all;
        this.scopesMap = scopes;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public Map<String, List<String>> getScopesMap() {
        return scopesMap;
    }

    public void setScopesMap(Map<String, List<String>> scopesMap) {
        this.scopesMap = scopesMap;
    }

    public void addScope(String entity, Collection<String> entities){
        List<String> origial= scopesMap.get(entity);
        if(origial==null){
            if(entities instanceof ArrayList){
                scopesMap.put(entity,(List<String>) entities);
            }else {
                scopesMap.put(entity,new ArrayList<>(entities));
            }
        }else{
            origial.addAll(entities);
        }
    }

    public Map<String, List<String>> getProps() {
        return props;
    }

    public void setProps(Map<String, List<String>> props) {
        this.props = props;
    }

    public void addProp(String prop,String express){
        List<String> exprs = this.props.get(prop);
        if(CollectionUtils.isEmpty(exprs)){
            exprs = new ArrayList<>();
            exprs.add(express);
            this.props.put(prop,exprs);
        }else {
            if(!exprs.contains(props)){
                exprs.add(express);
            }
        }
    }


    public Map<String, Boolean> getScopeIsStr() {
        return scopeIsStr;
    }

    public void setScopeIsStr(Map<String, Boolean> scopeIsStr) {
        this.scopeIsStr = scopeIsStr;
    }

    public void addIsStr(String scope, boolean isStr){
        scopeIsStr.put(scope,isStr);
    }
}
