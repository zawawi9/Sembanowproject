package com.raven.main;

import com.raven.component.MenuLayout;
import com.raven.event.EventMenuSelected;
import com.raven.form.Form_2;
import com.raven.form.Form_transaksi;
import com.raven.form.MainForm;
import com.raven.form.pilihan;
import com.raven.form.FormLogin;
import com.raven.form.Form_searchproduk;
import com.raven.form.data;
import com.raven.form.pilihanKeuangan;
import com.raven.form.pilihanpendataan;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import raven.dialog.LogoutSure;

public class Main extends javax.swing.JFrame {

    private final MigLayout layout;
    private final MainForm main;
    private final MenuLayout menu;
    private final Animator animator;
    private final int menuWidth = 215;

    public Main() {
        initComponents();
        layout = new MigLayout("fill", "0[fill]0", "0[fill]0");
        main = new MainForm();
        menu = new MenuLayout();
        menu.getMenu().initMoving(Main.this);
        main.initMoving(Main.this);
        mainPanel.setLayer(menu, JLayeredPane.POPUP_LAYER);
        mainPanel.setLayout(layout);
        mainPanel.add(main, "pos 0 0 100% 100%");
        mainPanel.add(menu, "pos -215 0 215px 100%", 0);
        menu.setShow(false);
        menu.setVisible(false);
        
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/com/raven/icon/emojis.com grocery-cart.png"));
            setIconImage(icon.getImage()); // Atur logo kustom untuk jendela dan taskbar
        } catch (Exception e) {
            System.err.println("Gagal memuat logo: " + e.getMessage());
            e.printStackTrace();
        }

        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                float x = (fraction * menuWidth);
                float mainX = (1 - fraction) * menuWidth;
                if (menu.isShow()) {
                    x = -x;
                } else {
                    x -= menuWidth;
                    mainX = fraction * menuWidth;
                }
                layout.setComponentConstraints(menu, "pos " + (int) x + " 0 215px 100%");
                layout.setComponentConstraints(main, "pos " + (int) mainX + " 0 100% 100%");
                mainPanel.revalidate();
                mainPanel.repaint();
            }

            @Override
            public void end() {
                menu.setShow(!menu.isShow());
                if (!menu.isShow()) {
                    menu.setVisible(false);
                    layout.setComponentConstraints(main, "pos 0 0 100% 100%");
                } else {
                    layout.setComponentConstraints(main, "pos 215 0 100% 100%");
                }
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        };
        animator = new Animator(200, target);
        animator.setResolution(0);
        animator.setDeceleration(0.5f);
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    if (!animator.isRunning()) {
                        if (menu.isShow()) {
                            animator.start();
                        }
                    }
                }
            }
        });
        main.addEventMenu(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!animator.isRunning()) {
                    if (!menu.isShow()) {
                        menu.setVisible(true);
                        animator.start();
                    } else {
                        animator.start();
                    }
                }
            }
        });
        menu.getMenu().addEventMenuSelected(new EventMenuSelected() {
            @Override
            public void selected(int index) {
                String role = data.getRole();
                if (index == 8) {
                    main.show(new Form_2());
                }else if (index == 10 && "admin".equals(role)) {
                    main.show(new pilihan());
                } else if (index == 12 && "admin".equals(role)) {
                    main.show(new pilihanpendataan());
                } else if (index == 11 && "admin".equals(role)) {
                    main.show(new Form_transaksi());
                } else if (index == 13 && "admin".equals(role)) {
                    main.show(new pilihanKeuangan());
                } else if (index == 9 && "admin".equals(role)) {
                    main.show(new Form_2());
                } else if (index == 9 && "karyawan".equals(role)) {
                    main.show(new pilihan());
                } else if (index == 10 && "karyawan".equals(role)) {
                    main.show(new Form_transaksi());
                } else if (index == 18 && "admin".equals(role) || index == 14 && "karyawan".equals(role)) {
                    // Logout
                    Window window = SwingUtilities.getWindowAncestor(Main.this);
            LogoutSure sure = new LogoutSure((Frame)window, true);
            sure.setVisible(true);
            if(sure.isConfirmed()){
                    data.clearSession();
                    dispose();
                    new FormLogin().setVisible(true);
            }else{
                sure.dispose();
            }
                    }
                
            }
        });
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        mainPanel.setBackground(new java.awt.Color(250, 250, 250));
        mainPanel.setOpaque(true);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1100, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 645, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FormLogin().setVisible(true); // Mulai dari Form_Login
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane mainPanel;
    // End of variables declaration//GEN-END:variables
}
