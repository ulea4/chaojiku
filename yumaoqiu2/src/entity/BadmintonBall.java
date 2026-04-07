package entity;

import util.Constants;
import java.awt.*;
import java.util.Random;

public class BadmintonBall extends GameObject {
    private boolean isServing = true;
    private int servePlayer = 1;
    private int hitCount = 0;
    private Random random = new Random();
    private TrailParticle[] trail = new TrailParticle[5];

    private class TrailParticle {
        int x, y;
        int life = 10;

        TrailParticle(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void update() { life--; }
        boolean isAlive() { return life > 0; }
    }

    public BadmintonBall(int x, int y) {
        super(x, y, 16, 16);
        for (int i = 0; i < trail.length; i++) {
            trail[i] = null;
        }
    }

    @Override
    public void update() {
        if (isServing) return;

        addTrailParticle();
        updateTrail();

        vx *= Constants.AIR_RESISTANCE;
        vy += Constants.GRAVITY;
        x += vx;
        y += vy;

        if (x < Constants.LEFT_BOUNDARY) {
            x = Constants.LEFT_BOUNDARY;
            vx = -vx * 0.8;
        }
        if (x > Constants.RIGHT_BOUNDARY - width) {
            x = Constants.RIGHT_BOUNDARY - width;
            vx = -vx * 0.8;
        }

        if (y < Constants.TOP_BOUNDARY) {
            y = Constants.TOP_BOUNDARY;
            vy = -vy * 0.8;
        }

        if (Math.abs(x - Constants.NET_X) < 10 && y > Constants.GROUND_Y - 150) {
            vx = -vx * 0.6;
            x = x < Constants.NET_X ? Constants.NET_X - 10 : Constants.NET_X + 10;
        }

        if (y >= Constants.GROUND_Y) {
            y = Constants.GROUND_Y;
            isServing = true;
        }
    }

    private void addTrailParticle() {
        for (int i = trail.length - 1; i > 0; i--) {
            trail[i] = trail[i-1];
        }
        trail[0] = new TrailParticle(x, y);
    }

    private void updateTrail() {
        for (TrailParticle p : trail) {
            if (p != null) p.update();
        }
    }

    public void serve(int playerId, Player player) {
        isServing = false;
        hitCount = 0;

        if (playerId == 1) {
            x = player.getX() + player.getWidth();
            y = player.getY() - 10;
            vx = 7 + random.nextDouble() * 2;
            vy = -5 - random.nextDouble() * 3;
        } else {
            x = player.getX() - 10;
            y = player.getY() - 10;
            vx = -7 - random.nextDouble() * 2;
            vy = -5 - random.nextDouble() * 3;
        }
    }

    public void hit(Player player) {
        hitCount++;

        int hitY = y - player.getY();
        double angle = Math.toRadians(Math.min(60, Math.max(-60, hitY * 2)));

        double baseSpeed = 8;
        if (player.getActivePowerUp() == Player.PowerUpType.POWER_SHOT) {
            baseSpeed = 14;
        }

        if (player.isFacingRight()) {
            vx = baseSpeed + Math.abs(vx) * 0.3;
        } else {
            vx = -baseSpeed - Math.abs(vx) * 0.3;
        }

        vy = -6 + Math.sin(angle) * 4;

        vx += (random.nextDouble() - 0.5) * 1.5;
        vy += (random.nextDouble() - 0.5) * 1;

        double maxSpeed = player.getActivePowerUp() == Player.PowerUpType.POWER_SHOT ? 22 : 15;
        if (Math.abs(vx) > maxSpeed) vx = vx > 0 ? maxSpeed : -maxSpeed;
        if (Math.abs(vy) > 12) vy = vy > 0 ? 12 : -12;
    }

    public boolean isOutOfBounds() {
        return y >= Constants.GROUND_Y;
    }

    public int getScoringPlayer() {
        if (x < Constants.NET_X) {
            return 2;
        } else {
            return 1;
        }
    }

    public void reset() {
        x = Constants.WINDOW_WIDTH / 2;
        y = Constants.WINDOW_HEIGHT / 2;
        vx = 0;
        vy = 0;
        isServing = true;
        hitCount = 0;
    }

    public void setPositionForServe(int playerId, Player player) {
        if (playerId == 1) {
            x = player.getX() + player.getWidth();
            y = player.getY() - 10;
        } else {
            x = player.getX() - 10;
            y = player.getY() - 10;
        }
        vx = 0;
        vy = 0;
    }

    @Override
    public void draw(Graphics2D g) {
        for (TrailParticle p : trail) {
            if (p != null && p.isAlive()) {
                int alpha = (int)(255 * (p.life / 10.0));
                g.setColor(new Color(255, 215, 0, alpha / 2));
                g.fillOval(p.x - 4, p.y - 4, 8, 8);
            }
        }

        g.setColor(Color.WHITE);
        g.fillOval(x - 8, y - 8, 16, 16);

        Graphics2D g2d = (Graphics2D) g;
        RadialGradientPaint gradient = new RadialGradientPaint(
                x, y, 8,
                new float[]{0f, 1f},
                new Color[]{Constants.COLOR_BALL, Color.WHITE}
        );
        g2d.setPaint(gradient);
        g2d.fillOval(x - 8, y - 8, 16, 16);

        g.setColor(new Color(240, 240, 240));
        for (int i = 0; i < 4; i++) {
            int angle = i * 90 + (hitCount % 360);
            int dx = (int)(Math.cos(Math.toRadians(angle)) * 12);
            int dy = (int)(Math.sin(Math.toRadians(angle)) * 12);
            g.drawLine(x, y, x + dx, y + dy);
        }

        g.setColor(new Color(255, 255, 255, 180));
        g.fillOval(x - 3, y - 3, 6, 6);
    }

    public void setServing(boolean serving) { isServing = serving; }
    public void setServePlayer(int player) { servePlayer = player; }
    public boolean isServing() { return isServing; }
    public int getHitCount() { return hitCount; }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - 8, y - 8, 16, 16);
    }
}