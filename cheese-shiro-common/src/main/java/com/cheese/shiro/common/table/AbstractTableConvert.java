package com.cheese.shiro.common.table;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 抽象实体转换器，定义数据类型，子类完成功能模板
 *
 * @author sobann
 */
public abstract class AbstractTableConvert implements TableConverter {
    private static List<String> numTypes = Arrays.asList(new String[]{"int", "smallint", "tinyint", "mediumint", "bigint"});

    @Override
    public String getTableName(String obj) {
        TableEntity tableEntity = getTableEntity(obj);
        return tableEntity == null ? null : tableEntity.getTableExpression();
    }

    @Override
    public String getTableName(String target, String scope) {
        TableLink tableLink = getTableLink(target, scope);
        if (tableLink == null) {
            tableLink = getTableLink(scope, target);
        }
        return tableLink == null ? null : tableLink.getLinkExpression();
    }

    @Override
    public String getIdColumn(String obj) {
        TableEntity tableEntity = getTableEntity(obj);
        return tableEntity == null ? null : tableEntity.getPrimaryKey();
    }

    @Override
    public String getNameColumn(String obj) {
        TableEntity tableEntity = getTableEntity(obj);
        return tableEntity == null ? null : tableEntity.getName();
    }

    @Override
    public String getPidColumn(String obj) {
        TableEntity tableEntity = getTableEntity(obj);
        return tableEntity == null ? null : tableEntity.getPid();
    }

    @Override
    public String getRootId(String object) {
        TableEntity tableEntity = getTableEntity(object);
        return tableEntity == null ? "0" : tableEntity.getRootId();
    }

    @Override
    public String getCreateColumn(String obj) {
        TableEntity tableEntity = getTableEntity(obj);
        return tableEntity == null ? null : tableEntity.getCreateEntity();
    }

    @Override
    public Boolean idTypeIsStr(String obj) {
        TableEntity tableEntity = getTableEntity(obj);
        if (tableEntity != null) {
            return typeIsStr(tableEntity.getKeyType());
        }
        return true;
    }

    public Boolean typeIsStr(String type) {
        if (StringUtils.isBlank(type)) {
            return false;
        }
        String str = type.trim().toLowerCase();
        return !numTypes.contains(str);
    }

    @Override
    public Integer getLevel(String obj) {
        TableEntity tableEntity = getTableEntity(obj);
        return tableEntity == null ? null : tableEntity.getLevel();
    }

    @Override
    public String queryService(String target) {
        TableEntity tableEntity = getTableEntity(target);
        return tableEntity == null ? null : tableEntity.getService();
    }

    @Override
    public String queryService(String target, String scope) {
        TableLink tableLink = getTableLink(target, scope);
        if (tableLink == null) {
            tableLink = getTableLink(scope, target);
        }
        return tableLink == null ? null : tableLink.getService();
    }

    @Override
    public String getPropsColumn(String obj) {
        TableEntity tableEntity = getTableEntity(obj);
        return tableEntity == null ? null : tableEntity.getProps();
    }
}
