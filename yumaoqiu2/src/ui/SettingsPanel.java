package ui;

import util.Constants;
import audio.AudioManager;
import java.awt.*;
import java.awt.event.KeyEvent;

public class SettingsPanel {
    private boolean isMuted = false;
    private float volume = 0.7f;
    private int selectedOption = 0;
    private String[] options = {"音量", "静音", "返回"};

    public void draw(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        g.setFont(Constants.FONT_TITLE.deriveFont(48f));
        g.setColor(Color.YELLOW);
        String title = "游戏设置";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (Constants.WINDOW_WIDTH - fm.stringWidth(title)) / 2, 150);

        g.setFont(Constants.FONT_MENU);

        int y = 250;
        g.setColor(Color.WHITE);
        g.drawString("音量: " + (int)(volume * 100) + "%", 400, y);

        g.setColor(Color.GRAY);
        g.fillRect(400, y + 10, 300, 20);
        g.setColor(Color.GREEN);
        g.fillRect(400, y + 10, (int)(300 * volume), 20);

        y = 330;
        g.setColor(selectedOption == 1 ? Color.YELLOW : Color.WHITE);
        g.drawString("静音: " + (isMuted ? "✓ 是" : "✗ 否"), 400, y);

        y = 410;
        g.setColor(selectedOption == 2 ? Color.YELLOW : Color.WHITE);
        g.drawString("返回主菜单", 400, y);

        g.setFont(Constants.FONT_SMALL);
        g.setColor(Color.GRAY);
        String hint = "使用 ↑/↓ 选择选项，←/→ 调整音量，ENTER 确认";
        fm = g.getFontMetrics();
        g.drawString(hint, (Constants.WINDOW_WIDTH - fm.stringWidth(hint)) / 2,
                Constants.WINDOW_HEIGHT - 80);
    }

    public void handleKeyPress(int key) {
        switch (key) {
            case KeyEvent.VK_UP:
                selectedOption = Math.max(0, selectedOption - 1);
                break;
            case KeyEvent.VK_DOWN:
                selectedOption = Math.min(2, selectedOption + 1);
                break;
            case KeyEvent.VK_LEFT:
                if (selectedOption == 0) {
                    volume = Math.max(0, volume - 0.05f);
                    AudioManager.getInstance().setVolume(volume);
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (selectedOption == 0) {
                    volume = Math.min(1, volume + 0.05f);
                    AudioManager.getInstance().setVolume(volume);
                }
                break;
            case KeyEvent.VK_ENTER:
                if (selectedOption == 1) {
                    isMuted = !isMuted;
                    AudioManager.getInstance().setMuted(isMuted);
                }
                break;
            default:
                break;
        }
    }

    public boolean isReturnSelected() {
        return selectedOption == 2;
    }
}