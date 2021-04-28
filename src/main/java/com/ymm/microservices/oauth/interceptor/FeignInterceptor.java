package com.ymm.microservices.oauth.interceptor;


import com.ymm.microservices.oauth.config.RequestScopeConfig;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeignInterceptor implements RequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(FeignInterceptor.class);

    @Autowired
    private RequestScopeConfig requestScopeConfig;
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("tenantId", requestScopeConfig.getTenantId());
    }
}
