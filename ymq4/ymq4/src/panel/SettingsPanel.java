package yumaoqou.panel;

import yumaoqou.core.GameCore;
import yumaoqou.window.GameWindow;
import yumaoqou.util.FontManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingsPanel extends JPanel {
    private GameWindow gameWindow;
    private FontManager fontManager;
    private JSlider volumeSlider;
    private JCheckBox musicCheckBox;
    private JCheckBox effectsCheckBox;
    private JComboBox<String> difficultyCombo;
    private JLabel volumeValueLabel;

    public SettingsPanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.fontManager = FontManager.getInstance();
        setPreferredSize(new Dimension(GameCore.WIDTH, GameCore.HEIGHT));
        setLayout(new BorderLayout());
        setBackground(new Color(20, 30, 40));

        JLabel titleLabel = new JLabel("游戏设置", SwingConstants.CENTER);
        titleLabel.setFont(fontManager.getBoldFont(52));
        titleLabel.setForeground(new Color(255, 215, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;

        // 音量控制
        JLabel volumeLabel = new JLabel("音量控制");
        volumeLabel.setFont(fontManager.getBoldFont(26));
        volumeLabel.setForeground(Color.WHITE);
        settingsPanel.add(volumeLabel, gbc);

        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volumePanel.setOpaque(false);

        volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setPreferredSize(new Dimension(350, 45));
        volumeSlider.setOpaque(false);
        volumeSlider.setBackground(new Color(60, 70, 80));

        volumeValueLabel = new JLabel("70%");
        volumeValueLabel.setFont(fontManager.getPlainFont(20));
        volumeValueLabel.setForeground(Color.WHITE);

        volumeSlider.addChangeListener(e -> {
            volumeValueLabel.setText(volumeSlider.getValue() + "%");
        });

        volumePanel.add(volumeSlider);
        volumePanel.add(volumeValueLabel);
        settingsPanel.add(volumePanel, gbc);

        // 难度选择
        JLabel difficultyLabel = new JLabel("默认难度");
        difficultyLabel.setFont(fontManager.getBoldFont(26));
        difficultyLabel.setForeground(Color.WHITE);
        settingsPanel.add(difficultyLabel, gbc);

        String[] difficulties = {"简单", "中等", "困难", "专家"};
        difficultyCombo = new JComboBox<>(difficulties);
        difficultyCombo.setFont(fontManager.getPlainFont(20));
        difficultyCombo.setPreferredSize(new Dimension(220, 45));
        difficultyCombo.setBackground(new Color(60, 70, 80));
        difficultyCombo.setForeground(Color.WHITE);
        difficultyCombo.setSelectedIndex(1);
        settingsPanel.add(difficultyCombo, gbc);

        // 音乐开关
        musicCheckBox = new JCheckBox("背景音乐");
        musicCheckBox.setFont(fontManager.getPlainFont(22));
        musicCheckBox.setForeground(Color.WHITE);
        musicCheckBox.setOpaque(false);
        musicCheckBox.setSelected(true);
        settingsPanel.add(musicCheckBox, gbc);

        // 音效开关
        effectsCheckBox = new JCheckBox("游戏音效");
        effectsCheckBox.setFont(fontManager.getPlainFont(22));
        effectsCheckBox.setForeground(Color.WHITE);
        effectsCheckBox.setOpaque(false);
        effectsCheckBox.setSelected(true);
        settingsPanel.add(effectsCheckBox, gbc);

        // 信息区域
        JTextArea infoArea = new JTextArea(
                "═══════════════════════════════════\n" +
                        "      游戏版本: 高画质增强版 v2.0\n" +
                        "      最后更新: 2026年\n" +
                        "      开发者: 羽毛球大师赛团队\n" +
                        "═══════════════════════════════════\n\n" +
                        "⭐ 核心特性:\n" +
                        "  • 任何时候都可以蓄力/挥拍！\n" +
                        "  • 空挥也有特效和音效\n" +
                        "  • 智能AI对手系统\n" +
                        "  • 天气系统影响球路\n" +
                        "  • 扣杀、吊球、高远球\n" +
                        "  • 连击奖励系统\n\n" +
                        "⚡ 蓄力说明:\n" +
                        "  • 发球蓄力: 2秒最大\n" +
                        "  • 接球蓄力: 1秒最大\n" +
                        "  • 扣杀蓄力: 1.5秒\n" +
                        "  • 长按蓄力键可取消\n\n" +
                        "🏆 游戏规则:\n" +
                        "  • 先得11分者获胜\n" +
                        "  • 连击时得分加倍\n" +
                        "  • 球落地或出界即得分"
        );
        infoArea.setFont(fontManager.getPlainFont(15));
        infoArea.setForeground(new Color(220, 220, 220));
        infoArea.setOpaque(false);
        infoArea.setEditable(false);
        infoArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        settingsPanel.add(infoArea, gbc);

        add(settingsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 25));
        buttonPanel.setOpaque(false);

        JButton saveButton = createButton("保存设置", new Color(70, 130, 200));
        JButton resetButton = createButton("恢复默认", new Color(100, 180, 100));
        JButton backButton = createButton("返回菜单", new Color(200, 70, 70));

        saveButton.addActionListener(e -> saveSettings());
        resetButton.addActionListener(e -> resetSettings());
        backButton.addActionListener(e -> gameWindow.showMenu());

        buttonPanel.add(saveButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(fontManager.getBoldFont(20));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void saveSettings() {
        String message = String.format(
                "═══════════════════════\n" +
                        "      设置已保存！\n" +
                        "═══════════════════════\n\n" +
                        "音量: %d%%\n" +
                        "默认难度: %s\n" +
                        "背景音乐: %s\n" +
                        "游戏音效: %s",
                volumeSlider.getValue(),
                difficultyCombo.getSelectedItem(),
                musicCheckBox.isSelected() ? "开启" : "关闭",
                effectsCheckBox.isSelected() ? "开启" : "关闭"
        );

        JTextArea textArea = new JTextArea(message);
        textArea.setFont(fontManager.getPlainFont(16));
        textArea.setEditable(false);
        textArea.setOpaque(false);

        JOptionPane.showMessageDialog(this,
                textArea, "设置保存成功",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetSettings() {
        volumeSlider.setValue(70);
        difficultyCombo.setSelectedIndex(1);
        musicCheckBox.setSelected(true);
        effectsCheckBox.setSelected(true);

        JOptionPane.showMessageDialog(this,
                "所有设置已恢复为默认值", "设置重置",
                JOptionPane.INFORMATION_MESSAGE);
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