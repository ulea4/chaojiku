package com.example.service.impl;

import com.example.dto.BorrowRecordDTO;
import com.example.entity.Book;
import com.example.entity.BorrowRecord;
import com.example.entity.User;
import com.example.repository.BookRepository;
import com.example.repository.BorrowRecordRepository;
import com.example.repository.UserRepository;
import com.example.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Override
    public List<BorrowRecordDTO> findAll() {
        return borrowRecordRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public BorrowRecordDTO findById(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id).orElse(null);
        return record != null ? convertToDTO(record) : null;
    }
    
    @Override
    public BorrowRecordDTO borrowBook(Long userId, Long bookId) {
        // 检查用户是否存在
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查图书是否存在且有库存
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null || book.getStockCount() <= 0) {
            throw new RuntimeException("图书不存在或库存不足");
        }
        
        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(LocalDateTime.now());
        record.setStatus("BORROWED");
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        
        // 更新图书库存
        book.setStockCount(book.getStockCount() - 1);
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);
        
        BorrowRecord savedRecord = borrowRecordRepository.save(record);
        return convertToDTO(savedRecord);
    }
    
    @Override
    public BorrowRecordDTO returnBook(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id).orElse(null);
        if (record == null || !"BORROWED".equals(record.getStatus())) {
            throw new RuntimeException("借阅记录不存在或已归还");
        }
        
        // 更新归还日期和状态
        record.setReturnDate(LocalDateTime.now());
        record.setStatus("RETURNED");
        record.setUpdatedAt(LocalDateTime.now());
        
        // 更新图书库存
        Book book = bookRepository.findById(record.getBookId()).orElse(null);
        if (book != null) {
            book.setStockCount(book.getStockCount() + 1);
            book.setUpdatedAt(LocalDateTime.now());
            bookRepository.save(book);
        }
        
        BorrowRecord savedRecord = borrowRecordRepository.save(record);
        return convertToDTO(savedRecord);
    }
    
    @Override
    public List<BorrowRecordDTO> findByUserId(Long userId) {
        return borrowRecordRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BorrowRecordDTO> findByBookId(Long bookId) {
        return borrowRecordRepository.findByBookId(bookId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BorrowRecordDTO> findByUserIdAndStatus(Long userId, String status) {
        return borrowRecordRepository.findByUserIdAndStatus(userId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private BorrowRecordDTO convertToDTO(BorrowRecord record) {
        if (record == null) return null;
        
        BorrowRecordDTO dto = new BorrowRecordDTO();
        dto.setId(record.getId());
        dto.setUserId(record.getUserId());
        dto.setBookId(record.getBookId());
        
        // 获取用户姓名
        User user = userRepository.findById(record.getUserId()).orElse(null);
        if (user != null) {
            dto.setUserName(user.getRealName());
        }
        
        // 获取图书标题
        Book book = bookRepository.findById(record.getBookId()).orElse(null);
        if (book != null) {
            dto.setBookTitle(book.getTitle());
        }
        
        dto.setBorrowDate(record.getBorrowDate());
        dto.setReturnDate(record.getReturnDate());
        dto.setStatus(record.getStatus());
        
        return dto;
    }
}
