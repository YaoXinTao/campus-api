package com.campus.api.controller.admin;

import com.campus.api.entity.AdminUser;
import com.campus.api.service.AdminUserService;
import com.campus.api.dto.admin.AdminLoginDTO;
import com.campus.api.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "管理员用户接口", description = "后台管理员用户相关接口")
@RestController
@RequestMapping("/api/v1/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(summary = "获取管理员详情")
    @GetMapping("/{adminId}")
    public Result<AdminUser> getAdminDetail(@Parameter(description = "管理员ID") @PathVariable Long adminId) {
        AdminUser adminUser = adminUserService.getAdminDetail(adminId);
        return Result.success(adminUser);
    }

    @Operation(summary = "删除管理员")
    @DeleteMapping("/{adminId}")
    public Result<Void> deleteAdmin(@Parameter(description = "管理员ID") @PathVariable Long adminId) {
        adminUserService.deleteAdmin(adminId);
        return Result.success();
    }

    @Operation(summary = "创建管理员")
    @PostMapping("/create")
    public Result<Void> createAdmin(@RequestBody AdminUser adminUser) {
        adminUserService.createAdmin(adminUser);
        return Result.success();
    }

    @Operation(summary = "获取管理员列表")
    @GetMapping("/list")
    public Result<List<AdminUser>> getAdminList() {
        List<AdminUser> adminList = adminUserService.getAdminList();
        return Result.success(adminList);
    }

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<AdminUser> login(@RequestBody AdminLoginDTO loginDTO, HttpServletResponse response) {
        AdminUser adminUser = adminUserService.login(loginDTO);
        response.setHeader("Authorization", "Bearer " + adminUser.getToken());
        return Result.success(adminUser);
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, Object> params) {
        Long adminId = Long.parseLong(params.get("id").toString());
        String oldPassword = params.get("oldPassword").toString();
        String newPassword = params.get("newPassword").toString();
        adminUserService.updatePassword(adminId, oldPassword, newPassword);
        return Result.success();
    }

    @Operation(summary = "更新管理员状态")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long adminId,
                                   @RequestParam Integer status) {
        adminUserService.updateStatus(adminId, status);
        return Result.success();
    }

    @Operation(summary = "更新管理员信息")
    @PutMapping("/update")
    public Result<Void> updateAdmin(@RequestBody AdminUser adminUser) {
        adminUserService.updateAdmin(adminUser);
        return Result.success();
    }
} 