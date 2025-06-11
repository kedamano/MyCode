package com.zhunong.controller;

import com.zhunong.entity.CartItem;
import com.zhunong.entity.User;
import com.zhunong.service.CartService;
import com.zhunong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // 通过用户名查找用户ID
        String username = userDetails.getUsername();
        User user = userService.getByUsername(username);
        Long employeeId = user.getId();
        
        logger.info("View cart for employee: {} (ID: {})", username, employeeId);
        
        List<CartItem> cartItems = cartService.getCartItems(employeeId);
        logger.info("Retrieved {} cart items for employee {}", cartItems.size(), employeeId);
        
        model.addAttribute("cartItems", cartItems);
        
        // 计算总金额
        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getSubtotal().doubleValue())
                .sum();
        model.addAttribute("totalAmount", totalAmount);
        
        logger.info("Cart total amount: {}", totalAmount);
        
        return "cart/view";
    }

    @PostMapping("/add/{productId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @ResponseBody
    public Map<String, Object> addToCartWithPathVariable(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 通过用户名查找用户ID
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            cartService.addItem(employeeId, productId, 1);
            
            response.put("success", true);
            response.put("message", "商品已成功加入购物车");
            response.put("cartCount", cartService.getCartItems(employeeId).size());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "添加失败：" + e.getMessage());
            return response;
        }
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @ResponseBody
    public Map<String, Object> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 通过用户名查找用户ID
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            Long productId;
            
            // 处理productId参数，确保正确转换为Long类型
            Object productIdObj = params.get("productId");
            if (productIdObj instanceof Number) {
                productId = ((Number) productIdObj).longValue();
            } else if (productIdObj instanceof String) {
                productId = Long.parseLong((String) productIdObj);
            } else {
                response.put("success", false);
                response.put("message", "添加失败：商品ID格式不正确");
                return response;
            }
            
            // 处理quantity参数，确保正确转换为Integer类型
            Object quantityObj = params.get("quantity");
            Integer quantity;
            if (quantityObj instanceof Number) {
                quantity = ((Number) quantityObj).intValue();
            } else if (quantityObj instanceof String) {
                quantity = Integer.parseInt((String) quantityObj);
            } else {
                quantity = 1; // 默认数量为1
            }
            
            cartService.addItem(employeeId, productId, quantity);
            
            response.put("success", true);
            response.put("message", "商品已成功加入购物车");
            response.put("cartCount", cartService.getCartItems(employeeId).size());
            return response;
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "添加失败：参数格式不正确 - " + e.getMessage());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "添加失败：" + e.getMessage());
            return response;
        }
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @ResponseBody
    public Map<String, Object> updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 通过用户名查找用户ID
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            Long productId;
            
            // 处理productId参数，确保正确转换为Long类型
            Object productIdObj = params.get("productId");
            if (productIdObj instanceof Number) {
                productId = ((Number) productIdObj).longValue();
            } else if (productIdObj instanceof String) {
                productId = Long.parseLong((String) productIdObj);
            } else {
                response.put("success", false);
                response.put("message", "更新失败：商品ID格式不正确");
                return response;
            }
            
            // 处理quantity参数，确保正确转换为Integer类型
            Object quantityObj = params.get("quantity");
            Integer quantity;
            if (quantityObj instanceof Number) {
                quantity = ((Number) quantityObj).intValue();
            } else if (quantityObj instanceof String) {
                quantity = Integer.parseInt((String) quantityObj);
            } else {
                response.put("success", false);
                response.put("message", "更新失败：数量格式不正确");
                return response;
            }
            
            cartService.updateItemQuantity(employeeId, productId, quantity);
            
            // 获取更新后的购物车信息
            List<CartItem> cartItems = cartService.getCartItems(employeeId);
            double totalAmount = cartItems.stream()
                    .mapToDouble(item -> item.getSubtotal().doubleValue())
                    .sum();
            
            response.put("success", true);
            response.put("message", "数量已更新");
            response.put("totalAmount", totalAmount);
            return response;
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "更新失败：参数格式不正确 - " + e.getMessage());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失败：" + e.getMessage());
            return response;
        }
    }

    @PostMapping("/remove")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @ResponseBody
    public Map<String, Object> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 通过用户名查找用户ID
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            Long productId;
            
            // 处理productId参数，确保正确转换为Long类型
            Object productIdObj = params.get("productId");
            if (productIdObj instanceof Number) {
                productId = ((Number) productIdObj).longValue();
            } else if (productIdObj instanceof String) {
                productId = Long.parseLong((String) productIdObj);
            } else {
                response.put("success", false);
                response.put("message", "移除失败：商品ID格式不正确");
                return response;
            }
            
            cartService.removeItem(employeeId, productId);
            
            response.put("success", true);
            response.put("message", "商品已从购物车中移除");
            return response;
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "移除失败：参数格式不正确 - " + e.getMessage());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "移除失败：" + e.getMessage());
            return response;
        }
    }

    // 通过路径参数处理移除商品请求
    @PostMapping("/remove/{productId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String removeItemByPath(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId,
            RedirectAttributes redirectAttributes) {
        try {
            // 通过用户名查找用户ID
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            
            logger.info("通过路径参数移除购物车商品: employeeId={}, productId={}", employeeId, productId);
            
            cartService.removeItem(employeeId, productId);
            
            redirectAttributes.addFlashAttribute("successMessage", "商品已从购物车中移除");
        } catch (Exception e) {
            logger.error("移除购物车商品失败: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "移除失败：" + e.getMessage());
        }
        return "redirect:/cart";
    }

    // 通过路径参数处理更新商品数量请求
    @PostMapping("/update/{productId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String updateQuantityByPath(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) String delta,
            RedirectAttributes redirectAttributes) {
        
        // 调整数量 - 支持直接设置数量或增减数量
        if (quantity == null && delta != null) {
            try {
                int deltaValue = Integer.parseInt(delta);
                // 获取当前数量
                String username = userDetails.getUsername();
                User user = userService.getByUsername(username);
                Long employeeId = user.getId();
                List<CartItem> items = cartService.getCartItems(employeeId);
                
                for (CartItem item : items) {
                    if (item.getProductId().equals(productId)) {
                        quantity = item.getQuantity() + deltaValue;
                        break;
                    }
                }
                
                if (quantity == null || quantity < 1) {
                    quantity = 1; // 默认最小数量为1
                }
            } catch (NumberFormatException e) {
                logger.warn("解析增量失败: {}", delta);
                redirectAttributes.addFlashAttribute("errorMessage", "更新失败：无效的数量参数");
                return "redirect:/cart";
            }
        }
        
        if (quantity == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "更新失败：缺少数量参数");
            return "redirect:/cart";
        }
        try {
            // 通过用户名查找用户ID
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            
            logger.info("通过路径参数更新购物车商品数量: employeeId={}, productId={}, quantity={}", employeeId, productId, quantity);
            
            cartService.updateItemQuantity(employeeId, productId, quantity);
            
            redirectAttributes.addFlashAttribute("successMessage", "商品数量已更新");
        } catch (Exception e) {
            logger.error("更新购物车商品数量失败: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "更新失败：" + e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String clearCart(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            // 通过用户名查找用户ID
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            
            cartService.clearCart(employeeId);
            
            redirectAttributes.addFlashAttribute("successMessage", "购物车已清空");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "清空失败：" + e.getMessage());
        }
        return "redirect:/cart";
    }
    
    // 测试用接口，用于诊断购物车问题
    @GetMapping("/debug/test-add")
    @ResponseBody
    public Map<String, Object> testAddToCart(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 通过用户名查找用户ID
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            
            logger.info("Test adding item to cart for employee: {} (ID: {})", username, employeeId);
            
            // 清空购物车
            cartService.clearCart(employeeId);
            logger.info("Cart cleared for testing");
            
            // 添加测试商品
            Long productId = 1L; // 假设产品ID为1
            cartService.addItem(employeeId, productId, 1);
            logger.info("Test item added to cart: productId={}, quantity={}", productId, 1);
            
            // 获取购物车
            List<CartItem> cartItems = cartService.getCartItems(employeeId);
            logger.info("Retrieved {} cart items after test add", cartItems.size());
            
            response.put("success", true);
            response.put("message", "测试商品已添加到购物车");
            response.put("cartItems", cartItems);
            return response;
        } catch (Exception e) {
            logger.error("Error in test add to cart: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "测试失败：" + e.getMessage());
            return response;
        }
    }
    
    // 诊断购物车状态
    @GetMapping("/debug/status")
    @ResponseBody
    public Map<String, Object> getCartStatus(@AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 通过用户名查找用户ID
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            
            logger.info("Getting cart status for employee: {} (ID: {})", username, employeeId);
            
            // 收集诊断信息
            Map<String, Object> diagnostics = new HashMap<>();
            
            // 获取购物车内容
            List<CartItem> cartItems = cartService.getCartItems(employeeId);
            diagnostics.put("cartItemsCount", cartItems.size());
            diagnostics.put("cartItems", cartItems);
            
            response.put("success", true);
            response.put("employeeId", employeeId);
            response.put("username", username);
            response.put("diagnostics", diagnostics);
            return response;
        } catch (Exception e) {
            logger.error("Error getting cart status: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "获取购物车状态失败：" + e.getMessage());
            return response;
        }
    }
    
    // 创建示例订单
    @PostMapping("/sample")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String createSampleOrder(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            // 通过用户名查找用户ID
            String username = userDetails.getUsername();
            User user = userService.getByUsername(username);
            Long employeeId = user.getId();
            
            logger.info("为用户 {} 创建示例订单", username);
            
            // 先清空购物车
            cartService.clearCart(employeeId);
            
            // 添加示例商品 - 假设产品ID为1、2、3
            // 实际应用中应该先检查这些商品是否存在
            try {
                cartService.addItem(employeeId, 1L, 2);
                cartService.addItem(employeeId, 2L, 1);
                cartService.addItem(employeeId, 3L, 3);
            } catch (Exception e) {
                // 如果添加特定商品失败，尝试添加任何可用的商品
                logger.warn("添加指定商品失败，尝试添加任何可用商品", e);
                
                // 此处可以实现查询任何可用商品并添加的逻辑
                // 简化处理：只添加一个商品ID=1的商品
                cartService.addItem(employeeId, 1L, 1);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "示例订单已创建，请继续结算");
        } catch (Exception e) {
            logger.error("创建示例订单失败: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "创建示例订单失败：" + e.getMessage());
        }
        return "redirect:/cart";
    }
} 