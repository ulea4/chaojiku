package yumaoqou.test;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试结果记录类
 */
public class TestResult {
    private String testName;
    private boolean passed;
    private String expected;
    private String actual;
    private String message;
    private long executionTime;

    public TestResult(String testName) {
        this.testName = testName;
        this.passed = false;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getTestName() {
        return testName;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(passed ? "✅ PASS" : "❌ FAIL").append(" - ").append(testName);
        sb.append(" (").append(executionTime).append("ms)");
        if (!passed) {
            sb.append("\n    预期: ").append(expected);
            sb.append("\n    实际: ").append(actual);
            if (message != null) {
                sb.append("\n    信息: ").append(message);
            }
        }
        return sb.toString();
    }

    /**
     * 测试结果汇总
     */
    public static class Summary {
        private List<TestResult> results = new ArrayList<>();

        public void addResult(TestResult result) {
            results.add(result);
        }

        /**
         * 合并另一个 Summary 的结果
         */
        public void merge(Summary other) {
            if (other != null && other.results != null) {
                this.results.addAll(other.results);
            }
        }

        /**
         * 获取所有结果
         */
        public List<TestResult> getResults() {
            return results;
        }

        public int getTotalTests() {
            return results.size();
        }

        public int getPassedTests() {
            int count = 0;
            for (TestResult r : results) {
                if (r.isPassed()) count++;
            }
            return count;
        }

        public int getFailedTests() {
            return getTotalTests() - getPassedTests();
        }

        public long getTotalTime() {
            long total = 0;
            for (TestResult r : results) {
                total += r.getExecutionTime();
            }
            return total;
        }

        public List<TestResult> getFailedResults() {
            List<TestResult> failed = new ArrayList<>();
            for (TestResult result : results) {
                if (!result.isPassed()) {
                    failed.add(result);
                }
            }
            return failed;
        }

        public void printSummary() {
            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                        测试结果汇总                           ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");

            for (TestResult result : results) {
                System.out.println("║ " + result);
            }

            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.printf("║ 总计: %d  |  通过: %d  |  失败: %d  |  耗时: %dms%n",
                    getTotalTests(), getPassedTests(), getFailedTests(), getTotalTime());

            double passRate = getTotalTests() > 0 ?
                    (getPassedTests() * 100.0 / getTotalTests()) : 0;
            System.out.printf("║ 通过率: %.1f%%%n", passRate);
            System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        }
    }
}