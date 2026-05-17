import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    public MenuPanel(Runnable start1v1, Runnable startAI) {
        setBackground(new Color(39, 174, 96)); // Zielone tlo boiska
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        
        JLabel title = new JLabel("PAPER SOCCER");
        title.setFont(new Font("Arial", Font.BOLD, 50));
        title.setForeground(Color.WHITE);
        
        JButton btn1v1 = createButton("Gra 1 vs 1");
        btn1v1.addActionListener(e -> start1v1.run());
        
        JButton btnAI = createButton("Gra z Komputerem");
        btnAI.addActionListener(e -> startAI.run());
        
        gbc.gridy = 0; add(title, gbc);
        gbc.gridy = 1; add(btn1v1, gbc);
        gbc.gridy = 2; add(btnAI, gbc);
    }
    
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 22));
        btn.setPreferredSize(new Dimension(300, 60));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(39, 174, 96));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}