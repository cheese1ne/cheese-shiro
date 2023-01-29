package com.cheese.shiro.server.core.sql.selector;

import com.cheese.shiro.common.sql.SqlTranslate;
import com.cheese.shiro.common.table.TableConverter;
import com.cheese.shiro.server.core.sql.DefaultStmtExecutor;
import com.cheese.shiro.server.core.sql.StmtExecutor;
import com.cheese.shiro.server.core.sql.translate.TemplateSqlTranslate;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多数据源 数据转换器 选择器 默认实现，构造方法通过泛型限定处理连接池类型
 * entry:serviceName->sqlTranslate
 *
 * @author sobann
 */
public class MultiDataSourceSqlTranslateSelector extends AbstractSqlTranslateSelector {

    private TableConverter tableConverter;
    private Map<String, SqlTranslate> sqlTranslates;
    private SqlTranslate defaultSqlTranslate;

    public MultiDataSourceSqlTranslateSelector(TableConverter tableConverter, String defaultDataSourceName, Map<String, ? extends DataSource> dataSources) {
        this.tableConverter = tableConverter;
        //通过构造函数将数据源列表配置为数据转换器列表
        this.sqlTranslates = createSqlTranslates(dataSources);
        this.defaultSqlTranslate = sqlTranslates.get(defaultDataSourceName);
    }

    @Override
    public SqlTranslate getSqlTranslateByService(String service) {
        return sqlTranslates.get(service);
    }

    @Override
    public TableConverter getTableConverter() {
        return tableConverter;
    }

    @Override
    public SqlTranslate getDefaultSqlTranslate() {
        return defaultSqlTranslate;
    }

    private Map<String, SqlTranslate> createSqlTranslates(Map<String, ? extends DataSource> dataSources) {
        Map<String, SqlTranslate> map = new ConcurrentHashMap<>();
        //根据 数据服务名称->数据服务数据源 键值对构建 数据服务转换器
        dataSources.forEach(
                (name, datasource) -> map.put(name, createSqlTranslate(name, datasource))
        );
        return map;
    }

    /**
     * 实例化 TemplateSqlTranslate
     * 注入实例转换器，sql执行器
     *
     * @param datasource
     * @return
     */
    public SqlTranslate createSqlTranslate(String service, DataSource datasource) {
        return new TemplateSqlTranslate(tableConverter, createStmtExecutor(service, datasource));
    }

    /**
     * 实例化 StmtExecutor
     *
     * @param dataSource
     * @return
     */
    public StmtExecutor createStmtExecutor(String service, DataSource dataSource) {
        return new DefaultStmtExecutor(service, dataSource);
    }

}
