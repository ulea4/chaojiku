package com.example.repository;

import com.example.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("BookRepository 单元测试")
public class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = new Book();
        sampleBook.setIsbn("978-7-111-12345-6");
        sampleBook.setTitle("Java编程思想");
        sampleBook.setAuthor("Bruce Eckel");
        sampleBook.setPublisher("机械工业出版社");
        sampleBook.setPrice(new BigDecimal("89.00"));
        sampleBook.setStockCount(10);
        sampleBook.setDescription("Java经典入门书籍");
        sampleBook.setCreatedAt(LocalDateTime.now());
        sampleBook.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== JpaRepository 继承方法测试 ====================

    @Test
    @DisplayName("save - 保存一本书籍应返回带ID的实体")
    void save_shouldReturnBookWithId() {
        Book saved = bookRepository.save(sampleBook);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getIsbn()).isEqualTo("978-7-111-12345-6");
        assertThat(saved.getTitle()).isEqualTo("Java编程思想");
        assertThat(saved.getAuthor()).isEqualTo("Bruce Eckel");
    }

    @Test
    @DisplayName("findById - 根据ID查找存在的书籍应返回Optional包含值")
    void findById_whenBookExists_shouldReturnBook() {
        Book persisted = entityManager.persistAndFlush(sampleBook);

        Optional<Book> found = bookRepository.findById(persisted.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Java编程思想");
    }

    @Test
    @DisplayName("findById - 根据不存在的ID查找应返回空Optional")
    void findById_whenBookNotExists_shouldReturnEmpty() {
        Optional<Book> found = bookRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findAll - 查询所有书籍应返回全部记录")
    void findAll_shouldReturnAllBooks() {
        entityManager.persistAndFlush(sampleBook);

        Book book2 = new Book();
        book2.setIsbn("978-7-222-22222-2");
        book2.setTitle("设计模式");
        book2.setAuthor("GoF");
        book2.setStockCount(5);
        book2.setCreatedAt(LocalDateTime.now());
        book2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(book2);

        List<Book> books = bookRepository.findAll();

        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Java编程思想", "设计模式");
    }

    @Test
    @DisplayName("deleteById - 根据ID删除书籍后应无法查到")
    void deleteById_shouldRemoveBook() {
        Book persisted = entityManager.persistAndFlush(sampleBook);
        Long id = persisted.getId();

        bookRepository.deleteById(id);
        entityManager.flush();

        assertThat(bookRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("count - 应返回数据库中书籍的总数")
    void count_shouldReturnTotalBooks() {
        entityManager.persistAndFlush(sampleBook);

        Book book2 = new Book();
        book2.setIsbn("978-7-222-22222-2");
        book2.setTitle("设计模式");
        book2.setAuthor("GoF");
        book2.setStockCount(5);
        book2.setCreatedAt(LocalDateTime.now());
        book2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(book2);

        assertThat(bookRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("existsById - 存在的ID应返回true")
    void existsById_whenExists_shouldReturnTrue() {
        Book persisted = entityManager.persistAndFlush(sampleBook);

        assertThat(bookRepository.existsById(persisted.getId())).isTrue();
    }

    @Test
    @DisplayName("existsById - 不存在的ID应返回false")
    void existsById_whenNotExists_shouldReturnFalse() {
        assertThat(bookRepository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("save - 更新已有书籍应修改字段值")
    void save_shouldUpdateExistingBook() {
        Book persisted = entityManager.persistAndFlush(sampleBook);

        persisted.setTitle("Java编程思想(第4版)");
        persisted.setStockCount(20);
        bookRepository.saveAndFlush(persisted);

        Book updated = entityManager.find(Book.class, persisted.getId());
        assertThat(updated.getTitle()).isEqualTo("Java编程思想(第4版)");
        assertThat(updated.getStockCount()).isEqualTo(20);
    }

    @Test
    @DisplayName("deleteAll - 应删除所有书籍")
    void deleteAll_shouldRemoveAllBooks() {
        entityManager.persistAndFlush(sampleBook);

        Book book2 = new Book();
        book2.setIsbn("978-7-222-22222-2");
        book2.setTitle("设计模式");
        book2.setAuthor("GoF");
        book2.setStockCount(5);
        book2.setCreatedAt(LocalDateTime.now());
        book2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(book2);

        bookRepository.deleteAll();
        entityManager.flush();

        assertThat(bookRepository.count()).isEqualTo(0);
    }

    // ==================== 自定义 @Query 方法测试 ====================

    @Test
    @DisplayName("findByTitleContaining - 按标题关键字模糊查询应返回匹配结果")
    void findByTitleContaining_shouldReturnMatchingBooks() {
        entityManager.persistAndFlush(sampleBook);

        Book book2 = new Book();
        book2.setIsbn("978-7-222-22222-2");
        book2.setTitle("Python编程从入门到实践");
        book2.setAuthor("Eric Matthes");
        book2.setStockCount(8);
        book2.setCreatedAt(LocalDateTime.now());
        book2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(book2);

        Book book3 = new Book();
        book3.setIsbn("978-7-333-33333-3");
        book3.setTitle("深入理解Java虚拟机");
        book3.setAuthor("周志明");
        book3.setStockCount(15);
        book3.setCreatedAt(LocalDateTime.now());
        book3.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(book3);

        List<Book> result = bookRepository.findByTitleContaining("Java");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Java编程思想", "深入理解Java虚拟机");
    }

    @Test
    @DisplayName("findByTitleContaining - 无匹配时应返回空列表")
    void findByTitleContaining_whenNoMatch_shouldReturnEmptyList() {
        entityManager.persistAndFlush(sampleBook);

        List<Book> result = bookRepository.findByTitleContaining("不存在的书名");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByAuthorContaining - 按作者关键字模糊查询应返回匹配结果")
    void findByAuthorContaining_shouldReturnMatchingBooks() {
        entityManager.persistAndFlush(sampleBook);

        Book book2 = new Book();
        book2.setIsbn("978-7-222-22222-2");
        book2.setTitle("C++编程思想");
        book2.setAuthor("Bruce Eckel");
        book2.setStockCount(6);
        book2.setCreatedAt(LocalDateTime.now());
        book2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(book2);

        Book book3 = new Book();
        book3.setIsbn("978-7-333-33333-3");
        book3.setTitle("设计模式");
        book3.setAuthor("GoF");
        book3.setStockCount(5);
        book3.setCreatedAt(LocalDateTime.now());
        book3.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(book3);

        List<Book> result = bookRepository.findByAuthorContaining("Bruce");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Book::getAuthor)
                .allMatch(author -> author.contains("Bruce"));
    }

    @Test
    @DisplayName("findByAuthorContaining - 无匹配时应返回空列表")
    void findByAuthorContaining_whenNoMatch_shouldReturnEmptyList() {
        entityManager.persistAndFlush(sampleBook);

        List<Book> result = bookRepository.findByAuthorContaining("不存在的作者");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByIsbn - 精确ISBN查询应返回对应书籍")
    void findByIsbn_shouldReturnMatchingBook() {
        entityManager.persistAndFlush(sampleBook);

        Book book2 = new Book();
        book2.setIsbn("978-7-222-22222-2");
        book2.setTitle("设计模式");
        book2.setAuthor("GoF");
        book2.setStockCount(5);
        book2.setCreatedAt(LocalDateTime.now());
        book2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(book2);

        List<Book> result = bookRepository.findByIsbn("978-7-111-12345-6");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Java编程思想");
    }

    @Test
    @DisplayName("findByIsbn - 不存在的ISBN应返回空列表")
    void findByIsbn_whenNotExists_shouldReturnEmptyList() {
        entityManager.persistAndFlush(sampleBook);

        List<Book> result = bookRepository.findByIsbn("000-0-000-00000-0");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByTitleContaining - 空字符串应返回所有书籍")
    void findByTitleContaining_withEmptyString_shouldReturnAll() {
        entityManager.persistAndFlush(sampleBook);

        Book book2 = new Book();
        book2.setIsbn("978-7-222-22222-2");
        book2.setTitle("设计模式");
        book2.setAuthor("GoF");
        book2.setStockCount(5);
        book2.setCreatedAt(LocalDateTime.now());
        book2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(book2);

        List<Book> result = bookRepository.findByTitleContaining("");

        assertThat(result).hasSize(2);
    }
}
