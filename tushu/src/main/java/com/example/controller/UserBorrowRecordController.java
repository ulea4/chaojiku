package com.example.controller;

import com.example.dto.BorrowRecordDTO;
import com.example.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserBorrowRecordController {
    
    @Autowired
    private BorrowRecordService borrowRecordService;
    
    @GetMapping("/{userId}/borrow-records")
    public ResponseEntity<List<BorrowRecordDTO>> getUserBorrowRecords(@PathVariable Long userId) {
        return ResponseEntity.ok(borrowRecordService.findByUserId(userId));
    }
    
    @GetMapping("/{userId}/borrow-records/active")
    public ResponseEntity<List<BorrowRecordDTO>> getActiveBorrowRecords(@PathVariable Long userId) {
        return ResponseEntity.ok(borrowRecordService.findByUserIdAndStatus(userId, "BORROWED"));
    }
    
    @GetMapping("/{userId}/borrow-records/history")
    public ResponseEntity<List<BorrowRecordDTO>> getBorrowHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(borrowRecordService.findByUserIdAndStatus(userId, "RETURNED"));
    }
}
