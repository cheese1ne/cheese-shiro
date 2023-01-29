package com.cheese.shiro.client.mybatis.manager;


import com.cheese.shiro.client.core.util.ApplicationContextHelper;
import com.cheese.shiro.client.mybatis.props.MybatisProp;
import com.cheese.shiro.common.anno.AuthKey;
import com.cheese.shiro.common.anno.AuthMapKey;
import com.cheese.shiro.common.anno.MultipleAuthKey;
import com.cheese.shiro.common.exception.NoAccessDataException;
import com.cheese.shiro.common.manager.identity.IdentityManager;
import com.cheese.shiro.common.service.ShiroServiceProvider;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.service.entity.QueryRet;
import com.cheese.shiro.common.util.ExpressUtils;
import com.cheese.shiro.common.util.SqlUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限认证管理器，完成权限信息的收集
 *
 * @author sobann
 */
public abstract class AuthKeyManager implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(AuthKeyManager.class);

    @Autowired
    protected ApplicationContextHelper applicationContextHelper;
    @Autowired
    protected ShiroServiceProvider shiroServiceProvider;
    @Autowired
    protected MybatisProp mybatisProp;

    public void setApplicationContextHelper(ApplicationContextHelper applicationContextHelper) {
        this.applicationContextHelper = applicationContextHelper;
    }

    public void setShiroServiceProvider(ShiroServiceProvider shiroServiceProvider) {
        this.shiroServiceProvider = shiroServiceProvider;
    }

    public void setMybatisProp(MybatisProp mybatisProp) {
        this.mybatisProp = mybatisProp;
    }

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    private Map<String, AuthKeyInfo> authKeyInfos = new HashMap<>();

    private Map<String, AuthMapKey> authMapKeyMap = new HashMap<>();

    private Map<String, MultipleAuthKey> multipleAuthKeyMap = new HashMap<>();

    public Map<String, AuthKeyInfo> getAuthKeyInfos() {
        return authKeyInfos;
    }

    /**
     * 将一个类中的 方法及对应 @authKey信息 保存在成员变量中
     *
     * @param clazzName
     * @throws ClassNotFoundException
     */
    public void setAuthKeyInfos(String clazzName) throws Exception {
        Class<?> loadClass = classLoader.loadClass(clazzName);
        if (loadClass.isAnnotationPresent(AuthKey.class)) {
            setAuthKeyInfos(loadClass);
        }
    }

    /**
     * 遍历 clazz 中方法，所有方法添加clazz上的默认注解，拥有新注解的则进行覆盖
     *
     * @param clazz
     */
    public void setAuthKeyInfos(Class<?> clazz) throws Exception {
        AuthKey clazzAnn = clazz.getAnnotation(AuthKey.class);
        String name = clazz.getName();
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            String methodName = name + "." + m.getName();
            if (m.isAnnotationPresent(AuthMapKey.class)) {
                AuthMapKey authMapKey = m.getAnnotation(AuthMapKey.class);
                String[] keys = authMapKey.keys();
                String[] scopes = authMapKey.scopes();
                if (keys.length != scopes.length || keys.length == 0) {
                    throw new Exception("keys and scopes length in @AuthMapKey Must be equal and Not be 0 With " + methodName);
                }
                this.authMapKeyMap.put(methodName, authMapKey);
            } else if (m.isAnnotationPresent(MultipleAuthKey.class)) {
                MultipleAuthKey annotation = m.getAnnotation(MultipleAuthKey.class);
                this.multipleAuthKeyMap.put(methodName, annotation);
            } else {
                AuthKeyInfo info = new AuthKeyInfo(clazzAnn);
                if (m.isAnnotationPresent(AuthKey.class)) {
                    AuthKey annotation = m.getAnnotation(AuthKey.class);
                    info.reset(annotation);
                }
                if (StringUtils.isBlank(info.getApp())) {
                    info.setApp(mybatisProp.getDefaultApp());
                }
                authKeyInfos.put(methodName, info);
            }
        }
    }

    /**
     * 鉴权接口
     *
     * @param identity
     * @param info
     * @return
     */
    public QueryRet getQueryRetFromService(String identity, AuthKeyInfo info) {
        QueryRet instanceIds;
        if (StringUtils.isBlank(info.getScope())) {
            instanceIds = shiroServiceProvider.getShiroService().getInstanceIdsWithAction(info.getEntity(), info.getAction(), identity, info.getApp());
        } else {
            instanceIds = shiroServiceProvider.getShiroService().getScopeIdsWithAction(info.getEntity(), info.getAction(), info.getScope(), identity, info.isIgnoreLevel(), info.getApp());
        }
        return instanceIds;
    }

    public QueryRet getQueryRetFromService(String identity, AuthKey authKey) {
        String app = StringUtils.isNotBlank(authKey.app()) ? authKey.app() : mybatisProp.getDefaultApp();
        QueryRet instanceIds;
        if (StringUtils.isBlank(authKey.scope())) {
            instanceIds = shiroServiceProvider.getShiroService().getInstanceIdsWithAction(authKey.entity(), authKey.action(), identity, app);
        } else {
            instanceIds = shiroServiceProvider.getShiroService().getScopeIdsWithAction(authKey.entity(), authKey.action(), authKey.scope(), identity, authKey.ignoreLevel(), app);
        }
        return instanceIds;
    }

    public QueryMap getQueryMapFromService(String identity, AuthMapKey authMapKey) {
        String app = StringUtils.isNotBlank(authMapKey.app()) ? authMapKey.app() : mybatisProp.getDefaultApp();
        return shiroServiceProvider.getShiroService().getQueryMap(authMapKey.entity(), authMapKey.action(), authMapKey.scopes(), identity, app);
    }


    /**
     * 修改 增强sql语句
     *
     * @param method          方法全称
     * @param sql             原始sql
     * @param parameterObject 传参
     * @return
     * @throws Exception
     */
    public String enhanceSql(String method, String sql, Object parameterObject) throws Exception {
        //获取当前用户身份,调用shiro服务，获取权限数据
        String identity = IdentityManager.getIdentityOrDefault();
        try {
            AuthMapKey authMapKey = authMapKeyMap.get(method);
            //使用@MultipleAuthKey
            if (authMapKey != null) {
                return enhanceSqlWithAuthMapKey(authMapKey, sql, identity);
            }
            MultipleAuthKey multipleAuthKey = multipleAuthKeyMap.get(method);
            if (multipleAuthKey != null) {
                return enhanceSqlWithMultipleAuthKey(multipleAuthKey, sql, identity);
            }
            //使用@AuthKey
            return enhanceSqlWithAuthKey(method, sql, identity, parameterObject);
        } catch (NoAccessDataException e) {
            if (mybatisProp.isEnableSqlInValid()) {
                return SqlUtil.invalid(sql);
            }
            throw e;
        }
    }

    public String enhanceSqlWithMultipleAuthKey(MultipleAuthKey multipleAuthKey, String sql, String identity) throws Exception {
        //多重条件
        List<Expression> expressions = new ArrayList<>();
        //注入@AuthKey条件
        injectExpressions(expressions, multipleAuthKey.authKeys(), identity);
        //注入@AuthMapKey条件
        injectExpressions(expressions, multipleAuthKey.authMapKeys(), identity);
        if (CollectionUtils.isEmpty(expressions)) {
            return sql;
        } else {
            Expression expression = SqlUtil.buildAndExpress(expressions);
            return SqlUtil.addCondition(sql, expression);
        }
    }

    public void injectExpressions(List<Expression> expressions, AuthKey[] authKeys, String identity) throws Exception {
        if (authKeys == null || authKeys.length == 0) {
            return;
        }
        for (AuthKey authKey : authKeys) {
            QueryRet queryRet = getQueryRetFromService(identity, authKey);
            if (queryRet.isAll()) {
                continue;
            }
            if (queryRet.isEmpty()) {
                throw new NoAccessDataException();
            }
            Expression expression = SqlUtil.buildInOrEqualExpress(authKey.key(), queryRet.getEntity(), queryRet.isStr());
            expressions.add(expression);
        }
    }


    public void injectExpressions(List<Expression> expressions, AuthMapKey[] authMapKeys, String identity) throws Exception {
        if (authMapKeys == null || authMapKeys.length == 0) {
            return;
        }
        for (AuthMapKey authMapKey : authMapKeys) {
            QueryMap queryMap = getQueryMapFromService(identity, authMapKey);
            if (queryMap.isAll()) {
                continue;
            }
            if (CollectionUtils.isEmpty(queryMap.getScopesMap()) && CollectionUtils.isEmpty(queryMap.getProps())) {
                throw new NoAccessDataException();
            }
            List<Expression> current = getExpressions(authMapKey, queryMap);
            if (CollectionUtils.isEmpty(current)) {
                return;
            }
            Expression expression = SqlUtil.buildOrExpress(current);
            if (current.size() > 1) {
                expression = CCJSqlParserUtil.parseCondExpression("(" + expression + ")");
            }
            expressions.add(expression);
        }
    }


    public String enhanceSqlWithAuthKey(String method, String sql, String identity, Object parameterObject) throws Exception {
        AuthKeyInfo active = getAuthKeyInfos().get(method);
        //动态组建info
        AuthKeyInfo info = getAuthKeyInfoWithExpress(active.getKey(), active.getEntity(), active.getScope(), active.getAction(), parameterObject);
        info.setIgnoreLevel(active.isIgnoreLevel());
        info.setApp(active.getApp());
        QueryRet queryRet = getQueryRetFromService(identity, info);
        if (queryRet.isAll()) {
            return sql;
        }
        if (queryRet.isEmpty()) {
            throw new NoAccessDataException();
        }
        return SqlUtil.addInCondition(sql, info.getKey(), queryRet.getEntity(), queryRet.isStr());
    }

    public String enhanceSqlWithAuthMapKey(AuthMapKey authMapKey, String sql, String identity) throws Exception {
        QueryMap queryMap = getQueryMapFromService(identity, authMapKey);
        if (queryMap.isAll()) {
            return sql;
        } else if (CollectionUtils.isEmpty(queryMap.getScopesMap()) && CollectionUtils.isEmpty(queryMap.getProps())) {
            throw new NoAccessDataException();
        } else {
            return enhanceSqlWithAuthMapKey(authMapKey, queryMap, sql);
        }
    }

    public String enhanceSqlWithAuthMapKey(AuthMapKey authMapKey, QueryMap queryMap, String sql) throws Exception {
        List<Expression> conditions = getExpressions(authMapKey, queryMap);
        if (!CollectionUtils.isEmpty(conditions)) {
            Expression expression = SqlUtil.buildOrExpress(conditions);
            if (conditions.size() == 1) {
                sql = SqlUtil.addCondition(sql, expression);
            } else {
                sql = SqlUtil.addCondition(sql, "(" + expression + ")");
            }
        }
        return sql;
    }

    public List<Expression> getExpressions(AuthMapKey authMapKey, QueryMap queryMap) throws JSQLParserException {
        List<Expression> conditions = new ArrayList<>();
        String[] scopes = authMapKey.scopes();
        String[] keys = authMapKey.keys();
        for (int i = 0; i < scopes.length; i++) {
            String scope = scopes[i];
            String key = keys[i];
            if (scope.startsWith("@")) {
                String prop = scope.substring(1);
                List<String> express = queryMap.getProps().get(prop);
                if (!CollectionUtils.isEmpty(express)) {
                    for (String expr : express) {
                        String condition = ExpressUtils.variableReplace(expr, var -> var.equals(prop) ? key : var);
                        conditions.add(SqlUtil.buildStringExpress(condition));
                    }
                }
            } else {
                List<String> scopeIds = queryMap.getScopesMap().get(scope);
                if (!CollectionUtils.isEmpty(scopeIds)) {
                    Boolean isStr = queryMap.getScopeIsStr().get(scope);
                    conditions.add(SqlUtil.buildInOrEqualExpress(key, scopeIds, isStr));
                }
            }
        }
        return conditions;
    }

    public boolean enableSqlAuth() {
        return mybatisProp.isEnableSqlAuth();
    }

    /**
     * 将动态注解，转换为确定信息
     * express = $param,从传参中动态获取
     *
     * @param keyExpress
     * @param entityExpress
     * @param ScopeExpress
     * @param actionEpress
     * @param parameterObject ignoreLevel 无法动态
     * @return
     */
    public abstract AuthKeyInfo getAuthKeyInfoWithExpress(String keyExpress, String entityExpress, String ScopeExpress, String actionEpress, Object parameterObject);

    /**
     * 判断是否进行校验
     *
     * @param param
     * @param methodName
     * @return
     */
    public abstract boolean isAuth(String methodName, Object param);

    /**
     * 获取所有被 @AuthKey标记的全类名
     *
     * @param beanFactory
     * @return
     */
    public abstract List<String> getFullClassNameMarkWithAuthKey(ListableBeanFactory beanFactory);

    /**
     * 应用启动后 ，自动加载带有@authKey的类
     *
     * @param contextRefreshedEvent
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext context = applicationContextHelper.getApplicationContext();
        List<String> clazzes = getFullClassNameMarkWithAuthKey(context);
        if (!CollectionUtils.isEmpty(clazzes)) {
            for (String clazz : clazzes) {
                try {
                    setAuthKeyInfos(clazz);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }
        logger.info("Initial authKeyInfos:{}", this.authKeyInfos);
    }

}
