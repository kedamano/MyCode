/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80025
 Source Host           : localhost:3306
 Source Schema         : zhunong

 Target Server Type    : MySQL
 Target Server Version : 80025
 File Encoding         : 65001

 Date: 05/06/2025 18:02:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for accounts
-- ----------------------------
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '账户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of accounts
-- ----------------------------
INSERT INTO `accounts` VALUES (1, 2, 'employee1', 335.00, '2025-06-02 21:59:43', '2025-06-02 21:59:43');
INSERT INTO `accounts` VALUES (2, 3, 'employee2', 384.20, '2025-06-02 21:59:43', '2025-06-02 21:59:43');
INSERT INTO `accounts` VALUES (3, 4, 'employee3', 500.00, '2025-06-02 21:59:43', '2025-06-02 21:59:43');
INSERT INTO `accounts` VALUES (4, 5, 'employee', 400.00, '2025-06-02 21:59:43', '2025-06-02 21:59:43');
INSERT INTO `accounts` VALUES (5, 16, '张益达', 200.00, '2025-06-05 11:39:07', '2025-06-05 11:39:07');
INSERT INTO `accounts` VALUES (6, 6, 'farmer1', 1.00, '2025-06-05 14:55:59', '2025-06-05 14:55:59');
INSERT INTO `accounts` VALUES (7, 7, 'farmer2', 0.00, '2025-06-05 14:55:59', '2025-06-05 14:55:59');
INSERT INTO `accounts` VALUES (8, 8, 'farmer3', 0.00, '2025-06-05 14:55:59', '2025-06-05 14:55:59');
INSERT INTO `accounts` VALUES (9, 9, 'farmer', 0.00, '2025-06-05 14:55:59', '2025-06-05 14:55:59');
INSERT INTO `accounts` VALUES (10, 10, 'farmer-pending', 0.00, '2025-06-05 14:55:59', '2025-06-05 14:55:59');
INSERT INTO `accounts` VALUES (11, 1, 'admin', 6.00, '2025-06-05 15:54:10', '2025-06-05 15:54:10');
INSERT INTO `accounts` VALUES (12, 17, '一生一世', 0.00, '2025-06-05 16:47:20', '2025-06-05 16:47:20');

