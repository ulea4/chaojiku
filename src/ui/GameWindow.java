package yumaoqou.ui;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private SettingsPanel settingsPanel;
    private HelpPanel helpPanel;

    public GameWindow() {
        setTitle("羽毛球大师赛 - 多线程并发版");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);
        settingsPanel = new SettingsPanel(this);
        helpPanel = new HelpPanel(this);

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(settingsPanel, "SETTINGS");
        mainPanel.add(helpPanel, "HELP");

        add(mainPanel);
        pack();

        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (gamePanel != null) {
                    gamePanel.stop();
                }
            }
        });
    }

    public void showMenu() {
        cardLayout.show(mainPanel, "MENU");
    }

    public void startGame() {
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
            if (gamePanel != null) {
                gamePanel.stop();
            }
            System.exit(0);
        }
    }
}