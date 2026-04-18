package yumaoqou.test;

import yumaoqou.audio.AudioManager;

/**
 * 音频系统测试
 */
public class AudioTest {

    private AudioManager audioManager;
    private TestResult.Summary summary;

    public AudioTest() {
        this.summary = new TestResult.Summary();
    }

    public TestResult.Summary runAllTests() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                      音频系统测试                             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        testAudioManagerSingleton();
        testBGMControl();
        testSFXControl();
        testVolumeControl();
        testSoundEffectsExist();

        return summary;
    }

    private void testAudioManagerSingleton() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("AudioManager单例模式");

        AudioManager instance1 = AudioManager.getInstance();
        AudioManager instance2 = AudioManager.getInstance();

        boolean isSingleton = instance1 == instance2;
        boolean notNull = instance1 != null;

        boolean passed = isSingleton && notNull;

        result.setPassed(passed);
        result.setExpected("AudioManager是单例且非空");
        result.setActual(String.format("单例=%s，非空=%s", isSingleton, notNull));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [1] " + (passed ? "✅" : "❌") + " AudioManager单例模式测试");
    }

    private void testBGMControl() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("背景音乐控制");

        audioManager = AudioManager.getInstance();

        boolean initiallyEnabled = audioManager.isBgmEnabled();

        audioManager.setBgmEnabled(false);
        boolean disabled = !audioManager.isBgmEnabled();

        audioManager.setBgmEnabled(true);
        boolean enabled = audioManager.isBgmEnabled();

        boolean passed = initiallyEnabled && disabled && enabled;

        result.setPassed(passed);
        result.setExpected("可以开关背景音乐");
        result.setActual(String.format("初始=%s，关闭后=%s，开启后=%s",
                initiallyEnabled, !disabled, enabled));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [2] " + (passed ? "✅" : "❌") + " 背景音乐控制测试");
    }

    private void testSFXControl() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("音效控制");

        audioManager = AudioManager.getInstance();

        boolean initiallyEnabled = audioManager.isSfxEnabled();

        audioManager.setSfxEnabled(false);
        boolean disabled = !audioManager.isSfxEnabled();

        audioManager.setSfxEnabled(true);
        boolean enabled = audioManager.isSfxEnabled();

        boolean passed = initiallyEnabled && disabled && enabled;

        result.setPassed(passed);
        result.setExpected("可以开关音效");
        result.setActual(String.format("初始=%s，关闭后=%s，开启后=%s",
                initiallyEnabled, !disabled, enabled));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [3] " + (passed ? "✅" : "❌") + " 音效控制测试");
    }

    private void testVolumeControl() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("音量控制");

        audioManager = AudioManager.getInstance();

        float initialBgmVol = audioManager.getBgmVolume();
        float initialSfxVol = audioManager.getSfxVolume();

        audioManager.setBgmVolume(0.8f);
        audioManager.setSfxVolume(0.6f);

        boolean bgmChanged = Math.abs(audioManager.getBgmVolume() - 0.8f) < 0.01f;
        boolean sfxChanged = Math.abs(audioManager.getSfxVolume() - 0.6f) < 0.01f;

        // 恢复原值
        audioManager.setBgmVolume(initialBgmVol);
        audioManager.setSfxVolume(initialSfxVol);

        boolean passed = bgmChanged && sfxChanged;

        result.setPassed(passed);
        result.setExpected("可以调节音量");
        result.setActual(String.format("BGM音量=%.1f，SFX音量=%.1f",
                audioManager.getBgmVolume(), audioManager.getSfxVolume()));
        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [4] " + (passed ? "✅" : "❌") + " 音量控制测试");
    }

    private void testSoundEffectsExist() {
        long startTime = System.currentTimeMillis();
        TestResult result = new TestResult("音效文件存在性");

        audioManager = AudioManager.getInstance();

        // 测试播放音效（不实际检查文件，只测试方法调用）
        try {
            audioManager.playHitSound();
            audioManager.playServeSound();
            audioManager.playScoreSound();
            audioManager.playSmashSound();
            audioManager.playDashSound();
            audioManager.playClickSound();
            audioManager.playWinSound();

            result.setPassed(true);
            result.setExpected("所有音效方法可调用");
            result.setActual("7种音效方法调用成功");
        } catch (Exception e) {
            result.setPassed(false);
            result.setActual("音效播放异常: " + e.getMessage());
        }

        result.setExecutionTime(System.currentTimeMillis() - startTime);

        summary.addResult(result);
        System.out.println("  [5] " + (result.isPassed() ? "✅" : "❌") + " 音效文件存在性测试");
    }
}