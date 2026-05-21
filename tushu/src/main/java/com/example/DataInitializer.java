package com.example;

import com.example.dto.BookDTO;
import com.example.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private BookService bookService;
    
    @Override
    public void run(String... args) throws Exception {
        // 检查是否已经有数据
        List<BookDTO> existingBooks = bookService.findAll();
        if (existingBooks.size() > 5) {
            System.out.println("数据已存在，跳过初始化");
            return;
        }
        
        System.out.println("开始初始化图书数据...");
        
        List<BookDTO> books = Arrays.asList(
            // Python 相关
            createBook(null, "978-7-115-54501-0", "Python编程：从入门到实践", "Eric Matthes", "人民邮电出版社", 89.00, 18, "Python入门经典教程，适合零基础学习者"),
            createBook(null, "978-7-111-60309-4", "流畅的Python", "Luciano Ramalho", "人民邮电出版社", 139.00, 12, "深入理解Python高级特性"),
            
            // Java 进阶
            createBook(null, "978-7-111-54742-0", "Effective Java", "Joshua Bloch", "机械工业出版社", 99.00, 15, "Java编程最佳实践"),
            createBook(null, "978-7-115-45621-0", "深入理解Java虚拟机", "周志明", "机械工业出版社", 129.00, 10, "JVM原理与调优实战"),
            createBook(null, "978-7-121-28593-0", "并发编程的艺术", "方腾飞", "电子工业出版社", 79.00, 14, "Java并发编程深入解析"),
            
            // 前端开发
            createBook(null, "978-7-115-48450-0", "JavaScript高级程序设计", "Matt Frisbie", "人民邮电出版社", 129.00, 16, "前端开发经典教材"),
            createBook(null, "978-7-115-50892-0", "Vue.js设计与实现", "霍春阳", "人民邮电出版社", 109.00, 13, "Vue 3源码分析与实战"),
            createBook(null, "978-7-111-61651-0", "CSS揭秘", "Lea Verou", "机械工业出版社", 79.00, 11, "CSS高级技巧与最佳实践"),
            
            // 数据库与大数据
            createBook(null, "978-7-115-46365-0", "Redis设计与实现", "黄健宏", "机械工业出版社", 89.00, 17, "Redis内部机制详解"),
            createBook(null, "978-7-121-33891-0", "Elasticsearch权威指南", "Clinton Gormley", "电子工业出版社", 99.00, 9, "全文搜索引擎实战"),
            
            // 软件工程与架构
            createBook(null, "978-7-111-60821-0", "代码整洁之道", "Robert C. Martin", "人民邮电出版社", 79.00, 20, "编写高质量代码的准则"),
            createBook(null, "978-7-115-48867-0", "架构整洁之道", "Robert C. Martin", "人民邮电出版社", 89.00, 14, "软件架构设计最佳实践"),
            createBook(null, "978-7-111-59292-0", "重构：改善既有代码的设计", "Martin Fowler", "机械工业出版社", 99.00, 16, "代码重构经典指南"),
            
            // 计算机网络与安全
            createBook(null, "978-7-115-49449-0", "计算机网络：自顶向下方法", "James F. Kurose", "人民邮电出版社", 89.00, 13, "网络原理经典教材"),
            createBook(null, "978-7-121-34653-0", "Web安全深度剖析", "张炳帅", "电子工业出版社", 79.00, 11, "Web应用安全防护实战"),
            
            // DevOps与运维
            createBook(null, "978-7-115-53811-0", "Docker技术入门与实战", "杨保华", "人民邮电出版社", 79.00, 15, "容器化技术实战指南"),
            createBook(null, "978-7-121-35678-0", "Kubernetes权威指南", "龚正", "电子工业出版社", 119.00, 12, "容器编排与云原生实践"),
            
            // 数据结构与算法
            createBook(null, "978-7-115-48921-0", "算法（第4版）", "Robert Sedgewick", "人民邮电出版社", 128.00, 10, "算法经典教材，图文并茂"),
            createBook(null, "978-7-111-60437-0", "剑指Offer", "何海涛", "电子工业出版社", 79.00, 18, "程序员面试金典"),
            
            // 操作系统
            createBook(null, "978-7-111-60301-0", "现代操作系统", "Andrew S. Tanenbaum", "机械工业出版社", 109.00, 14, "操作系统原理经典教材"),
            createBook(null, "978-7-115-48294-0", "深入理解Linux内核", "Daniel P. Bovet", "人民邮电出版社", 99.00, 11, "Linux内核源码分析")
        );
        
        for (BookDTO book : books) {
            try {
                bookService.save(book);
                System.out.println("已添加: " + book.getTitle());
            } catch (Exception e) {
                System.err.println("添加失败: " + book.getTitle() + " - " + e.getMessage());
            }
        }
        
        System.out.println("图书数据初始化完成！共添加 " + books.size() + " 本书");
    }
    
    private BookDTO createBook(Long id, String isbn, String title, String author, 
                               String publisher, double price, int stockCount, String description) {
        BookDTO book = new BookDTO();
        book.setId(id);
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setPrice(new BigDecimal(price));
        book.setStockCount(stockCount);
        book.setDescription(description);
        return book;
    }
}
