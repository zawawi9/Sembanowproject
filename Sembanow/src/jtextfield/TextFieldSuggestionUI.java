package jtextfield;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalTextFieldUI;
import java.awt.geom.RoundRectangle2D;

public class TextFieldSuggestionUI extends MetalTextFieldUI {

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
        border.setRound(round);
        textfield.repaint();
    }

    private JTextField textfield;
    private Border border;
    private int round = 15;

    public TextFieldSuggestionUI(JTextField textfield) {
        this.textfield = textfield;
        border = new Border(10);
        border.setRound(round);
        textfield.setBorder(border);
        textfield.setOpaque(false); // Ensure transparency
    }

    @Override
    protected void paintBackground(Graphics grphcs) {
        // Do nothing here to keep the background transparent
        // We only want the border to be drawn, not the background
    }

    @Override
    protected void paintSafely(Graphics grphcs) {
        // Clip the content to the rounded area
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D roundRect = new RoundRectangle2D.Float(
            0, 0, textfield.getWidth() - 1, textfield.getHeight() - 1, round, round
        );
        g2.clip(roundRect);

        // Paint the text and other components
        super.paintSafely(g2);
        g2.dispose();
    }

    private class Border extends EmptyBorder {

        public int getRound() {
            return round;
        }

        public void setRound(int round) {
            this.round = round;
        }

        private Color color = new Color(0, 128, 128); // Green border color (teal-green)
        private int round;

        public Border(int border) {
            super(border, border, border, border);
        }

        @Override
        public void paintBorder(Component cmpnt, Graphics grphcs, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) grphcs.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); // Use the green border color
            g2.drawRoundRect(x, y, width - 1, height - 1, round, round);
            g2.dispose();
        }
    }
}