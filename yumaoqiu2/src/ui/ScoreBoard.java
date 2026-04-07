package ui;

import util.Constants;
import entity.Player;
import java.awt.*;

public class ScoreBoard {
    private int leftScore;
    private int rightScore;
    private int leftSets;
    private int rightSets;
    private boolean isTournamentMode = false;
    private int currentSet = 1;
    private int targetScore = Constants.WIN_SCORE;

    public ScoreBoard() {
        reset();
    }

    public void reset() {
        leftScore = 0;
        rightScore = 0;
    }

    public void resetMatch() {
        leftScore = 0;
        rightScore = 0;
        leftSets = 0;
        rightSets = 0;
        currentSet = 1;
    }

    public void addPoint(int playerId) {
        if (playerId == 1) {
            leftScore++;
        } else {
            rightScore++;
        }
    }

    public boolean checkSetWinner() {
        if (leftScore >= targetScore && leftScore - rightScore >= 2) {
            leftSets++;
            return true;
        } else if (rightScore >= targetScore && rightScore - leftScore >= 2) {
            rightSets++;
            return true;
        }
        return false;
    }

    public int getWinner() {
        if (leftSets >= 2) return 1;
        if (rightSets >= 2) return 2;
        return 0;
    }

    public void draw(Graphics2D g, Player player1, Player player2) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(Constants.WINDOW_WIDTH / 2 - 150, 20, 300, 80, 15, 15);

        g.setFont(Constants.FONT_SCORE);

        g.setColor(Constants.COLOR_PLAYER1);
        String leftScoreStr = String.format("%02d", leftScore);
        FontMetrics fm = g.getFontMetrics();
        int leftWidth = fm.stringWidth(leftScoreStr);
        g.drawString(leftScoreStr, Constants.WINDOW_WIDTH / 2 - 80 - leftWidth / 2, 80);

        g.setColor(Color.WHITE);
        g.drawString("-", Constants.WINDOW_WIDTH / 2 - 15, 80);

        g.setColor(Constants.COLOR_PLAYER2);
        String rightScoreStr = String.format("%02d", rightScore);
        g.drawString(rightScoreStr, Constants.WINDOW_WIDTH / 2 + 20, 80);

        g.setFont(Constants.FONT_SMALL);
        fm = g.getFontMetrics();

        g.setColor(Constants.COLOR_PLAYER1);
        String name1 = player1 != null ? player1.getName() : "Player 1";
        int name1Width = fm.stringWidth(name1);
        g.drawString(name1, Constants.WINDOW_WIDTH / 2 - 100 - name1Width / 2, 50);

        g.setColor(Constants.COLOR_PLAYER2);
        String name2 = player2 != null ? player2.getName() : "Player 2";
        int name2Width = fm.stringWidth(name2);
        g.drawString(name2, Constants.WINDOW_WIDTH / 2 + 100 - name2Width / 2, 50);

        if (isTournamentMode) {
            g.setFont(Constants.FONT_SMALL);
            g.setColor(Color.WHITE);
            String setText = "Set " + currentSet + " - " + leftSets + " : " + rightSets;
            fm = g.getFontMetrics();
            int setWidth = fm.stringWidth(setText);
            g.drawString(setText, (Constants.WINDOW_WIDTH - setWidth) / 2, 110);
        }
    }

    public int getLeftScore() { return leftScore; }
    public int getRightScore() { return rightScore; }
    public void setTargetScore(int target) { this.targetScore = target; }
    public void setTournamentMode(boolean mode) { isTournamentMode = mode; }
    public void nextSet() {
        currentSet++;
        leftScore = 0;
        rightScore = 0;
    }
}