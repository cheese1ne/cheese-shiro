package com.cheese.shiro.rpc.dubbo.filter;

import com.cheese.shiro.common.manager.identity.IdentityManager;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于激活消费端过滤器
 * @author sobann
 */
@Activate(group ="consumer")
public class ConsumerIdentityTracerFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerIdentityTracerFilter.class);
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if(IdentityManager.hasContext()){
            RpcContext.getContext().setAttachment(IdentityManager.getContextTracerName(),IdentityManager.getContext());
        }
        return invoker.invoke(invocation);
    }
}
