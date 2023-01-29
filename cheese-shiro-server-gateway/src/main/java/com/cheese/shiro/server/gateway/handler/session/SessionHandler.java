package com.cheese.shiro.server.gateway.handler.session;


import com.cheese.shiro.common.Context;
import com.cheese.shiro.common.manager.identity.IdentityManager;
import com.cheese.shiro.common.manager.session.Session;
import com.cheese.shiro.common.manager.session.SessionStoreManager;
import com.cheese.shiro.server.gateway.handler.Handler;

import java.util.Date;

/**
 * token验证成功之后使用，用于更新session (或者核对session)
 * order 220
 * @author sobann
 */
public class SessionHandler extends Handler {

    protected SessionStoreManager sessionStoreManager;

    public SessionHandler(SessionStoreManager sessionStoreManager) {
        this.sessionStoreManager = sessionStoreManager;
    }

    /**
     * 更新session
     * @param context
     * @return
     */
    @Override
    public boolean preHandle(Context context) {
        updateSessionExpire(context);
        return true;
    }

    public void updateSessionExpire(Context context){
        Object identityContext = context.getIdentityContext();
        if(identityContext==null){
            return;
        }
        String identity = IdentityManager.getContextParser().getIdentity(identityContext);
        Session session = sessionStoreManager.get(identity);
        Date expire = getExpire(sessionStoreManager.getExpire());
        if(session==null){
            session = new Session(identity,context.getRealIp(),new Date(),expire);
        }else {
            session.setExpireTime(expire);
        }
        sessionStoreManager.save(identity,session);
    }

    private Date getExpire(long ttlMins){
        long expMillis = System.currentTimeMillis() + ttlMins*1000*60;
        return new Date(expMillis);
    }


    @Override
    public int order() {
        return 220;
    }
}
