package com.example.controller;

import com.example.dto.BookDTO;
import com.example.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping("/inventory")
    public ResponseEntity<Map<String, Object>> getInventoryStatistics() {
        List<BookDTO> books = bookService.findAll();
        
        long totalBooks = books.size();
        long availableBooks = books.stream()
                .filter(book -> book.getStockCount() > 0)
                .count();
        long outOfStockBooks = books.stream()
                .filter(book -> book.getStockCount() == 0)
                .count();
        
        double averageStock = books.stream()
                .mapToInt(BookDTO::getStockCount)
                .average()
                .orElse(0.0);
        
        Map<String, Object> stats = Map.of(
            "totalBooks", totalBooks,
            "availableBooks", availableBooks,
            "outOfStockBooks", outOfStockBooks,
            "averageStock", averageStock
        );
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/top-authors")
    public ResponseEntity<List<Map<String, Object>>> getTopAuthors() {
        List<BookDTO> books = bookService.findAll();
        
        return ResponseEntity.ok(books.stream()
                .collect(Collectors.groupingBy(
                    BookDTO::getAuthor,
                    Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> Map.of("author", entry.getKey(), "bookCount", entry.getValue()))
                .collect(Collectors.toList()));
    }
}
