package yumaoqou.launcher;

import yumaoqou.window.GameWindow;
import javax.swing.*;
import java.awt.*;

public class GameLauncher {
    public static void main(String[] args) {
        // 设置全局中文字体
        setGlobalFont();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);
        });
    }

    /**
     * 设置全局中文字体，确保中文正常显示
     */
    private static void setGlobalFont() {
        // 获取系统中可用的中文字体
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        String chineseFontName = null;

        // 按优先级查找中文字体
        String[] preferredFonts = {
                "微软雅黑", "Microsoft YaHei", "Microsoft YaHei UI",
                "宋体", "SimSun",
                "黑体", "SimHei",
                "楷体", "KaiTi",
                "Dialog", "SansSerif"
        };

        for (String preferred : preferredFonts) {
            for (Font font : fonts) {
                if (font.getName().equals(preferred) || font.getFamily().equals(preferred)) {
                    chineseFontName = font.getName();
                    break;
                }
            }
            if (chineseFontName != null) break;
        }

        // 如果没找到，使用默认字体
        if (chineseFontName == null) {
            chineseFontName = "SansSerif";
        }

        // 设置全局默认字体
        Font defaultFont = new Font(chineseFontName, Font.PLAIN, 12);
        UIManager.put("defaultFont", defaultFont);

        System.out.println("使用字体: " + chineseFontName);
    }
}