-- ----------------------------
-- Table structure for cart_items
-- ----------------------------
DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE `cart_items`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `employee_id` bigint(0) NOT NULL COMMENT '职工ID',
  `product_id` bigint(0) NOT NULL COMMENT '产品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品名称',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产品图片',
  `farmer_id` bigint(0) NOT NULL COMMENT '农户ID',
  `farmer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '农户名称',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `quantity` int(0) NOT NULL COMMENT '数量',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_employee_product`(`employee_id`, `product_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart_items
-- ----------------------------
INSERT INTO `cart_items` VALUES (23, 2, 17, '鸡蛋', '/images/products/egg.jpg', 1, '赵大山', 1.00, 1, 1.00, '2025-06-05 17:19:56', '2025-06-05 17:19:56');

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NOT NULL COMMENT '订单ID',
  `product_id` bigint(0) NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `farmer_id` bigint(0) NOT NULL COMMENT '农户ID',
  `farmer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '农户名称',
  `price` decimal(10, 2) NOT NULL COMMENT '商品单价',
  `quantity` int(0) NOT NULL COMMENT '购买数量',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计金额',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片路径',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` VALUES (1, 1, 1, '有机红富士苹果', 6, '赵大山', 15.80, 2, 31.60, '2025-06-02 21:59:36', '2025-06-04 11:13:44', '/images/products/apple.jpg');
INSERT INTO `order_items` VALUES (2, 1, 3, '有机胡萝卜', 6, '赵大山', 3.50, 3, 10.50, '2025-06-02 21:59:36', '2025-06-04 11:13:44', '/images/products/carrot.jpg');
INSERT INTO `order_items` VALUES (3, 1, 2, '新鲜西红柿', 6, '赵大山', 5.50, 1, 5.50, '2025-06-02 21:59:36', '2025-06-04 11:13:44', '/images/products/tomato.jpg');
INSERT INTO `order_items` VALUES (4, 2, 4, '东北大米', 7, '钱二牛', 25.90, 2, 51.80, '2025-06-02 21:59:36', '2025-06-04 11:13:44', '/images/products/rice.jpg');
INSERT INTO `order_items` VALUES (5, 2, 6, '有机黄瓜', 7, '钱二牛', 4.50, 5, 22.50, '2025-06-02 21:59:36', '2025-06-04 11:13:44', '/images/products/cucumber.jpg');
INSERT INTO `order_items` VALUES (6, 2, 14, '有机菠菜', 7, '钱二牛', 5.20, 2, 10.40, '2025-06-02 21:59:36', '2025-06-04 11:13:44', '/images/products/spinach.jpg');
INSERT INTO `order_items` VALUES (7, 3, 4, '东北大米', 7, '钱二牛', 25.90, 1, 25.90, '2025-06-02 21:59:36', '2025-06-04 11:13:44', '/images/products/rice.jpg');
INSERT INTO `order_items` VALUES (8, 4, 5, '花生油', 7, '钱二牛', 58.00, 1, 58.00, '2025-06-02 21:59:36', '2025-06-04 11:13:44', '/images/products/peanut_oil.jpg');
INSERT INTO `order_items` VALUES (9, 5, 7, '有机香蕉', 8, '孙小芳', 8.80, 2, 17.60, '2025-06-02 21:59:36', '2025-06-04 11:13:44', '/images/products/banana.jpg');
INSERT INTO `order_items` VALUES (10, 5, 9, '有机茄子', 8, '孙小芳', 4.20, 1, 4.20, '2025-06-02 21:59:36', '2025-06-04 11:13:44', '/images/products/eggplant.jpg');
INSERT INTO `order_items` VALUES (11, 5, 8, '有机葡萄', 8, '孙小芳', 12.80, 1, 12.80, '2025-06-02 21:59:36', '2025-06-04 11:13:44', '/images/products/grape.jpg');
INSERT INTO `order_items` VALUES (12, 6, 7, '有机香蕉', 8, '孙小芳', 8.80, 1, 8.80, '2025-06-03 14:39:51', '2025-06-04 11:13:44', '/images/products/banana.jpg');
INSERT INTO `order_items` VALUES (13, 6, 1, '有机红富士苹果', 6, '赵大山', 15.80, 3, 47.40, '2025-06-03 14:39:51', '2025-06-04 11:13:44', '/images/products/apple.jpg');
INSERT INTO `order_items` VALUES (14, 7, 1, '有机红富士苹果', 6, '赵大山', 15.80, 1, 15.80, '2025-06-03 14:56:05', '2025-06-04 11:13:44', '/images/products/apple.jpg');
INSERT INTO `order_items` VALUES (15, 8, 2, '新鲜西红柿', 6, '赵大山', 5.50, 1, 5.50, '2025-06-04 12:55:54', '2025-06-04 12:55:54', '/images/products/tomato.jpg');
INSERT INTO `order_items` VALUES (16, 9, 1, '有机红富士苹果', 6, '赵大山', 15.80, 1, 15.80, '2025-06-04 12:59:04', '2025-06-04 12:59:04', '/images/products/apple.jpg');
INSERT INTO `order_items` VALUES (17, 10, 5, '花生油', 7, '钱二牛', 58.00, 1, 58.00, '2025-06-04 13:05:44', '2025-06-04 13:05:44', '/images/products/peanut_oil.jpg');
INSERT INTO `order_items` VALUES (18, 11, 7, '有机香蕉', 8, '孙小芳', 8.80, 1, 8.80, '2025-06-04 13:18:52', '2025-06-04 13:18:52', '/images/products/banana.jpg');
INSERT INTO `order_items` VALUES (19, 12, 3, '有机胡萝卜', 6, '赵大山', 3.80, 1, 3.80, '2025-06-04 13:28:37', '2025-06-04 13:28:37', '/images/products/carrot.jpg');
INSERT INTO `order_items` VALUES (20, 13, 1, '有机红富士苹果', 6, '赵大山', 15.80, 1, 15.80, '2025-06-04 13:39:05', '2025-06-04 13:39:05', '/images/products/apple.jpg');
INSERT INTO `order_items` VALUES (21, 14, 6, '有机黄瓜', 7, '钱二牛', 4.50, 1, 4.50, '2025-06-04 13:40:08', '2025-06-04 13:40:08', '/images/products/cucumber.jpg');
INSERT INTO `order_items` VALUES (22, 15, 6, '有机黄瓜', 7, '钱二牛', 4.50, 1, 4.50, '2025-06-04 13:40:17', '2025-06-04 13:40:17', '/images/products/cucumber.jpg');
INSERT INTO `order_items` VALUES (23, 16, 6, '有机黄瓜', 7, '钱二牛', 4.50, 1, 4.50, '2025-06-04 13:40:31', '2025-06-04 13:40:31', '/images/products/cucumber.jpg');
INSERT INTO `order_items` VALUES (24, 17, 8, '有机葡萄', 8, '孙小芳', 12.80, 1, 12.80, '2025-06-04 13:41:17', '2025-06-04 13:41:17', '/images/products/grape.jpg');
INSERT INTO `order_items` VALUES (25, 18, 1, '有机红富士苹果', 6, '赵大山', 15.80, 1, 15.80, '2025-06-04 14:00:42', '2025-06-04 14:00:42', '/images/products/apple.jpg');
INSERT INTO `order_items` VALUES (26, 19, 2, '新鲜西红柿', 6, '赵大山', 5.50, 1, 5.50, '2025-06-04 14:03:41', '2025-06-04 14:03:41', '/images/products/tomato.jpg');
INSERT INTO `order_items` VALUES (27, 20, 2, '新鲜西红柿', 6, '赵大山', 5.50, 1, 5.50, '2025-06-04 14:03:47', '2025-06-04 14:03:47', '/images/products/tomato.jpg');
INSERT INTO `order_items` VALUES (28, 21, 5, '花生油', 7, '钱二牛', 58.00, 1, 58.00, '2025-06-04 14:10:42', '2025-06-04 14:10:42', '/images/products/peanut_oil.jpg');
INSERT INTO `order_items` VALUES (29, 22, 16, '广西火龙果', 1, '农户名称', 15.80, 1, 15.80, '2025-06-05 09:41:14', '2025-06-05 09:41:14', '/images/products/default.jpg');
INSERT INTO `order_items` VALUES (30, 23, 17, '鸡蛋', 1, '赵大山', 1.00, 5, 5.00, '2025-06-05 15:39:25', '2025-06-05 15:39:25', '/images/products/egg.jpg');
INSERT INTO `order_items` VALUES (31, 24, 1, '有机红富士苹果', 6, '赵大山', 15.80, 1, 15.80, '2025-06-05 15:39:38', '2025-06-05 15:39:38', '/images/products/apple.jpg');
INSERT INTO `order_items` VALUES (32, 25, 17, '鸡蛋', 1, '赵大山', 1.00, 1, 1.00, '2025-06-05 15:43:55', '2025-06-05 15:43:55', '/images/products/egg.jpg');
INSERT INTO `order_items` VALUES (33, 26, 17, '鸡蛋', 1, '赵大山', 1.00, 1, 1.00, '2025-06-05 16:24:21', '2025-06-05 16:24:21', '/images/products/egg.jpg');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `employee_id` bigint(0) NOT NULL COMMENT '职工ID',
  `employee_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '职工姓名',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '待付款',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货地址',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `employee_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '员工电话',
  `employee_department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '员工部门',
  `ship_time` datetime(0) NULL DEFAULT NULL COMMENT '发货时间',
  `complete_time` datetime(0) NULL DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime(0) NULL DEFAULT NULL COMMENT '取消时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, '202306020001', 2, '张三', 45.10, '已收货', '北京市朝阳区', '13811111111', '正常订单', '2025-06-02 21:59:28', '2025-06-05 12:35:58', '2025-06-02 20:59:28', '13811111111', '财务部', '2025-06-03 21:59:28', '2025-06-05 21:59:28', NULL);
INSERT INTO `orders` VALUES (2, '202306020002', 3, '李四', 83.90, '已付款', '北京市海淀区', '13822222222', '请尽快发货', '2025-06-02 21:59:28', '2025-06-05 12:42:28', '2025-06-02 23:59:28', '13822222222', '人事部', '2025-06-03 12:42:21', '2025-06-04 12:42:25', NULL);
INSERT INTO `orders` VALUES (4, '202306020004', 2, '张三', 58.00, '已收货', '北京市朝阳区', '13811111111', '标准订单', '2025-06-02 21:59:28', '2025-06-05 12:39:16', '2025-06-02 19:59:28', '13811111111', '财务部', '2025-06-03 21:59:28', '2025-06-06 21:59:28', NULL);
INSERT INTO `orders` VALUES (5, '202306020005', 5, '测试职工', 24.30, '已取消', '北京市东城区', '13900000001', '周末送货', '2025-06-02 21:59:28', '2025-06-05 12:42:03', NULL, '13900000001', '测试部', '2025-06-05 09:41:49', NULL, '2025-06-05 11:52:55');
INSERT INTO `orders` VALUES (6, '202506031439516710', 2, '张三', 56.20, '已取消', '浙江省杭州市', '15083720873', '', '2025-06-03 14:39:51', '2025-06-05 12:42:11', NULL, '13811111111', '财务部', '2025-06-01 12:42:04', NULL, '2025-06-03 18:39:51');
INSERT INTO `orders` VALUES (8, '202506041255532930', 2, '张三', 5.50, '已取消', '广东省深圳市', '15371450973', '尽快送达', '2025-06-04 12:55:54', '2025-06-05 13:16:24', NULL, '13811111111', '财务部', NULL, NULL, '2025-06-04 14:55:54');
INSERT INTO `orders` VALUES (9, '202506041259036142', 2, '张三', 15.80, '已取消', '广东省深圳市', '15371068945', '', '2025-06-04 12:59:04', '2025-06-05 12:35:58', NULL, '13811111111', '财务部', NULL, NULL, '2025-06-04 14:59:04');
INSERT INTO `orders` VALUES (10, '202506041305438670', 2, '张三', 58.00, '已取消', '广东省深圳市', '15371098456', '', '2025-06-04 13:05:44', '2025-06-05 12:35:58', NULL, '13811111111', '财务部', NULL, NULL, '2025-06-04 15:05:44');
INSERT INTO `orders` VALUES (11, '202506041318525736', 2, '张三', 8.80, '已取消', '湖南省长沙市', '17390786412', '', '2025-06-04 13:18:52', '2025-06-05 12:35:58', NULL, '13811111111', '财务部', NULL, NULL, '2025-06-04 15:18:52');
INSERT INTO `orders` VALUES (12, '202506041328367792', 2, '张三', 3.80, '已取消', '湖南省长沙市', '18772385472', '', '2025-06-04 13:28:37', '2025-06-05 12:35:58', NULL, '13811111111', '财务部', NULL, NULL, '2025-06-04 15:28:37');
INSERT INTO `orders` VALUES (13, '202506041339050493', 2, '张三', 15.80, '已取消', '广东省广州市', '13800138000', '示例订单', '2025-06-04 13:39:05', '2025-06-05 12:35:58', NULL, '13811111111', '财务部', NULL, NULL, '2025-06-04 15:39:05');
INSERT INTO `orders` VALUES (14, '202506041340076317', 2, '张三', 4.50, '已取消', '湖北省武汉市', '18772385472', '尽快送达', '2025-06-04 13:40:08', '2025-06-05 12:35:58', NULL, '13811111111', '财务部', NULL, NULL, '2025-06-04 14:10:08');
INSERT INTO `orders` VALUES (15, '202506041340165268', 2, '张三', 4.50, '已取消', '湖北省武汉市', '18772385472', '尽快送达', '2025-06-04 13:40:17', '2025-06-05 12:35:58', NULL, '13811111111', '财务部', NULL, NULL, '2025-06-04 14:10:17');
INSERT INTO `orders` VALUES (16, '202506041340302821', 2, '张三', 4.50, '已取消', '湖北省武汉市', '18772385472', '尽快送达', '2025-06-04 13:40:31', '2025-06-05 12:35:58', NULL, '13811111111', '财务部', NULL, NULL, '2025-06-04 14:10:31');
INSERT INTO `orders` VALUES (17, '202506041341164148', 2, '张三', 12.80, '已取消', '湖北省武汉市', '18772385472', '无', '2025-06-04 13:41:17', '2025-06-05 12:35:58', NULL, '13811111111', '财务部', NULL, NULL, '2025-06-04 14:11:17');
INSERT INTO `orders` VALUES (18, '202506041400411456', 2, '张三', 15.80, '已取消', '湖南省长沙市', '18772385472', '尽快送达', '2025-06-04 14:00:42', '2025-06-05 12:35:58', NULL, '13811111111', '财务部', NULL, NULL, '2025-06-04 14:30:42');
INSERT INTO `orders` VALUES (19, '202506041403407275', 2, '张三', 5.50, '待付款', '湖南省长沙市', '18772385472', '无', '2025-06-04 14:03:41', '2025-06-05 12:35:58', NULL, '13811111111', '财务部', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (20, '202506041403462050', 2, '张三', 5.50, '待付款', '湖南省长沙市', '18772385472', '无', '2025-06-04 14:03:47', '2025-06-05 12:35:58', NULL, '13811111111', '财务部', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (21, '202506041410416901', 2, '张三', 58.00, '已付款', '陕西省西安市', '18653210918', '无', '2025-06-04 14:10:42', '2025-06-05 12:35:58', '2025-06-04 15:10:42', '13811111111', '财务部', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (22, '202506050941133277', 3, '李四', 15.80, '已收货', '广西省桂林市', '18772385472', '无', '2025-06-05 09:41:14', '2025-06-05 12:35:58', '2025-06-05 09:11:14', '13822222222', '人事部', '2025-06-05 21:41:14', '2025-06-07 09:41:14', NULL);
INSERT INTO `orders` VALUES (23, '202506051539252791', 2, '张三', 5.00, '已付款', '广东省深圳市', '15371690835', '', '2025-06-05 15:39:25', '2025-06-05 15:39:25', '2025-06-05 16:05:13', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (24, '202506051539370036', 2, '张三', 15.80, '待付款', '广东省深圳市', '15371690835', '', '2025-06-05 15:39:38', '2025-06-05 15:39:38', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (25, '202506051543556780', 2, '张三', 1.00, '已收货', '广东省深圳市', '15371690835', '', '2025-06-05 15:43:55', '2025-06-05 15:43:55', '2025-06-05 15:54:10', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `orders` VALUES (26, '202506051624207301', 2, '张三', 1.00, '已付款', '湖南省岳阳市', '18772385472', '尽快送达', '2025-06-05 16:24:21', '2025-06-05 16:24:21', '2025-06-05 16:30:29', NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `stock` int(0) NOT NULL DEFAULT 0 COMMENT '库存',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类',
  `farmer_id` bigint(0) NOT NULL COMMENT '农户ID',
  `farmer_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '农户名称',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-正常，2-下架',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `sales_count` int(0) NOT NULL DEFAULT 0 COMMENT '销量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, '有机红富士苹果', '来自山东的有机红富士苹果，无农药，无污染', 15.80, 98, '水果', 6, '赵大山', '/images/products/apple.jpg', 1, '2025-06-02 21:59:19', '2025-06-05 15:39:38', 0);
