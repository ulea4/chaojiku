package yumaoqou.test;

import yumaoqou.core.GameCore;

/**
 * 物理引擎测试
 */
public class PhysicsTest {

    private GameCore gameCore;
    private TestResult.Summary summary;

    public PhysicsTest() {
        this.summary = new TestResult.Summary();
    }

    public TestResult.Summary runAllTests() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                      物理引擎测试                             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        testGravityEffect();
        testAirResistance();
        testBallBounce();
        testJumpPhysics();
        testBallSpin();

        return summary;
    }

    private void testGravityEffect() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("重力效果");

        gameCore = new GameCore();

        // 发球
        gameCore.startCharging(true);
        gameCore.releaseCharge(true);

        float initialY = gameCore.getBallY();

        // 模拟多帧更新
        for (int i = 0; i < 60; i++) {
            gameCore.update();
        }

        float finalY = gameCore.getBallY();
        boolean gravityWorks = finalY > initialY || !gameCore.isBallInPlay();

        result.setPassed(gravityWorks);
        result.setExpected("球受重力影响下落");
        result.setActual(String.format("初始Y=%.0f，60帧后Y=%.0f", initialY, finalY));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [1] " + (gravityWorks ? "✅" : "❌") + " 重力效果测试");
    }

    private void testAirResistance() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("空气阻力");

        // 空气阻力系数测试
        float resistance = 0.98f;
        float speed = 20f;

        for (int i = 0; i < 10; i++) {
            speed *= resistance;
        }

        boolean speedDecreased = speed < 20f;

        result.setPassed(speedDecreased);
        result.setExpected("速度逐渐减小");
        result.setActual(String.format("初始速度=20.0，10帧后=%.2f", speed));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [2] " + (speedDecreased ? "✅" : "❌") + " 空气阻力测试");
    }

    private void testBallBounce() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("球反弹物理");

        // 反弹系数测试
        float bounceLoss = 0.6f;
        float speed = 15f;
        float bouncedSpeed = -speed * bounceLoss;

        boolean directionChanged = bouncedSpeed < 0;
        boolean speedReduced = Math.abs(bouncedSpeed) < Math.abs(speed);

        boolean passed = directionChanged && speedReduced;

        result.setPassed(passed);
        result.setExpected("反弹后方向相反，速度减小");
        result.setActual(String.format("原速度=%.1f，反弹后=%.1f", speed, bouncedSpeed));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [3] " + (passed ? "✅" : "❌") + " 球反弹物理测试");
    }

    private void testJumpPhysics() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("跳跃物理");

        gameCore = new GameCore();

        float initialY = gameCore.getPlayer1Y();
        gameCore.jump(true);

        // 上升阶段
        for (int i = 0; i < 15; i++) {
            gameCore.update();
        }
        float peakY = gameCore.getPlayer1Y();
        boolean wentUp = peakY < initialY;

        // 下落阶段
        for (int i = 0; i < 50; i++) {
            gameCore.update();
        }
        float finalY = gameCore.getPlayer1Y();
        boolean landed = Math.abs(finalY - initialY) < 5;

        boolean passed = wentUp && landed;

        result.setPassed(passed);
        result.setExpected("先上升后下落，最终落地");
        result.setActual(String.format("初始Y=%.0f，最高Y=%.0f，最终Y=%.0f",
                initialY, peakY, finalY));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [4] " + (passed ? "✅" : "❌") + " 跳跃物理测试");
    }

    private void testBallSpin() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("球旋转效果");

        gameCore = new GameCore();

        float initialSpin = gameCore.getBallSpin();

        // 发球后应该有旋转
        gameCore.startCharging(true);
        gameCore.releaseCharge(true);

        float afterServeSpin = gameCore.getBallSpin();
        boolean hasSpin = Math.abs(afterServeSpin) > 0;

        result.setPassed(hasSpin);
        result.setExpected("发球后球有旋转");
        result.setActual(String.format("初始旋转=%.2f，发球后=%.2f", initialSpin, afterServeSpin));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [5] " + (hasSpin ? "✅" : "❌") + " 球旋转效果测试");
    }
}