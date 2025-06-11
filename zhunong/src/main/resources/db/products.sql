-- 清空表
TRUNCATE TABLE products;

-- 农户1(ID=6)的产品
INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (1, '有机红富士苹果', '来自山东的有机红富士苹果，无农药，无污染', 15.80, 100, '水果', 6, '赵大山', '/images/products/apple.jpg', 1, NOW(), NOW());

INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (2, '新鲜西红柿', '河北特产西红柿，个大味甜', 5.50, 200, '蔬菜', 6, '赵大山', '/images/products/tomato.jpg', 1, NOW(), NOW());

INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (3, '有机胡萝卜', '富含胡萝卜素，对眼睛好', 3.80, 150, '蔬菜', 6, '赵大山', '/images/products/carrot.jpg', 1, NOW(), NOW());

-- 农户2(ID=7)的产品
INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (4, '东北大米', '东北黑土地种植的优质大米', 25.90, 50, '粮油', 7, '钱二牛', '/images/products/rice.jpg', 1, NOW(), NOW());

INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (5, '花生油', '纯天然压榨花生油，无添加', 58.00, 30, '粮油', 7, '钱二牛', '/images/products/peanut_oil.jpg', 1, NOW(), NOW());

INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (6, '有机黄瓜', '新鲜采摘的有机黄瓜', 4.50, 100, '蔬菜', 7, '钱二牛', '/images/products/cucumber.jpg', 1, NOW(), NOW());

-- 农户3(ID=8)的产品
INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (7, '有机香蕉', '来自海南的有机香蕉', 8.80, 80, '水果', 8, '孙小芳', '/images/products/banana.jpg', 1, NOW(), NOW());

INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (8, '有机葡萄', '阳光充足下生长的有机葡萄', 12.80, 60, '水果', 8, '孙小芳', '/images/products/grape.jpg', 1, NOW(), NOW());

INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (9, '有机茄子', '紫色有机茄子，口感细腻', 4.20, 120, '蔬菜', 8, '孙小芳', '/images/products/eggplant.jpg', 1, NOW(), NOW());

-- 测试农户(ID=9)的产品
INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (10, '有机土豆', '黄心土豆，口感绵软', 3.50, 200, '蔬菜', 9, '测试农户', '/images/products/potato.jpg', 1, NOW(), NOW());

INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (11, '有机白菜', '新鲜采摘的有机白菜', 2.80, 150, '蔬菜', 9, '测试农户', '/images/products/cabbage.jpg', 1, NOW(), NOW());

INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (12, '有机玉米', '甜糯可口的有机玉米', 3.50, 100, '蔬菜', 9, '测试农户', '/images/products/corn.jpg', 1, NOW(), NOW());

-- 更多多样化产品
INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (13, '有机猕猴桃', '富含维生素C的猕猴桃', 15.00, 50, '水果', 6, '赵大山', '/images/products/kiwi.jpg', 1, NOW(), NOW());

INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (14, '有机菠菜', '富含铁质的有机菠菜', 5.20, 80, '蔬菜', 7, '钱二牛', '/images/products/spinach.jpg', 1, NOW(), NOW());

INSERT INTO `products`(`id`, `name`, `description`, `price`, `stock`, `category`, `farmer_id`, `farmer_name`, `image`, `status`, `create_time`, `update_time`) 
VALUES (15, '有机草莓', '鲜红多汁的有机草莓', 18.50, 40, '水果', 8, '孙小芳', '/images/products/strawberry.jpg', 1, NOW(), NOW());
