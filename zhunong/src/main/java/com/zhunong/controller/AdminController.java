package com.zhunong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhunong.entity.Account;
import com.zhunong.entity.Order;
import com.zhunong.entity.OrderItem;
import com.zhunong.entity.Product;
import com.zhunong.entity.User;
import com.zhunong.service.AccountService;
import com.zhunong.service.OrderItemService;
import com.zhunong.service.OrderService;
import com.zhunong.service.ProductService;
import com.zhunong.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderItemService orderItemService;
    
    // 管理员首页
    @GetMapping
    public String index() {
        return "admin/index";
    }
    
    // 待审核农户列表
    @GetMapping("/farmers/pending")
    public String pendingFarmers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Model model) {
        
        logger.debug("查询待审核农户列表: page={}, size={}", page, size);
        
        Page<User> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getRole, "FARMER")
                .eq(User::getStatus, 0)
                .orderByDesc(User::getCreateTime);
        
        // 输出所有FARMER角色用户，不限状态，用于调试
        List<User> allFarmers = userService.list(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "FARMER"));
        logger.debug("系统中所有农户数量: {}", allFarmers.size());
        for (User farmer : allFarmers) {
            logger.debug("农户: id={}, username={}, realName={}, status={}, createTime={}", 
                    farmer.getId(), farmer.getUsername(), farmer.getRealName(), 
                    farmer.getStatus(), farmer.getCreateTime());
        }
        
        // 待审核页面不需要额外设置pendingCount，因为farms.total就是待审核总数
        model.addAttribute("pendingCount", null); // 设为null，模板会使用farms.total
        
        // 查询待审核农户
        IPage<User> farmers = userService.page(pageRequest, queryWrapper);
        logger.debug("查询到待审核农户: {}", farmers.getTotal());
        
        model.addAttribute("farmers", farmers);
        return "admin/farmers/pending";
    }
    
    // 添加农户页面
    @GetMapping("/farmers/add")
    public String addFarmerPage(Model model) {
        model.addAttribute("user", new User());
        return "admin/farmers/add";
    }
    
    // 保存新农户
    @PostMapping("/farmers/save")
    public String saveFarmer(@ModelAttribute User user) {
        // 设置为农户角色
        user.setRole("FARMER");
        // 设置为已审核状态
        user.setStatus(1);
        // 注册新用户
        userService.register(user);
        
        return "redirect:/admin/farmers/approved";
    }
    
    // 已审核农户列表
    @GetMapping("/farmers/approved")
    public String approvedFarmers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            Model model) {
        
        // 处理空字符串参数
        username = (username != null && !username.trim().isEmpty()) ? username.trim() : null;
        realName = (realName != null && !realName.trim().isEmpty()) ? realName.trim() : null;
        phone = (phone != null && !phone.trim().isEmpty()) ? phone.trim() : null;
        address = (address != null && !address.trim().isEmpty()) ? address.trim() : null;
        
        // 输出调试信息
        System.out.println("查询参数 - username: " + username + ", realName: " + realName + 
                          ", phone: " + phone + ", address: " + address);
        
        Page<User> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getRole, "FARMER")
                .eq(User::getStatus, 1)
                .like(username != null, User::getUsername, username)
                .like(realName != null, User::getRealName, realName)
                .like(phone != null, User::getPhone, phone)
                .like(address != null, User::getAddress, address)
                .orderByDesc(User::getCreateTime);
        
        // 获取待审核农户数量
        long pendingCount = userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "FARMER")
                .eq(User::getStatus, 0));
        model.addAttribute("pendingCount", pendingCount);
        
        // 将查询参数添加到模型中，以便在页面上回显
        model.addAttribute("username", username);
        model.addAttribute("realName", realName);
        model.addAttribute("phone", phone);
        model.addAttribute("address", address);
                
        model.addAttribute("farmers", userService.page(pageRequest, queryWrapper));
        return "admin/farmers/approved";
    }
    
    // 已禁用农户列表
    @GetMapping("/farmers/disabled")
    public String disabledFarmers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            Model model) {
        
        // 处理空字符串参数
        username = (username != null && !username.trim().isEmpty()) ? username.trim() : null;
        realName = (realName != null && !realName.trim().isEmpty()) ? realName.trim() : null;
        phone = (phone != null && !phone.trim().isEmpty()) ? phone.trim() : null;
        address = (address != null && !address.trim().isEmpty()) ? address.trim() : null;
        
        // 输出调试信息
        System.out.println("禁用农户查询参数 - username: " + username + ", realName: " + realName + 
                          ", phone: " + phone + ", address: " + address);
        
        Page<User> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getRole, "FARMER")
                .eq(User::getStatus, 2) // 状态为2表示已禁用
                .like(username != null, User::getUsername, username)
                .like(realName != null, User::getRealName, realName)
                .like(phone != null, User::getPhone, phone)
                .like(address != null, User::getAddress, address)
                .orderByDesc(User::getCreateTime);
        
        // 获取待审核农户数量
        long pendingCount = userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "FARMER")
                .eq(User::getStatus, 0));
        model.addAttribute("pendingCount", pendingCount);
        
        // 将查询参数添加到模型中，以便在页面上回显
        model.addAttribute("username", username);
        model.addAttribute("realName", realName);
        model.addAttribute("phone", phone);
        model.addAttribute("address", address);
                
        model.addAttribute("farmers", userService.page(pageRequest, queryWrapper));
        return "admin/farmers/disabled";
    }
    
    // 审核农户
    @PostMapping("/farmers/review/{id}")
    @ResponseBody
    public String reviewFarmer(
            @PathVariable Long id,
            @RequestParam Boolean approved) {
        try {
            logger.debug("审核农户: id={}, approved={}", id, approved);
            
            User farmer = userService.getById(id);
            if (farmer == null || !"FARMER".equals(farmer.getRole())) {
                logger.warn("农户不存在: id={}", id);
                return "农户不存在";
            }
            
            logger.debug("找到农户: username={}, status={}", farmer.getUsername(), farmer.getStatus());
            
            // 调用UserService的reviewFarmer方法，该方法会确保为审核通过的农户创建账户
            userService.reviewFarmer(id, approved);
            
            logger.debug("审核农户成功: username={}, approved={}", 
                    farmer.getUsername(), approved);
            
            return approved ? "启用成功" : "禁用成功";
        } catch (Exception e) {
            logger.error("审核农户失败: id=" + id, e);
            return "操作失败：" + e.getMessage();
        }
    }
    
    // 待审核商品列表
    @GetMapping("/products/pending")
    public String pendingProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Model model) {
        
        Page<Product> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 0)
                .orderByDesc(Product::getCreateTime);
                
        model.addAttribute("products", productService.page(pageRequest, queryWrapper));
        return "admin/products/pending";
    }
    
    // 全部商品列表
    @GetMapping("/products/list")
    public String productsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status,
            Model model) {
        
        Page<Product> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<Product>()
                .ne(Product::getStatus, 0) // 排除待审核商品
                .like(name != null && !name.isEmpty(), Product::getName, name)
                .like(category != null && !category.isEmpty(), Product::getCategory, category)
                .eq(status != null, Product::getStatus, status)
                .orderByDesc(Product::getCreateTime);
                
        model.addAttribute("products", productService.page(pageRequest, queryWrapper));
        model.addAttribute("name", name);
        model.addAttribute("category", category);
        model.addAttribute("status", status);
        return "admin/products/list";
    }
    
    // 创建职工账户页面
    @GetMapping("/employees/add")
    public String addEmployeePage(Model model) {
        model.addAttribute("user", new User());
        return "admin/employees/add";
    }
    
    // 创建职工账户
    @PostMapping("/employees/add")
    public String addEmployee(
            @ModelAttribute User user,
            @RequestParam(required = false, defaultValue = "0") BigDecimal initialBalance) {
        user.setRole("EMPLOYEE");
        user.setStatus(1); // 直接设置为已激活状态
        userService.register(user);
        
        // 如果有初始余额，创建账户并充值
        if (initialBalance.compareTo(BigDecimal.ZERO) > 0) {
            accountService.recharge(user.getId(), initialBalance);
        }
        
        return "redirect:/admin/employees/list";
    }
    
    // 职工账户列表
    @GetMapping("/employees/list")
    public String employeeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Model model) {
        
        Page<User> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getRole, "EMPLOYEE")
                .orderByDesc(User::getCreateTime);
                
        Page<User> employeePage = userService.page(pageRequest, queryWrapper);
        
        // 获取每个职工的账户信息
        List<User> employees = employeePage.getRecords();
        for (User employee : employees) {
            Account account = accountService.getByUserId(employee.getId());
            employee.setAccount(account);
        }
        
        model.addAttribute("employees", employeePage);
        return "admin/employees/list";
    }
    
    // 职工账户状态更新
    @PostMapping("/employees/status/{id}")
    @ResponseBody
    public String updateEmployeeStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            User employee = userService.getById(id);
            if (employee == null || !"EMPLOYEE".equals(employee.getRole())) {
                return "职工不存在";
            }
            
            employee.setStatus(status);
            userService.updateById(employee);
            return "操作成功";
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }
    
    // 账户充值
    @PostMapping("/account/recharge")
    @ResponseBody
    public String rechargeAccount(
            @RequestParam Long employeeId,
            @RequestParam BigDecimal amount) {
        try {
            User employee = userService.getById(employeeId);
            if (employee == null || !"EMPLOYEE".equals(employee.getRole())) {
                return "职工不存在";
            }
            
            accountService.recharge(employeeId, amount);
            return "充值成功";
        } catch (Exception e) {
            return "充值失败：" + e.getMessage();
        }
    }
    
    // 充值表单页面
    @GetMapping("/account/recharge-form")
    public String rechargeForm(
            @RequestParam Long employeeId,
            @RequestParam String employeeName,
            Model model) {
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("employeeName", employeeName);
        return "admin/account/recharge";
    }
    
    // 处理充值表单提交
    @PostMapping("/account/recharge-form")
    public String processRechargeForm(
            @RequestParam Long employeeId,
            @RequestParam String employeeName,
            @RequestParam BigDecimal amount,
            RedirectAttributes redirectAttributes) {
        try {
            User employee = userService.getById(employeeId);
            if (employee == null || !"EMPLOYEE".equals(employee.getRole())) {
                redirectAttributes.addFlashAttribute("error", "职工不存在");
                return "redirect:/admin/employees/list";
            }
            
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "请输入有效的充值金额");
                return "redirect:/admin/account/recharge-form?employeeId=" + employeeId + "&employeeName=" + employeeName;
            }
            
            accountService.recharge(employeeId, amount);
            redirectAttributes.addFlashAttribute("message", "已成功为 " + employeeName + " 充值 ¥" + amount);
            return "redirect:/admin/employees/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "充值失败：" + e.getMessage());
            return "redirect:/admin/account/recharge-form?employeeId=" + employeeId + "&employeeName=" + employeeName;
        }
    }
    
    // 处理Ajax充值表单提交
    @PostMapping("/account/recharge-ajax")
    @ResponseBody
    public String processRechargeAjax(
            @RequestParam Long employeeId,
            @RequestParam String employeeName,
            @RequestParam BigDecimal amount) {
        try {
            User employee = userService.getById(employeeId);
            if (employee == null || !"EMPLOYEE".equals(employee.getRole())) {
                return "职工不存在";
            }
            
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return "请输入有效的充值金额";
            }
            
            accountService.recharge(employeeId, amount);
            return "已成功为 " + employeeName + " 充值 ¥" + amount;
        } catch (Exception e) {
            return "充值失败：" + e.getMessage();
        }
    }
    
    // 测试页面路由
    @GetMapping("/test")
    public String testPage(Model model) {
        model.addAttribute("message", "这是一个测试页面");
        return "admin/test";
    }
    
    // 农户详情页面
    @GetMapping("/farmers/detail/{id}")
    public String farmerDetail(@PathVariable Long id, Model model) {
        User farmer = userService.getById(id);
        if (farmer == null || !"FARMER".equals(farmer.getRole())) {
            return "redirect:/admin/farmers/approved";
        }
        model.addAttribute("farmer", farmer);
        return "admin/farmers/detail";
    }
    
    // 商品详情页面
    @GetMapping("/products/detail/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getById(id);
        if (product == null) {
            return "redirect:/admin/products/list";
        }
        model.addAttribute("product", product);
        return "admin/products/detail";
    }
    
    // 商品编辑页面
    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.getById(id);
        if (product == null) {
            return "redirect:/admin/products/list";
        }
        
        // 获取所有已审核的农户列表
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getRole, "FARMER")
                .eq(User::getStatus, 1)  // 只获取已审核的农户
                .orderByAsc(User::getRealName);
        List<User> farmers = userService.list(queryWrapper);
        
        // 添加调试日志
        System.out.println("农户列表大小: " + farmers.size());
        for (User farmer : farmers) {
            System.out.println("农户ID: " + farmer.getId() + ", 姓名: " + farmer.getRealName() + ", 电话: " + farmer.getPhone());
        }
        
        model.addAttribute("product", product);
        model.addAttribute("farmers", farmers);
        return "admin/products/edit";
    }
    
    // 保存商品
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product) {
        // 如果是编辑现有商品，保留原有的创建时间
        if (product.getId() != null) {
            Product existingProduct = productService.getById(product.getId());
            if (existingProduct != null) {
                product.setCreateTime(existingProduct.getCreateTime());
                product.setUpdateTime(java.time.LocalDateTime.now());
            }
        }
        
        // 获取农户信息并设置到商品中
        if (product.getFarmerId() != null) {
            User farmer = userService.getById(product.getFarmerId());
            if (farmer != null) {
                product.setFarmerName(farmer.getRealName());
            }
        }
        
        productService.saveOrUpdate(product);
        return "redirect:/admin/products/list";
    }
    
    // 删除商品
    @PostMapping("/products/delete/{id}")
    @ResponseBody
    public String deleteProduct(@PathVariable Long id) {
        try {
            productService.removeById(id);
            return "删除成功";
        } catch (Exception e) {
            return "删除失败：" + e.getMessage();
        }
    }
    
    // 更新商品状态（上架/下架）
    @PostMapping("/products/updateStatus")
    @ResponseBody
    public String updateProductStatus(
            @RequestParam Long id,
            @RequestParam Integer status) {
        try {
            Product product = productService.getById(id);
            if (product == null) {
                return "商品不存在";
            }
            
            // 更新状态
            product.setStatus(status);
            productService.updateById(product);
            
            return "状态更新成功";
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }
    
    // 审核商品（通过审核）
    @PostMapping("/products/approve/{id}")
    public String approveProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        System.out.println("接收到商品审核通过请求: id=" + id);
        try {
            Product product = productService.getById(id);
            if (product == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "商品不存在");
                return "redirect:/admin/products/pending";
            }
            
            // 如果当前状态不是待审核(0)，则返回错误
            if (product.getStatus() != 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "只能审核待审核状态的商品");
                return "redirect:/admin/products/pending";
            }
            
            // 更新状态为已上架(1)
            product.setStatus(1);
            productService.updateById(product);
            
            redirectAttributes.addFlashAttribute("successMessage", "商品审核通过");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "操作失败：" + e.getMessage());
        }
        return "redirect:/admin/products/pending";
    }
    
    // 拒绝商品审核
    @PostMapping("/products/reject/{id}")
    public String rejectProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        System.out.println("接收到商品审核拒绝请求: id=" + id);
        try {
            Product product = productService.getById(id);
            if (product == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "商品不存在");
                return "redirect:/admin/products/pending";
            }
            
            // 如果当前状态不是待审核(0)，则返回错误
            if (product.getStatus() != 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "只能审核待审核状态的商品");
                return "redirect:/admin/products/pending";
            }
            
            // 更新状态为已下架(2)
            product.setStatus(2);
            productService.updateById(product);
            
            redirectAttributes.addFlashAttribute("successMessage", "商品审核已拒绝");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "操作失败：" + e.getMessage());
        }
        return "redirect:/admin/products/pending";
    }
    
    // 高级商品搜索
    @GetMapping("/products/search")
    public String searchProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String farmer,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer status,
            Model model) {
        
        Page<Product> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<Product>()
                .like(name != null && !name.isEmpty(), Product::getName, name)
                .like(category != null && !category.isEmpty(), Product::getCategory, category)
                .like(farmer != null && !farmer.isEmpty(), Product::getFarmerName, farmer)
                .ge(minPrice != null, Product::getPrice, minPrice)
                .le(maxPrice != null, Product::getPrice, maxPrice)
                .eq(status != null, Product::getStatus, status)
                .orderByDesc(Product::getCreateTime);
                
        model.addAttribute("products", productService.page(pageRequest, queryWrapper));
        
        // 回显搜索条件
        model.addAttribute("name", name);
        model.addAttribute("category", category);
        model.addAttribute("farmer", farmer);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("status", status);
        
        return "admin/products/search";
    }
    
    // 订单详情页面
    @GetMapping("/orders/detail/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderDetail(id);
        if (order == null) {
            return "redirect:/admin/orders/list";
        }
        
        // 获取订单项
        List<OrderItem> orderItems = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, id));
        
        // TODO: 添加订单日志服务并获取订单日志
        List<Object> orderLogs = new ArrayList<>(); // 暂时使用空列表
        
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("orderLogs", orderLogs);
        
        return "admin/orders/detail";
    }
    
    // 订单编辑页面
    @GetMapping("/orders/edit/{id}")
    public String editOrder(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderDetail(id);
        if (order == null) {
            return "redirect:/admin/orders/list";
        }
        model.addAttribute("order", order);
        return "admin/orders/edit";
    }
    
    // 保存订单
    @PostMapping("/orders/save")
    public String saveOrder(@ModelAttribute Order order) {
        // 保留原有的创建时间和订单项
        if (order.getId() != null) {
            Order existingOrder = orderService.getById(order.getId());
            if (existingOrder != null) {
                order.setItems(existingOrder.getItems());
                order.setCreateTime(existingOrder.getCreateTime());
                order.setUpdateTime(java.time.LocalDateTime.now());
            }
        }
        
        orderService.updateById(order);
        return "redirect:/admin/orders/list";
    }
    
    // 删除订单
    @PostMapping("/orders/delete/{id}")
    @ResponseBody
    public String deleteOrder(@PathVariable Long id) {
        try {
            orderService.removeById(id);
            return "删除成功";
        } catch (Exception e) {
            return "删除失败：" + e.getMessage();
        }
    }
    
    // 高级订单搜索
    @GetMapping("/orders/search")
    public String searchOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer paymentStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model) {
        
        Page<Order> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<Order>()
                .like(orderNo != null && !orderNo.isEmpty(), Order::getOrderNo, orderNo)
                .like(employeeName != null && !employeeName.isEmpty(), Order::getEmployeeName, employeeName)
                .ge(minAmount != null, Order::getTotalAmount, minAmount)
                .le(maxAmount != null, Order::getTotalAmount, maxAmount)
                .eq(status != null && !status.isEmpty(), Order::getStatus, status)
                .orderByDesc(Order::getCreateTime);
        
        // 处理付款状态查询
        if (paymentStatus != null) {
            if (paymentStatus == 1) {
                // 已付款状态包括：已付款、待发货、已发货、已完成
                queryWrapper.and(wrapper -> wrapper
                        .eq(Order::getStatus, "已付款")
                        .or()
                        .eq(Order::getStatus, "待发货")
                        .or()
                        .eq(Order::getStatus, "已发货")
                        .or()
                        .eq(Order::getStatus, "已完成"));
            } else if (paymentStatus == 0) {
                // 未付款状态只有待付款
                queryWrapper.eq(Order::getStatus, "待付款");
            }
        }
        
        // 处理日期范围查询
        if (startDate != null && !startDate.isEmpty()) {
            try {
                java.time.LocalDateTime start = java.time.LocalDate.parse(startDate).atStartOfDay();
                queryWrapper.ge(Order::getCreateTime, start);
            } catch (Exception e) {
                // 忽略日期解析错误
            }
        }
        
        if (endDate != null && !endDate.isEmpty()) {
            try {
                java.time.LocalDateTime end = java.time.LocalDate.parse(endDate).plusDays(1).atStartOfDay();
                queryWrapper.lt(Order::getCreateTime, end);
            } catch (Exception e) {
                // 忽略日期解析错误
            }
        }
        
        // 获取订单列表并加载订单项
        IPage<Order> orders = orderService.page(pageRequest, queryWrapper);
        for (Order order : orders.getRecords()) {
            List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));
            order.setItems(items);
        }
        
        model.addAttribute("orders", orders);
        
        // 回显搜索条件
        model.addAttribute("orderNo", orderNo);
        model.addAttribute("employeeName", employeeName);
        model.addAttribute("minAmount", minAmount);
        model.addAttribute("maxAmount", maxAmount);
        model.addAttribute("status", status);
        model.addAttribute("paymentStatus", paymentStatus);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "admin/orders/search";
    }
    
    // 管理员标记订单为已支付
    @PostMapping("/orders/mark-paid/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String markOrderAsPaid(@PathVariable Long id) {
        try {
            Order order = orderService.getById(id);
            if (order == null) {
                return "订单不存在";
            }
            
            if (!"待付款".equals(order.getStatus())) {
                return "只有待付款状态的订单才能标记为已支付";
            }
            
            order.setStatus("已付款");
            orderService.updateById(order);
            return "操作成功";
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }
    
    // 管理员标记订单为待发货
    @PostMapping("/orders/mark-ready-to-ship/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String markOrderAsReadyToShip(@PathVariable Long id) {
        try {
            Order order = orderService.getById(id);
            if (order == null) {
                return "订单不存在";
            }
            
            if (!"已付款".equals(order.getStatus())) {
                return "只有已付款状态的订单才能标记为待发货";
            }
            
            order.setStatus("待发货");
            orderService.updateById(order);
            return "操作成功";
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }
    
    // 管理员标记订单为已发货
    @PostMapping("/orders/mark-shipped/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String markOrderAsShipped(@PathVariable Long id) {
        try {
            Order order = orderService.getById(id);
            if (order == null) {
                return "订单不存在";
            }
            
            if (!"待发货".equals(order.getStatus())) {
                return "只有待发货状态的订单才能标记为已发货";
            }
            
            order.setStatus("已发货");
            order.setShipTime(java.time.LocalDateTime.now());
            orderService.updateById(order);
            return "操作成功";
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }
    
    // 管理员标记订单为已完成
    @PostMapping("/orders/mark-completed/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String markOrderAsCompleted(@PathVariable Long id) {
        try {
            Order order = orderService.getById(id);
            if (order == null) {
                return "订单不存在";
            }
            
            if (!"已发货".equals(order.getStatus())) {
                return "只有已发货状态的订单才能标记为已完成";
            }
            
            order.setStatus("已完成");
            order.setCompleteTime(java.time.LocalDateTime.now());
            orderService.updateById(order);
            return "操作成功";
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }
    
    // 管理员取消订单
    @PostMapping("/orders/cancel/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String cancelOrder(@PathVariable Long id) {
        try {
            Order order = orderService.getById(id);
            if (order == null) {
                return "订单不存在";
            }
            
            if ("已完成".equals(order.getStatus()) || "已取消".equals(order.getStatus())) {
                return "已完成或已取消的订单不能再次取消";
            }
            
            order.setStatus("已取消");
            order.setCancelTime(java.time.LocalDateTime.now());
            orderService.updateById(order);
            return "操作成功";
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }
    
    // 订单列表
    @GetMapping("/orders/list")
    public String orderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String timeRange,
            Model model) {
        
        Page<Order> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<Order>()
                .orderByDesc(Order::getCreateTime);
        
        // 处理关键字搜索
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Order::getOrderNo, keyword)
                    .or()
                    .like(Order::getEmployeeName, keyword));
        }
        
        // 处理状态筛选
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        // 处理时间范围筛选
        if (timeRange != null && !timeRange.isEmpty()) {
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;
            LocalDateTime now = LocalDateTime.now();
            
            switch (timeRange) {
                case "today":
                    startTime = now.toLocalDate().atStartOfDay();
                    endTime = now;
                    break;
                case "yesterday":
                    startTime = now.toLocalDate().minusDays(1).atStartOfDay();
                    endTime = now.toLocalDate().atStartOfDay();
                    break;
                case "thisWeek":
                    startTime = now.toLocalDate().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
                    endTime = now;
                    break;
                case "lastWeek":
                    startTime = now.toLocalDate().minusWeeks(1).with(java.time.DayOfWeek.MONDAY).atStartOfDay();
                    endTime = now.toLocalDate().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
                    break;
                case "thisMonth":
                    startTime = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
                    endTime = now;
                    break;
                case "lastMonth":
                    startTime = now.toLocalDate().minusMonths(1).withDayOfMonth(1).atStartOfDay();
                    endTime = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
                    break;
            }
            
            if (startTime != null) {
                queryWrapper.ge(Order::getCreateTime, startTime);
            }
            if (endTime != null) {
                queryWrapper.le(Order::getCreateTime, endTime);
            }
        }
        
        // 获取订单列表
        IPage<Order> orders = orderService.page(pageRequest, queryWrapper);
        
        // 加载订单项
        for (Order order : orders.getRecords()) {
            List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));
            order.setItems(items);
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("timeRange", timeRange);
        
        return "admin/orders/list";
    }
} 