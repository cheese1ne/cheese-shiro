package com.cheese.shiro.common.manager.identity;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存用户身份信息的上下文Holder
 * @author sobann
 */
public class IdentityHolder {
    /**
     * 线程信息保存
     */
    private static ThreadLocal<Map<String,Object>> Holder = ThreadLocal.withInitial(HashMap::new);
    private static final String CONTEXT ="Context";
    private static final String CONTEXT_OBJECT ="ContextObject";
    private static final String IDENTITY ="Identity";
    private static final String SUBJECT ="Subject";
    private static final String PRIMARY ="Primary";


    public static void setContext(String context){
        clear();
        Holder.get().put(CONTEXT,context);
    }

    public static String getContext(){
        return (String) Holder.get().get(CONTEXT);
    }

    public static void setContextObject(Object contextObject){
        Holder.get().put(CONTEXT_OBJECT,contextObject);
    }

    public static Object getContextObject(){
       return Holder.get().get(CONTEXT_OBJECT);
    }

    public static void setIdentity(String identity){
        Holder.get().put(IDENTITY,identity);
    }

    public static String getIdentity(){
        return (String)Holder.get().get(IDENTITY);
    }

    public static void setSubject(Object subject){
        Holder.get().put(SUBJECT,subject);
    }

    public static Object getSubject(){
       return Holder.get().get(SUBJECT);
    }

    public static void setPrimary(Object primary){
        Holder.get().put(PRIMARY,primary);
    }

    public static Object getPrimary(){
        return Holder.get().get(PRIMARY);
    }

    public static void clear(){
        Holder.remove();
    }
}
