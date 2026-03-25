package yumaoqou.ui;

import yumaoqou.utils.GameConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HelpPanel extends JPanel {
    private GameWindow gameWindow;

    public HelpPanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setPreferredSize(new Dimension(GameConstants.WIDTH, GameConstants.HEIGHT));
        setLayout(new BorderLayout());
        setBackground(new Color(20, 30, 40));

        JLabel titleLabel = new JLabel("游戏帮助", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 215, 0));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        tabbedPane.setBackground(new Color(40, 50, 60));
        tabbedPane.setForeground(Color.WHITE);

        JPanel rulesPanel = createInfoPanel(new String[]{
                "🏸 基本规则",
                "• 平视视角，球员站在地面",
                "• 球员不能越过黄色虚线（不能过网）",
                "• 球必须过网且落地才能得分",
                "• 赢家发球规则",
                "• 先得7分者获胜",
                "",
                "🎯 得分规则",
                "• 球在对方半场落地：得1分",
                "• 球出界：对方得1分",
                "• 球碰网后落地：对方得1分",
                "• 二次击球：对方得1分"
        });

        JPanel controlPanel = createInfoPanel(new String[]{
                "🎮 玩家1控制（蓝方）",
                "   A - 向左移动",
                "   D - 向右移动",
                "   W - 跳跃",
                "   J - 单击蓄力，再次单击击球",
                "",
                "🎮 玩家2控制（红方）",
                "   ← - 向左移动",
                "   → - 向右移动",
                "   ↑ - 跳跃",
                "   1 (数字键1) - 单击蓄力，再次单击击球",
                "",
                "⌨️ 通用控制",
                "   R - 重新开始游戏",
                "   ESC - 返回菜单",
                "",
                "⚡ 多线程并发",
                "• 移动与蓄力可同时进行",
                "• 独立线程处理输入",
                "• 流畅的游戏体验"
        });

        JPanel physicsPanel = createInfoPanel(new String[]{
                "⚡ 蓄力系统",
                "• 单击J/1键开始蓄力",
                "• 蓄力过程中可以移动",
                "• 再次单击释放击球",
                "• 最大蓄力时间1.5秒",
                "• 蓄力越久，球速越快、越高",
                "",
                "🏸 球拍碰撞检测",
                "• 球必须碰到球拍才能击飞",
                "• 精确的圆形碰撞检测",
                "• 击球角度受击球点影响",
                "",
                "🏸 物理系统",
                "• 重力影响抛物线轨迹",
                "• 空气阻力减缓球速",
                "• 跳跃物理：起跳和下落",
                "• 网碰撞：反弹并减速"
        });

        JPanel tipsPanel = createInfoPanel(new String[]{
                "💡 技巧提示",
                "1. 空中接球：可以在跳跃时蓄力接球",
                "2. 移动与蓄力并发：蓄力时也可以移动",
                "3. 单击蓄力：单击J/1开始，再单击释放",
                "4. 精确击球：需要球拍与球碰撞才能击飞",
                "5. 击球角度：击球点越高，球越向上",
                "6. 二次击球：每人每回合只能击球一次",
                "7. 网前球：靠近网击球更容易过网",
                "8. 高远球：蓄力时间长可以打出高远球",
                "9. 防守位置：根据对方击球预判移动",
                "10. 进攻策略：打角度刁钻的球"
        });

        tabbedPane.addTab("基本规则", rulesPanel);
        tabbedPane.addTab("操作说明", controlPanel);
        tabbedPane.addTab("物理系统", physicsPanel);
        tabbedPane.addTab("技巧提示", tipsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JButton backButton = new JButton("返回菜单");
        backButton.setFont(new Font("微软雅黑", Font.BOLD, 20));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(200, 70, 70));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(150, 50));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> gameWindow.showMenu());

        backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                backButton.setBackground(new Color(220, 90, 90));
            }
            public void mouseExited(MouseEvent evt) {
                backButton.setBackground(new Color(200, 70, 70));
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel(String[] lines) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 40, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        textArea.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}