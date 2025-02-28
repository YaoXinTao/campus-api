package com.campus.api.service.impl;

import com.campus.api.entity.AdminUser;
import com.campus.api.mapper.AdminUserMapper;
import com.campus.api.service.AdminUserService;
import com.campus.api.dto.admin.AdminLoginDTO;
import com.campus.api.common.exception.BusinessException;
import com.campus.api.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@CacheConfig(cacheNames = "admin")
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AdminUser login(AdminLoginDTO loginDTO) {
        AdminUser adminUser = adminUserMapper.selectByUsername(loginDTO.getUsername());
        if (adminUser == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), adminUser.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (adminUser.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 更新登录信息
        adminUser.setLastLoginTime(LocalDateTime.now());
        adminUserMapper.update(adminUser);

        // 生成token
        String token = jwtUtil.generateToken(adminUser.getId().toString(), adminUser.getRole());
        adminUser.setToken(token); // 设置 token
        adminUser.setPassword(null); // 清除密码
        return adminUser;
    }

    @Override
    @Transactional
    public void createAdmin(AdminUser adminUser) {
        // 检查用户名是否存在
        AdminUser existUser = adminUserMapper.selectByUsername(adminUser.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 加密密码
        adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        adminUser.setStatus(1);
        adminUser.setRole("ADMIN"); // 设置默认角色为普通管理员
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUserMapper.insert(adminUser);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "admin", allEntries = true)
    public void updateAdmin(AdminUser adminUser) {
        AdminUser existUser = adminUserMapper.selectById(adminUser.getId());
        if (existUser == null) {
            throw new BusinessException("管理员不存在");
        }

        // 如果修改了用户名，检查是否存在
        if (!existUser.getUsername().equals(adminUser.getUsername())) {
            AdminUser userByUsername = adminUserMapper.selectByUsername(adminUser.getUsername());
            if (userByUsername != null) {
                throw new BusinessException("用户名已存在");
            }
        }

        adminUserMapper.update(adminUser);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#adminId")
    public void deleteAdmin(Long adminId) {
        AdminUser adminUser = adminUserMapper.selectById(adminId);
        if (adminUser == null) {
            throw new BusinessException("管理员不存在");
        }

        if ("SUPER_ADMIN".equals(adminUser.getRole())) {
            throw new BusinessException("超级管理员不能删除");
        }

        adminUserMapper.delete(adminId);
    }

    @Override
    // @Cacheable(key = "'list'") // 暂时注释掉缓存
    public List<AdminUser> getAdminList() {
        log.info("从数据库获取管理员列表");
        List<AdminUser> adminList = adminUserMapper.selectList();
        log.info("获取到的管理员列表: {}", adminList);
        return adminList;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#adminId")
    public void updatePassword(Long adminId, String oldPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectById(adminId);
        if (adminUser == null) {
            throw new BusinessException("管理员不存在");
        }

        if (!passwordEncoder.matches(oldPassword, adminUser.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        adminUserMapper.updatePassword(adminId, passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#adminId")
    public void updateStatus(Long adminId, Integer status) {
        AdminUser adminUser = adminUserMapper.selectById(adminId);
        if (adminUser == null) {
            throw new BusinessException("管理员不存在");
        }

        if ("SUPER_ADMIN".equals(adminUser.getRole())) {
            throw new BusinessException("超级管理员状态不能修改");
        }

        adminUserMapper.updateStatus(adminId, status);
    }

    @Override
    public AdminUser getAdminDetail(Long adminId) {
        AdminUser adminUser = adminUserMapper.selectById(adminId);
        if (adminUser == null) {
            throw new BusinessException("管理员不存在");
        }
        adminUser.setPassword(null); // 清除密码
        return adminUser;
    }
} 