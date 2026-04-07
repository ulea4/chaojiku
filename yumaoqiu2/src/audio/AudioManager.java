package audio;

import javax.sound.sampled.*;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static AudioManager instance;
    private Map<String, Clip> sounds;
    private boolean muted = false;
    private float volume = 0.7f;

    public enum SoundEffect {
        HIT("hit.wav"),
        SCORE("score.wav"),
        JUMP("jump.wav"),
        POWERUP("powerup.wav"),
        VICTORY("victory.wav"),
        BACKGROUND("background.wav");

        private String filename;

        SoundEffect(String filename) {
            this.filename = filename;
        }

        public String getFilename() { return filename; }
    }

    private AudioManager() {
        sounds = new HashMap<>();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playSound(SoundEffect sound) {
        if (muted) return;
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    public void playBackgroundMusic() {
        if (muted) return;
    }

    public void stopBackgroundMusic() {
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        if (muted) {
            stopBackgroundMusic();
        } else {
            playBackgroundMusic();
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(1, volume));
    }

    public boolean isMuted() { return muted; }
}