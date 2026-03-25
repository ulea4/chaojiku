package yumaoqou.ui;

import yumaoqou.utils.GameConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingsPanel extends JPanel {
    private GameWindow gameWindow;
    private JSlider volumeSlider;
    private JCheckBox musicCheckBox;
    private JCheckBox effectsCheckBox;
    private JComboBox<String> difficultyCombo;
    private JLabel volumeValueLabel;

    public SettingsPanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setPreferredSize(new Dimension(GameConstants.WIDTH, GameConstants.HEIGHT));
        setLayout(new BorderLayout());
        setBackground(new Color(20, 30, 40));

        JLabel titleLabel = new JLabel("游戏设置", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 215, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel volumeLabel = new JLabel("音量控制");
        volumeLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        volumeLabel.setForeground(Color.WHITE);
        settingsPanel.add(volumeLabel, gbc);

        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volumePanel.setOpaque(false);

        volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setPreferredSize(new Dimension(300, 40));
        volumeSlider.setOpaque(false);

        volumeValueLabel = new JLabel("70%");
        volumeValueLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        volumeValueLabel.setForeground(Color.WHITE);

        volumeSlider.addChangeListener(e -> {
            volumeValueLabel.setText(volumeSlider.getValue() + "%");
        });

        volumePanel.add(volumeSlider);
        volumePanel.add(volumeValueLabel);
        settingsPanel.add(volumePanel, gbc);

        JLabel difficultyLabel = new JLabel("游戏难度");
        difficultyLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        difficultyLabel.setForeground(Color.WHITE);
        settingsPanel.add(difficultyLabel, gbc);

        String[] difficulties = {"简单", "中等", "困难", "专家"};
        difficultyCombo = new JComboBox<>(difficulties);
        difficultyCombo.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        difficultyCombo.setPreferredSize(new Dimension(200, 40));
        difficultyCombo.setBackground(new Color(60, 70, 80));
        difficultyCombo.setForeground(Color.WHITE);
        settingsPanel.add(difficultyCombo, gbc);

        musicCheckBox = new JCheckBox("背景音乐");
        musicCheckBox.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        musicCheckBox.setForeground(Color.WHITE);
        musicCheckBox.setOpaque(false);
        musicCheckBox.setSelected(true);
        settingsPanel.add(musicCheckBox, gbc);

        effectsCheckBox = new JCheckBox("特效音效");
        effectsCheckBox.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        effectsCheckBox.setForeground(Color.WHITE);
        effectsCheckBox.setOpaque(false);
        effectsCheckBox.setSelected(true);
        settingsPanel.add(effectsCheckBox, gbc);

        JTextArea infoArea = new JTextArea(
                "游戏版本: 多线程并发版 v6.0\n" +
                        "最后更新: 2024年\n" +
                        "开发者: 羽毛球大师赛团队\n\n" +
                        "技术特性:\n" +
                        "• 多线程并发控制\n" +
                        "• 移动与蓄力独立线程\n" +
                        "• 球拍精确碰撞检测\n" +
                        "• 单击蓄力击球系统\n\n" +
                        "蓄力说明:\n" +
                        "• 单击J/1键开始蓄力\n" +
                        "• 蓄力过程中可移动\n" +
                        "• 再次单击释放击球\n" +
                        "• 最大蓄力1.5秒"
        );
        infoArea.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        infoArea.setForeground(new Color(200, 200, 200));
        infoArea.setOpaque(false);
        infoArea.setEditable(false);
        infoArea.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        settingsPanel.add(infoArea, gbc);

        add(settingsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
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
        button.setFont(new Font("微软雅黑", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 45));
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
                "设置已保存！\n\n音量: %d%%\n难度: %s\n背景音乐: %s\n特效音效: %s",
                volumeSlider.getValue(),
                difficultyCombo.getSelectedItem(),
                musicCheckBox.isSelected() ? "开启" : "关闭",
                effectsCheckBox.isSelected() ? "开启" : "关闭"
        );

        JOptionPane.showMessageDialog(this,
                message, "设置保存成功",
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
}