package org.example;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class ModernComboBox extends JComboBox<String> {
    public ModernComboBox(String[] items) {
        super(items);
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setForeground(Color.WHITE);
        setBackground(new Color(40, 40, 50));
        setUI(new BasicComboBoxUI() {
            protected JButton createArrowButton() {
                JButton b = new JButton();
                b.setContentAreaFilled(false);
                b.setBorder(null);
                b.setIcon(new Icon() {
                    public void paintIcon(Component c, Graphics g, int x, int y) {
                        g.setColor(Color.WHITE);
                        g.fillPolygon(new int[]{x, x + 10, x + 5}, new int[]{y + 4, y + 4, y + 10}, 3);
                    }
                    public int getIconWidth() { return 10; }
                    public int getIconHeight() { return 10; }
                });
                return b;
            }
        });
    }
}

class NeonButton extends JButton {
    private Color baseColor;

    public NeonButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setMaximumSize(new Dimension(220, 35));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { setForeground(color.brighter()); repaint(); }
            public void mouseExited(MouseEvent e) { setForeground(Color.WHITE); repaint(); }
        });
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(baseColor.darker());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.setColor(baseColor);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
        super.paintComponent(g);
    }
}