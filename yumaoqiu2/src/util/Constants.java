package util;

import java.awt.*;

public class Constants {
    // 窗口尺寸
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 600;

    // 游戏区域边界
    public static final int LEFT_BOUNDARY = 50;
    public static final int RIGHT_BOUNDARY = WINDOW_WIDTH - 50;
    public static final int TOP_BOUNDARY = 50;
    public static final int GROUND_Y = 500;
    public static final int NET_X = WINDOW_WIDTH / 2;

    // 物理常量
    public static final double GRAVITY = 0.5;
    public static final double AIR_RESISTANCE = 0.99;
    public static final int JUMP_POWER = -18;
    public static final int PLAYER_SPEED = 5;

    // 游戏设置
    public static final int WIN_SCORE = 6;
    public static final int FPS = 60;
    public static final int FRAME_DELAY = 1000 / FPS;

    // 颜色
    public static final Color COLOR_SKY = new Color(135, 206, 235);
    public static final Color COLOR_GRASS = new Color(34, 139, 34);
    public static final Color COLOR_COURT_LINE = Color.WHITE;
    public static final Color COLOR_NET = new Color(200, 200, 200);
    public static final Color COLOR_PLAYER1 = new Color(65, 105, 225);
    public static final Color COLOR_PLAYER2 = new Color(220, 20, 60);
    public static final Color COLOR_BALL = new Color(255, 215, 0);

    // 字体
    public static final Font FONT_TITLE = new Font("Microsoft YaHei", Font.BOLD, 72);
    public static final Font FONT_SCORE = new Font("Microsoft YaHei", Font.BOLD, 48);
    public static final Font FONT_MENU = new Font("Microsoft YaHei", Font.PLAIN, 24);
    public static final Font FONT_SMALL = new Font("Microsoft YaHei", Font.PLAIN, 16);
}