INSERT INTO `products` VALUES (2, '新鲜西红柿', '河北特产西红柿，个大味甜', 5.50, 198, '蔬菜', 6, '赵大山', '/images/products/tomato.jpg', 1, '2025-06-02 21:59:19', '2025-06-04 14:03:47', 0);
INSERT INTO `products` VALUES (3, '有机胡萝卜', '富含胡萝卜素，对眼睛好', 3.80, 150, '蔬菜', 6, '赵大山', '/images/products/carrot.jpg', 1, '2025-06-02 21:59:19', '2025-06-04 13:59:54', 0);
INSERT INTO `products` VALUES (4, '东北大米', '东北黑土地种植的优质大米', 25.90, 50, '粮油', 7, '钱二牛', '/images/products/rice.jpg', 1, '2025-06-02 21:59:19', '2025-06-02 21:59:19', 0);
INSERT INTO `products` VALUES (5, '花生油', '纯天然压榨花生油，无添加', 58.00, 29, '粮油', 7, '钱二牛', '/images/products/peanut_oil.jpg', 1, '2025-06-02 21:59:19', '2025-06-04 14:10:42', 0);
INSERT INTO `products` VALUES (6, '有机黄瓜', '新鲜采摘的有机黄瓜', 4.50, 100, '蔬菜', 7, '钱二牛', '/images/products/cucumber.jpg', 1, '2025-06-02 21:59:19', '2025-06-04 13:59:27', 0);
INSERT INTO `products` VALUES (7, '有机香蕉', '来自海南的有机香蕉', 8.80, 80, '水果', 8, '孙小芳', '/images/products/banana.jpg', 1, '2025-06-02 21:59:19', '2025-06-04 13:59:35', 0);
INSERT INTO `products` VALUES (8, '有机葡萄', '阳光充足下生长的有机葡萄', 12.80, 60, '水果', 8, '孙小芳', '/images/products/grape.jpg', 1, '2025-06-02 21:59:19', '2025-06-04 13:45:19', 0);
INSERT INTO `products` VALUES (9, '有机茄子', '紫色有机茄子，口感细腻', 4.20, 120, '蔬菜', 8, '孙小芳', '/images/products/eggplant.jpg', 1, '2025-06-02 21:59:19', '2025-06-02 21:59:19', 0);
INSERT INTO `products` VALUES (10, '有机土豆', '黄心土豆，口感绵软', 3.50, 200, '蔬菜', 9, '测试农户', '/images/products/potato.jpg', 1, '2025-06-02 21:59:19', '2025-06-02 21:59:19', 0);
INSERT INTO `products` VALUES (11, '有机白菜', '新鲜采摘的有机白菜', 2.80, 150, '蔬菜', 9, '测试农户', '/images/products/cabbage.jpg', 1, '2025-06-02 21:59:19', '2025-06-02 21:59:19', 0);
INSERT INTO `products` VALUES (12, '有机玉米', '甜糯可口的有机玉米', 3.50, 100, '蔬菜', 9, '测试农户', '/images/products/corn.jpg', 1, '2025-06-02 21:59:19', '2025-06-02 21:59:19', 0);
INSERT INTO `products` VALUES (13, '有机猕猴桃', '富含维生素C的猕猴桃', 15.00, 50, '水果', 6, '赵大山', '/images/products/kiwi.jpg', 1, '2025-06-02 21:59:19', '2025-06-02 21:59:19', 0);
INSERT INTO `products` VALUES (14, '有机菠菜', '富含铁质的有机菠菜', 5.20, 80, '蔬菜', 7, '钱二牛', '/images/products/spinach.jpg', 1, '2025-06-02 21:59:19', '2025-06-02 21:59:19', 0);
INSERT INTO `products` VALUES (15, '有机草莓', '鲜红多汁的有机草莓', 18.50, 40, '水果', 8, '孙小芳', '/images/products/strawberry.jpg', 1, '2025-06-02 21:59:19', '2025-06-02 21:59:19', 0);
INSERT INTO `products` VALUES (17, '鸡蛋', '正宗散养柴鸡蛋', 1.00, 93, '其他', 1, '赵大山', '/images/products/egg.jpg', 1, '2025-06-05 14:51:40', '2025-06-05 16:24:21', 0);
INSERT INTO `products` VALUES (18, '新鲜小龙虾', '小龙虾活体1kg', 45.00, 150, '其他', 1, '赵大山', '/images/products/longxia.jpg', 1, '2025-06-05 15:16:49', '2025-06-05 15:20:34', 0);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色：ADMIN-管理员，EMPLOYEE-职工，FARMER-农户',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-正常，2-禁用',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ID card number',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Province',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'City',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'District',
  `farm_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Farm name',
  `farming_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Farming type',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Description',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '0192023a7bbd73250516f069df18b500', '系统管理员', '13800000000', 'admin@example.com', '北京市朝阳区', 'ADMIN', 1, '2025-06-02 21:59:10', '2025-06-05 12:35:26', '110101198001010001', '北京市', '朝阳区', '建国门街道', NULL, NULL, '系统超级管理员');
