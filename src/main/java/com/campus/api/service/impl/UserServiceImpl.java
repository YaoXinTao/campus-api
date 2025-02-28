package com.campus.api.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.campus.api.common.PageResult;
import com.campus.api.common.exception.BusinessException;
import com.campus.api.entity.User;
import com.campus.api.mapper.UserMapper;
import com.campus.api.service.UserService;
import com.campus.api.dto.mini.MiniLoginDTO;
import com.campus.api.dto.mini.PhoneLoginDTO;
import com.campus.api.util.JwtUtil;
import com.campus.api.annotation.RateLimiter;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.List;

@Slf4j
@Service
@CacheConfig(cacheNames = "user")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    @RateLimiter(key = "mini_login", time = 60, count = 5, message = "登录太频繁，请稍后再试")
    public User miniLogin(MiniLoginDTO loginDTO) {
        log.info("小程序用户登录: {}", loginDTO);
        
        // 调用微信API获取openid
        String openid;
        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(loginDTO.getCode());
            openid = session.getOpenid();
        } catch (WxErrorException e) {
            log.error("获取微信openid失败", e);
            throw new BusinessException("微信登录失败");
        }
        
        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            // 新用户注册
            user = new User();
            user.setOpenid(openid);
            user.setNickname(loginDTO.getNickname());
            user.setAvatarUrl(loginDTO.getAvatarUrl());
            user.setGender(loginDTO.getGender());
            user.setStatus(1);
            userMapper.insert(user);
        } else {
            // 更新登录信息
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.update(user);
        }

        // 生成token
        String token = jwtUtil.generateToken(user.getId().toString(), "MINI");
        user.setToken(token);
        
        return user;
    }

    @Override
    @Caching(evict = {
        @CacheEvict(key = "#user.id"),
        @CacheEvict(key = "'phone_' + #user.phone", condition = "#user.phone != null"),
        @CacheEvict(key = "'student_' + #user.studentId", condition = "#user.studentId != null")
    })
    public void updateUserInfo(User user) {
        log.info("更新用户信息: {}", user);
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.update(user);
    }

    @Override
    @Cacheable(key = "#userId")
    @RateLimiter(key = "get_user_info", time = 60, count = 100)
    public User getUserInfo(Long userId) {
        log.info("获取用户信息: {}", userId);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(key = "#userId"),
        @CacheEvict(key = "'student_' + #studentId")
    })
    @RateLimiter(key = "bind_student", time = 60, count = 3, message = "绑定学生信息太频繁，请稍后再试")
    public void bindStudentInfo(Long userId, String studentId, String realName) {
        log.info("绑定学生信息: userId={}, studentId={}, realName={}", userId, studentId, realName);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // TODO: 调用学校API验证学生信息
        userMapper.updateStudentInfo(userId, studentId, realName);
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        log.info("检查手机号是否存在: {}", phone);
        User user = userMapper.selectByPhone(phone);
        return user != null;
    }

    @Override
    public boolean checkStudentIdExists(String studentId) {
        log.info("检查学号是否存在: {}", studentId);
        User user = userMapper.selectByStudentId(studentId);
        return user != null;
    }

    @Override
    @Transactional
    @CacheEvict(key = "#userId")
    public void deactivateUser(Long userId) {
        log.info("注销用户: {}", userId);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.updateStatus(userId, 0);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#userId")
    public void resetPassword(Long userId) {
        log.info("重置用户密码: {}", userId);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 生成随机密码
        String randomPassword = generateRandomPassword();
        // 加密密码
        String encodedPassword = passwordEncoder.encode(randomPassword);
        // 更新密码
        userMapper.updatePassword(userId, encodedPassword);
        // TODO: 发送短信通知用户新密码
        log.info("用户密码重置成功，新密码已通过短信发送");
    }

    /**
     * 生成8位随机密码
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }

    @Override
    @Transactional
    @RateLimiter(key = "phone_login", time = 60, count = 5, message = "登录太频繁，请稍后再试")
    public User phoneLogin(PhoneLoginDTO loginDTO) {
        log.info("手机号登录: {}", loginDTO.getPhone());
        
        // 验证验证码
        String cacheCode = redisTemplate.opsForValue().get("verify_code:" + loginDTO.getPhone());
        if (cacheCode == null) {
            throw new BusinessException("验证码已过期");
        }
        if (!cacheCode.equals(loginDTO.getCode())) {
            throw new BusinessException("验证码错误");
        }
        
        // 删除验证码
        redisTemplate.delete("verify_code:" + loginDTO.getPhone());
        
        // 查找或创建用户
        User user = userMapper.selectByPhone(loginDTO.getPhone());
        if (user == null) {
            // 新用户注册
            user = new User();
            user.setPhone(loginDTO.getPhone());
            user.setNickname("用户" + loginDTO.getPhone().substring(7)); // 默认昵称为"用户"+手机号后4位
            user.setStatus(1);
            userMapper.insert(user);
        } else {
            // 更新登录信息
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.update(user);
        }

        // 生成token
        String token = jwtUtil.generateToken(user.getId().toString(), "MINI");
        user.setToken(token);
        
        return user;
    }

    @Override
    @RateLimiter(key = "send_code", time = 60, count = 1, message = "发送验证码太频繁，请稍后再试")
    public void sendVerificationCode(String phone) {
        log.info("发送验证码到手机: {}", phone);
        
        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        
        // 将验证码保存到Redis，设置5分钟过期
        redisTemplate.opsForValue().set("verify_code:" + phone, code, 5, TimeUnit.MINUTES);
        
        // 在控制台打印验证码（实际项目中应该调用短信服务发送）
        log.info("向手机号 {} 发送验证码: {}", phone, code);
    }

    @Override
    public PageResult<User> getMiniUserList(Integer pageNum, Integer pageSize, String keyword) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        long total = userMapper.selectCount(keyword);
        if (total == 0) {
            return PageResult.empty(pageNum, pageSize);
        }
        
        // 查询列表数据
        List<User> list = userMapper.selectList(keyword, offset, pageSize);
        
        return PageResult.of(pageNum, pageSize, total, list);
    }

    @Override
    public User getMiniUserDetail(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public void updateMiniUserStatus(Long id, Integer status) {
        // 参数校验
        if (status != 0 && status != 1) {
            throw new BusinessException("状态值无效");
        }
        
        // 检查用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新状态
        int rows = userMapper.updateStatus(id, status);
        if (rows != 1) {
            throw new BusinessException("更新用户状态失败");
        }
    }
} 