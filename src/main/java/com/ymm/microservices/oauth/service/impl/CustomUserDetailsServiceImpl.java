package com.ymm.microservices.oauth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import com.ymm.microservices.oauth.UserInfo;
import com.ymm.microservices.oauth.feign.UserCenterAuth;
import com.ymm.microservices.oauth.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;



@Configuration
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private static Logger log = LoggerFactory.getLogger(CustomUserDetailsServiceImpl.class);
    @Autowired
    private UserCenterAuth client;
    @Autowired
    private SysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // log.debug("username={}", username);
        R result = client.auth(username);
        // log.info("result = {}", result);
        Object data = result.getData();
        String jsonStr = JSON.toJSONString(data);
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String password = jsonObject.getString("password");
        Integer id = jsonObject.getInteger("id");
        Collection<String> authorities = jsonObject.getJSONArray("authorities").toJavaList(String.class);
        // log.info("id={},username={},password={},authorities={}", id, username, password, authorities);
        return new UserInfo(id, username, password, AuthorityUtils.createAuthorityList(authorities.toArray(new String[0])));
    }
}
