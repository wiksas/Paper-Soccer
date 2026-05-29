package pl.edu.pk.papersoccer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Przeciwnik komputerowy oparty na minimaxie z odcięciami alfa-beta.
 *
 * Gracz maksymalizujący to komputer (Gracz 2, isPlayerOneTurn == false),
 * minimalizujący to człowiek. Regułę odbicia obsługujemy w naturalny sposób:
 * gracz na ruchu w danym węźle to zawsze stan.isPlayerOneTurn, więc po odbiciu
 * (dziecko ma to samo isPlayerOneTurn) ten sam gracz "rusza" ponownie.
 *
 * Wszystkie poziomy działają tak samo - różnią się tylko głębokością (2/4/7).
 * Przy równej ocenie wybieramy ruch stawiający piłkę najbliżej bramki przeciwnika.
 */
public class MinimaxAi {

    private static final int WIN_SCORE = 1_000_000;
    private static final int DEPTH_BONUS = 1_000;   // premia za szybszą wygraną

    private final int searchDepth;

    public MinimaxAi(Difficulty difficulty) {
        this.searchDepth = difficulty.getSearchDepth();
    }

    /** Najlepsza krawędź (węzeł docelowy) z bieżącej pozycji, albo null gdy brak ruchu. */
    public Point chooseMove(GameLogic state) {
        if (state.getWinnerMessage() != null) return null;
        List<Point> moves = legalMoves(state);
        if (moves.isEmpty()) return null;

        boolean rootMax = !state.isPlayerOneTurn();
        List<Integer> scores = new ArrayList<>();
        int best = rootMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Point p : moves) {
            GameLogic child = new GameLogic(state);
            child.makeMove(p);
            int s = minimax(child, searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            scores.add(s);
            best = rootMax ? Math.max(best, s) : Math.min(best, s);
        }
        return pickMove(moves, scores, best, rootMax);
    }

    private int minimax(GameLogic node, int depth, int alpha, int beta) {
        if (node.getWinnerMessage() != null) return terminalScore(node, depth);
        if (depth == 0) return evaluate(node);

        List<Point> moves = legalMoves(node);
        if (moves.isEmpty()) return evaluate(node);

        boolean max = !node.isPlayerOneTurn();
        if (max) {
            int value = Integer.MIN_VALUE;
            for (Point p : moves) {
                GameLogic child = new GameLogic(node);
                child.makeMove(p);
                value = Math.max(value, minimax(child, depth - 1, alpha, beta));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break;
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (Point p : moves) {
                GameLogic child = new GameLogic(node);
                child.makeMove(p);
                value = Math.min(value, minimax(child, depth - 1, alpha, beta));
                beta = Math.min(beta, value);
                if (beta <= alpha) break;
            }
            return value;
        }
    }

    // Wynik stanu końcowego z perspektywy komputera. Klasyfikacja PO GEOMETRII,
    // nigdy po tekście winnerMessage (ten zależy od trybu vsAI).
    private int terminalScore(GameLogic node, int depth) {
        Point pos = node.getCurrentPosition();
        if (pos.y == GameLogic.HEIGHT) return WIN_SCORE + depth * DEPTH_BONUS;   // gol komputera
        if (pos.y == 0) return -WIN_SCORE - depth * DEPTH_BONUS;                 // gol człowieka
        // Blokada: przegrywa strona będąca na ruchu (po ewentualnym flipie w makeMove).
        return node.isPlayerOneTurn() ? WIN_SCORE + depth * DEPTH_BONUS
                                      : -WIN_SCORE - depth * DEPTH_BONUS;
    }

    // Heurystyka liścia z perspektywy komputera (atakuje y == HEIGHT). Bez losowości.
    private int evaluate(GameLogic node) {
        Point pos = node.getCurrentPosition();
        int score = -movesToGoal(pos, GameLogic.HEIGHT) * 12;   // mniej ruchów do bramki = lepiej
        int mobility = legalMoves(node).size();
        // Mobilność: korzystna dla komputera, szkodliwa gdy na ruchu jest człowiek.
        score += node.isPlayerOneTurn() ? -mobility * 3 : mobility * 3;
        // Stanie na odwiedzonym węźle = możliwość odbicia (zachowanie tury).
        if (node.isVisited(pos)) score += node.isPlayerOneTurn() ? -8 : 8;
        return score;
    }

    // Liczba ruchów (metryka króla) do otworu bramki na linii y == goalY.
    private int movesToGoal(Point pos, int goalY) {
        int gx = Math.max(GameLogic.GOAL_LEFT, Math.min(GameLogic.GOAL_RIGHT, pos.x));
        return Math.max(Math.abs(goalY - pos.y), Math.abs(pos.x - gx));
    }

    private List<Point> legalMoves(GameLogic node) {
        List<Point> out = new ArrayList<>();
        Point c = node.getCurrentPosition();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Point p = new Point(c.x + dx, c.y + dy);
                if (node.isValidMove(p)) out.add(p);
            }
        }
        return out;
    }

    // Spośród ruchów o najlepszej ocenie wybiera ten, który stawia piłkę najbliżej
    // bramki przeciwnika (najmniej ruchów do niej). W pełni deterministyczne.
    private Point pickMove(List<Point> moves, List<Integer> scores, int best, boolean rootMax) {
        int goalY = rootMax ? GameLogic.HEIGHT : 0;
        Point chosen = moves.get(0);
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < moves.size(); i++) {
            if (scores.get(i) != best) continue;
            int dist = movesToGoal(moves.get(i), goalY);
            if (dist < bestDist) {
                bestDist = dist;
                chosen = moves.get(i);
            }
        }
        return chosen;
    }
}
