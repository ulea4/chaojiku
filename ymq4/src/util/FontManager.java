package yumaoqou.util;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 字体管理器 - 统一管理游戏中使用的字体，确保中文正常显示
 */
public class FontManager {
    private static FontManager instance;
    private String chineseFontName;
    private Map<String, Font> fontCache = new HashMap<>();

    private FontManager() {
        initChineseFont();
    }

    public static FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }
        return instance;
    }

    private void initChineseFont() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();

        // 按优先级查找中文字体
        String[] preferredFonts = {
                "微软雅黑", "Microsoft YaHei", "Microsoft YaHei UI",
                "宋体", "SimSun",
                "黑体", "SimHei",
                "楷体", "KaiTi",
                "方正舒体", "FZShuTi",
                "幼圆", "YouYuan",
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

        if (chineseFontName == null) {
            chineseFontName = "SansSerif";
        }

        System.out.println("中文字体: " + chineseFontName);
    }

    /**
     * 获取字体
     * @param style 字体样式 Font.PLAIN, Font.BOLD, Font.ITALIC
     * @param size 字体大小
     * @return Font对象
     */
    public Font getFont(int style, float size) {
        String key = chineseFontName + "-" + style + "-" + size;
        if (!fontCache.containsKey(key)) {
            fontCache.put(key, new Font(chineseFontName, style, (int)size).deriveFont(size));
        }
        return fontCache.get(key);
    }

    /**
     * 获取普通字体
     */
    public Font getPlainFont(float size) {
        return getFont(Font.PLAIN, size);
    }

    /**
     * 获取粗体字体
     */
    public Font getBoldFont(float size) {
        return getFont(Font.BOLD, size);
    }

    /**
     * 获取斜体字体
     */
    public Font getItalicFont(float size) {
        return getFont(Font.ITALIC, size);
    }

    /**
     * 获取字体名称
     */
    public String getFontName() {
        return chineseFontName;
    }
}