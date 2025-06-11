package com.zhunong.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhunong.entity.CartItem;
import com.zhunong.entity.Order;
import com.zhunong.entity.Product;
import com.zhunong.entity.User;
import com.zhunong.service.AccountService;
import com.zhunong.service.CartService;
import com.zhunong.service.OrderService;
import com.zhunong.service.ProductService;
import com.zhunong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private ProductService productService;

    // 创建订单
    @PostMapping("/create")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @ResponseBody
    public Map<String, Object> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> orderData) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            String address = (String) orderData.get("address");
            String phone = (String) orderData.get("phone");
            String remark = (String) orderData.get("remark");
            
            logger.info("Creating order for employee: {} (ID: {})", username, employeeId);
            logger.info("Order data: address={}, phone={}, remark={}", address, phone, remark);
            
            // 检查是否有直接传入的商品项
            List<Map<String, Object>> directItems = (List<Map<String, Object>>) orderData.get("items");
            
            List<Map<String, Object>> orderItems = new ArrayList<>();
            
            if (directItems != null && !directItems.isEmpty()) {
                // 使用直接传入的商品项
                logger.info("Using {} direct items from request", directItems.size());
                orderItems = directItems;
            } else {
                // 从购物车获取商品
                List<CartItem> cartItems = cartService.getCartItems(employeeId);
                logger.info("Retrieved {} cart items for order creation", cartItems != null ? cartItems.size() : 0);
                
                if (cartItems == null || cartItems.isEmpty()) {
                    // 尝试手动添加购物车中的商品
                    logger.info("Cart is empty, trying to add items manually");
                    
                    // 获取第一个商品作为示例
                    Product product = productService.getById(1L); // 假设ID为1的商品存在
                    if (product != null) {
                        logger.info("Adding product to cart: {}", product.getName());
                        cartService.addItem(employeeId, product.getId(), 1);
                        
                        // 重新获取购物车
                        cartItems = cartService.getCartItems(employeeId);
                        logger.info("Retrieved {} cart items after manual addition", cartItems != null ? cartItems.size() : 0);
                    }
                    
                    // 如果仍然为空，则返回错误
                    if (cartItems == null || cartItems.isEmpty()) {
                        logger.error("Cart is still empty for employee {}", employeeId);
                        throw new RuntimeException("购物车为空，请先添加商品后再提交订单");
                    }
                }
                
                // 记录购物车中的商品
                for (CartItem item : cartItems) {
                    logger.info("Cart item for order: productId={}, name={}, quantity={}, price={}", 
                            item.getProductId(), item.getProductName(), item.getQuantity(), item.getPrice());
                }
                
                // 转换为订单项格式
                for (CartItem cartItem : cartItems) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("productId", cartItem.getProductId());
                    item.put("quantity", cartItem.getQuantity());
                    orderItems.add(item);
                }
            }
            
            // 创建订单
            Order order = orderService.createOrder(employeeId, orderItems, address, phone, remark);
            logger.info("Order created successfully: orderNo={}, totalAmount={}", 
                    order.getOrderNo(), order.getTotalAmount());
            
            // 清空购物车
            cartService.clearCart(employeeId);
            logger.info("Cart cleared for employee {}", employeeId);
            
            // 获取用户当前余额
            BigDecimal employeeBalance = accountService.getBalance(employeeId);
            
            // 构建返回数据
            result.put("id", order.getId());
            result.put("orderNo", order.getOrderNo());
            result.put("totalAmount", order.getTotalAmount());
            result.put("employeeBalance", employeeBalance);
            result.put("createTime", order.getCreateTime());
            result.put("status", order.getStatus());
            
            return result;
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
            throw new RuntimeException("创建订单失败：" + e.getMessage());
        }
    }
    
    // 支付订单
    @PostMapping("/pay/{orderNo}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @ResponseBody
    public Map<String, Object> payOrder(
            @PathVariable String orderNo,
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取用户ID
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            
            // 查询订单
            Order order = orderService.getByOrderNo(orderNo);
            if (order == null) {
                result.put("success", false);
                result.put("message", "订单不存在");
                return result;
            }
            
            // 验证订单所属
            if (!order.getEmployeeId().equals(employeeId)) {
                result.put("success", false);
                result.put("message", "无权支付此订单");
                return result;
            }
            
            // 获取账户余额
            BigDecimal balance = accountService.getBalance(employeeId);
            BigDecimal orderAmount = order.getTotalAmount();
            
            // 检查余额是否足够
            if (balance.compareTo(orderAmount) < 0) {
                result.put("success", false);
                result.put("message", "账户余额不足");
                return result;
            }
            
            // 使用orderService处理支付，包括资金流转
            boolean paymentSuccess = orderService.payOrder(order.getId());
            
            if (paymentSuccess) {
                result.put("success", true);
                result.put("message", "支付成功");
            } else {
                result.put("success", false);
                result.put("message", "支付处理失败");
            }
            
            return result;
        } catch (Exception e) {
            logger.error("支付订单失败", e);
            result.put("success", false);
            result.put("message", "支付失败：" + e.getMessage());
            return result;
        }
    }

    // 职工的订单列表
    @GetMapping("/employee/list")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String employeeOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            String username = userDetails.getUsername();
            logger.info("Fetching orders for employee: {}, status: {}, dateRange: {} to {}, keyword: {}", 
                    username, status, startDate, endDate, keyword);
            
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            
            Page<Order> pageRequest = new Page<>(page, size);
            com.baomidou.mybatisplus.core.metadata.IPage<Order> orders = 
                    orderService.getEmployeeOrdersWithFilter(employeeId, status, startDate, endDate, keyword, pageRequest);
            
            logger.info("Found {} orders for employee", orders.getRecords().size());
            if (!orders.getRecords().isEmpty()) {
                logger.info("First order: ID={}, OrderNo={}, Status={}", 
                        orders.getRecords().get(0).getId(),
                        orders.getRecords().get(0).getOrderNo(),
                        orders.getRecords().get(0).getStatus());
            }
            
            model.addAttribute("orders", orders);
            model.addAttribute("status", status);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("keyword", keyword);
            return "order/employee/list";
        } catch (Exception e) {
            logger.error("Error fetching employee orders: {}", e.getMessage(), e);
            throw e;
        }
    }

    // 农户的订单列表
    @GetMapping("/farmer/list")
    @PreAuthorize("hasRole('FARMER')")
    public String farmerOrderList(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  Model model) {
        Long farmerId = 1L; // TODO: 从userDetails获取真实ID
        Page<Order> pageRequest = new Page<>(page, size);
        com.baomidou.mybatisplus.core.metadata.IPage<Order> orders = orderService.getFarmerOrders(farmerId, pageRequest);
        model.addAttribute("orders", orders);
        return "order/farmer/list";
    }
    
    // 订单详情
    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderDetail(id));
        return "order/detail";
    }

    // 订单详情（通过订单编号）
    @GetMapping("/detail/no/{orderNo}")
    public String orderDetailByOrderNo(@PathVariable String orderNo, Model model) {
        logger.info("Fetching order details by order number: {}", orderNo);
        
        try {
            Order order = orderService.getByOrderNo(orderNo);
            
            if (order == null) {
                logger.error("Order not found with orderNo: {}", orderNo);
                throw new RuntimeException("订单不存在: " + orderNo);
            }
            
            logger.info("Found order with ID: {}", order.getId());
            Order orderDetail = orderService.getOrderDetail(order.getId());
            model.addAttribute("order", orderDetail);
            
            return "order/detail";
        } catch (Exception e) {
            logger.error("Error retrieving order by orderNo {}: {}", orderNo, e.getMessage(), e);
            throw new RuntimeException("获取订单详情失败: " + e.getMessage());
        }
    }

    // 发货
    @PostMapping("/ship/{id}")
    @PreAuthorize("hasRole('FARMER')")
    @ResponseBody
    public String ship(@PathVariable Long id) {
        try {
            orderService.ship(id);
            return "发货成功";
        } catch (Exception e) {
            return "发货失败：" + e.getMessage();
        }
    }

    // 确认收货
    @PostMapping("/confirm/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @ResponseBody
    public String confirm(@PathVariable Long id) {
        try {
            orderService.confirm(id);
            return "确认收货成功";
        } catch (Exception e) {
            return "确认收货失败：" + e.getMessage();
        }
    }

    // 取消订单
    @PostMapping("/cancel/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @ResponseBody
    public String cancel(@PathVariable Long id) {
        try {
            orderService.cancel(id);
            return "取消订单成功";
        } catch (Exception e) {
            return "取消订单失败：" + e.getMessage();
        }
    }

    // 农户修改订单状态（如发货、完成等）
    @PostMapping("/farmer/status/{id}")
    @PreAuthorize("hasRole('FARMER')")
    @ResponseBody
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status, @AuthenticationPrincipal UserDetails userDetails) {
        // TODO: 校验权限
        Order order = orderService.getById(id);
        if (order == null) return "订单不存在";
        order.setStatus(status);
        orderService.updateById(order);
        return "操作成功";
    }

    // 农户查看订单详情
    @GetMapping("/farmer/detail/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public String farmerOrderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderDetail(id);
        model.addAttribute("order", order);
        return "order/detail";
    }

    // 异常处理
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleRuntimeException(RuntimeException e) {
        return e.getMessage();
    }
    
    // 直接创建订单（无需购物车）
    @PostMapping("/create-direct")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @ResponseBody
    public Order createDirectOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> orderData) {
        try {
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            String address = (String) orderData.get("address");
            String phone = (String) orderData.get("phone");
            String remark = (String) orderData.get("remark");
            
            logger.info("Creating direct order for employee: {} (ID: {})", username, employeeId);
            logger.info("Order data: address={}, phone={}, remark={}", address, phone, remark);
            
            // 获取直接传入的商品项
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
            
            if (items == null || items.isEmpty()) {
                logger.error("No items provided for direct order");
                throw new RuntimeException("请提供至少一个商品项");
            }
            
            // 记录商品项
            for (Map<String, Object> item : items) {
                logger.info("Order item: productId={}, quantity={}", 
                        item.get("productId"), item.get("quantity"));
            }
            
            // 创建订单
            Order order = orderService.createOrder(employeeId, items, address, phone, remark);
            logger.info("Direct order created successfully: orderNo={}, totalAmount={}", 
                    order.getOrderNo(), order.getTotalAmount());
            
            return order;
        } catch (Exception e) {
            logger.error("Error creating direct order: {}", e.getMessage(), e);
            throw new RuntimeException("创建订单失败：" + e.getMessage());
        }
    }
} 