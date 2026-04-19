package yumaoqou.test;

import yumaoqou.core.GameCore;

/**
 * 碰撞检测测试
 */
public class CollisionTest {

    private GameCore gameCore;
    private TestResult.Summary summary;

    public CollisionTest() {
        this.summary = new TestResult.Summary();
    }

    public TestResult.Summary runAllTests() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                      碰撞检测测试                             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        testHitRadiusConstant();
        testNetCollisionBounds();
        testBoundaryValues();
        testPlayerBoundaryConstants();

        return summary;
    }

    private void testHitRadiusConstant() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("击球半径常量");

        int hitRadius = GameCore.HIT_RADIUS;
        boolean validRadius = hitRadius > 0 && hitRadius < 200;

        result.setPassed(validRadius);
        result.setExpected("击球半径在合理范围内 (0-200)");
        result.setActual("HIT_RADIUS = " + hitRadius);
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [1] " + (validRadius ? "✅" : "❌") + " 击球半径常量测试");
    }

    private void testNetCollisionBounds() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("球网碰撞边界");

        int netLeft = GameCore.getNetCollisionLeft();
        int netRight = GameCore.getNetCollisionRight();
        int netTop = GameCore.getNetCollisionTop();
        int netBottom = GameCore.getNetCollisionBottom();

        boolean validBounds = netLeft < netRight && netTop < netBottom;
        boolean inScreen = netLeft >= 0 && netRight <= GameCore.WIDTH;

        boolean passed = validBounds && inScreen;

        result.setPassed(passed);
        result.setExpected("球网碰撞边界在屏幕内且合理");
        result.setActual(String.format("左=%d，右=%d，上=%d，下=%d",
                netLeft, netRight, netTop, netBottom));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [2] " + (passed ? "✅" : "❌") + " 球网碰撞边界测试");
    }

    private void testBoundaryValues() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("场地边界值");

        boolean leftValid = GameCore.LEFT_BOUNDARY > 0 && GameCore.LEFT_BOUNDARY < GameCore.NET_X;
        boolean rightValid = GameCore.RIGHT_BOUNDARY > GameCore.NET_X &&
                GameCore.RIGHT_BOUNDARY < GameCore.WIDTH;
        boolean player1MaxValid = GameCore.PLAYER1_MAX_X < GameCore.NET_X;
        boolean player2MinValid = GameCore.PLAYER2_MIN_X > GameCore.NET_X;

        boolean passed = leftValid && rightValid && player1MaxValid && player2MinValid;

        result.setPassed(passed);
        result.setExpected("所有边界值合理");
        result.setActual(String.format("左边界=%d，右边界=%d，P1最大=%d，P2最小=%d",
                GameCore.LEFT_BOUNDARY, GameCore.RIGHT_BOUNDARY,
                GameCore.PLAYER1_MAX_X, GameCore.PLAYER2_MIN_X));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [3] " + (passed ? "✅" : "❌") + " 场地边界值测试");
    }

    private void testPlayerBoundaryConstants() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("玩家边界常量");

        boolean startXValid = GameCore.PLAYER1_START_X > GameCore.LEFT_BOUNDARY &&
                GameCore.PLAYER1_START_X < GameCore.PLAYER1_MAX_X;
        boolean startX2Valid = GameCore.PLAYER2_START_X > GameCore.PLAYER2_MIN_X &&
                GameCore.PLAYER2_START_X < GameCore.RIGHT_BOUNDARY;

        boolean passed = startXValid && startX2Valid;

        result.setPassed(passed);
        result.setExpected("玩家起始位置在合法范围内");
        result.setActual(String.format("P1起始=%d (范围%d-%d)，P2起始=%d (范围%d-%d)",
                GameCore.PLAYER1_START_X, GameCore.LEFT_BOUNDARY, GameCore.PLAYER1_MAX_X,
                GameCore.PLAYER2_START_X, GameCore.PLAYER2_MIN_X, GameCore.RIGHT_BOUNDARY));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [4] " + (passed ? "✅" : "❌") + " 玩家边界常量测试");
    }
}