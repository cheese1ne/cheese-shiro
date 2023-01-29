package com.cheese.shiro.common.table;

import java.io.Serializable;

/**
 * 实体 与 表名 主键 映射
 *
 * @author sobann
 */
public class TableEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 对象代号
     */
    private String entity;
    /**
     * 数据库表表达式
     */
    private String tableExpression;
    /**
     * 主键列名
     */
    private String primaryKey;
    /**
     * 主键数据类型
     */
    private String keyType;
    /**
     * 名称列
     */
    private String name;
    /**
     * 范围大小
     */
    private Integer level;
    /**
     * 父id
     */
    private String pid;
    /**
     * 创建id
     */
    private String createEntity;
    /**
     * 实现服务
     */
    private String service;
    /**
     * 根id
     */
    private String rootId;
    /**
     * 扩展列
     */
    private String props;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getTableExpression() {
        return tableExpression;
    }

    public void setTableExpression(String tableExpression) {
        this.tableExpression = tableExpression;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCreateEntity() {
        return createEntity;
    }

    public void setCreateEntity(String createEntity) {
        this.createEntity = createEntity;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public String getProps() {
        return props;
    }

    public void setProps(String props) {
        this.props = props;
    }
}
