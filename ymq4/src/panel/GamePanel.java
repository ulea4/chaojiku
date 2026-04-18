package yumaoqou.panel;

import yumaoqou.core.GameCore;
import yumaoqou.window.GameWindow;
import yumaoqou.util.FontManager;
import util.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GamePanel extends JPanel implements KeyListener, Runnable {
    private GameWindow gameWindow;
    private GameCore gameCore;

    private boolean aPressed, dPressed, wPressed, sPressed, jPressed, kPressed;
    private boolean leftPressed, rightPressed, upPressed, downPressed, num1Pressed, num2Pressed;
    private boolean showDebugBounds = false;

    private GradientPaint skyGradient;
    private GradientPaint grassGradient;

    private int gameMode = GameCore.MODE_VS_PLAYER;
    private int difficulty = GameCore.DIFF_MEDIUM;

    private FontManager fontManager;
    private ImageManager imageManager;

    public GamePanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.gameCore = new GameCore();
        this.fontManager = FontManager.getInstance();
        this.imageManager = ImageManager.getInstance();

        setPreferredSize(new Dimension(GameCore.WIDTH, GameCore.HEIGHT));
        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(new Color(20, 30, 40));

        skyGradient = new GradientPaint(0, 0, new Color(135, 206, 235),
                0, GameCore.HEIGHT, new Color(255, 255, 224));
        grassGradient = new GradientPaint(0, GameCore.GROUND_Y - 20, new Color(34, 139, 34),
                0, GameCore.HEIGHT, new Color(20, 100, 20));

        addKeyListener(this);

        new Thread(this).start();
    }

    public void setGameMode(int mode, int diff) {
        this.gameMode = mode;
        this.difficulty = diff;
        gameCore.setGameMode(mode, diff);
    }

    public void startNewGame() {
        gameCore.resetGame();
    }

    private String getDifficultyName() {
        switch (difficulty) {
            case GameCore.DIFF_EASY: return "简单";
            case GameCore.DIFF_MEDIUM: return "中等";
            case GameCore.DIFF_HARD: return "困难";
            case GameCore.DIFF_EXPERT: return "专家";
            default: return "中等";
        }
    }

    private String getChineseShotType(String shotType) {
        if (shotType == null) return "";
        switch (shotType) {
            case "SERVE": return "发球";
            case "SMASH": return "扣杀";
            case "CLEAR": return "高远球";
            case "DROP": return "吊球";
            case "DRIVE": return "平抽";
            default: return shotType;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        drawBackground(g2d);
        drawWeatherEffects(g2d);
        drawCourt(g2d);
        drawNet(g2d);

        if (showDebugBounds) {
            drawCollisionBounds(g2d);
        }

        drawBoundaryLines(g2d);
        drawStickMen(g2d);
        drawBall(g2d);
        drawEffects(g2d);
        drawUI(g2d);

        if (gameCore.isBulletTime()) {
            drawBulletTimeEffect(g2d);
        }

        if (!gameCore.isGameRunning()) {
            drawGameOver(g2d);
        }
    }

    private void drawBackground(Graphics2D g2d) {
        // 尝试使用外部背景图片
        BufferedImage bgImage = imageManager.getImage("background");
        if (bgImage != null) {
            g2d.drawImage(bgImage, 0, 0, GameCore.WIDTH, GameCore.HEIGHT, null);
        } else {
            // 使用渐变背景
            g2d.setPaint(skyGradient);
            g2d.fillRect(0, 0, GameCore.WIDTH, GameCore.HEIGHT);
        }

        // 绘制云朵（使用图片或默认绘制）
        drawCloud(g2d, 100, 80, 60);
        drawCloud(g2d, 400, 50, 80);
        drawCloud(g2d, 800, 100, 70);
        drawCloud(g2d, 1050, 60, 50);

        g2d.setPaint(grassGradient);
        g2d.fillRect(0, GameCore.GROUND_Y - 20, GameCore.WIDTH, 120);

        g2d.setColor(new Color(0, 80, 0, 100));
        g2d.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < GameCore.WIDTH; i += 25) {
            int height = 8 + (int)(Math.sin(i * 0.05 + System.currentTimeMillis() * 0.001) * 4);
            g2d.drawLine(i, GameCore.GROUND_Y - 20, i - 5, GameCore.GROUND_Y - 20 - height);
            g2d.drawLine(i + 8, GameCore.GROUND_Y - 20, i + 3, GameCore.GROUND_Y - 20 - height + 3);
        }
    }

    private void drawWeatherEffects(Graphics2D g2d) {
        if (!gameCore.isWeatherEnabled()) return;

        float windPower = gameCore.getWindPower();
        float windDir = gameCore.getWindDirection();

        int windX = GameCore.WIDTH - 120;
        int windY = 100;

        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setFont(fontManager.getBoldFont(14));
        g2d.drawString("风力: " + String.format("%.1f", windPower), windX - 20, windY - 10);

        g2d.setStroke(new BasicStroke(3));
        int arrowLength = 40 + (int)(windPower * 10);
        int endX = windX + (int)(Math.cos(windDir) * arrowLength);
        int endY = windY + (int)(Math.sin(windDir) * arrowLength);

        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.drawLine(windX, windY, endX, endY);

        double angle = Math.atan2(endY - windY, endX - windX);
        int arrowSize = 10;
        int x1 = endX - (int)(arrowSize * Math.cos(angle - 0.8));
        int y1 = endY - (int)(arrowSize * Math.sin(angle - 0.8));
        int x2 = endX - (int)(arrowSize * Math.cos(angle + 0.8));
        int y2 = endY - (int)(arrowSize * Math.sin(angle + 0.8));
        g2d.drawLine(endX, endY, x1, y1);
        g2d.drawLine(endX, endY, x2, y2);
    }

    private void drawCloud(Graphics2D g2d, int x, int y, int size) {
        // 尝试使用云朵图片
        BufferedImage cloudImage = imageManager.getImage("cloud");
        if (cloudImage != null) {
            g2d.drawImage(cloudImage, x, y, size, (int)(size * 0.6), null);
        } else {
            // 默认绘制云朵
            g2d.setColor(new Color(255, 255, 255, 180));
            Ellipse2D.Double cloud1 = new Ellipse2D.Double(x, y, size, size * 0.7);
            Ellipse2D.Double cloud2 = new Ellipse2D.Double(x + size * 0.6, y - size * 0.2, size * 0.8, size * 0.6);
            Ellipse2D.Double cloud3 = new Ellipse2D.Double(x - size * 0.2, y - size * 0.1, size * 0.7, size * 0.6);
            g2d.fill(cloud1);
            g2d.fill(cloud2);
            g2d.fill(cloud3);
        }
    }

    private void drawCourt(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fillRect(70, GameCore.GROUND_Y - 145, 340, 130);
        g2d.fillRect(GameCore.WIDTH - 410, GameCore.GROUND_Y - 145, 340, 130);

        g2d.setColor(new Color(255, 255, 255, 120));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(80, GameCore.GROUND_Y - 150, 300, 120);
        g2d.drawRect(GameCore.WIDTH - 380, GameCore.GROUND_Y - 150, 300, 120);

        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{15, 15}, 0));
        g2d.drawLine(GameCore.NET_X, GameCore.GROUND_Y - 150,
                GameCore.NET_X, GameCore.GROUND_Y - 30);

        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.drawLine(150, GameCore.GROUND_Y - 100, 310, GameCore.GROUND_Y - 100);
        g2d.drawLine(GameCore.WIDTH - 310, GameCore.GROUND_Y - 100,
                GameCore.WIDTH - 150, GameCore.GROUND_Y - 100);
    }

    private void drawNet(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRect(GameCore.NET_X - 6, GameCore.GROUND_Y - 148, 12, 130);

        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(GameCore.NET_X - 8, GameCore.GROUND_Y - 150, 16, 130);

        g2d.setColor(new Color(180, 100, 40));
        g2d.fillRect(GameCore.NET_X - 6, GameCore.GROUND_Y - 150, 4, 130);

        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setStroke(new BasicStroke(1.5f));

        for (int y = GameCore.GROUND_Y - 140; y < GameCore.GROUND_Y - 30; y += 12) {
            g2d.drawLine(GameCore.NET_X - 35, y, GameCore.NET_X + 35, y);
        }

        for (int x = GameCore.NET_X - 30; x <= GameCore.NET_X + 30; x += 15) {
            g2d.drawLine(x, GameCore.GROUND_Y - 145, x, GameCore.GROUND_Y - 35);
        }

        g2d.setColor(new Color(255, 255, 255));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(GameCore.NET_X - 38, GameCore.NET_TOP_Y,
                GameCore.NET_X + 38, GameCore.NET_TOP_Y);

        g2d.setColor(new Color(255, 255, 255, 60));
        g2d.fillRect(GameCore.NET_LEFT, GameCore.NET_BOTTOM_Y,
                GameCore.NET_WIDTH, GameCore.NET_TOP_Y - GameCore.NET_BOTTOM_Y);
    }

    private void drawCollisionBounds(Graphics2D g2d) {
        g2d.setColor(new Color(255, 0, 0, 80));
        g2d.fillRect(GameCore.getNetCollisionLeft(), GameCore.getNetCollisionTop(),
                GameCore.getNetCollisionRight() - GameCore.getNetCollisionLeft(),
                GameCore.getNetCollisionBottom() - GameCore.getNetCollisionTop());
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(GameCore.getNetCollisionLeft(), GameCore.getNetCollisionTop(),
                GameCore.getNetCollisionRight() - GameCore.getNetCollisionLeft(),
                GameCore.getNetCollisionBottom() - GameCore.getNetCollisionTop());
    }

    private void drawBoundaryLines(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(GameCore.PLAYER1_MAX_X + 2, GameCore.GROUND_Y - 198,
                GameCore.PLAYER1_MAX_X + 2, GameCore.GROUND_Y + 2);
        g2d.drawLine(GameCore.PLAYER2_MIN_X - 2, GameCore.GROUND_Y - 198,
                GameCore.PLAYER2_MIN_X - 2, GameCore.GROUND_Y + 2);

        g2d.setColor(new Color(255, 255, 0, 150));
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{10, 10}, 0));
        g2d.drawLine(GameCore.PLAYER1_MAX_X, GameCore.GROUND_Y - 200,
                GameCore.PLAYER1_MAX_X, GameCore.GROUND_Y);
        g2d.drawLine(GameCore.PLAYER2_MIN_X, GameCore.GROUND_Y - 200,
                GameCore.PLAYER2_MIN_X, GameCore.GROUND_Y);
    }

    private void drawStickMen(Graphics2D g2d) {
        ArrayList<GameCore.StickMan> stickMen = gameCore.getStickMen();
        if (stickMen != null) {
            for (GameCore.StickMan s : stickMen) {
                drawStickMan(g2d, s);
            }
        }
    }

    private void drawStickMan(Graphics2D g2d, GameCore.StickMan s) {
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        float headY = s.y - 35;
        float bodyY = s.y;

        if (s.isJumping) {
            g2d.setColor(new Color(0, 0, 0, 80));
            Ellipse2D.Double shadow = new Ellipse2D.Double(s.x - 18, GameCore.GROUND_Y - 18, 36, 12);
            g2d.fill(shadow);
        }

        if (s.isDashing) {
            g2d.setColor(new Color(255, 255, 255, 50));
            for (int i = 1; i <= 3; i++) {
                float offset = s.isLeft ? -i * 15 : i * 15;
                g2d.drawLine((int)(s.x + offset), (int)headY + 8, (int)(s.x + offset), (int)bodyY);
            }
        }

        Color bodyColor = s.isLeft ? new Color(70, 130, 200) : new Color(200, 70, 70);
        if (s.isDashing) {
            bodyColor = bodyColor.brighter();
        }
        g2d.setColor(bodyColor);
        g2d.drawLine((int)s.x, (int)headY + 8, (int)s.x, (int)bodyY);

        RadialGradientPaint headGrad = new RadialGradientPaint(
                s.x - 2, headY - 2, 15,
                new float[]{0f, 0.7f, 1f},
                new Color[]{bodyColor.brighter(), bodyColor, bodyColor.darker()}
        );
        g2d.setPaint(headGrad);
        g2d.fillOval((int)s.x - 13, (int)headY - 13, 26, 26);

        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)s.x - 8, (int)headY - 8, 7, 7);
        g2d.fillOval((int)s.x + 2, (int)headY - 8, 7, 7);
        g2d.setColor(Color.BLACK);
        g2d.fillOval((int)s.x - 7, (int)headY - 7, 4, 4);
        g2d.fillOval((int)s.x + 3, (int)headY - 7, 4, 4);
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)s.x - 8, (int)headY - 9, 2, 2);
        g2d.fillOval((int)s.x + 2, (int)headY - 9, 2, 2);

        g2d.setStroke(new BasicStroke(2));
        g2d.drawArc((int)s.x - 8, (int)headY - 2, 16, 10, 0, -180);

        drawArmsAndRacket(g2d, s);

        g2d.setColor(bodyColor);
        g2d.drawLine((int)s.x, (int)bodyY, (int)s.x - 13, (int)bodyY + 28);
        g2d.drawLine((int)s.x, (int)bodyY, (int)s.x + 13, (int)bodyY + 28);

        g2d.setColor(Color.DARK_GRAY);
        g2d.fillOval((int)s.x - 18, (int)bodyY + 24, 12, 6);
        g2d.fillOval((int)s.x + 6, (int)bodyY + 24, 12, 6);

        if (s.isCharging) {
            float power = s.chargeProgress;
            int glowSize = 30 + (int)(power * 50);

            Color glowColor = s.isSmashCharging ?
                    new Color(255, 100, 0, 120) : new Color(255, 200, 0, 120);

            RadialGradientPaint glow = new RadialGradientPaint(
                    s.x, headY - 5, glowSize,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{
                            glowColor,
                            new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 60),
                            new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 0)
                    }
            );
            g2d.setPaint(glow);
            g2d.fillOval((int)s.x - glowSize/2, (int)headY - glowSize/2 - 5, glowSize, glowSize);

            int barWidth = 80;
            int barHeight = 12;
            int barX = (int)s.x - barWidth/2;
            int barY = (int)headY - 60;

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRoundRect(barX, barY, barWidth, barHeight, 6, 6);

            Color barColor = s.isSmashCharging ? new Color(255, 100, 0) : new Color(255, 200, 0);
            g2d.setColor(barColor);
            g2d.fillRoundRect(barX + 2, barY + 2, (int)((barWidth - 4) * power), barHeight - 4, 4, 4);

            g2d.setColor(Color.WHITE);
            g2d.setFont(fontManager.getBoldFont(12));
            String chargeText = s.isSmashCharging ? "扣杀" : "蓄力";
            int textWidth = g2d.getFontMetrics().stringWidth(chargeText);
            g2d.drawString(chargeText, barX + barWidth/2 - textWidth/2, barY - 5);
        }
    }

    private void drawArmsAndRacket(Graphics2D g2d, GameCore.StickMan s) {
        float headY = s.y - 35;
        float shoulderY = headY + 15;

        Color bodyColor = s.isLeft ? new Color(70, 130, 200) : new Color(200, 70, 70);
        g2d.setColor(bodyColor);

        if (s.isLeft) {
            g2d.drawLine((int)s.x, (int)shoulderY, (int)s.x - 22, (int)shoulderY + 5);

            if (s.isSwinging) {
                float swingX = s.x + 45 + 30 * s.swingProgress;
                float swingY = shoulderY - 20 - 20 * s.swingProgress;
                g2d.drawLine((int)s.x, (int)shoulderY, (int)swingX, (int)swingY);
                drawBadmintonRacket(g2d, swingX, swingY, true, s.swingProgress);
            } else {
                g2d.drawLine((int)s.x, (int)shoulderY, (int)s.x + 38, (int)shoulderY - 12);
                drawBadmintonRacket(g2d, s.x + 38, shoulderY - 12, true, 0);
            }
        } else {
            g2d.drawLine((int)s.x, (int)shoulderY, (int)s.x + 22, (int)shoulderY + 5);

            if (s.isSwinging) {
                float swingX = s.x - 45 - 30 * s.swingProgress;
                float swingY = shoulderY - 20 - 20 * s.swingProgress;
                g2d.drawLine((int)s.x, (int)shoulderY, (int)swingX, (int)swingY);
                drawBadmintonRacket(g2d, swingX, swingY, false, s.swingProgress);
            } else {
                g2d.drawLine((int)s.x, (int)shoulderY, (int)s.x - 38, (int)shoulderY - 12);
                drawBadmintonRacket(g2d, s.x - 38, shoulderY - 12, false, 0);
            }
        }
    }

    private void drawBadmintonRacket(Graphics2D g2d, float x, float y, boolean isLeft, float swingProgress) {
        // 尝试使用球拍图片
        BufferedImage racketImage = imageManager.getImage("racket");
        if (racketImage != null) {
            int width = 30;
            int height = 50;
            if (!isLeft) {
                // 右侧玩家翻转图片
                g2d.drawImage(racketImage, (int)x - width/2, (int)y - height/2, 
                        (int)x + width/2, (int)y + height/2, 
                        racketImage.getWidth(), 0, 0, racketImage.getHeight(), null);
            } else {
                g2d.drawImage(racketImage, (int)x - width/2, (int)y - height/2, width, height, null);
            }
        } else {
            // 默认绘制球拍
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.drawOval((int)x - 13, (int)y - 19, 26, 32);

            g2d.setColor(new Color(180, 100, 50));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval((int)x - 13, (int)y - 19, 26, 32);

            g2d.setColor(new Color(255, 255, 255, 200));
            g2d.setStroke(new BasicStroke(1));
            for (int i = 0; i < 4; i++) {
                int yOffset = (int)y - 13 + i * 8;
                g2d.drawLine((int)x - 10, yOffset, (int)x + 10, yOffset);
            }
            for (int i = 0; i < 3; i++) {
                int xOffset = (int)x - 6 + i * 6;
                g2d.drawLine(xOffset, (int)y - 15, xOffset, (int)y + 9);
            }

            g2d.setColor(new Color(139, 69, 19));
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine((int)x, (int)y + 13, (int)x, (int)y + 40);

            g2d.setColor(new Color(100, 50, 20));
            g2d.fillRect((int)x - 4, (int)y + 38, 8, 15);

            if (swingProgress > 0) {
                g2d.setColor(new Color(255, 255, 200, (int)(100 * (1 - swingProgress))));
                g2d.setStroke(new BasicStroke(2));
                for (int i = 0; i < 5; i++) {
                    float offset = swingProgress * 30 * i;
                    if (isLeft) {
                        g2d.drawLine((int)(x - offset), (int)(y - offset), (int)(x - offset + 10), (int)(y - offset - 5));
                    } else {
                        g2d.drawLine((int)(x + offset), (int)(y - offset), (int)(x + offset - 10), (int)(y - offset - 5));
                    }
                }
            }
        }
    }

    private void drawBall(Graphics2D g2d) {
        float bx = gameCore.getBallX();
        float by = gameCore.getBallY();
        float spin = gameCore.getBallSpin();

        // 尝试使用羽毛球图片
        BufferedImage ballImage = imageManager.getImage("ball");
        if (ballImage != null) {
            g2d.drawImage(ballImage, (int)bx, (int)by, GameCore.BALL_SIZE, GameCore.BALL_SIZE, null);
        } else {
            // 默认绘制羽毛球
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.fillOval((int)bx + 2, (int)by + 4, GameCore.BALL_SIZE, GameCore.BALL_SIZE);

            double spinOffset = spin * 0.1;

            RadialGradientPaint ballGrad = new RadialGradientPaint(
                    bx + 5 + (float)spinOffset, by + 5, GameCore.BALL_SIZE,
                    new float[]{0f, 0.6f, 1f},
                    new Color[]{Color.WHITE, new Color(240, 240, 240), new Color(200, 200, 200)}
            );
            g2d.setPaint(ballGrad);
            g2d.fillOval((int)bx, (int)by, GameCore.BALL_SIZE, GameCore.BALL_SIZE);

            g2d.setColor(new Color(255, 255, 255, 180));
            g2d.fillOval((int)bx + 3, (int)by + 3, 5, 5);

            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.setStroke(new BasicStroke(1.5f));
            int centerX = (int)bx + GameCore.BALL_SIZE/2;
            int centerY = (int)by + GameCore.BALL_SIZE/2;

            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4 + spin * 0.05;
                int x2 = centerX + (int)(Math.cos(angle) * 12);
                int y2 = centerY + (int)(Math.sin(angle) * 12);
                g2d.drawLine(centerX, centerY, x2, y2);

                int x3 = x2 + (int)(Math.cos(angle + Math.PI/2) * 3);
                int y3 = y2 + (int)(Math.sin(angle + Math.PI/2) * 3);
                g2d.drawLine(x2, y2, x3, y3);
            }
        }
    }

    private void drawEffects(Graphics2D g2d) {
        for (GameCore.Particle p : gameCore.getParticles()) {
            float alpha = p.getAlpha();
            g2d.setColor(new Color(
                    p.color.getRed(), p.color.getGreen(), p.color.getBlue(),
                    (int)(180 * alpha)
            ));
            int size = (int)(6 * alpha);
            g2d.fillOval((int)p.x - size/2, (int)p.y - size/2, size, size);
        }

        for (GameCore.TrailEffect t : gameCore.getTrails()) {
            float alpha = t.getAlpha();
            g2d.setColor(new Color(255, 255, 255, (int)(100 * alpha)));
            int size = (int)(GameCore.BALL_SIZE * (0.5f + alpha * 0.5f));
            g2d.fillOval((int)t.x, (int)t.y, size, size);
        }

        for (GameCore.Sparkle s : gameCore.getSparkles()) {
            float alpha = s.getAlpha();
            g2d.setColor(new Color(
                    s.color.getRed(), s.color.getGreen(), s.color.getBlue(),
                    (int)(200 * alpha)
            ));
            int size = (int)(s.size * alpha);
            g2d.fillOval((int)s.x - size/2, (int)s.y - size/2, size, size);
        }

        for (GameCore.SmashWave w : gameCore.getSmashWaves()) {
            float alpha = w.getAlpha();
            g2d.setColor(new Color(255, 100, 0, (int)(100 * alpha)));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval((int)(w.x - w.radius), (int)(w.y - w.radius),
                    (int)(w.radius * 2), (int)(w.radius * 2));
        }
    }

    private void drawBulletTimeEffect(Graphics2D g2d) {
        g2d.setColor(new Color(255, 200, 0, 30));
        g2d.fillRect(0, 0, GameCore.WIDTH, GameCore.HEIGHT);

        int borderWidth = 20;
        GradientPaint edgeGlow = new GradientPaint(
                0, 0, new Color(255, 200, 0, 80),
                GameCore.WIDTH, 0, new Color(255, 200, 0, 0)
        );
        g2d.setPaint(edgeGlow);
        g2d.fillRect(0, 0, GameCore.WIDTH, borderWidth);
        g2d.fillRect(0, GameCore.HEIGHT - borderWidth, GameCore.WIDTH, borderWidth);
    }

    private void drawUI(Graphics2D g2d) {
        // 比分背景
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(140, 30, 140, 80, 15, 15);
        g2d.fillRoundRect(GameCore.WIDTH - 280, 30, 140, 80, 15, 15);

        // 比分
        g2d.setFont(fontManager.getBoldFont(56));
        g2d.setColor(new Color(70, 130, 200));
        g2d.drawString(String.valueOf(gameCore.getScore1()), 175, 85);
        g2d.setColor(new Color(200, 70, 70));
        g2d.drawString(String.valueOf(gameCore.getScore2()), GameCore.WIDTH - 245, 85);

        // 连击显示
        int combo = gameCore.getComboCount();
        if (combo > 1) {
            g2d.setFont(fontManager.getBoldFont(36));
            g2d.setColor(new Color(255, 200, 0));
            String comboText = combo + " 连击！";
            int comboWidth = g2d.getFontMetrics().stringWidth(comboText);
            g2d.drawString(comboText, GameCore.WIDTH/2 - comboWidth/2, 100);
        }

        // 击球类型
        String shotType = gameCore.getLastShotType();
        if (shotType != null && !shotType.isEmpty()) {
            String chineseShotType = getChineseShotType(shotType);
            g2d.setFont(fontManager.getItalicFont(18));
            g2d.setColor(new Color(255, 255, 255, 150));
            int shotWidth = g2d.getFontMetrics().stringWidth(chineseShotType);
            g2d.drawString(chineseShotType, GameCore.WIDTH/2 - shotWidth/2, 130);
        }

        // 能量条
        drawEnergyBar(g2d, 50, GameCore.HEIGHT - 100, gameCore.getPlayer1Energy(),
                new Color(70, 130, 200), "玩家1");
        drawEnergyBar(g2d, GameCore.WIDTH - 250, GameCore.HEIGHT - 100, gameCore.getPlayer2Energy(),
                new Color(200, 70, 70), gameCore.isAIMode() ? "AI" : "玩家2");

        // 发球方指示
        g2d.setFont(fontManager.getBoldFont(18));
        if (gameCore.getServer() == 1) {
            g2d.setColor(new Color(70, 130, 200));
            g2d.drawString("← 发球方", 260, 180);

            if (gameCore.isCharging1()) {
                float power = gameCore.getPower1();
                String chargeType = gameCore.isServeCharge1() ? "发球" :
                        (gameCore.isSmashCharge1() ? "扣杀" : "蓄力");
                Color barColor = gameCore.isSmashCharge1() ? new Color(255, 100, 0) : new Color(255, 200, 0);

                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRoundRect(200, 200, 220, 25, 10, 10);
                g2d.setColor(barColor);
                g2d.fillRoundRect(202, 202, (int)(216 * power), 21, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.setFont(fontManager.getBoldFont(14));
                g2d.drawString(chargeType + "蓄力中", 260, 220);
            }
        } else {
            g2d.setColor(new Color(200, 70, 70));
            g2d.drawString("发球方 →", GameCore.WIDTH - 350, 180);

            if (gameCore.isCharging2()) {
                float power = gameCore.getPower2();
                String chargeType = gameCore.isServeCharge2() ? "发球" :
                        (gameCore.isSmashCharge2() ? "扣杀" : "蓄力");
                Color barColor = gameCore.isSmashCharge2() ? new Color(255, 100, 0) : new Color(255, 200, 0);

                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRoundRect(GameCore.WIDTH - 420, 200, 220, 25, 10, 10);
                g2d.setColor(barColor);
                g2d.fillRoundRect(GameCore.WIDTH - 418, 202, (int)(216 * power), 21, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.setFont(fontManager.getBoldFont(14));
                g2d.drawString(chargeType + "蓄力中", GameCore.WIDTH - 360, 220);
            }
        }

        // 操作说明
        g2d.setFont(fontManager.getPlainFont(13));
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(20, GameCore.HEIGHT - 80, GameCore.WIDTH - 40, 70, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.drawString("玩家1: A/D移动 W跳跃 S闪避 J蓄力/挥拍 K取消", 35, GameCore.HEIGHT - 55);

        if (gameCore.isAIMode()) {
            g2d.drawString("AI对手: 智能移动/跳跃/蓄力/击球", 35, GameCore.HEIGHT - 35);
        } else {
            g2d.drawString("玩家2: ←/→移动 ↑跳跃 ↓闪避 1蓄力/挥拍 2取消", 35, GameCore.HEIGHT - 35);
        }

        g2d.setFont(fontManager.getPlainFont(13));
        g2d.drawString("R重开 | B碰撞箱 | ESC菜单", GameCore.WIDTH - 230, GameCore.HEIGHT - 55);

        // 模式指示
        String modeText = "";
        switch (gameMode) {
            case GameCore.MODE_VS_PLAYER:
                modeText = "双人对战";
                break;
            case GameCore.MODE_VS_AI:
                modeText = "人机对战";
                break;
        }
        g2d.setFont(fontManager.getBoldFont(14));
        g2d.setColor(new Color(255, 200, 100));
        g2d.drawString(modeText + " | 难度: " + getDifficultyName(), GameCore.WIDTH/2 - 80, GameCore.HEIGHT - 90);

        // AI状态指示
        if (gameCore.isAIMode()) {
            int aiState = gameCore.getAIState();
            String stateText = "";
            switch (aiState) {
                case 0: stateText = "回位"; break;
                case 1: stateText = "移动接球"; break;
                case 2: stateText = "准备击球"; break;
                case 3: stateText = "击球"; break;
            }
            g2d.setFont(fontManager.getItalicFont(12));
            g2d.setColor(new Color(255, 255, 255, 120));
            g2d.drawString("AI状态: " + stateText, GameCore.WIDTH - 120, 140);
        }

        // 发球提示
        if (!gameCore.isBallInPlay()) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRoundRect(GameCore.WIDTH/2 - 200, GameCore.HEIGHT/2 - 80, 400, 60, 20, 20);
            g2d.setFont(fontManager.getBoldFont(24));
            g2d.setColor(new Color(255, 200, 0));
            if (gameCore.getServer() == 1) {
                g2d.drawString("玩家1 按住 J 蓄力发球", GameCore.WIDTH/2 - 165, GameCore.HEIGHT/2 - 35);
            } else {
                if (gameCore.isAIMode()) {
                    g2d.drawString("AI 准备发球...", GameCore.WIDTH/2 - 130, GameCore.HEIGHT/2 - 35);
                } else {
                    g2d.drawString("玩家2 按住 1 蓄力发球", GameCore.WIDTH/2 - 165, GameCore.HEIGHT/2 - 35);
                }
            }
        }
    }

    private void drawEnergyBar(Graphics2D g2d, int x, int y, float energy, Color color, String label) {
        int width = 200;
        int height = 15;

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(x, y, width, height, 8, 8);

        g2d.setColor(color);
        g2d.fillRoundRect(x + 2, y + 2, (int)((width - 4) * energy / 100), height - 4, 6, 6);

        g2d.setColor(Color.WHITE);
        g2d.setFont(fontManager.getPlainFont(11));
        g2d.drawString(label + " " + (int)energy + "%", x + width/2 - 30, y + height + 12);
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.fillRect(0, 0, GameCore.WIDTH, GameCore.HEIGHT);

        String winner;
        Color winnerColor;
        if (gameCore.getScore1() >= GameCore.WIN_SCORE) {
            winner = gameCore.isAIMode() ? "玩家获胜！" : "蓝方获胜！";
            winnerColor = new Color(70, 130, 200);
        } else {
            winner = gameCore.isAIMode() ? "AI获胜！" : "红方获胜！";
            winnerColor = new Color(200, 70, 70);
        }

        g2d.setFont(fontManager.getBoldFont(80));
        g2d.setColor(Color.BLACK);
        g2d.drawString(winner, GameCore.WIDTH/2 - 148, GameCore.HEIGHT/2 - 48);
        g2d.setColor(winnerColor);
        g2d.drawString(winner, GameCore.WIDTH/2 - 150, GameCore.HEIGHT/2 - 50);

        g2d.setFont(fontManager.getPlainFont(24));
        g2d.setColor(Color.WHITE);
        g2d.drawString("最大连击: " + gameCore.getMaxCombo(), GameCore.WIDTH/2 - 80, GameCore.HEIGHT/2 + 20);

        g2d.setFont(fontManager.getPlainFont(28));
        g2d.drawString("按 R 重新开始 | ESC 返回菜单",
                GameCore.WIDTH/2 - 240, GameCore.HEIGHT/2 + 80);
    }

    private void updateGame() {
        if (aPressed) gameCore.movePlayer1(false);
        if (dPressed) gameCore.movePlayer1(true);
        if (wPressed) gameCore.jump(true);

        if (leftPressed && !gameCore.isAIMode()) gameCore.movePlayer2(false);
        if (rightPressed && !gameCore.isAIMode()) gameCore.movePlayer2(true);
        if (upPressed && !gameCore.isAIMode()) gameCore.jump(false);

        gameCore.update();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_A) aPressed = true;
        if (key == KeyEvent.VK_D) dPressed = true;
        if (key == KeyEvent.VK_W) wPressed = true;
        if (key == KeyEvent.VK_S) {
            if (!sPressed) gameCore.useDash(true);
            sPressed = true;
        }
        if (key == KeyEvent.VK_J) {
            if (!jPressed) gameCore.startCharging(true);
            jPressed = true;
        }
        if (key == KeyEvent.VK_K) {
            gameCore.cancelCharge(true);
        }

        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
        if (key == KeyEvent.VK_UP) upPressed = true;
        if (key == KeyEvent.VK_DOWN) {
            if (!downPressed && !gameCore.isAIMode()) gameCore.useDash(false);
            downPressed = true;
        }
        if (key == KeyEvent.VK_1 || key == KeyEvent.VK_NUMPAD1) {
            if (!num1Pressed && !gameCore.isAIMode()) gameCore.startCharging(false);
            num1Pressed = true;
        }
        if (key == KeyEvent.VK_2 || key == KeyEvent.VK_NUMPAD2) {
            if (!gameCore.isAIMode()) gameCore.cancelCharge(false);
        }

        if (key == KeyEvent.VK_R) {
            gameCore.resetGame();
            gameCore.setGameMode(gameMode, difficulty);
        }
        if (key == KeyEvent.VK_B) showDebugBounds = !showDebugBounds;
        if (key == KeyEvent.VK_ESCAPE) gameWindow.showMenu();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_A) aPressed = false;
        if (key == KeyEvent.VK_D) dPressed = false;
        if (key == KeyEvent.VK_W) wPressed = false;
        if (key == KeyEvent.VK_S) sPressed = false;
        if (key == KeyEvent.VK_J) {
            gameCore.releaseCharge(true);
            jPressed = false;
        }

        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
        if (key == KeyEvent.VK_UP) upPressed = false;
        if (key == KeyEvent.VK_DOWN) downPressed = false;
        if (key == KeyEvent.VK_1 || key == KeyEvent.VK_NUMPAD1) {
            if (!gameCore.isAIMode()) gameCore.releaseCharge(false);
            num1Pressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerFrame = 1000000000.0 / 60.0;
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;

            while (delta >= 1) {
                updateGame();
                delta--;
            }

            repaint();

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}