package com.cheese.shiro.server.gateway.handler;

import com.cheese.shiro.common.comparator.OrderComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理器管理器
 * @author sobann
 */
public class HandlerManager {
    private static final Logger logger = LoggerFactory.getLogger(HandlerManager.class);
    /**
     * 处理器链
     */
    private List<Handler> handlers = new ArrayList<>();
    /**
     * 处理器链中处理器的处理顺序比较器
     */
    private OrderComparator comparator = new OrderComparator();

    public HandlerManager() {
    }

    public List<Handler> getHandlers() {
        return handlers;
    }

    /**
     * 注入IOC中全部自定义的处理器
     * @param handlers
     */
    @Autowired(required = false)
    public void setHandlers(List<Handler> handlers) {
        this.handlers = new ArrayList<>(handlers);
        this.handlers.sort(comparator);
        logger.info("Load {} Handlers",this.handlers.size());
        this.handlers.forEach(
                handler -> logger.info("{} -> handler : {}",handler.order(),handler.getClass().getCanonicalName())
        );
    }

    public void addHandler(Handler handler){
        handlers.add(handler);
        this.handlers.sort(comparator);
    }

    public void addHandlers(List<Handler> handlers){
        this.handlers.addAll(handlers);
        this.handlers.sort(comparator);
    }
}
