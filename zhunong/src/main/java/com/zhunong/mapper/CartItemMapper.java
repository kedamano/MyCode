package com.zhunong.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhunong.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
    
    /**
     * 根据职工ID获取购物车商品
     */
    @Select("SELECT * FROM cart_items WHERE employee_id = #{employeeId}")
    List<CartItem> getCartItemsByEmployeeId(@Param("employeeId") Long employeeId);
    
    /**
     * 根据职工ID和商品ID获取购物车商品
     */
    @Select("SELECT * FROM cart_items WHERE employee_id = #{employeeId} AND product_id = #{productId}")
    CartItem getCartItemByEmployeeIdAndProductId(@Param("employeeId") Long employeeId, @Param("productId") Long productId);
    
    /**
     * 清空指定职工的购物车
     */
    @Select("DELETE FROM cart_items WHERE employee_id = #{employeeId}")
    void clearCartByEmployeeId(@Param("employeeId") Long employeeId);
} 