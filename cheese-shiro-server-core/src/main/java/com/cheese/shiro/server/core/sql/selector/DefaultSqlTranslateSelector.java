package com.cheese.shiro.server.core.sql.selector;


import com.cheese.shiro.common.sql.SqlTranslate;
import com.cheese.shiro.common.sql.SqlTranslateSelector;

/**
 * 默认选择器，单例SqlTranslate
 *
 * @author sobann
 */
public class DefaultSqlTranslateSelector implements SqlTranslateSelector {
    private SqlTranslate sqlTranslate;

    public void setSqlTranslate(SqlTranslate sqlTranslate) {
        this.sqlTranslate = sqlTranslate;
    }

    @Override
    public SqlTranslate getSqlTranslate(String target) {
        return sqlTranslate;
    }

    @Override
    public SqlTranslate getSqlTranslate(String target, String scope) {
        return sqlTranslate;
    }
}
