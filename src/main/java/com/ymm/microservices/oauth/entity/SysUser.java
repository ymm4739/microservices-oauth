package com.ymm.microservices.oauth.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

/**
 * 用户表(SysUser)表实体类
 *
 * @author makejava
 * @since 2020-09-22 16:01:00
 */

public class SysUser extends Model<SysUser> {
    //主键ID
    private Integer id;
    //用户名
    private String username;
    //密码
    private String password;
    //部门ID
    private Integer deptId;
    //邮箱
    private String email;
    //手机号
    private String phone;
    //头像
    private String avatar;
    //创建时间
    private Date createdTime;
    //修改时间
    private Date updatedTime;
    //0-正常，1-停用
    private Integer status;
    //0-正常，1-删除
    private Integer deleted;
    //租户id
    private String tenantId;
    //0-保密，1-男，2-女
    private Integer sex;
    //上次登陆时间
    private Date lastLoginTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
