package com.cheese.shiro.server.gateway.handler.assign;

import com.cheese.shiro.common.Context;
import com.cheese.shiro.server.gateway.assign.AssignRouter;
import com.cheese.shiro.server.gateway.handler.Handler;
import com.cheese.shiro.server.gateway.host.Host;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * 实现 @AssignRoute 具体功能
 * order 320
 * @author sobann
 *
 */
public class ZuulAssignRouteHandler extends Handler {

    private static final Logger logger = LoggerFactory.getLogger(ZuulAssignRouteHandler.class);

    private AssignRouter assignRouter;

    public void setAssignRouter(AssignRouter assignRouter) {
        this.assignRouter = assignRouter;
    }

    /**
     * 更改路由
     *
     * @param context
     * @return
     */
    @Override
    public boolean preHandle(Context context) {
        Host host = assignRouter.assign(context);
        if (host != null) {
            try {
                RequestContext currentContext = (RequestContext) context.getOriginalContext();
                assignHost(currentContext, host);
                Object key = context.getAttribute(AssignRouter.KEY);
                logger.info("Assign Route Host For {} With Host={}, port={}", key, host.getHost(), host.getPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 解除路由指派
     *
     * @param context
     */
    @Override
    public void onError(Context context) {
        assignRouter.unAssign(context);
    }

    @Override
    public int order() {
        return 320;
    }

    /**
     * 更改路由
     *
     * @param requestContext
     * @param host
     * @throws Exception
     */
    private void assignHost(RequestContext requestContext, Host host) throws Exception {
        URL assignHost = null;
        URL routeHost = requestContext.getRouteHost();
        if (routeHost == null) {
            assignHost = new URL("http", host.getHost(), host.getPort(), "/");
        } else {
            assignHost = new URL(routeHost.getProtocol(), host.getHost(), host.getPort(), routeHost.getFile());
        }
        requestContext.setRouteHost(assignHost);
    }

}
