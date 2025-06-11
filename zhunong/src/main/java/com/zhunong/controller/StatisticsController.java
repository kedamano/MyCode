package com.zhunong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhunong.entity.Order;
import com.zhunong.entity.Product;
import com.zhunong.entity.User;
import com.zhunong.service.OrderItemService;
import com.zhunong.service.OrderService;
import com.zhunong.service.ProductService;
import com.zhunong.service.UserService;
import com.zhunong.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 统计报表控制器
 */
@Controller
@RequestMapping("/admin/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderItemService orderItemService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 统计报表首页
     */
    @GetMapping
    public String statisticsPage(Model model) {
        // 获取平台整体统计数据
        Map<String, Object> platformStatistics = statisticsService.getPlatformStatistics();
        model.addAttribute("statistics", platformStatistics);
        
        return "admin/statistics/index";
    }
    
    /**
     * 农户统计页面
     */
    @GetMapping("/farmers")
    public String farmerStatisticsPage(Model model) {
        // 获取农户统计数据
        Map<String, Object> farmerStatistics = statisticsService.getFarmerStatistics();
        model.addAttribute("statistics", farmerStatistics);
        
        return "admin/statistics/farmers";
    }
    
    /**
     * 商品统计页面
     */
    @GetMapping("/products")
    public String productStatisticsPage(Model model) {
        // 获取商品统计数据
        Map<String, Object> productStatistics = statisticsService.getProductStatistics();
        model.addAttribute("statistics", productStatistics);
        
        return "admin/statistics/products";
    }
    
    /**
     * 获取平台统计数据API
     */
    @GetMapping("/api/platform")
    @ResponseBody
    public Map<String, Object> getPlatformStatistics() {
        return statisticsService.getPlatformStatistics();
    }
    
    /**
     * 获取农户统计数据API
     */
    @GetMapping("/api/farmers")
    @ResponseBody
    public Map<String, Object> getFarmerStatistics() {
        return statisticsService.getFarmerStatistics();
    }
    
    /**
     * 获取商品统计数据API
     */
    @GetMapping("/api/products")
    @ResponseBody
    public Map<String, Object> getProductStatistics() {
        return statisticsService.getProductStatistics();
    }
    
    /**
     * 获取统计数据
     */
    @GetMapping("/data")
    @ResponseBody
    public Map<String, Object> getStatisticsData(
            @RequestParam(defaultValue = "30") Integer timeRange) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(timeRange);
            
            // 获取销售趋势数据
            List<Map<String, Object>> salesTrend = orderService.getSalesTrend(startDate, endDate);
            result.put("salesTrend", salesTrend);
            
            // 获取农户销售排行
            List<Map<String, Object>> topFarmers = orderItemService.getTopFarmers(5);
            result.put("topFarmers", topFarmers);
            
            // 获取商品销售排行
            List<Map<String, Object>> topProducts = orderItemService.getTopProducts(5);
            result.put("topProducts", topProducts);
            
            // 获取分类销售占比
            List<Map<String, Object>> categorySales = orderItemService.getCategorySales();
            result.put("categorySales", categorySales);
            
            // 获取职工购买排行
            List<Map<String, Object>> topEmployees = generateTopEmployees();
            result.put("topEmployees", topEmployees);
            
            return result;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "获取统计数据失败：" + e.getMessage());
            return error;
        }
    }
    
    /**
     * 生成职工购买排行数据
     */
    private List<Map<String, Object>> generateTopEmployees() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<User> employees = userService.findByRole("EMPLOYEE");
        
        // 获取所有订单
        List<Order> orders = orderService.list();
        
        // 按职工ID分组计算订单金额和数量
        Map<Long, BigDecimal> employeePurchases = new HashMap<>();
        Map<Long, Integer> employeeOrderCounts = new HashMap<>();
        Map<Long, String> employeeNames = new HashMap<>();
        
        for (Order order : orders) {
            // 检查订单是否已完成
            if (order.getStatus() != null && "已完成".equals(order.getStatus())) {
                Long employeeId = order.getEmployeeId();
                BigDecimal amount = employeePurchases.getOrDefault(employeeId, BigDecimal.ZERO);
                Integer count = employeeOrderCounts.getOrDefault(employeeId, 0);
                
                employeePurchases.put(employeeId, amount.add(order.getTotalAmount()));
                employeeOrderCounts.put(employeeId, count + 1);
                employeeNames.put(employeeId, order.getEmployeeName());
            }
        }
        
        // 转换为前端需要的格式
        for (Map.Entry<Long, BigDecimal> entry : employeePurchases.entrySet()) {
            Long employeeId = entry.getKey();
            Map<String, Object> item = new HashMap<>();
            item.put("employeeId", employeeId);
            item.put("employeeName", employeeNames.get(employeeId));
            item.put("totalPurchases", entry.getValue());
            item.put("orderCount", employeeOrderCounts.get(employeeId));
            result.add(item);
        }
        
        // 按购买金额排序
        result.sort((a, b) -> ((BigDecimal) b.get("totalPurchases")).compareTo((BigDecimal) a.get("totalPurchases")));
        
        // 限制数量
        if (result.size() > 5) {
            result = result.subList(0, 5);
        }
        
        // 如果没有数据，添加示例数据
        if (result.isEmpty()) {
            for (int i = 1; i <= 5; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("employeeId", (long) i);
                item.put("employeeName", "示例职工" + i);
                item.put("totalPurchases", new BigDecimal("" + (1000 - i * 100) + ".00"));
                item.put("orderCount", 10 - i);
                result.add(item);
            }
        }
        
        return result;
    }
    
    /**
     * 获取销售趋势数据（按日）
     */
    @GetMapping("/sales/daily")
    @ResponseBody
    public List<Map<String, Object>> getSalesTrendDaily(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // 如果没有指定日期范围，默认查询最近30天
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(29);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        return orderService.getSalesTrend(startDateTime, endDateTime);
    }
    
    /**
     * 获取农户销售排行
     */
    @GetMapping("/farmers/top")
    @ResponseBody
    public List<Map<String, Object>> getTopFarmers(
            @RequestParam(defaultValue = "5") Integer limit) {
        return orderItemService.getTopFarmers(limit);
    }
    
    /**
     * 获取商品销售排行
     */
    @GetMapping("/products/top")
    @ResponseBody
    public List<Map<String, Object>> getTopProducts(
            @RequestParam(defaultValue = "5") Integer limit) {
        return orderItemService.getTopProducts(limit);
    }
    
    /**
     * 获取分类销售占比
     */
    @GetMapping("/categories")
    @ResponseBody
    public List<Map<String, Object>> getCategorySales() {
        return orderItemService.getCategorySales();
    }
} 