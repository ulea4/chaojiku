package com.example.repository;

import com.example.entity.User;
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
@DisplayName("UserRepository 单元测试")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUsername("zhangsan");
        sampleUser.setRealName("张三");
        sampleUser.setPhone("13800138000");
        sampleUser.setEmail("zhangsan@example.com");
        sampleUser.setCreatedAt(LocalDateTime.now());
        sampleUser.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== JpaRepository 继承方法测试 ====================

    @Test
    @DisplayName("save - 保存用户应返回带ID的实体")
    void save_shouldReturnUserWithId() {
        User saved = userRepository.save(sampleUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("zhangsan");
        assertThat(saved.getRealName()).isEqualTo("张三");
    }

    @Test
    @DisplayName("findById - 根据ID查找存在的用户应返回Optional包含值")
    void findById_whenExists_shouldReturnUser() {
        User persisted = entityManager.persistAndFlush(sampleUser);

        Optional<User> found = userRepository.findById(persisted.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("zhangsan");
        assertThat(found.get().getRealName()).isEqualTo("张三");
    }

    @Test
    @DisplayName("findById - 根据不存在的ID查找应返回空Optional")
    void findById_whenNotExists_shouldReturnEmpty() {
        Optional<User> found = userRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findAll - 查询所有用户应返回全部记录")
    void findAll_shouldReturnAllUsers() {
        entityManager.persistAndFlush(sampleUser);

        User user2 = new User();
        user2.setUsername("lisi");
        user2.setRealName("李四");
        user2.setPhone("13900139000");
        user2.setEmail("lisi@example.com");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user2);

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername)
                .containsExactlyInAnyOrder("zhangsan", "lisi");
    }

    @Test
    @DisplayName("deleteById - 根据ID删除用户后应无法查到")
    void deleteById_shouldRemoveUser() {
        User persisted = entityManager.persistAndFlush(sampleUser);
        Long id = persisted.getId();

        userRepository.deleteById(id);
        entityManager.flush();

        assertThat(userRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("count - 应返回数据库中用户的总数")
    void count_shouldReturnTotalUsers() {
        entityManager.persistAndFlush(sampleUser);

        User user2 = new User();
        user2.setUsername("lisi");
        user2.setRealName("李四");
        user2.setPhone("13900139000");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user2);

        assertThat(userRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("existsById - 存在的ID应返回true")
    void existsById_whenExists_shouldReturnTrue() {
        User persisted = entityManager.persistAndFlush(sampleUser);

        assertThat(userRepository.existsById(persisted.getId())).isTrue();
    }

    @Test
    @DisplayName("existsById - 不存在的ID应返回false")
    void existsById_whenNotExists_shouldReturnFalse() {
        assertThat(userRepository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("save - 更新已有用户应修改字段值")
    void save_shouldUpdateExistingUser() {
        User persisted = entityManager.persistAndFlush(sampleUser);

        persisted.setPhone("13700137000");
        persisted.setEmail("new_email@example.com");
        userRepository.saveAndFlush(persisted);

        User updated = entityManager.find(User.class, persisted.getId());
        assertThat(updated.getPhone()).isEqualTo("13700137000");
        assertThat(updated.getEmail()).isEqualTo("new_email@example.com");
    }

    @Test
    @DisplayName("deleteAll - 应删除所有用户")
    void deleteAll_shouldRemoveAllUsers() {
        entityManager.persistAndFlush(sampleUser);

        User user2 = new User();
        user2.setUsername("lisi");
        user2.setRealName("李四");
        user2.setPhone("13900139000");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user2);

        userRepository.deleteAll();
        entityManager.flush();

        assertThat(userRepository.count()).isEqualTo(0);
    }

    // ==================== 自定义派生查询方法测试 ====================

    @Test
    @DisplayName("findByUsername - 按用户名精确查询应返回对应用户")
    void findByUsername_shouldReturnMatchingUser() {
        entityManager.persistAndFlush(sampleUser);

        User user2 = new User();
        user2.setUsername("lisi");
        user2.setRealName("李四");
        user2.setPhone("13900139000");
        user2.setEmail("lisi@example.com");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user2);

        User found = userRepository.findByUsername("zhangsan");

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("zhangsan");
        assertThat(found.getRealName()).isEqualTo("张三");
        assertThat(found.getPhone()).isEqualTo("13800138000");
        assertThat(found.getEmail()).isEqualTo("zhangsan@example.com");
    }

    @Test
    @DisplayName("findByUsername - 不存在的用户名应返回null")
    void findByUsername_whenNotExists_shouldReturnNull() {
        entityManager.persistAndFlush(sampleUser);

        User found = userRepository.findByUsername("nonexistent");

        assertThat(found).isNull();
    }

    @Test
    @DisplayName("findByUsername - 用户名区分大小写")
    void findByUsername_shouldBeCaseSensitive() {
        entityManager.persistAndFlush(sampleUser);

        // "Zhangsan" 与 "zhangsan" 不同
        User found = userRepository.findByUsername("Zhangsan");

        assertThat(found).isNull();
    }

    @Test
    @DisplayName("findByUsername - 空字符串应返回null")
    void findByUsername_withEmptyString_shouldReturnNull() {
        entityManager.persistAndFlush(sampleUser);

        User found = userRepository.findByUsername("");

        assertThat(found).isNull();
    }

    @Test
    @DisplayName("findByUsername - 多个用户时应精确匹配")
    void findByUsername_withMultipleUsers_shouldReturnExactMatch() {
        entityManager.persistAndFlush(sampleUser);

        User user2 = new User();
        user2.setUsername("zhangsan2");
        user2.setRealName("张三2");
        user2.setPhone("13900139001");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user2);

        User user3 = new User();
        user3.setUsername("zhangsan_admin");
        user3.setRealName("张三管理员");
        user3.setPhone("13900139002");
        user3.setCreatedAt(LocalDateTime.now());
        user3.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user3);

        User found = userRepository.findByUsername("zhangsan");

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("zhangsan");
        assertThat(found.getRealName()).isEqualTo("张三");
    }

    @Test
    @DisplayName("save - 用户只有必填字段时也能保存成功")
    void save_withMinimalFields_shouldSucceed() {
        User minimalUser = new User();
        minimalUser.setUsername("minimal_user");
        minimalUser.setRealName("最小用户");

        User saved = userRepository.save(minimalUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("minimal_user");
        assertThat(saved.getPhone()).isNull();
        assertThat(saved.getEmail()).isNull();
    }
}
