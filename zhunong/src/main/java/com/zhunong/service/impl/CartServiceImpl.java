package com.zhunong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhunong.entity.CartItem;
import com.zhunong.entity.Product;
import com.zhunong.mapper.CartItemMapper;
import com.zhunong.service.CartService;
import com.zhunong.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CartItemMapper cartItemMapper;

    @Override
    public void addItem(Long employeeId, Long productId, Integer quantity) {
        try {
            logger.info("Adding item to cart: employeeId={}, productId={}, quantity={}", 
                    employeeId, productId, quantity);
            
            // 获取商品信息
            Product product = productService.getById(productId);
            if (product == null || product.getStatus() != 1) {
                logger.error("Product not found or inactive: {}", productId);
                throw new RuntimeException("商品不存在或已下架");
            }
            
            // 检查购物车中是否已存在该商品
            CartItem existingItem = cartItemMapper.getCartItemByEmployeeIdAndProductId(employeeId, productId);
            logger.info("Existing item in database: {}", existingItem);
            
            LocalDateTime now = LocalDateTime.now();
            
            if (existingItem != null) {
                // 如果购物车中已存在该商品，则更新数量
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                existingItem.setSubtotal(existingItem.getPrice().multiply(new BigDecimal(existingItem.getQuantity())));
                existingItem.setUpdateTime(now);
                logger.info("Updated existing item quantity to: {}", existingItem.getQuantity());
                
                // 更新数据库
                cartItemMapper.updateById(existingItem);
            } else {
                // 否则添加新商品
                CartItem cartItem = new CartItem();
                cartItem.setEmployeeId(employeeId);
                cartItem.setProductId(productId);
                cartItem.setProductName(product.getName());
                cartItem.setProductImage(product.getImage());
                cartItem.setFarmerId(product.getFarmerId());
                cartItem.setFarmerName(product.getFarmerName());
                cartItem.setPrice(product.getPrice());
                cartItem.setQuantity(quantity);
                cartItem.setSubtotal(product.getPrice().multiply(new BigDecimal(quantity)));
                cartItem.setCreateTime(now);
                cartItem.setUpdateTime(now);
                logger.info("Created new cart item: {}", cartItem);
                
                // 保存到数据库
                cartItemMapper.insert(cartItem);
            }
            
            logger.info("Item saved to database successfully");
        } catch (Exception e) {
            logger.error("Error in addItem: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void updateItemQuantity(Long employeeId, Long productId, Integer quantity) {
        try {
            logger.info("Updating item quantity in cart: employeeId={}, productId={}, newQuantity={}", 
                    employeeId, productId, quantity);
            
            // 从数据库获取商品
            CartItem cartItem = cartItemMapper.getCartItemByEmployeeIdAndProductId(employeeId, productId);
            
            if (cartItem == null) {
                logger.error("Item not found in cart: employeeId={}, productId={}", employeeId, productId);
                throw new RuntimeException("购物车中不存在该商品");
            }
            
            if (quantity <= 0) {
                // 如果数量小于等于0，则从购物车中移除
                removeItem(employeeId, productId);
                return;
            }
            
            // 更新数量和小计
            cartItem.setQuantity(quantity);
            cartItem.setSubtotal(cartItem.getPrice().multiply(new BigDecimal(quantity)));
            cartItem.setUpdateTime(LocalDateTime.now());
            
            // 更新数据库
            cartItemMapper.updateById(cartItem);
            logger.info("Item quantity updated in database successfully");
        } catch (Exception e) {
            logger.error("Error in updateItemQuantity: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void removeItem(Long employeeId, Long productId) {
        try {
            logger.info("Removing item from cart: employeeId={}, productId={}", employeeId, productId);
            
            // 从数据库删除
            LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CartItem::getEmployeeId, employeeId)
                   .eq(CartItem::getProductId, productId);
            cartItemMapper.delete(wrapper);
            logger.info("Item removed from database successfully");
        } catch (Exception e) {
            logger.error("Error in removeItem: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void clearCart(Long employeeId) {
        try {
            logger.info("Clearing cart for employeeId={}", employeeId);
            
            // 从数据库删除
            LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CartItem::getEmployeeId, employeeId);
            cartItemMapper.delete(wrapper);
            logger.info("Cart cleared from database successfully");
        } catch (Exception e) {
            logger.error("Error in clearCart: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<CartItem> getCartItems(Long employeeId) {
        try {
            logger.info("Getting cart items for employeeId={}", employeeId);
            
            // 从数据库获取
            List<CartItem> cartItems = cartItemMapper.getCartItemsByEmployeeId(employeeId);
            logger.info("Retrieved {} items from database cart", cartItems.size());
            
            // 记录购物车内容
            for (CartItem item : cartItems) {
                logger.info("Cart item: productId={}, name={}, quantity={}", 
                        item.getProductId(), item.getProductName(), item.getQuantity());
            }
            
            return cartItems;
        } catch (Exception e) {
            logger.error("Error in getCartItems: {}", e.getMessage(), e);
            // 返回空列表，避免应用崩溃
            return new ArrayList<>();
        }
    }
} 