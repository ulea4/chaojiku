package yumaoqou.utils;

import java.awt.*;

public class GameConstants {
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 800;
    public static final int PADDLE_WIDTH = 35;
    public static final int PADDLE_HEIGHT = 90;
    public static final int BALL_SIZE = 16;
    public static final int PLAYER_SPEED = 8;
    public static final int JUMP_SPEED = 12;
    public static final int WIN_SCORE = 7;

    public static final int GROUND_Y = HEIGHT - 150;
    public static final int NET_X = WIDTH / 2;
    public static final int NET_TOP_Y = GROUND_Y - 120;
    public static final int NET_BOTTOM_Y = GROUND_Y - 40;
    public static final int NET_HEIGHT = NET_TOP_Y;
    public static final int NET_WIDTH = 16;
    public static final int NET_LEFT = NET_X - NET_WIDTH/2;
    public static final int NET_RIGHT = NET_X + NET_WIDTH/2;
    public static final int LEFT_BOUNDARY = 80;
    public static final int RIGHT_BOUNDARY = WIDTH - 80;

    public static final int PLAYER1_MAX_X = NET_X - 60;
    public static final int PLAYER2_MIN_X = NET_X + 60;
    public static final int PLAYER1_START_X = 200;
    public static final int PLAYER2_START_X = WIDTH - 200;

    public static final float GRAVITY = 0.28f;
    public static final float AIR_RESISTANCE = 0.985f;
    public static final float MAX_POWER = 18f;
    public static final float MIN_POWER = 7f;
    public static final float NET_BOUNCE_LOSS = 0.6f;
    public static final float JUMP_GRAVITY = 0.42f;
    public static final float MIN_SPEED = 1.2f;

    public static final long MAX_CHARGE_TIME = 1200;
    public static final float MAX_ANGLE = 0.75f;
    public static final float MIN_ANGLE = 0.25f;

    public static final long HIT_COOLDOWN = 400;

    public static final Color PLAYER1_COLOR = new Color(70, 130, 200);
    public static final Color PLAYER2_COLOR = new Color(200, 70, 70);
    public static final Color SKY_TOP = new Color(135, 206, 235);
    public static final Color SKY_BOTTOM = new Color(255, 255, 224);
    public static final Color GROUND_COLOR = new Color(34, 139, 34);
    public static final Color NET_COLOR = new Color(139, 69, 19);
    public static final Color CHARGE_COLOR = new Color(255, 200, 0);
}