import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class GamePanel extends JPanel {
    private static final int GRID_SIZE = 45;
    private static final int MARGIN = 70;
    private GameLogic logic;
    private Runnable onMenu;
    private Runnable onRematch;
    private Timer aiTimer;

    public GamePanel(GameLogic logic, Runnable onMenu, Runnable onRematch) {
        this.logic = logic;
        this.onMenu = onMenu;
        this.onRematch = onRematch;

        int panelWidth = 8 * GRID_SIZE + 2 * MARGIN;
        int panelHeight = 10 * GRID_SIZE + 2 * MARGIN;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setBackground(new Color(39, 174, 96)); 

        aiTimer = new Timer(600, e -> {
            if (!logic.isPlayerOneTurn() && logic.getWinnerMessage() == null) {
                logic.makeAIMove();
                repaint();
                
                if (logic.getWinnerMessage() != null) {
                    aiTimer.stop();
                    SwingUtilities.invokeLater(this::showEndGameDialog);
                } 
                else if (logic.isPlayerOneTurn()) {
                    aiTimer.stop();
                }
            } else {
                aiTimer.stop();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (logic.getWinnerMessage() != null || aiTimer.isRunning()) return;

                int gridX = Math.round((float) (e.getX() - MARGIN) / GRID_SIZE);
                int gridY = Math.round((float) (e.getY() - MARGIN) / GRID_SIZE);
                Point target = new Point(gridX, gridY);

                if (logic.isValidMove(target)) {
                    logic.makeMove(target);
                    repaint(); 

                    if (logic.getWinnerMessage() != null) {
                        SwingUtilities.invokeLater(() -> showEndGameDialog());
                    } 
                    else if (logic.isVsAI() && !logic.isPlayerOneTurn()) {
                        aiTimer.start();
                    }
                }
            }
        });
    }

    private void showEndGameDialog() {
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 16));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 14));
        
        int choice = JOptionPane.showOptionDialog(this,
                logic.getWinnerMessage() + "\nCo chcesz zrobic dalej?",
                "Koniec meczu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, 
                new String[]{"Rewanz", "Wroc do menu"},
                "Rewanz");

        if (choice == JOptionPane.YES_OPTION) {
            onRematch.run();
        } else {
            onMenu.run();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawDots(g2d);
        drawBorders(g2d);
        drawLines(g2d);
        drawCurrentPosition(g2d);
        drawUI(g2d);
    }

    private void drawDots(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 180));
        int dotSize = 8;
        for (int x = 0; x <= 8; x++) {
            for (int y = 0; y <= 10; y++) {
                g.fillOval(MARGIN + x * GRID_SIZE - dotSize / 2, MARGIN + y * GRID_SIZE - dotSize / 2, dotSize, dotSize);
            }
        }
    }

    private void drawBorders(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(5));
        for (Line line : logic.getBorders()) {
            g.drawLine(MARGIN + line.p1.x * GRID_SIZE, MARGIN + line.p1.y * GRID_SIZE,
                       MARGIN + line.p2.x * GRID_SIZE, MARGIN + line.p2.y * GRID_SIZE);
        }
    }

    private void drawLines(Graphics2D g) {
        List<Line> lines = logic.getDrawnLines();
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            if (i == lines.size() - 1) {
                g.setStroke(new BasicStroke(5));
                if (line.byPlayerOne) {
                    g.setColor(new Color(41, 128, 185));
                } else {
                    g.setColor(new Color(192, 57, 43));
                }
            } else {
                g.setStroke(new BasicStroke(3));
                g.setColor(new Color(255, 255, 255, 200)); 
            }
            g.drawLine(MARGIN + line.p1.x * GRID_SIZE, MARGIN + line.p1.y * GRID_SIZE,
                       MARGIN + line.p2.x * GRID_SIZE, MARGIN + line.p2.y * GRID_SIZE);
        }
    }

    private void drawCurrentPosition(Graphics2D g) {
        Point p = logic.getCurrentPosition();
        int ovalSize = 18; 
        
        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(MARGIN + p.x * GRID_SIZE - ovalSize / 2 + 2, MARGIN + p.y * GRID_SIZE - ovalSize / 2 + 3, ovalSize, ovalSize);

        if (logic.isPlayerOneTurn()) {
            g.setColor(new Color(52, 152, 219));
        } else {
            g.setColor(new Color(231, 76, 60));
        }
        
        g.fillOval(MARGIN + p.x * GRID_SIZE - ovalSize / 2, MARGIN + p.y * GRID_SIZE - ovalSize / 2, ovalSize, ovalSize);
        
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawOval(MARGIN + p.x * GRID_SIZE - ovalSize / 2, MARGIN + p.y * GRID_SIZE - ovalSize / 2, ovalSize, ovalSize);
    }

    private void drawUI(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        if (logic.getWinnerMessage() == null) {
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRoundRect(MARGIN - 10, MARGIN - 55, 200, 35, 15, 15);

            if (logic.isPlayerOneTurn()) {
                g.setColor(new Color(52, 152, 219));
                g.drawString("Tura: Gracz 1", MARGIN, MARGIN - 30);
            } else {
                g.setColor(new Color(231, 76, 60));
                String name = logic.isVsAI() ? "Tura: Komputer" : "Tura: Gracz 2";
                g.drawString(name, MARGIN, MARGIN - 30);
            }
        }

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        g.drawString(logic.isVsAI() ? "BRAMKA KOMPUTERA" : "BRAMKA GRACZA 2", MARGIN + 2 * GRID_SIZE + 5, MARGIN - 20);
        g.drawString("BRAMKA GRACZA 1", MARGIN + 2 * GRID_SIZE + 15, MARGIN + 10 * GRID_SIZE + 35);
    }
}