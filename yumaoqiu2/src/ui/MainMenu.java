package ui;

import util.Constants;
import game.GameMode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenu {
    private int selectedOption = 0;
    private String[] mainOptions = {"开始游戏", "游戏模式", "设置", "退出"};
    private GameMode selectedMode = GameMode.VERSUS;
    private boolean showModeSelect = false;

    private Rectangle[] optionBounds = new Rectangle[4];
    private Rectangle[] modeBounds = new Rectangle[GameMode.values().length];

    public MainMenu() {
        for (int i = 0; i < optionBounds.length; i++) {
            optionBounds[i] = new Rectangle();
        }
        for (int i = 0; i < modeBounds.length; i++) {
            modeBounds[i] = new Rectangle();
        }
    }

    public void updateMousePosition(int mouseX, int mouseY) {
        for (int i = 0; i < optionBounds.length; i++) {
            if (optionBounds[i].contains(mouseX, mouseY)) {
                selectedOption = i;
            }
        }

        if (showModeSelect) {
            for (int i = 0; i < modeBounds.length; i++) {
                if (modeBounds[i].contains(mouseX, mouseY)) {
                    selectedMode = GameMode.values()[i];
                }
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY) {
        if (showModeSelect) {
            for (int i = 0; i < modeBounds.length; i++) {
                if (modeBounds[i].contains(mouseX, mouseY)) {
                    selectedMode = GameMode.values()[i];
                    showModeSelect = false;
                    return;
                }
            }
            showModeSelect = false;
        } else {
            for (int i = 0; i < optionBounds.length; i++) {
                if (optionBounds[i].contains(mouseX, mouseY)) {
                    selectedOption = i;
                    return;
                }
            }
        }
    }

    public void draw(Graphics2D g) {
        GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 0, 0, 200),
                0, Constants.WINDOW_HEIGHT, new Color(50, 50, 50, 220));
        g.setPaint(gradient);
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        drawDecorationCourt(g);

        g.setFont(Constants.FONT_TITLE);
        g.setColor(Color.YELLOW);
        String title = "🏸 羽毛球大战";
        FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g.drawString(title, (Constants.WINDOW_WIDTH - titleWidth) / 2, 120);

        g.setFont(Constants.FONT_MENU);
        g.setColor(new Color(200, 200, 200));
        String subtitle = "Badminton Battle";
        fm = g.getFontMetrics();
        int subWidth = fm.stringWidth(subtitle);
        g.drawString(subtitle, (Constants.WINDOW_WIDTH - subWidth) / 2, 170);

        if (showModeSelect) {
            drawModeSelect(g);
        } else {
            drawMainOptions(g);
        }

        drawControls(g);
    }

    private void drawDecorationCourt(Graphics2D g) {
        g.setStroke(new BasicStroke(2));
        g.setColor(new Color(255, 255, 255, 50));

        int courtWidth = 500;
        int courtHeight = 300;
        int courtX = (Constants.WINDOW_WIDTH - courtWidth) / 2;
        int courtY = Constants.WINDOW_HEIGHT - 180;

        g.drawRect(courtX, courtY, courtWidth, courtHeight);
        g.drawLine(courtX + courtWidth / 2, courtY, courtX + courtWidth / 2, courtY + courtHeight);

        for (int i = 0; i < 10; i++) {
            g.drawLine(courtX + courtWidth / 2, courtY + i * 30,
                    courtX + courtWidth / 2 + 20, courtY + i * 30);
        }
    }

    private void drawMainOptions(Graphics2D g) {
        g.setFont(Constants.FONT_MENU);
        FontMetrics fm = g.getFontMetrics();

        for (int i = 0; i < mainOptions.length; i++) {
            int y = 280 + i * 60;

            if (i == selectedOption) {
                g.setColor(new Color(255, 215, 0));
                g.setStroke(new BasicStroke(3));
                String arrow = "▶ ";
                int arrowWidth = fm.stringWidth(arrow);
                g.drawString(arrow, Constants.WINDOW_WIDTH / 2 - 100 - arrowWidth, y + 5);

                int textWidth = fm.stringWidth(mainOptions[i]);
                optionBounds[i].setBounds(Constants.WINDOW_WIDTH / 2 - 80, y - 20, textWidth + 160, 40);
                g.setColor(new Color(255, 215, 0, 50));
                g.fillRect(optionBounds[i].x, optionBounds[i].y, optionBounds[i].width, optionBounds[i].height);
                g.setColor(new Color(255, 215, 0));
                g.drawRect(optionBounds[i].x, optionBounds[i].y, optionBounds[i].width, optionBounds[i].height);
            } else {
                optionBounds[i].setBounds(Constants.WINDOW_WIDTH / 2 - 80, y - 20,
                        fm.stringWidth(mainOptions[i]) + 160, 40);
            }

            g.setColor(i == selectedOption ? Color.YELLOW : Color.WHITE);
            int textWidth = fm.stringWidth(mainOptions[i]);
            g.drawString(mainOptions[i], (Constants.WINDOW_WIDTH - textWidth) / 2, y);
        }
    }

    private void drawModeSelect(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        g.setFont(Constants.FONT_TITLE.deriveFont(36f));
        g.setColor(Color.YELLOW);
        String title = "选择游戏模式";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (Constants.WINDOW_WIDTH - fm.stringWidth(title)) / 2, 200);

        GameMode[] modes = GameMode.values();
        g.setFont(Constants.FONT_MENU);

        for (int i = 0; i < modes.length; i++) {
            int y = 280 + i * 80;
            int x = Constants.WINDOW_WIDTH / 2 - 150;

            modeBounds[i].setBounds(x, y - 25, 300, 60);

            if (modes[i] == selectedMode) {
                g.setColor(new Color(255, 215, 0, 80));
                g.fillRoundRect(modeBounds[i].x, modeBounds[i].y, modeBounds[i].width, modeBounds[i].height, 10, 10);
                g.setColor(Color.YELLOW);
                g.drawRoundRect(modeBounds[i].x, modeBounds[i].y, modeBounds[i].width, modeBounds[i].height, 10, 10);
            }

            g.setColor(modes[i] == selectedMode ? Color.YELLOW : Color.WHITE);
            g.drawString(modes[i].getName(), x + 10, y);

            g.setFont(Constants.FONT_SMALL);
            g.setColor(new Color(180, 180, 180));
            g.drawString(modes[i].getDescription(), x + 10, y + 25);
            g.setFont(Constants.FONT_MENU);
        }

        g.setFont(Constants.FONT_SMALL);
        g.setColor(Color.GRAY);
        String hint = "点击选择模式，点击空白区域返回";
        fm = g.getFontMetrics();
        g.drawString(hint, (Constants.WINDOW_WIDTH - fm.stringWidth(hint)) / 2,
                Constants.WINDOW_HEIGHT - 50);
    }

    private void drawControls(Graphics2D g) {
        g.setFont(Constants.FONT_SMALL);
        g.setColor(new Color(150, 150, 150));

        String[] controls = {
                "← 左玩家: A/D 移动 | W 跳跃 | F 击球 →",
                "← 右玩家: ←/→ 移动 | ↑ 跳跃 | ↓ 击球 →"
        };

        FontMetrics fm = g.getFontMetrics();
        for (int i = 0; i < controls.length; i++) {
            int textWidth = fm.stringWidth(controls[i]);
            g.drawString(controls[i], (Constants.WINDOW_WIDTH - textWidth) / 2,
                    Constants.WINDOW_HEIGHT - 80 + i * 25);
        }
    }

    public int getSelectedOption() { return selectedOption; }
    public GameMode getSelectedMode() { return selectedMode; }
    public boolean isShowModeSelect() { return showModeSelect; }
    public void setShowModeSelect(boolean show) { showModeSelect = show; }
}