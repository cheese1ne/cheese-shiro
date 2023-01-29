package com.cheese.shiro.server.core.sql.selector;

import com.alibaba.druid.pool.DruidDataSource;
import com.cheese.shiro.common.table.TableConverter;

import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.Map;

/**
 * 多数据源的druid实现，构造方法通过泛型限定处理连接池类型
 *
 * @author sobann
 */
public class MultiDruidDataSourceSqlTranslateSelector extends MultiDataSourceSqlTranslateSelector {

    private Collection<DruidDataSource> ds;

    public MultiDruidDataSourceSqlTranslateSelector(TableConverter tableConverter, String defaultDataSourceName, Map<String, DruidDataSource> dataSources) {
        super(tableConverter, defaultDataSourceName, dataSources);
        this.ds = dataSources.values();
    }

    @PreDestroy
    public void close() {
        if (ds != null && ds.size() > 0) {
            ds.forEach(
                    DruidDataSource::close
            );
        }
    }
}
