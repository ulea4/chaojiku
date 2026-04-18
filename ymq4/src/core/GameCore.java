package yumaoqou.core;

import yumaoqou.audio.AudioManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameCore {
    // 游戏常量
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 800;
    public static final int PADDLE_WIDTH = 30;
    public static final int PADDLE_HEIGHT = 80;
    public static final int BALL_SIZE = 18;
    public static final int PLAYER_SPEED = 8;
    public static final int JUMP_SPEED = 12;
    public static final int WIN_SCORE = 11;
    public static final int HIT_RADIUS = 65;

    // 游戏模式
    public static final int MODE_VS_PLAYER = 0;
    public static final int MODE_VS_AI = 1;

    // 难度
    public static final int DIFF_EASY = 0;
    public static final int DIFF_MEDIUM = 1;
    public static final int DIFF_HARD = 2;
    public static final int DIFF_EXPERT = 3;

    // 场地常量
    public static final int GROUND_Y = HEIGHT - 150;
    public static final int NET_X = WIDTH / 2;
    public static final int NET_TOP_Y = GROUND_Y - 120;
    public static final int NET_BOTTOM_Y = GROUND_Y - 40;
    public static final int NET_HEIGHT = NET_TOP_Y;
    public static final int NET_WIDTH = 16;
    public static final int NET_LEFT = NET_X - NET_WIDTH/2;
    public static final int NET_RIGHT = NET_X + NET_WIDTH/2;

    public static final int NET_COLLISION_LEFT = NET_LEFT - 5;
    public static final int NET_COLLISION_RIGHT = NET_RIGHT + 5;
    public static final int NET_COLLISION_TOP = NET_TOP_Y;
    public static final int NET_COLLISION_BOTTOM = NET_BOTTOM_Y + 10;

    public static final int LEFT_BOUNDARY = 80;
    public static final int RIGHT_BOUNDARY = WIDTH - 80;
    public static final int PLAYER1_MAX_X = NET_X - 50;
    public static final int PLAYER2_MIN_X = NET_X + 50;
    public static final int PLAYER1_START_X = 200;
    public static final int PLAYER2_START_X = WIDTH - 200;

    // 物理常量
    private static final float GRAVITY = 0.25f;
    private static final float AIR_RESISTANCE = 0.98f;
    private static final float MAX_POWER = 24f;
    private static final float MIN_POWER = 7f;
    private static final float NET_BOUNCE_LOSS = 0.6f;
    private static final float JUMP_GRAVITY = 0.4f;
    private static final float MIN_SPEED = 1.0f;

    // 蓄力常量
    private static final long MAX_CHARGE_TIME_SERVE = 2000;
    private static final long MAX_CHARGE_TIME_HIT = 1000;
    private static final long SMASH_CHARGE_TIME = 1500;

    // 游戏状态
    private float player1X, player1Y;
    private float player2X, player2Y;
    private float player1Vy, player2Vy;
    private boolean player1Jumping = false;
    private boolean player2Jumping = false;
    private float ballX, ballY;
    private float ballSpeedX, ballSpeedY;
    private float ballSpin = 0;
    private int score1, score2;
    private boolean gameRunning;
    private int server;
    private boolean ballInPlay;

    // 游戏模式
    private int gameMode = MODE_VS_PLAYER;
    private int difficulty = DIFF_MEDIUM;
    private boolean aiEnabled = false;

    // 天气系统
    private float windPower = 0;
    private float windDirection = 0;
    private boolean weatherEnabled = false;
    private long lastWeatherChange = 0;
    private static final long WEATHER_INTERVAL = 5000;

    // AI 智能系统
    private float aiTargetX = PLAYER2_START_X;
    private float aiPredictedBallX = 0;
    private float aiPredictedBallY = 0;
    private long aiLastDecisionTime = 0;
    private static final long AI_DECISION_INTERVAL = 50;
    private int aiState = 0; // 0: 回位, 1: 移动接球, 2: 准备击球, 3: 击球
    private float aiAggression = 0.5f; // 攻击性
    private float aiPrecision = 0.7f; // 精准度
    private float aiReactionTime = 0.1f; // 反应时间（秒）
    private long aiChargeStartTime = 0;
    private boolean aiIsCharging = false;
    private float aiTargetPower = 0.5f;

    // 蓄力系统
    private float power1 = 0;
    private float power2 = 0;
    private boolean charging1 = false;
    private boolean charging2 = false;
    private long chargeStartTime1 = 0;
    private long chargeStartTime2 = 0;
    private boolean isServeCharge1 = false;
    private boolean isServeCharge2 = false;
    private boolean isSmashCharge1 = false;
    private boolean isSmashCharge2 = false;

    // 球跟随状态
    private boolean ballFollowing1 = false;
    private boolean ballFollowing2 = false;

    // 击球冷却
    private long lastHitTime1 = 0;
    private long lastHitTime2 = 0;
    private static final long HIT_COOLDOWN = 150;

    // 子弹时间
    private boolean bulletTime = false;
    private long bulletTimeEnd = 0;
    private float timeScale = 1.0f;

    // 特效系统
    private CopyOnWriteArrayList<Particle> particles;
    private CopyOnWriteArrayList<TrailEffect> trails;
    private CopyOnWriteArrayList<Sparkle> sparkles;
    private CopyOnWriteArrayList<SmashWave> smashWaves;
    private ArrayList<StickMan> stickMen;
    private Random random;

    // 击球特效
    private long lastSwingTime1, lastSwingTime2;

    // 连击系统
    private int comboCount = 0;
    private int maxCombo = 0;
    private long lastHitTime = 0;
    private static final long COMBO_TIMEOUT = 2000;

    // 技能系统
    private float player1Energy = 100;
    private float player2Energy = 100;
    private boolean player1Dash = false;
    private boolean player2Dash = false;
    private long dashEndTime1 = 0;
    private long dashEndTime2 = 0;

    // 特殊击球
    private boolean isSmash = false;
    private boolean isDropShot = false;
    private boolean isClear = false;
    private String lastShotType = "";

    public GameCore() {
        this.random = new Random();
        this.particles = new CopyOnWriteArrayList<>();
        this.trails = new CopyOnWriteArrayList<>();
        this.sparkles = new CopyOnWriteArrayList<>();
        this.smashWaves = new CopyOnWriteArrayList<>();
        this.stickMen = new ArrayList<>();

        resetGame();
    }

    /**
     * 播放击球音效
     */
    private void playHitSound() {
        AudioManager.getInstance().playHitSound();
    }

    /**
     * 播放发球音效
     */
    private void playServeSound() {
        AudioManager.getInstance().playServeSound();
    }

    /**
     * 播放得分音效
     */
    private void playScoreSound() {
        AudioManager.getInstance().playScoreSound();
    }

    /**
     * 播放扣杀音效
     */
    private void playSmashSound() {
        AudioManager.getInstance().playSmashSound();
    }

    /**
     * 播放闪避音效
     */
    private void playDashSound() {
        AudioManager.getInstance().playDashSound();
    }

    public void setGameMode(int mode, int diff) {
        this.gameMode = mode;
        this.difficulty = diff;
        this.aiEnabled = (mode == MODE_VS_AI);
        this.weatherEnabled = (diff >= DIFF_HARD);

        // 根据难度设置 AI 参数
        configureAI();
    }

    private void configureAI() {
        switch (difficulty) {
            case DIFF_EASY:
                aiAggression = 0.3f;
                aiPrecision = 0.5f;
                aiReactionTime = 0.3f;
                break;
            case DIFF_MEDIUM:
                aiAggression = 0.5f;
                aiPrecision = 0.7f;
                aiReactionTime = 0.15f;
                break;
            case DIFF_HARD:
                aiAggression = 0.7f;
                aiPrecision = 0.85f;
                aiReactionTime = 0.08f;
                break;
            case DIFF_EXPERT:
                aiAggression = 0.9f;
                aiPrecision = 0.95f;
                aiReactionTime = 0.03f;
                break;
        }
    }

    public void resetGame() {
        player1X = PLAYER1_START_X;
        player1Y = GROUND_Y - 50;
        player2X = PLAYER2_START_X;
        player2Y = GROUND_Y - 50;
        player1Vy = 0;
        player2Vy = 0;
        player1Jumping = false;
        player2Jumping = false;

        score1 = 0;
        score2 = 0;
        gameRunning = true;
        server = random.nextInt(2) + 1;
        ballInPlay = false;
        ballFollowing1 = (server == 1);
        ballFollowing2 = (server == 2);

        comboCount = 0;
        maxCombo = 0;
        player1Energy = 100;
        player2Energy = 100;
        bulletTime = false;
        timeScale = 1.0f;
        windPower = weatherEnabled ? random.nextFloat() * 3 : 0;
        windDirection = random.nextFloat() * (float)Math.PI * 2;

        aiState = 0;
        aiIsCharging = false;
        aiTargetX = PLAYER2_START_X;

        resetBall();

        particles.clear();
        trails.clear();
        sparkles.clear();
        smashWaves.clear();
        stickMen.clear();

        stickMen.add(new StickMan(player1X, player1Y, true));
        stickMen.add(new StickMan(player2X, player2Y, false));

        power1 = 0;
        power2 = 0;
        charging1 = false;
        charging2 = false;
        isServeCharge1 = false;
        isServeCharge2 = false;
    }

    public void resetBall() {
        ballInPlay = false;
        ballSpin = 0;

        if (server == 1) {
            ballFollowing1 = true;
            ballFollowing2 = false;
            ballX = player1X + 30;
            ballY = player1Y - 40;
        } else {
            ballFollowing1 = false;
            ballFollowing2 = true;
            ballX = player2X - 30;
            ballY = player2Y - 40;
        }
        ballSpeedX = 0;
        ballSpeedY = 0;
    }

    /**
     * AI 智能决策系统
     */
    private void updateAI() {
        if (!aiEnabled || !gameRunning) return;

        long now = System.currentTimeMillis();

        // 决策间隔
        if (now - aiLastDecisionTime < AI_DECISION_INTERVAL) {
            // 仍然执行移动
            executeAIMovement();
            return;
        }
        aiLastDecisionTime = now;

        // 预测球的轨迹
        predictBallTrajectory();

        // 根据游戏状态决定 AI 行为
        if (!ballInPlay) {
            // 球未发出
            if (server == 2) {
                // AI 发球
                aiState = 3;
                handleAIServe();
            } else {
                // 等待对方发球
                aiState = 0;
                aiTargetX = PLAYER2_START_X;
            }
        } else {
            // 球在飞行中
            analyzeBallAndDecide();
        }

        // 执行移动
        executeAIMovement();

        // 执行蓄力和击球
        executeAIChargeAndHit();
    }

    /**
     * 预测球的轨迹
     */
    private void predictBallTrajectory() {
        if (!ballInPlay) return;

        float predX = ballX;
        float predY = ballY;
        float predSpeedX = ballSpeedX;
        float predSpeedY = ballSpeedY;

        // 模拟球的飞行轨迹
        int maxSteps = 100;
        for (int i = 0; i < maxSteps; i++) {
            predX += predSpeedX;
            predY += predSpeedY;
            predSpeedY += GRAVITY;
            predSpeedX *= AIR_RESISTANCE;
            predSpeedY *= AIR_RESISTANCE;

            // 考虑风力
            if (weatherEnabled) {
                predSpeedX += Math.cos(windDirection) * windPower * 0.01f;
                predSpeedY += Math.sin(windDirection) * windPower * 0.01f;
            }

            // 球落地或过网
            if (predY >= GROUND_Y - BALL_SIZE - 5) {
                break;
            }

            // 记录球在AI半场时的位置
            if (predX > NET_X) {
                aiPredictedBallX = predX;
                aiPredictedBallY = predY;
            }
        }

        // 加入预测误差（根据精准度）
        float error = (1 - aiPrecision) * 50;
        aiPredictedBallX += (random.nextFloat() * 2 - 1) * error;
        aiPredictedBallY += (random.nextFloat() * 2 - 1) * error * 0.5f;

        // 限制在场地内
        aiPredictedBallX = Math.max(PLAYER2_MIN_X, Math.min(RIGHT_BOUNDARY, aiPredictedBallX));
    }

    /**
     * 分析球况并决定行动
     */
    private void analyzeBallAndDecide() {
        // 球在AI半场
        if (ballX > NET_X && ballSpeedX < 0) {
            // 球向AI飞来
            float distToBall = Math.abs(ballX - player2X);
            float timeToArrive = distToBall / Math.abs(ballSpeedX);

            // 判断是否需要跳跃
            boolean shouldJump = ballY < GROUND_Y - 80 &&
                    aiPredictedBallY < player2Y - 20 &&
                    !player2Jumping &&
                    random.nextFloat() < aiAggression * 0.5f;

            if (shouldJump) {
                jump(false);
            }

            // 判断是否使用闪避
            if (player2Energy >= 30 && distToBall > 120 && timeToArrive < 0.5f) {
                if (random.nextFloat() < aiAggression * 0.3f) {
                    useDash(false);
                }
            }

            // 决定击球策略
            if (distToBall < 120) {
                aiState = 2; // 准备击球

                // 计算目标击球力量
                float distToOpponent = Math.abs(player1X - player2X);
                if (distToOpponent > 400) {
                    // 对手站位靠后，使用吊球
                    aiTargetPower = 0.3f + random.nextFloat() * 0.2f;
                } else if (player1X < 300) {
                    // 对手靠前，使用高远球
                    aiTargetPower = 0.8f + random.nextFloat() * 0.2f;
                } else {
                    // 正常击球
                    aiTargetPower = 0.5f + aiAggression * 0.3f;
                }

                // 有机会就扣杀
                if (ballY < GROUND_Y - 100 && player2Jumping && player2Energy >= 30) {
                    if (random.nextFloat() < aiAggression * 0.4f) {
                        aiTargetPower = 1.0f;
                    }
                }
            } else {
                aiState = 1; // 移动接球
            }

            aiTargetX = aiPredictedBallX;

        } else if (ballX > NET_X && ballSpeedX > 0) {
            // 球向对方半场飞去
            aiState = 0;
            aiTargetX = PLAYER2_START_X; // 回位

        } else {
            // 球在对方半场
            aiState = 0;

            // 根据对手位置调整站位
            float optimalX = PLAYER2_START_X;
            if (player1X < 300) {
                optimalX = PLAYER2_MIN_X + 100; // 对手靠前，AI稍后退
            } else if (player1X > PLAYER1_MAX_X - 100) {
                optimalX = RIGHT_BOUNDARY - 50; // 对手靠后，AI稍前压
            }
            aiTargetX = optimalX;
        }

        // 反应延迟
        if (aiReactionTime > 0) {
            aiTargetX = player2X + (aiTargetX - player2X) * (1 - aiReactionTime);
        }
    }

    /**
     * 执行 AI 移动
     */
    private void executeAIMovement() {
        if (!aiEnabled) return;

        float moveSpeed = player2Dash ? PLAYER_SPEED * 1.5f : PLAYER_SPEED;
        moveSpeed *= (0.8f + aiPrecision * 0.4f);

        float diff = aiTargetX - player2X;

        if (Math.abs(diff) > moveSpeed) {
            if (diff > 0) {
                player2X = Math.min(RIGHT_BOUNDARY, player2X + moveSpeed);
            } else {
                player2X = Math.max(PLAYER2_MIN_X, player2X - moveSpeed);
            }
        } else {
            player2X = aiTargetX;
        }

        if (ballFollowing2) {
            ballX = player2X - 30;
        }
    }

    /**
     * 执行 AI 蓄力和击球
     */
    private void executeAIChargeAndHit() {
        if (!aiEnabled) return;

        long now = System.currentTimeMillis();

        // AI 发球
        if (!ballInPlay && server == 2) {
            if (!aiIsCharging) {
                startCharging(false);
                aiIsCharging = true;
                aiChargeStartTime = now;
            } else {
                long chargeTime = now - aiChargeStartTime;
                if (chargeTime > MAX_CHARGE_TIME_SERVE * 0.7f) {
                    releaseCharge(false);
                    aiIsCharging = false;
                }
            }
            return;
        }

        // 击球决策
        if (aiState == 2 || aiState == 3) {
            float distToBall = Math.abs(ballX - player2X);
            boolean canHit = checkHitConnection(false);

            if (canHit && (now - lastHitTime2) > HIT_COOLDOWN) {
                if (!aiIsCharging) {
                    // 开始蓄力
                    startCharging(false);
                    aiIsCharging = true;
                    aiChargeStartTime = now;

                    // 根据目标力量决定蓄力类型
                    if (aiTargetPower > 0.8f && player2Jumping) {
                        isSmashCharge2 = true;
                    }
                } else {
                    // 检查是否达到目标蓄力
                    long chargeTime = now - aiChargeStartTime;
                    long maxTime = isSmashCharge2 ? SMASH_CHARGE_TIME :
                            (isServeCharge2 ? MAX_CHARGE_TIME_SERVE : MAX_CHARGE_TIME_HIT);
                    float currentPower = (float)chargeTime / maxTime;

                    if (currentPower >= aiTargetPower || chargeTime > maxTime * 0.9f) {
                        releaseCharge(false);
                        aiIsCharging = false;
                        isSmashCharge2 = false;
                    }
                }
            }
        } else {
            // 不在击球状态，取消蓄力
            if (aiIsCharging) {
                cancelCharge(false);
                aiIsCharging = false;
                isSmashCharge2 = false;
            }
        }
    }

    /**
     * AI 发球处理
     */
    private void handleAIServe() {
        // AI 发球策略
        aiTargetPower = 0.6f + random.nextFloat() * 0.3f;
    }

    private boolean checkHitConnection(boolean isPlayer1) {
        float playerX = isPlayer1 ? player1X : player2X;
        float playerY = isPlayer1 ? player1Y : player2Y;

        float racketX = isPlayer1 ? playerX + 40 : playerX - 40;
        float racketY = playerY - 35;

        float ballCenterX = ballX + BALL_SIZE / 2;
        float ballCenterY = ballY + BALL_SIZE / 2;

        float dx = ballCenterX - racketX;
        float dy = ballCenterY - racketY;
        float distance = (float)Math.sqrt(dx * dx + dy * dy);

        float hitRange = HIT_RADIUS;
        if ((isPlayer1 && player1Jumping) || (!isPlayer1 && player2Jumping)) {
            hitRange += 20;
        }
        if (isPlayer1 && player1Dash || !isPlayer1 && player2Dash) {
            hitRange += 10;
        }

        boolean onCorrectSide = isPlayer1 ? (ballX < NET_X) : (ballX > NET_X);

        return distance < hitRange && onCorrectSide;
    }

    private boolean checkBallNetCollision() {
        float ballLeft = ballX;
        float ballRight = ballX + BALL_SIZE;
        float ballTop = ballY;
        float ballBottom = ballY + BALL_SIZE;

        return ballRight > NET_COLLISION_LEFT &&
                ballLeft < NET_COLLISION_RIGHT &&
                ballBottom > NET_COLLISION_TOP &&
                ballTop < NET_COLLISION_BOTTOM;
    }

    private void handleNetCollision() {
        float ballCenterX = ballX + BALL_SIZE / 2;

        if (ballCenterX < NET_X) {
            ballX = NET_COLLISION_LEFT - BALL_SIZE;
        } else {
            ballX = NET_COLLISION_RIGHT;
        }

        ballSpeedX = -ballSpeedX * NET_BOUNCE_LOSS;
        ballSpeedY = ballSpeedY * 0.8f;
        ballSpin *= 0.5f;

        addExplosion(ballX + BALL_SIZE/2, ballY + BALL_SIZE/2, new Color(255, 100, 100), 25);

        server = (server == 1) ? 2 : 1;
    }

    public void startCharging(boolean isPlayer1) {
        long now = System.currentTimeMillis();

        boolean canSmash = isPlayer1 ? player1Jumping : player2Jumping;

        if (!ballInPlay) {
            if (isPlayer1 && server == 1 && !charging1) {
                charging1 = true;
                isServeCharge1 = true;
                chargeStartTime1 = now;
                power1 = 0;
            } else if (!isPlayer1 && server == 2 && !charging2) {
                charging2 = true;
                isServeCharge2 = true;
                chargeStartTime2 = now;
                power2 = 0;
            }
        } else {
            if (isPlayer1 && !charging1) {
                charging1 = true;
                isServeCharge1 = false;
                isSmashCharge1 = canSmash && player1Energy >= 30;
                chargeStartTime1 = now;
                power1 = 0;
                if (isSmashCharge1) {
                    addGlowEffect(player1X + 35, player1Y - 30, 40, new Color(255, 100, 0));
                } else {
                    addGlowEffect(player1X + 35, player1Y - 30, 30, new Color(255, 200, 0));
                }
            } else if (!isPlayer1 && !charging2) {
                charging2 = true;
                isServeCharge2 = false;
                isSmashCharge2 = canSmash && player2Energy >= 30;
                chargeStartTime2 = now;
                power2 = 0;
                if (isSmashCharge2) {
                    addGlowEffect(player2X - 35, player2Y - 30, 40, new Color(255, 100, 0));
                } else {
                    addGlowEffect(player2X - 35, player2Y - 30, 30, new Color(255, 200, 0));
                }
            }
        }
    }

    public void cancelCharge(boolean isPlayer1) {
        if (isPlayer1 && charging1) {
            charging1 = false;
            isServeCharge1 = false;
            isSmashCharge1 = false;
            power1 = 0;
        } else if (!isPlayer1 && charging2) {
            charging2 = false;
            isServeCharge2 = false;
            isSmashCharge2 = false;
            power2 = 0;
        }
    }

    public void releaseCharge(boolean isPlayer1) {
        long now = System.currentTimeMillis();

        if (!ballInPlay) {
            if (isPlayer1 && charging1 && server == 1) {
                long maxChargeTime = MAX_CHARGE_TIME_SERVE;
                long chargeTime = Math.min(maxChargeTime, now - chargeStartTime1);
                float powerFactor = (float)chargeTime / maxChargeTime;
                if (powerFactor < 0.1f) powerFactor = 0.1f;
                power1 = MIN_POWER + (MAX_POWER - MIN_POWER) * powerFactor;

                serve(true, power1);
                charging1 = false;
                isServeCharge1 = false;
                lastSwingTime1 = now;
                lastHitTime1 = now;
                lastShotType = "SERVE";

                addBigExplosion(ballX, ballY, new Color(255, 200, 0), 30);
                playServeSound();

            } else if (!isPlayer1 && charging2 && server == 2) {
                long maxChargeTime = MAX_CHARGE_TIME_SERVE;
                long chargeTime = Math.min(maxChargeTime, now - chargeStartTime2);
                float powerFactor = (float)chargeTime / maxChargeTime;
                if (powerFactor < 0.1f) powerFactor = 0.1f;
                power2 = MIN_POWER + (MAX_POWER - MIN_POWER) * powerFactor;

                serve(false, power2);
                charging2 = false;
                isServeCharge2 = false;
                lastSwingTime2 = now;
                lastHitTime2 = now;
                lastShotType = "SERVE";

                addBigExplosion(ballX, ballY, new Color(255, 200, 0), 30);
                playServeSound();

                // AI 重置状态
                aiIsCharging = false;
            } else {
                swingMiss(isPlayer1);
            }
        } else {
            if (isPlayer1 && charging1) {
                lastSwingTime1 = now;
                addSwingTrail(true);

                if (checkHitConnection(true) && (now - lastHitTime1) > HIT_COOLDOWN) {
                    long maxChargeTime = isSmashCharge1 ? SMASH_CHARGE_TIME : MAX_CHARGE_TIME_HIT;
                    long chargeTime = Math.min(maxChargeTime, now - chargeStartTime1);
                    float powerFactor = (float)chargeTime / maxChargeTime;
                    if (powerFactor < 0.1f) powerFactor = 0.1f;
                    power1 = MIN_POWER + (MAX_POWER - MIN_POWER) * powerFactor;

                    if (isSmashCharge1) {
                        player1Energy = Math.max(0, player1Energy - 30);
                        bulletTime = true;
                        bulletTimeEnd = now + 500;
                        isSmash = true;
                        lastShotType = "SMASH";
                    } else {
                        isSmash = false;
                        if (powerFactor > 0.7f) {
                            isClear = true;
                            isDropShot = false;
                            lastShotType = "CLEAR";
                        } else if (powerFactor < 0.3f) {
                            isDropShot = true;
                            isClear = false;
                            lastShotType = "DROP";
                        } else {
                            isClear = false;
                            isDropShot = false;
                            lastShotType = "DRIVE";
                        }
                    }

                    hitBall(true, power1);
                    lastHitTime1 = now;

                    updateCombo();

                    addHitEffect(ballX, ballY, isSmash ? new Color(255, 100, 0) : new Color(70, 130, 200), powerFactor);
                    
                    // 播放击球音效
                    if (isSmash) {
                        playSmashSound();
                    } else {
                        playHitSound();
                    }
                } else {
                    addSwingMissEffect(true);
                }

                charging1 = false;
                isServeCharge1 = false;
                isSmashCharge1 = false;

            } else if (!isPlayer1 && charging2) {
                lastSwingTime2 = now;
                addSwingTrail(false);

                if (checkHitConnection(false) && (now - lastHitTime2) > HIT_COOLDOWN) {
                    long maxChargeTime = isSmashCharge2 ? SMASH_CHARGE_TIME : MAX_CHARGE_TIME_HIT;
                    long chargeTime = Math.min(maxChargeTime, now - chargeStartTime2);
                    float powerFactor = (float)chargeTime / maxChargeTime;
                    if (powerFactor < 0.1f) powerFactor = 0.1f;
                    power2 = MIN_POWER + (MAX_POWER - MIN_POWER) * powerFactor;

                    if (isSmashCharge2) {
                        player2Energy = Math.max(0, player2Energy - 30);
                        bulletTime = true;
                        bulletTimeEnd = now + 500;
                        isSmash = true;
                        lastShotType = "SMASH";
                    } else {
                        isSmash = false;
                        if (powerFactor > 0.7f) {
                            isClear = true;
                            isDropShot = false;
                            lastShotType = "CLEAR";
                        } else if (powerFactor < 0.3f) {
                            isDropShot = true;
                            isClear = false;
                            lastShotType = "DROP";
                        } else {
                            isClear = false;
                            isDropShot = false;
                            lastShotType = "DRIVE";
                        }
                    }

                    hitBall(false, power2);
                    lastHitTime2 = now;

                    updateCombo();

                    addHitEffect(ballX, ballY, isSmash ? new Color(255, 100, 0) : new Color(200, 70, 70), powerFactor);
                    
                    // 播放击球音效
                    if (isSmash) {
                        playSmashSound();
                    } else {
                        playHitSound();
                    }
                } else {
                    addSwingMissEffect(false);
                }

                charging2 = false;
                isServeCharge2 = false;
                isSmashCharge2 = false;

                // AI 重置状态
                aiIsCharging = false;
            }
        }
    }

    private void updateCombo() {
        long now = System.currentTimeMillis();
        if (now - lastHitTime < COMBO_TIMEOUT) {
            comboCount++;
            if (comboCount > maxCombo) {
                maxCombo = comboCount;
            }
        } else {
            comboCount = 1;
        }
        lastHitTime = now;
    }

    public void useDash(boolean isPlayer1) {
        long now = System.currentTimeMillis();
        if (isPlayer1 && player1Energy >= 20 && !player1Dash) {
            player1Energy -= 20;
            player1Dash = true;
            dashEndTime1 = now + 200;

            if (ballX > player1X) {
                player1X = Math.min(PLAYER1_MAX_X, player1X + 80);
            } else {
                player1X = Math.max(LEFT_BOUNDARY, player1X - 80);
            }

            addDashEffect(player1X, player1Y, true);
            playDashSound();
        } else if (!isPlayer1 && player2Energy >= 20 && !player2Dash) {
            player2Energy -= 20;
            player2Dash = true;
            dashEndTime2 = now + 200;

            if (ballX > player2X) {
                player2X = Math.min(RIGHT_BOUNDARY, player2X + 80);
            } else {
                player2X = Math.max(PLAYER2_MIN_X, player2X - 80);
            }

            addDashEffect(player2X, player2Y, false);
            playDashSound();
        }
    }

    private void addDashEffect(float x, float y, boolean isLeft) {
        Color dashColor = isLeft ? new Color(70, 130, 200) : new Color(200, 70, 70);
        for (int i = 0; i < 20; i++) {
            particles.add(new Particle(x + random.nextInt(40) - 20, y + random.nextInt(40) - 20,
                    random.nextFloat() * 6 - 3, random.nextFloat() * 4 - 6,
                    dashColor));
        }
        for (int i = 0; i < 10; i++) {
            sparkles.add(new Sparkle(x, y - 20,
                    random.nextFloat() * 8 - 4, random.nextFloat() * 8 - 4,
                    dashColor, 12));
        }
    }

    private void addGlowEffect(float x, float y, int radius, Color color) {
        for (int i = 0; i < 12; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            float vx = (float)(Math.cos(angle) * 2);
            float vy = (float)(Math.sin(angle) * 2);
            sparkles.add(new Sparkle(x, y, vx, vy,
                    new Color(color.getRed(), color.getGreen(), color.getBlue(), 100), radius));
        }
    }

    private void addSwingTrail(boolean isPlayer1) {
        float x = isPlayer1 ? player1X + 40 : player2X - 40;
        float y = isPlayer1 ? player1Y - 35 : player2Y - 35;
        Color trailColor = isPlayer1 ? new Color(70, 130, 200, 150) : new Color(200, 70, 70, 150);
        for (int i = 0; i < 15; i++) {
            sparkles.add(new Sparkle(x + random.nextInt(30) - 15, y + random.nextInt(30) - 15,
                    random.nextFloat() * 4 - 2, random.nextFloat() * 4 - 2,
                    trailColor, 8));
        }
    }

    private void addSwingMissEffect(boolean isPlayer1) {
        float x = isPlayer1 ? player1X + 40 : player2X - 40;
        float y = isPlayer1 ? player1Y - 35 : player2Y - 35;
        for (int i = 0; i < 12; i++) {
            sparkles.add(new Sparkle(x + random.nextInt(20) - 10, y + random.nextInt(20) - 10,
                    random.nextFloat() * 3 - 1.5f, random.nextFloat() * 3 - 1.5f,
                    new Color(150, 150, 150, 120), 5));
        }
    }

    private void addHitEffect(float x, float y, Color color, float power) {
        int count = 25 + (int)(power * 35);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            float speed = random.nextFloat() * 6 + 2;
            float vx = (float)(Math.cos(angle) * speed);
            float vy = (float)(Math.sin(angle) * speed);
            particles.add(new Particle(x + BALL_SIZE/2, y + BALL_SIZE/2, vx, vy, color));
        }

        for (int i = 0; i < 8; i++) {
            sparkles.add(new Sparkle(x + BALL_SIZE/2, y + BALL_SIZE/2,
                    random.nextFloat() * 5 - 2.5f, random.nextFloat() * 5 - 2.5f,
                    Color.WHITE, 12));
        }

        if (isSmash) {
            smashWaves.add(new SmashWave(x + BALL_SIZE/2, y + BALL_SIZE/2));
        }
    }

    private void swingMiss(boolean isPlayer1) {
        long now = System.currentTimeMillis();
        if (isPlayer1) {
            lastSwingTime1 = now;
            addSwingMissEffect(true);
        } else {
            lastSwingTime2 = now;
            addSwingMissEffect(false);
        }
    }

    private void serve(boolean isPlayer1, float power) {
        ballInPlay = true;
        ballFollowing1 = false;
        ballFollowing2 = false;

        if (isPlayer1) {
            ballSpeedX = power * 0.85f;
            ballSpeedY = -power * 0.55f;
        } else {
            ballSpeedX = -power * 0.85f;
            ballSpeedY = -power * 0.55f;
        }

        ballSpin = random.nextFloat() * 4 - 2;
        addBigExplosion(ballX, ballY, new Color(255, 215, 0), 35);
    }

    private void hitBall(boolean isPlayer1, float power) {
        float playerYPos = isPlayer1 ? player1Y : player2Y;
        float hitHeight = ballY - (playerYPos - 30);
        float hitAngle = Math.max(-1.8f, Math.min(1.8f, hitHeight / 35));

        float baseSpeed = power;

        if (isSmash) {
            baseSpeed *= 1.4f;
            ballSpin = 8;
        } else if (isClear) {
            hitAngle = -1.0f;
            ballSpin = 3;
        } else if (isDropShot) {
            baseSpeed *= 0.6f;
            ballSpin = 5;
        }

        if (isPlayer1) {
            ballSpeedX = baseSpeed * 0.95f;
            ballSpeedY = hitAngle * 8 - 5;
            server = 2;
        } else {
            ballSpeedX = -baseSpeed * 0.95f;
            ballSpeedY = hitAngle * 8 - 5;
            server = 1;
        }

        if (ballSpeedY > -2 && power > MIN_POWER) {
            ballSpeedY = -4;
        }

        ballSpin += random.nextFloat() * 4 - 2;
    }

    public void jump(boolean isPlayer1) {
        if (isPlayer1 && !player1Jumping) {
            player1Vy = -JUMP_SPEED;
            player1Jumping = true;
        } else if (!isPlayer1 && !player2Jumping) {
            player2Vy = -JUMP_SPEED;
            player2Jumping = true;
        }
    }

    public void update() {
        if (!gameRunning) return;

        long now = System.currentTimeMillis();

        if (bulletTime) {
            if (now > bulletTimeEnd) {
                bulletTime = false;
            }
            timeScale = bulletTime ? 0.3f : 1.0f;
        }

        if (weatherEnabled && now - lastWeatherChange > WEATHER_INTERVAL) {
            windPower = random.nextFloat() * 4;
            windDirection = random.nextFloat() * (float)Math.PI * 2;
            lastWeatherChange = now;
        }

        player1Energy = Math.min(100, player1Energy + 0.1f);
        player2Energy = Math.min(100, player2Energy + 0.1f);

        if (player1Dash && now > dashEndTime1) {
            player1Dash = false;
        }
        if (player2Dash && now > dashEndTime2) {
            player2Dash = false;
        }

        updatePlayerPhysics();
        updateAI();

        if (ballFollowing1) {
            ballX = player1X + 30;
            ballY = player1Y - 40;
        } else if (ballFollowing2) {
            ballX = player2X - 30;
            ballY = player2Y - 40;
        }

        if (ballInPlay) {
            applyPhysics();

            if (weatherEnabled) {
                ballSpeedX += Math.cos(windDirection) * windPower * 0.01f;
                ballSpeedY += Math.sin(windDirection) * windPower * 0.01f;
            }

            if (ballSpin != 0) {
                ballSpeedY += ballSpin * 0.05f;
                ballSpin *= 0.99f;
            }

            ballX += ballSpeedX * timeScale;
            ballY += ballSpeedY * timeScale;

            maintainMinimumSpeed();

            if (checkBallNetCollision()) {
                handleNetCollision();
            }

            handleBoundaryCollision();
            checkScoring();

            if (random.nextInt(2) == 0) {
                trails.add(new TrailEffect(ballX, ballY));
            }
            if (random.nextInt(3) == 0) {
                Color trailColor = isSmash ? new Color(255, 100, 0, 100) : new Color(255, 255, 255, 100);
                particles.add(new Particle(ballX + BALL_SIZE/2, ballY + BALL_SIZE/2,
                        -ballSpeedX * 0.3f, -ballSpeedY * 0.3f, trailColor));
            }
        }

        if (now - lastHitTime > COMBO_TIMEOUT) {
            comboCount = 0;
        }

        updateChargingEffects();
        updateEffects();
        updateStickMen();
    }

    private void updatePlayerPhysics() {
        if (player1Jumping) {
            player1Y += player1Vy;
            player1Vy += JUMP_GRAVITY;

            if (player1Y >= GROUND_Y - 50) {
                player1Y = GROUND_Y - 50;
                player1Jumping = false;
                player1Vy = 0;
            }
        }

        if (player2Jumping) {
            player2Y += player2Vy;
            player2Vy += JUMP_GRAVITY;

            if (player2Y >= GROUND_Y - 50) {
                player2Y = GROUND_Y - 50;
                player2Jumping = false;
                player2Vy = 0;
            }
        }
    }

    private void applyPhysics() {
        ballSpeedY += GRAVITY;
        ballSpeedX *= AIR_RESISTANCE;
        ballSpeedY *= AIR_RESISTANCE;
    }

    private void maintainMinimumSpeed() {
        float currentSpeed = (float)Math.sqrt(ballSpeedX * ballSpeedX + ballSpeedY * ballSpeedY);
        if (currentSpeed < MIN_SPEED && currentSpeed > 0 && ballInPlay) {
            float ratio = MIN_SPEED / currentSpeed;
            ballSpeedX *= ratio;
            ballSpeedY *= ratio;
        }
    }

    private void handleBoundaryCollision() {
        if (ballY <= 30) {
            ballY = 31;
            ballSpeedY = -ballSpeedY * 0.7f;
            addExplosion(ballX, ballY, Color.WHITE, 12);
        }

        if (ballX <= 20) {
            score2 += (comboCount > 1) ? 2 : 1;
            if (score2 >= WIN_SCORE) gameRunning = false;
            server = 2;
            addBigExplosion(ballX, ballY, new Color(139, 69, 19), 30);
            playScoreSound();
            resetAfterPoint();
        } else if (ballX >= WIDTH - BALL_SIZE - 20) {
            score1 += (comboCount > 1) ? 2 : 1;
            if (score1 >= WIN_SCORE) gameRunning = false;
            server = 1;
            addBigExplosion(ballX, ballY, new Color(139, 69, 19), 30);
            playScoreSound();
            resetAfterPoint();
        }
    }

    private void checkScoring() {
        if (ballY >= GROUND_Y - BALL_SIZE - 5) {
            if (ballX < NET_X) {
                score2 += (comboCount > 1) ? 2 : 1;
                if (score2 >= WIN_SCORE) gameRunning = false;
                server = 2;
            } else {
                score1 += (comboCount > 1) ? 2 : 1;
                if (score1 >= WIN_SCORE) gameRunning = false;
                server = 1;
            }

            addBigExplosion(ballX, ballY, new Color(139, 69, 19), 30);
            resetAfterPoint();
        }
    }

    private void resetAfterPoint() {
        ballInPlay = false;
        lastHitTime1 = 0;
        lastHitTime2 = 0;
        comboCount = 0;
        bulletTime = false;
        timeScale = 1.0f;
        aiState = 0;
        aiIsCharging = false;

        if (server == 1) {
            ballFollowing1 = true;
            ballFollowing2 = false;
        } else {
            ballFollowing1 = false;
            ballFollowing2 = true;
        }
        resetBall();
    }

    private void updateChargingEffects() {
        if (charging1) {
            long maxChargeTime = isServeCharge1 ? MAX_CHARGE_TIME_SERVE :
                    (isSmashCharge1 ? SMASH_CHARGE_TIME : MAX_CHARGE_TIME_HIT);
            long chargeTime = Math.min(maxChargeTime, System.currentTimeMillis() - chargeStartTime1);
            float progress = (float)chargeTime / maxChargeTime;

            Color chargeColor = isSmashCharge1 ? new Color(255, 100, 0) : new Color(255, 200, 100);

            if (random.nextInt(2) == 0) {
                float x = player1X + 35 + random.nextInt(40) - 20;
                float y = player1Y - 35 + random.nextInt(40) - 20;
                particles.add(new Particle(x, y, random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1,
                        new Color(chargeColor.getRed(), chargeColor.getGreen(), chargeColor.getBlue(), 150)));
            }
            if (random.nextInt(3) == 0) {
                sparkles.add(new Sparkle(player1X + 35, player1Y - 35,
                        random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1,
                        new Color(chargeColor.getRed(), chargeColor.getGreen(), chargeColor.getBlue(), 80),
                        15 + (int)(progress * 25)));
            }
        }

        if (charging2) {
            long maxChargeTime = isServeCharge2 ? MAX_CHARGE_TIME_SERVE :
                    (isSmashCharge2 ? SMASH_CHARGE_TIME : MAX_CHARGE_TIME_HIT);
            long chargeTime = Math.min(maxChargeTime, System.currentTimeMillis() - chargeStartTime2);
            float progress = (float)chargeTime / maxChargeTime;

            Color chargeColor = isSmashCharge2 ? new Color(255, 100, 0) : new Color(255, 200, 100);

            if (random.nextInt(2) == 0) {
                float x = player2X - 35 + random.nextInt(40) - 20;
                float y = player2Y - 35 + random.nextInt(40) - 20;
                particles.add(new Particle(x, y, random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1,
                        new Color(chargeColor.getRed(), chargeColor.getGreen(), chargeColor.getBlue(), 150)));
            }
            if (random.nextInt(3) == 0) {
                sparkles.add(new Sparkle(player2X - 35, player2Y - 35,
                        random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1,
                        new Color(chargeColor.getRed(), chargeColor.getGreen(), chargeColor.getBlue(), 80),
                        15 + (int)(progress * 25)));
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
            stickMen.get(1).isCharging = charging2 || aiIsCharging;
            stickMen.get(0).isJumping = player1Jumping;
            stickMen.get(1).isJumping = player2Jumping;
            stickMen.get(0).isDashing = player1Dash;
            stickMen.get(1).isDashing = player2Dash;
            stickMen.get(0).chargeProgress = getPower1();
            stickMen.get(1).chargeProgress = getPower2();
            stickMen.get(0).isSmashCharging = isSmashCharge1;
            stickMen.get(1).isSmashCharging = isSmashCharge2;
        }
    }

    private void updateEffects() {
        particles.removeIf(p -> !p.update());
        trails.removeIf(t -> !t.update());
        sparkles.removeIf(s -> !s.update());
        smashWaves.removeIf(w -> !w.update());
    }

    public void addExplosion(float x, float y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            float speed = random.nextFloat() * 4 + 1;
            float vx = (float)(Math.cos(angle) * speed);
            float vy = (float)(Math.sin(angle) * speed);
            particles.add(new Particle(x, y, vx, vy, color));
        }
    }

    public void addBigExplosion(float x, float y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            float speed = random.nextFloat() * 6 + 2;
            float vx = (float)(Math.cos(angle) * speed);
            float vy = (float)(Math.sin(angle) * speed);
            particles.add(new Particle(x + BALL_SIZE/2, y + BALL_SIZE/2, vx, vy, color));
        }
        for (int i = 0; i < 10; i++) {
            sparkles.add(new Sparkle(x + BALL_SIZE/2, y + BALL_SIZE/2,
                    random.nextFloat() * 4 - 2, random.nextFloat() * 4 - 2,
                    Color.WHITE, 10));
        }
    }

    public void movePlayer1(boolean right) {
        float speed = player1Dash ? PLAYER_SPEED * 1.5f : PLAYER_SPEED;
        if (right) {
            player1X = Math.min(PLAYER1_MAX_X, player1X + speed);
        } else {
            player1X = Math.max(LEFT_BOUNDARY, player1X - speed);
        }
        if (ballFollowing1) {
            ballX = player1X + 30;
        }
    }

    public void movePlayer2(boolean right) {
        if (aiEnabled) return;

        float speed = player2Dash ? PLAYER_SPEED * 1.5f : PLAYER_SPEED;
        if (right) {
            player2X = Math.min(RIGHT_BOUNDARY, player2X + speed);
        } else {
            player2X = Math.max(PLAYER2_MIN_X, player2X - speed);
        }
        if (ballFollowing2) {
            ballX = player2X - 30;
        }
    }

    // Getters
    public float getPlayer1X() { return player1X; }
    public float getPlayer1Y() { return player1Y; }
    public float getPlayer2X() { return player2X; }
    public float getPlayer2Y() { return player2Y; }
    public float getBallX() { return ballX; }
    public float getBallY() { return ballY; }
    public float getBallSpin() { return ballSpin; }
    public int getScore1() { return score1; }
    public int getScore2() { return score2; }
    public int getServer() { return server; }
    public boolean isGameRunning() { return gameRunning; }
    public boolean isBallInPlay() { return ballInPlay; }
    public int getComboCount() { return comboCount; }
    public int getMaxCombo() { return maxCombo; }
    public float getPlayer1Energy() { return player1Energy; }
    public float getPlayer2Energy() { return player2Energy; }
    public boolean isBulletTime() { return bulletTime; }
    public float getWindPower() { return windPower; }
    public float getWindDirection() { return windDirection; }
    public boolean isWeatherEnabled() { return weatherEnabled; }
    public String getLastShotType() { return lastShotType; }
    public boolean isAIMode() { return aiEnabled; }
    public int getAIState() { return aiState; }

    public float getPower1() {
        if (charging1) {
            long maxChargeTime = isServeCharge1 ? MAX_CHARGE_TIME_SERVE :
                    (isSmashCharge1 ? SMASH_CHARGE_TIME : MAX_CHARGE_TIME_HIT);
            long chargeTime = Math.min(maxChargeTime, System.currentTimeMillis() - chargeStartTime1);
            return (float)chargeTime / maxChargeTime;
        }
        return 0;
    }

    public float getPower2() {
        if (charging2) {
            long maxChargeTime = isServeCharge2 ? MAX_CHARGE_TIME_SERVE :
                    (isSmashCharge2 ? SMASH_CHARGE_TIME : MAX_CHARGE_TIME_HIT);
            long chargeTime = Math.min(maxChargeTime, System.currentTimeMillis() - chargeStartTime2);
            return (float)chargeTime / maxChargeTime;
        }
        return 0;
    }

    public boolean isCharging1() { return charging1; }
    public boolean isCharging2() { return charging2; }
    public boolean isServeCharge1() { return isServeCharge1; }
    public boolean isServeCharge2() { return isServeCharge2; }
    public boolean isSmashCharge1() { return isSmashCharge1; }
    public boolean isSmashCharge2() { return isSmashCharge2; }

    public CopyOnWriteArrayList<Particle> getParticles() { return particles; }
    public CopyOnWriteArrayList<TrailEffect> getTrails() { return trails; }
    public CopyOnWriteArrayList<Sparkle> getSparkles() { return sparkles; }
    public CopyOnWriteArrayList<SmashWave> getSmashWaves() { return smashWaves; }
    public ArrayList<StickMan> getStickMen() { return stickMen; }

    public static int getNetCollisionLeft() { return NET_COLLISION_LEFT; }
    public static int getNetCollisionRight() { return NET_COLLISION_RIGHT; }
    public static int getNetCollisionTop() { return NET_COLLISION_TOP; }
    public static int getNetCollisionBottom() { return NET_COLLISION_BOTTOM; }

    // 内部类
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
            life = maxLife = 20 + random.nextInt(20);
        }

        public boolean update() {
            x += vx;
            y += vy;
            vy += 0.1f;
            vx *= 0.99f;
            life--;
            return life > 0;
        }

        public float getAlpha() {
            return (float)life / maxLife;
        }
    }

    public class TrailEffect {
        public float x, y;
        public int life = 15;

        public TrailEffect(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public boolean update() {
            life--;
            return life > 0;
        }

        public float getAlpha() {
            return (float)life / 15;
        }
    }

    public class Sparkle {
        public float x, y, vx, vy;
        public Color color;
        public int life, maxLife;
        public int size;

        public Sparkle(float x, float y, float vx, float vy, Color color, int size) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.size = size;
            life = maxLife = 10 + random.nextInt(10);
        }

        public boolean update() {
            x += vx;
            y += vy;
            vx *= 0.95f;
            vy *= 0.95f;
            life--;
            return life > 0;
        }

        public float getAlpha() {
            return (float)life / maxLife;
        }
    }

    public class SmashWave {
        public float x, y;
        public float radius = 0;
        public int life = 30;

        public SmashWave(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public boolean update() {
            radius += 8;
            life--;
            return life > 0;
        }

        public float getAlpha() {
            return (float)life / 30;
        }
    }

    public class StickMan {
        public float x, y;
        public boolean isLeft;
        public boolean isSwinging = false;
        public float swingProgress = 0;
        public boolean isCharging = false;
        public boolean isJumping = false;
        public boolean isDashing = false;
        public float chargeProgress = 0;
        public boolean isSmashCharging = false;

        public StickMan(float x, float y, boolean isLeft) {
            this.x = x;
            this.y = y;
            this.isLeft = isLeft;
        }
    }
}