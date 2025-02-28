package com.campus.api.controller.admin;

import com.campus.api.common.Result;
import com.campus.api.dto.address.AddressDTO;
import com.campus.api.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理后台收货地址接口", description = "管理后台收货地址相关接口")
@RestController
@RequestMapping("/api/v1/admin/address")
@RequiredArgsConstructor
public class AdminAddressController {

    private final AddressService addressService;

    @Operation(summary = "获取用户地址列表")
    @Parameter(name = "userId", description = "用户ID", required = true)
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<List<AddressDTO>> getUserAddresses(@PathVariable Long userId) {
        return Result.success(addressService.getUserAddresses(userId));
    }

    @Operation(summary = "获取地址详情")
    @Parameter(name = "userId", description = "用户ID", required = true)
    @Parameter(name = "addressId", description = "地址ID", required = true)
    @GetMapping("/{userId}/{addressId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<AddressDTO> getAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        return Result.success(addressService.getAddress(userId, addressId));
    }
} 