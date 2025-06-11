package com.zhunong.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhunong.entity.Product;

public interface ProductService extends IService<Product> {
    // 添加商品
    void addProduct(Product product);
    
    // 获取已上架商品列表
    IPage<Product> getProductList(String category, Page<Product> page);
    
    // 获取已上架商品列表（带筛选和排序）
    IPage<Product> getProductListWithFilter(String category, String keyword, 
                                          java.math.BigDecimal minPrice, 
                                          java.math.BigDecimal maxPrice, 
                                          String sort, 
                                          Page<Product> page);
    
    // 获取分类统计信息
    java.util.Map<String, Integer> getCategoryStats();
    
    // 获取农户的商品列表
    IPage<Product> getFarmerProducts(Long farmerId, Page<Product> page);
    
    // 获取已审核商品
    IPage<Product> getApprovedProducts(Page<Product> page);
    
    // 按分类获取商品
    IPage<Product> getProductsByCategory(String category, Page<Product> page);
    
    // 审核商品
    void reviewProduct(Long id, boolean approved);
    
    // 更新商品状态
    void updateStatus(Long productId, Integer status);
    
    // 更新库存
    void updateStock(Long productId, Integer change);
    
    // 搜索商品（支持名称、描述、农户名称模糊查询）
    IPage<Product> searchProducts(String category, String keyword, Page<Product> page);
} 