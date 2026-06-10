package com.example.repository;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;

/**
 * DAO层全部单元测试 — 可独立运行的 Java 程序。
 * <p>
 * 在 IntelliJ IDEA 中右键点击本类 → Run 'AllDaoTestsRunner.main()'，
 * 即可在控制台列出所有测试并执行，输出每个测试的通过/失败状态和统计汇总。
 * </p>
 *
 * 使用方式：
 * <pre>
 *   直接运行本类的 main 方法即可。
 *   无需 Maven / Gradle，无需外部数据库（使用 H2 内存数据库）。
 * </pre>
 */
public class AllDaoTestsRunner {

    public static void main(String[] args) {
        printBanner();

        // ── 1. 构建测试发现请求：选中三个 Repository 测试类 ──
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        DiscoverySelectors.selectClass(BookRepositoryTest.class),
                        DiscoverySelectors.selectClass(BorrowRecordRepositoryTest.class),
                        DiscoverySelectors.selectClass(UserRepositoryTest.class)
                )
                .build();

        // ── 2. 创建 Launcher 并注册监听器 ──
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);

        // ── 3. 执行测试 ──
        Instant start = Instant.now();
        launcher.execute(request);
        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);

        // ── 4. 输出详细结果 ──
        TestExecutionSummary summary = listener.getSummary();
        printDetailedResults(summary);

        // ── 5. 输出汇总统计 ──
        printSummary(summary, elapsed);

        // ── 6. 如果有失败，以非零状态退出 ──
        if (summary.getTotalFailureCount() > 0) {
            System.exit(1);
        }
    }

    // ======================== 输出格式化工具 ========================

    private static void printBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║        图书管理系统 — DAO层全部单元测试运行器            ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  测试类:                                                ║");
        System.out.println("║    1. BookRepositoryTest          (16 个测试)           ║");
        System.out.println("║    2. BorrowRecordRepositoryTest  (13 个测试)           ║");
        System.out.println("║    3. UserRepositoryTest          (13 个测试)           ║");
        System.out.println("║  共计: 42 个测试                                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("正在启动 Spring Boot 测试上下文 (H2内存数据库)...");
        System.out.println("────────────────────────────────────────────────────────────");
    }

    private static void printDetailedResults(TestExecutionSummary summary) {
        PrintWriter pw = new PrintWriter(System.out);

        // 输出所有失败的测试详情
        if (!summary.getFailures().isEmpty()) {
            System.out.println();
            System.out.println("══════════════════════ 失败测试详情 ══════════════════════");
            summary.printFailuresTo(pw);
            pw.flush();
        }
    }

    private static void printSummary(TestExecutionSummary summary, Duration elapsed) {
        System.out.println();
        System.out.println("══════════════════════ 测试结果汇总 ══════════════════════");

        long total    = summary.getTestsFoundCount();
        long success  = summary.getTestsSucceededCount();
        long failed   = summary.getTestsFailedCount();
        long skipped  = summary.getTestsSkippedCount();
        long aborted  = summary.getTestsAbortedCount();

        // 状态图标
        String statusIcon = (failed == 0) ? "[ALL PASSED]" : "[HAS FAILURES]";

        System.out.printf("  状态    : %s%n", statusIcon);
        System.out.printf("  总测试数: %d%n", total);
        System.out.printf("  通过    : %d%n", success);
        System.out.printf("  失败    : %d%n", failed);
        System.out.printf("  跳过    : %d%n", skipped);
        System.out.printf("  中止    : %d%n", aborted);
        System.out.printf("  耗时    : %d ms%n", elapsed.toMillis());

        System.out.println("══════════════════════════════════════════════════════════");

        if (failed == 0) {
            System.out.println("  ALL 42 TESTS PASSED SUCCESSFULLY!");
        } else {
            System.out.printf("  %d test(s) FAILED. Check details above.%n", failed);
        }

        System.out.println("══════════════════════════════════════════════════════════");
        System.out.println();
    }
}
