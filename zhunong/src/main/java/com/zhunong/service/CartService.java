package com.zhunong.service;

import com.zhunong.entity.CartItem;

import java.util.List;

public interface CartService {
    // 添加商品到购物车
    void addItem(Long employeeId, Long productId, Integer quantity);
    
    // 更新购物车中商品数量
    void updateItemQuantity(Long employeeId, Long productId, Integer quantity);
    
    // 从购物车中移除商品
    void removeItem(Long employeeId, Long productId);
    
    // 清空购物车
    void clearCart(Long employeeId);
    
    // 获取购物车中的所有商品
    List<CartItem> getCartItems(Long employeeId);
} 