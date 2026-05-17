import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {

    private GameLogic logic;

    @BeforeEach
    void setUp() {
        logic = new GameLogic();
        // Wymagane, aby wygenerowac granice boiska i ustawic pilke na srodku
        logic.reset(false); 
    }

    @Test
    void testInitialPosition() {
        Point start = logic.getCurrentPosition();
        assertEquals(4, start.x);
        assertEquals(5, start.y);
    }

    @Test
    void testValidOrthogonalMove() {
        Point start = logic.getCurrentPosition();
        Point newPos = new Point(start.x + 1, start.y); // Ruch w prawo
        assertTrue(logic.isValidMove(newPos));
    }

    @Test
    void testValidDiagonalMove() {
        Point start = logic.getCurrentPosition();
        Point newPos = new Point(start.x + 1, start.y + 1); // Skos w dol-prawo
        assertTrue(logic.isValidMove(newPos));
    }

    @Test
    void testInvalidMoveTooFar() {
        Point start = logic.getCurrentPosition();
        Point newPos = new Point(start.x + 2, start.y); // Ruch o 2 pola (niedozwolony)
        assertFalse(logic.isValidMove(newPos));
    }

    @Test
    void testCannotMoveOnAlreadyDrawnLine() {
        Point start = logic.getCurrentPosition();
        Point nextPos = new Point(start.x + 1, start.y);
        
        logic.makeMove(nextPos); // Gracz 1 idzie w prawo
        
        // Tura przechodzi na Gracza 2, proba powrotu po dokladnie tej samej linii
        assertFalse(logic.isValidMove(start)); 
    }

    @Test
    void testTurnChangesOnEmptyVertex() {
        assertTrue(logic.isPlayerOneTurn(), "Powinna byc tura Gracza 1");
        
        // Ruch na pusty, nieodwiedzony wezel
        logic.makeMove(new Point(5, 5)); 
        
        assertFalse(logic.isPlayerOneTurn(), "Po wejsciu na czysty wezel tura powinna przejsc na Gracza 2");
    }

    @Test
    void testBonusTurnOnVisitedVertex() {
        // Symulacja: P1 idzie (4,5)->(5,5)
        logic.makeMove(new Point(5, 5)); 
        assertFalse(logic.isPlayerOneTurn()); // Tura P2
        
        // P2 idzie (5,5)->(5,4)
        logic.makeMove(new Point(5, 4));
        assertTrue(logic.isPlayerOneTurn()); // Tura P1
        
        // P1 idzie (5,4)->(4,5). Wezel (4,5) to srodek boiska, byl juz odwiedzony!
        logic.makeMove(new Point(4, 5));
        
        // Tura NIE POWINNA sie zmienic, Gracz 1 dostaje bonusowy ruch z odbicia
        assertTrue(logic.isPlayerOneTurn(), "Gracz 1 powinien zachowac ture po wejsciu w stary wezel");
    }
}