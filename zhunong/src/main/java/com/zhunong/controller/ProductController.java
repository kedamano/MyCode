package com.zhunong.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhunong.entity.Product;
import com.zhunong.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 商品列表页面
    @GetMapping("/list")
    public String productList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "default") String sort,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            Model model) {
        
        Page<Product> pageRequest = new Page<>(page, size);
        com.baomidou.mybatisplus.core.metadata.IPage<Product> products = 
                productService.getProductListWithFilter(category, keyword, minPrice, maxPrice, sort, pageRequest);
        
        // 获取分类统计信息
        Map<String, Integer> categoryStats = productService.getCategoryStats();
        
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
        model.addAttribute("categoryStats", categoryStats);
        
        return "product/list";
    }

    // 农户的商品管理页面
    @GetMapping("/farmer/list")
    @PreAuthorize("hasRole('FARMER')")
    public String farmerProductList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        Long farmerId = 1L; // 需要从userDetails中获取
        model.addAttribute("products", productService.getFarmerProducts(farmerId, new Page<>(page, size)));
        return "product/farmer/list";
    }

    // 添加商品页面
    @GetMapping("/farmer/add")
    @PreAuthorize("hasRole('FARMER')")
    public String addProductPage(Model model) {
        model.addAttribute("product", new Product());
        return "product/farmer/edit";
    }

    // 编辑商品页面
    @GetMapping("/farmer/edit/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public String editProductPage(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getById(id));
        return "product/farmer/edit";
    }

    // 保存商品
    @PostMapping("/farmer/save")
    @PreAuthorize("hasRole('FARMER')")
    public String saveProduct(
            @ModelAttribute Product product,
            @RequestParam(required = false) MultipartFile imageFile,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long farmerId = 1L; // 需要从userDetails中获取
        String farmerName = "农户名称"; // 需要从userDetails中获取
        
        // 如果是新商品
        if (product.getId() == null) {
            product.setFarmerId(farmerId);
            product.setFarmerName(farmerName);
            product.setStatus(0); // 待审核
            product.setCreateTime(java.time.LocalDateTime.now());
            product.setUpdateTime(java.time.LocalDateTime.now());
        } else {
            // 如果是编辑商品，需要验证是否是该农户的商品
            Product existingProduct = productService.getById(product.getId());
            if (existingProduct == null || !existingProduct.getFarmerId().equals(farmerId)) {
                throw new RuntimeException("无权编辑该商品");
            }
            
            // 保留原有的农户信息和状态
            product.setFarmerId(existingProduct.getFarmerId());
            product.setFarmerName(existingProduct.getFarmerName());
            product.setStatus(existingProduct.getStatus());
            product.setCreateTime(existingProduct.getCreateTime());
            product.setUpdateTime(java.time.LocalDateTime.now());
        }
        
        // 处理图片上传
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // 获取文件名并生成唯一文件名
                String originalFilename = imageFile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFilename = UUID.randomUUID().toString() + fileExtension;
                
                // 确保目录存在
                String uploadDir = "src/main/resources/static/images/products";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // 保存文件
                Path filePath = uploadPath.resolve(newFilename);
                Files.copy(imageFile.getInputStream(), filePath);
                
                // 设置图片URL到产品
                product.setImage("/images/products/" + newFilename);
            } catch (IOException e) {
                e.printStackTrace();
                // 如果上传失败，使用默认图片
                product.setImage("/images/products/default.jpg");
            }
        }
        
        productService.saveOrUpdate(product);
        return "redirect:/product/farmer/list";
    }

    // 图片上传接口
    @PostMapping("/farmer/upload")
    @PreAuthorize("hasRole('FARMER')")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "/images/products/default.jpg";
        }
        
        try {
            // 获取文件名并生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            
            // 确保目录存在
            String uploadDir = "src/main/resources/static/images/products";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 保存文件
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // 返回图片URL
            return "/images/products/" + newFilename;
        } catch (IOException e) {
            e.printStackTrace();
            return "/images/products/default.jpg";
        }
    }

    // AJAX方式添加商品
    @PostMapping("/farmer/add")
    @PreAuthorize("hasRole('FARMER')")
    @ResponseBody
    public String addProduct(@RequestBody Map<String, Object> productData, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Product product = new Product();
            product.setName((String) productData.get("name"));
            product.setDescription((String) productData.get("description"));
            product.setPrice(new BigDecimal(productData.get("price").toString()));
            product.setStock(Integer.parseInt(productData.get("stock").toString()));
            product.setCategory((String) productData.get("category"));
            product.setImage((String) productData.get("image"));
            
            // 如果没有设置图片，使用默认图片
            if (product.getImage() == null || product.getImage().isEmpty()) {
                product.setImage("/images/products/default.jpg");
            }
            
            Long farmerId = 1L; // 需要从userDetails中获取
            String farmerName = "农户名称"; // 需要从userDetails中获取
            
            product.setFarmerId(farmerId);
            product.setFarmerName(farmerName);
            product.setStatus(0); // 待审核
            product.setCreateTime(java.time.LocalDateTime.now());
            product.setUpdateTime(java.time.LocalDateTime.now());
            
            productService.saveOrUpdate(product);
            return "商品添加成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "商品添加失败: " + e.getMessage();
        }
    }

    // 上下架商品
    @PostMapping("/farmer/status/{id}")
    @PreAuthorize("hasRole('FARMER')")
    @ResponseBody
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 验证是否是该农户的商品
            Long farmerId = 1L; // 需要从userDetails中获取
            Product product = productService.getById(id);
            if (product == null || !product.getFarmerId().equals(farmerId)) {
                return "无权操作该商品";
            }
            
            productService.updateStatus(id, status);
            return "操作成功";
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }

    // 管理员审核商品
    @PostMapping("/admin/review/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String reviewProduct(
            @PathVariable Long id,
            @RequestParam Boolean approved) {
        try {
            productService.reviewProduct(id, approved);
            return "审核成功";
        } catch (Exception e) {
            return "审核失败：" + e.getMessage();
        }
    }

    // 商品详情页面
    @GetMapping("/detail/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getById(id));
        return "product/detail";
    }

    // 搜索商品（支持名称、描述、农户名称模糊查询）
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            Model model) {
        model.addAttribute("products", productService.searchProducts(category, keyword, new Page<>(page, size)));
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        return "product/list";
    }

    // 农户删除商品
    @PostMapping("/farmer/delete/{id}")
    @PreAuthorize("hasRole('FARMER')")
    @ResponseBody
    public String deleteProduct(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Long farmerId = 1L; // TODO: 从userDetails获取真实ID
        Product product = productService.getById(id);
        if (product == null || !product.getFarmerId().equals(farmerId)) {
            return "无权删除该商品";
        }
        productService.removeById(id);
        return "删除成功";
    }
} 