package yumaoqou.core;

import yumaoqou.utils.GameConstants;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class GameCore {
    // 游戏状态
    private float player1X, player1Y;
    private float player2X, player2Y;
    private float player1Vy, player2Vy;
    private boolean player1Jumping = false;
    private boolean player2Jumping = false;
    private float ballX, ballY;
    private float ballSpeedX, ballSpeedY;
    private int score1, score2;
    private boolean gameRunning;
    private int server;
    private boolean ballInPlay;

    // 蓄力系统
    private float power1 = 0;
    private float power2 = 0;
    private boolean charging1 = false;
    private boolean charging2 = false;
    private long chargeStartTime1 = 0;
    private long chargeStartTime2 = 0;
    private boolean justReleased1 = false;
    private boolean justReleased2 = false;
    private long lastReleaseTime1 = 0;
    private long lastReleaseTime2 = 0;

    // 球跟随状态
    private boolean ballFollowing1 = false;
    private boolean ballFollowing2 = false;

    // 击球状态
    private boolean hasHit1 = false;
    private boolean hasHit2 = false;
    private long lastHitTime1 = 0;
    private long lastHitTime2 = 0;

    // 特效系统
    private CopyOnWriteArrayList<Particle> particles;
    private CopyOnWriteArrayList<TrailEffect> trails;
    private ArrayList<StickMan> stickMen;
    private Random random;

    // 击球特效
    private long lastSwingTime1, lastSwingTime2;
    private int hitCount1 = 0;
    private int hitCount2 = 0;

    // 线程锁
    private final ReentrantLock gameLock = new ReentrantLock();

    public GameCore() {
        this.random = new Random();
        this.particles = new CopyOnWriteArrayList<>();
        this.trails = new CopyOnWriteArrayList<>();
        this.stickMen = new ArrayList<>();

        resetGame();
    }

    public void resetGame() {
        gameLock.lock();
        try {
            player1X = GameConstants.PLAYER1_START_X;
            player1Y = GameConstants.GROUND_Y - 50;
            player2X = GameConstants.PLAYER2_START_X;
            player2Y = GameConstants.GROUND_Y - 50;
            player1Vy = 0;
            player2Vy = 0;
            player1Jumping = false;
            player2Jumping = false;

            score1 = 0;
            score2 = 0;
            gameRunning = true;
            server = 1;
            ballInPlay = false;
            ballFollowing1 = true;
            ballFollowing2 = false;

            hitCount1 = 0;
            hitCount2 = 0;
            hasHit1 = false;
            hasHit2 = false;

            // 重置球的位置
            ballX = player1X + 30;
            ballY = player1Y - 40;
            ballSpeedX = 0;
            ballSpeedY = 0;

            particles.clear();
            trails.clear();
            stickMen.clear();

            stickMen.add(new StickMan(player1X, player1Y, true));
            stickMen.add(new StickMan(player2X, player2Y, false));

            power1 = 0;
            power2 = 0;
            charging1 = false;
            charging2 = false;
            justReleased1 = false;
            justReleased2 = false;
        } finally {
            gameLock.unlock();
        }
    }

    public void resetBall() {
        ballInPlay = false;
        hasHit1 = false;
        hasHit2 = false;

        if (server == 1) {
            ballFollowing1 = true;
            ballFollowing2 = false;
            ballX = player1X + 30;
            ballY = player1Y - 40;
            ballSpeedX = 0;
            ballSpeedY = 0;
        } else {
            ballFollowing1 = false;
            ballFollowing2 = true;
            ballX = player2X - 30;
            ballY = player2Y - 40;
            ballSpeedX = 0;
            ballSpeedY = 0;
        }
    }

    // 蓄力开始
    public void startCharging(boolean isPlayer1) {
        long now = System.currentTimeMillis();

        // 检查是否刚释放过，防止快速连按
        if (isPlayer1 && justReleased1 && (now - lastReleaseTime1) < GameConstants.RELEASE_COOLDOWN) {
            return;
        }
        if (!isPlayer1 && justReleased2 && (now - lastReleaseTime2) < GameConstants.RELEASE_COOLDOWN) {
            return;
        }

        // 发球阶段
        if (!ballInPlay) {
            if (isPlayer1 && server == 1 && !charging1) {
                charging1 = true;
                chargeStartTime1 = System.currentTimeMillis();
                power1 = 0;
            } else if (!isPlayer1 && server == 2 && !charging2) {
                charging2 = true;
                chargeStartTime2 = System.currentTimeMillis();
                power2 = 0;
            }
        }
        // 比赛阶段
        else {
            if (isPlayer1 && !hasHit1 && !charging1 && canHit(true)) {
                charging1 = true;
                chargeStartTime1 = System.currentTimeMillis();
                power1 = 0;
            } else if (!isPlayer1 && !hasHit2 && !charging2 && canHit(false)) {
                charging2 = true;
                chargeStartTime2 = System.currentTimeMillis();
                power2 = 0;
            }
        }
    }

    // 释放蓄力（击球）
    public void releaseCharge(boolean isPlayer1) {
        long now = System.currentTimeMillis();

        // 发球阶段
        if (!ballInPlay) {
            if (isPlayer1 && charging1 && server == 1) {
                long chargeTime = Math.min(GameConstants.MAX_CHARGE_TIME,
                        now - chargeStartTime1);
                float powerFactor = (float)chargeTime / GameConstants.MAX_CHARGE_TIME;
                power1 = GameConstants.MIN_POWER +
                        (GameConstants.MAX_POWER - GameConstants.MIN_POWER) * powerFactor;

                serve(true, power1);
                charging1 = false;
                justReleased1 = true;
                lastReleaseTime1 = now;
                lastSwingTime1 = now;
                hasHit1 = true;

                for (int i = 0; i < 10 + (int)(powerFactor * 20); i++) {
                    addExplosion(ballX, ballY, GameConstants.CHARGE_COLOR, 1);
                }

            } else if (!isPlayer1 && charging2 && server == 2) {
                long chargeTime = Math.min(GameConstants.MAX_CHARGE_TIME,
                        now - chargeStartTime2);
                float powerFactor = (float)chargeTime / GameConstants.MAX_CHARGE_TIME;
                power2 = GameConstants.MIN_POWER +
                        (GameConstants.MAX_POWER - GameConstants.MIN_POWER) * powerFactor;

                serve(false, power2);
                charging2 = false;
                justReleased2 = true;
                lastReleaseTime2 = now;
                lastSwingTime2 = now;
                hasHit2 = true;

                for (int i = 0; i < 10 + (int)(powerFactor * 20); i++) {
                    addExplosion(ballX, ballY, GameConstants.CHARGE_COLOR, 1);
                }
            }
        }
        // 比赛阶段
        else {
            if (isPlayer1 && charging1 && !hasHit1 && canHit(true)) {
                long chargeTime = Math.min(GameConstants.MAX_CHARGE_TIME,
                        now - chargeStartTime1);
                float powerFactor = (float)chargeTime / GameConstants.MAX_CHARGE_TIME;
                power1 = GameConstants.MIN_POWER +
                        (GameConstants.MAX_POWER - GameConstants.MIN_POWER) * powerFactor;

                hitBall(true, power1);
                charging1 = false;
                justReleased1 = true;
                lastReleaseTime1 = now;
                lastSwingTime1 = now;
                lastHitTime1 = now;
                hasHit1 = true;
                hitCount1++;

                for (int i = 0; i < 10 + (int)(powerFactor * 20); i++) {
                    addExplosion(ballX, ballY, GameConstants.PLAYER1_COLOR, 1);
                }

            } else if (!isPlayer1 && charging2 && !hasHit2 && canHit(false)) {
                long chargeTime = Math.min(GameConstants.MAX_CHARGE_TIME,
                        now - chargeStartTime2);
                float powerFactor = (float)chargeTime / GameConstants.MAX_CHARGE_TIME;
                power2 = GameConstants.MIN_POWER +
                        (GameConstants.MAX_POWER - GameConstants.MIN_POWER) * powerFactor;

                hitBall(false, power2);
                charging2 = false;
                justReleased2 = true;
                lastReleaseTime2 = now;
                lastSwingTime2 = now;
                lastHitTime2 = now;
                hasHit2 = true;
                hitCount2++;

                for (int i = 0; i < 10 + (int)(powerFactor * 20); i++) {
                    addExplosion(ballX, ballY, GameConstants.PLAYER2_COLOR, 1);
                }
            }
        }

        // 延迟重置释放标记
        new Thread(() -> {
            try {
                Thread.sleep(GameConstants.RELEASE_COOLDOWN);
                if (isPlayer1) {
                    justReleased1 = false;
                } else {
                    justReleased2 = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean canHit(boolean isPlayer1) {
        long now = System.currentTimeMillis();
        if (isPlayer1) {
            boolean ballInLeft = ballX < GameConstants.NET_X;
            boolean collision = checkPaddleCollision(true);
            boolean notOnCooldown = (now - lastHitTime1) > GameConstants.HIT_COOLDOWN;
            return ballInLeft && collision && notOnCooldown && !hasHit1;
        } else {
            boolean ballInRight = ballX > GameConstants.NET_X;
            boolean collision = checkPaddleCollision(false);
            boolean notOnCooldown = (now - lastHitTime2) > GameConstants.HIT_COOLDOWN;
            return ballInRight && collision && notOnCooldown && !hasHit2;
        }
    }

    private boolean checkPaddleCollision(boolean isPlayer1) {
        float paddleX = isPlayer1 ? player1X : player2X;
        float paddleY = isPlayer1 ? player1Y : player2Y;

        float paddleCenterX = paddleX + GameConstants.PADDLE_WIDTH/2;
        float paddleCenterY = paddleY + GameConstants.PADDLE_HEIGHT/2;
        float ballCenterX = ballX + GameConstants.BALL_SIZE/2;
        float ballCenterY = ballY + GameConstants.BALL_SIZE/2;

        float dx = ballCenterX - paddleCenterX;
        float dy = ballCenterY - paddleCenterY;
        float distance = (float)Math.sqrt(dx*dx + dy*dy);

        return distance < GameConstants.HIT_RADIUS;
    }

    private void serve(boolean isPlayer1, float power) {
        ballInPlay = true;
        ballFollowing1 = false;
        ballFollowing2 = false;

        if (isPlayer1) {
            ballX = player1X + 30;
            ballY = player1Y - 40;
            ballSpeedX = power * 0.8f;
            ballSpeedY = -power * 0.6f;
            server = 2;
        } else {
            ballX = player2X - 30;
            ballY = player2Y - 40;
            ballSpeedX = -power * 0.8f;
            ballSpeedY = -power * 0.6f;
            server = 1;
        }

        if (ballSpeedY > 0) ballSpeedY = -Math.abs(ballSpeedY);

        addExplosion(ballX, ballY, new Color(255, 215, 0), 20);
    }

    private void hitBall(boolean isPlayer1, float power) {
        float powerFactor = (power - GameConstants.MIN_POWER) /
                (GameConstants.MAX_POWER - GameConstants.MIN_POWER);
        float angle = GameConstants.MIN_ANGLE + powerFactor * GameConstants.MAX_ANGLE;

        float hitY = isPlayer1 ? player1Y - 30 : player2Y - 30;
        float hitOffset = (ballY - hitY) / 40;
        hitOffset = Math.max(-0.5f, Math.min(0.5f, hitOffset));

        float finalAngle = angle + hitOffset * 0.3f;
        finalAngle = Math.max(0.1f, Math.min(0.9f, finalAngle));

        if (isPlayer1) {
            ballSpeedX = power;
            ballSpeedY = -power * finalAngle;
            server = 2;
        } else {
            ballSpeedX = -power;
            ballSpeedY = -power * finalAngle;
            server = 1;
        }

        if (ballSpeedY > 0) ballSpeedY = -Math.abs(ballSpeedY);

        addExplosion(ballX, ballY, new Color(255, 215, 0), 15);
    }

    public void jump(boolean isPlayer1) {
        if (isPlayer1 && !player1Jumping) {
            player1Vy = -GameConstants.JUMP_SPEED;
            player1Jumping = true;
        } else if (!isPlayer1 && !player2Jumping) {
            player2Vy = -GameConstants.JUMP_SPEED;
            player2Jumping = true;
        }
    }

    public void update() {
        if (!gameRunning) return;

        gameLock.lock();
        try {
            updatePlayerPhysics();

            if (ballFollowing1) {
                ballX = player1X + 30;
                ballY = player1Y - 40;
            } else if (ballFollowing2) {
                ballX = player2X - 30;
                ballY = player2Y - 40;
            }

            if (ballInPlay) {
                float prevX = ballX;
                float prevY = ballY;

                applyPhysics();

                ballX += ballSpeedX;
                ballY += ballSpeedY;

                maintainMinimumSpeed();
                checkNetCollision(prevX, prevY);
                handleBoundaryCollision();
                checkScoring();

                if (random.nextInt(3) == 0) {
                    trails.add(new TrailEffect(ballX, ballY));
                }
            }

            updateChargingEffects();
            updateEffects();
            updateStickMen();
        } finally {
            gameLock.unlock();
        }
    }

    private void updatePlayerPhysics() {
        if (player1Jumping) {
            player1Y += player1Vy;
            player1Vy += GameConstants.JUMP_GRAVITY;
            if (player1Y >= GameConstants.GROUND_Y - 50) {
                player1Y = GameConstants.GROUND_Y - 50;
                player1Jumping = false;
                player1Vy = 0;
            }
        }

        if (player2Jumping) {
            player2Y += player2Vy;
            player2Vy += GameConstants.JUMP_GRAVITY;
            if (player2Y >= GameConstants.GROUND_Y - 50) {
                player2Y = GameConstants.GROUND_Y - 50;
                player2Jumping = false;
                player2Vy = 0;
            }
        }
    }

    private void applyPhysics() {
        ballSpeedY += GameConstants.GRAVITY;
        ballSpeedX *= GameConstants.AIR_RESISTANCE;
        ballSpeedY *= GameConstants.AIR_RESISTANCE;
    }

    private void maintainMinimumSpeed() {
        float currentSpeed = (float)Math.sqrt(ballSpeedX * ballSpeedX + ballSpeedY * ballSpeedY);
        if (currentSpeed < GameConstants.MIN_SPEED && currentSpeed > 0) {
            float ratio = GameConstants.MIN_SPEED / currentSpeed;
            ballSpeedX *= ratio;
            ballSpeedY *= ratio;
        }
    }

    private void checkNetCollision(float prevX, float prevY) {
        boolean ballOverlapsNet = ballX + GameConstants.BALL_SIZE/2 > GameConstants.NET_LEFT &&
                ballX - GameConstants.BALL_SIZE/2 < GameConstants.NET_RIGHT &&
                ballY + GameConstants.BALL_SIZE/2 > GameConstants.NET_BOTTOM_Y &&
                ballY - GameConstants.BALL_SIZE/2 < GameConstants.NET_TOP_Y;

        boolean crossedNet = (prevX < GameConstants.NET_X && ballX >= GameConstants.NET_X) ||
                (prevX > GameConstants.NET_X && ballX <= GameConstants.NET_X);

        if (ballOverlapsNet || crossedNet) {
            if (ballY + GameConstants.BALL_SIZE/2 > GameConstants.NET_HEIGHT &&
                    ballY - GameConstants.BALL_SIZE/2 < GameConstants.NET_BOTTOM_Y + 20) {
                if (crossedNet) {
                    ballX = prevX;
                } else {
                    ballX = ballX < GameConstants.NET_X ?
                            GameConstants.NET_LEFT - GameConstants.BALL_SIZE/2 :
                            GameConstants.NET_RIGHT + GameConstants.BALL_SIZE/2;
                }

                ballSpeedX = -ballSpeedX * GameConstants.NET_BOUNCE_LOSS;
                ballSpeedY *= GameConstants.NET_BOUNCE_LOSS;

                addExplosion(GameConstants.NET_X, ballY, Color.RED, 15);

                if (server == 1) server = 2;
                else server = 1;

                hasHit1 = false;
                hasHit2 = false;
            } else if (crossedNet && ballY <= GameConstants.NET_HEIGHT) {
                addExplosion(GameConstants.NET_X, ballY, new Color(255, 255, 255, 80), 3);
            }
        }
    }

    private void handleBoundaryCollision() {
        if (ballY <= 30) {
            ballY = 31;
            ballSpeedY = -ballSpeedY * 0.8f;
            addExplosion(ballX, ballY, Color.WHITE, 5);
        }

        if (ballX <= 20 || ballX >= GameConstants.WIDTH - GameConstants.BALL_SIZE - 20) {
            if (ballX <= 20) {
                score2++;
                if (score2 >= GameConstants.WIN_SCORE) gameRunning = false;
                server = 2;
            } else {
                score1++;
                if (score1 >= GameConstants.WIN_SCORE) gameRunning = false;
                server = 1;
            }

            addExplosion(ballX, ballY, new Color(139, 69, 19), 15);
            ballInPlay = false;
            hasHit1 = false;
            hasHit2 = false;

            if (server == 1) {
                ballFollowing1 = true;
                ballFollowing2 = false;
            } else {
                ballFollowing1 = false;
                ballFollowing2 = true;
            }
            resetBall();
        }
    }

    private void checkScoring() {
        if (ballY >= GameConstants.GROUND_Y - GameConstants.BALL_SIZE - 5) {
            if (ballX < GameConstants.NET_X) {
                score2++;
                if (score2 >= GameConstants.WIN_SCORE) gameRunning = false;
                server = 2;
            } else {
                score1++;
                if (score1 >= GameConstants.WIN_SCORE) gameRunning = false;
                server = 1;
            }

            addExplosion(ballX, ballY, new Color(139, 69, 19), 15);
            ballInPlay = false;
            hasHit1 = false;
            hasHit2 = false;

            if (server == 1) {
                ballFollowing1 = true;
                ballFollowing2 = false;
            } else {
                ballFollowing1 = false;
                ballFollowing2 = true;
            }
            resetBall();
        }
    }

    private void updateChargingEffects() {
        if (charging1) {
            long chargeTime = Math.min(GameConstants.MAX_CHARGE_TIME,
                    System.currentTimeMillis() - chargeStartTime1);
            float progress = (float)chargeTime / GameConstants.MAX_CHARGE_TIME;
            if (random.nextInt(3) == 0) {
                addExplosion(ballX + random.nextInt(20) - 10,
                        ballY + random.nextInt(20) - 10,
                        GameConstants.CHARGE_COLOR, 1);
            }
        }

        if (charging2) {
            long chargeTime = Math.min(GameConstants.MAX_CHARGE_TIME,
                    System.currentTimeMillis() - chargeStartTime2);
            float progress = (float)chargeTime / GameConstants.MAX_CHARGE_TIME;
            if (random.nextInt(3) == 0) {
                addExplosion(ballX + random.nextInt(20) - 10,
                        ballY + random.nextInt(20) - 10,
                        GameConstants.CHARGE_COLOR, 1);
            }
        }
    }

    private void updateStickMen() {
        if (stickMen.size() >= 2) {
            stickMen.get(0).x = player1X;
            stickMen.get(0).y = player1Y;
            stickMen.get(1).x = player2X;
            stickMen.get(1).y = player2Y;

            long now = System.currentTimeMillis();
            stickMen.get(0).isSwinging = (now - lastSwingTime1) < 200;
            stickMen.get(1).isSwinging = (now - lastSwingTime2) < 200;
            stickMen.get(0).swingProgress = Math.max(0, 1 - (now - lastSwingTime1) / 200f);
            stickMen.get(1).swingProgress = Math.max(0, 1 - (now - lastSwingTime2) / 200f);
            stickMen.get(0).isCharging = charging1;
            stickMen.get(1).isCharging = charging2;
            stickMen.get(0).isJumping = player1Jumping;
            stickMen.get(1).isJumping = player2Jumping;
            stickMen.get(0).hasHit = hasHit1;
            stickMen.get(1).hasHit = hasHit2;
        }
    }

    private void updateEffects() {
        particles.removeIf(p -> !p.update());
        trails.removeIf(t -> !t.update());
    }

    public void addExplosion(float x, float y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            float speed = random.nextFloat() * 3 + 1;
            float vx = (float)(Math.cos(angle) * speed);
            float vy = (float)(Math.sin(angle) * speed);
            particles.add(new Particle(x, y, vx, vy, color));
        }
    }

    public void movePlayer1(boolean right) {
        gameLock.lock();
        try {
            if (right) {
                player1X = Math.min(GameConstants.PLAYER1_MAX_X, player1X + GameConstants.PLAYER_SPEED);
            } else {
                player1X = Math.max(GameConstants.LEFT_BOUNDARY, player1X - GameConstants.PLAYER_SPEED);
            }
            if (ballFollowing1) {
                ballX = player1X + 30;
            }
        } finally {
            gameLock.unlock();
        }
    }

    public void movePlayer2(boolean right) {
        gameLock.lock();
        try {
            if (right) {
                player2X = Math.min(GameConstants.RIGHT_BOUNDARY, player2X + GameConstants.PLAYER_SPEED);
            } else {
                player2X = Math.max(GameConstants.PLAYER2_MIN_X, player2X - GameConstants.PLAYER_SPEED);
            }
            if (ballFollowing2) {
                ballX = player2X - 30;
            }
        } finally {
            gameLock.unlock();
        }
    }

    // Getters
    public float getPlayer1X() { return player1X; }
    public float getPlayer1Y() { return player1Y; }
    public float getPlayer2X() { return player2X; }
    public float getPlayer2Y() { return player2Y; }
    public float getBallX() { return ballX; }
    public float getBallY() { return ballY; }
    public int getScore1() { return score1; }
    public int getScore2() { return score2; }
    public int getServer() { return server; }
    public boolean isGameRunning() { return gameRunning; }
    public boolean isBallInPlay() { return ballInPlay; }
    public int getHitCount1() { return hitCount1; }
    public int getHitCount2() { return hitCount2; }
    public boolean hasHit1() { return hasHit1; }
    public boolean hasHit2() { return hasHit2; }

    public float getPower1() {
        if (charging1) {
            long chargeTime = Math.min(GameConstants.MAX_CHARGE_TIME,
                    System.currentTimeMillis() - chargeStartTime1);
            return (float)chargeTime / GameConstants.MAX_CHARGE_TIME;
        }
        return 0;
    }

    public float getPower2() {
        if (charging2) {
            long chargeTime = Math.min(GameConstants.MAX_CHARGE_TIME,
                    System.currentTimeMillis() - chargeStartTime2);
            return (float)chargeTime / GameConstants.MAX_CHARGE_TIME;
        }
        return 0;
    }

    public boolean isCharging1() { return charging1; }
    public boolean isCharging2() { return charging2; }

    public CopyOnWriteArrayList<Particle> getParticles() { return particles; }
    public CopyOnWriteArrayList<TrailEffect> getTrails() { return trails; }
    public ArrayList<StickMan> getStickMen() { return stickMen; }

    // 内部类：粒子
    public class Particle {
        public float x, y, vx, vy;
        public Color color;
        public int life, maxLife;

        public Particle(float x, float y, float vx, float vy, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            life = maxLife = 15 + random.nextInt(15);
        }

        public boolean update() {
            x += vx;
            y += vy;
            vy += 0.1f;
            life--;
            return life > 0;
        }
    }

    // 内部类：轨迹
    public class TrailEffect {
        public float x, y;
        public int life = 12;

        public TrailEffect(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public boolean update() {
            life--;
            return life > 0;
        }
    }

    // 内部类：火柴人
    public class StickMan {
        public float x, y;
        public boolean isLeft;
        public boolean isSwinging = false;
        public float swingProgress = 0;
        public boolean isCharging = false;
        public boolean isJumping = false;
        public boolean hasHit = false;

        public StickMan(float x, float y, boolean isLeft) {
            this.x = x;
            this.y = y;
            this.isLeft = isLeft;
        }
    }
}