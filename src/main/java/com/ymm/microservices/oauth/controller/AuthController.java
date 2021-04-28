package com.ymm.microservices.oauth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.ymm.microservices.oauth.config.ApiResult;
import com.ymm.microservices.oauth.config.RequestScopeConfig;
import com.ymm.microservices.oauth.dto.OAuth2TokenDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    KeyPair keyPair;

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    private RequestScopeConfig requestScopeConfig;

    /**
     * Oauth2登录认证
     */
    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST)
    public ApiResult postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String tenantId = request.getHeader("tenantId");
        requestScopeConfig.setTenantId(tenantId);
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        OAuth2TokenDto oauth2TokenDto = new OAuth2TokenDto();
        oauth2TokenDto.setAccessToken(oAuth2AccessToken.getValue());
        oauth2TokenDto.setRefreshToken(oAuth2AccessToken.getRefreshToken().getValue());
        oauth2TokenDto.setExpiration(oAuth2AccessToken.getExpiresIn());
        oauth2TokenDto.setTokenType(oAuth2AccessToken.getTokenType());

        return new ApiResult(0, "操作成功", oauth2TokenDto);
    }

    @RequestMapping(value = "/user-info", method = RequestMethod.GET)
    public Principal getUser(Principal principal) {
        return principal;
    }

    @GetMapping("/rsa/publicKey")
    public Map<String, Object> getKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }



}
