-- Add employee_phone and employee_department columns to orders table
ALTER TABLE orders
ADD COLUMN employee_phone VARCHAR(20) DEFAULT NULL COMMENT '员工电话',
ADD COLUMN employee_department VARCHAR(50) DEFAULT NULL COMMENT '员工部门'; 