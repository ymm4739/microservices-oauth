package com.ymm.microservices.oauth.feign.fallback;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.ymm.microservices.oauth.UserInfo;
import com.ymm.microservices.oauth.entity.SysPermission;
import com.ymm.microservices.oauth.entity.SysUser;
import com.ymm.microservices.oauth.feign.UserCenterAuth;
import com.ymm.microservices.oauth.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class UserCenterAuthFallback implements UserCenterAuth {
    private static final Logger log = LoggerFactory.getLogger(UserCenterAuthFallback.class);

    @Autowired
    private SysUserService userService;

    @Override
    public R auth(String username) {
        log.warn("feign调用user-center服务的auth方法失败");
        SysUser sysUser = new SysUser();
        sysUser.setUsername(username);

        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        SysUser user = userService.getOne(queryWrapper);
        if (null == user) {
            log.info("登录用户：" + username + " 获取失败.");
            throw new UsernameNotFoundException("登录用户：" + username + " 获取失败");
        }
        Collection<String> authorities = getUserAuthorities(user);
        Map<String, Object> result = new HashMap<>();
        result.put("username", user.getUsername());
        result.put("password", user.getPassword());
        result.put("authorities", authorities);
        result.put("id", user.getId());
        return R.ok(result);
    }

    private Collection<String> getUserAuthorities(SysUser user) {
        // 获取用户拥有的角色
        // 用户权限列表，根据用户拥有的权限标识与如 @PreAuthorize("hasAuthority('sys:menu:view')") 标注的接口对比，决定是否可以调用接口
        // 权限集合
        List<SysPermission> permissions = userService.findPermissionListByUser(user);
        Set<String> urls = new HashSet<>();
        for (SysPermission permission : permissions) {
            urls.add(permission.getUrl());
        }
        return urls;
    }
}
