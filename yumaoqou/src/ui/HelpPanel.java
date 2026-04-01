package yumaoqou.ui;

import yumaoqou.utils.GameConstants;
import javax.swing.*;
import java.awt.*;

public class HelpPanel extends JPanel {
    public HelpPanel(GameWindow gameWindow) {
        setPreferredSize(new Dimension(GameConstants.WIDTH, GameConstants.HEIGHT));
        setLayout(new BorderLayout());
        setBackground(new Color(20, 30, 40));

        JLabel title = new JLabel("游戏帮助", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 48));
        title.setForeground(new Color(255, 215, 0));
        add(title, BorderLayout.NORTH);

        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setOpaque(false);
        text.setForeground(Color.WHITE);
        text.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        text.setText(
                "游戏规则：\n\n" +
                        "1. 球必须碰到球拍才能击飞\n" +
                        "2. 球必须过网且落地才能得分\n" +
                        "3. 赢家发球规则\n" +
                        "4. 先得7分者获胜\n\n" +
                        "操作说明：\n\n" +
                        "玩家1（蓝方）：\n" +
                        "  A - 向左移动\n" +
                        "  D - 向右移动\n" +
                        "  W - 跳跃\n" +
                        "  J - 按住蓄力，松开击球\n\n" +
                        "玩家2（红方）：\n" +
                        "  ← - 向左移动\n" +
                        "  → - 向右移动\n" +
                        "  ↑ - 跳跃\n" +
                        "  1 - 按住蓄力，松开击球\n\n" +
                        "R - 重新开始游戏\n" +
                        "ESC - 返回菜单\n\n" +
                        "游戏机制：\n\n" +
                        "• 发球后，对方可以接球\n" +
                        "• 每人每回合只能击球一次\n" +
                        "• 屏幕上显示绿色\"可以击球\"提示\n" +
                        "• 蓄力时间越长，球速越快、越高\n" +
                        "• 球碰到网会反弹"
        );
        text.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

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