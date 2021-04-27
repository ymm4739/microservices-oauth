package com.ymm.microservices.oauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    //@Autowired
    //private RestAuthExceptionHandler restAuthExceptionHandler;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {

        resources.resourceId("rid") // 配置资源id，这里的资源id和授权服务器中的资源id一致
                .stateless(true); // 设置这些资源仅基于令牌认证
        //     .accessDeniedHandler(restAuthExceptionHandler)
        //     .authenticationEntryPoint(restAuthExceptionHandler);
    }

    // 配置 URL 访问权限
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/oauth/**", "/rsa/publicKey")
                .permitAll()
                .anyRequest()
                .authenticated();
        //http.authorizeRequests().anyRequest().access("@rbacAuthorizationConfig.hasPermission(request, authentication)");
    }
}
