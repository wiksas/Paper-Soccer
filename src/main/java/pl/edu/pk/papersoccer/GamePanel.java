package pl.edu.pk.papersoccer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class GamePanel extends JPanel {
    private static final int GRID_SIZE = 45;
    private static final int MARGIN = 70;

    // Paleta: zielona murawa, białe linie boiska i ślady ruchów
    private static final Color FIELD_BG = new Color(39, 174, 96);
    private static final Color GRID_COLOR = new Color(255, 255, 255, 45);
    private static final Color BORDER_COLOR = new Color(255, 255, 255);
    private static final Color P1_COLOR = new Color(37, 99, 235);
    private static final Color P2_COLOR = new Color(225, 29, 72);
    private static final Color PATH_COLOR = new Color(255, 255, 255);

    private GameLogic logic;
    private Runnable onMenu;
    private Runnable onRematch;
    private Timer aiTimer;
    private Point hoverTarget;

    public GamePanel(GameLogic logic, Runnable onMenu, Runnable onRematch) {
        this.logic = logic;
        this.onMenu = onMenu;
        this.onRematch = onRematch;

        int panelWidth = GameLogic.WIDTH * GRID_SIZE + 2 * MARGIN;
        int panelHeight = GameLogic.HEIGHT * GRID_SIZE + 2 * MARGIN;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setBackground(FIELD_BG);

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

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (logic.getWinnerMessage() != null || aiTimer.isRunning()) return;

                Point target = nodeAt(e);
                if (logic.isValidMove(target)) {
                    logic.makeMove(target);
                    hoverTarget = null;
                    repaint();

                    if (logic.getWinnerMessage() != null) {
                        SwingUtilities.invokeLater(() -> showEndGameDialog());
                    }
                    else if (logic.isVsAI() && !logic.isPlayerOneTurn()) {
                        aiTimer.start();
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (logic.getWinnerMessage() != null || aiTimer.isRunning()
                        || (logic.isVsAI() && !logic.isPlayerOneTurn())) {
                    setHover(null);
                    return;
                }
                Point t = nodeAt(e);
                setHover(logic.isValidMove(t) ? t : null);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHover(null);
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    private Point nodeAt(MouseEvent e) {
        int gridX = Math.round((float) (e.getX() - MARGIN) / GRID_SIZE);
        int gridY = Math.round((float) (e.getY() - MARGIN) / GRID_SIZE);
        return new Point(gridX, gridY);
    }

    private void setHover(Point p) {
        if (hoverTarget == null ? p == null : hoverTarget.equals(p)) return;
        hoverTarget = p;
        repaint();
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
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        drawGrid(g2d);
        drawBorders(g2d);
        drawGoals(g2d);
        drawLines(g2d);
        drawHover(g2d);
        drawCurrentPosition(g2d);
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(GRID_COLOR);
        g.setStroke(new BasicStroke(1));
        int w = getWidth();
        int h = getHeight();
        for (int x = MARGIN % GRID_SIZE; x <= w; x += GRID_SIZE) {
            g.drawLine(x, 0, x, h);
        }
        for (int y = MARGIN % GRID_SIZE; y <= h; y += GRID_SIZE) {
            g.drawLine(0, y, w, y);
        }
    }

    private void drawBorders(Graphics2D g) {
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(5));
        for (Line line : logic.getBorders()) {
            g.drawLine(MARGIN + line.p1.x * GRID_SIZE, MARGIN + line.p1.y * GRID_SIZE,
                       MARGIN + line.p2.x * GRID_SIZE, MARGIN + line.p2.y * GRID_SIZE);
        }
    }

    private void drawGoals(Graphics2D g) {
        g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Bramka Gracza 2 / Komputera (góra, y = 0) - czerwona
        g.setColor(P2_COLOR);
        g.drawLine(MARGIN + GameLogic.GOAL_LEFT * GRID_SIZE, MARGIN,
                   MARGIN + GameLogic.GOAL_RIGHT * GRID_SIZE, MARGIN);
        // Bramka Gracza 1 (dół, y = HEIGHT) - niebieska
        g.setColor(P1_COLOR);
        g.drawLine(MARGIN + GameLogic.GOAL_LEFT * GRID_SIZE, MARGIN + GameLogic.HEIGHT * GRID_SIZE,
                   MARGIN + GameLogic.GOAL_RIGHT * GRID_SIZE, MARGIN + GameLogic.HEIGHT * GRID_SIZE);
    }

    private void drawLines(Graphics2D g) {
        List<Line> lines = logic.getDrawnLines();
        if (lines.isEmpty()) return;

        // Początek ostatniej ciągłej serii ruchów jednego gracza (bieżąca tura).
        // Cała ta ścieżka jest w kolorze gracza, aż przeciwnik wykona ruch; starsze są czarne.
        boolean lastOwner = lines.get(lines.size() - 1).byPlayerOne;
        int runStart = lines.size() - 1;
        while (runStart > 0 && lines.get(runStart - 1).byPlayerOne == lastOwner) {
            runStart--;
        }

        Color current = lastOwner ? P1_COLOR : P2_COLOR;
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            g.setColor(i >= runStart ? current : PATH_COLOR);
            g.drawLine(MARGIN + line.p1.x * GRID_SIZE, MARGIN + line.p1.y * GRID_SIZE,
                       MARGIN + line.p2.x * GRID_SIZE, MARGIN + line.p2.y * GRID_SIZE);
        }
    }

    private void drawHover(Graphics2D g) {
        if (hoverTarget == null || logic.getWinnerMessage() != null) return;
        if (logic.isVsAI() && !logic.isPlayerOneTurn()) return;
        if (!logic.isValidMove(hoverTarget)) return;

        Point cur = logic.getCurrentPosition();
        Color base = logic.isPlayerOneTurn() ? P1_COLOR : P2_COLOR;

        // Półprzezroczysty podgląd ruchu z aktualnej pozycji do węzła pod kursorem
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 110));
        g.drawLine(MARGIN + cur.x * GRID_SIZE, MARGIN + cur.y * GRID_SIZE,
                   MARGIN + hoverTarget.x * GRID_SIZE, MARGIN + hoverTarget.y * GRID_SIZE);

        // Mała kropka na docelowym węźle
        int dotSize = 12;
        g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 200));
        g.fillOval(MARGIN + hoverTarget.x * GRID_SIZE - dotSize / 2,
                   MARGIN + hoverTarget.y * GRID_SIZE - dotSize / 2, dotSize, dotSize);
    }

    private void drawCurrentPosition(Graphics2D g) {
        Point p = logic.getCurrentPosition();
        int ovalSize = 18;

        g.setColor(new Color(0, 0, 0, 80));
        g.fillOval(MARGIN + p.x * GRID_SIZE - ovalSize / 2 + 2, MARGIN + p.y * GRID_SIZE - ovalSize / 2 + 3, ovalSize, ovalSize);

        if (logic.isPlayerOneTurn()) {
            g.setColor(P1_COLOR);
        } else {
            g.setColor(P2_COLOR);
        }

        g.fillOval(MARGIN + p.x * GRID_SIZE - ovalSize / 2, MARGIN + p.y * GRID_SIZE - ovalSize / 2, ovalSize, ovalSize);

        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(2));
        g.drawOval(MARGIN + p.x * GRID_SIZE - ovalSize / 2, MARGIN + p.y * GRID_SIZE - ovalSize / 2, ovalSize, ovalSize);
    }
}
