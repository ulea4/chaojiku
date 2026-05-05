# 简易图书管理系统

这是一个基于Spring Boot的简易图书管理系统，支持图书录入、借阅/归还、库存统计和用户借阅记录功能。

## 技术栈
- 后端：Java 25 + Spring Boot 3.4
- 数据库：MySQL
- 前端：Vue 3（可选）
- 依赖管理：Maven

## 核心功能
1. **图书管理**
   - 图书录入（ISBN、标题、作者、出版社、价格、库存）
   - 图书查询（按标题、作者、ISBN搜索）
   - 图书修改和删除

2. **用户管理**
   - 用户注册和信息管理
   - 用户查询

3. **借阅管理**
   - 图书借阅（检查库存）
   - 图书归还（更新库存）
   - 借阅记录查询

4. **统计分析**
   - 库存统计（总图书数、可借阅数、缺货数、平均库存）
   - 作者热门度统计

## API接口

### 图书相关接口
- `GET /api/books` - 获取所有图书
- `GET /api/books/{id}` - 获取指定图书
- `POST /api/books` - 创建新图书
- `PUT /api/books/{id}` - 更新图书
- `DELETE /api/books/{id}` - 删除图书
- `GET /api/books/search/title?title=xxx` - 按标题搜索
- `GET /api/books/search/author?author=xxx` - 按作者搜索
- `GET /api/books/search/isbn?isbn=xxx` - 按ISBN搜索

### 用户相关接口
- `GET /api/users` - 获取所有用户
- `GET /api/users/{id}` - 获取指定用户
- `POST /api/users` - 创建新用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户
- `GET /api/users/username/{username}` - 按用户名查询

### 借阅相关接口
- `GET /api/borrow-records` - 获取所有借阅记录
- `GET /api/borrow-records/{id}` - 获取指定借阅记录
- `POST /api/borrow-records/borrow?userId=xxx&bookId=xxx` - 借阅图书
- `POST /api/borrow-records/return/{id}` - 归还图书
- `GET /api/borrow-records/user/{userId}` - 获取用户所有借阅记录
- `GET /api/borrow-records/book/{bookId}` - 获取图书所有借阅记录
- `GET /api/borrow-records/user/{userId}/status/{status}` - 获取用户指定状态的借阅记录

### 统计相关接口
- `GET /api/statistics/inventory` - 库存统计
- `GET /api/statistics/top-authors` - 热门作者统计

### 用户借阅记录接口
- `GET /api/users/{userId}/borrow-records` - 用户所有借阅记录
- `GET /api/users/{userId}/borrow-records/active` - 用户当前借阅记录
- `GET /api/users/{userId}/borrow-records/history` - 用户借阅历史

## 数据库配置
在 `src/main/resources/application.yml` 中配置MySQL连接信息：
- URL: `jdbc:mysql://localhost:3306/tushu_db`
- Username: `root`
- Password: `password`

## 启动项目
1. 确保MySQL服务已启动
2. 创建数据库 `tushu_db`
3. 运行 `mvn spring-boot:run` 或在IDE中运行 `TushuApplication.java`
4. 服务将启动在 `http://localhost:8080`

## 前端集成
前端Vue应用可以通过上述API接口与后端交互。建议使用Axios进行HTTP请求。

---

> 注意：本系统使用JPA自动创建表结构（ddl-auto: update），首次启动时会自动创建数据库表。