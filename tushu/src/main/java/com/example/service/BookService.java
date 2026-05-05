package com.example.service;

import com.example.dto.BookDTO;
import com.example.entity.Book;

import java.util.List;

public interface BookService {
    List<BookDTO> findAll();
    BookDTO findById(Long id);
    BookDTO save(BookDTO bookDTO);
    void deleteById(Long id);
    List<BookDTO> searchByTitle(String title);
    List<BookDTO> searchByAuthor(String author);
    List<BookDTO> searchByIsbn(String isbn);
}
