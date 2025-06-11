-- 清空表
TRUNCATE TABLE order_items;

-- 订单1的订单项
INSERT INTO `order_items`(`id`, `order_id`, `product_id`, `product_name`, `farmer_id`, `farmer_name`, `price`, `quantity`, `subtotal`, `create_time`, `update_time`) 
VALUES (1, 1, 1, '有机红富士苹果', 6, '赵大山', 15.80, 2, 31.60, NOW(), NOW());

INSERT INTO `order_items`(`id`, `order_id`, `product_id`, `product_name`, `farmer_id`, `farmer_name`, `price`, `quantity`, `subtotal`, `create_time`, `update_time`) 
VALUES (2, 1, 3, '有机胡萝卜', 6, '赵大山', 3.50, 3, 10.50, NOW(), NOW());

INSERT INTO `order_items`(`id`, `order_id`, `product_id`, `product_name`, `farmer_id`, `farmer_name`, `price`, `quantity`, `subtotal`, `create_time`, `update_time`) 
VALUES (3, 1, 2, '新鲜西红柿', 6, '赵大山', 5.50, 1, 5.50, NOW(), NOW());

-- 订单2的订单项
INSERT INTO `order_items`(`id`, `order_id`, `product_id`, `product_name`, `farmer_id`, `farmer_name`, `price`, `quantity`, `subtotal`, `create_time`, `update_time`) 
VALUES (4, 2, 4, '东北大米', 7, '钱二牛', 25.90, 2, 51.80, NOW(), NOW());

INSERT INTO `order_items`(`id`, `order_id`, `product_id`, `product_name`, `farmer_id`, `farmer_name`, `price`, `quantity`, `subtotal`, `create_time`, `update_time`) 
VALUES (5, 2, 6, '有机黄瓜', 7, '钱二牛', 4.50, 5, 22.50, NOW(), NOW());

INSERT INTO `order_items`(`id`, `order_id`, `product_id`, `product_name`, `farmer_id`, `farmer_name`, `price`, `quantity`, `subtotal`, `create_time`, `update_time`) 
VALUES (6, 2, 14, '有机菠菜', 7, '钱二牛', 5.20, 2, 10.40, NOW(), NOW());

-- 订单3的订单项
INSERT INTO `order_items`(`id`, `order_id`, `product_id`, `product_name`, `farmer_id`, `farmer_name`, `price`, `quantity`, `subtotal`, `create_time`, `update_time`) 
VALUES (7, 3, 4, '东北大米', 7, '钱二牛', 25.90, 1, 25.90, NOW(), NOW());

-- 订单4的订单项
INSERT INTO `order_items`(`id`, `order_id`, `product_id`, `product_name`, `farmer_id`, `farmer_name`, `price`, `quantity`, `subtotal`, `create_time`, `update_time`) 
VALUES (8, 4, 5, '花生油', 7, '钱二牛', 58.00, 1, 58.00, NOW(), NOW());

-- 订单5的订单项
INSERT INTO `order_items`(`id`, `order_id`, `product_id`, `product_name`, `farmer_id`, `farmer_name`, `price`, `quantity`, `subtotal`, `create_time`, `update_time`) 
VALUES (9, 5, 7, '有机香蕉', 8, '孙小芳', 8.80, 2, 17.60, NOW(), NOW());

INSERT INTO `order_items`(`id`, `order_id`, `product_id`, `product_name`, `farmer_id`, `farmer_name`, `price`, `quantity`, `subtotal`, `create_time`, `update_time`) 
VALUES (10, 5, 9, '有机茄子', 8, '孙小芳', 4.20, 1, 4.20, NOW(), NOW());

INSERT INTO `order_items`(`id`, `order_id`, `product_id`, `product_name`, `farmer_id`, `farmer_name`, `price`, `quantity`, `subtotal`, `create_time`, `update_time`) 
VALUES (11, 5, 8, '有机葡萄', 8, '孙小芳', 12.80, 1, 12.80, NOW(), NOW());
