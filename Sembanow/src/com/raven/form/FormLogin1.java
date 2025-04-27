package com.raven.form;

import raven.dialog.pesankeluarapp;
import com.raven.main.Main;
import config.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import raven.dialog.loginberhasill;
import raven.dialog.password_username_kosong;
import raven.dialog.IncorrectPassword;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.event.*;

/**
 *
 * @author Toshiba
 */
public class FormLogin1 extends javax.swing.JFrame {
    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();

    int xx, xy;

    public FormLogin1() {
        initComponents();
        SwingUtilities.invokeLater(() -> usernametxt.requestFocusInWindow());
        usernametxt.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer = new Timer();

            private void triggerLogin() {
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(() -> prosesLoginOtomatis());
                    }
                }, 500); // Delay untuk menghindari trigger setiap karakter
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                triggerLogin();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // Tidak perlu trigger saat menghapus teks
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Tidak diperlukan untuk JTextField
            }
        });

        pwtxt.setText("Password");
        pwtxt.setEchoChar((char) 0);
        hidepw.setVisible(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        BExit = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        BLogin = new Custom.Custom_ButtonRounded1();
        BLupapw = new javax.swing.JButton();
        pwpanel = new javax.swing.JPanel();
        icongembok = new javax.swing.JLabel();
        pwtxt = new javax.swing.JPasswordField();
        usernamepanel = new javax.swing.JPanel();
        usernametxt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        showpw = new javax.swing.JLabel();
        hidepw = new javax.swing.JLabel();

        jToggleButton1.setText("jToggleButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(249, 247, 228));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(8, 112, 105));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/App_login-removebg-preview (2).png"))); // NOI18N
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-30, 120, 355, 350));

        jLabel6.setFont(new java.awt.Font("Arial", 1, 27)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("WELCOME TO,");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, 50));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/sembakofixed.png"))); // NOI18N
        jLabel9.setText("jLabel9");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 120, 90));

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 330, 470);

        BExit.setBackground(new java.awt.Color(204, 0, 0));
        BExit.setForeground(new java.awt.Color(255, 255, 255));
        BExit.setText("X");
        BExit.setBorder(null);
        BExit.setBorderPainted(false);
        BExit.setFocusPainted(false);
        BExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BExitMouseClicked(evt);
            }
        });
        jPanel1.add(BExit);
        BExit.setBounds(680, 0, 40, 24);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setText("account!");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(490, 70, 100, 50);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel8.setText("Please Sign in your");
        jPanel1.add(jLabel8);
        jLabel8.setBounds(420, 40, 220, 50);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/logopreview.png"))); // NOI18N
        jPanel1.add(jLabel2);
        jLabel2.setBounds(460, 110, 130, 110);

        BLogin.setBackground(new java.awt.Color(153, 255, 153));
        BLogin.setText("LOGIN");
        BLogin.setFillClick(new java.awt.Color(2, 75, 70));
        BLogin.setFillOriginal(new java.awt.Color(8, 112, 105));
        BLogin.setFillOver(new java.awt.Color(28, 178, 167));
        BLogin.setFont(new java.awt.Font("Lucida Sans", 0, 12)); // NOI18N
        BLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BLoginActionPerformed(evt);
            }
        });
        jPanel1.add(BLogin);
        BLogin.setBounds(430, 290, 200, 30);

        BLupapw.setBackground(new java.awt.Color(249, 247, 228));
        BLupapw.setText("Lupa kata sandi?");
        BLupapw.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        BLupapw.setBorderPainted(false);
        BLupapw.setContentAreaFilled(false);
        BLupapw.setFocusPainted(false);
        BLupapw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BLupapwActionPerformed(evt);
            }
        });
        jPanel1.add(BLupapw);
        BLupapw.setBounds(430, 320, 100, 18);

        pwpanel.setBackground(new java.awt.Color(255, 255, 255));
        pwpanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pwpanel.setLayout(null);

        icongembok.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ikon/password-lock-icon-free-vector-removebg-preview (1).png"))); // NOI18N
        pwpanel.add(icongembok);
        icongembok.setBounds(7, 7, 0, 0);

        pwtxt.setFont(new java.awt.Font("Century", 0, 12)); // NOI18N
        pwtxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pwtxt.setBorder(null);
        pwtxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                pwtxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                pwtxtFocusLost(evt);
            }
        });
        pwpanel.add(pwtxt);
        pwtxt.setBounds(28, 1, 171, 28);

        jPanel1.add(pwpanel);
        pwpanel.setBounds(430, 250, 200, 30);

        usernamepanel.setBackground(new java.awt.Color(255, 255, 255));
        usernamepanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        usernametxt.setFont(new java.awt.Font("Century", 0, 12)); // NOI18N
        usernametxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        usernametxt.setText("Username");
        usernametxt.setBorder(null);
        usernametxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                usernametxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                usernametxtFocusLost(evt);
            }
        });
        usernametxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernametxtActionPerformed(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ikon/usernameikon (1).png"))); // NOI18N

        javax.swing.GroupLayout usernamepanelLayout = new javax.swing.GroupLayout(usernamepanel);
        usernamepanel.setLayout(usernamepanelLayout);
        usernamepanelLayout.setHorizontalGroup(
            usernamepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usernamepanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernametxt, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
        );
        usernamepanelLayout.setVerticalGroup(
            usernamepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(usernametxt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usernamepanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap())
        );

        jPanel1.add(usernamepanel);
        usernamepanel.setBounds(430, 210, 185, 30);

        showpw.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ikon/icons8-show-password-25.png"))); // NOI18N
        showpw.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showpwMouseClicked(evt);
            }
        });
        jPanel1.add(showpw);
        showpw.setBounds(635, 250, 0, 0);

        hidepw.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ikon/icons8-hide-password-25.png"))); // NOI18N
        hidepw.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hidepwMouseClicked(evt);
            }
        });
        jPanel1.add(hidepw);
        hidepw.setBounds(635, 250, 0, 0);

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 720, 460));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private Timer timer;

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        xx = evt.getX();
        xy = evt.getY();
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xx, y - xy);
    }//GEN-LAST:event_formMouseDragged

    private void BExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BExitMouseClicked
        pesankeluarapp ConfirmDialog = new pesankeluarapp(this, true);

        ConfirmDialog.setVisible(true);
    }//GEN-LAST:event_BExitMouseClicked

    private void BLupapwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BLupapwActionPerformed
        // Membuka dialog lupa password
        formlupapw lupaPasswordDialog = new formlupapw(this, true); // 'this' adalah parent frame
        lupaPasswordDialog.setVisible(true);
    }//GEN-LAST:event_BLupapwActionPerformed

    private void BLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BLoginActionPerformed
        String username = usernametxt.getText().trim();
        String password = pwtxt.getText().trim();

        if (username.equals("Username") || username.isEmpty() || password.equals("Password") || password.isEmpty()) {
            password_username_kosong Pwusrkosong = new password_username_kosong(this, true);
            Pwusrkosong.setVisible(true);
            return;
        }

        try {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("status");
                int idKaryawan = rs.getInt("id_karyawan");

                // Simpan waktu login
                LocalTime loginTime = LocalTime.now();

                // Simpan data ke tabel absensi
                String sqlAbsensi = "INSERT INTO absensi (id_karyawan, tanggal, check_in) VALUES (?, ?, ?)";
                PreparedStatement stmtAbsensi = cn.prepareStatement(sqlAbsensi);
                stmtAbsensi.setInt(1, idKaryawan);
                stmtAbsensi.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                stmtAbsensi.setTime(3, java.sql.Time.valueOf(loginTime));
                stmtAbsensi.executeUpdate();
                stmtAbsensi.close();

                // Simpan data ke UserSession
                UserSession.setUsername(username);
                UserSession.setLoginTime(loginTime);
                UserSession.setRole(role);

                // Tampilkan dialog login berhasil
                loginberhasill Loginberhasil = new loginberhasill(this, true);
                Loginberhasil.setVisible(true);

                // Buka form Main dengan role yang sesuai
                Main mainForm = new Main(role);
                mainForm.setVisible(true);

                this.dispose();
            } else {
                IncorrectPassword Pwsalah = new IncorrectPassword(this, true);
                Pwsalah.setVisible(true);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }//GEN-LAST:event_BLoginActionPerformed

    private void prosesLoginOtomatis() {
        String username = usernametxt.getText().trim();
        String password = pwtxt.getText().trim();

        if (username.equals("Username") || username.isEmpty() || password.equals("Password") || password.isEmpty()) {
            password_username_kosong Pwusrkosong = new password_username_kosong(this, true);
            Pwusrkosong.setVisible(true);
            return;
        }

        try {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("status");
                int idKaryawan = rs.getInt("id_karyawan");

                // Simpan waktu login
                LocalTime loginTime = LocalTime.now();

                // Simpan data ke tabel absensi
                String sqlAbsensi = "INSERT INTO absensi (id_karyawan, tanggal, check_in) VALUES (?, ?, ?)";
                PreparedStatement stmtAbsensi = cn.prepareStatement(sqlAbsensi);
                stmtAbsensi.setInt(1, idKaryawan);
                stmtAbsensi.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                stmtAbsensi.setTime(3, java.sql.Time.valueOf(loginTime));
                stmtAbsensi.executeUpdate();
                stmtAbsensi.close();

                // Simpan data ke UserSession
                UserSession.setUsername(username);
                UserSession.setLoginTime(loginTime);
                UserSession.setRole(role);

                // Tampilkan dialog login berhasil
                loginberhasill Loginberhasil = new loginberhasill(this, true);
                Loginberhasil.setVisible(true);

                // Buka form Main dengan role yang sesuai
                Main mainForm = new Main(role);
                mainForm.setVisible(true);

                this.dispose();
            } else {
                IncorrectPassword Pwsalah = new IncorrectPassword(this, true);
                Pwsalah.setVisible(true);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void usernametxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_usernametxtFocusGained
        String pass = usernametxt.getText();
        if (pass.equals("Username")) {
            usernametxt.setText("");
        }
    }//GEN-LAST:event_usernametxtFocusGained

    private void usernametxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_usernametxtFocusLost
        String pass = usernametxt.getText();
        if (pass.equals("")) {
            usernametxt.setText("Username");
        }
    }//GEN-LAST:event_usernametxtFocusLost

    private void pwtxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pwtxtFocusGained
        if (String.valueOf(pwtxt.getPassword()).equals("Password")) {
            pwtxt.setText("");
            pwtxt.setEchoChar('*');
        }
    }//GEN-LAST:event_pwtxtFocusGained

    private void pwtxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pwtxtFocusLost
        if (pwtxt.getPassword().length == 0) {
            pwtxt.setText("Password");
            pwtxt.setEchoChar((char) 0);
        }
    }//GEN-LAST:event_pwtxtFocusLost

    private void showpwMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showpwMouseClicked
        showpw.setVisible(false);
        hidepw.setVisible(true);
        pwtxt.setEchoChar((char) 0);
    }//GEN-LAST:event_showpwMouseClicked

    private void hidepwMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hidepwMouseClicked
        hidepw.setVisible(false);
        showpw.setVisible(true);
        pwtxt.setEchoChar('*');
    }//GEN-LAST:event_hidepwMouseClicked

    private void usernametxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernametxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernametxtActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormLogin1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormLogin1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormLogin1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormLogin1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormLogin1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BExit;
    private Custom.Custom_ButtonRounded1 BLogin;
    private javax.swing.JButton BLupapw;
    private javax.swing.JLabel hidepw;
    private javax.swing.JLabel icongembok;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JPanel pwpanel;
    private javax.swing.JPasswordField pwtxt;
    private javax.swing.JLabel showpw;
    private javax.swing.JPanel usernamepanel;
    private javax.swing.JTextField usernametxt;
    // End of variables declaration//GEN-END:variables
}
