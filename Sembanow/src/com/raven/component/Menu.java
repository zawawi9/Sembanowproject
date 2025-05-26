package com.raven.component;

import com.raven.form.data;
import com.raven.event.EventMenuSelected;
import com.raven.model.Model_Menu;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Path2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFrame;

public class Menu extends javax.swing.JPanel {

    private EventMenuSelected event;

    public void addEventMenuSelected(EventMenuSelected event) {
        this.event = event;
        listMenu1.addEventMenuSelected(event);
    }

    public Menu() {
        initComponents();
        setOpaque(false);
        listMenu1.setOpaque(false);
        init();
    }

    private void init() {
        // Ambil data dari kelas data
        String username = data.getUsername() != null ? data.getUsername() : "Pengguna";
        String loginTimeStr = "07:00"; // Default jika loginTime null
        if (data.getLoginTime() != null) {
            LocalDateTime loginTime = data.getLoginTime();
            loginTimeStr = loginTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        String role = data.getRole() != null ? data.getRole() : "hadir";

        // Item menu
        listMenu1.addItem(new Model_Menu("", "Pengguna", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("8", username, Model_Menu.MenuType.MENU)); // Ganti "Almil" dengan username
        listMenu1.addItem(new Model_Menu("", "Presensi Masuk", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("1", loginTimeStr, Model_Menu.MenuType.MENU)); // Ganti "07.00" dengan waktu login
        listMenu1.addItem(new Model_Menu("1", "Keterangan", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("2", role, Model_Menu.MenuType.MENU)); // Ganti "hadir" dengan role
        listMenu1.addItem(new Model_Menu("", "", Model_Menu.MenuType.EMPTY));
        listMenu1.addItem(new Model_Menu("", "=================", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("", "", Model_Menu.MenuType.EMPTY));

        // Tambahkan item menu berdasarkan role
        if ("admin".equals(role)) {
            initAdminMenu();
        } else if ("karyawan".equals(role)) {
            initKaryawanMenu();
        }

        // Item penutup
        listMenu1.addItem(new Model_Menu("", "", Model_Menu.MenuType.EMPTY));
        listMenu1.addItem(new Model_Menu("", "=================", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("", "", Model_Menu.MenuType.EMPTY));
        listMenu1.addItem(new Model_Menu("10", "Logout", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("", "", Model_Menu.MenuType.EMPTY));
    }

    private void initAdminMenu() {
        listMenu1.addItem(new Model_Menu("3", "Dashboard", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("4", "Produk", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("5", "Transaksi", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("8", "Pendataan", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("7", "Keuangan", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("9", "Extra", Model_Menu.MenuType.MENU));
    }

    private void initKaryawanMenu() {
        listMenu1.addItem(new Model_Menu("4", "Produk", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("5", "Transaksi", Model_Menu.MenuType.MENU));
    }

    @Override
    protected void paintChildren(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint g = new GradientPaint(
            0, 0, Color.decode("#006464"),
            0, getHeight(), Color.decode("#00A0A0")
        );
        int height = 140;
        Path2D.Float f = new Path2D.Float();
        f.moveTo(0, 0);
        f.curveTo(0, 0, 0, 70, 100, 70);
        f.curveTo(100, 70, getWidth(), 70, getWidth(), height);
        f.lineTo(getWidth(), getHeight());
        f.lineTo(0, getHeight());
        g2.setColor(Color.decode("#00A0A0"));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setPaint(g);
        g2.fill(f);
        super.paintChildren(grphcs);
    }

    private int x;
    private int y;

    public void initMoving(JFrame fram) {
        profile1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                x = me.getX();
                y = me.getY();
            }
        });
        profile1.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                fram.setLocation(me.getXOnScreen() - x, me.getYOnScreen() - y);
            }
        });
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listMenu1 = new com.raven.swing.ListMenu<>();
        profile1 = new com.raven.component.Profile();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(listMenu1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(profile1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(profile1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(listMenu1, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.swing.ListMenu<String> listMenu1;
    private com.raven.component.Profile profile1;
    // End of variables declaration//GEN-END:variables
}
