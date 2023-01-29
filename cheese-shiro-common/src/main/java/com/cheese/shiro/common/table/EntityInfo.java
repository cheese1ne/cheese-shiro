package com.cheese.shiro.common.table;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 授权页面，hash 情况下，树状或列表形式显示具体数据范围
 *
 * @author sobann
 */
public class EntityInfo implements Serializable {
    private static final long serialVersionUID = -6687960432299124823L;
    private String id;
    private String name;
    private String pid;
    private List<EntityInfo> children;
    private boolean check = false;
    private Map<String, String> prop = new HashMap<>();

    public EntityInfo() {
    }

    public EntityInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public EntityInfo(String id, String name, String pid) {
        this.id = id;
        this.name = name;
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public List<EntityInfo> getChildren() {
        return children;
    }

    public void setChildren(List<EntityInfo> children) {
        this.children = children;
    }

    public Map<String, String> getProp() {
        return prop;
    }

    public void setProp(Map<String, String> prop) {
        this.prop = prop;
    }

    public void addProp(String prop, String value) {
        this.prop.put(prop, value);
    }
}
