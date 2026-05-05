package com.example.controller;

import com.example.dto.BorrowRecordDTO;
import com.example.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow-records")
@CrossOrigin(origins = "*")
public class BorrowRecordController {
    
    @Autowired
    private BorrowRecordService borrowRecordService;
    
    @GetMapping
    public ResponseEntity<List<BorrowRecordDTO>> getAllBorrowRecords() {
        return ResponseEntity.ok(borrowRecordService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BorrowRecordDTO> getBorrowRecordById(@PathVariable Long id) {
        BorrowRecordDTO record = borrowRecordService.findById(id);
        return record != null ? ResponseEntity.ok(record) : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/borrow")
    public ResponseEntity<BorrowRecordDTO> borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        return ResponseEntity.ok(borrowRecordService.borrowBook(userId, bookId));
    }
    
    @PostMapping("/return/{id}")
    public ResponseEntity<BorrowRecordDTO> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowRecordService.returnBook(id));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BorrowRecordDTO>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(borrowRecordService.findByUserId(userId));
    }
    
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BorrowRecordDTO>> findByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(borrowRecordService.findByBookId(bookId));
    }
    
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<BorrowRecordDTO>> findByUserIdAndStatus(@PathVariable Long userId, @PathVariable String status) {
        return ResponseEntity.ok(borrowRecordService.findByUserIdAndStatus(userId, status));
    }
}
