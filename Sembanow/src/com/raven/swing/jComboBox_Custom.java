
package com.raven.swing;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
public class jComboBox_Custom<E> extends JComboBox<E>{
     private int cornerRadius = 20; // Radius sudut

    // ✅ Konstruktor default agar bisa digunakan di GUI Builder
    public jComboBox_Custom() {
        super();
        setUI(new RoundedComboBoxUI());
        setOpaque(false);
        setFocusable(false);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    // ✅ Konstruktor lain untuk inisialisasi langsung dengan data
    public jComboBox_Custom(E[] items) {
        super(items);
        setUI(new RoundedComboBoxUI());
        setOpaque(false);
        setFocusable(false);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    // Custom UI untuk menggambar JComboBox dengan sudut melengkung
    private class RoundedComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton arrowButton = new JButton("▼") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(Color.BLACK);
                    g2.drawString("▼", getWidth() / 2 - 4, getHeight() / 2 + 5);
                }
            };
            arrowButton.setBorder(null);
            arrowButton.setContentAreaFilled(false);
            return arrowButton;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height, cornerRadius, cornerRadius));
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Warna Background JComboBox
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), cornerRadius, cornerRadius);

            // Border
            g2.setColor(new Color(100, 100, 100));
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, cornerRadius, cornerRadius);

            super.paint(g, c);
        }
    }
}
