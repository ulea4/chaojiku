package game;

import util.Constants;
import entity.Player;
import entity.BadmintonBall;
import ui.ScoreBoard;
import ui.MainMenu;
import ui.SettingsPanel;
import audio.AudioManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener {
    private GameManager gameManager;
    private GameState currentState;
    private GameMode currentMode;

    // 游戏对象
    private Player player1;
    private Player player2;
    private BadmintonBall ball;
    private ScoreBoard scoreBoard;
    private MainMenu mainMenu;
    private SettingsPanel settingsPanel;

    // 游戏控制
    private Timer gameTimer;
    private boolean isServing = true;
    private int servePlayer = 1;
    private boolean canHit = false;
    private boolean gameStarted = false;

    // 时间挑战模式
    private int timeRemaining = 60;
    private Timer timeChallengeTimer;

    // 练习模式
    private boolean showAimingGuide = false;

    // 特效
    private java.util.List<Particle> particles = new ArrayList<>();
    private Random random = new Random();

    private class Particle {
        int x, y;
        int vx, vy;
        int life;
        Color color;

        Particle(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.vx = random.nextInt(10) - 5;
            this.vy = random.nextInt(10) - 5;
            this.life = 30;
            this.color = color;
        }

        void update() {
            x += vx;
            y += vy;
            life--;
        }

        void draw(Graphics2D g) {
            if (life > 0) {
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), life * 8));
                g.fillOval(x - 2, y - 2, 4, 4);
            }
        }

        boolean isAlive() { return life > 0; }
    }

    public GamePanel(GameManager manager) {
        this.gameManager = manager;
        this.currentState = GameState.MAIN_MENU;
        this.currentMode = GameMode.VERSUS;

        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.COLOR_SKY);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        initGame();

        gameTimer = new Timer(Constants.FRAME_DELAY, this);
        gameTimer.start();
    }

    private void initGame() {
        player1 = new Player(100, Constants.GROUND_Y, true, "Player 1");
        player2 = new Player(Constants.WINDOW_WIDTH - 200, Constants.GROUND_Y, false, "Player 2");
        ball = new BadmintonBall(Constants.WINDOW_WIDTH / 2, Constants.GROUND_Y - 100);
        scoreBoard = new ScoreBoard();
        mainMenu = new MainMenu();
        settingsPanel = new SettingsPanel();

        resetForServe();
    }

    private void resetGame() {
        player1.reset();
        player2.reset();
        ball.reset();
        scoreBoard.resetMatch();
        isServing = true;
        servePlayer = 1;
        canHit = false;
        gameStarted = true;
        particles.clear();

        resetForServe();

        if (currentMode == GameMode.TIME_ATTACK) {
            startTimeChallenge();
        }
    }

    private void resetForServe() {
        isServing = true;
        canHit = false;
        ball.setServing(true);
        ball.setServePlayer(servePlayer);
        ball.setPositionForServe(servePlayer, servePlayer == 1 ? player1 : player2);
    }

    private void startTimeChallenge() {
        timeRemaining = 60;
        if (timeChallengeTimer != null) {
            timeChallengeTimer.stop();
        }
        timeChallengeTimer = new Timer(1000, e -> {
            if (currentState == GameState.PLAYING && currentMode == GameMode.TIME_ATTACK) {
                timeRemaining--;
                if (timeRemaining <= 0) {
                    timeRemaining = 0;
                    gameOver();
                }
            }
        });
        timeChallengeTimer.start();
    }

    private void serveBall(int playerId) {
        isServing = false;
        canHit = true;
        ball.setServing(false);
        ball.serve(playerId, playerId == 1 ? player1 : player2);
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.HIT);
    }

    private void hitBall(int playerId) {
        Player hitter = (playerId == 1) ? player1 : player2;
        ball.hit(hitter);
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.HIT);

        addHitEffect(ball.getX(), ball.getY());

        if (hitter.getActivePowerUp() == Player.PowerUpType.POWER_SHOT) {
            addPowerEffect(ball.getX(), ball.getY());
        }
    }

    private void addHitEffect(int x, int y) {
        for (int i = 0; i < 10; i++) {
            particles.add(new Particle(x, y, Color.YELLOW));
        }
    }

    private void addPowerEffect(int x, int y) {
        for (int i = 0; i < 20; i++) {
            particles.add(new Particle(x, y, Color.RED));
        }
    }

    private void addScoreEffect(int x, int y) {
        for (int i = 0; i < 15; i++) {
            particles.add(new Particle(x, y, Color.GREEN));
        }
    }

    private void updateGame() {
        if (currentState != GameState.PLAYING) return;

        player1.update();
        player2.update();
        ball.update();

        particles.removeIf(p -> !p.isAlive());
        particles.forEach(Particle::update);

        checkCollisions();
        checkScore();

        if (scoreBoard.getWinner() != 0) {
            gameOver();
        }
    }

    private void checkCollisions() {
        if (canHit) {
            if (player1.canHitBall(ball)) {
                hitBall(1);
            } else if (player2.canHitBall(ball)) {
                hitBall(2);
            }
        }
    }

    private void checkScore() {
        if (ball.isOutOfBounds()) {
            int scorer = ball.getScoringPlayer();
            scoreBoard.addPoint(scorer);
            addScoreEffect(ball.getX(), ball.getY());
            AudioManager.getInstance().playSound(AudioManager.SoundEffect.SCORE);

            if (currentMode == GameMode.TOURNAMENT) {
                if (scoreBoard.checkSetWinner()) {
                    if (scoreBoard.getWinner() != 0) {
                        gameOver();
                        return;
                    }
                    scoreBoard.nextSet();
                }
            }

            servePlayer = scorer;
            resetForServe();
        }
    }

    private void gameOver() {
        currentState = GameState.GAME_OVER;
        gameStarted = false;
        AudioManager.getInstance().playSound(AudioManager.SoundEffect.VICTORY);

        if (timeChallengeTimer != null) {
            timeChallengeTimer.stop();
        }
    }

    private void drawCourt(Graphics2D g) {
        GradientPaint skyGradient = new GradientPaint(0, 0, Constants.COLOR_SKY,
                0, Constants.GROUND_Y, new Color(200, 230, 255));
        g.setPaint(skyGradient);
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.GROUND_Y);

        g.setColor(Constants.COLOR_GRASS);
        g.fillRect(0, Constants.GROUND_Y, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT - Constants.GROUND_Y);

        g.setColor(Constants.COLOR_COURT_LINE);
        g.setStroke(new BasicStroke(3));

        g.drawRect(Constants.LEFT_BOUNDARY - 10, Constants.GROUND_Y - 50,
                Constants.RIGHT_BOUNDARY - Constants.LEFT_BOUNDARY + 20, 100);

        g.drawLine(Constants.NET_X, Constants.GROUND_Y - 50, Constants.NET_X, Constants.GROUND_Y + 50);

        g.drawLine(Constants.LEFT_BOUNDARY + 100, Constants.GROUND_Y - 50,
                Constants.LEFT_BOUNDARY + 100, Constants.GROUND_Y + 50);
        g.drawLine(Constants.RIGHT_BOUNDARY - 100, Constants.GROUND_Y - 50,
                Constants.RIGHT_BOUNDARY - 100, Constants.GROUND_Y + 50);

        drawNet(g);
        drawAudience(g);
    }

    private void drawNet(Graphics2D g) {
        g.setColor(Constants.COLOR_NET);
        g.setStroke(new BasicStroke(2));

        int netTop = Constants.GROUND_Y - 80;
        int netBottom = Constants.GROUND_Y;

        for (int y = netTop; y < netBottom; y += 10) {
            g.drawLine(Constants.NET_X - 15, y, Constants.NET_X + 15, y);
        }

        g.setStroke(new BasicStroke(4));
        g.drawLine(Constants.NET_X - 20, netTop, Constants.NET_X + 20, netTop);
        g.drawLine(Constants.NET_X - 20, netBottom, Constants.NET_X + 20, netBottom);

        g.setColor(new Color(139, 69, 19));
        g.fillRect(Constants.NET_X - 25, netTop - 10, 10, netBottom - netTop + 20);
        g.fillRect(Constants.NET_X + 15, netTop - 10, 10, netBottom - netTop + 20);
    }

    private void drawAudience(Graphics2D g) {
        g.setColor(new Color(100, 100, 100));
        for (int i = 0; i < 20; i++) {
            int x = 20 + i * 60;
            if (x < Constants.NET_X - 50 || x > Constants.NET_X + 50) {
                g.fillOval(x, Constants.GROUND_Y + 10, 8, 8);
                g.fillOval(x + 3, Constants.GROUND_Y + 18, 5, 5);
            }
        }
    }

    private void drawUI(Graphics2D g) {
        scoreBoard.draw(g, player1, player2);

        if (currentMode == GameMode.TIME_ATTACK && currentState == GameState.PLAYING) {
            g.setFont(Constants.FONT_SCORE.deriveFont(30f));
            g.setColor(Color.WHITE);
            String timeText = "⏱ " + timeRemaining;
            FontMetrics fm = g.getFontMetrics();
            g.drawString(timeText, Constants.WINDOW_WIDTH - 120, 70);
        }

        if (currentMode == GameMode.PRACTICE && showAimingGuide) {
            drawAimingGuide(g);
        }

        if (isServing && currentState == GameState.PLAYING) {
            drawServeIndicator(g);
        }

        drawPowerUpIndicators(g);
    }

    private void drawAimingGuide(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 100));
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5, 5}, 0));

        int mouseX = getMousePosition() != null ? getMousePosition().x : Constants.WINDOW_WIDTH / 2;
        int mouseY = getMousePosition() != null ? getMousePosition().y : Constants.GROUND_Y - 100;

        g.drawLine(ball.getX(), ball.getY(), mouseX, mouseY);

        g.setColor(Color.RED);
        g.fillOval(mouseX - 5, mouseY - 5, 10, 10);
    }

    private void drawServeIndicator(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 200));
        g.setFont(Constants.FONT_MENU);
        String serveText = "🏸 Player " + servePlayer + " 发球 🏸";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(serveText);

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect((Constants.WINDOW_WIDTH - textWidth) / 2 - 20,
                Constants.WINDOW_HEIGHT - 60, textWidth + 40, 40, 10, 10);

        g.setColor(Color.YELLOW);
        g.drawString(serveText, (Constants.WINDOW_WIDTH - textWidth) / 2, Constants.WINDOW_HEIGHT - 35);
    }

    private void drawPowerUpIndicators(Graphics2D g) {
        drawPowerUpIndicator(g, player1, 10);
        drawPowerUpIndicator(g, player2, Constants.WINDOW_WIDTH - 110);
    }

    private void drawPowerUpIndicator(Graphics2D g, Player player, int x) {
        if (player.getActivePowerUp() != null) {
            g.setFont(Constants.FONT_SMALL);
            String powerUpText = "";
            Color color = Color.WHITE;

            switch (player.getActivePowerUp()) {
                case SPEED_BOOST:
                    powerUpText = "⚡ 加速";
                    color = Color.CYAN;
                    break;
                case POWER_SHOT:
                    powerUpText = "💥 强力击球";
                    color = Color.RED;
                    break;
                case SLOW_TIME:
                    powerUpText = "⏰ 时间减缓";
                    color = Color.MAGENTA;
                    break;
            }

            g.setColor(color);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(powerUpText, x, 50);
        }
    }

    private void drawGameStatus(Graphics2D g) {
        switch (currentState) {
            case MAIN_MENU:
                mainMenu.draw(g);
                break;
            case GAME_OVER:
                drawGameOver(g);
                break;
            case PAUSED:
                drawPauseMenu(g);
                break;
            case SETTINGS:
                settingsPanel.draw(g);
                break;
            default:
                break;
        }
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        g.setFont(Constants.FONT_TITLE);

        int winner = scoreBoard.getWinner();
        String winnerText;
        if (winner == 1) {
            winnerText = "🏆 Player 1 获胜！ 🏆";
            g.setColor(Constants.COLOR_PLAYER1);
        } else {
            winnerText = "🏆 Player 2 获胜！ 🏆";
            g.setColor(Constants.COLOR_PLAYER2);
        }

        FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth(winnerText);
        g.drawString(winnerText, (Constants.WINDOW_WIDTH - titleWidth) / 2, 250);

        g.setFont(Constants.FONT_SCORE);
        g.setColor(Color.WHITE);
        String finalScore = String.format("%d  -  %d", scoreBoard.getLeftScore(), scoreBoard.getRightScore());
        fm = g.getFontMetrics();
        int scoreWidth = fm.stringWidth(finalScore);
        g.drawString(finalScore, (Constants.WINDOW_WIDTH - scoreWidth) / 2, 350);

        g.setFont(Constants.FONT_MENU);
        g.setColor(Color.YELLOW);
        String restartText = "按 SPACE 返回主菜单";
        fm = g.getFontMetrics();
        int restartWidth = fm.stringWidth(restartText);
        g.drawString(restartText, (Constants.WINDOW_WIDTH - restartWidth) / 2, 500);
    }

    private void drawPauseMenu(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        g.setFont(Constants.FONT_TITLE);
        g.setColor(Color.WHITE);
        String pauseText = "⏸ 游戏暂停 ⏸";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(pauseText, (Constants.WINDOW_WIDTH - fm.stringWidth(pauseText)) / 2, 250);

        g.setFont(Constants.FONT_MENU);
        g.setColor(Color.YELLOW);
        String continueText = "按 P 或 ESC 继续游戏";
        fm = g.getFontMetrics();
        g.drawString(continueText, (Constants.WINDOW_WIDTH - fm.stringWidth(continueText)) / 2, 350);

        String menuText = "按 M 返回主菜单";
        g.drawString(menuText, (Constants.WINDOW_WIDTH - fm.stringWidth(menuText)) / 2, 420);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (currentState == GameState.PLAYING || currentState == GameState.PAUSED) {
            drawCourt(g2d);
            player1.draw(g2d);
            player2.draw(g2d);
            ball.draw(g2d);

            for (Particle p : particles) {
                p.draw(g2d);
            }

            drawUI(g2d);
        }

        drawGameStatus(g2d);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (currentState == GameState.MAIN_MENU) {
            handleMenuKeyPress(key);
            return;
        }

        if (currentState == GameState.GAME_OVER) {
            if (key == KeyEvent.VK_SPACE) {
                currentState = GameState.MAIN_MENU;
                initGame();
            }
            return;
        }

        if (currentState == GameState.PAUSED) {
            if (key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE) {
                currentState = GameState.PLAYING;
            } else if (key == KeyEvent.VK_M) {
                currentState = GameState.MAIN_MENU;
                initGame();
            }
            return;
        }

        if (currentState == GameState.SETTINGS) {
            settingsPanel.handleKeyPress(key);
            if (settingsPanel.isReturnSelected()) {
                currentState = GameState.MAIN_MENU;
            }
            return;
        }

        if (currentState != GameState.PLAYING) return;

        // 玩家1控制
        if (key == KeyEvent.VK_A) player1.setMovingLeft(true);
        if (key == KeyEvent.VK_D) player1.setMovingRight(true);
        if (key == KeyEvent.VK_W) {
            player1.jump();
            AudioManager.getInstance().playSound(AudioManager.SoundEffect.JUMP);
        }
        if (key == KeyEvent.VK_F) {
            player1.swingRacket();
            if (isServing && servePlayer == 1) {
                serveBall(1);
            }
        }

        // 玩家2控制
        if (key == KeyEvent.VK_LEFT) player2.setMovingLeft(true);
        if (key == KeyEvent.VK_RIGHT) player2.setMovingRight(true);
        if (key == KeyEvent.VK_UP) {
            player2.jump();
            AudioManager.getInstance().playSound(AudioManager.SoundEffect.JUMP);
        }
        if (key == KeyEvent.VK_DOWN) {
            player2.swingRacket();
            if (isServing && servePlayer == 2) {
                serveBall(2);
            }
        }

        if (key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE) {
            currentState = GameState.PAUSED;
        }

        if (currentMode == GameMode.PRACTICE && key == KeyEvent.VK_H) {
            showAimingGuide = !showAimingGuide;
        }
    }

    private void handleMenuKeyPress(int key) {
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) {
            int selected = mainMenu.getSelectedOption();
            if (mainMenu.isShowModeSelect()) {
                currentMode = mainMenu.getSelectedMode();
                mainMenu.setShowModeSelect(false);
            } else {
                switch (selected) {
                    case 0:
                        currentState = GameState.PLAYING;
                        resetGame();
                        break;
                    case 1:
                        mainMenu.setShowModeSelect(true);
                        break;
                    case 2:
                        currentState = GameState.SETTINGS;
                        break;
                    case 3:
                        System.exit(0);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_A) player1.setMovingLeft(false);
        if (key == KeyEvent.VK_D) player1.setMovingRight(false);
        if (key == KeyEvent.VK_LEFT) player2.setMovingLeft(false);
        if (key == KeyEvent.VK_RIGHT) player2.setMovingRight(false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (currentState == GameState.MAIN_MENU) {
            mainMenu.mouseClicked(e.getX(), e.getY());
            if (!mainMenu.isShowModeSelect() && mainMenu.getSelectedOption() == 0) {
                currentMode = mainMenu.getSelectedMode();
                currentState = GameState.PLAYING;
                resetGame();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (currentState == GameState.MAIN_MENU) {
            mainMenu.updateMousePosition(e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e) {}
}