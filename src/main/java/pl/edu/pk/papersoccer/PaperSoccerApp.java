import javax.swing.*;
import java.awt.*;

public class PaperSoccerApp {
    private JFrame frame;
    private JPanel cards;
    private CardLayout cardLayout;
    private GameLogic logic;
    private GamePanel gamePanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaperSoccerApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Paper Soccer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        logic = new GameLogic();
        
        MenuPanel menuPanel = new MenuPanel(
            () -> startGame(false),
            () -> startGame(true)
        );

        gamePanel = new GamePanel(logic, 
            () -> cardLayout.show(cards, "MENU"),
            () -> startGame(logic.isVsAI())
        );

        cards.add(menuPanel, "MENU");
        cards.add(gamePanel, "GAME");

        frame.add(cards);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startGame(boolean vsAI) {
        logic.reset(vsAI);
        gamePanel.repaint();
        cardLayout.show(cards, "GAME");
    }
}