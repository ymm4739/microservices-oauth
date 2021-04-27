package com.ymm.microservices.oauth.config;

import com.ymm.microservices.oauth.UserInfo;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class JwtTokenEnhancer implements TokenEnhancer {

    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        UserInfo userInfo = (UserInfo) oAuth2Authentication.getPrincipal();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", userInfo.getId());
        map.put("authorities", new HashSet(userInfo.getAuthorities()));
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(map);
        return oAuth2AccessToken;
    }
}
