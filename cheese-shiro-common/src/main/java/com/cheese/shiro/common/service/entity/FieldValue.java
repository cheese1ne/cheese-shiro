package com.cheese.shiro.common.service.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 字段条件
 * @author sobann
 */
public class FieldValue implements Serializable {
    private static final long serialVersionUID = 7542902123360708850L;

    private String name;
    private String jsonValue;
    private Class type;

    public FieldValue() {
    }


    public FieldValue(String name, String jsonValue, Class type) {
        this.name = name;
        this.jsonValue = jsonValue;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJsonValue() {
        return jsonValue;
    }

    public void setJsonValue(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public Object getRealValue() {
        try {
            return JSON.parseObject(this.jsonValue,this.type);
        } catch (Exception e) {
            e.printStackTrace();
           return null;
        }
    }
}
