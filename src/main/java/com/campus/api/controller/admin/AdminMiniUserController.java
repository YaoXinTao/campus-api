package com.campus.api.controller.admin;

import com.campus.api.common.PageResult;
import com.campus.api.common.Result;
import com.campus.api.entity.User;
import com.campus.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理后台小程序用户接口", description = "管理后台小程序用户相关接口")
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminMiniUserController {

    private final UserService userService;

    @Operation(summary = "获取小程序用户列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<PageResult<User>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword) {
        return Result.success(userService.getMiniUserList(pageNum, pageSize, keyword));
    }

    @Operation(summary = "获取小程序用户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<User> getDetail(@PathVariable Long id) {
        return Result.success(userService.getMiniUserDetail(id));
    }

    @Operation(summary = "更新小程序用户状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request) {
        userService.updateMiniUserStatus(id, request.getStatus());
        return Result.success();
    }
}

// 添加状态请求DTO
class StatusRequest {
    private Integer status;
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
} 