package game;

import javax.swing.*;

public class GameManager {
    private JFrame mainFrame;
    private GamePanel gamePanel;

    public GameManager() {
        initialize();
    }

    private void initialize() {
        mainFrame = new JFrame("🏸 羽毛球大战 - Badminton Battle");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);

        gamePanel = new GamePanel(this);
        mainFrame.add(gamePanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
    }

    public void start() {
        mainFrame.setVisible(true);
        gamePanel.requestFocusInWindow();
    }

    public void exitGame() {
        System.exit(0);
    }

    public void restartGame() {
        gamePanel = new GamePanel(this);
        mainFrame.setContentPane(gamePanel);
        mainFrame.revalidate();
        gamePanel.requestFocusInWindow();
    }
}