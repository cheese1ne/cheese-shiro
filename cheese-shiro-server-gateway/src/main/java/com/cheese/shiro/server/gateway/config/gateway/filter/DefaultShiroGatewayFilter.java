package com.cheese.shiro.server.gateway.config.gateway.filter;

import com.cheese.shiro.common.comparator.OrderComparator;
import com.cheese.shiro.server.gateway.config.gateway.GatewayContext;
import com.cheese.shiro.server.gateway.handler.Handler;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 默认的gateway网关过滤器
 * @author sobann
 */
public class DefaultShiroGatewayFilter implements GlobalFilter, Ordered {

    private List<Handler> handlers = new ArrayList<>();

    public void setHandlers(List<Handler> handlers) {
        Collections.sort(handlers,new OrderComparator());
        this.handlers = handlers;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        GatewayContext context = new GatewayContext(exchange);
        for (Handler handler : handlers) {
            if(handler.isEnable() && handler.preHandle(context)){
                String errorContent = handler.getErrorContent(context);
                String errorContentType = handler.getErrorContentType(context);
                exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE,errorContentType);
                DataBuffer wrap = exchange.getResponse().bufferFactory().wrap(errorContent.getBytes());
                return exchange.getResponse().writeWith(Flux.just(wrap));
            }
        }
        return chain.filter(context.getOriginalContext());
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
