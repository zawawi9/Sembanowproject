/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.raven.form;

import com.raven.main.Main;
import config.koneksi;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import raven.dialog.IncorrectPassword;
import raven.dialog.Loading;
import raven.dialog.Logout;
import raven.dialog.MasukkanPass;
import raven.dialog.MasukkanUser;
import raven.dialog.RFIDTidakterdaftar;
import raven.dialog.loginberhasill;
import raven.dialog.password_username_kosong;

/**
 *
 * @author Fitrah
 */
public class FormLogin extends javax.swing.JFrame {

    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();
    private long startTime = 0;
    private boolean isRFIDInput = false;
    private final long RFID_THRESHOLD_MS = 500; // Contoh threshold 500ms, perlu disesuaikan
    private String rfidBuffer = "";

    public FormLogin() {
        initComponents();

        // Agar JFrame menerima input keyboard
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        

        // Resize image setelah komponen selesai diinisialisasi
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/com/raven/icon/loginpage123.jpg"));
        Image image = originalIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(image);

        // Mengatur gambar pada jLabel2
        jLabel2.setIcon(resizedIcon);
        SwingUtilities.invokeLater(() -> txtUsername.requestFocusInWindow());
        txtUsername.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String currentText = txtUsername.getText();

                if (startTime == 0 && currentText.length() > 0) {
                    startTime = System.currentTimeMillis();
                    rfidBuffer = currentText;
                } else if (startTime > 0) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime > RFID_THRESHOLD_MS) {
                        // Waktu input melebihi threshold, anggap ini input manual
                        startTime = 0;
                        isRFIDInput = false;
                        rfidBuffer = "";
                    }
                }

                // Asumsikan ID RFID memiliki panjang tertentu, ganti 10 dengan panjang ID Anda
                if (startTime > 0 && currentText.length() == 10 && (System.currentTimeMillis() - startTime) <= RFID_THRESHOLD_MS) {
                    isRFIDInput = true;
                    konversiRFIDtoLogin(currentText);
                    // Reset variabel waktu dan flag
                    startTime = 0;
                    isRFIDInput = false;
                    rfidBuffer = "";
                } else if (currentText.isEmpty()) {
                    // Reset jika field kosong
                    startTime = 0;
                    isRFIDInput = false;
                    rfidBuffer = "";
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // Reset jika ada penghapusan, karena ini kemungkinan interaksi manual
                startTime = 0;
                isRFIDInput = false;
                rfidBuffer = "";
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Tidak diperlukan untuk JTextField
            }
        });

        txtUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    NotifKeluar(); // Menutup form
                }
            }
        });
        txtUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
                }
            }
        });
        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }
            }
        });

        txtPassword.setText("Password");
        txtPassword.setEchoChar((char) 0);
        hidepw.setVisible(false);
        
        
    }
    private void NotifKeluar(){
        Window window = SwingUtilities.getWindowAncestor(FormLogin.this);
                    Logout keluar = new Logout((Frame)window, true);
            keluar.setVisible(true);
            if(keluar.isConfirmed()){
                     dispose();// Menutup form
            }
             
    }

    private void konversiRFIDtoLogin(String rfidID) {
        try {
            String sql = "SELECT username, password FROM karyawan WHERE id_karyawan = ?"; // Asumsikan ada kolom rfid_id
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setString(1, rfidID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                txtUsername.setText(rs.getString("username"));
                txtPassword.setText(rs.getString("password"));
                btnLoginActionPerformed(null); // Trigger tombol login secara otomatis
            } else {
                RFIDTidakterdaftar user = new RFIDTidakterdaftar(this, rootPaneCheckingEnabled);
            user.setVisible(true);
            user.fadeIn();
                txtUsername.setText(""); // Kosongkan kembali username field
            return;
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memproses RFID: " + e.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BGKANAN = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        showpw = new javax.swing.JLabel();
        hidepw = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        txtUsername = new javax.swing.JTextField();
        garisusername = new javax.swing.JPanel();
        garispw = new javax.swing.JPanel();
        btnLogin = new Custom.Custom_ButtonRounded1();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BGKANAN.setBackground(new java.awt.Color(255, 255, 255));
        BGKANAN.setLayout(null);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/loginpage123.jpg"))); // NOI18N
        jLabel2.setText("jLabel2");
        BGKANAN.add(jLabel2);
        jLabel2.setBounds(0, 0, 390, 450);

        getContentPane().add(BGKANAN, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 0, 390, 460));

        jPanel2.setBackground(new java.awt.Color(8, 112, 105));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        showpw.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/showpw.png"))); // NOI18N
        showpw.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showpwMouseClicked(evt);
            }
        });
        jPanel2.add(showpw, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 260, -1, -1));

        hidepw.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/hidepw.png"))); // NOI18N
        hidepw.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hidepwMouseClicked(evt);
            }
        });
        jPanel2.add(hidepw, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 260, -1, -1));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/pwikon.png"))); // NOI18N
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 260, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/usrikon.png"))); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 190, -1, -1));

        txtPassword.setBackground(new java.awt.Color(8, 112, 105));
        txtPassword.setFont(new java.awt.Font("Century", 0, 12)); // NOI18N
        txtPassword.setForeground(new java.awt.Color(255, 255, 255));
        txtPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPassword.setBorder(null);
        txtPassword.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPasswordFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPasswordFocusLost(evt);
            }
        });
        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });
        jPanel2.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 260, 220, 30));

        txtUsername.setBackground(new java.awt.Color(8, 112, 105));
        txtUsername.setFont(new java.awt.Font("Century", 0, 12)); // NOI18N
        txtUsername.setForeground(new java.awt.Color(255, 255, 255));
        txtUsername.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtUsername.setText("Username");
        txtUsername.setBorder(null);
        txtUsername.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUsernameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUsernameFocusLost(evt);
            }
        });
        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });
        jPanel2.add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 190, 220, 28));

        garisusername.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout garisusernameLayout = new javax.swing.GroupLayout(garisusername);
        garisusername.setLayout(garisusernameLayout);
        garisusernameLayout.setHorizontalGroup(
            garisusernameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
        );
        garisusernameLayout.setVerticalGroup(
            garisusernameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel2.add(garisusername, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 220, 220, 1));

        garispw.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout garispwLayout = new javax.swing.GroupLayout(garispw);
        garispw.setLayout(garispwLayout);
        garispwLayout.setHorizontalGroup(
            garispwLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
        );
        garispwLayout.setVerticalGroup(
            garispwLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jPanel2.add(garispw, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 290, 220, 1));

        btnLogin.setForeground(new java.awt.Color(0, 0, 0));
        btnLogin.setText("LOGIN");
        btnLogin.setFillClick(new java.awt.Color(255, 255, 255));
        btnLogin.setFillOriginal(new java.awt.Color(255, 255, 255));
        btnLogin.setFillOver(new java.awt.Color(255, 255, 255));
        btnLogin.setFocusCycleRoot(true);
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        jPanel2.add(btnLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 350, 220, 30));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Please sign in to your account");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 90, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("LOGIN");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, -1, -1));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Lupa kata sandi?");
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 310, -1, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 330, 460));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPasswordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPasswordFocusGained
        if (String.valueOf(txtPassword.getPassword()).equals("Password")) {
            txtPassword.setText("");
            txtPassword.setEchoChar('*');
        }
    }//GEN-LAST:event_txtPasswordFocusGained

    private void txtPasswordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPasswordFocusLost
        if (txtPassword.getPassword().length == 0) {
            txtPassword.setText("Password");
            txtPassword.setEchoChar((char) 0);
        }
    }//GEN-LAST:event_txtPasswordFocusLost

    private void txtUsernameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUsernameFocusGained
        String pass = txtUsername.getText();
        if (pass.equals("Username")) {
            txtUsername.setText("");
        }
    }//GEN-LAST:event_txtUsernameFocusGained

    private void txtUsernameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUsernameFocusLost
        String pass = txtUsername.getText();
        if (pass.equals("")) {
            txtUsername.setText("Username");
        }
    }//GEN-LAST:event_txtUsernameFocusLost

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || username.equals("username")) {
            MasukkanUser user = new MasukkanUser(this, rootPaneCheckingEnabled);
            user.setVisible(true);
            user.fadeIn();
            return;
        }
        if (password.isEmpty() || password.equals("password")) {
            MasukkanPass pw = new MasukkanPass(this, rootPaneCheckingEnabled);
            pw.setVisible(true);
            pw.fadeIn();
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);

            String sql = "SELECT username, status FROM karyawan WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                data.setUsername(rs.getString("username"));
                data.setLoginTime(LocalTime.now());
                data.setRole(rs.getString("status"));

                new Main().setVisible(true);
                this.dispose();
            } else {
                IncorrectPassword salah = new IncorrectPassword(this, rootPaneCheckingEnabled);
                salah.setVisible(true);
                salah.shake();
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Koneksi database gagal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void showpwMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showpwMouseClicked
        showpw.setVisible(false);
        hidepw.setVisible(true);
        txtPassword.setEchoChar((char) 0);
    }//GEN-LAST:event_showpwMouseClicked

    private void hidepwMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hidepwMouseClicked
        hidepw.setVisible(false);
        showpw.setVisible(true);
        txtPassword.setEchoChar('*');
    }//GEN-LAST:event_hidepwMouseClicked

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        Form_forgetPassword forgot = new Form_forgetPassword(this, true);
        forgot.setVisible(true);
    }//GEN-LAST:event_jLabel4MouseClicked

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
            java.util.logging.Logger.getLogger(FormLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BGKANAN;
    private Custom.Custom_ButtonRounded1 btnLogin;
    private javax.swing.JPanel garispw;
    private javax.swing.JPanel garisusername;
    private javax.swing.JLabel hidepw;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel showpw;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
