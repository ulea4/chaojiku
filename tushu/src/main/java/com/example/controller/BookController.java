package com.example.controller;

import com.example.dto.BookDTO;
import com.example.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        BookDTO book = bookService.findById(id);
        return book != null ? ResponseEntity.ok(book) : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(bookService.save(bookDTO));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        bookDTO.setId(id);
        return ResponseEntity.ok(bookService.save(bookDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search/title")
    public ResponseEntity<List<BookDTO>> searchByTitle(@RequestParam String title) {
        try {
            System.out.println("==============================");
            System.out.println("开始搜索图书，关键词：" + title);
            System.out.println("==============================");
            List<BookDTO> result = bookService.searchByTitle(title);
            System.out.println("搜索结果数量：" + result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("搜索图书时发生错误：");
            System.err.println("错误类型：" + e.getClass().getName());
            System.err.println("错误信息：" + e.getMessage());
            System.err.println("========================================");
            e.printStackTrace();
            throw e;
        }
    }
    
    @GetMapping("/search/author")
    public ResponseEntity<List<BookDTO>> searchByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookService.searchByAuthor(author));
    }
    
    @GetMapping("/search/isbn")
    public ResponseEntity<List<BookDTO>> searchByIsbn(@RequestParam String isbn) {
        return ResponseEntity.ok(bookService.searchByIsbn(isbn));
    }
}
