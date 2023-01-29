package com.cheese.shiro.client.core.interceptor;

import com.cheese.shiro.common.manager.identity.IdentityManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * HTTPClient请求拦截器，在发送请求时在请求头上添加用户身份信息
 * 加入拦截器链，在身份认证拦截器和负载均衡拦截器之后执行
 * 引入JSR303
 *
 * @author sobann
 */
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor, InitializingBean {

    @Autowired(required = false)
    private List<RestTemplate> restTemplates;

    @Override
    @NonNull
    public ClientHttpResponse intercept(
            @NonNull HttpRequest httpRequest, @NonNull byte[] bytes,
            @NonNull ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        if (IdentityManager.hasContext()) {
            httpRequest.getHeaders().add(IdentityManager.getContextTracerName(), IdentityManager.getContext());
        }
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Objects.nonNull(restTemplates)) {
            restTemplates.forEach(restTemplate -> {
                List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
                interceptors.add(interceptors.size(), RestTemplateInterceptor.this);
            });
        }
    }
}
