package com.cheese.shiro.server.core;

import com.cheese.shiro.common.manager.token.JwtSyncTokenManager;
import com.cheese.shiro.common.manager.token.TokenManager;
import com.cheese.shiro.common.perm.Permission;
import com.cheese.shiro.common.service.ShiroService;
import com.cheese.shiro.common.service.ShiroServiceProvider;
import com.cheese.shiro.common.sql.SqlTranslateSelector;
import com.cheese.shiro.common.table.TableConverter;
import com.cheese.shiro.server.core.authentication.DefaultShiroService;
import com.cheese.shiro.server.core.authorization.Realm;
import com.cheese.shiro.server.core.manager.identifier.IdentifierManager;
import com.cheese.shiro.server.core.manager.identifier.IdentifierPermission;
import com.cheese.shiro.server.core.manager.identifier.ShiroIdentifierManager;
import com.cheese.shiro.server.core.manager.instance.DefaultInstanceManager;
import com.cheese.shiro.server.core.manager.instance.InstanceManager;
import com.cheese.shiro.server.core.manager.rule.RuleExplainer;
import com.cheese.shiro.server.core.manager.rule.parser.DefaultRuleParseManager;
import com.cheese.shiro.server.core.manager.rule.parser.RuleParserManager;
import com.cheese.shiro.server.core.manager.rule.parser.hash.HashChildRuleParser;
import com.cheese.shiro.server.core.manager.rule.parser.hash.HashSelfRuleParser;
import com.cheese.shiro.server.core.manager.rule.parser.predefine.PreChildRuleParser;
import com.cheese.shiro.server.core.manager.rule.parser.predefine.PreCreateRuleParser;
import com.cheese.shiro.server.core.manager.rule.parser.predefine.PreParentRuleParser;
import com.cheese.shiro.server.core.manager.rule.parser.predefine.PreSelfRuleParser;
import com.cheese.shiro.server.core.manager.rule.parser.propertity.PropertityRuleParser;
import com.cheese.shiro.server.core.props.ShiroServerCoreProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 权限服务核心配置
 * @author sobann
 */
@ComponentScan(basePackages = "com.cheese.shiro.server.core.props")
@Configuration
public class ShiroServerAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ShiroServerAutoConfiguration.class);
    /*************************server 配置******************************************/

    /**
     * 与网关保持一致
     * 默认注册后，server配置将与gateway同步,效果相同
     * 或 server 远程调用 gateway
     * 或 gateway 远程调用 server
     * gatway 使用频率 较高
     *
     * @param
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(TokenManager.class)
    public JwtSyncTokenManager tokenManager() {
        JwtSyncTokenManager jwtTokenManager = new JwtSyncTokenManager();
        logger.info("prepare to initialize JwtSyncTokenManager");
        return jwtTokenManager;
    }

    /*************************配置 数据实例管理器******************************************/
    @Bean
    @ConditionalOnMissingBean
    public InstanceManager instanceManager(TableConverter tableConverter, RuleExplainer ruleExplainer) {
        logger.info("prepare to initialize DefaultInstanceManager");
        return new DefaultInstanceManager(tableConverter, ruleExplainer);
    }

    /*************************配置 规则解释器******************************************/
    @Bean
    @ConditionalOnMissingBean
    public RuleExplainer ruleExplainer(SqlTranslateSelector sqlTranslateSelector, TableConverter tableConverter, RuleParserManager ruleParserManager) {
        logger.info("prepare to initialize RuleExplainer");
        return new RuleExplainer(sqlTranslateSelector, tableConverter, ruleParserManager);
    }

    /*************************配置 标识符管理器 ******************************************/
    @Bean
    @ConditionalOnMissingBean
    public Permission permission() {
        logger.info("prepare to initialize IdentifierPermission");
        return new IdentifierPermission();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdentifierManager identifierManager(Permission permission) {
        logger.info("prepare to initialize ShiroIdentifierManager");
        return new ShiroIdentifierManager(permission);
    }

    /*************************开启默认shiroService实现类******************************************/
    @Bean
    public ShiroService shiroService(Realm realm, InstanceManager instanceManager, IdentifierManager identifierManager, ShiroServerCoreProps shiroServerCoreProps) {
        DefaultShiroService defaultShiroService = new DefaultShiroService(realm, instanceManager, identifierManager);
        defaultShiroService.setBatchSize(shiroServerCoreProps.getBatchSize());
        logger.info("prepare to initialize the ShiroService wrapper: DefaultShiroService");
        return defaultShiroService;
    }

    @Bean
    @ConditionalOnMissingBean
    public ShiroServiceProvider shiroServiceProvider(ShiroService shiroService) {
        //不基于rpc方式获取ShiroServiceProvider，返回DefaultShiroService，此种方式仅由鉴权服务提供者自身使用
        logger.info("prepare to initialize ShiroServiceProvider");
        return () -> shiroService;
    }

    /*************************开启规则管理器******************************************/
    /**
     * 加载默认规则解析器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public RuleParserManager ruleParserManager() {
        DefaultRuleParseManager ruleParseManager = new DefaultRuleParseManager();
        //执行顺序：预定义-哈希-属性
        ruleParseManager.addRuleParser(new PreCreateRuleParser());
        ruleParseManager.addRuleParser(new PreChildRuleParser());
        ruleParseManager.addRuleParser(new PreParentRuleParser());
        ruleParseManager.addRuleParser(new PreSelfRuleParser());
        ruleParseManager.addRuleParser(new HashSelfRuleParser());
        ruleParseManager.addRuleParser(new HashChildRuleParser());
        ruleParseManager.addRuleParser(new PropertityRuleParser());
        logger.info("prepare to initialize DefaultRuleParseManager");
        return ruleParseManager;
    }


}
