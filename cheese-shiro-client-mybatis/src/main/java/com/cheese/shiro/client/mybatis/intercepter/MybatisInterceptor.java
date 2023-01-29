package com.cheese.shiro.client.mybatis.intercepter;

import com.cheese.shiro.client.mybatis.manager.AuthKeyManager;
import com.cheese.shiro.client.mybatis.util.ProxyTargetUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Properties;

/**
 * 客户端权限sql过滤器
 *
 * @author sobann
 */
@Intercepts({@Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class, Integer.class})})
public class MybatisInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(MybatisInterceptor.class);

    private AuthKeyManager authKeyManager;

    public void setAuthKeyManager(AuthKeyManager authKeyManager) {
        this.authKeyManager = authKeyManager;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //是否启动
        if (!authKeyManager.enableSqlAuth()) {
            return invocation.proceed();
        }
        StatementHandler handler = (StatementHandler) ProxyTargetUtil.realTarget(invocation.getTarget());
        MetaObject statementHandler = SystemMetaObject.forObject(handler);
        MappedStatement mappedStatement = (MappedStatement) statementHandler.getValue("delegate.mappedStatement");
        SqlCommandType type = mappedStatement.getSqlCommandType();
        //仅对查询语句进行 权限筛选
        if (!SqlCommandType.SELECT.equals(type)) {
            return invocation.proceed();
        }
        //获取 原生sql
        BoundSql boundSql = handler.getBoundSql();
        String sql = boundSql.getSql();
        //根据方法 获取 权限校验注解
        String method = mappedStatement.getId();
        //根据传参判断是否需要校验
        if (!authKeyManager.isAuth(method, boundSql.getParameterObject())) {
            return invocation.proceed();
        }
        String newSql = authKeyManager.enhanceSql(method, sql, boundSql.getParameterObject());
        statementHandler.setValue("delegate.boundSql.sql", newSql);
        logger.info("Origin SQL :{}", sql);
        logger.info("New SQL :{}", newSql);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        //生成代理对象
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
