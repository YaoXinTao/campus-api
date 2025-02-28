package com.campus.api.service;

import com.campus.api.entity.ShoppingCart;
import java.util.List;

public interface ShoppingCartService {
    
    /**
     * 添加商品到购物车
     */
    void addToCart(ShoppingCart cart);
    
    /**
     * 更新购物车商品
     */
    void updateCartItem(ShoppingCart cart);
    
    /**
     * 更新购物车商品数量
     */
    void updateQuantity(Long cartId, Integer quantity);
    
    /**
     * 删除购物车商品
     */
    void removeFromCart(Long cartId);
    
    /**
     * 更新商品选中状态
     */
    void updateSelected(Long cartId, Integer selected);
    
    /**
     * 获取用户购物车列表
     */
    List<ShoppingCart> getUserCart(Long userId);
} 