INSERT INTO `users` VALUES (2, 'employee1', '033836b6cedd9a857d82681aafadbc19', '张三', '13811111111', 'zhangsan@example.com', '北京市朝阳区', 'EMPLOYEE', 1, '2025-06-02 21:59:10', '2025-06-05 12:35:26', '110101198201010002', '北京市', '朝阳区', '建外街道', NULL, NULL, '财务部门员工');
INSERT INTO `users` VALUES (3, 'employee2', '033836b6cedd9a857d82681aafadbc19', '李四', '13822222222', 'lisi@example.com', '北京市海淀区', 'EMPLOYEE', 1, '2025-06-02 21:59:10', '2025-06-05 12:35:26', '110101198301010003', '北京市', '海淀区', '中关村街道', NULL, NULL, '人事部门员工');
INSERT INTO `users` VALUES (4, 'employee3', '033836b6cedd9a857d82681aafadbc19', '王五', '13833333333', 'wangwu@example.com', '北京市西城区', 'EMPLOYEE', 1, '2025-06-02 21:59:10', '2025-06-05 12:35:26', '110101198401010004', '北京市', '西城区', '西单街道', NULL, NULL, '采购部门员工');
INSERT INTO `users` VALUES (5, 'employee', '033836b6cedd9a857d82681aafadbc19', '测试职工', '13900000001', 'test-employee@example.com', '北京市东城区', 'EMPLOYEE', 1, '2025-06-02 21:59:10', '2025-06-05 12:35:26', '110101198501010005', '北京市', '东城区', '东直门街道', NULL, NULL, '测试部门员工');
INSERT INTO `users` VALUES (6, 'farmer1', '4b3bcc3fd4c3c0ac234af3b9fd81c899', '赵大山', '13844444444', 'zhaodashan@example.com', '河北省石家庄市', 'FARMER', 1, '2025-06-02 21:59:10', '2025-06-05 12:35:26', '130101198601010006', '河北省', '石家庄市', '长安区', '大山有机农场', '有机蔬菜', '专注有机蔬菜种植10年');
INSERT INTO `users` VALUES (7, 'farmer2', '4b3bcc3fd4c3c0ac234af3b9fd81c899', '钱二牛', '13855555555', 'qianerniu@example.com', '山东省济南市', 'FARMER', 1, '2025-06-02 21:59:10', '2025-06-05 12:35:26', '370101198701010007', '山东省', '济南市', '历下区', '二牛家庭农场', '水果种植', '主要种植苹果、梨等水果');
INSERT INTO `users` VALUES (8, 'farmer3', '4b3bcc3fd4c3c0ac234af3b9fd81c899', '孙小芳', '13866666666', 'sunxiaofang@example.com', '河南省郑州市', 'FARMER', 1, '2025-06-02 21:59:10', '2025-06-05 12:35:26', '410101198801010008', '河南省', '郑州市', '金水区', '小芳绿色农场', '粮食作物', '种植无公害小麦和水稻');
INSERT INTO `users` VALUES (9, 'farmer', '4b3bcc3fd4c3c0ac234af3b9fd81c899', '测试农户', '13900000002', 'test-farmer@example.com', '山西省太原市', 'FARMER', 2, '2025-06-02 21:59:10', '2025-06-05 12:35:26', '140101198901010009', '山西省', '太原市', '小店区', '测试示范农场', '混合种植', '用于系统测试的农户账号');
INSERT INTO `users` VALUES (10, 'farmer-pending', '4b3bcc3fd4c3c0ac234af3b9fd81c899', '待审核农户', '13900000003', 'pending@example.com', '陕西省西安市', 'FARMER', 1, '2025-06-02 21:59:10', '2025-06-05 12:35:26', '610101199001010010', '陕西省', '西安市', '雁塔区', '待审核农场', '特色农产品', '待审核农户信息');
INSERT INTO `users` VALUES (16, '张益达', 'e10adc3949ba59abbe56e057f20f883e', '张伟', '15371098642', '1392806889@qq.com', '广东省广州市', 'EMPLOYEE', 1, '2025-06-05 11:39:07', '2025-06-05 12:35:26', '440101199101010016', '广东省', '广州市', '天河区', NULL, NULL, '市场部门员工');
INSERT INTO `users` VALUES (17, '一生一世', 'e10adc3949ba59abbe56e057f20f883e', '谢娜', '15371690857', '1392806889@qq.com', '武汉市江夏区188号', 'FARMER', 1, '2025-06-05 16:47:20', '2025-06-05 16:47:20', NULL, NULL, NULL, NULL, '测试农场2', '蔬菜', '主要是蔬菜：茄子，豇豆');

SET FOREIGN_KEY_CHECKS = 1;
