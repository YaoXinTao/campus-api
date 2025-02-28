package com.campus.api.controller.mini;

import com.campus.api.entity.User;
import com.campus.api.service.UserService;
import com.campus.api.dto.mini.MiniLoginDTO;
import com.campus.api.dto.mini.PhoneLoginDTO;
import com.campus.api.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Tag(name = "小程序用户接口", description = "小程序用户相关接口")
@RestController
@RequestMapping("/api/v1/mini/user")
@Validated
public class MiniUserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户登录", description = "小程序用户登录接口，使用微信code换取openid并登录")
    @PostMapping("/login")
    public Result<User> login(@RequestBody @Valid MiniLoginDTO loginDTO) {
        User user = userService.miniLogin(loginDTO);
        if (user.getToken() != null) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletResponse response = attributes.getResponse();
                if (response != null) {
                    response.setHeader("Authorization", "Bearer " + user.getToken());
                }
            }
        }
        return Result.success(user);
    }

    @Operation(summary = "手机号登录", description = "使用手机号和验证码登录")
    @PostMapping("/phone-login")
    public Result<User> phoneLogin(@RequestBody @Valid PhoneLoginDTO loginDTO) {
        User user = userService.phoneLogin(loginDTO);
        if (user.getToken() != null) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletResponse response = attributes.getResponse();
                if (response != null) {
                    response.setHeader("Authorization", "Bearer " + user.getToken());
                }
            }
        }
        return Result.success(user);
    }

    @Operation(summary = "发送验证码", description = "向指定手机号发送验证码")
    @PostMapping("/send-code")
    public Result<Void> sendVerificationCode(@RequestBody @Valid SendCodeDTO sendCodeDTO) {
        userService.sendVerificationCode(sendCodeDTO.getPhone());
        return Result.success();
    }

    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    @SecurityRequirement(name = "bearer-key")
    @Parameter(name = "userId", description = "用户ID", required = true)
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam Long userId) {
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    @Operation(summary = "更新用户信息", description = "更新用户的基本信息")
    @SecurityRequirement(name = "bearer-key")
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody @Valid User user) {
        userService.updateUserInfo(user);
        return Result.success();
    }

    @Operation(summary = "绑定学生信息", description = "绑定用户的学生身份信息")
    @SecurityRequirement(name = "bearer-key")
    @Parameters({
        @Parameter(name = "userId", description = "用户ID", required = true),
        @Parameter(name = "studentId", description = "学号", required = true),
        @Parameter(name = "realName", description = "真实姓名", required = true)
    })
    @PostMapping("/bind-student")
    public Result<Void> bindStudentInfo(@RequestParam Long userId,
                                      @RequestParam String studentId,
                                      @RequestParam String realName) {
        userService.bindStudentInfo(userId, studentId, realName);
        return Result.success();
    }

    @Operation(summary = "检查手机号是否存在", description = "检查手机号是否已被其他用户使用")
    @GetMapping("/check-phone")
    public Result<Boolean> checkPhoneExists(@RequestParam String phone) {
        boolean exists = userService.checkPhoneExists(phone);
        return Result.success(exists);
    }

    @Operation(summary = "检查学号是否存在", description = "检查学号是否已被其他用户绑定")
    @GetMapping("/check-student")
    public Result<Boolean> checkStudentIdExists(@RequestParam String studentId) {
        boolean exists = userService.checkStudentIdExists(studentId);
        return Result.success(exists);
    }

    @Operation(summary = "注销账号", description = "用户注销账号")
    @SecurityRequirement(name = "bearer-key")
    @PostMapping("/deactivate")
    public Result<Void> deactivateUser(@RequestParam Long userId) {
        userService.deactivateUser(userId);
        return Result.success();
    }

    @Data
    public static class SendCodeDTO {
        @NotBlank(message = "手机号不能为空")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        private String phone;
    }
} 