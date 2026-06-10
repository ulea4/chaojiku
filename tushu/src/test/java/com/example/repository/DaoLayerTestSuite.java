package com.example.repository;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * DAO层（Repository）全部单元测试套件。
 * <p>
 * 在 IntelliJ IDEA 中右键点击本类 → Run 'DaoLayerTestSuite'，
 * 即可一次性执行 DAO 层的所有单元测试（共 42 个）。
 * </p>
 *
 * 包含的测试类：
 * <ul>
 *     <li>{@link BookRepositoryTest}         — 16 个测试</li>
 *     <li>{@link BorrowRecordRepositoryTest} — 13 个测试</li>
 *     <li>{@link UserRepositoryTest}         — 13 个测试</li>
 * </ul>
 */
@Suite
@SuiteDisplayName("DAO层全部单元测试 (42个)")
@SelectClasses({
        BookRepositoryTest.class,
        BorrowRecordRepositoryTest.class,
        UserRepositoryTest.class
})
public class DaoLayerTestSuite {
    // 本类仅作为测试套件入口，不包含测试方法。
    // 右键 → Run 即可运行全部 DAO 层测试。
}
