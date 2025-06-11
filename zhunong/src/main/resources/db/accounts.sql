-- 清空表
TRUNCATE TABLE accounts;

-- 职工账户
INSERT INTO `accounts`(`id`, `user_id`, `username`, `balance`, `create_time`, `update_time`) 
VALUES (1, 2, 'employee1', 400.00, NOW(), NOW());

INSERT INTO `accounts`(`id`, `user_id`, `username`, `balance`, `create_time`, `update_time`) 
VALUES (2, 3, 'employee2', 400.00, NOW(), NOW());

INSERT INTO `accounts`(`id`, `user_id`, `username`, `balance`, `create_time`, `update_time`) 
VALUES (3, 4, 'employee3', 400.00, NOW(), NOW());

INSERT INTO `accounts`(`id`, `user_id`, `username`, `balance`, `create_time`, `update_time`) 
VALUES (4, 5, 'employee', 400.00, NOW(), NOW());

-- 农户账户
INSERT INTO `accounts`(`id`, `user_id`, `username`, `balance`, `create_time`, `update_time`) 
VALUES (5, 6, 'farmer1', 0.00, NOW(), NOW());

INSERT INTO `accounts`(`id`, `user_id`, `username`, `balance`, `create_time`, `update_time`) 
VALUES (6, 7, 'farmer2', 0.00, NOW(), NOW());

INSERT INTO `accounts`(`id`, `user_id`, `username`, `balance`, `create_time`, `update_time`) 
VALUES (7, 8, 'farmer3', 0.00, NOW(), NOW());

INSERT INTO `accounts`(`id`, `user_id`, `username`, `balance`, `create_time`, `update_time`) 
VALUES (8, 9, 'farmer', 0.00, NOW(), NOW());

INSERT INTO `accounts`(`id`, `user_id`, `username`, `balance`, `create_time`, `update_time`) 
VALUES (9, 10, 'farmer-pending', 0.00, NOW(), NOW());
