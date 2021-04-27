package com.ymm.microservices.oauth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ymm.microservices.oauth.entity.SysPermission;
import com.ymm.microservices.oauth.entity.SysUser;
import com.ymm.microservices.oauth.mapper.SysUserMapper;
import com.ymm.microservices.oauth.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户表(SysUser)表服务实现类
 *
 * @author makejava
 * @since 2020-09-22 16:01:08
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<SysPermission> findPermissionListByUser(SysUser user) {
        return sysUserMapper.findPermissionListByUser(user);
    }
}
