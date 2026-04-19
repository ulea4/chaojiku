package yumaoqou.panel;

import yumaoqou.core.GameCore;
import yumaoqou.window.GameWindow;
import yumaoqou.util.FontManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModeSelectPanel extends JPanel {
    private GameWindow gameWindow;
    private int selectedMode = GameCore.MODE_VS_PLAYER;
    private int selectedDifficulty = GameCore.DIFF_MEDIUM;
    private JLabel modeDescLabel;
    private JLabel diffDescLabel;
    private FontManager fontManager;

    public ModeSelectPanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.fontManager = FontManager.getInstance();
        setPreferredSize(new Dimension(GameCore.WIDTH, GameCore.HEIGHT));
        setLayout(new BorderLayout());
        setBackground(new Color(20, 30, 40));

        JLabel titleLabel = new JLabel("选择游戏模式", SwingConstants.CENTER);
        titleLabel.setFont(fontManager.getBoldFont(52));
        titleLabel.setForeground(new Color(255, 215, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 30, 15, 30);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JPanel modePanel = createModePanel();
        centerPanel.add(modePanel, gbc);

        JPanel diffPanel = createDifficultyPanel();
        centerPanel.add(diffPanel, gbc);

        modeDescLabel = new JLabel("", SwingConstants.CENTER);
        modeDescLabel.setFont(fontManager.getPlainFont(18));
        modeDescLabel.setForeground(new Color(200, 200, 200));
        centerPanel.add(modeDescLabel, gbc);

        diffDescLabel = new JLabel("", SwingConstants.CENTER);
        diffDescLabel.setFont(fontManager.getPlainFont(16));
        diffDescLabel.setForeground(new Color(180, 180, 180));
        centerPanel.add(diffDescLabel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 25));
        buttonPanel.setOpaque(false);

        JButton startButton = createButton("开始游戏", new Color(70, 130, 200));
        JButton backButton = createButton("返回菜单", new Color(200, 70, 70));

        startButton.addActionListener(e -> {
            gameWindow.startGame(selectedMode, selectedDifficulty);
        });
        backButton.addActionListener(e -> gameWindow.showMenu());

        buttonPanel.add(startButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        updateDescriptions();
    }

    private JPanel createModePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 10));

        JRadioButton vsPlayerBtn = createRadioButton("双人对战", true);
        JRadioButton vsAIBtn = createRadioButton("人机对战", false);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(vsPlayerBtn);
        modeGroup.add(vsAIBtn);

        vsPlayerBtn.addActionListener(e -> {
            selectedMode = GameCore.MODE_VS_PLAYER;
            updateDescriptions();
        });
        vsAIBtn.addActionListener(e -> {
            selectedMode = GameCore.MODE_VS_AI;
            updateDescriptions();
        });

        panel.add(vsPlayerBtn);
        panel.add(vsAIBtn);

        return panel;
    }

    private JPanel createDifficultyPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JRadioButton easyBtn = createRadioButton("简单", false);
        JRadioButton mediumBtn = createRadioButton("中等", true);
        JRadioButton hardBtn = createRadioButton("困难", false);
        JRadioButton expertBtn = createRadioButton("专家", false);

        ButtonGroup diffGroup = new ButtonGroup();
        diffGroup.add(easyBtn);
        diffGroup.add(mediumBtn);
        diffGroup.add(hardBtn);
        diffGroup.add(expertBtn);

        easyBtn.addActionListener(e -> {
            selectedDifficulty = GameCore.DIFF_EASY;
            updateDescriptions();
        });
        mediumBtn.addActionListener(e -> {
            selectedDifficulty = GameCore.DIFF_MEDIUM;
            updateDescriptions();
        });
        hardBtn.addActionListener(e -> {
            selectedDifficulty = GameCore.DIFF_HARD;
            updateDescriptions();
        });
        expertBtn.addActionListener(e -> {
            selectedDifficulty = GameCore.DIFF_EXPERT;
            updateDescriptions();
        });

        panel.add(easyBtn);
        panel.add(mediumBtn);
        panel.add(hardBtn);
        panel.add(expertBtn);

        return panel;
    }

    private JRadioButton createRadioButton(String text, boolean selected) {
        JRadioButton btn = new JRadioButton(text, selected);
        btn.setFont(fontManager.getBoldFont(24));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateDescriptions() {
        String modeDesc = "";
        String diffDesc = "";

        switch (selectedMode) {
            case GameCore.MODE_VS_PLAYER:
                modeDesc = "与朋友一起享受羽毛球乐趣！";
                break;
            case GameCore.MODE_VS_AI:
                modeDesc = "挑战智能AI对手，测试你的实力！";
                break;
        }

        switch (selectedDifficulty) {
            case GameCore.DIFF_EASY:
                diffDesc = "AI反应较慢，攻击性低，适合新手练习";
                break;
            case GameCore.DIFF_MEDIUM:
                diffDesc = "AI正常反应，会使用基本策略";
                break;
            case GameCore.DIFF_HARD:
                diffDesc = "AI反应迅速，善于预判，有风力影响";
                break;
            case GameCore.DIFF_EXPERT:
                diffDesc = "AI几乎完美，攻击性强，强风天气";
                break;
        }

        modeDescLabel.setText(modeDesc);
        diffDescLabel.setText(diffDesc);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(fontManager.getBoldFont(24));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 55));
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(0, 0, new Color(20, 30, 40),
                getWidth(), getHeight(), new Color(40, 50, 70));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(new Color(255, 215, 0, 50));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(100, 120, getWidth() - 100, 120);
    }
}