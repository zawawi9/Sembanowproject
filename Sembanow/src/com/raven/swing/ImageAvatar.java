package com.raven.swing;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class ImageAvatar extends JComponent {

    // Getters and Setters
    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        updateImage = false;
        repaint();
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        updateImage = false;
        repaint();
    }

    public Color getBorderColor1() {
        return borderColor1;
    }

    public void setBorderColor1(Color borderColor1) {
        this.borderColor1 = borderColor1;
        updateImage = false;
        repaint();
    }

    public Color getBorderColor2() {
        return borderColor2;
    }

    public void setBorderColor2(Color borderColor2) {
        this.borderColor2 = borderColor2;
        updateImage = false;
        repaint();
    }

    public float getShadowOpacity() {
        return shadowOpacity;
    }

    public void setShadowOpacity(float shadowOpacity) {
        this.shadowOpacity = Math.max(0, Math.min(1, shadowOpacity));
        updateImage = false;
        repaint();
    }

    public int getShadowSize() {
        return shadowSize;
    }

    public void setShadowSize(int shadowSize) {
        this.shadowSize = shadowSize;
        updateImage = false;
        repaint();
    }

    public boolean isGlowEffect() {
        return glowEffect;
    }

    public void setGlowEffect(boolean glowEffect) {
        this.glowEffect = glowEffect;
        updateImage = false;
        repaint();
    }

    // Fields
    private Icon icon;
    private int borderSize = 4;
    private Color borderColor1 = Color.WHITE; // Warna gradien 1
    private Color borderColor2 = Color.GRAY;  // Warna gradien 2
    private Image image;
    private boolean updateImage = false;
    private int oldWidth;
    private int oldHeight;
    private float shadowOpacity = 0.5f; // Opacity bayangan
    private int shadowSize = 8;         // Ukuran bayangan
    private boolean glowEffect = false;  // Efek cahaya

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2d = (Graphics2D) grphcs;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();
        if (!updateImage || oldWidth != width || oldHeight != height) {
            createImage();
        }
        if (image != null) {
            g2d.drawImage(image, 0, 0, null);
        }
        super.paintComponent(grphcs);
    }

    private void createImage() {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) return;

        // Gunakan resolusi 4x untuk kualitas tinggi
        int scaleFactor = 4;
        int scaledWidth = width * scaleFactor;
        int scaledHeight = height * scaleFactor;

        BufferedImage buff = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buff.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.scale(scaleFactor, scaleFactor);

        // Hitung dimensi dengan mempertimbangkan bayangan dan border
        int frameSize = 2;
        int border = borderSize * 2;
        int shadowPadding = shadowSize * 2;
        int totalPadding = frameSize * 2 + border + shadowPadding;
        int diameter = Math.min(width, height) - totalPadding;
        int x = (width - diameter) / 2;
        int y = (height - diameter) / 2;

        // Gambar bayangan
        if (shadowSize > 0) {
            int shadowDiameter = diameter + border + frameSize * 2;
            BufferedImage shadow = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2Shadow = shadow.createGraphics();
            g2Shadow.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2Shadow.setColor(new Color(0, 0, 0, shadowOpacity));
            g2Shadow.fillOval(x - frameSize + shadowSize / 2, y - frameSize + shadowSize / 2, shadowDiameter, shadowDiameter);
            g2Shadow.dispose();
            g2.drawImage(blurImage(shadow, shadowSize), 0, 0, null);
        }

        // Gambar bingkai hitam
        int frameDiameter = diameter + border + frameSize * 2;
        g2.setColor(Color.BLACK);
        g2.fillOval(x - frameSize, y - frameSize, frameDiameter, frameDiameter);

        // Gambar border dengan gradien
        if (borderSize > 0) {
            diameter += border;
            GradientPaint gradient = new GradientPaint(
                x, y, borderColor1,
                x + diameter, y + diameter, borderColor2
            );
            g2.setPaint(gradient);
            g2.fillOval(x, y, diameter, diameter);
        }

        // Gambar background lingkaran
        if (isOpaque()) {
            g2.setColor(getBackground());
            diameter -= border;
            g2.fillOval(x + borderSize, y + borderSize, diameter, diameter);
        }

        // Gambar efek cahaya (glow)
        if (glowEffect && borderSize > 0) {
            int glowDiameter = diameter + borderSize * 2;
            BufferedImage glow = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2Glow = glow.createGraphics();
            g2Glow.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2Glow.setPaint(new RadialGradientPaint(
                x + glowDiameter / 2, y + glowDiameter / 2, glowDiameter / 2,
                new float[]{0.0f, 1.0f},
                new Color[]{new Color(255, 255, 255, 100), new Color(255, 255, 255, 0)}
            ));
            g2Glow.fillOval(x, y, glowDiameter, glowDiameter);
            g2Glow.dispose();
            g2.drawImage(glow, 0, 0, null);
        }

        // Gambar ikon
        BufferedImage img = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2Img = img.createGraphics();
        g2Img.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2Img.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2Img.fillOval(0, 0, diameter, diameter);

        Composite composite = g2Img.getComposite();
        g2Img.setComposite(AlphaComposite.SrcIn);
        Image iconImage = toImage(icon);
        g2Img.drawImage(iconImage, 0, 0, diameter, diameter, null);
        g2Img.setComposite(composite);
        g2Img.dispose();

        g2.drawImage(img, x + borderSize, y + borderSize, null);
        g2.dispose();

        // Skala kembali ke ukuran asli
        image = buff.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        oldWidth = width;
        oldHeight = height;
        updateImage = true;
    }

    private Image toImage(Icon icon) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon).getImage();
        }
        return null;
    }

    // Fungsi untuk membuat efek blur pada bayangan
    private BufferedImage blurImage(BufferedImage image, int radius) {
        float[] matrix = new float[9];
        for (int i = 0; i < 9; i++) {
            matrix[i] = 1.0f / 9.0f;
        }
        java.awt.image.ConvolveOp op = new java.awt.image.ConvolveOp(
            new java.awt.image.Kernel(3, 3, matrix),
            java.awt.image.ConvolveOp.EDGE_NO_OP,
            null
        );
        BufferedImage blurred = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        op.filter(image, blurred);
        return blurred;
    }
}