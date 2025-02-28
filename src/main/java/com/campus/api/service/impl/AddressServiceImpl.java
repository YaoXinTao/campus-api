package com.campus.api.service.impl;

import com.campus.api.dto.address.AddressDTO;
import com.campus.api.entity.Address;
import com.campus.api.common.exception.BusinessException;
import com.campus.api.mapper.AddressMapper;
import com.campus.api.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;
    private static final int MAX_ADDRESS_COUNT = 20;

    @Override
    public List<AddressDTO> getUserAddresses(Long userId) {
        return addressMapper.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO getAddress(Long userId, Long addressId) {
        Address address = addressMapper.findByIdAndUserId(addressId, userId);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        return convertToDTO(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(Long userId, AddressDTO addressDTO) {
        // 检查地址数量限制
        if (addressMapper.countByUserId(userId) >= MAX_ADDRESS_COUNT) {
            throw new BusinessException("最多只能添加20个地址");
        }
        
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(userId);
        
        // 如果是默认地址，清除其他默认标记
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearOtherDefault(userId, null);
        }
        
        addressMapper.insert(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long userId, AddressDTO addressDTO) {
        if (addressDTO.getId() == null) {
            throw new BusinessException("地址ID不能为空");
        }
        
        // 先获取原有地址信息
        Address existingAddress = addressMapper.findByIdAndUserId(addressDTO.getId(), userId);
        if (existingAddress == null) {
            throw new BusinessException("地址不存在或无权限修改");
        }
        
        // 创建新的地址对象并设置属性
        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(userId);
        
        // 特殊处理可能为空的字段
        address.setPostalCode(addressDTO.getPostalCode() == null ? "" : addressDTO.getPostalCode());
        address.setTag(addressDTO.getTag() == null ? "" : addressDTO.getTag());
        
        // 如果是默认地址，清除其他默认标记
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearOtherDefault(userId, address.getId());
        }
        
        if (addressMapper.update(address) == 0) {
            throw new BusinessException("更新地址失败");
        }
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        if (addressMapper.deleteByIdAndUserId(addressId, userId) == 0) {
            throw new BusinessException("地址不存在或无权限删除");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long addressId) {
        Address address = addressMapper.findByIdAndUserId(addressId, userId);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        
        addressMapper.clearOtherDefault(userId, addressId);
        
        address.setIsDefault(1);
        addressMapper.update(address);
    }

    private AddressDTO convertToDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        BeanUtils.copyProperties(address, dto);
        return dto;
    }
} 