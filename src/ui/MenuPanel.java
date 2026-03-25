package yumaoqou.ui;

import yumaoqou.utils.GameConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuPanel extends JPanel {
    private GameWindow gameWindow;
    private float time = 0;
    private Timer animationTimer;

    public MenuPanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setPreferredSize(new Dimension(GameConstants.WIDTH, GameConstants.HEIGHT));
        setLayout(new GridBagLayout());

        createMenuComponents();
        startAnimation();
    }

    private void createMenuComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("羽毛球大师赛");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 72));
        titleLabel.setForeground(new Color(255, 215, 0));

        JLabel subtitleLabel = new JLabel("多线程并发版");
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 36));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        JLabel featureLabel = new JLabel("✓ 单击蓄力击球  ✓ 球拍碰撞检测  ✓ 多线程并发");
        featureLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        featureLabel.setForeground(new Color(255, 255, 255, 180));

        JButton startButton = createMenuButton("开始游戏", new Color(70, 130, 200));
        JButton settingsButton = createMenuButton("游戏设置", new Color(100, 180, 100));
        JButton helpButton = createMenuButton("游戏帮助", new Color(200, 130, 70));
        JButton exitButton = createMenuButton("退出游戏", new Color(200, 70, 70));

        startButton.addActionListener(e -> gameWindow.startGame());
        settingsButton.addActionListener(e -> gameWindow.showSettings());
        helpButton.addActionListener(e -> gameWindow.showHelp());
        exitButton.addActionListener(e -> gameWindow.exitGame());

        add(titleLabel, gbc);
        add(subtitleLabel, gbc);
        add(Box.createVerticalStrut(20), gbc);
        add(featureLabel, gbc);
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

                GradientPaint gp = new GradientPaint(
                        0, 0, color,
                        getWidth(), getHeight(), color.brighter()
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);

                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.setColor(Color.WHITE);
                g2d.drawString(text, x, y);

                g2d.dispose();
            }
        };

        button.setFont(new Font("微软雅黑", Font.BOLD, 28));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(280, 60));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setFont(new Font("微软雅黑", Font.BOLD, 32));
            }
            public void mouseExited(MouseEvent evt) {
                button.setFont(new Font("微软雅黑", Font.BOLD, 28));
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

        g2d.setColor(new Color(255, 255, 255, 30));
        for (int i = 0; i < 5; i++) {
            int x = (int)(Math.sin(time + i) * 200 + getWidth()/2);
            int y = (int)(Math.cos(time + i) * 100 + getHeight()/2);
            g2d.fillOval(x - 25, y - 25, 50, 50);
        }

        g2d.setColor(new Color(255, 255, 255, 50));
        for (int i = 0; i < 3; i++) {
            int x = (int)(Math.sin(time * 2 + i) * 300 + getWidth()/2);
            int y = (int)(Math.cos(time * 1.5 + i) * 200 + getHeight()/2);
            g2d.drawOval(x - 30, y - 30, 60, 60);
        }
    }
}