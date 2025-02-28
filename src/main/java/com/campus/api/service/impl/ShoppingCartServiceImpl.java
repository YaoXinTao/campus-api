package com.campus.api.service.impl;

import com.campus.api.entity.ShoppingCart;
import com.campus.api.mapper.ShoppingCartMapper;
import com.campus.api.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToCart(ShoppingCart cart) {
        // 检查是否已存在相同商品
        ShoppingCart existCart = shoppingCartMapper.findByUserIdAndSkuId(cart.getUserId(), cart.getSkuId());
        if (existCart != null) {
            // 已存在则更新数量
            existCart.setQuantity(existCart.getQuantity() + cart.getQuantity());
            shoppingCartMapper.updateQuantityById(existCart.getId(), existCart.getQuantity());
        } else {
            // 不存在则新增
            shoppingCartMapper.insert(cart);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCartItem(ShoppingCart cart) {
        shoppingCartMapper.updateCartItem(cart);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Long cartId, Integer quantity) {
        shoppingCartMapper.updateQuantity(cartId, quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromCart(Long cartId) {
        shoppingCartMapper.deleteById(cartId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSelected(Long cartId, Integer selected) {
        shoppingCartMapper.updateSelected(cartId, selected);
    }

    @Override
    public List<ShoppingCart> getUserCart(Long userId) {
        return shoppingCartMapper.findByUserId(userId);
    }
} 