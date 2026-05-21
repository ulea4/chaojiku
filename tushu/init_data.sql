-- 插入示例图书数据
INSERT INTO books (isbn, title, author, publisher, price, stock_count, description, created_at, updated_at) VALUES
('978-7-111-12345-1', 'Java核心技术', '凯·S·霍斯特曼', '机械工业出版社', 99.00, 10, 'Java编程经典教程', NOW(), NOW()),
('978-7-115-12345-2', 'Spring Boot实战', 'Craig Walls', '人民邮电出版社', 79.00, 15, 'Spring Boot开发指南', NOW(), NOW()),
('978-7-121-12345-3', '算法导论', 'Thomas H. Cormen', '电子工业出版社', 128.00, 8, '算法领域经典著作', NOW(), NOW()),
('978-7-302-12345-4', '设计模式', 'Erich Gamma', '清华大学出版社', 68.00, 12, 'GoF设计模式详解', NOW(), NOW()),
('978-7-111-12345-5', 'MySQL必知必会', 'Ben Forta', '机械工业出版社', 45.00, 20, '数据库入门经典', NOW(), NOW());

-- 插入示例用户数据
INSERT INTO users (username, real_name, phone, email, created_at, updated_at) VALUES
('zhangsan', '张三', '13800138001', 'zhangsan@example.com', NOW(), NOW()),
('lisi', '李四', '13800138002', 'lisi@example.com', NOW(), NOW()),
('wangwu', '王五', '13800138003', 'wangwu@example.com', NOW(), NOW());
