package com.zhunong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhunong.entity.Order;
import com.zhunong.entity.OrderItem;
import com.zhunong.entity.Product;
import com.zhunong.entity.User;
import com.zhunong.mapper.OrderMapper;
import com.zhunong.service.OrderItemService;
import com.zhunong.service.OrderService;
import com.zhunong.service.ProductService;
import com.zhunong.service.UserService;
import com.zhunong.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderItemService orderItemService;
    
    @Autowired
    private TransactionService transactionService;

    @Override
    @Transactional
    public Order createOrder(Long employeeId, List<Map<String, Object>> items, String address, String phone, String remark) {
        // 验证职工
        User employee = userService.getById(employeeId);
        if (employee == null || !"EMPLOYEE".equals(employee.getRole())) {
            throw new RuntimeException("职工不存在");
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setEmployeeId(employeeId);
        order.setEmployeeName(employee.getRealName());
        order.setAddress(address);
        order.setPhone(phone);
        order.setRemark(remark);
        order.setStatus("待付款");
        
        // 计算总金额并创建订单项
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (Map<String, Object> item : items) {
            Long productId = Long.parseLong(item.get("productId").toString());
            Integer quantity = Integer.parseInt(item.get("quantity").toString());
            
            Product product = productService.getById(productId);
            if (product == null || product.getStatus() != 1) {
                throw new RuntimeException("商品不存在或已下架");
            }
            if (product.getStock() < quantity) {
                throw new RuntimeException("商品库存不足");
            }
            
            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(productId);
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage());
            orderItem.setFarmerId(product.getFarmerId());
            orderItem.setFarmerName(product.getFarmerName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(quantity);
            orderItem.setSubtotal(product.getPrice().multiply(new BigDecimal(quantity)));
            
            totalAmount = totalAmount.add(orderItem.getSubtotal());
            orderItems.add(orderItem);
            
            // 更新库存
            productService.updateStock(productId, -quantity);
        }
        
        order.setTotalAmount(totalAmount);
        save(order);
        
        // 保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemService.save(item);
        }
        
        order.setItems(orderItems);
        return order;
    }

    @Override
    public IPage<Order> getEmployeeOrders(Long employeeId, Page<Order> page) {
        IPage<Order> orders = page(page, new LambdaQueryWrapper<Order>()
                .eq(Order::getEmployeeId, employeeId)
                .orderByDesc(Order::getCreateTime));
                
        // 加载订单项
        orders.getRecords().forEach(order -> {
            List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));
            order.setItems(items);
        });
        
        return orders;
    }

    @Override
    public IPage<Order> getFarmerOrders(Long farmerId, Page<Order> page) {
        // 获取包含该农户商品的所有订单ID
        List<Long> orderIds = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getFarmerId, farmerId))
                .stream()
                .map(OrderItem::getOrderId)
                .distinct()
                .collect(Collectors.toList());
                
        if (orderIds.isEmpty()) {
            return new Page<>();
        }
        
        IPage<Order> orders = page(page, new LambdaQueryWrapper<Order>()
                .in(Order::getId, orderIds)
                .orderByDesc(Order::getCreateTime));
                
        // 只加载该农户的订单项
        orders.getRecords().forEach(order -> {
            List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId())
                    .eq(OrderItem::getFarmerId, farmerId));
            order.setItems(items);
        });
        
        return orders;
    }
    
    @Override
    public IPage<Order> getAllOrders(Integer status, Page<Order> page) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<Order>()
                .orderByDesc(Order::getCreateTime);
        
        // 根据状态码筛选
        if (status != null) {
            String statusText;
            switch (status) {
                case 0: statusText = "待付款"; break;
                case 1: statusText = "已发货"; break;
                case 2: statusText = "已收货"; break;
                case 3: statusText = "已取消"; break;
                case 4: statusText = "已付款"; break;
                default: statusText = null;
            }
            
            if (statusText != null) {
                log.info("Filtering orders by status: {}", statusText);
                queryWrapper.eq(Order::getStatus, statusText);
            }
        }
        
        IPage<Order> orders = page(page, queryWrapper);
        
        // 加载所有订单项
        orders.getRecords().forEach(order -> {
            List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));
            order.setItems(items);
        });
        
        return orders;
    }

    @Override
    @Transactional
    public void ship(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"已付款".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus("已发货");
        updateById(order);
    }

    @Override
    @Transactional
    public void confirm(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"已发货".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus("已收货");
        updateById(order);
    }

    @Override
    @Transactional
    public void cancel(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"待付款".equals(order.getStatus()) && !"已付款".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }
        
        // 恢复库存
        List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            productService.updateStock(item.getProductId(), item.getQuantity());
        }
        
        order.setStatus("已取消");
        updateById(order);
    }

    @Override
    public Order getOrderDetail(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        order.setItems(items);
        
        return order;
    }
    
    @Override
    public Order getByOrderNo(String orderNo) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            throw new IllegalArgumentException("订单号不能为空");
        }
        
        try {
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<Order>()
                    .eq(Order::getOrderNo, orderNo.trim());
            
            Order order = getOne(queryWrapper);
            
            if (order != null) {
                // 加载订单项
                List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, order.getId()));
                order.setItems(items);
            } else {
                log.warn("No order found with orderNo: {}", orderNo);
            }
            
            return order;
        } catch (Exception e) {
            log.error("Error retrieving order by orderNo {}: {}", orderNo, e.getMessage(), e);
            throw new RuntimeException("根据订单号查询订单失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public BigDecimal calculateTotalSales() {
        // 获取所有已完成订单
        List<Order> completedOrders = list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, "已收货"));
                
        // 计算总销售额
        return completedOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public List<Map<String, Object>> getDailySales(LocalDate startDate, LocalDate endDate) {
        // 获取日期范围内的已完成订单
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        List<Order> orders = list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, "已收货")
                .ge(Order::getCreateTime, startDateTime)
                .lt(Order::getCreateTime, endDateTime));
                
        // 按日期分组计算销售额
        Map<String, BigDecimal> dailySales = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Order order : orders) {
            String date = order.getCreateTime().format(formatter);
            BigDecimal amount = dailySales.getOrDefault(date, BigDecimal.ZERO);
            dailySales.put(date, amount.add(order.getTotalAmount()));
        }
        
        // 转换为前端需要的格式
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : dailySales.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("sales", entry.getValue());
            result.add(item);
        }
        
        // 按日期排序
        result.sort(Comparator.comparing(map -> (String) map.get("date")));
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getTopEmployees(int limit) {
        // 获取所有已完成订单
        List<Order> completedOrders = list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, "已收货"));
                
        // 按职工ID分组计算购买金额
        Map<Long, BigDecimal> employeeSales = new HashMap<>();
        Map<Long, String> employeeNames = new HashMap<>();
        
        for (Order order : completedOrders) {
            Long employeeId = order.getEmployeeId();
            BigDecimal amount = employeeSales.getOrDefault(employeeId, BigDecimal.ZERO);
            employeeSales.put(employeeId, amount.add(order.getTotalAmount()));
            employeeNames.put(employeeId, order.getEmployeeName());
        }
        
        // 转换为前端需要的格式并排序
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : employeeSales.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("employeeId", entry.getKey());
            item.put("employeeName", employeeNames.get(entry.getKey()));
            item.put("totalAmount", entry.getValue());
            result.add(item);
        }
        
        // 按购买金额排序并限制数量
        result.sort((a, b) -> ((BigDecimal) b.get("totalAmount")).compareTo((BigDecimal) a.get("totalAmount")));
        if (result.size() > limit) {
            result = result.subList(0, limit);
        }
        
        return result;
    }
    
    @Override
    public BigDecimal getTotalSales() {
        // 获取所有已完成订单的总销售额
        List<Order> completedOrders = list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, "已收货"));
        
        if (completedOrders.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return completedOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public long countByStatus(String status) {
        return count(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, status));
    }

    @Override
    public List<Map<String, Object>> getSalesTrend(LocalDateTime startDate, LocalDateTime endDate) {
        // 获取指定时间范围内的已完成订单
        List<Order> orders = list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, "已收货")
                .between(Order::getCreateTime, startDate, endDate)
                .orderByAsc(Order::getCreateTime));
        
        // 按日期分组统计
        Map<String, List<Order>> ordersByDate = orders.stream()
                .collect(Collectors.groupingBy(order -> 
                    order.getCreateTime().format(DateTimeFormatter.ofPattern("M/d"))));
        
        // 生成结果
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 确保每一天都有数据
        LocalDateTime current = startDate;
        while (!current.isAfter(endDate)) {
            String dateStr = current.format(DateTimeFormatter.ofPattern("M/d"));
            List<Order> dayOrders = ordersByDate.getOrDefault(dateStr, Collections.emptyList());
            
            BigDecimal amount = dayOrders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", dateStr);
            item.put("amount", amount);
            item.put("count", dayOrders.size());
            
            result.add(item);
            
            current = current.plusDays(1);
        }
        
        return result;
    }
    
    @Override
    public IPage<Order> getEmployeeOrdersWithFilter(Long employeeId, String status, String startDateStr, String endDateStr, String keyword, Page<Order> page) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getEmployeeId, employeeId)
                .orderByDesc(Order::getCreateTime);
        
        // 按订单状态筛选
        if (status != null && !status.isEmpty()) {
            log.info("Filtering by status: {}", status);
            queryWrapper.eq(Order::getStatus, status);
        }
        
        // 按日期范围筛选
        if (startDateStr != null && !startDateStr.isEmpty()) {
            try {
                LocalDate startDate = LocalDate.parse(startDateStr);
                LocalDateTime startDateTime = startDate.atStartOfDay();
                log.info("Filtering by start date: {}", startDateTime);
                queryWrapper.ge(Order::getCreateTime, startDateTime);
            } catch (Exception e) {
                log.error("Invalid start date format: {}", startDateStr);
            }
        }
        
        if (endDateStr != null && !endDateStr.isEmpty()) {
            try {
                LocalDate endDate = LocalDate.parse(endDateStr);
                LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
                log.info("Filtering by end date: {}", endDateTime);
                queryWrapper.lt(Order::getCreateTime, endDateTime);
            } catch (Exception e) {
                log.error("Invalid end date format: {}", endDateStr);
            }
        }
        
        // 按关键词搜索订单号或包含的商品
        if (keyword != null && !keyword.isEmpty()) {
            log.info("Searching by keyword: {}", keyword);
            
            // 获取订单号匹配的订单IDs
            List<Long> orderIdsByOrderNo = list(new LambdaQueryWrapper<Order>()
                    .eq(Order::getEmployeeId, employeeId)
                    .like(Order::getOrderNo, keyword))
                    .stream()
                    .map(Order::getId)
                    .collect(Collectors.toList());
            
            // 获取商品名称匹配的订单IDs
            List<Long> orderIdsByProductName = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                    .like(OrderItem::getProductName, keyword))
                    .stream()
                    .map(OrderItem::getOrderId)
                    .distinct()
                    .collect(Collectors.toList());
            
            // 合并两个列表并去重
            Set<Long> orderIds = new HashSet<>();
            orderIds.addAll(orderIdsByOrderNo);
            orderIds.addAll(orderIdsByProductName);
            
            if (!orderIds.isEmpty()) {
                queryWrapper.and(wrapper -> wrapper.like(Order::getOrderNo, keyword).or().in(Order::getId, orderIds));
            } else {
                queryWrapper.like(Order::getOrderNo, keyword);
            }
        }
        
        IPage<Order> orders = page(page, queryWrapper);
        
        // 加载订单项
        orders.getRecords().forEach(order -> {
            List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));
            order.setItems(items);
        });
        
        return orders;
    }
    
    @Override
    @Transactional
    public boolean payOrder(Long orderId) {
        Order order = getOrderDetail(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!"待付款".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，只能支付待付款状态的订单");
        }
        
        // 获取订单项，按农户分组处理付款
        List<OrderItem> items = order.getItems();
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("订单项不存在");
        }
        
        // 按农户ID分组订单项
        Map<Long, List<OrderItem>> farmerItemsMap = items.stream()
                .collect(Collectors.groupingBy(OrderItem::getFarmerId));
        
        // 为每个农户处理支付
        for (Map.Entry<Long, List<OrderItem>> entry : farmerItemsMap.entrySet()) {
            Long farmerId = entry.getKey();
            List<OrderItem> farmerItems = entry.getValue();
            
            // 计算该农户的总金额
            BigDecimal farmerAmount = farmerItems.stream()
                    .map(OrderItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 处理支付交易
            boolean success = transactionService.processOrderPayment(
                    order, order.getEmployeeId(), farmerId, farmerAmount);
            
            if (!success) {
                throw new RuntimeException("支付处理失败");
            }
        }
        
        // 更新订单状态
        order.setStatus("已付款");
        order.setPayTime(LocalDateTime.now());
        updateById(order);
        
        return true;
    }
    
    private String generateOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
                + String.format("%04d", (int)(Math.random() * 10000));
    }
} 