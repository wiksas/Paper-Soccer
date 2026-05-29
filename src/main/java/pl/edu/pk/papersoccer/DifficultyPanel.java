package pl.edu.pk.papersoccer;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DifficultyPanel extends JPanel {
    private static final Color FIELD_GREEN = new Color(39, 174, 96);

    public DifficultyPanel(Consumer<Difficulty> startAI, Runnable onBack) {
        setBackground(FIELD_GREEN);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;

        JLabel title = new JLabel("Wybierz poziom trudności");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JButton btnEasy = createButton("Łatwy");
        btnEasy.addActionListener(e -> startAI.accept(Difficulty.EASY));

        JButton btnMedium = createButton("Średni");
        btnMedium.addActionListener(e -> startAI.accept(Difficulty.MEDIUM));

        JButton btnHard = createButton("Trudny");
        btnHard.addActionListener(e -> startAI.accept(Difficulty.HARD));

        JButton btnBack = createButton("Wstecz");
        btnBack.addActionListener(e -> onBack.run());

        gbc.gridy = 0; add(title, gbc);
        gbc.gridy = 1; add(btnEasy, gbc);
        gbc.gridy = 2; add(btnMedium, gbc);
        gbc.gridy = 3; add(btnHard, gbc);
        gbc.gridy = 4; add(btnBack, gbc);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 22));
        btn.setPreferredSize(new Dimension(300, 60));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(FIELD_GREEN);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
