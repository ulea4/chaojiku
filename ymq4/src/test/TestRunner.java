package yumaoqou.test;

/**
 * 测试运行器 - 运行所有测试用例
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║             羽毛球大师赛 - 自动化测试                          ║");
        System.out.println("║                     Test Runner v1.0                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        TestResult.Summary totalSummary = new TestResult.Summary();

        // 运行所有测试模块并合并结果
        System.out.println("\n>>> 开始执行测试用例...\n");

        TestResult.Summary coreSummary = new GameCoreTest().runAllTests();
        totalSummary.merge(coreSummary);

        TestResult.Summary physicsSummary = new PhysicsTest().runAllTests();
        totalSummary.merge(physicsSummary);

        TestResult.Summary collisionSummary = new CollisionTest().runAllTests();
        totalSummary.merge(collisionSummary);

        TestResult.Summary aiSummary = new AITest().runAllTests();
        totalSummary.merge(aiSummary);

        TestResult.Summary audioSummary = new AudioTest().runAllTests();
        totalSummary.merge(audioSummary);

        // 打印最终汇总
        printFinalSummary(totalSummary);

        // 如果有失败，返回非0退出码
        if (totalSummary.getFailedTests() > 0) {
            System.exit(1);
        }
    }

    private static void printFinalSummary(TestResult.Summary summary) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                        最终测试报告                           ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║  测试模块: 核心逻辑 | 物理引擎 | 碰撞检测 | AI系统 | 音频系统  ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║  总测试数: %-50d║%n", summary.getTotalTests());
        System.out.printf("║  通过数量: %-50d║%n", summary.getPassedTests());
        System.out.printf("║  失败数量: %-50d║%n", summary.getFailedTests());

        double passRate = summary.getTotalTests() > 0 ?
                (summary.getPassedTests() * 100.0 / summary.getTotalTests()) : 0;
        System.out.printf("║  通过率:   %.1f%%%-46s║%n", passRate, "");
        System.out.printf("║  总耗时:   %dms%-47s║%n", summary.getTotalTime(), "");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // 打印失败的测试详情
        if (summary.getFailedTests() > 0) {
            System.out.println("\n⚠️ 失败测试详情：");
            for (TestResult result : summary.getFailedResults()) {
                System.out.println("  " + result);
            }
            System.out.println("\n⚠️ 存在失败的测试用例，请检查！\n");
        } else {
            System.out.println("\n🎉 恭喜！所有测试用例通过！\n");
        }
    }
}