package yumaoqou.panel;

import yumaoqou.core.GameCore;
import yumaoqou.window.GameWindow;
import yumaoqou.util.FontManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

public class MenuPanel extends JPanel {
    private GameWindow gameWindow;
    private float time = 0;
    private Timer animationTimer;
    private FontManager fontManager;

    public MenuPanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.fontManager = FontManager.getInstance();
        setPreferredSize(new Dimension(GameCore.WIDTH, GameCore.HEIGHT));
        setLayout(new GridBagLayout());

        createMenuComponents();
        startAnimation();
    }

    private void createMenuComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 标题 - 使用中文字体
        JLabel titleLabel = new JLabel("羽毛球大师赛");
        titleLabel.setFont(fontManager.getBoldFont(72));
        titleLabel.setForeground(new Color(255, 215, 0));

        JLabel subtitleLabel = new JLabel("高画质增强版");
        subtitleLabel.setFont(fontManager.getPlainFont(36));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        JButton startButton = createMenuButton("开始游戏", new Color(70, 130, 200));
        JButton settingsButton = createMenuButton("游戏设置", new Color(100, 180, 100));
        JButton helpButton = createMenuButton("游戏帮助", new Color(200, 130, 70));
        JButton exitButton = createMenuButton("退出游戏", new Color(200, 70, 70));

        startButton.addActionListener(e -> gameWindow.showModeSelect());
        settingsButton.addActionListener(e -> gameWindow.showSettings());
        helpButton.addActionListener(e -> gameWindow.showHelp());
        exitButton.addActionListener(e -> gameWindow.exitGame());

        add(titleLabel, gbc);
        add(subtitleLabel, gbc);
        add(Box.createVerticalStrut(50), gbc);
        add(startButton, gbc);
        add(settingsButton, gbc);
        add(helpButton, gbc);
        add(exitButton, gbc);
    }

    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // 按钮阴影
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRoundRect(3, 3, getWidth(), getHeight(), 25, 25);

                // 按钮渐变
                GradientPaint gp = new GradientPaint(
                        0, 0, color,
                        getWidth(), getHeight(), color.brighter()
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // 按钮边框
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 25, 25);

                // 文字 - 使用中文字体
                g2d.setFont(fontManager.getBoldFont(getFont().getSize()));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.setColor(Color.WHITE);
                g2d.drawString(text, x, y);

                g2d.dispose();
            }
        };

        button.setFont(fontManager.getBoldFont(28));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(280, 65));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setFont(fontManager.getBoldFont(32));
                button.repaint();
            }
            public void mouseExited(MouseEvent evt) {
                button.setFont(fontManager.getBoldFont(28));
                button.repaint();
            }
        });

        return button;
    }

    private void startAnimation() {
        animationTimer = new Timer(50, e -> {
            time += 0.05f;
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 动态渐变背景
        int r = (int)(20 + 10 * Math.sin(time));
        int g2 = (int)(30 + 15 * Math.sin(time + 2));
        int b = (int)(50 + 20 * Math.sin(time + 4));

        Color color1 = new Color(r, g2, b);
        Color color2 = new Color(
                (int)(40 + 20 * Math.sin(time + 1)),
                (int)(20 + 10 * Math.sin(time + 3)),
                (int)(70 + 25 * Math.sin(time + 5))
        );

        GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 动态光晕
        g2d.setColor(new Color(255, 255, 255, 20));
        for (int i = 0; i < 8; i++) {
            int x = (int)(Math.sin(time + i) * 250 + getWidth()/2);
            int y = (int)(Math.cos(time + i) * 150 + getHeight()/2);
            g2d.fillOval(x - 50, y - 50, 100, 100);
        }

        // 羽毛球场背景线条
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(100, getHeight()/2 - 100, getWidth() - 200, 200);
        g2d.drawLine(getWidth()/2, getHeight()/2 - 100, getWidth()/2, getHeight()/2 + 100);

        // 底部提示文字
        g2d.setFont(fontManager.getPlainFont(14));
        g2d.setColor(new Color(255, 255, 255, 100));
        String tipText = "按 ESC 返回菜单 | B 显示碰撞箱";
        int tipWidth = g2d.getFontMetrics().stringWidth(tipText);
        g2d.drawString(tipText, getWidth()/2 - tipWidth/2, getHeight() - 30);
    }
}