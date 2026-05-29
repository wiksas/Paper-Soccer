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
    private Difficulty difficulty;
    private MinimaxAi ai;

    public GameLogic() {
        drawnLines = new ArrayList<>();
        borders = new ArrayList<>();
        visitedVertices = new HashSet<>();
    }

    // Konstruktor kopiujący na potrzeby symulacji minimaxa.
    // Głęboko kopiuje mutowane kolekcje; borders są stałe w trakcie gry, więc współdzielone.
    public GameLogic(GameLogic other) {
        this.drawnLines = new ArrayList<>(other.drawnLines);
        this.borders = other.borders;
        this.visitedVertices = new HashSet<>(other.visitedVertices);
        this.currentPosition = new Point(other.currentPosition);
        this.isPlayerOneTurn = other.isPlayerOneTurn;
        this.winnerMessage = other.winnerMessage;
        this.vsAI = other.vsAI;
        this.difficulty = other.difficulty;
        this.ai = null;
    }

    public void reset(boolean vsAI) {
        reset(vsAI, Difficulty.MEDIUM);
    }

    public void reset(boolean vsAI, Difficulty difficulty) {
        this.vsAI = vsAI;
        this.difficulty = difficulty;
        this.ai = new MinimaxAi(difficulty);
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
            // Do bramki wolno wejść tylko z otworu (kolumna x w [GOAL_LEFT, GOAL_RIGHT]),
            // nie po skosie zza słupka - taka linia przechodziłaby poza boiskiem.
            if ((newPos.y == 0 || newPos.y == HEIGHT)
                    && (currentPosition.x < GOAL_LEFT || currentPosition.x > GOAL_RIGHT)) {
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
            winnerMessage = "Wygrywa Niebieski! (GOL!)";
            return;
        }
        if (currentPosition.y == HEIGHT) {
            winnerMessage = vsAI ? "Wygrywa Komputer! (GOL!)" : "Wygrywa Czerwony! (GOL!)";
            return;
        }

        if (!visitedVertices.contains(currentPosition)) {
            isPlayerOneTurn = !isPlayerOneTurn;
            visitedVertices.add(currentPosition);
        }

        if (isDeadEnd(currentPosition)) {
            if (isPlayerOneTurn) {
                winnerMessage = vsAI ? "Wygrywa Komputer! (Blokada)" : "Wygrywa Czerwony! (Blokada)";
            } else {
                winnerMessage = "Wygrywa Niebieski! (Blokada)";
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
        if (winnerMessage != null || isPlayerOneTurn) return;
        if (ai == null) {
            ai = new MinimaxAi(difficulty == null ? Difficulty.MEDIUM : difficulty);
        }
        Point best = ai.chooseMove(this);
        if (best != null) {
            makeMove(best);
        }
    }

    public Point getCurrentPosition() { return currentPosition; }
    public List<Line> getDrawnLines() { return drawnLines; }
    public List<Line> getBorders() { return borders; }
    public boolean isPlayerOneTurn() { return isPlayerOneTurn; }
    public String getWinnerMessage() { return winnerMessage; }
    public boolean isVsAI() { return vsAI; }
    public Difficulty getDifficulty() { return difficulty; }

    boolean isVisited(Point p) { return visitedVertices.contains(p); }
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
