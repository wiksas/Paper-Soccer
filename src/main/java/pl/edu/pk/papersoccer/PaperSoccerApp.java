package pl.edu.pk.papersoccer;

import javax.swing.*;
import java.awt.*;

public class PaperSoccerApp {
    private JFrame frame;
    private JPanel cards;
    private CardLayout cardLayout;
    private GameLogic logic;
    private GamePanel gamePanel;

    public static void main(String[] args) {
        // Wygładzanie (antyaliasing) tekstu Swinga - ustawione przed startem GUI,
        // inaczej napisy na przyciskach i tytułach są "schodkowate".
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
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
            () -> startGame(false, Difficulty.MEDIUM),
            () -> cardLayout.show(cards, "DIFFICULTY")
        );

        DifficultyPanel difficultyPanel = new DifficultyPanel(
            d -> startGame(true, d),
            () -> cardLayout.show(cards, "MENU")
        );

        gamePanel = new GamePanel(logic, 
            () -> cardLayout.show(cards, "MENU"),
            () -> startGame(logic.isVsAI(), logic.getDifficulty())
        );

        cards.add(menuPanel, "MENU");
        cards.add(difficultyPanel, "DIFFICULTY");
        cards.add(gamePanel, "GAME");

        frame.add(cards);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startGame(boolean vsAI, Difficulty difficulty) {
        logic.reset(vsAI, difficulty);
        gamePanel.repaint();
        cardLayout.show(cards, "GAME");
    }
}