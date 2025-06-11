package com.zhunong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhunong.entity.*;
import com.zhunong.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 调试控制器，用于开发测试
 */
@Controller
@RequestMapping("/debug")
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderItemService orderItemService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private CartService cartService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/encode")
    public String encodePassword(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }

    @GetMapping("/check")
    public boolean checkPassword(@RequestParam String rawPassword, @RequestParam String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    @GetMapping("/fix-admin-password")
    public String fixAdminPassword(@RequestParam(defaultValue = "admin123") String password) {
        User admin = userService.getOne(new LambdaQueryWrapper<User>()
            .eq(User::getUsername, "admin"));
        
        if (admin == null) {
            return "管理员账户不存在";
        }
        
        // 生成新的密码哈希
        String encodedPassword = passwordEncoder.encode(password);
        admin.setPassword(encodedPassword);
        
        // 更新数据库
        userService.updateById(admin);
        
        return "管理员密码已更新为: " + password + ", 密码哈希: " + encodedPassword;
    }
    
    @GetMapping("/fix-all-passwords")
    public Map<String, Object> fixAllPasswords() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 更新管理员密码
            String adminPassword = "admin123";
            String adminHash = passwordEncoder.encode(adminPassword);
            int adminCount = jdbcTemplate.update(
                "UPDATE users SET password = ? WHERE role = 'ADMIN'", 
                adminHash
            );
            
            // 更新职工密码
            String employeePassword = "employee123";
            String employeeHash = passwordEncoder.encode(employeePassword);
            int employeeCount = jdbcTemplate.update(
                "UPDATE users SET password = ? WHERE role = 'EMPLOYEE'", 
                employeeHash
            );
            
            // 更新农户密码
            String farmerPassword = "farmer123";
            String farmerHash = passwordEncoder.encode(farmerPassword);
            int farmerCount = jdbcTemplate.update(
                "UPDATE users SET password = ? WHERE role = 'FARMER'", 
                farmerHash
            );
            
            // 确保所有用户状态正常
            int statusCount = jdbcTemplate.update(
                "UPDATE users SET status = 1 WHERE status != 1"
            );
            
            result.put("success", true);
            result.put("adminUpdated", adminCount);
            result.put("employeeUpdated", employeeCount);
            result.put("farmerUpdated", farmerCount);
            result.put("statusUpdated", statusCount);
            result.put("adminPassword", adminPassword);
            result.put("employeePassword", employeePassword);
            result.put("farmerPassword", farmerPassword);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/users")
    public List<Map<String, Object>> listAllUsers() {
        return jdbcTemplate.queryForList("SELECT id, username, password, role, status FROM users");
    }
    
    @GetMapping("/test-login")
    public Map<String, Object> testLogin(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        
        User user = userService.getByUsername(username);
        if (user == null) {
            result.put("exists", false);
            return result;
        }
        
        result.put("exists", true);
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("status", user.getStatus());
        result.put("storedPassword", user.getPassword());
        
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        result.put("passwordMatches", matches);
        
        return result;
    }
    
    @GetMapping("/reset-database")
    public String resetDatabase() {
        try {
            // 清空表
            jdbcTemplate.execute("DELETE FROM order_items");
            jdbcTemplate.execute("DELETE FROM orders");
            jdbcTemplate.execute("DELETE FROM cart_items");
            jdbcTemplate.execute("DELETE FROM products");
            jdbcTemplate.execute("DELETE FROM accounts");
            jdbcTemplate.execute("DELETE FROM users");
            
            // 重置自增ID
            jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE accounts AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE products AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE cart_items AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE orders AUTO_INCREMENT = 1");
            jdbcTemplate.execute("ALTER TABLE order_items AUTO_INCREMENT = 1");
            
            // 创建基本用户
            createUsers();
            
            return "数据库已重置，并创建了基本用户";
        } catch (Exception e) {
            return "重置失败: " + e.getMessage();
        }
    }
    
    @GetMapping("/create-users")
    public String createUsers() {
        try {
            // 创建管理员
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("0192023a7bbd73250516f069df18b500"); // admin123
            admin.setRealName("系统管理员");
            admin.setPhone("13800000000");
            admin.setEmail("admin@example.com");
            admin.setAddress("北京市朝阳区");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            userService.save(admin);
            
            // 创建职工
            User employee = new User();
            employee.setUsername("employee1");
            employee.setPassword("033836b6cedd9a857d82681aafadbc19"); // employee123
            employee.setRealName("张三");
            employee.setPhone("13811111111");
            employee.setEmail("employee1@example.com");
            employee.setAddress("北京市海淀区");
            employee.setRole("EMPLOYEE");
            employee.setStatus(1);
            employee.setCreateTime(LocalDateTime.now());
            employee.setUpdateTime(LocalDateTime.now());
            userService.save(employee);
            
            // 创建农户
            User farmer = new User();
            farmer.setUsername("farmer1");
            farmer.setPassword("4b3bcc3fd4c3c0ac234af3b9fd81c899"); // farmer123
            farmer.setRealName("赵大山");
            farmer.setPhone("13822222222");
            farmer.setEmail("farmer1@example.com");
            farmer.setAddress("河北省石家庄市");
            farmer.setRole("FARMER");
            farmer.setStatus(1);
            farmer.setCreateTime(LocalDateTime.now());
            farmer.setUpdateTime(LocalDateTime.now());
            userService.save(farmer);
            
            // 创建职工账户
            Account employeeAccount = new Account();
            employeeAccount.setUserId(employee.getId());
            employeeAccount.setUsername(employee.getUsername());
            employeeAccount.setBalance(new BigDecimal("400.00"));
            accountService.save(employeeAccount);
            
            return "基本用户已创建";
        } catch (Exception e) {
            return "创建用户失败: " + e.getMessage();
        }
    }
    
    @GetMapping("/create-sample-data")
    public String createSampleData() {
        try {
            // 获取农户和职工
            User farmer = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, "farmer1"));
            User employee = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, "employee1"));
            
            if (farmer == null || employee == null) {
                return "请先创建基本用户";
            }
            
            // 创建示例产品
            List<Product> products = createSampleProducts(farmer);
            
            // 创建示例订单
            createSampleOrders(employee, products);
            
            return "示例数据已创建";
        } catch (Exception e) {
            return "创建示例数据失败: " + e.getMessage();
        }
    }
    
    private List<Product> createSampleProducts(User farmer) {
        List<Product> products = new ArrayList<>();
        
        // 水果类
        String[] fruitNames = {"有机红富士苹果", "有机黄元帅苹果", "有机砀山梨", "有机红心猕猴桃", "有机脐橙", "有机草莓", "有机葡萄", "有机西瓜"};
        String[] fruitDesc = {
            "来自山东的有机红富士苹果，无农药，无污染",
            "来自陕西的有机黄元帅苹果，酸甜可口，营养丰富",
            "来自安徽砀山的梨，汁多味甜，果肉细腻",
            "来自四川的红心猕猴桃，富含维生素C，口感酸甜",
            "来自江西的脐橙，果肉饱满，汁多甘甜",
            "来自云南的有机草莓，新鲜采摘，口感佳",
            "来自新疆的有机葡萄，颗粒饱满，甜度高",
            "来自海南的有机西瓜，瓜瓤红嫩，清甜多汁"
        };
        
        for (int i = 0; i < fruitNames.length; i++) {
            Product product = new Product();
            product.setName(fruitNames[i]);
            product.setDescription(fruitDesc[i]);
            product.setCategory("水果");
            product.setPrice(new BigDecimal(String.format("%.2f", 10 + Math.random() * 20)));
            product.setStock(100 + new Random().nextInt(100));
            product.setImage("/img/products/fruit-" + (i+1) + ".jpg");
            product.setFarmerId(farmer.getId());
            product.setFarmerName(farmer.getRealName());
            product.setStatus(1);
            product.setCreateTime(LocalDateTime.now());
            product.setUpdateTime(LocalDateTime.now());
            
            productService.save(product);
            products.add(product);
        }
        
        // 蔬菜类
        String[] vegNames = {"有机西红柿", "有机黄瓜", "有机胡萝卜", "有机土豆", "有机白菜", "有机菠菜", "有机茄子", "有机青椒"};
        String[] vegDesc = {
            "来自山东的有机西红柿，果实饱满，口感酸甜",
            "来自河北的有机黄瓜，脆嫩多汁，清香可口",
            "来自内蒙古的有机胡萝卜，富含胡萝卜素，营养丰富",
            "来自甘肃的有机土豆，口感绵软，富含淀粉",
            "来自河南的有机白菜，叶片厚实，口感清脆",
            "来自江苏的有机菠菜，富含铁质，营养丰富",
            "来自四川的有机茄子，皮薄肉嫩，口感细腻",
            "来自湖南的有机青椒，辣味适中，风味独特"
        };
        
        for (int i = 0; i < vegNames.length; i++) {
            Product product = new Product();
            product.setName(vegNames[i]);
            product.setDescription(vegDesc[i]);
            product.setCategory("蔬菜");
            product.setPrice(new BigDecimal(String.format("%.2f", 5 + Math.random() * 10)));
            product.setStock(100 + new Random().nextInt(100));
            product.setImage("/img/products/vegetable-" + (i+1) + ".jpg");
            product.setFarmerId(farmer.getId());
            product.setFarmerName(farmer.getRealName());
            product.setStatus(1);
            product.setCreateTime(LocalDateTime.now());
            product.setUpdateTime(LocalDateTime.now());
            
            productService.save(product);
            products.add(product);
        }
        
        // 粮油类
        String[] grainNames = {"有机大米", "有机小米", "有机玉米", "有机高粱", "有机花生油", "有机菜籽油", "有机芝麻油", "有机豆油"};
        String[] grainDesc = {
            "来自东北的有机大米，颗粒饱满，口感软糯",
            "来自山西的有机小米，营养丰富，适合熬粥",
            "来自吉林的有机玉米，颗粒饱满，甜度适中",
            "来自内蒙古的有机高粱，富含蛋白质和矿物质",
            "来自山东的有机花生油，纯正天然，香气浓郁",
            "来自湖北的有机菜籽油，色泽金黄，香气四溢",
            "来自河南的有机芝麻油，香气浓郁，风味独特",
            "来自黑龙江的有机豆油，清淡爽口，营养丰富"
        };
        
        for (int i = 0; i < grainNames.length; i++) {
            Product product = new Product();
            product.setName(grainNames[i]);
            product.setDescription(grainDesc[i]);
            product.setCategory("粮油");
            product.setPrice(new BigDecimal(String.format("%.2f", 15 + Math.random() * 25)));
            product.setStock(100 + new Random().nextInt(100));
            product.setImage("/img/products/grain-" + (i+1) + ".jpg");
            product.setFarmerId(farmer.getId());
            product.setFarmerName(farmer.getRealName());
            product.setStatus(1);
            product.setCreateTime(LocalDateTime.now());
            product.setUpdateTime(LocalDateTime.now());
            
            productService.save(product);
            products.add(product);
        }
        
        return products;
    }
    
    private void createSampleOrders(User employee, List<Product> products) {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        
        // 创建多个订单，模拟不同状态和不同时间
        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setEmployeeId(employee.getId());
            order.setEmployeeName(employee.getRealName());
            order.setAddress(employee.getAddress());
            order.setPhone(employee.getPhone());
            order.setRemark("测试订单" + (i+1));
            
            // 随机订单状态：0待发货，1已发货，2已完成，3已取消
            int status = random.nextInt(4);
            order.setStatus(String.valueOf(status));
            
            // 设置不同的创建时间，模拟不同时间段的订单
            order.setCreateTime(now.minusDays(random.nextInt(30)));
            
            // 保存订单
            orderService.save(order);
            
            // 为订单添加2-5个商品
            int itemCount = 2 + random.nextInt(4);
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (int j = 0; j < itemCount; j++) {
                // 随机选择一个商品
                Product product = products.get(random.nextInt(products.size()));
                
                // 随机数量1-5
                int quantity = 1 + random.nextInt(5);
                
                // 计算小计金额
                BigDecimal subtotal = product.getPrice().multiply(new BigDecimal(quantity));
                totalAmount = totalAmount.add(subtotal);
                
                // 创建订单项
                OrderItem item = new OrderItem();
                item.setOrderId(order.getId());
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setFarmerId(product.getFarmerId());
                item.setFarmerName(product.getFarmerName());
                item.setPrice(product.getPrice());
                item.setQuantity(quantity);
                item.setSubtotal(subtotal);
                
                // 保存订单项
                orderItemService.save(item);
            }
            
            // 更新订单总金额
            order.setTotalAmount(totalAmount);
            orderService.updateById(order);
        }
    }
    
    private String generateOrderNo() {
        return "ZN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4);
    }

    /**
     * 生成测试数据
     */
    @GetMapping("/generate-data")
    @ResponseBody
    public String generateTestData() {
        StringBuilder result = new StringBuilder();
        
        // 生成农户数据
        List<User> farmers = generateFarmers(5);
        result.append("已生成农户数据：").append(farmers.size()).append("条<br>");
        
        // 生成职工数据
        List<User> employees = generateEmployees(10);
        result.append("已生成职工数据：").append(employees.size()).append("条<br>");
        
        // 生成商品数据
        List<Product> products = generateProducts(farmers, 30);
        result.append("已生成商品数据：").append(products.size()).append("条<br>");
        
        // 生成订单数据
        List<Order> orders = generateOrders(employees, 20);
        result.append("已生成订单数据：").append(orders.size()).append("条<br>");
        
        // 生成订单项数据
        int orderItemCount = generateOrderItems(orders, products);
        result.append("已生成订单项数据：").append(orderItemCount).append("条<br>");
        
        return result.toString();
    }
    
    /**
     * 生成农户数据
     */
    private List<User> generateFarmers(int count) {
        List<User> farmers = new ArrayList<>();
        String[] farmerNames = {"张三农场", "李四果园", "王五蔬菜基地", "赵六家庭农场", "钱七有机农业"};
        
        for (int i = 0; i < count; i++) {
            User farmer = new User();
            farmer.setUsername("farmer" + (i + 1));
            farmer.setPassword("$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm"); // 密码：123456
            farmer.setRealName(farmerNames[i % farmerNames.length]);
            farmer.setPhone("1388888" + String.format("%04d", i));
            farmer.setEmail("farmer" + (i + 1) + "@example.com");
            farmer.setAddress("农村地址" + (i + 1));
            farmer.setRole("FARMER");
            farmer.setStatus(1);
            farmer.setCreateTime(LocalDateTime.now());
            farmer.setUpdateTime(LocalDateTime.now());
            
            userService.save(farmer);
            farmers.add(farmer);
        }
        
        return farmers;
    }
    
    /**
     * 生成职工数据
     */
    private List<User> generateEmployees(int count) {
        List<User> employees = new ArrayList<>();
        String[] employeeNames = {"张经理", "李主管", "王工程师", "赵分析师", "钱设计师"};
        
        for (int i = 0; i < count; i++) {
            User employee = new User();
            employee.setUsername("employee" + (i + 1));
            employee.setPassword("$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm"); // 密码：123456
            employee.setRealName(employeeNames[i % employeeNames.length]);
            employee.setPhone("1399999" + String.format("%04d", i));
            employee.setEmail("employee" + (i + 1) + "@example.com");
            employee.setAddress("城市地址" + (i + 1));
            employee.setRole("EMPLOYEE");
            employee.setStatus(1);
            employee.setCreateTime(LocalDateTime.now());
            employee.setUpdateTime(LocalDateTime.now());
            
            userService.save(employee);
            employees.add(employee);
            
            // 为职工创建账户余额
            Account account = new Account();
            account.setUserId(employee.getId());
            account.setBalance(new BigDecimal(String.valueOf(1000 + Math.random() * 9000)));
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            
            accountService.save(account);
        }
        
        return employees;
    }
    
    /**
     * 生成商品数据
     */
    private List<Product> generateProducts(List<User> farmers, int count) {
        List<Product> products = new ArrayList<>();
        String[] categories = {"水果", "蔬菜", "粮油", "干货", "其他"};
        String[] productNames = {
            "有机大米", "新鲜苹果", "生态鸡蛋", "山区土豆", "野生蘑菇",
            "有机胡萝卜", "富硒大米", "新鲜草莓", "绿色青菜", "有机花生油",
            "野生木耳", "农家小米", "新鲜西红柿", "农家土鸡", "有机黄豆",
            "新鲜玉米", "山区红薯", "野生核桃", "有机白菜", "新鲜莲子"
        };
        
        for (int i = 0; i < count; i++) {
            Product product = new Product();
            User farmer = farmers.get(i % farmers.size());
            
            product.setName(productNames[i % productNames.length]);
            product.setCategory(categories[i % categories.length]);
            product.setPrice(new BigDecimal(String.valueOf(10 + Math.random() * 90)));
            product.setDescription(product.getName() + "，来自" + farmer.getRealName() + "的优质农产品，绿色健康，无污染。");
            product.setStock(100 + (int)(Math.random() * 900));
            product.setImage("/images/products/product" + (i % 10 + 1) + ".jpg");
            product.setFarmerId(farmer.getId());
            product.setFarmerName(farmer.getRealName());
            product.setStatus(1);
            product.setCreateTime(LocalDateTime.now());
            product.setUpdateTime(LocalDateTime.now());
            
            productService.save(product);
            products.add(product);
        }
        
        return products;
    }
    
    /**
     * 生成订单数据
     */
    private List<Order> generateOrders(List<User> employees, int count) {
        List<Order> orders = new ArrayList<>();
        String[] statuses = {"待付款", "已完成", "已取消"};
        int[] statusWeights = {1, 3, 1}; // 权重：待付款:已完成:已取消 = 1:3:1
        
        // 创建加权状态列表
        List<String> weightedStatuses = new ArrayList<>();
        for (int i = 0; i < statuses.length; i++) {
            for (int j = 0; j < statusWeights[i]; j++) {
                weightedStatuses.add(statuses[i]);
            }
        }
        
        for (int i = 0; i < count; i++) {
            Order order = new Order();
            User employee = employees.get(i % employees.size());
            
            order.setOrderNo("ORDER" + String.format("%08d", i + 1));
            order.setEmployeeId(employee.getId());
            order.setEmployeeName(employee.getRealName());
            
            // 随机生成订单时间，最近30天内
            LocalDateTime orderTime = LocalDateTime.now().minusDays((long)(Math.random() * 30));
            order.setCreateTime(orderTime);
            
            // 随机选择状态
            String status = weightedStatuses.get((int)(Math.random() * weightedStatuses.size()));
            order.setStatus(status);
            
            // 如果是已完成状态，设置支付时间
            if ("已完成".equals(status)) {
                order.setPayTime(orderTime.plusMinutes((long)(Math.random() * 60)));
            }
            
            orderService.save(order);
            orders.add(order);
        }
        
        return orders;
    }
    
    /**
     * 生成订单项数据
     */
    private int generateOrderItems(List<Order> orders, List<Product> products) {
        int count = 0;
        
        for (Order order : orders) {
            // 每个订单随机生成1-5个订单项
            int itemCount = 1 + (int)(Math.random() * 5);
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            // 为了避免重复商品，先随机打乱商品列表
            List<Product> shuffledProducts = new ArrayList<>(products);
            Collections.shuffle(shuffledProducts);
            
            for (int i = 0; i < itemCount && i < shuffledProducts.size(); i++) {
                OrderItem item = new OrderItem();
                Product product = shuffledProducts.get(i);
                
                item.setOrderId(order.getId());
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setFarmerId(product.getFarmerId());
                item.setFarmerName(product.getFarmerName());
                
                // 随机生成购买数量1-5
                int quantity = 1 + (int)(Math.random() * 5);
                item.setQuantity(quantity);
                
                item.setPrice(product.getPrice());
                item.setSubtotal(product.getPrice().multiply(new BigDecimal(quantity)));
                
                // 累加订单总金额
                totalAmount = totalAmount.add(item.getSubtotal());
                
                item.setCreateTime(order.getCreateTime());
                
                orderItemService.save(item);
                count++;
            }
            
            // 更新订单总金额
            order.setTotalAmount(totalAmount);
            orderService.updateById(order);
        }
        
        return count;
    }
    
    /**
     * 测试购物车功能
     */
    @GetMapping("/cart-test")
    @ResponseBody
    public String testCart() {
        StringBuilder result = new StringBuilder();
        
        try {
            // 获取一个职工ID
            Long employeeId = 1L;
            
            // 获取几个商品ID
            List<Product> products = productService.list();
            if (products.isEmpty()) {
                return "没有可用的商品数据，请先生成测试数据";
            }
            
            // 清空购物车
            cartService.clearCart(employeeId);
            result.append("已清空购物车<br>");
            
            // 添加商品到购物车
            for (int i = 0; i < Math.min(3, products.size()); i++) {
                Product product = products.get(i);
                int quantity = 1 + (int)(Math.random() * 3);
                
                cartService.addItem(employeeId, product.getId(), quantity);
                result.append("已添加商品：").append(product.getName())
                      .append("，数量：").append(quantity)
                      .append("，单价：").append(product.getPrice())
                      .append("<br>");
            }
            
            // 获取购物车内容
            List<CartItem> cartItems = cartService.getCartItems(employeeId);
            result.append("<br>购物车内容：<br>");
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CartItem item : cartItems) {
                result.append("商品：").append(item.getProductName())
                      .append("，数量：").append(item.getQuantity())
                      .append("，单价：").append(item.getPrice())
                      .append("，小计：").append(item.getSubtotal())
                      .append("<br>");
                
                totalAmount = totalAmount.add(item.getSubtotal());
            }
            
            result.append("<br>总金额：").append(totalAmount);
            
            return result.toString();
        } catch (Exception e) {
            return "测试购物车功能失败：" + e.getMessage();
        }
    }
    
    /**
     * 测试统计数据
     */
    @GetMapping("/statistics-test")
    @ResponseBody
    public Map<String, Object> testStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 销售趋势数据
            List<Map<String, Object>> salesTrend = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            
            for (int i = 29; i >= 0; i--) {
                LocalDateTime date = now.minusDays(i);
                Map<String, Object> item = new HashMap<>();
                item.put("date", date.getMonthValue() + "/" + date.getDayOfMonth());
                item.put("amount", 1000 + Math.random() * 5000);
                item.put("count", 5 + Math.random() * 20);
                salesTrend.add(item);
            }
            result.put("salesTrend", salesTrend);
            
            // 农户销售排行
            result.put("topFarmers", orderItemService.getTopFarmers(5));
            
            // 商品销售排行
            result.put("topProducts", orderItemService.getTopProducts(5));
            
            // 分类销售占比
            result.put("categorySales", orderItemService.getCategorySales());
            
            // 职工购买排行（模拟数据）
            List<Map<String, Object>> topEmployees = new ArrayList<>();
            List<User> employees = userService.findByRole("EMPLOYEE");
            
            for (int i = 0; i < Math.min(5, employees.size()); i++) {
                User employee = employees.get(i);
                Map<String, Object> item = new HashMap<>();
                item.put("employeeId", employee.getId());
                item.put("employeeName", employee.getRealName());
                item.put("totalPurchases", 1000 + Math.random() * 3000);
                item.put("orderCount", 5 + Math.random() * 15);
                topEmployees.add(item);
            }
            
            // 按购买金额排序
            topEmployees.sort((a, b) -> {
                Double aAmount = (Double) a.get("totalPurchases");
                Double bAmount = (Double) b.get("totalPurchases");
                return bAmount.compareTo(aAmount);
            });
            
            result.put("topEmployees", topEmployees);
            
            return result;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "测试统计数据失败：" + e.getMessage());
            return error;
        }
    }
    
    /**
     * 测试页面
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("message", "这是调试控制器首页，用于开发测试。");
        return "debug/index";
    }

    /**
     * 批量生成100个农户、100个职工及其相关商品、订单、订单项等真实数据
     */
    @GetMapping("/generate-realistic-data")
    @ResponseBody
    public String generateRealisticData() {
        StringBuilder result = new StringBuilder();
        try {
            // 生成100个农户
            List<User> farmers = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                User farmer = new User();
                farmer.setUsername("farmer" + i);
                farmer.setPassword("$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm"); // 密码：123456
                farmer.setRealName("农户" + i);
                farmer.setPhone("1388" + String.format("%06d", i));
                farmer.setEmail("farmer" + i + "@example.com");
                farmer.setAddress("农场地址" + i);
                farmer.setRole("FARMER");
                farmer.setStatus(1);
                farmer.setCreateTime(LocalDateTime.now());
                farmer.setUpdateTime(LocalDateTime.now());
                userService.save(farmer);
                farmers.add(farmer);
            }
            result.append("已生成农户：").append(farmers.size()).append("<br>");

            // 生成100个职工
            List<User> employees = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                User employee = new User();
                employee.setUsername("employee" + i);
                employee.setPassword("$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm"); // 密码：123456
                employee.setRealName("职工" + i);
                employee.setPhone("1399" + String.format("%06d", i));
                employee.setEmail("employee" + i + "@example.com");
                employee.setAddress("城市地址" + i);
                employee.setRole("EMPLOYEE");
                employee.setStatus(1);
                employee.setCreateTime(LocalDateTime.now());
                employee.setUpdateTime(LocalDateTime.now());
                userService.save(employee);
                employees.add(employee);
                // 创建账户
                Account account = new Account();
                account.setUserId(employee.getId());
                account.setUsername(employee.getUsername());
                account.setBalance(new BigDecimal(String.valueOf(1000 + Math.random() * 9000)));
                account.setCreateTime(LocalDateTime.now());
                account.setUpdateTime(LocalDateTime.now());
                accountService.save(account);
            }
            result.append("已生成职工：").append(employees.size()).append("<br>");

            // 生成每个农户10个商品
            List<Product> products = new ArrayList<>();
            for (User farmer : farmers) {
                for (int j = 1; j <= 10; j++) {
                    Product product = new Product();
                    product.setName("农产品" + farmer.getRealName() + "-" + j);
                    product.setDescription("优质农产品，编号" + j + "，来自" + farmer.getRealName());
                    product.setCategory(j % 5 == 0 ? "水果" : j % 5 == 1 ? "蔬菜" : j % 5 == 2 ? "粮油" : j % 5 == 3 ? "干货" : "其他");
                    product.setPrice(new BigDecimal(10 + j));
                    product.setStock(100 + j);
                    product.setImage("/img/products/fruit-" + ((j % 8) + 1) + ".jpg");
                    product.setFarmerId(farmer.getId());
                    product.setFarmerName(farmer.getRealName());
                    product.setStatus(1);
                    product.setCreateTime(LocalDateTime.now());
                    product.setUpdateTime(LocalDateTime.now());
                    productService.save(product);
                    products.add(product);
                }
            }
            result.append("已生成商品：").append(products.size()).append("<br>");

            // 每个职工下10个订单，每单2-5个商品
            int orderCount = 0, orderItemCount = 0;
            java.util.Random random = new java.util.Random();
            for (User employee : employees) {
                for (int k = 0; k < 10; k++) {
                    Order order = new Order();
                    order.setOrderNo("ORDER" + System.currentTimeMillis() + random.nextInt(10000));
                    order.setEmployeeId(employee.getId());
                    order.setEmployeeName(employee.getRealName());
                    order.setAddress(employee.getAddress());
                    order.setPhone(employee.getPhone());
                    order.setRemark("测试订单" + k);
                    order.setStatus("2"); // 已完成
                    order.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(30)));
                    orderService.save(order);
                    orderCount++;
                    java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
                    int itemCount = 2 + random.nextInt(4);
                    for (int m = 0; m < itemCount; m++) {
                        Product product = products.get(random.nextInt(products.size()));
                        int quantity = 1 + random.nextInt(5);
                        java.math.BigDecimal subtotal = product.getPrice().multiply(new java.math.BigDecimal(quantity));
                        OrderItem item = new OrderItem();
                        item.setOrderId(order.getId());
                        item.setProductId(product.getId());
                        item.setProductName(product.getName());
                        item.setFarmerId(product.getFarmerId());
                        item.setFarmerName(product.getFarmerName());
                        item.setPrice(product.getPrice());
                        item.setQuantity(quantity);
                        item.setSubtotal(subtotal);
                        item.setCreateTime(order.getCreateTime());
                        orderItemService.save(item);
                        orderItemCount++;
                        totalAmount = totalAmount.add(subtotal);
                    }
                    order.setTotalAmount(totalAmount);
                    orderService.updateById(order);
                }
            }
            result.append("已生成订单：").append(orderCount).append("，订单项：").append(orderItemCount).append("<br>");
            return result.toString();
        } catch (Exception e) {
            return "生成数据失败：" + e.getMessage();
        }
    }

    @GetMapping("/redis/test")
    public Map<String, Object> testRedis() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 测试Redis连接
            String testKey = "test:key";
            String testValue = "Hello Redis " + System.currentTimeMillis();
            
            logger.info("Testing Redis connection with key: {}, value: {}", testKey, testValue);
            
            // 设置测试值
            redisTemplate.opsForValue().set(testKey, testValue);
            logger.info("Test value set successfully");
            
            // 获取测试值
            Object retrievedValue = redisTemplate.opsForValue().get(testKey);
            logger.info("Retrieved test value: {}", retrievedValue);
            
            // 获取所有键
            Set<String> keys = redisTemplate.keys("*");
            logger.info("All Redis keys: {}", keys);
            
            response.put("success", true);
            response.put("message", "Redis连接测试成功");
            response.put("testValue", retrievedValue);
            response.put("allKeys", keys);
            
            return response;
        } catch (Exception e) {
            logger.error("Error testing Redis connection: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Redis连接测试失败: " + e.getMessage());
            response.put("error", e.toString());
            return response;
        }
    }
    
    @GetMapping("/redis/keys")
    public Map<String, Object> getRedisKeys() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取所有键
            Set<String> keys = redisTemplate.keys("*");
            logger.info("All Redis keys: {}", keys);
            
            response.put("success", true);
            response.put("message", "获取Redis键成功");
            response.put("keys", keys);
            
            return response;
        } catch (Exception e) {
            logger.error("Error getting Redis keys: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "获取Redis键失败: " + e.getMessage());
            response.put("error", e.toString());
            return response;
        }
    }
    
    @GetMapping("/redis/get")
    public Map<String, Object> getRedisValue(@RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查键是否存在
            Boolean exists = redisTemplate.hasKey(key);
            logger.info("Key {} exists: {}", key, exists);
            
            if (Boolean.TRUE.equals(exists)) {
                // 尝试获取值
                Object value = redisTemplate.opsForValue().get(key);
                logger.info("Value for key {}: {}", key, value);
                
                // 尝试获取哈希值
                Map<Object, Object> hashEntries = redisTemplate.opsForHash().entries(key);
                logger.info("Hash entries for key {}: {}", key, hashEntries);
                
                response.put("success", true);
                response.put("message", "获取Redis值成功");
                response.put("exists", true);
                response.put("value", value);
                response.put("hashEntries", hashEntries);
            } else {
                response.put("success", true);
                response.put("message", "键不存在");
                response.put("exists", false);
            }
            
            return response;
        } catch (Exception e) {
            logger.error("Error getting Redis value: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "获取Redis值失败: " + e.getMessage());
            response.put("error", e.toString());
            return response;
        }
    }
} 