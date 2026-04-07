package entity;

import java.awt.*;

public abstract class GameObject {
    protected int x, y;
    protected int width, height;
    protected double vx, vy;
    protected boolean active = true;

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.vx = 0;
        this.vy = 0;
    }

    public abstract void update();
    public abstract void draw(Graphics2D g);

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean collidesWith(GameObject other) {
        return getBounds().intersects(other.getBounds());
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public void setVx(double vx) { this.vx = vx; }
    public void setVy(double vy) { this.vy = vy; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}