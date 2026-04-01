package yumaoqou.main;

import yumaoqou.ui.GameWindow;
import javax.swing.*;

public class GameLauncher {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new GameWindow().setVisible(true));
    }
}