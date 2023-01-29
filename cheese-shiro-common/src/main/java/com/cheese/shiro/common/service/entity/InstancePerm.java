package com.cheese.shiro.common.service.entity;

/**
 * 权限实例
 * @author sobann
 */
public class InstancePerm extends Identifier {
    private static final long serialVersionUID = 7756588156242691217L;
    private String instanceId;

    public InstancePerm() {
    }

    public InstancePerm(String identifier, String instanceId) {
        super(identifier);
        this.instanceId = instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
