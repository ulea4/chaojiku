package yumaoqou.window;

import yumaoqou.panel.*;
import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private SettingsPanel settingsPanel;
    private HelpPanel helpPanel;
    private ModeSelectPanel modeSelectPanel;

    public GameWindow() {
        setTitle("羽毛球大师赛 - 高画质增强版");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        } catch (Exception e) {}

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);
        settingsPanel = new SettingsPanel(this);
        helpPanel = new HelpPanel(this);
        modeSelectPanel = new ModeSelectPanel(this);

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(modeSelectPanel, "MODE");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(settingsPanel, "SETTINGS");
        mainPanel.add(helpPanel, "HELP");

        add(mainPanel);
        pack();

        setLocationRelativeTo(null);
    }

    public void showMenu() {
        cardLayout.show(mainPanel, "MENU");
    }

    public void showModeSelect() {
        cardLayout.show(mainPanel, "MODE");
    }

    public void startGame(int mode, int difficulty) {
        gamePanel.setGameMode(mode, difficulty);
        gamePanel.startNewGame();
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocus();
    }

    public void showSettings() {
        cardLayout.show(mainPanel, "SETTINGS");
    }

    public void showHelp() {
        cardLayout.show(mainPanel, "HELP");
    }

    public void exitGame() {
        int choice = JOptionPane.showConfirmDialog(this,
                "确定要退出游戏吗？", "退出游戏",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}