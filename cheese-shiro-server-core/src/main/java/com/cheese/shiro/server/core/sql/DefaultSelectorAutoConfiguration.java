package com.cheese.shiro.server.core.sql;

import com.cheese.shiro.common.sql.SqlTranslateSelector;
import com.cheese.shiro.common.table.TableConverter;
import com.cheese.shiro.server.core.sql.selector.DefaultSqlTranslateSelector;
import com.cheese.shiro.server.core.sql.translate.TemplateSqlTranslate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 默认单数据源配置
 * @author sobann
 */
@Configuration
public class DefaultSelectorAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSelectorAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public StmtExecutor stmtExecutor(DataSource dataSource) {
        return new DefaultStmtExecutor("default", dataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlTranslateSelector sqlTranslateSelector(TableConverter tableConverter, StmtExecutor stmtExecutor) {
        DefaultSqlTranslateSelector sqlTranslateSelector = new DefaultSqlTranslateSelector();
        sqlTranslateSelector.setSqlTranslate(new TemplateSqlTranslate(tableConverter, stmtExecutor));
        logger.info("prepare to initialize DefaultSqlTranslateSelector");
        return sqlTranslateSelector;
    }

}
