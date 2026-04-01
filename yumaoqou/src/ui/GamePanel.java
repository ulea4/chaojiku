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
                try { Thread.sleep(16); } catch (InterruptedException e) { break; }
            }
        });
        movementThread.start();
    }

    public void startNewGame() { gameCore.resetGame(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2d);
        drawCourt(g2d);
        drawNet(g2d);
        drawBoundaryLines(g2d);
        drawStickMen(g2d);
        drawBall(g2d);
        drawEffects(g2d);
        drawUI(g2d);
        if (!gameCore.isGameRunning()) drawGameOver(g2d);
    }

    private void drawBackground(Graphics2D g2d) {
        GradientPaint sky = new GradientPaint(0, 0, GameConstants.SKY_TOP, 0, GameConstants.HEIGHT, GameConstants.SKY_BOTTOM);
        g2d.setPaint(sky);
        g2d.fillRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        g2d.setColor(GameConstants.GROUND_COLOR);
        g2d.fillRect(0, GameConstants.GROUND_Y - 20, GameConstants.WIDTH, 100);
        g2d.setColor(new Color(0, 100, 0));
        for (int i = 0; i < GameConstants.WIDTH; i += 40)
            g2d.drawLine(i, GameConstants.GROUND_Y - 20, i + 20, GameConstants.GROUND_Y - 10);
    }

    private void drawCourt(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{15, 15}, 0));
        g2d.drawLine(GameConstants.NET_X, GameConstants.GROUND_Y - 150, GameConstants.NET_X, GameConstants.GROUND_Y - 30);
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.drawRect(80, GameConstants.GROUND_Y - 150, 300, 120);
        g2d.drawRect(GameConstants.WIDTH - 380, GameConstants.GROUND_Y - 150, 300, 120);
    }

    private void drawNet(Graphics2D g2d) {
        g2d.setColor(GameConstants.NET_COLOR);
        g2d.fillRect(GameConstants.NET_X - 8, GameConstants.GROUND_Y - 150, 16, 130);
        g2d.setColor(new Color(255, 255, 255, 180));
        for (int y = GameConstants.GROUND_Y - 140; y < GameConstants.GROUND_Y - 30; y += 15)
            g2d.drawLine(GameConstants.NET_X - 35, y, GameConstants.NET_X + 35, y);
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(GameConstants.NET_X - 35, GameConstants.NET_TOP_Y, GameConstants.NET_X + 35, GameConstants.NET_TOP_Y);
    }

    private void drawBoundaryLines(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 0, 100));
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10, 10}, 0));
        g2d.drawLine(GameConstants.PLAYER1_MAX_X, GameConstants.GROUND_Y - 200, GameConstants.PLAYER1_MAX_X, GameConstants.GROUND_Y);
        g2d.drawLine(GameConstants.PLAYER2_MIN_X, GameConstants.GROUND_Y - 200, GameConstants.PLAYER2_MIN_X, GameConstants.GROUND_Y);
        g2d.setColor(Color.YELLOW);
        g2d.drawString("左边界", GameConstants.PLAYER1_MAX_X - 70, GameConstants.GROUND_Y - 180);
        g2d.drawString("右边界", GameConstants.PLAYER2_MIN_X + 20, GameConstants.GROUND_Y - 180);
    }

    private void drawStickMen(Graphics2D g2d) {
        for (GameCore.StickMan s : gameCore.getStickMen()) drawStickMan(g2d, s);
    }

    private void drawStickMan(Graphics2D g2d, GameCore.StickMan s) {
        g2d.setStroke(new BasicStroke(4));
        g2d.setColor(s.isLeft ? GameConstants.PLAYER1_COLOR : GameConstants.PLAYER2_COLOR);
        float headY = s.y - 35;

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

        if (!s.canHit) {
            g2d.setColor(Color.RED);
            g2d.drawString("X", (int)s.x - 5, (int)headY - 20);
        }

        g2d.drawLine((int)s.x, (int)headY + 8, (int)s.x, (int)s.y);
        drawArmsAndRacket(g2d, s);
        g2d.drawLine((int)s.x, (int)s.y, (int)s.x - 12, (int)s.y + 25);
        g2d.drawLine((int)s.x, (int)s.y, (int)s.x + 12, (int)s.y + 25);

        if (s.isCharging) {
            float power = s.isLeft ? gameCore.getPower1() : gameCore.getPower2();
            g2d.setColor(new Color(255, 200, 0, 100));
            g2d.fillOval((int)s.x - 25, (int)headY - 45, 50 + (int)(power * 30), 20);
            g2d.setColor(GameConstants.CHARGE_COLOR);
            g2d.fillRect((int)s.x - 30, (int)headY - 50, (int)(60 * power), 8);
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
                drawRacket(g2d, swingX, swingY);
            } else {
                g2d.drawLine((int)s.x, (int)shoulderY, (int)s.x + 30, (int)shoulderY - 10);
                drawRacket(g2d, s.x + 30, shoulderY - 10);
            }
            g2d.drawLine((int)s.x, (int)shoulderY, (int)s.x - 20, (int)shoulderY);
        } else {
            if (s.isSwinging) {
                float swingX = s.x - 30 - 20 * s.swingProgress;
                float swingY = shoulderY - 10 - 10 * s.swingProgress;
                g2d.drawLine((int)s.x, (int)shoulderY, (int)swingX, (int)swingY);
                drawRacket(g2d, swingX, swingY);
            } else {
                g2d.drawLine((int)s.x, (int)shoulderY, (int)s.x - 30, (int)shoulderY - 10);
                drawRacket(g2d, s.x - 30, shoulderY - 10);
            }
            g2d.drawLine((int)s.x, (int)shoulderY, (int)s.x + 20, (int)shoulderY);
        }
    }

    private void drawRacket(Graphics2D g2d, float x, float y) {
        g2d.setColor(new Color(160, 82, 45));
        g2d.drawOval((int)x - 12, (int)y - 18, 24, 30);
        g2d.setColor(new Color(255, 255, 255, 180));
        for (int i = 0; i < 3; i++) g2d.drawLine((int)x - 8, (int)y - 12 + i * 8, (int)x + 8, (int)y - 12 + i * 8);
        for (int i = 0; i < 2; i++) g2d.drawLine((int)x - 4 + i * 8, (int)y - 14, (int)x - 4 + i * 8, (int)y + 8);
        g2d.fillRect((int)x - 3, (int)y + 12, 6, 25);
    }

    private void drawBall(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)gameCore.getBallX(), (int)gameCore.getBallY(), GameConstants.BALL_SIZE, GameConstants.BALL_SIZE);
        int cx = (int)gameCore.getBallX() + GameConstants.BALL_SIZE/2;
        int cy = (int)gameCore.getBallY() + GameConstants.BALL_SIZE/2;
        for (int i = 0; i < 6; i++) {
            double a = i * Math.PI / 3;
            g2d.drawLine(cx, cy, cx + (int)(Math.cos(a) * 10), cy + (int)(Math.sin(a) * 10));
        }
    }

    private void drawEffects(Graphics2D g2d) {
        for (GameCore.Particle p : gameCore.getParticles()) {
            float alpha = (float)p.life / p.maxLife;
            g2d.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), (int)(150 * alpha)));
            g2d.fillOval((int)p.x - 2, (int)p.y - 2, 4, 4);
        }
        for (GameCore.TrailEffect t : gameCore.getTrails()) {
            float alpha = (float)t.life / 12;
            g2d.setColor(new Color(255, 255, 255, (int)(60 * alpha)));
            int size = (int)(GameConstants.BALL_SIZE * (0.6f + alpha * 0.4f));
            g2d.fillOval((int)t.x, (int)t.y, size, size);
        }
    }

    private void drawUI(Graphics2D g2d) {
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.setColor(Color.GREEN);
            if (gameCore.canHit1()) {
                g2d.drawString("✓ 可以击球", 200, 160);
            } else if (gameCore.justHit1()) {
                g2d.setColor(Color.RED);
                g2d.drawString("已击球", 200, 160);
            }

            if (gameCore.canHit2()) {
                g2d.setColor(Color.GREEN);
                g2d.drawString("✓ 可以击球", GameConstants.WIDTH - 250, 160);
            } else if (gameCore.justHit2()) {
                g2d.setColor(Color.RED);
                g2d.drawString("已击球", GameConstants.WIDTH - 250, 160);
            }
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

        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.RED);
        if (gameCore.canHit1()) g2d.drawString("可以击球", 200, 160);
        if (gameCore.canHit2()) g2d.drawString("可以击球", GameConstants.WIDTH - 250, 160);

        if (gameCore.isCharging1()) {
            float power = gameCore.getPower1();
            g2d.setColor(GameConstants.CHARGE_COLOR);
            g2d.fillRect(200, 200, (int)(200 * power), 20);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(200, 200, 200, 20);
            g2d.drawString("蓄力: " + (int)(power * 100) + "%", 420, 220);
        }
        if (gameCore.isCharging2()) {
            float power = gameCore.getPower2();
            g2d.setColor(GameConstants.CHARGE_COLOR);
            g2d.fillRect(GameConstants.WIDTH - 400, 200, (int)(200 * power), 20);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(GameConstants.WIDTH - 400, 200, 200, 20);
            g2d.drawString("蓄力: " + (int)(power * 100) + "%", GameConstants.WIDTH - 180, 220);
        }

        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(Color.BLACK);
        g2d.drawString("玩家1: A(左) D(右) W(跳) J(蓄力/击球) | 玩家2: ← → ↑(跳) 1(蓄力/击球) | R:重开 | ESC:菜单", 50, GameConstants.HEIGHT - 40);

        if (!gameCore.isBallInPlay()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 28));
            g2d.setColor(GameConstants.CHARGE_COLOR);
            if (gameCore.getServer() == 1) g2d.drawString("玩家1按J蓄力发球", GameConstants.WIDTH/2 - 150, 280);
            else g2d.drawString("玩家2按1蓄力发球", GameConstants.WIDTH/2 - 150, 280);
        } else {
            if (gameCore.isCharging1()) g2d.drawString("蓄力中... 按J击球", 200, 280);
            if (gameCore.isCharging2()) g2d.drawString("蓄力中... 按1击球", GameConstants.WIDTH - 300, 280);
        }
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, GameConstants.WIDTH, GameConstants.HEIGHT);
        String winner = gameCore.getScore1() >= GameConstants.WIN_SCORE ? "蓝方获胜！" : "红方获胜！";
        g2d.setFont(new Font("Arial", Font.BOLD, 72));
        g2d.setColor(gameCore.getScore1() >= GameConstants.WIN_SCORE ? GameConstants.PLAYER1_COLOR : GameConstants.PLAYER2_COLOR);
        g2d.drawString(winner, GameConstants.WIDTH/2 - 150, GameConstants.HEIGHT/2 - 50);
        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        g2d.setColor(Color.WHITE);
        g2d.drawString("按 R 重新开始 | ESC 返回菜单", GameConstants.WIDTH/2 - 180, GameConstants.HEIGHT/2 + 50);
    }

    private void updateGame() { gameCore.update(); }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A) aPressed = true;
        if (key == KeyEvent.VK_D) dPressed = true;
        if (key == KeyEvent.VK_W) wPressed = true;
        if (key == KeyEvent.VK_J && !jPressed) { gameCore.startCharging(true); jPressed = true; }
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
        if (key == KeyEvent.VK_UP) upPressed = true;
        if ((key == KeyEvent.VK_1 || key == KeyEvent.VK_NUMPAD1) && !num1Pressed) { gameCore.startCharging(false); num1Pressed = true; }
        if (key == KeyEvent.VK_R) gameCore.resetGame();
        if (key == KeyEvent.VK_ESCAPE) gameWindow.showMenu();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A) aPressed = false;
        if (key == KeyEvent.VK_D) dPressed = false;
        if (key == KeyEvent.VK_W) wPressed = false;
        if (key == KeyEvent.VK_J) { gameCore.releaseCharge(true); jPressed = false; }
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
        if (key == KeyEvent.VK_UP) upPressed = false;
        if (key == KeyEvent.VK_1 || key == KeyEvent.VK_NUMPAD1) { gameCore.releaseCharge(false); num1Pressed = false; }
    }

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerFrame = 1000000000.0 / 60.0;
        double delta = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;
            while (delta >= 1) { updateGame(); delta--; }
            repaint();
            try { Thread.sleep(1); } catch (InterruptedException e) { break; }
        }
    }

    public void stop() { running = false; if (movementThread != null) movementThread.interrupt(); }
}