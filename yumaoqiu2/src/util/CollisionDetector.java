package util;

import entity.Player;
import entity.BadmintonBall;
import java.awt.*;

public class CollisionDetector {

    public static boolean checkCollision(Player player, BadmintonBall ball) {
        Rectangle playerRect = player.getBounds();
        Rectangle racketRect = player.getRacketBounds();
        Rectangle ballRect = ball.getBounds();

        return playerRect.intersects(ballRect) || racketRect.intersects(ballRect);
    }

    public static boolean checkRacketHit(Player player, BadmintonBall ball) {
        Rectangle racketRect = player.getRacketBounds();
        Rectangle ballRect = ball.getBounds();

        return racketRect.intersects(ballRect);
    }

    public static boolean isBallOnPlayerSide(BadmintonBall ball, boolean isLeftSide) {
        if (isLeftSide) {
            return ball.getX() < Constants.NET_X;
        } else {
            return ball.getX() > Constants.NET_X;
        }
    }
}