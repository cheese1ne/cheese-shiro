package com.cheese.shiro.client.mybatis.manager;

import com.cheese.shiro.client.mybatis.util.ProxyTargetUtil;
import com.cheese.shiro.common.anno.AuthKey;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * mybatis权限认证管理器
 *
 * @author sobann
 */

public class MybatisAuthKeyManager extends AuthKeyManager {

    private static final Logger logger = LoggerFactory.getLogger(MybatisAuthKeyManager.class);

    private String authKey;

    public MybatisAuthKeyManager(String authKey) {
        this.authKey = authKey;
    }


    private String getValue(String express, Map map) {
        if (StringUtils.isBlank(express) || !express.startsWith("$") || CollectionUtils.isEmpty(map)) {
            return express;
        }
        String key = express.substring(1);
        Object value = map.get(key);
        if (value != null && value instanceof String) {
            return (String) value;
        }
        return key;
    }


    @Override
    public AuthKeyInfo getAuthKeyInfoWithExpress(String keyExpress, String entityExpress, String ScopeExpress, String actionEpress, Object parameterObject) {
        Map map = null;
        if (parameterObject == null || parameterObject instanceof Map) {
            map = (Map) parameterObject;
        }
        AuthKeyInfo info = new AuthKeyInfo();
        info.setKey(getValue(keyExpress, map));
        info.setEntity(getValue(entityExpress, map));
        info.setScope(getValue(ScopeExpress, map));
        info.setAction(getValue(actionEpress, map));
        return info;
    }

    /**
     * 根据mybatis传参 或 方法名，判断是否进行校验
     *
     * @param param
     * @return
     */
    @Override
    public boolean isAuth(String methodName, Object param) {
        if (param == null || !(param instanceof Map)) {
            return false;
        }
        Map map = (Map) param;
        Object auth = null;
        Object ew = null;
        try {
            auth = map.get(authKey);
        } catch (Exception e) {
        }

        try {
            ew = map.get("ew");
        } catch (Exception e) {
        }

        if (auth != null) {
            if ((boolean) auth) {
                return true;
            }
        }
        if (ew != null && ew instanceof AuthAble) {
            AuthAble authable = (AuthAble) ew;
            if (authable.isAuth()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getFullClassNameMarkWithAuthKey(ListableBeanFactory beanFactory) {
        List<String> classes = new ArrayList<>();
        Map<String, Object> map = beanFactory.getBeansWithAnnotation(AuthKey.class);
        for (Object o : map.values()) {
            if (Proxy.isProxyClass(o.getClass())) {
                Object targetObject = null;
                try {
                    targetObject = ProxyTargetUtil.getTargetObject(o);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                MetaObject metaObject = SystemMetaObject.forObject(targetObject);
                String value = (String) metaObject.getValue("h.mapperInterface.name");
                classes.add(value);
            } else {
                classes.add(o.getClass().getCanonicalName());
            }
        }
        return classes;
    }

}
