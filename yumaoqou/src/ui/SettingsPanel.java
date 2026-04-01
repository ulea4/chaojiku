package yumaoqou.ui;

import yumaoqou.utils.GameConstants;
import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public SettingsPanel(GameWindow gameWindow) {
        setPreferredSize(new Dimension(GameConstants.WIDTH, GameConstants.HEIGHT));
        setLayout(new BorderLayout());
        setBackground(new Color(20, 30, 40));

        JLabel title = new JLabel("游戏设置", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 48));
        title.setForeground(new Color(255, 215, 0));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        JLabel info = new JLabel("游戏版本: 完美版 v9.0");
        info.setFont(new Font("微软雅黑", Font.PLAIN, 24));
        info.setForeground(Color.WHITE);
        center.add(info);
        add(center, BorderLayout.CENTER);

        JButton back = new JButton("返回菜单");
        back.setFont(new Font("微软雅黑", Font.BOLD, 20));
        back.setBackground(new Color(200, 70, 70));
        back.setForeground(Color.WHITE);
        back.setBorderPainted(false);
        back.setPreferredSize(new Dimension(150, 50));
        back.addActionListener(e -> gameWindow.showMenu());

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
    }
}