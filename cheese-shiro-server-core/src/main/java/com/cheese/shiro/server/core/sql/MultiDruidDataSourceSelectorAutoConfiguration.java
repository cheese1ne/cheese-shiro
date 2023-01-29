package com.cheese.shiro.server.core.sql;

import com.alibaba.druid.pool.DruidDataSource;
import com.cheese.shiro.common.sql.SqlTranslateSelector;
import com.cheese.shiro.common.table.TableConverter;
import com.cheese.shiro.server.core.config.DataSourceConfig;
import com.cheese.shiro.server.core.props.ShiroServerCoreProps;
import com.cheese.shiro.server.core.sql.selector.MultiDruidDataSourceSqlTranslateSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源选择器配置类，用于注入在权限服务中配置的多数据源
 * @author sobann
 */
@Configuration
public class MultiDruidDataSourceSelectorAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MultiDruidDataSourceSelectorAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public SqlTranslateSelector sqlTranslateSelector(TableConverter tableConverter, ShiroServerCoreProps shiroServerCoreProps){
        Map<String, DataSourceConfig> dataSources = shiroServerCoreProps.getDataSources();
        //将数据源配置转换成数据源对象
        Map<String,DruidDataSource> sourceMap = new HashMap<>();
        dataSources.forEach(
                (name,config)->{
                    sourceMap.put(name,createDataSource(config));
                    logger.info("Create DruidDataSource :{}",name);
                }
        );
        logger.info("prepare to initialize MultiDruidDataSourceSqlTranslateSelector");
        return new MultiDruidDataSourceSqlTranslateSelector(tableConverter,shiroServerCoreProps.getDefaultDataSource(),sourceMap);
    }


    private DruidDataSource createDataSource(DataSourceConfig config){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(config.getUrl());
        dataSource.setDriverClassName(config.getDriverClassName());
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());
        dataSource.setInitialSize(config.getInitialSize());
        dataSource.setMinIdle(config.getMinIdle());
        dataSource.setMaxActive(config.getMaxActive());
        dataSource.setMaxWait(config.getMaxWait());
        return dataSource;
    }

}
