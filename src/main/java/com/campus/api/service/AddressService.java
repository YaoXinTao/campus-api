package com.campus.api.service;

import com.campus.api.dto.address.AddressDTO;
import java.util.List;

public interface AddressService {
    
    List<AddressDTO> getUserAddresses(Long userId);
    
    AddressDTO getAddress(Long userId, Long addressId);
    
    void addAddress(Long userId, AddressDTO addressDTO);
    
    void updateAddress(Long userId, AddressDTO addressDTO);
    
    void deleteAddress(Long userId, Long addressId);
    
    void setDefaultAddress(Long userId, Long addressId);
} 