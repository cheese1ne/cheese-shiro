package com.cheese.shiro.rpc.dubbo.filter;

import com.cheese.shiro.common.manager.identity.IdentityManager;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于激活服务端过滤器
 * @author sobann
 */
@Activate(group ="provider")
public class ProviderIdentityTracerFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ProviderIdentityTracerFilter.class);
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if(IdentityManager.initialized()){
            String identityName = IdentityManager.getContextTracerName();
            String identity = RpcContext.getContext().getAttachment(identityName);
            IdentityManager.bind(identity);
        }
        return invoker.invoke(invocation);
    }
}
