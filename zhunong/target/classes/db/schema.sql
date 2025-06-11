-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `role` varchar(20) NOT NULL COMMENT '角色：ADMIN, EMPLOYEE, FARMER',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态：0-待审核，1-正常，2-禁用',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区/县',
  `farm_name` varchar(100) DEFAULT NULL COMMENT '农场/基地名称',
  `farming_type` varchar(50) DEFAULT NULL COMMENT '种植/养殖类型',
  `description` text DEFAULT NULL COMMENT '农户简介',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 账户表
CREATE TABLE IF NOT EXISTS `accounts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户表';

-- 产品表
CREATE TABLE IF NOT EXISTS `products` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '产品名称',
  `description` text COMMENT '产品描述',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `farmer_id` bigint(20) NOT NULL COMMENT '农户ID',
  `farmer_name` varchar(50) NOT NULL COMMENT '农户名称',
  `image` varchar(255) DEFAULT NULL COMMENT '图片路径',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态：0-待审核，1-已上架，2-已下架',
  `sales_count` int(11) NOT NULL DEFAULT '0' COMMENT '销量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- 购物车表
CREATE TABLE IF NOT EXISTS `cart_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `employee_id` bigint(20) NOT NULL COMMENT '职工ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(255) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `farmer_id` bigint(20) NOT NULL COMMENT '农户ID',
  `farmer_name` varchar(100) NOT NULL COMMENT '农户名称',
  `price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `quantity` int(11) NOT NULL COMMENT '商品数量',
  `subtotal` decimal(10,2) NOT NULL COMMENT '小计金额',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_product` (`employee_id`,`product_id`) COMMENT '职工和商品的唯一索引',
  KEY `idx_employee_id` (`employee_id`) COMMENT '职工ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `employee_id` bigint(20) NOT NULL COMMENT '职工ID',
  `employee_name` varchar(50) NOT NULL COMMENT '职工名称',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态：0-待发货，1-已发货，2-已完成，3-已取消',
  `address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单项表
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `farmer_id` bigint(20) NOT NULL COMMENT '农户ID',
  `farmer_name` varchar(50) NOT NULL COMMENT '农户名称',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `subtotal` decimal(10,2) NOT NULL COMMENT '小计',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表'; 