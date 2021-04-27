package com.ymm.microservices.oauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymm.microservices.oauth.entity.SysPermission;
import com.ymm.microservices.oauth.entity.SysUser;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户表(SysUser)表数据库访问层
 *
 * @author makejava
 * @since 2020-09-22 16:01:06
 */
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

    List<SysPermission> findPermissionListByUser(SysUser user);
}
