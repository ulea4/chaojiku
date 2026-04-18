package yumaoqou.audio;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 音频管理器 - 管理背景音乐和游戏音效
 */
public class AudioManager {
    private static AudioManager instance;
    private ExecutorService audioExecutor;

    // 背景音乐
    private Clip bgmClip;
    private String currentBgm;
    private float bgmVolume = 0.5f;
    private boolean bgmEnabled = true;

    // 音效
    private Map<String, Clip> soundClips;
    private float sfxVolume = 0.7f;
    private boolean sfxEnabled = true;

    // 音效缓存
    private Map<String, byte[]> soundCache;

    private AudioManager() {
        audioExecutor = Executors.newFixedThreadPool(3);
        soundClips = new HashMap<>();
        soundCache = new HashMap<>();

        // 预加载音效
        preloadSounds();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * 预加载音效文件
     */
    private void preloadSounds() {
        // 尝试从外部文件加载音效
        boolean externalLoaded = loadExternalSounds();
        
        // 如果外部文件加载失败，使用合成音效
        if (!externalLoaded) {
            System.out.println("使用合成音效...");
            generateSyntheticSounds();
        }
    }

    /**
     * 从外部文件加载音效
     */
    private boolean loadExternalSounds() {
        boolean anyLoaded = false;
        
        // 定义音效文件映射
        Map<String, String> soundFiles = new HashMap<>();
        soundFiles.put("hit", "resources/audio/hit.wav");
        soundFiles.put("serve", "resources/audio/serve.wav");
        soundFiles.put("score", "resources/audio/score.wav");
        soundFiles.put("smash", "resources/audio/smash.wav");
        soundFiles.put("dash", "resources/audio/dash.wav");
        soundFiles.put("win", "resources/audio/win.wav");
        soundFiles.put("click", "resources/audio/click.wav");
        soundFiles.put("bgm", "resources/audio/bgm.wav");
        
        for (Map.Entry<String, String> entry : soundFiles.entrySet()) {
            if (loadSoundFromFile(entry.getKey(), entry.getValue())) {
                anyLoaded = true;
            }
        }
        
        return anyLoaded;
    }

    /**
     * 从文件加载单个音效
     */
    private boolean loadSoundFromFile(String name, String path) {
        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                byte[] data = Files.readAllBytes(filePath);
                soundCache.put(name, data);
                System.out.println("已加载音效: " + path);
                return true;
            } else {
                System.out.println("音效文件不存在: " + path);
            }
        } catch (Exception e) {
            System.err.println("加载音效失败 " + path + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * 生成合成音效（当找不到音频文件时使用）
     */
    private void generateSyntheticSounds() {
        // 击球音效 - 短促的"啪"声
        soundCache.put("hit", generateHitSound());
        // 发球音效
        soundCache.put("serve", generateServeSound());
        // 得分音效
        soundCache.put("score", generateScoreSound());
        // 扣杀音效
        soundCache.put("smash", generateSmashSound());
        // 闪避音效
        soundCache.put("dash", generateDashSound());
        // 胜利音效
        soundCache.put("win", generateWinSound());
        // 按钮点击音效
        soundCache.put("click", generateClickSound());
        // 背景音乐
        soundCache.put("bgm", generateBgmSound());
    }

    /**
     * 生成击球音效
     */
    private byte[] generateHitSound() {
        return generateToneSound(800, 0.08f, 0.5f);
    }

    /**
     * 生成发球音效
     */
    private byte[] generateServeSound() {
        return generateToneSound(600, 0.15f, 0.6f);
    }

    /**
     * 生成得分音效
     */
    private byte[] generateScoreSound() {
        return generateToneSound(1000, 0.3f, 0.7f);
    }

    /**
     * 生成扣杀音效
     */
    private byte[] generateSmashSound() {
        return generateToneSound(400, 0.2f, 0.8f);
    }

    /**
     * 生成闪避音效
     */
    private byte[] generateDashSound() {
        return generateSweepSound();
    }

    /**
     * 生成胜利音效
     */
    private byte[] generateWinSound() {
        return generateVictorySound();
    }

    /**
     * 生成点击音效
     */
    private byte[] generateClickSound() {
        return generateToneSound(1200, 0.05f, 0.3f);
    }

    /**
     * 生成背景音乐
     */
    private byte[] generateBgmSound() {
        return generateSimpleBgm();
    }

    /**
     * 生成单音调声音
     */
    private byte[] generateToneSound(float frequency, float duration, float amplitude) {
        int sampleRate = 44100;
        int numSamples = (int)(sampleRate * duration);
        byte[] audioData = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            double angle = 2.0 * Math.PI * frequency * i / sampleRate;
            // 添加衰减包络
            double envelope = Math.sin(Math.PI * i / numSamples);
            short value = (short)(32767 * amplitude * Math.sin(angle) * envelope);
            audioData[i * 2] = (byte)(value & 0xFF);
            audioData[i * 2 + 1] = (byte)((value >> 8) & 0xFF);
        }

        return audioData;
    }

    /**
     * 生成扫频音效（用于闪避）
     */
    private byte[] generateSweepSound() {
        int sampleRate = 44100;
        float duration = 0.2f;
        int numSamples = (int)(sampleRate * duration);
        byte[] audioData = new byte[numSamples * 2];

        for (int i = 0; i < numSamples; i++) {
            float t = (float)i / numSamples;
            float frequency = 400 + t * 800; // 频率从400Hz扫到1200Hz
            double angle = 2.0 * Math.PI * frequency * i / sampleRate;
            double envelope = Math.sin(Math.PI * t);
            short value = (short)(32767 * 0.5 * Math.sin(angle) * envelope);
            audioData[i * 2] = (byte)(value & 0xFF);
            audioData[i * 2 + 1] = (byte)((value >> 8) & 0xFF);
        }

        return audioData;
    }

    /**
     * 生成胜利音乐
     */
    private byte[] generateVictorySound() {
        int sampleRate = 44100;
        float duration = 1.5f;
        int numSamples = (int)(sampleRate * duration);
        byte[] audioData = new byte[numSamples * 2];

        int[] notes = {523, 659, 784, 1047}; // C, E, G, C 和弦

        for (int i = 0; i < numSamples; i++) {
            float t = (float)i / numSamples;
            int noteIndex = (int)(t * 2.5f) % notes.length;
            float frequency = notes[noteIndex];
            double angle = 2.0 * Math.PI * frequency * i / sampleRate;
            double envelope = Math.exp(-t * 3);
            short value = (short)(32767 * 0.4 * Math.sin(angle) * envelope);
            audioData[i * 2] = (byte)(value & 0xFF);
            audioData[i * 2 + 1] = (byte)((value >> 8) & 0xFF);
        }

        return audioData;
    }

    /**
     * 生成简单的背景音乐（循环播放）
     */
    private byte[] generateSimpleBgm() {
        int sampleRate = 22050;
        float duration = 8.0f; // 8秒循环
        int numSamples = (int)(sampleRate * duration);
        byte[] audioData = new byte[numSamples * 2];

        // 简单的旋律
        int[] melody = {523, 587, 659, 698, 784, 880, 988, 1047}; // C大调音阶
        float noteLength = duration / melody.length;

        for (int i = 0; i < numSamples; i++) {
            float t = (float)i / sampleRate;
            int noteIndex = (int)(t / noteLength) % melody.length;
            float frequency = melody[noteIndex];

            // 主旋律
            double angle1 = 2.0 * Math.PI * frequency * i / sampleRate;
            // 低音伴奏
            double angle2 = 2.0 * Math.PI * (frequency / 2) * i / sampleRate;

            double value = 0.15 * Math.sin(angle1) + 0.1 * Math.sin(angle2);
            // 添加淡入淡出
            double envelope = Math.sin(Math.PI * t / noteLength) * 0.8;

            short shortValue = (short)(32767 * value * envelope);
            audioData[i * 2] = (byte)(shortValue & 0xFF);
            audioData[i * 2 + 1] = (byte)((shortValue >> 8) & 0xFF);
        }

        return audioData;
    }

    /**
     * 播放音频数据
     */
    private Clip playAudioData(byte[] audioData, float volume, boolean loop) {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream ais = new AudioInputStream(bais, format, audioData.length / 2);

            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            // 设置音量
            setClipVolume(clip, volume);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            clip.start();

            return clip;
        } catch (Exception e) {
            System.err.println("播放音频失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 设置Clip音量
     */
    private void setClipVolume(Clip clip, float volume) {
        if (clip == null) return;
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            float gain = min + (max - min) * volume;
            gainControl.setValue(gain);
        } catch (Exception e) {
            // 某些系统不支持音量控制
        }
    }

    // ==================== 公开方法 ====================

    /**
     * 播放背景音乐
     */
    public void playBGM() {
        if (!bgmEnabled) return;

        stopBGM();

        audioExecutor.submit(() -> {
            byte[] bgmData = soundCache.get("bgm");
            if (bgmData != null) {
                bgmClip = playAudioData(bgmData, bgmVolume, true);
                currentBgm = "bgm";
            }
        });
    }

    /**
     * 停止背景音乐
     */
    public void stopBGM() {
        if (bgmClip != null) {
            bgmClip.stop();
            bgmClip.close();
            bgmClip = null;
        }
        currentBgm = null;
    }

    /**
     * 暂停背景音乐
     */
    public void pauseBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    /**
     * 恢复背景音乐
     */
    public void resumeBGM() {
        if (bgmClip != null && !bgmClip.isRunning() && bgmEnabled) {
            bgmClip.start();
        }
    }

    /**
     * 设置背景音乐音量
     */
    public void setBgmVolume(float volume) {
        this.bgmVolume = Math.max(0, Math.min(1, volume));
        if (bgmClip != null) {
            setClipVolume(bgmClip, bgmVolume);
        }
    }

    /**
     * 启用/禁用背景音乐
     */
    public void setBgmEnabled(boolean enabled) {
        this.bgmEnabled = enabled;
        if (enabled) {
            playBGM();
        } else {
            stopBGM();
        }
    }

    /**
     * 设置音效开关
     */
    public void setSfxEnabled(boolean enabled) {
        this.sfxEnabled = enabled;
    }

    /**
     * 设置音效音量
     */
    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0, Math.min(1, volume));
    }

