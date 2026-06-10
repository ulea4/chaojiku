package com.example.repository;

import com.example.entity.BorrowRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("BorrowRecordRepository 单元测试")
public class BorrowRecordRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    private BorrowRecord record1;
    private BorrowRecord record2;
    private BorrowRecord record3;

    @BeforeEach
    void setUp() {
        // 用户1借阅书籍1，已借出
        record1 = new BorrowRecord();
        record1.setUserId(1L);
        record1.setBookId(100L);
        record1.setBorrowDate(LocalDateTime.of(2024, 1, 10, 10, 0));
        record1.setStatus("BORROWED");
        record1.setCreatedAt(LocalDateTime.now());
        record1.setUpdatedAt(LocalDateTime.now());

        // 用户1借阅书籍2，已归还
        record2 = new BorrowRecord();
        record2.setUserId(1L);
        record2.setBookId(200L);
        record2.setBorrowDate(LocalDateTime.of(2024, 1, 5, 14, 0));
        record2.setReturnDate(LocalDateTime.of(2024, 1, 15, 16, 0));
        record2.setStatus("RETURNED");
        record2.setCreatedAt(LocalDateTime.now());
        record2.setUpdatedAt(LocalDateTime.now());

        // 用户2借阅书籍1，已借出
        record3 = new BorrowRecord();
        record3.setUserId(2L);
        record3.setBookId(100L);
        record3.setBorrowDate(LocalDateTime.of(2024, 2, 1, 9, 0));
        record3.setStatus("BORROWED");
        record3.setCreatedAt(LocalDateTime.now());
        record3.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== JpaRepository 继承方法测试 ====================

    @Test
    @DisplayName("save - 保存借阅记录应返回带ID的实体")
    void save_shouldReturnRecordWithId() {
        BorrowRecord saved = borrowRecordRepository.save(record1);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getBookId()).isEqualTo(100L);
        assertThat(saved.getStatus()).isEqualTo("BORROWED");
    }

    @Test
    @DisplayName("findById - 根据ID查找存在的记录应返回Optional包含值")
    void findById_whenExists_shouldReturnRecord() {
        BorrowRecord persisted = entityManager.persistAndFlush(record1);

        Optional<BorrowRecord> found = borrowRecordRepository.findById(persisted.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(1L);
        assertThat(found.get().getBookId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("findById - 根据不存在的ID查找应返回空Optional")
    void findById_whenNotExists_shouldReturnEmpty() {
        Optional<BorrowRecord> found = borrowRecordRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findAll - 查询所有借阅记录应返回全部记录")
    void findAll_shouldReturnAllRecords() {
        entityManager.persistAndFlush(record1);
        entityManager.persistAndFlush(record2);
        entityManager.persistAndFlush(record3);

        List<BorrowRecord> records = borrowRecordRepository.findAll();

        assertThat(records).hasSize(3);
    }

    @Test
    @DisplayName("deleteById - 根据ID删除借阅记录后应无法查到")
    void deleteById_shouldRemoveRecord() {
        BorrowRecord persisted = entityManager.persistAndFlush(record1);
        Long id = persisted.getId();

        borrowRecordRepository.deleteById(id);
        entityManager.flush();

        assertThat(borrowRecordRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("count - 应返回数据库中借阅记录的总数")
    void count_shouldReturnTotalRecords() {
        entityManager.persistAndFlush(record1);
        entityManager.persistAndFlush(record2);

        assertThat(borrowRecordRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("save - 更新借阅记录状态(借出→归还)")
    void save_shouldUpdateRecordStatus() {
        BorrowRecord persisted = entityManager.persistAndFlush(record1);

        persisted.setStatus("RETURNED");
        persisted.setReturnDate(LocalDateTime.now());
        borrowRecordRepository.saveAndFlush(persisted);

        BorrowRecord updated = entityManager.find(BorrowRecord.class, persisted.getId());
        assertThat(updated.getStatus()).isEqualTo("RETURNED");
        assertThat(updated.getReturnDate()).isNotNull();
    }

    @Test
    @DisplayName("deleteAll - 应删除所有借阅记录")
    void deleteAll_shouldRemoveAllRecords() {
        entityManager.persistAndFlush(record1);
        entityManager.persistAndFlush(record2);

        borrowRecordRepository.deleteAll();
        entityManager.flush();

        assertThat(borrowRecordRepository.count()).isEqualTo(0);
    }

    // ==================== 自定义派生查询方法测试 ====================

    @Test
    @DisplayName("findByUserId - 按用户ID查询应返回该用户的所有借阅记录")
    void findByUserId_shouldReturnRecordsForUser() {
        entityManager.persistAndFlush(record1);
        entityManager.persistAndFlush(record2);
        entityManager.persistAndFlush(record3);

        List<BorrowRecord> result = borrowRecordRepository.findByUserId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(BorrowRecord::getUserId).allMatch(uid -> uid == 1L);
        assertThat(result).extracting(BorrowRecord::getBookId)
                .containsExactlyInAnyOrder(100L, 200L);
    }

    @Test
    @DisplayName("findByUserId - 用户无借阅记录时应返回空列表")
    void findByUserId_whenNoRecords_shouldReturnEmptyList() {
        entityManager.persistAndFlush(record1);

        List<BorrowRecord> result = borrowRecordRepository.findByUserId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByBookId - 按书籍ID查询应返回该书的所有借阅记录")
    void findByBookId_shouldReturnRecordsForBook() {
        entityManager.persistAndFlush(record1);
        entityManager.persistAndFlush(record2);
        entityManager.persistAndFlush(record3);

        List<BorrowRecord> result = borrowRecordRepository.findByBookId(100L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(BorrowRecord::getBookId).allMatch(bid -> bid == 100L);
        assertThat(result).extracting(BorrowRecord::getUserId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("findByBookId - 书籍无借阅记录时应返回空列表")
    void findByBookId_whenNoRecords_shouldReturnEmptyList() {
        entityManager.persistAndFlush(record1);

        List<BorrowRecord> result = borrowRecordRepository.findByBookId(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUserIdAndStatus - 按用户ID和状态查询应返回匹配记录")
    void findByUserIdAndStatus_shouldReturnMatchingRecords() {
        entityManager.persistAndFlush(record1);
        entityManager.persistAndFlush(record2);
        entityManager.persistAndFlush(record3);

        List<BorrowRecord> borrowed = borrowRecordRepository.findByUserIdAndStatus(1L, "BORROWED");
        List<BorrowRecord> returned = borrowRecordRepository.findByUserIdAndStatus(1L, "RETURNED");

        assertThat(borrowed).hasSize(1);
        assertThat(borrowed.get(0).getBookId()).isEqualTo(100L);
        assertThat(borrowed.get(0).getStatus()).isEqualTo("BORROWED");

        assertThat(returned).hasSize(1);
        assertThat(returned.get(0).getBookId()).isEqualTo(200L);
        assertThat(returned.get(0).getStatus()).isEqualTo("RETURNED");
    }

    @Test
    @DisplayName("findByUserIdAndStatus - 无匹配时应返回空列表")
    void findByUserIdAndStatus_whenNoMatch_shouldReturnEmptyList() {
        entityManager.persistAndFlush(record1);
        entityManager.persistAndFlush(record2);

        // 用户1没有"OVERDUE"状态的记录
        List<BorrowRecord> result = borrowRecordRepository.findByUserIdAndStatus(1L, "OVERDUE");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByUserIdAndStatus - 不同用户相同状态应正确过滤")
    void findByUserIdAndStatus_shouldFilterByUserAndStatus() {
        entityManager.persistAndFlush(record1);
        entityManager.persistAndFlush(record2);
        entityManager.persistAndFlush(record3);

        // 用户1和用户2都有BORROWED状态的记录
        List<BorrowRecord> user1Borrowed = borrowRecordRepository.findByUserIdAndStatus(1L, "BORROWED");
        List<BorrowRecord> user2Borrowed = borrowRecordRepository.findByUserIdAndStatus(2L, "BORROWED");

        assertThat(user1Borrowed).hasSize(1);
        assertThat(user1Borrowed.get(0).getUserId()).isEqualTo(1L);

        assertThat(user2Borrowed).hasSize(1);
        assertThat(user2Borrowed.get(0).getUserId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("综合测试 - 多用户多书籍多状态的复杂场景查询")
    void complexScenario_shouldQueryCorrectly() {
        entityManager.persistAndFlush(record1);
        entityManager.persistAndFlush(record2);
        entityManager.persistAndFlush(record3);

        // 添加更多记录
        BorrowRecord record4 = new BorrowRecord();
        record4.setUserId(2L);
        record4.setBookId(200L);
        record4.setBorrowDate(LocalDateTime.of(2024, 2, 10, 11, 0));
        record4.setReturnDate(LocalDateTime.of(2024, 2, 20, 15, 0));
        record4.setStatus("RETURNED");
        record4.setCreatedAt(LocalDateTime.now());
        record4.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(record4);

        // 验证总数
        assertThat(borrowRecordRepository.count()).isEqualTo(4);

        // 按用户查询
        assertThat(borrowRecordRepository.findByUserId(1L)).hasSize(2);
        assertThat(borrowRecordRepository.findByUserId(2L)).hasSize(2);

        // 按书籍查询
        assertThat(borrowRecordRepository.findByBookId(100L)).hasSize(2);
        assertThat(borrowRecordRepository.findByBookId(200L)).hasSize(2);

        // 按用户+状态查询
        assertThat(borrowRecordRepository.findByUserIdAndStatus(1L, "BORROWED")).hasSize(1);
        assertThat(borrowRecordRepository.findByUserIdAndStatus(1L, "RETURNED")).hasSize(1);
        assertThat(borrowRecordRepository.findByUserIdAndStatus(2L, "BORROWED")).hasSize(1);
        assertThat(borrowRecordRepository.findByUserIdAndStatus(2L, "RETURNED")).hasSize(1);
    }
}
