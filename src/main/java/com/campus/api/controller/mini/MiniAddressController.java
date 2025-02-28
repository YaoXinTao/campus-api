package com.campus.api.controller.mini;

import com.campus.api.common.Result;
import com.campus.api.dto.address.AddressDTO;
import com.campus.api.service.AddressService;
import com.campus.api.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "小程序收货地址接口", description = "小程序收货地址相关接口")
@RestController
@RequestMapping("/api/v1/mini/address")
@RequiredArgsConstructor
public class MiniAddressController {

    private final AddressService addressService;

    @Operation(summary = "获取用户地址列表")
    @GetMapping("/list")
    public Result<List<AddressDTO>> getAddressList() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(addressService.getUserAddresses(userId));
    }

    @Operation(summary = "获取地址详情")
    @Parameter(name = "id", description = "地址ID", required = true)
    @GetMapping("/{id}")
    public Result<AddressDTO> getAddress(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(addressService.getAddress(userId, id));
    }

    @Operation(summary = "新增收货地址")
    @PostMapping
    public Result<Void> addAddress(@Validated @RequestBody AddressDTO addressDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        addressService.addAddress(userId, addressDTO);
        return Result.success();
    }

    @Operation(summary = "修改收货地址")
    @PutMapping
    public Result<Void> updateAddress(@Validated @RequestBody AddressDTO addressDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        addressService.updateAddress(userId, addressDTO);
        return Result.success();
    }

    @Operation(summary = "删除收货地址")
    @Parameter(name = "id", description = "地址ID", required = true)
    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        addressService.deleteAddress(userId, id);
        return Result.success();
    }

    @Operation(summary = "设置默认地址")
    @Parameter(name = "id", description = "地址ID", required = true)
    @PutMapping("/{id}/default")
    public Result<Void> setDefaultAddress(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        addressService.setDefaultAddress(userId, id);
        return Result.success();
    }
} 