package com.zhunong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhunong.entity.OrderItem;
import com.zhunong.entity.Product;
import com.zhunong.mapper.OrderItemMapper;
import com.zhunong.service.OrderItemService;
import com.zhunong.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
    
    @Autowired
    private ProductService productService;
    
    @Override
    public List<Map<String, Object>> getTopFarmers(int limit) {
        // 获取所有订单项
        List<OrderItem> orderItems = list();
        
        // 按农户ID分组计算销售金额
        Map<Long, BigDecimal> farmerSales = new HashMap<>();
        Map<Long, String> farmerNames = new HashMap<>();
        
        for (OrderItem item : orderItems) {
            Long farmerId = item.getFarmerId();
            BigDecimal amount = farmerSales.getOrDefault(farmerId, BigDecimal.ZERO);
            farmerSales.put(farmerId, amount.add(item.getSubtotal()));
            farmerNames.put(farmerId, item.getFarmerName());
        }
        
        // 转换为前端需要的格式并排序
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : farmerSales.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("farmerId", entry.getKey());
            item.put("farmerName", farmerNames.get(entry.getKey()));
            item.put("totalSales", entry.getValue());
            result.add(item);
        }
        
        // 按销售金额排序并限制数量
        result.sort((a, b) -> ((BigDecimal) b.get("totalSales")).compareTo((BigDecimal) a.get("totalSales")));
        if (result.size() > limit) {
            result = result.subList(0, limit);
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getTopProducts(int limit) {
        // 获取所有订单项
        List<OrderItem> orderItems = list();
        
        // 按商品ID分组计算销售数量和金额
        Map<Long, Integer> productQuantities = new HashMap<>();
        Map<Long, BigDecimal> productSales = new HashMap<>();
        Map<Long, String> productNames = new HashMap<>();
        
        for (OrderItem item : orderItems) {
            Long productId = item.getProductId();
            Integer quantity = productQuantities.getOrDefault(productId, 0);
            BigDecimal sales = productSales.getOrDefault(productId, BigDecimal.ZERO);
            
            productQuantities.put(productId, quantity + item.getQuantity());
            productSales.put(productId, sales.add(item.getSubtotal()));
            productNames.put(productId, item.getProductName());
        }
        
        // 转换为前端需要的格式并排序
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Map<String, Object> item = new HashMap<>();
            item.put("productId", productId);
            item.put("productName", productNames.get(productId));
            item.put("quantity", entry.getValue());
            item.put("totalSales", productSales.get(productId));
            result.add(item);
        }
        
        // 按销售金额排序并限制数量
        result.sort((a, b) -> ((BigDecimal) b.get("totalSales")).compareTo((BigDecimal) a.get("totalSales")));
        if (result.size() > limit) {
            result = result.subList(0, limit);
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getCategorySales() {
        // 获取所有订单项
        List<OrderItem> orderItems = list();
        
        // 获取所有商品信息，用于获取商品分类
        List<Product> products = productService.list();
        Map<Long, String> productCategories = products.stream()
            .collect(Collectors.toMap(Product::getId, Product::getCategory));
        
        // 按分类分组计算销售金额
        Map<String, BigDecimal> categorySales = new HashMap<>();
        
        for (OrderItem item : orderItems) {
            String category = productCategories.getOrDefault(item.getProductId(), "其他");
            BigDecimal amount = categorySales.getOrDefault(category, BigDecimal.ZERO);
            categorySales.put(category, amount.add(item.getSubtotal()));
        }
        
        // 转换为前端需要的格式并排序
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : categorySales.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("category", entry.getKey());
            item.put("totalSales", entry.getValue());
            result.add(item);
        }
        
        // 按销售金额排序
        result.sort((a, b) -> ((BigDecimal) b.get("totalSales")).compareTo((BigDecimal) a.get("totalSales")));
        
        return result;
    }
} 