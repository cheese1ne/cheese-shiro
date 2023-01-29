package com.cheese.shiro.rpc.feign.strategy;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Hystrix 线程池 传递 identity
 * 自定义并发策略，解决Hystrix的默认隔离策略无法传递ThreadLocal数据的问题
 * 由于Hystrix只允许拥有一个并发策略，通过构造进行重置
 * 重点实现方法：wrapCallable，IdentityHystrixConcurrencyStrategy
 * @author sobann
 */
public class IdentityHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

    private static final Logger logger = LoggerFactory.getLogger(IdentityHystrixConcurrencyStrategy.class);

    private HystrixConcurrencyStrategy delegate;

    /**
     * 模仿SleuthHystrixConcurrencyStrategy改写并发策略
     * 参考：https://www.jianshu.com/p/f30892335057
     */
    public IdentityHystrixConcurrencyStrategy() {
        try {
            this.delegate = HystrixPlugins.getInstance().getConcurrencyStrategy();
            if (this.delegate instanceof IdentityHystrixConcurrencyStrategy) {
                logger.debug("Non another HystrixConcurrencyStrategy.");
                return;
            }
            HystrixCommandExecutionHook commandExecutionHook =
                    HystrixPlugins.getInstance().getCommandExecutionHook();
            HystrixEventNotifier eventNotifier =
                    HystrixPlugins.getInstance().getEventNotifier();
            HystrixMetricsPublisher metricsPublisher =
                    HystrixPlugins.getInstance().getMetricsPublisher();
            HystrixPropertiesStrategy propertiesStrategy =
                    HystrixPlugins.getInstance().getPropertiesStrategy();
            logger.debug("HystrixEventNotifier:{}, HystrixMetricsPublisher:{}, HystrixPropertiesStrategy:{}",
                    eventNotifier, metricsPublisher, propertiesStrategy);
            HystrixPlugins.reset();
            HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
            HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
            HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
        } catch (Exception e) {
            logger.error("Failed to register Tracing Hystrix Concurrency Strategy", e);
        }
    }

    /**
     * 上下文数据传递，通过静态IdentityHolder传递数据
     * tip：此处也可将从客户端获取到的完整的请求信息向外传递
     *
     */
    @Override
    public <T> Callable<T>wrapCallable(Callable<T> callable) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return new WrappedCallable<>(callable, requestAttributes);
        //只在自定义的IdentityHolder中保存用户认证数据
//        if(IdentityManager.hasContext()){
//            //服务消费方线程中获取Context
//            String context = IdentityManager.getContext();
//            return () -> {
//                try {
//                    //服务提供方线程绑定Context
//                    IdentityManager.bind(context);
//                    return delegate.wrapCallable(callable).call();
//                } finally {
//                    IdentityManager.unBind();
//                }
//            };
//        }else {
//            return ()-> delegate.wrapCallable(callable).call();
//        }
    }

    static class WrappedCallable<T> implements Callable<T> {

        private final Callable<T> target;
        private final RequestAttributes requestAttributes;

        public WrappedCallable(Callable<T> target, RequestAttributes requestAttributes) {
            this.target = target;
            this.requestAttributes = requestAttributes;
        }

        @Override
        public T call() throws Exception {
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                return target.call();
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }
    }

    @Override
    public ThreadPoolExecutor getThreadPool(final HystrixThreadPoolKey threadPoolKey, HystrixProperty<Integer> corePoolSize, HystrixProperty<Integer> maximumPoolSize, HystrixProperty<Integer> keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        return this.delegate.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixThreadPoolProperties threadPoolProperties) {
        return this.delegate.getThreadPool(threadPoolKey, threadPoolProperties);
    }

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return this.delegate.getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv) {
        return this.delegate.getRequestVariable(rv);
    }
}
