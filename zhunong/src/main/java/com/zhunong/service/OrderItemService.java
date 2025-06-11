package com.zhunong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhunong.entity.OrderItem;

import java.util.List;
import java.util.Map;

public interface OrderItemService extends IService<OrderItem> {
    // 获取农户销售排行
    List<Map<String, Object>> getTopFarmers(int limit);
    
    // 获取商品销售排行
    List<Map<String, Object>> getTopProducts(int limit);
    
    // 获取分类销售占比数据
    List<Map<String, Object>> getCategorySales();
} 