package pl.edu.pk.papersoccer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import static org.junit.jupiter.api.Assertions.*;

class MinimaxAiTest {

    private GameLogic logic;

    @BeforeEach
    void setUp() {
        logic = new GameLogic();
    }

    private Point firstLegalMove(GameLogic g) {
        Point c = g.getCurrentPosition();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Point p = new Point(c.x + dx, c.y + dy);
                if (g.isValidMove(p)) return p;
            }
        }
        return null;
    }

    @Test
    void testChooseMoveReturnsLegalMove() {
        logic.reset(true, Difficulty.HARD);
        logic.makeMove(new Point(4, 6));   // ruch człowieka na świeży węzeł -> tura komputera
        assertFalse(logic.isPlayerOneTurn());
        Point m = new MinimaxAi(Difficulty.HARD).chooseMove(logic);
        assertNotNull(m);
        assertTrue(logic.isValidMove(m));
    }

    @Test
    void testDepthsAreRespected() {
        assertEquals(2, Difficulty.EASY.getSearchDepth());
        assertEquals(4, Difficulty.MEDIUM.getSearchDepth());
        assertEquals(7, Difficulty.HARD.getSearchDepth());
    }

    @Test
    void testAllLevelsDeterministic() {
        logic.reset(true, Difficulty.MEDIUM);
        logic.makeMove(new Point(4, 6));
        for (Difficulty d : Difficulty.values()) {
            assertEquals(new MinimaxAi(d).chooseMove(logic),
                         new MinimaxAi(d).chooseMove(logic),
                         "poziom " + d + " powinien byc deterministyczny");
        }
    }

    @Test
    void testTakesWinningGoal() {
        logic.reset(true, Difficulty.HARD);
        // Prowadzimy piłkę tak, by komputer (Gracz 2) stanął na (4,9) na swojej turze.
        logic.makeMove(new Point(4, 6));   // P1 -> P2
        logic.makeMove(new Point(3, 7));   // P2 -> P1
        logic.makeMove(new Point(4, 8));   // P1 -> P2
        logic.makeMove(new Point(3, 8));   // P2 -> P1
        logic.makeMove(new Point(4, 9));   // P1 -> P2 (świeży węzeł w otworze bramki)
        assertFalse(logic.isPlayerOneTurn(), "powinna być tura komputera");
        assertNull(logic.getWinnerMessage());

        Point m = new MinimaxAi(Difficulty.HARD).chooseMove(logic);
        assertNotNull(m);
        assertEquals(GameLogic.HEIGHT, m.y, "komputer powinien strzelić gola");
    }

    @Test
    void testPlaysLegalMovesThroughGame() {
        logic.reset(true, Difficulty.MEDIUM);
        MinimaxAi ai = new MinimaxAi(Difficulty.MEDIUM);
        int guard = 0;
        while (logic.getWinnerMessage() == null && guard++ < 1000) {
            if (logic.isPlayerOneTurn()) {
                Point hm = firstLegalMove(logic);
                if (hm == null) break;
                logic.makeMove(hm);
            } else {
                Point am = ai.chooseMove(logic);
                assertNotNull(am, "komputer powinien mieć ruch");
                assertTrue(logic.isValidMove(am), "komputer zwrócił nielegalny ruch");
                logic.makeMove(am);
            }
        }
        assertNotNull(logic.getWinnerMessage(), "gra powinna się zakończyć");
    }
}
