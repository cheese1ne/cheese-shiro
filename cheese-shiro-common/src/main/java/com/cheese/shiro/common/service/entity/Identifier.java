package com.cheese.shiro.common.service.entity;

import java.io.Serializable;

/**
 * 标识符
 * @author sobann
 */
public class Identifier implements Serializable {
    private static final long serialVersionUID = -1065421505323751821L;
    private String identifier;

    public Identifier() {
    }

    public Identifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
