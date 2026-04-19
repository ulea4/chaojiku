package yumaoqou.panel;

import yumaoqou.core.GameCore;
import yumaoqou.window.GameWindow;
import yumaoqou.util.FontManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HelpPanel extends JPanel {
    private GameWindow gameWindow;
    private FontManager fontManager;

    public HelpPanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.fontManager = FontManager.getInstance();
        setPreferredSize(new Dimension(GameCore.WIDTH, GameCore.HEIGHT));
        setLayout(new BorderLayout());
        setBackground(new Color(20, 30, 40));

        JLabel titleLabel = new JLabel("游戏帮助", SwingConstants.CENTER);
        titleLabel.setFont(fontManager.getBoldFont(52));
        titleLabel.setForeground(new Color(255, 215, 0));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(fontManager.getPlainFont(18));
        tabbedPane.setBackground(new Color(40, 50, 60));
        tabbedPane.setForeground(Color.WHITE);

        JPanel rulesPanel = createInfoPanel(new String[]{
                "🏸 基本规则",
                "═══════════════════════",
                "• 平视视角，球员站在地面",
                "• 球员不能越过黄色虚线（不能过网）",
                "• 球必须过网且落地才能得分",
                "• 赢家发球规则",
                "• 先得11分者获胜",
                "• 连击时得分加倍",
                "",
                "🎯 得分规则",
                "═══════════════════════",
                "• 球在对方半场落地：得1分",
                "• 连击状态下得分：得2分",
                "• 球出界：对方得1分",
                "• 球碰网后落地：对方得1分"
        });

        JPanel controlPanel = createInfoPanel(new String[]{
                "🎮 玩家1控制（蓝方）",
                "═══════════════════════",
                "   A - 向左移动",
                "   D - 向右移动",
                "   W - 跳跃",
                "   S - 闪避冲刺（消耗能量）",
                "   J - 按住蓄力，松开挥拍",
                "   K - 取消蓄力",
                "",
                "🎮 玩家2控制（红方）",
                "═══════════════════════",
                "   ← - 向左移动",
                "   → - 向右移动",
                "   ↑ - 跳跃",
                "   ↓ - 闪避冲刺（消耗能量）",
                "   1 (数字键1) - 按住蓄力，松开挥拍",
                "   2 (数字键2) - 取消蓄力",
                "",
                "⌨️ 通用控制",
                "═══════════════════════",
                "   R - 重新开始游戏",
                "   B - 显示/隐藏碰撞箱（调试）",
                "   ESC - 返回菜单"
        });

        JPanel skillPanel = createInfoPanel(new String[]{
                "⚡ 蓄力系统",
                "═══════════════════════",
                "• 任何时候都可以开始蓄力！",
                "• 发球蓄力: 2秒达到最大力量",
                "• 接球蓄力: 1秒达到最大力量",
                "• 扣杀蓄力: 跳起后蓄力1.5秒",
                "• 松开按键即可挥拍",
                "• 空挥也有特效",
                "• 蓄力过程中可松开取消",
                "",
                "🎯 击球类型",
                "═══════════════════════",
                "• 发球 - 比赛开始时的击球",
                "• 扣杀 - 跳起后强力击球",
                "• 高远球 - 蓄力70%以上",
                "• 吊球 - 蓄力30%以下",
                "• 平抽 - 正常蓄力击球",
                "",
                "💨 技能系统",
                "═══════════════════════",
                "• 闪避 - 消耗20能量快速移动",
                "• 扣杀 - 消耗30能量发动",
                "• 能量自动恢复"
        });

        JPanel aiPanel = createInfoPanel(new String[]{
                "🤖 AI智能系统",
                "═══════════════════════",
                "• AI会预测球的轨迹",
                "• AI会根据难度调整反应",
                "• AI会使用扣杀和闪避",
                "• AI会根据玩家站位选择策略",
                "",
                "📊 难度等级",
                "═══════════════════════",
                "• 简单 - AI反应慢，攻击性低",
                "• 中等 - AI正常反应，有策略",
                "• 困难 - AI反应快，有风力影响",
                "• 专家 - AI近乎完美，强风天气",
                "",
                "🌬️ 天气系统",
                "═══════════════════════",
                "• 困难和专家难度有风力",
                "• 风力影响球的飞行轨迹",
                "• 风向会随机变化"
        });

        JPanel tipsPanel = createInfoPanel(new String[]{
                "💡 技巧提示",
                "═══════════════════════",
                "1. 提前蓄力：球还没过来就可以蓄力",
                "2. 空中击球：跳跃时可以挥拍击球",
                "3. 蓄力时机：蓄力越长力量越大",
                "4. 取消蓄力：按取消键可放弃蓄力",
                "5. 击球角度：击球点越高球越向上",
                "6. 网前球：靠近网击球更容易过网",
                "7. 高远球：蓄力70%以上打出",
                "8. 吊球：轻蓄力让球贴网而过",
                "9. 扣杀：跳起后蓄力发动强力扣杀",
                "10. 闪避：被调动时用闪避快速回位",
                "",
                "🎨 画质特性",
                "═══════════════════════",
                "• 抗锯齿渲染",
                "• 动态渐变背景",
                "• 粒子特效系统",
                "• 光影效果",
                "• 轨迹拖尾特效",
                "• 子弹时间（扣杀时）"
        });

        tabbedPane.addTab("基本规则", rulesPanel);
        tabbedPane.addTab("操作说明", controlPanel);
        tabbedPane.addTab("技能系统", skillPanel);
        tabbedPane.addTab("AI智能", aiPanel);
        tabbedPane.addTab("技巧提示", tipsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JButton backButton = new JButton("返回菜单");
        backButton.setFont(fontManager.getBoldFont(22));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(200, 70, 70));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(160, 55));
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
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setForeground(new Color(240, 240, 240));
        textArea.setFont(fontManager.getPlainFont(17));
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(0, 0, new Color(20, 30, 40),
                getWidth(), getHeight(), new Color(30, 40, 55));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}