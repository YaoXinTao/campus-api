package com.campus.api.service;

import com.campus.api.entity.AdminUser;
import com.campus.api.dto.admin.AdminLoginDTO;
import java.util.List;

public interface AdminUserService {
    /**
     * 管理员登录
     */
    AdminUser login(AdminLoginDTO loginDTO);
    
    /**
     * 创建管理员
     */
    void createAdmin(AdminUser adminUser);
    
    /**
     * 更新管理员信息
     */
    void updateAdmin(AdminUser adminUser);
    
    /**
     * 删除管理员
     */
    void deleteAdmin(Long adminId);
    
    /**
     * 获取管理员列表
     */
    List<AdminUser> getAdminList();
    
    /**
     * 更新管理员密码
     */
    void updatePassword(Long adminId, String oldPassword, String newPassword);
    
    /**
     * 更新管理员状态
     */
    void updateStatus(Long adminId, Integer status);
    
    /**
     * 获取管理员详情
     */
    AdminUser getAdminDetail(Long adminId);
} 