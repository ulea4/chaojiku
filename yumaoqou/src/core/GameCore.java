package yumaoqou.core;

import yumaoqou.utils.GameConstants;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class GameCore {
    private float player1X, player1Y;
    private float player2X, player2Y;
    private float player1Vy, player2Vy;
    private boolean player1Jumping, player2Jumping;
    private float ballX, ballY;
    private float ballSpeedX, ballSpeedY;
    private int score1, score2;
    private boolean gameRunning, ballInPlay;
    private int server;

    private boolean charging1, charging2;
    private long chargeStart1, chargeStart2;
    private boolean canHit1, canHit2;

    private CopyOnWriteArrayList<Particle> particles;
    private CopyOnWriteArrayList<TrailEffect> trails;
    private ArrayList<StickMan> stickMen;
    private Random random;
    private long lastSwing1, lastSwing2;
    private int hitCount1, hitCount2;

    private final ReentrantLock lock = new ReentrantLock();

    public GameCore() {
        random = new Random();
        particles = new CopyOnWriteArrayList<>();
        trails = new CopyOnWriteArrayList<>();
        stickMen = new ArrayList<>();
        resetGame();
    }

    public void resetGame() {
        lock.lock();
        try {
            player1X = 150;
            player1Y = GameConstants.GROUND_Y - 50;
            player2X = GameConstants.WIDTH - 150;
            player2Y = GameConstants.GROUND_Y - 50;
            player1Vy = player2Vy = 0;
            player1Jumping = player2Jumping = false;

            score1 = score2 = 0;
            gameRunning = true;
            ballInPlay = false;
            server = 1;

            canHit1 = canHit2 = true;
            charging1 = charging2 = false;
            lastSwing1 = lastSwing2 = 0;
            hitCount1 = hitCount2 = 0;

            ballX = player1X + 30;
            ballY = player1Y - 20;
            ballSpeedX = ballSpeedY = 0;

            particles.clear();
            trails.clear();
            stickMen.clear();
            stickMen.add(new StickMan(player1X, player1Y, true));
            stickMen.add(new StickMan(player2X, player2Y, false));
        } finally {
            lock.unlock();
        }
    }

    // 按住蓄力
    public void startCharging(boolean isP1) {
        if (!gameRunning || !ballInPlay) return;
        if (isP1) {
            if (!charging1 && canHit1) {
                charging1 = true;
                chargeStart1 = System.currentTimeMillis();
            }
        } else {
            if (!charging2 && canHit2) {
                charging2 = true;
                chargeStart2 = System.currentTimeMillis();
            }
        }
    }

    // 松开击球 —— 只有球碰到拍才能打
    public void releaseCharge(boolean isP1) {
        if (!gameRunning || !ballInPlay) return;

        if (isP1) {
            if (!charging1 || !canHit1) return;
            charging1 = false;

            // ========== 真实碰撞：球必须在球拍范围内 ==========
            if (isBallTouchingRacket(true)) {
                float power = getChargePower(1);
                hitRacket(true, power);
                canHit1 = false;
                canHit2 = true;
                lastSwing1 = System.currentTimeMillis();
                hitCount1++;
            }
        } else {
            if (!charging2 || !canHit2) return;
            charging2 = false;

            if (isBallTouchingRacket(false)) {
                float power = getChargePower(2);
                hitRacket(false, power);
                canHit2 = false;
                canHit1 = true;
                lastSwing2 = System.currentTimeMillis();
                hitCount2++;
            }
        }
    }

    // ========== 核心：球是否碰到球拍 ==========
    private boolean isBallTouchingRacket(boolean isP1) {
        float px = isP1 ? player1X : player2X;
        float py = isP1 ? player1Y : player2Y;

        float rx = isP1 ? px + 30 : px - 30;  // 球拍位置
        float ry = py - 20;

        float dx = ballX - rx;
        float dy = ballY - ry;

        // 球拍有效范围：横向60，纵向40
        return Math.abs(dx) < 60 && Math.abs(dy) < 40;
    }

    // 蓄力力度
    private float getChargePower(int p) {
        long dt = p == 1 ? System.currentTimeMillis() - chargeStart1
                : System.currentTimeMillis() - chargeStart2;
        return 3.0f + Math.min(1.0f, dt / 1000f) * 7.0f;
    }

    // 用球拍击球反弹
    private void hitRacket(boolean isP1, float power) {
        if (isP1) {
            ballSpeedX = power * 0.9f;
            ballSpeedY = -power * 0.55f;
        } else {
            ballSpeedX = -power * 0.9f;
            ballSpeedY = -power * 0.55f;
        }
        addExplosion(ballX, ballY, Color.ORANGE, 6);
    }

    // 发球
    public void serve(boolean isP1) {
        if (ballInPlay) return;
        ballInPlay = true;
        if (isP1) {
            ballSpeedX = 6f;
            ballSpeedY = -5f;
            server = 2;
        } else {
            ballSpeedX = -6f;
            ballSpeedY = -5f;
            server = 1;
        }
        canHit1 = false;
        canHit2 = true;
    }

    public void jump(boolean isP1) {
        if (isP1 && !player1Jumping) {
            player1Vy = -GameConstants.JUMP_SPEED;
            player1Jumping = true;
        } else if (!isP1 && !player2Jumping) {
            player2Vy = -GameConstants.JUMP_SPEED;
            player2Jumping = true;
        }
    }

    public void update() {
        if (!gameRunning) return;

        lock.lock();
        try {
            // 玩家跳跃
            if (player1Jumping) {
                player1Y += player1Vy;
                player1Vy += 0.3f;
                if (player1Y >= GameConstants.GROUND_Y - 50) {
                    player1Y = GameConstants.GROUND_Y - 50;
                    player1Jumping = false;
                    player1Vy = 0;
                }
            }
            if (player2Jumping) {
                player2Y += player2Vy;
                player2Vy += 0.3f;
                if (player2Y >= GameConstants.GROUND_Y - 50) {
                    player2Y = GameConstants.GROUND_Y - 50;
                    player2Jumping = false;
                    player2Vy = 0;
                }
            }

            // 球物理
            if (ballInPlay) {
                ballSpeedY += 0.25f;
                ballSpeedX *= 0.99f;
                ballSpeedY *= 0.99f;
                ballX += ballSpeedX;
                ballY += ballSpeedY;

                // 落地/出界得分
                if (ballY >= GameConstants.GROUND_Y - 10 || ballX < 5 || ballX > GameConstants.WIDTH - 5) {
                    if (ballX < GameConstants.NET_X) score2++; else score1++;
                    if (score1 >= 7 || score2 >= 7) gameRunning = false;
                    resetBall();
                }

                if (random.nextInt(4) == 0)
                    trails.add(new TrailEffect(ballX, ballY));
            }

            particles.removeIf(p -> !p.update());
            trails.removeIf(t -> !t.update());

            // 同步火柴人
            if (stickMen.size() >= 2) {
                StickMan s1 = stickMen.get(0), s2 = stickMen.get(1);
                s1.x = player1X; s1.y = player1Y;
                s2.x = player2X; s2.y = player2Y;
                long now = System.currentTimeMillis();
                s1.isSwinging = now - lastSwing1 < 200;
                s2.isSwinging = now - lastSwing2 < 200;
                s1.swingProgress = Math.max(0, 1 - (now - lastSwing1) / 200f);
                s2.swingProgress = Math.max(0, 1 - (now - lastSwing2) / 200f);
                s1.isCharging = charging1; s2.isCharging = charging2;
                s1.isJumping = player1Jumping; s2.isJumping = player2Jumping;
                s1.canHit = canHit1; s2.canHit = canHit2;
            }
        } finally {
            lock.unlock();
        }
    }

    private void resetBall() {
        ballInPlay = false;
        canHit1 = canHit2 = true;
        charging1 = charging2 = false;
        if (server == 1) {
            ballX = player1X + 30;
            ballY = player1Y - 20;
        } else {
            ballX = player2X - 30;
            ballY = player2Y - 20;
        }
    }

    public void movePlayer1(boolean right) {
        lock.lock();
        try {
            player1X += right ? 6 : -6;
            player1X = Math.clamp(player1X, 50, GameConstants.NET_X - 40);
        } finally { lock.unlock(); }
    }

    public void movePlayer2(boolean right) {
        lock.lock();
        try {
            player2X += right ? 6 : -6;
            player2X = Math.clamp(player2X, GameConstants.NET_X + 40, GameConstants.WIDTH - 50);
        } finally { lock.unlock(); }
    }

    public void addExplosion(float x, float y, Color c, int n) {
        for (int i = 0; i < n; i++) {
            float a = random.nextFloat() * 6.28f;
            float s = random.nextFloat() * 3 + 1;
            particles.add(new Particle(x, y, (float) Math.cos(a) * s, (float) Math.sin(a) * s, c));
        }
    }

    // 内部类完全兼容界面
    public class Particle {
        public float x, y, vx, vy;
        public Color color;
        public int life, maxLife;
        public Particle(float x, float y, float vx, float vy, Color color) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.color = color;
            life = maxLife = 15 + random.nextInt(15);
        }
        public boolean update() { x += vx; y += vy; vy += 0.1f; life--; return life > 0; }
    }

    public class TrailEffect {
        public float x, y;
        public int life = 12;
        public TrailEffect(float x, float y) { this.x = x; this.y = y; }
        public boolean update() { life--; return life > 0; }
    }

    public class StickMan {
        public float x, y;
        public boolean isLeft;
        public boolean isSwinging;
        public float swingProgress;
        public boolean isCharging, isJumping, canHit;
        public StickMan(float x, float y, boolean isLeft) {
            this.x = x; this.y = y; this.isLeft = isLeft;
        }
    }

    // Getter 不变
    public float getPlayer1X() { return player1X; }
    public float getPlayer1Y() { return player1Y; }
    public float getPlayer2X() { return player2X; }
    public float getPlayer2Y() { return player2Y; }
    public float getBallX() { return ballX; }
    public float getBallY() { return ballY; }
    public int getScore1() { return score1; }
    public int getScore2() { return score2; }
    public boolean isGameRunning() { return gameRunning; }
    public boolean isBallInPlay() { return ballInPlay; }
    public int getHitCount1() { return hitCount1; }
    public int getHitCount2() { return hitCount2; }
    public boolean canHit1() { return canHit1; }
    public boolean canHit2() { return canHit2; }
    public boolean isCharging1() { return charging1; }
    public boolean isCharging2() { return charging2; }
    public CopyOnWriteArrayList<Particle> getParticles() { return particles; }
    public CopyOnWriteArrayList<TrailEffect> getTrails() { return trails; }
    public ArrayList<StickMan> getStickMen() { return stickMen; }
}