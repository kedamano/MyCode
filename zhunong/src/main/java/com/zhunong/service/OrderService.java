package com.zhunong.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhunong.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {
    // 创建订单
    Order createOrder(Long employeeId, List<Map<String, Object>> items, String address, String phone, String remark);
    
    // 获取职工的订单列表
    IPage<Order> getEmployeeOrders(Long employeeId, Page<Order> page);
    
    // 获取职工的订单列表（带筛选条件）
    IPage<Order> getEmployeeOrdersWithFilter(Long employeeId, String status, String startDate, String endDate, String keyword, Page<Order> page);
    
    // 获取农户的订单列表
    IPage<Order> getFarmerOrders(Long farmerId, Page<Order> page);
    
    // 获取所有订单（管理员用）
    IPage<Order> getAllOrders(Integer status, Page<Order> page);
    
    // 发货
    void ship(Long orderId);
    
    // 确认收货
    void confirm(Long orderId);
    
    // 取消订单
    void cancel(Long orderId);
    
    // 获取订单详情
    Order getOrderDetail(Long orderId);
    
    // 通过订单号获取订单
    Order getByOrderNo(String orderNo);
    
    // 计算总销售额
    BigDecimal calculateTotalSales();
    
    // 获取每日销售数据
    List<Map<String, Object>> getDailySales(LocalDate startDate, LocalDate endDate);
    
    // 获取职工购买排行
    List<Map<String, Object>> getTopEmployees(int limit);
    
    // 获取总销售额
    BigDecimal getTotalSales();
    
    // 按状态统计订单数量
    long countByStatus(String status);
    
    // 获取销售趋势数据
    List<Map<String, Object>> getSalesTrend(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 支付订单
     * @param orderId 订单ID
     * @return 是否支付成功
     */
    boolean payOrder(Long orderId);
} 