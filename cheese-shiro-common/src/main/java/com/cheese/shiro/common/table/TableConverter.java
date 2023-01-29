package com.cheese.shiro.common.table;

/**
 * 实体转换器，权限配置信息的核心接口
 *
 * @author sobann
 */
public interface TableConverter {
    /**
     * 获取表名
     * @param obj
     * @return
     */
    String getTableName(String obj);

    /**
     * 获取关联表名
     * @param target
     * @param scope
     * @return
     */
    String getTableName(String target, String scope);

    /**
     * 获取id列
     * @param obj
     * @return
     */
    String getIdColumn(String obj);

    /**
     * 获取name列
     * @param obj
     * @return
     */
    String getNameColumn(String obj);

    /**
     * 获取 父id列
     * @param obj
     * @return
     */
    String getPidColumn(String obj);

    /**
     * 获取根id
     * @param object
     * @return
     */
    String getRootId(String object);

    /**
     * 获取创建列
     * @param obj
     * @return
     */
    String getCreateColumn(String obj);

    /**
     * 获取各位属性列
     * a,b,c
     * @param obj
     * @return
     */
    String getPropsColumn(String obj);
    /**
     * id是否为字符串
     * @param obj
     * @return
     */
    Boolean idTypeIsStr(String obj);

    /**
     * 获取级别
     * @param obj
     * @return
     */
    Integer getLevel(String obj);

    /**
     * 获取关联表配置信息
     * @param target
     * @param scope
     * @return
     */
    TableLink getTableLink(String target, String scope);

    /**
     * 获取单表配置信息
     * @param target
     * @return
     */
    TableEntity getTableEntity(String target);

    /**
     * 获取单表实现服务
     * @param target
     * @return
     */
    String queryService(String target);

    /**
     * 获取单表实现服务
     * @param target
     * @param scope
     * @return
     */
    String queryService(String target, String scope);
}
