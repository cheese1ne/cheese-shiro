package com.cheese.shiro.session.manager;


import com.cheese.shiro.common.manager.identity.IdentityManager;
import com.cheese.shiro.common.manager.session.Session;
import com.cheese.shiro.common.manager.session.SessionStoreManager;

import java.util.Map;

/**
 * session管理器
 * @author sobann
 */
public class SessionManager {
    private static SessionStoreManager SessionStoreManager;

    public static void init(SessionStoreManager sessionStoreManager){
        SessionManager.SessionStoreManager = sessionStoreManager;
    }

    public static void save(Session session){
        String identity = IdentityManager.getIdentity();
        SessionStoreManager.save(identity,session);
    }

    public static Session get(){
        String identity = IdentityManager.getIdentity();
        return SessionStoreManager.get(identity);
    }

    public static void clear(){
        String identity = IdentityManager.getIdentity();
        SessionStoreManager.clear(identity);
    }

    public static Object getProp(String prop){
        String identity = IdentityManager.getIdentity();
        return SessionStoreManager.getProp(identity,prop);
    }

    public static void setProp(String prop, Object value){
        String identity = IdentityManager.getIdentity();
        SessionStoreManager.saveProp(identity,prop,value);
    }

    public static void setProps(Map<String,Object> props){
        String identity = IdentityManager.getIdentity();
        SessionStoreManager.saveProps(identity,props);
    }

    public static Object removeProp(String prop){
        String identity = IdentityManager.getIdentity();
        return SessionStoreManager.removeProp(identity,prop);
    }

    public static Map<String,Object> getProps(){
        String identity = IdentityManager.getIdentity();
        return SessionStoreManager.getProps(identity);
    }
}
