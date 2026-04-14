package yumaoqou.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioManager {

    private static AudioManager instance;
    private Map<String, Clip> soundEffects;
    private Clip backgroundMusic;
    private float masterVolume = 0.7f;
    private float musicVolume = 0.5f;
    private float effectVolume = 0.7f;
    private boolean musicEnabled = true;
    private boolean effectsEnabled = true;
    private ExecutorService audioExecutor;
    private Clip currentChargeClip;

    private AudioManager() {
        soundEffects = new HashMap<>();
        audioExecutor = Executors.newSingleThreadExecutor();
        loadSounds();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private void loadSounds() {
        registerSynthesizedSound("hit1", 880, 200);
        registerSynthesizedSound("hit2", 1046, 200);
        registerSynthesizedSound("serve", 660, 300);
        registerSynthesizedSound("score", 523, 500);
        registerSynthesizedSound("swing", 440, 100);
        registerSynthesizedSound("net", 330, 150);
        registerSynthesizedSound("gameover", 262, 800);
        registerSynthesizedSound("charge", 523, 50);
    }

    private void registerSynthesizedSound(String name, int frequency, int durationMs) {
        try {
            byte[] audioData = generateSineWave(frequency, durationMs);
            AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream ais = new AudioInputStream(bais, format, audioData.length);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            soundEffects.put(name, clip);
        } catch (Exception e) {
            System.err.println("无法创建音效: " + name);
        }
    }

    private byte[] generateSineWave(int frequency, int durationMs) {
        int sampleRate = 44100;
        int numSamples = sampleRate * durationMs / 1000;
        byte[] buffer = new byte[numSamples];
        double delta = 2 * Math.PI * frequency / sampleRate;
        double angle = 0;

        for (int i = 0; i < numSamples; i++) {
            double sample = Math.sin(angle) * 0.5;
            buffer[i] = (byte) (sample * 127);
            angle += delta;
            if (i > numSamples - sampleRate / 10) {
                double fadeOut = 1.0 - (i - (numSamples - sampleRate / 10)) / (double)(sampleRate / 10);
                buffer[i] = (byte) (buffer[i] * fadeOut);
            }
        }
        return buffer;
    }

    public void playEffect(String name) {
        if (!effectsEnabled) return;
        audioExecutor.submit(() -> {
            try {
                Clip clip = soundEffects.get(name);
                if (clip != null) {
                    clip.setFramePosition(0);
                    setClipVolume(clip, masterVolume * effectVolume);
                    clip.start();
                }
            } catch (Exception e) {
                // 忽略音频错误
            }
        });
    }

    public void playHitSound(boolean isPlayer1, float power) {
        if (!effectsEnabled) return;
        final int frequency = isPlayer1 ? 800 : 900;
        final int finalFrequency = frequency + (int)(power * 100);
        final int duration = 150 + (int)(power * 100);

        audioExecutor.submit(() -> {
            try {
                byte[] audioData = generateSineWave(finalFrequency, duration);
                AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
                ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
                AudioInputStream ais = new AudioInputStream(bais, format, audioData.length);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                setClipVolume(clip, masterVolume * effectVolume);
                clip.start();
                new Thread(() -> {
                    try {
                        Thread.sleep(duration + 100);
                        clip.close();
                    } catch (Exception e) {}
                }).start();
            } catch (Exception e) {
                // 忽略
            }
        });
    }

    public void playScoreSound(int player) {
        if (!effectsEnabled) return;
        audioExecutor.submit(() -> {
            try {
                byte[] audioData = generateVictoryFanfare();
                AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
                ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
                AudioInputStream ais = new AudioInputStream(bais, format, audioData.length);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                setClipVolume(clip, masterVolume * effectVolume);
                clip.start();
            } catch (Exception e) {
                // 忽略
            }
        });
    }

    private byte[] generateVictoryFanfare() {
        int sampleRate = 44100;
        int duration = 800;
        int numSamples = sampleRate * duration / 1000;
        byte[] buffer = new byte[numSamples];
        int[] frequencies = {523, 587, 659, 523, 587, 659, 784};
        int[] durations = {100, 100, 100, 100, 100, 100, 200};
        int samplePos = 0;

        for (int i = 0; i < frequencies.length; i++) {
            int freq = frequencies[i];
            int dur = durations[i];
            int samples = sampleRate * dur / 1000;
            double delta = 2 * Math.PI * freq / sampleRate;
            double angle = 0;
            for (int j = 0; j < samples && samplePos < numSamples; j++) {
                double sample = Math.sin(angle) * 0.5;
                buffer[samplePos++] = (byte) (sample * 127);
                angle += delta;
            }
        }
        return buffer;
    }

    public void startBackgroundMusic() {
        if (!musicEnabled) return;
        audioExecutor.submit(() -> {
            try {
                if (backgroundMusic != null && backgroundMusic.isRunning()) return;
                byte[] musicData = generateBackgroundMusic();
                AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
                ByteArrayInputStream bais = new ByteArrayInputStream(musicData);
                AudioInputStream ais = new AudioInputStream(bais, format, musicData.length);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(ais);
                setClipVolume(backgroundMusic, masterVolume * musicVolume);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundMusic.start();
            } catch (Exception e) {
                // 忽略
            }
        });
    }

    private byte[] generateBackgroundMusic() {
        int sampleRate = 44100;
        int loopDuration = 4000;
        int numSamples = sampleRate * loopDuration / 1000;
        byte[] buffer = new byte[numSamples];
        int[] notes = {262, 294, 330, 262, 330, 349, 392, 330, 294, 262};
        int[] noteDurations = {400, 400, 400, 400, 400, 400, 400, 400, 400, 400};
        int samplePos = 0;

        for (int i = 0; i < notes.length && samplePos < numSamples; i++) {
            int freq = notes[i];
            int duration = noteDurations[i];
            int samples = sampleRate * duration / 1000;
            double delta = 2 * Math.PI * freq / sampleRate;
            double angle = 0;
            for (int j = 0; j < samples && samplePos < numSamples; j++) {
                double sample = Math.sin(angle) * 0.3;
                sample += Math.sin(angle * 2.0) * 0.15;
                sample += Math.sin(angle * 4.0) * 0.05;
                buffer[samplePos++] = (byte) (sample * 100);
                angle += delta;
            }
        }
        return buffer;
    }

    public void stopBackgroundMusic() {
        audioExecutor.submit(() -> {
            try {
                if (backgroundMusic != null) {
                    backgroundMusic.stop();
                    backgroundMusic.close();
                    backgroundMusic = null;
                }
            } catch (Exception e) {
                // 忽略
            }
        });
    }

    private void setClipVolume(Clip clip, float volume) {
        if (clip == null) return;
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float safeVolume = Math.max(0.01f, volume);
            float dB = (float) (Math.log(safeVolume) / Math.log(10.0) * 20.0);
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            gainControl.setValue(Math.max(min, Math.min(max, dB)));
        } catch (Exception e) {
            // 不支持音量控制，忽略
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0, Math.min(1, volume));
        if (backgroundMusic != null) {
            setClipVolume(backgroundMusic, masterVolume * musicVolume);
        }
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0, Math.min(1, volume));
        if (backgroundMusic != null) {
            setClipVolume(backgroundMusic, masterVolume * musicVolume);
        }
    }

    public void setEffectVolume(float volume) {
        this.effectVolume = Math.max(0, Math.min(1, volume));
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (enabled) {
            startBackgroundMusic();
        } else {
            stopBackgroundMusic();
        }
    }

    public void setEffectsEnabled(boolean enabled) {
        this.effectsEnabled = enabled;
    }

    public boolean isMusicEnabled() { return musicEnabled; }
    public boolean isEffectsEnabled() { return effectsEnabled; }
    public float getMasterVolume() { return masterVolume; }
    public float getMusicVolume() { return musicVolume; }
    public float getEffectVolume() { return effectVolume; }

    public void playChargeSound() {
        if (!effectsEnabled) return;
        audioExecutor.submit(() -> {
            try {
                if (currentChargeClip != null && currentChargeClip.isRunning()) return;
                byte[] audioData = generateSineWave(523, 1000);
                AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
                ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
                AudioInputStream ais = new AudioInputStream(bais, format, audioData.length);
                currentChargeClip = AudioSystem.getClip();
                currentChargeClip.open(ais);
                setClipVolume(currentChargeClip, masterVolume * effectVolume * 0.5f);
                currentChargeClip.loop(Clip.LOOP_CONTINUOUSLY);
                currentChargeClip.start();
            } catch (Exception e) {
                // 忽略
            }
        });
    }

    public void stopChargeSound() {
        audioExecutor.submit(() -> {
            try {
                if (currentChargeClip != null) {
                    currentChargeClip.stop();
                    currentChargeClip.close();
                    currentChargeClip = null;
                }
            } catch (Exception e) {
                // 忽略
            }
        });
    }

    public void playSwingSound() {
        playEffect("swing");
    }

    public void playNetSound() {
        playEffect("net");
    }

    public void playGameOverSound() {
        playEffect("gameover");
    }

    public void shutdown() {
        stopBackgroundMusic();
        stopChargeSound();
        for (Clip clip : soundEffects.values()) {
            if (clip != null) {
                clip.stop();
                clip.close();
            }
        }
        audioExecutor.shutdown();
    }
}