package com.example.repository;

import com.example.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    @Query("SELECT b FROM Book b WHERE b.title LIKE CONCAT('%', :title, '%')")
    List<Book> findByTitleContaining(@Param("title") String title);
    
    @Query("SELECT b FROM Book b WHERE b.author LIKE CONCAT('%', :author, '%')")
    List<Book> findByAuthorContaining(@Param("author") String author);
    
    @Query("SELECT b FROM Book b WHERE b.isbn = :isbn")
    List<Book> findByIsbn(@Param("isbn") String isbn);
}
