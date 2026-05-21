package com.example.service.impl;

import com.example.dto.BookDTO;
import com.example.entity.Book;
import com.example.repository.BookRepository;
import com.example.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Override
    public List<BookDTO> findAll() {
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public BookDTO findById(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        return book != null ? convertToDTO(book) : null;
    }
    
    @Override
    public BookDTO save(BookDTO bookDTO) {
        Book book = convertToEntity(bookDTO);
        if (book.getId() == null) {
            book.setCreatedAt(LocalDateTime.now());
        }
        book.setUpdatedAt(LocalDateTime.now());
        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }
    
    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
    
    @Override
    public List<BookDTO> searchByTitle(String title) {
        System.out.println("=== searchByTitle 开始，title=" + title + " ===");
        try {
            List<Book> allBooks = bookRepository.findAll();
            System.out.println("总图书数：" + allBooks.size());
            
            List<BookDTO> result = allBooks.stream()
                    .filter(book -> book.getTitle() != null && book.getTitle().contains(title))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            System.out.println("搜索结果数：" + result.size());
            return result;
        } catch (Exception e) {
            System.err.println("=== searchByTitle 异常 ===");
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public List<BookDTO> searchByAuthor(String author) {
        System.out.println("=== searchByAuthor 开始，author=" + author + " ===");
        try {
            List<Book> allBooks = bookRepository.findAll();
            System.out.println("总图书数：" + allBooks.size());
            
            List<BookDTO> result = allBooks.stream()
                    .filter(book -> book.getAuthor() != null && book.getAuthor().contains(author))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            System.out.println("搜索结果数：" + result.size());
            return result;
        } catch (Exception e) {
            System.err.println("=== searchByAuthor 异常 ===");
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public List<BookDTO> searchByIsbn(String isbn) {
        try {
            List<Book> books = bookRepository.findByIsbn(isbn);
            return books.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    private BookDTO convertToDTO(Book book) {
        if (book == null) return null;
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPublisher(book.getPublisher());
        dto.setPrice(book.getPrice());
        dto.setStockCount(book.getStockCount());
        dto.setDescription(book.getDescription());
        return dto;
    }
    
    private Book convertToEntity(BookDTO dto) {
        if (dto == null) return null;
        Book book = new Book();
        book.setId(dto.getId());
        book.setIsbn(dto.getIsbn());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPublisher(dto.getPublisher());
        book.setPrice(dto.getPrice());
        book.setStockCount(dto.getStockCount());
        book.setDescription(dto.getDescription());
        return book;
    }
}
