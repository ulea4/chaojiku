package yumaoqou.ui;

import yumaoqou.core.GameCore;
import yumaoqou.utils.GameConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements KeyListener, Runnable {
    private GameWindow gameWindow;
    private GameCore gameCore;

    private boolean aPressed, dPressed, wPressed;
    private boolean jPressed = false;
    private boolean leftPressed, rightPressed, upPressed;
    private boolean num1Pressed = false;

    private Thread movementThread;
    private volatile boolean running = true;

    public GamePanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.gameCore = new GameCore();

        setPreferredSize(new Dimension(GameConstants.WIDTH, GameConstants.HEIGHT));
        setFocusable(true);
        setDoubleBuffered(true);

        addKeyListener(this);

        new Thread(this).start();
        startMovementThread();
    }

    private void startMovementThread() {
        movementThread = new Thread(() -> {
            while (running) {
                if (aPressed) gameCore.movePlayer1(false);
                if (dPressed) gameCore.movePlayer1(true);
                if (wPressed) gameCore.jump(true);

                if (leftPressed) gameCore.movePlayer2(false);
                if (rightPressed) gameCore.movePlayer2(true);
                if (upPressed) gameCore.jump(false);

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        movementThread.start();
    }

    public void startNewGame() {
        gameCore.resetGame();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawBackground(g2d);
        drawCourt(g2d);
        drawNet(g2d);
        drawBoundaryLines(g2d);
        drawStickMen(g2d);
        drawBall(g2d);
        drawEffects(g2d);
        drawUI(g2d);

        if (!gameCore.isGameRunning()) {
            drawGameOver(g2d);
        }
    }

    private void drawBackground(Graphics2D g2d) {
        GradientPaint skyGradient = new GradientPaint(
                0, 0, GameConstants.SKY_TOP,
                0, GameConstants.HEIGHT, GameConstants.SKY_BOTTOM
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        g2d.setColor(GameConstants.GROUND_COLOR);
        g2d.fillRect(0, GameConstants.GROUND_Y - 20, GameConstants.WIDTH, 100);

        g2d.setColor(new Color(0, 100, 0));
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i < GameConstants.WIDTH; i += 40) {
            g2d.drawLine(i, GameConstants.GROUND_Y - 20, i + 20, GameConstants.GROUND_Y - 10);
        }
    }

    private void drawCourt(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{15, 15}, 0));
        g2d.drawLine(GameConstants.NET_X, GameConstants.GROUND_Y - 150,
                GameConstants.NET_X, GameConstants.GROUND_Y - 30);

        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(80, GameConstants.GROUND_Y - 150, 300, 120);
        g2d.drawRect(GameConstants.WIDTH - 380, GameConstants.GROUND_Y - 150, 300, 120);
    }

    private void drawNet(Graphics2D g2d) {
        g2d.setColor(GameConstants.NET_COLOR);
        g2d.fillRect(GameConstants.NET_X - 8, GameConstants.GROUND_Y - 150, 16, 130);

        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.setStroke(new BasicStroke(2));

        for (int y = GameConstants.GROUND_Y - 140; y < GameConstants.GROUND_Y - 30; y += 15) {
            g2d.drawLine(GameConstants.NET_X - 35, y, GameConstants.NET_X + 35, y);
        }

        g2d.setColor(new Color(255, 0, 0));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(GameConstants.NET_X - 35, GameConstants.NET_TOP_Y,
                GameConstants.NET_X + 35, GameConstants.NET_TOP_Y);

        g2d.setColor(new Color(255, 0, 0, 30));
        g2d.fillRect(GameConstants.NET_LEFT, GameConstants.NET_BOTTOM_Y,
                GameConstants.NET_WIDTH, GameConstants.NET_TOP_Y - GameConstants.NET_BOTTOM_Y);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("网", GameConstants.NET_X + 40, GameConstants.NET_TOP_Y - 5);
    }

    private void drawBoundaryLines(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 0, 100));
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{10, 10}, 0));
        g2d.drawLine(GameConstants.PLAYER1_MAX_X, GameConstants.GROUND_Y - 200,
                GameConstants.PLAYER1_MAX_X, GameConstants.GROUND_Y);
        g2d.drawLine(GameConstants.PLAYER2_MIN_X, GameConstants.GROUND_Y - 200,
                GameConstants.PLAYER2_MIN_X, GameConstants.GROUND_Y);

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.YELLOW);
        g2d.drawString("左边界", GameConstants.PLAYER1_MAX_X - 70, GameConstants.GROUND_Y - 180);
        g2d.drawString("右边界", GameConstants.PLAYER2_MIN_X + 20, GameConstants.GROUND_Y - 180);
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
        g2d.setStroke(new BasicStroke(4));

        if (s.isLeft) {
            g2d.setColor(GameConstants.PLAYER1_COLOR);
        } else {
            g2d.setColor(GameConstants.PLAYER2_COLOR);
        }

        float headY = s.y - 35;
        float bodyY = s.y;

        if (s.isJumping) {
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval((int)s.x - 15, (int)GameConstants.GROUND_Y - 15, 30, 10);
        }

        g2d.setColor(s.isLeft ? GameConstants.PLAYER1_COLOR : GameConstants.PLAYER2_COLOR);
        g2d.fillOval((int)s.x - 12, (int)headY - 12, 24, 24);

        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)s.x - 7, (int)headY - 7, 6, 6);
        g2d.fillOval((int)s.x + 2, (int)headY - 7, 6, 6);
        g2d.setColor(Color.BLACK);
        g2d.fillOval((int)s.x - 6, (int)headY - 6, 4, 4);
        g2d.fillOval((int)s.x + 3, (int)headY - 6, 4, 4);

        if (s.hasHit) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("X", (int)s.x - 5, (int)headY - 20);
        }

        g2d.setColor(s.isLeft ? GameConstants.PLAYER1_COLOR : GameConstants.PLAYER2_COLOR);
        g2d.drawLine((int)s.x, (int)headY + 8, (int)s.x, (int)bodyY);

        drawArmsAndRacket(g2d, s);

        g2d.drawLine((int)s.x, (int)bodyY, (int)s.x - 12, (int)bodyY + 25);
        g2d.drawLine((int)s.x, (int)bodyY, (int)s.x + 12, (int)bodyY + 25);

        if (s.isCharging) {
            float power = s.isLeft ? gameCore.getPower1() : gameCore.getPower2();
            int glowSize = 20 + (int)(power * 30);
            g2d.setColor(new Color(255, 200, 0, 100));
            g2d.fillOval((int)s.x - glowSize/2, (int)headY - glowSize/2 - 10, glowSize, glowSize);

            g2d.setColor(GameConstants.CHARGE_COLOR);
            g2d.fillRect((int)s.x - 30, (int)headY - 50, (int)(60 * power), 8);
            g2d.setColor(Color.BLACK);
            g2d.drawRect((int)s.x - 30, (int)headY - 50, 60, 8);
        }
    }

    private void drawArmsAndRacket(Graphics2D g2d, GameCore.StickMan s) {
        float headY = s.y - 35;
        float shoulderY = headY + 15;

        if (s.isLeft) {
            if (s.isSwinging) {
                float swingX = s.x + 30 + 20 * s.swingProgress;
                float swingY = shoulderY - 10 - 10 * s.swingProgress;
                g2d.drawLine((int)s.x, (int)shoulderY, (int)swingX, (int)swingY);
                drawBadmintonRacket(g2d, swingX, swingY, true);
            } else {
                g2d.drawLine((int)s.x, (int)shoulderY, (int)s.x + 30, (int)shoulderY - 10);
                drawBadmintonRacket(g2d, s.x + 30, shoulderY - 10, true);
            }
            g2d.drawLine((int)s.x, (int)shoulderY, (int)s.x - 20, (int)shoulderY);
        } else {
            if (s.isSwinging) {
                float swingX = s.x - 30 - 20 * s.swingProgress;
                float swingY = shoulderY - 10 - 10 * s.swingProgress;
                g2d.drawLine((int)s.x, (int)shoulderY, (int)swingX, (int)swingY);
                drawBadmintonRacket(g2d, swingX, swingY, false);
            } else {
                g2d.drawLine((int)s.x, (int)shoulderY, (int)s.x - 30, (int)shoulderY - 10);
                drawBadmintonRacket(g2d, s.x - 30, shoulderY - 10, false);
            }
            g2d.drawLine((int)s.x, (int)shoulderY, (int)s.x + 20, (int)shoulderY);
        }
    }

    private void drawBadmintonRacket(Graphics2D g2d, float x, float y, boolean isLeft) {
        g2d.setColor(new Color(160, 82, 45));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval((int)x - 12, (int)y - 18, 24, 30);

        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.setStroke(new BasicStroke(1));

        for (int i = 0; i < 3; i++) {
            int yOffset = (int)y - 12 + i * 8;
            g2d.drawLine((int)x - 8, yOffset, (int)x + 8, yOffset);
        }

        for (int i = 0; i < 2; i++) {
            int xOffset = (int)x - 4 + i * 8;
            g2d.drawLine(xOffset, (int)y - 14, xOffset, (int)y + 8);
        }

        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect((int)x - 3, (int)y + 12, 6, 25);
    }

    private void drawBall(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)gameCore.getBallX(), (int)gameCore.getBallY(),
                GameConstants.BALL_SIZE, GameConstants.BALL_SIZE);

        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.setStroke(new BasicStroke(1));

        int centerX = (int)gameCore.getBallX() + GameConstants.BALL_SIZE/2;
        int centerY = (int)gameCore.getBallY() + GameConstants.BALL_SIZE/2;

        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3;
            int x2 = centerX + (int)(Math.cos(angle) * 10);
            int y2 = centerY + (int)(Math.sin(angle) * 10);
            g2d.drawLine(centerX, centerY, x2, y2);
        }
    }

    private void drawEffects(Graphics2D g2d) {
        for (GameCore.Particle p : gameCore.getParticles()) {
            float alpha = (float)p.life / p.maxLife;
            g2d.setColor(new Color(
                    p.color.getRed(), p.color.getGreen(), p.color.getBlue(),
                    (int)(150 * alpha)
            ));
            int size = (int)(5 * alpha);
            g2d.fillOval((int)p.x - size/2, (int)p.y - size/2, size, size);
        }

        for (GameCore.TrailEffect t : gameCore.getTrails()) {
            float alpha = (float)t.life / 12;
            g2d.setColor(new Color(255, 255, 255, (int)(60 * alpha)));
            int size = (int)(GameConstants.BALL_SIZE * (0.6f + alpha * 0.4f));
            g2d.fillOval((int)t.x, (int)t.y, size, size);
        }
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 60));

        g2d.setColor(GameConstants.PLAYER1_COLOR);
        g2d.drawString(String.valueOf(gameCore.getScore1()), 200, 80);

        g2d.setColor(GameConstants.PLAYER2_COLOR);
        g2d.drawString(String.valueOf(gameCore.getScore2()), GameConstants.WIDTH - 250, 80);

        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.setColor(GameConstants.PLAYER1_COLOR);
        g2d.drawString("击球: " + gameCore.getHitCount1(), 200, 120);
        g2d.setColor(GameConstants.PLAYER2_COLOR);
        g2d.drawString("击球: " + gameCore.getHitCount2(), GameConstants.WIDTH - 250, 120);

        if (gameCore.hasHit1()) {
            g2d.setColor(new Color(70, 130, 200, 100));
            g2d.fillRect(150, 140, 100, 30);
            g2d.setColor(Color.WHITE);
            g2d.drawString("已击球", 160, 165);
        }
        if (gameCore.hasHit2()) {
            g2d.setColor(new Color(200, 70, 70, 100));
            g2d.fillRect(GameConstants.WIDTH - 250, 140, 100, 30);
            g2d.setColor(Color.WHITE);
            g2d.drawString("已击球", GameConstants.WIDTH - 240, 165);
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        if (gameCore.getServer() == 1) {
            g2d.setColor(GameConstants.PLAYER1_COLOR);
            g2d.drawString("发球方", 250, 200);
        } else {
            g2d.setColor(GameConstants.PLAYER2_COLOR);
            g2d.drawString("发球方", GameConstants.WIDTH - 320, 200);
        }

        // 蓄力条显示
        if (gameCore.isCharging1()) {
            float power = gameCore.getPower1();
            g2d.setColor(GameConstants.CHARGE_COLOR);
            g2d.fillRect(200, 220, (int)(200 * power), 20);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(200, 220, 200, 20);
            g2d.drawString("蓄力: " + (int)(power * 100) + "%", 420, 240);
        }

        if (gameCore.isCharging2()) {
            float power = gameCore.getPower2();
            g2d.setColor(GameConstants.CHARGE_COLOR);
            g2d.fillRect(GameConstants.WIDTH - 400, 220, (int)(200 * power), 20);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(GameConstants.WIDTH - 400, 220, 200, 20);
            g2d.drawString("蓄力: " + (int)(power * 100) + "%", GameConstants.WIDTH - 180, 240);
        }

        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(Color.BLACK);
        g2d.drawString("玩家1: A(左) D(右) W(跳) J(单击蓄力/击球) | 玩家2: ← → ↑(跳) 1(单击蓄力/击球) | R:重开 | ESC:菜单",
                50, GameConstants.HEIGHT - 40);

        // 发球提示
        if (!gameCore.isBallInPlay()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            g2d.setColor(GameConstants.CHARGE_COLOR);
            if (gameCore.getServer() == 1) {
                g2d.drawString("玩家1按J蓄力发球", GameConstants.WIDTH/2 - 150, 280);
                if (gameCore.isCharging1()) {
                    float power = gameCore.getPower1();
                    g2d.fillRect(GameConstants.WIDTH/2 - 100, 320, (int)(200 * power), 20);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(GameConstants.WIDTH/2 - 100, 320, 200, 20);
                }
            } else {
                g2d.drawString("玩家2按1蓄力发球", GameConstants.WIDTH/2 - 150, 280);
                if (gameCore.isCharging2()) {
                    float power = gameCore.getPower2();
                    g2d.fillRect(GameConstants.WIDTH/2 - 100, 320, (int)(200 * power), 20);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(GameConstants.WIDTH/2 - 100, 320, 200, 20);
                }
            }
        }
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);

        String winner;
        Color winnerColor;
        if (gameCore.getScore1() >= GameConstants.WIN_SCORE) {
            winner = "蓝方获胜！";
            winnerColor = GameConstants.PLAYER1_COLOR;
        } else {
            winner = "红方获胜！";
            winnerColor = GameConstants.PLAYER2_COLOR;
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 72));
        g2d.setColor(winnerColor);
        g2d.drawString(winner, GameConstants.WIDTH/2 - 200, GameConstants.HEIGHT/2 - 50);

        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        g2d.setColor(Color.WHITE);
        g2d.drawString("按 R 重新开始 | ESC 返回菜单",
                GameConstants.WIDTH/2 - 180, GameConstants.HEIGHT/2 + 30);
    }

    private void updateGame() {
        gameCore.update();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_A) aPressed = true;
        if (key == KeyEvent.VK_D) dPressed = true;
        if (key == KeyEvent.VK_W) wPressed = true;

        if (key == KeyEvent.VK_J) {
            if (!jPressed) {
                if (!gameCore.isCharging1()) {
                    gameCore.startCharging(true);
                } else {
                    gameCore.releaseCharge(true);
                }
            }
            jPressed = true;
        }

        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
        if (key == KeyEvent.VK_UP) upPressed = true;

        if (key == KeyEvent.VK_1 || key == KeyEvent.VK_NUMPAD1) {
            if (!num1Pressed) {
                if (!gameCore.isCharging2()) {
                    gameCore.startCharging(false);
                } else {
                    gameCore.releaseCharge(false);
                }
            }
            num1Pressed = true;
        }

        if (key == KeyEvent.VK_R) gameCore.resetGame();
        if (key == KeyEvent.VK_ESCAPE) gameWindow.showMenu();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_A) aPressed = false;
        if (key == KeyEvent.VK_D) dPressed = false;
        if (key == KeyEvent.VK_W) wPressed = false;
        if (key == KeyEvent.VK_J) jPressed = false;

        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
        if (key == KeyEvent.VK_UP) upPressed = false;
        if (key == KeyEvent.VK_1 || key == KeyEvent.VK_NUMPAD1) num1Pressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerFrame = 1000000000.0 / 60.0;
        double delta = 0;

        while (running) {
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
                break;
            }
        }
    }

    public void stop() {
        running = false;
        if (movementThread != null) {
            movementThread.interrupt();
        }
    }
}