    /**
     * 播放音效
     */
    public void playSound(String soundName) {
        if (!sfxEnabled) return;

        audioExecutor.submit(() -> {
            byte[] soundData = soundCache.get(soundName);
            if (soundData != null) {
                Clip clip = playAudioData(soundData, sfxVolume, false);
                if (clip != null) {
                    soundClips.put(soundName + System.currentTimeMillis(), clip);

                    // 播放完成后清理
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                }
            }
        });
    }

    /**
     * 播放击球音效
     */
    public void playHitSound() {
        playSound("hit");
    }

    /**
     * 播放发球音效
     */
    public void playServeSound() {
        playSound("serve");
    }

    /**
     * 播放得分音效
     */
    public void playScoreSound() {
        playSound("score");
    }

    /**
     * 播放扣杀音效
     */
    public void playSmashSound() {
        playSound("smash");
    }

    /**
     * 播放闪避音效
     */
    public void playDashSound() {
        playSound("dash");
    }

    /**
     * 播放胜利音效
     */
    public void playWinSound() {
        playSound("win");
    }

    /**
     * 播放按钮点击音效
     */
    public void playClickSound() {
        playSound("click");
    }

    /**
     * 清理资源
     */
    public void shutdown() {
        stopBGM();
        soundClips.values().forEach(clip -> {
            if (clip != null) {
                clip.stop();
                clip.close();
            }
        });
        soundClips.clear();
        audioExecutor.shutdown();
    }

    // Getters
    public boolean isBgmEnabled() { return bgmEnabled; }
    public boolean isSfxEnabled() { return sfxEnabled; }
    public float getBgmVolume() { return bgmVolume; }
    public float getSfxVolume() { return sfxVolume; }
}