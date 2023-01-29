package com.cheese.shiro.client.mybatis.util;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * 解决jdk或cglib动态形成的Mybatis mapper代理类
 * @author sobann
 */
public class ProxyTargetUtil {

    private static String JdkField="h";
    private static String CglibField="CGLIB$CALLBACK_0";

    public static Object getTargetObject(Object obj) throws Exception {
        if(!AopUtils.isAopProxy(obj)){
           return obj;
        }

        if(AopUtils.isCglibProxy(obj)){
            return getCglibTargetObject(obj);
        }else if(AopUtils.isJdkDynamicProxy(obj)){
            return getJdkTargetObject(obj);
        }else {
            return obj;
        }
    }

    public static Object getJdkTargetObject(Object obj) throws Exception {
        return getTargetObejct(obj,JdkField);
    }

    public static Object getCglibTargetObject(Object obj) throws Exception {
        return getTargetObejct(obj,CglibField);
    }

    private static Object getTargetObejct(Object obj,String fieldName) throws Exception {
        Field h = obj.getClass().getSuperclass().getDeclaredField(fieldName);
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(obj);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Object newObj = ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();
        return newObj;
    }

    /**
     * <p>
     * 获得真正的处理对象,可能多层代理.ibatisMapperProxy
     * </p>
     */
    public static Object realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return target;
    }

}
