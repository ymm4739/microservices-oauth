package com.ymm.microservices.oauth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ymm.microservices.oauth.entity.SysPermission;
import com.ymm.microservices.oauth.entity.SysUser;

import java.util.List;

/**
 * 用户表(SysUser)表服务接口
 *
 * @author makejava
 * @since 2020-09-22 16:01:08
 */
public interface SysUserService extends IService<SysUser> {

    List<SysPermission> findPermissionListByUser(SysUser user);
}
