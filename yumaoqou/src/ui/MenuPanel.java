package yumaoqou.ui;

import yumaoqou.utils.GameConstants;
import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    private GameWindow gameWindow;

    public MenuPanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setPreferredSize(new Dimension(GameConstants.WIDTH, GameConstants.HEIGHT));
        setLayout(new GridBagLayout());

        JLabel title = new JLabel("羽毛球大师赛");
        title.setFont(new Font("微软雅黑", Font.BOLD, 72));
        title.setForeground(new Color(255, 215, 0));

        JButton startBtn = createButton("开始游戏", new Color(70, 130, 200));
        JButton settingsBtn = createButton("游戏设置", new Color(100, 180, 100));
        JButton helpBtn = createButton("游戏帮助", new Color(200, 130, 70));
        JButton exitBtn = createButton("退出游戏", new Color(200, 70, 70));

        startBtn.addActionListener(e -> gameWindow.startGame());
        settingsBtn.addActionListener(e -> gameWindow.showSettings());
        helpBtn.addActionListener(e -> gameWindow.showHelp());
        exitBtn.addActionListener(e -> gameWindow.exitGame());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(title, gbc);
        add(Box.createVerticalStrut(50), gbc);
        add(startBtn, gbc);
        add(settingsBtn, gbc);
        add(helpBtn, gbc);
        add(exitBtn, gbc);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 28));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(280, 60));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(20, 30, 50), getWidth(), getHeight(), new Color(40, 50, 70));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}