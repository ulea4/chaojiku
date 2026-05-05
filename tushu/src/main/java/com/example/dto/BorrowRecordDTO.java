package com.example.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowRecordDTO {
    private Long id;
    private Long userId;
    private Long bookId;
    private String userName;
    private String bookTitle;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private String status;
}
