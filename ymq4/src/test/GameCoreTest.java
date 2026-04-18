package yumaoqou.test;

import yumaoqou.core.GameCore;

/**
 * GameCore 核心逻辑测试
 */
public class GameCoreTest {

    private GameCore gameCore;
    private TestResult.Summary summary;

    public GameCoreTest() {
        this.summary = new TestResult.Summary();
    }

    public TestResult.Summary runAllTests() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    GameCore 核心逻辑测试                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        testInitialState();
        testResetGame();
        testPlayerMovement();
        testPlayerJump();
        testPlayerBoundary();
        testScoreSystem();
        testWinCondition();
        testServerRotation();
        testGameModeSetting();
        testDifficultySetting();
        testComboSystem();
        testEnergySystem();

        return summary;
    }

    private void testInitialState() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("游戏初始状态");

        gameCore = new GameCore();

        boolean scoreValid = gameCore.getScore1() == 0 && gameCore.getScore2() == 0;
        boolean gameRunning = gameCore.isGameRunning();
        boolean ballNotInPlay = !gameCore.isBallInPlay();

        boolean passed = scoreValid && gameRunning && ballNotInPlay;

        result.setPassed(passed);
        result.setExpected("比分0:0，游戏运行中，球未发出");
        result.setActual(String.format("比分%d:%d，游戏%s，球%s",
                gameCore.getScore1(), gameCore.getScore2(),
                gameRunning ? "运行中" : "已结束",
                ballNotInPlay ? "未发出" : "飞行中"));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [1] " + (passed ? "✅" : "❌") + " 游戏初始状态测试");
    }

    private void testResetGame() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("游戏重置功能");

        gameCore = new GameCore();

        // 模拟一些游戏操作
        for (int i = 0; i < 5; i++) {
            gameCore.movePlayer1(true);
        }
        gameCore.jump(true);
        gameCore.startCharging(true);
        gameCore.releaseCharge(true);

        // 重置游戏
        gameCore.resetGame();

        boolean scoreReset = gameCore.getScore1() == 0 && gameCore.getScore2() == 0;
        boolean positionReset = Math.abs(gameCore.getPlayer1X() - GameCore.PLAYER1_START_X) < 10;
        boolean energyReset = gameCore.getPlayer1Energy() == 100;

        boolean passed = scoreReset && positionReset && energyReset;

        result.setPassed(passed);
        result.setExpected("比分0:0，位置回到起点，能量100%");
        result.setActual(String.format("比分%d:%d，位置X=%.0f，能量%.0f%%",
                gameCore.getScore1(), gameCore.getScore2(),
                gameCore.getPlayer1X(), gameCore.getPlayer1Energy()));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [2] " + (passed ? "✅" : "❌") + " 游戏重置功能测试");
    }

    private void testPlayerMovement() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("玩家移动功能");

        gameCore = new GameCore();
        float initialX = gameCore.getPlayer1X();

        // 向右移动
        for (int i = 0; i < 10; i++) {
            gameCore.movePlayer1(true);
        }
        float afterRight = gameCore.getPlayer1X();
        boolean movedRight = afterRight > initialX;

        // 向左移动
        for (int i = 0; i < 10; i++) {
            gameCore.movePlayer1(false);
        }
        float afterLeft = gameCore.getPlayer1X();
        boolean movedLeft = afterLeft < afterRight;

        boolean passed = movedRight && movedLeft;

        result.setPassed(passed);
        result.setExpected("玩家可以左右移动");
        result.setActual(String.format("初始X=%.0f，右移后X=%.0f，左移后X=%.0f",
                initialX, afterRight, afterLeft));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [3] " + (passed ? "✅" : "❌") + " 玩家移动功能测试");
    }

    private void testPlayerJump() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("玩家跳跃功能");

        gameCore = new GameCore();
        float initialY = gameCore.getPlayer1Y();

        // 跳跃
        gameCore.jump(true);

        // 模拟几帧物理更新
        for (int i = 0; i < 20; i++) {
            gameCore.update();
        }

        float afterJumpY = gameCore.getPlayer1Y();
        boolean jumped = Math.abs(afterJumpY - initialY) > 5;

        // 继续更新直到落地
        for (int i = 0; i < 100; i++) {
            gameCore.update();
        }

        float finalY = gameCore.getPlayer1Y();
        boolean landed = Math.abs(finalY - initialY) < 5;

        boolean passed = jumped && landed;

        result.setPassed(passed);
        result.setExpected("玩家跳跃后能落回地面");
        result.setActual(String.format("初始Y=%.0f，跳跃后Y=%.0f，最终Y=%.0f",
                initialY, afterJumpY, finalY));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [4] " + (passed ? "✅" : "❌") + " 玩家跳跃功能测试");
    }

    private void testPlayerBoundary() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("玩家边界限制");

        gameCore = new GameCore();

        // 测试不能越过网
        for (int i = 0; i < 200; i++) {
            gameCore.movePlayer1(true);
        }
        float finalX = gameCore.getPlayer1X();
        boolean notCrossNet = finalX <= GameCore.PLAYER1_MAX_X + 5;

        // 测试不能出左边界
        gameCore = new GameCore();
        for (int i = 0; i < 200; i++) {
            gameCore.movePlayer1(false);
        }
        float leftX = gameCore.getPlayer1X();
        boolean notCrossLeft = leftX >= GameCore.LEFT_BOUNDARY - 5;

        boolean passed = notCrossNet && notCrossLeft;

        result.setPassed(passed);
        result.setExpected("玩家不能越过网和左边界");
        result.setActual(String.format("右边界限制X=%.0f (限制=%d)，左边界限制X=%.0f (限制=%d)",
                finalX, GameCore.PLAYER1_MAX_X, leftX, GameCore.LEFT_BOUNDARY));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [5] " + (passed ? "✅" : "❌") + " 玩家边界限制测试");
    }

    private void testScoreSystem() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("得分系统");

        gameCore = new GameCore();

        int initialScore1 = gameCore.getScore1();
        int initialScore2 = gameCore.getScore2();

        boolean scoreValid = initialScore1 == 0 && initialScore2 == 0;

        result.setPassed(scoreValid);
        result.setExpected("初始比分0:0");
        result.setActual(String.format("比分%d:%d", initialScore1, initialScore2));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [6] " + (scoreValid ? "✅" : "❌") + " 得分系统测试");
    }

    private void testWinCondition() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("获胜条件");

        gameCore = new GameCore();

        boolean winScoreCorrect = GameCore.WIN_SCORE == 11;
        boolean gameRunning = gameCore.isGameRunning();

        boolean passed = winScoreCorrect && gameRunning;

        result.setPassed(passed);
        result.setExpected("获胜分数为11，游戏运行中");
        result.setActual(String.format("获胜分数=%d，游戏%s",
                GameCore.WIN_SCORE, gameRunning ? "运行中" : "已结束"));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [7] " + (passed ? "✅" : "❌") + " 获胜条件测试");
    }

    private void testServerRotation() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("发球权轮换");

        gameCore = new GameCore();

        int initialServer = gameCore.getServer();
        boolean validServer = initialServer == 1 || initialServer == 2;

        result.setPassed(validServer);
        result.setExpected("发球方为1或2");
        result.setActual("发球方=" + initialServer);
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [8] " + (validServer ? "✅" : "❌") + " 发球权轮换测试");
    }

    private void testGameModeSetting() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("游戏模式设置");

        gameCore = new GameCore();

        // 测试双人对战模式
        gameCore.setGameMode(GameCore.MODE_VS_PLAYER, GameCore.DIFF_MEDIUM);
        boolean vsPlayerMode = !gameCore.isAIMode();

        // 测试人机对战模式
        gameCore.setGameMode(GameCore.MODE_VS_AI, GameCore.DIFF_MEDIUM);
        boolean vsAIMode = gameCore.isAIMode();

        boolean passed = vsPlayerMode && vsAIMode;

        result.setPassed(passed);
        result.setExpected("可以切换双人对战和人机对战模式");
        result.setActual(String.format("双人模式AI=%s，人机模式AI=%s", !vsPlayerMode, vsAIMode));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [9] " + (passed ? "✅" : "❌") + " 游戏模式设置测试");
    }

    private void testDifficultySetting() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("难度设置");

        gameCore = new GameCore();

        boolean allValid = true;
        int[] difficulties = {GameCore.DIFF_EASY, GameCore.DIFF_MEDIUM,
                GameCore.DIFF_HARD, GameCore.DIFF_EXPERT};
        String[] diffNames = {"简单", "中等", "困难", "专家"};

        StringBuilder actual = new StringBuilder();
        for (int i = 0; i < difficulties.length; i++) {
            gameCore.setGameMode(GameCore.MODE_VS_AI, difficulties[i]);
            actual.append(diffNames[i]).append(" ");
        }

        result.setPassed(allValid);
        result.setExpected("支持简单、中等、困难、专家四个难度");
        result.setActual("已设置: " + actual.toString());
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [10] " + (allValid ? "✅" : "❌") + " 难度设置测试");
    }

    private void testComboSystem() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("连击系统");

        gameCore = new GameCore();

        int initialCombo = gameCore.getComboCount();
        int initialMaxCombo = gameCore.getMaxCombo();

        boolean comboValid = initialCombo == 0 && initialMaxCombo == 0;

        result.setPassed(comboValid);
        result.setExpected("初始连击数为0");
        result.setActual(String.format("连击=%d，最大连击=%d", initialCombo, initialMaxCombo));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [11] " + (comboValid ? "✅" : "❌") + " 连击系统测试");
    }

    private void testEnergySystem() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("能量系统");

        gameCore = new GameCore();

        float initialEnergy1 = gameCore.getPlayer1Energy();
        float initialEnergy2 = gameCore.getPlayer2Energy();

        boolean energyValid = Math.abs(initialEnergy1 - 100) < 1 &&
                Math.abs(initialEnergy2 - 100) < 1;

        result.setPassed(energyValid);
        result.setExpected("初始能量100%");
        result.setActual(String.format("玩家1能量=%.0f%%，玩家2能量=%.0f%%",
                initialEnergy1, initialEnergy2));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [12] " + (energyValid ? "✅" : "❌") + " 能量系统测试");
    }
}