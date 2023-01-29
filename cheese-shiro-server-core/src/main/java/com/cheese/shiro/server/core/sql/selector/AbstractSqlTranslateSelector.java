package com.cheese.shiro.server.core.sql.selector;

import com.cheese.shiro.common.sql.SqlTranslate;
import com.cheese.shiro.common.sql.SqlTranslateSelector;
import com.cheese.shiro.common.table.TableConverter;
import org.apache.commons.lang.StringUtils;

/**
 * 多数据源情况下 使用键值形式的数据转换器抽象类
 * 默认实现根据数据源名称获取转换器
 * 子类实现模板方法
 *
 * @author sobann
 */
public abstract class AbstractSqlTranslateSelector implements SqlTranslateSelector {

    @Override
    public SqlTranslate getSqlTranslate(String target) {
        String service = getTableConverter().queryService(target);
        return getsqlSqlTranslateByService(service);
    }

    @Override
    public SqlTranslate getSqlTranslate(String target, String scope) {
        String service = getTableConverter().queryService(target, scope);
        return getsqlSqlTranslateByService(service);
    }

    public SqlTranslate getsqlSqlTranslateByService(String service) {
        if (StringUtils.isBlank(service)) {
            return getDefaultSqlTranslate();
        } else {
            return getSqlTranslateByService(service);
        }
    }

    public abstract SqlTranslate getSqlTranslateByService(String service);

    public abstract TableConverter getTableConverter();

    public abstract SqlTranslate getDefaultSqlTranslate();

}
