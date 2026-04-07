package entity;

import util.Constants;
import java.awt.*;

public class Player extends GameObject {
    private boolean facingRight;
    private boolean isJumping = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean swinging = false;
    private int swingTimer = 0;
    private int racketX, racketY;
    private String playerName;
    private int score = 0;
    private int powerUpTimer = 0;
    private PowerUpType activePowerUp = null;

    private int animationFrame = 0;
    private int animationTimer = 0;
    private PlayerState state = PlayerState.IDLE;

    public enum PlayerState {
        IDLE, RUNNING, JUMPING, SWINGING
    }

    public enum PowerUpType {
        SPEED_BOOST, POWER_SHOT, SLOW_TIME
    }

    public Player(int x, int y, boolean facingRight, String playerName) {
        super(x, y, 40, 60);
        this.facingRight = facingRight;
        this.playerName = playerName;
        updateRacketPosition();
    }

    @Override
    public void update() {
        int currentSpeed = Constants.PLAYER_SPEED;
        if (activePowerUp == PowerUpType.SPEED_BOOST) {
            currentSpeed *= 1.5;
        }

        if (movingLeft && x > Constants.LEFT_BOUNDARY) {
            x -= currentSpeed;
            state = PlayerState.RUNNING;
            facingRight = false;
        } else if (movingRight && x < Constants.RIGHT_BOUNDARY - width) {
            x += currentSpeed;
            state = PlayerState.RUNNING;
            facingRight = true;
        } else {
            if (state != PlayerState.JUMPING && state != PlayerState.SWINGING) {
                state = PlayerState.IDLE;
            }
        }

        vy += Constants.GRAVITY;
        y += vy;

        if (y >= Constants.GROUND_Y) {
            y = Constants.GROUND_Y;
            vy = 0;
            isJumping = false;
            if (state == PlayerState.JUMPING) {
                state = PlayerState.IDLE;
            }
        }

        if (swinging) {
            swingTimer--;
            if (swingTimer <= 0) {
                swinging = false;
                if (state == PlayerState.SWINGING) {
                    state = PlayerState.IDLE;
                }
            }
        }

        updateAnimation();

        if (powerUpTimer > 0) {
            powerUpTimer--;
            if (powerUpTimer <= 0) {
                activePowerUp = null;
            }
        }

        updateRacketPosition();
    }

    private void updateAnimation() {
        animationTimer++;
        if (animationTimer >= 10) {
            animationTimer = 0;
            if (state == PlayerState.RUNNING) {
                animationFrame = (animationFrame + 1) % 8;
            } else {
                animationFrame = 0;
            }
        }
    }

    private void updateRacketPosition() {
        if (facingRight) {
            racketX = x + width;
            racketY = y + height / 2 - 10;
        } else {
            racketX = x - 20;
            racketY = y + height / 2 - 10;
        }
    }

    public void jump() {
        if (!isJumping) {
            vy = Constants.JUMP_POWER;
            isJumping = true;
            state = PlayerState.JUMPING;
        }
    }

    public void swingRacket() {
        swinging = true;
        swingTimer = 10;
        state = PlayerState.SWINGING;
    }

    public boolean canHitBall(BadmintonBall ball) {
        Rectangle racketRect = new Rectangle(racketX, racketY, 20, 20);
        Rectangle ballRect = ball.getBounds();
        return swinging && racketRect.intersects(ballRect);
    }

    public void addScore(int points) {
        score += points;
    }

    public void resetPosition() {
        if (facingRight) {
            x = 100;
        } else {
            x = Constants.RIGHT_BOUNDARY - width - 100;
        }
        y = Constants.GROUND_Y;
        vy = 0;
        isJumping = false;
        movingLeft = false;
        movingRight = false;
        swinging = false;
        state = PlayerState.IDLE;
    }

    public void reset() {
        resetPosition();
        score = 0;
        activePowerUp = null;
        powerUpTimer = 0;
    }

    public void applyPowerUp(PowerUpType powerUp) {
        this.activePowerUp = powerUp;
        this.powerUpTimer = 300;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(x + 5, y + height - 5, width - 10, 10);

        Color bodyColor = facingRight ? Constants.COLOR_PLAYER1 : Constants.COLOR_PLAYER2;
        if (activePowerUp != null) {
            bodyColor = bodyColor.brighter();
        }

        int bodyOffsetX = 0;
        if (state == PlayerState.RUNNING && animationFrame % 2 == 0) {
            bodyOffsetX = 2;
        }

        g.setColor(bodyColor);
        g.fillRect(x + bodyOffsetX, y, width, height);

        g.setColor(new Color(255, 200, 150));
        g.fillOval(x + 5, y - 20, 30, 30);

        g.setColor(Color.BLACK);
        if (facingRight) {
            g.fillOval(x + 20, y - 15, 5, 5);
            if (state == PlayerState.JUMPING) {
                g.fillOval(x + 28, y - 15, 3, 3);
            }
        } else {
            g.fillOval(x + 15, y - 15, 5, 5);
            if (state == PlayerState.JUMPING) {
                g.fillOval(x + 9, y - 15, 3, 3);
            }
        }

        if (state == PlayerState.JUMPING) {
            g.drawArc(x + 12, y - 10, 16, 10, 0, -180);
        } else if (state == PlayerState.SWINGING) {
            g.drawArc(x + 12, y - 10, 16, 10, 0, 180);
        }

        drawRacket(g);

        if (activePowerUp != null) {
            drawPowerUpEffect(g);
        }

        g.setColor(Color.WHITE);
        g.setFont(Constants.FONT_SMALL);
        FontMetrics fm = g.getFontMetrics();
        int nameWidth = fm.stringWidth(playerName);
        g.drawString(playerName, x + (width - nameWidth) / 2, y - 25);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        String scoreText = String.valueOf(score);
        fm = g.getFontMetrics();
        int scoreWidth = fm.stringWidth(scoreText);
        g.drawString(scoreText, x + (width - scoreWidth) / 2, y + height + 15);
    }

    private void drawRacket(Graphics2D g) {
        if (swinging) {
            g.setColor(new Color(255, 255, 0, 150));
            g.fillOval(racketX - 10, racketY - 10, 40, 40);
        }

        g.setColor(new Color(139, 69, 19));
        g.fillRect(racketX, racketY, 20, 5);
        g.setColor(Color.YELLOW);
        g.fillOval(racketX + (facingRight ? 15 : -10), racketY - 5, 15, 15);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        g.drawLine(racketX + (facingRight ? 20 : -5), racketY,
                racketX + (facingRight ? 20 : -5), racketY + 5);
        g.drawLine(racketX + (facingRight ? 23 : -2), racketY,
                racketX + (facingRight ? 23 : -2), racketY + 5);
    }

    private void drawPowerUpEffect(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        switch (activePowerUp) {
            case SPEED_BOOST:
                g.setColor(Color.CYAN);
                break;
            case POWER_SHOT:
                g.setColor(Color.RED);
                break;
            case SLOW_TIME:
                g.setColor(Color.MAGENTA);
                break;
            default:
                break;
        }
        g.fillOval(x - 5, y - 5, width + 10, height + 10);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    public void setMovingLeft(boolean moving) { movingLeft = moving; }
    public void setMovingRight(boolean moving) { movingRight = moving; }
    public boolean isJumping() { return isJumping; }
    public boolean isFacingRight() { return facingRight; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public Rectangle getRacketBounds() {
        return new Rectangle(racketX, racketY, 20, 20);
    }
    public String getName() { return playerName; }
    public PowerUpType getActivePowerUp() { return activePowerUp; }
}