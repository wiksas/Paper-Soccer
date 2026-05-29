package pl.edu.pk.papersoccer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

public class GameLogic {
    // Wymiary boiska (współrzędne węzłów: x od 0 do WIDTH, y od 0 do HEIGHT)
    public static final int WIDTH = 8;
    public static final int HEIGHT = 10;
    // Słupki bramki (otwór bramki to x od GOAL_LEFT do GOAL_RIGHT)
    public static final int GOAL_LEFT = 3;
    public static final int GOAL_RIGHT = 5;

    private Point currentPosition;
    private List<Line> drawnLines;
    private List<Line> borders;
    private Set<Point> visitedVertices;
    private boolean isPlayerOneTurn;
    private String winnerMessage;
    private boolean vsAI;

    public GameLogic() {
        drawnLines = new ArrayList<>();
        borders = new ArrayList<>();
        visitedVertices = new HashSet<>();
    }

    public void reset(boolean vsAI) {
        this.vsAI = vsAI;
        drawnLines.clear();
        borders.clear();
        visitedVertices.clear();
        isPlayerOneTurn = true;
        winnerMessage = null;
        setupBoard();
    }

    private void setupBoard() {
        for (int y = 1; y < HEIGHT - 1; y++) {
            borders.add(new Line(new Point(0, y), new Point(0, y + 1), false));
            borders.add(new Line(new Point(WIDTH, y), new Point(WIDTH, y + 1), false));
        }
        for (int x = 0; x < WIDTH; x++) {
            if (x < GOAL_LEFT || x >= GOAL_RIGHT) {
                borders.add(new Line(new Point(x, 1), new Point(x + 1, 1), false));
                borders.add(new Line(new Point(x, HEIGHT - 1), new Point(x + 1, HEIGHT - 1), false));
            }
        }
        borders.add(new Line(new Point(GOAL_LEFT, 1), new Point(GOAL_LEFT, 0), false));
        borders.add(new Line(new Point(GOAL_RIGHT, 1), new Point(GOAL_RIGHT, 0), false));
        borders.add(new Line(new Point(GOAL_LEFT, 0), new Point(GOAL_RIGHT, 0), false));

        borders.add(new Line(new Point(GOAL_LEFT, HEIGHT - 1), new Point(GOAL_LEFT, HEIGHT), false));
        borders.add(new Line(new Point(GOAL_RIGHT, HEIGHT - 1), new Point(GOAL_RIGHT, HEIGHT), false));
        borders.add(new Line(new Point(GOAL_LEFT, HEIGHT), new Point(GOAL_RIGHT, HEIGHT), false));

        for (Line l : borders) {
            visitedVertices.add(l.p1);
            visitedVertices.add(l.p2);
        }

        currentPosition = new Point(WIDTH / 2, HEIGHT / 2);
        visitedVertices.add(currentPosition);
    }

    public boolean isValidMove(Point newPos) {
        if (winnerMessage != null) return false;

        int dx = Math.abs(newPos.x - currentPosition.x);
        int dy = Math.abs(newPos.y - currentPosition.y);

        if ((dx <= 1 && dy <= 1) && !(dx == 0 && dy == 0)) {
            Line potentialLine = new Line(currentPosition, newPos, false);
            if (drawnLines.contains(potentialLine) || borders.contains(potentialLine)) return false;

            // Krzyżowanie linii na ukos jest dozwolone (brak blokady).

            if (newPos.x < 0 || newPos.x > WIDTH || newPos.y < 0 || newPos.y > HEIGHT) return false;
            if ((newPos.y == 0 && (newPos.x < GOAL_LEFT || newPos.x > GOAL_RIGHT)) ||
                (newPos.y == HEIGHT && (newPos.x < GOAL_LEFT || newPos.x > GOAL_RIGHT))) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void makeMove(Point newPos) {
        if (!isValidMove(newPos)) return;

        drawnLines.add(new Line(currentPosition, newPos, isPlayerOneTurn));
        currentPosition = newPos;

        if (currentPosition.y == 0) {
            winnerMessage = "Wygrywa Gracz 1! (GOL!)";
            return;
        }
        if (currentPosition.y == HEIGHT) {
            winnerMessage = vsAI ? "Wygrywa Komputer! (GOL!)" : "Wygrywa Gracz 2! (GOL!)";
            return;
        }

        if (!visitedVertices.contains(currentPosition)) {
            isPlayerOneTurn = !isPlayerOneTurn;
            visitedVertices.add(currentPosition);
        }

        if (isDeadEnd(currentPosition)) {
            if (isPlayerOneTurn) {
                winnerMessage = vsAI ? "Wygrywa Komputer! (Blokada)" : "Wygrywa Gracz 2! (Blokada)";
            } else {
                winnerMessage = "Wygrywa Gracz 1! (Blokada)";
            }
        }
    }

    private boolean isDeadEnd(Point p) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue;
                if (isValidMove(new Point(p.x + x, p.y + y))) {
                    return false;
                }
            }
        }
        return true;
    }


    public void makeAIMove() {
        List<Point> validMoves = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue;
                Point p = new Point(currentPosition.x + x, currentPosition.y + y);
                if (isValidMove(p)) {
                    validMoves.add(p);
                }
            }
        }

        if (!validMoves.isEmpty()) {
            Point bestMove = validMoves.get(0);
            int maxWeight = -999999;

            for (Point p : validMoves) {
                Line testLine = new Line(currentPosition, p, false);
                drawnLines.add(testLine);
                Point oldPos = currentPosition;
                currentPosition = p;

                int weight = 0;

                if (p.y == HEIGHT) {
                    weight = 10000;
                }
                else if (isDeadEnd(p)) {
                    if (!visitedVertices.contains(p)) {
                        weight = 5000;
                    } else {
                        weight = -5000;
                    }
                }

                else {
                    weight = p.y * 10;
                    weight -= Math.abs(p.x - WIDTH / 2) * 2;

                    if (visitedVertices.contains(p)) {
                        weight += 15;
                    }
                }

                weight += (int)(Math.random() * 4);
                currentPosition = oldPos;
                drawnLines.remove(drawnLines.size() - 1);

                if (weight > maxWeight) {
                    maxWeight = weight;
                    bestMove = p;
                }
            }
            makeMove(bestMove);
        }
    }

    public Point getCurrentPosition() { return currentPosition; }
    public List<Line> getDrawnLines() { return drawnLines; }
    public List<Line> getBorders() { return borders; }
    public boolean isPlayerOneTurn() { return isPlayerOneTurn; }
    public String getWinnerMessage() { return winnerMessage; }
    public boolean isVsAI() { return vsAI; }
}

class Line {
    Point p1, p2;
    boolean byPlayerOne;

    public Line(Point p1, Point p2, boolean byPlayerOne) {
        this.p1 = p1;
        this.p2 = p2;
        this.byPlayerOne = byPlayerOne;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Line)) return false;
        Line other = (Line) obj;
        return (p1.equals(other.p1) && p2.equals(other.p2)) ||
               (p1.equals(other.p2) && p2.equals(other.p1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(p1.x + p2.x, p1.y + p2.y, p1.x * p2.x, p1.y * p2.y);
    }
}
