package com.campus.api.service;

import com.campus.api.common.PageResult;
import com.campus.api.entity.User;
import com.campus.api.dto.mini.MiniLoginDTO;
import com.campus.api.dto.mini.PhoneLoginDTO;

public interface UserService {
    /**
     * 小程序用户登录
     */
    User miniLogin(MiniLoginDTO loginDTO);
    
    /**
     * 手机号登录
     */
    User phoneLogin(PhoneLoginDTO loginDTO);
    
    /**
     * 发送验证码
     */
    void sendVerificationCode(String phone);
    
    /**
     * 更新用户信息
     */
    void updateUserInfo(User user);
    
    /**
     * 获取用户信息
     */
    User getUserInfo(Long userId);
    
    /**
     * 绑定学生信息
     */
    void bindStudentInfo(Long userId, String studentId, String realName);

    /**
     * 检查手机号是否已被使用
     */
    boolean checkPhoneExists(String phone);

    /**
     * 检查学号是否已被绑定
     */
    boolean checkStudentIdExists(String studentId);

    /**
     * 用户注销
     */
    void deactivateUser(Long userId);

    /**
     * 重置密码（管理员功能）
     */
    void resetPassword(Long userId);

    /**
     * 获取小程序用户列表
     */
    PageResult<User> getMiniUserList(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 获取小程序用户详情
     */
    User getMiniUserDetail(Long id);

    /**
     * 更新小程序用户状态
     */
    void updateMiniUserStatus(Long id, Integer status);
} 