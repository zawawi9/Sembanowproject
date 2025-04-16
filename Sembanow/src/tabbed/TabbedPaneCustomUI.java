package tabbed;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class TabbedPaneCustomUI extends BasicTabbedPaneUI {

    private final TabbedPaneCustom tab;

    public TabbedPaneCustomUI(TabbedPaneCustom tab) {
        this.tab = tab;
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
    }

    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        // Atur tinggi tab agar cukup untuk teks dan garis biru
        return fontHeight + 15; // Tinggi teks + padding untuk garis biru
    }

    @Override
    protected void layoutLabel(int tabPlacement, FontMetrics metrics, int tabIndex, String title, Icon icon, Rectangle tabRect, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        // Hitung posisi teks agar berada di atas
        textRect.x = tabRect.x + (tabRect.width - metrics.stringWidth(title)) / 2; // Posisi horizontal tengah
        textRect.y = tabRect.y + 5; // Posisi vertikal dekat bagian atas tab (5 piksel dari atas)
        textRect.width = metrics.stringWidth(title);
        textRect.height = metrics.getHeight();

        // Jika ada ikon, sesuaikan posisi ikon (opsional)
        if (icon != null) {
            iconRect.x = tabRect.x + 5;
            iconRect.y = tabRect.y + (tabRect.height - icon.getIconHeight()) / 2;
            iconRect.width = icon.getIconWidth();
            iconRect.height = icon.getIconHeight();
        }
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int width, int height, boolean isSelected) {
        // Tidak perlu menggambar border kustom
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        // Tidak perlu indikator fokus
    }

    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
        // Gambar tab menggunakan default behavior
        super.paintTabArea(g, tabPlacement, selectedIndex);

        // Gambar garis biru di bawah tab yang aktif
        if (selectedIndex >= 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle rec = getTabBounds(tabPane, selectedIndex);
            FontMetrics metrics = g2.getFontMetrics(); // Dapatkan metrik font untuk menghitung tinggi teks
            g2.setColor(new Color(0, 102, 204)); // Warna biru
            g2.setStroke(new BasicStroke(2)); // Ketebalan garis 2px
            int lineY = rec.y + metrics.getHeight() + 5; // Garis berada di bawah teks (teks + 5 piksel)
            g2.drawLine(rec.x, lineY, rec.x + rec.width, lineY);
            g2.dispose();
        }
    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        // Biarkan default behavior
        super.paintContentBorder(g, tabPlacement, selectedIndex);
    }
}