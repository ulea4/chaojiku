package com.example.service;

import com.example.dto.BorrowRecordDTO;

import java.util.List;

public interface BorrowRecordService {
    
    List<BorrowRecordDTO> findAll();
    
    BorrowRecordDTO findById(Long id);
    
    BorrowRecordDTO borrowBook(Long userId, Long bookId);
    
    BorrowRecordDTO returnBook(Long id);
    
    List<BorrowRecordDTO> findByUserId(Long userId);
    
    List<BorrowRecordDTO> findByBookId(Long bookId);
    
    List<BorrowRecordDTO> findByUserIdAndStatus(Long userId, String status);
}
