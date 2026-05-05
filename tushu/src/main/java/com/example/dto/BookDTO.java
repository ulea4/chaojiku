package com.example.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookDTO {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private BigDecimal price;
    private Integer stockCount;
    private String description;
}
