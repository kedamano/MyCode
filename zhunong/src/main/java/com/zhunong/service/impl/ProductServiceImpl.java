package com.zhunong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhunong.entity.Product;
import com.zhunong.entity.User;
import com.zhunong.mapper.ProductMapper;
import com.zhunong.service.ProductService;
import com.zhunong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private UserService userService;
    
    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional
    public void addProduct(Product product) {
        // 验证农户
        User farmer = userService.getById(product.getFarmerId());
        if (farmer == null || !"FARMER".equals(farmer.getRole()) || farmer.getStatus() != 1) {
            throw new RuntimeException("农户不存在或未通过审核");
        }

        // 设置商品状态为待审核
        product.setStatus(0);
        product.setFarmerName(farmer.getRealName());
        
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        
        assert product.getUpdateTime() != null : "updateTime must not be null";
        save(product);
    }

    @Override
    public IPage<Product> getProductList(String category, Page<Product> page) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1) // 只查询已上架的商品
                .eq(StringUtils.hasText(category), Product::getCategory, category)
                .orderByDesc(Product::getCreateTime);
        return page(page, queryWrapper);
    }

    @Override
    public IPage<Product> getFarmerProducts(Long farmerId, Page<Product> page) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getFarmerId, farmerId)
                .orderByDesc(Product::getCreateTime);
        return page(page, queryWrapper);
    }
    
    @Override
    public IPage<Product> getApprovedProducts(Page<Product> page) {
        return page(page, new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreateTime));
    }

    @Override
    public IPage<Product> getProductsByCategory(String category, Page<Product> page) {
        return page(page, new LambdaQueryWrapper<Product>()
                .eq(Product::getCategory, category)
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreateTime));
    }

    @Override
    @Transactional
    public void reviewProduct(Long id, boolean approved) {
        Product product = getById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setStatus(approved ? 1 : 2);
        product.setUpdateTime(LocalDateTime.now());
        updateById(product);
    }

    @Override
    @Transactional
    public void updateStatus(Long productId, Integer status) {
        Product product = getById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        product.setStatus(status);
        product.setUpdateTime(LocalDateTime.now());
        updateById(product);
    }

    @Override
    @Transactional
    public void updateStock(Long productId, Integer change) {
        Product product = getById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        int newStock = product.getStock() + change;
        if (newStock < 0) {
            throw new RuntimeException("库存不足");
        }
        
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, productId)
                .set(Product::getStock, newStock)
                .set(Product::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);
    }

    @Override
    public IPage<Product> searchProducts(String category, String keyword, Page<Product> page) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1);
        if (org.springframework.util.StringUtils.hasText(category)) {
            queryWrapper.eq(Product::getCategory, category);
        }
        if (org.springframework.util.StringUtils.hasText(keyword)) {
            queryWrapper.and(q -> q
                .like(Product::getName, keyword)
                .or().like(Product::getDescription, keyword)
                .or().like(Product::getFarmerName, keyword)
            );
        }
        queryWrapper.orderByDesc(Product::getCreateTime);
        return page(page, queryWrapper);
    }

    @Override
    public IPage<Product> getProductListWithFilter(String category, String keyword, 
                                               java.math.BigDecimal minPrice, 
                                               java.math.BigDecimal maxPrice, 
                                               String sort, 
                                               Page<Product> page) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1); // 只查询已上架的商品
                
        // 分类筛选
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(Product::getCategory, category);
        }
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(q -> q
                .like(Product::getName, keyword)
                .or().like(Product::getDescription, keyword)
                .or().like(Product::getFarmerName, keyword)
            );
        }
        
        // 价格区间筛选
        if (minPrice != null) {
            queryWrapper.ge(Product::getPrice, minPrice);
        }
        
        if (maxPrice != null) {
            queryWrapper.le(Product::getPrice, maxPrice);
        }
        
        // 排序方式
        switch (sort) {
            case "price-asc":
                queryWrapper.orderByAsc(Product::getPrice);
                break;
            case "price-desc":
                queryWrapper.orderByDesc(Product::getPrice);
                break;
            case "newest":
                queryWrapper.orderByDesc(Product::getCreateTime);
                break;
            case "sales":
                queryWrapper.orderByDesc(Product::getSalesCount);
                break;
            default:
                queryWrapper.orderByDesc(Product::getCreateTime);
                break;
        }
        
        return page(page, queryWrapper);
    }

    @Override
    public java.util.Map<String, Integer> getCategoryStats() {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        
        // 获取所有已上架商品的分类统计
        java.util.List<Product> products = list(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1));
                
        // 按分类统计商品数量
        for (Product product : products) {
            String category = product.getCategory();
            if (StringUtils.hasText(category)) {
                stats.put(category, stats.getOrDefault(category, 0) + 1);
            }
        }
        
        return stats;
    }
}