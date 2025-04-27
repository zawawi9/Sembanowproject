package com.raven.form;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JTextField;

public class Form_Forget extends javax.swing.JFrame {

    public Form_Forget() {
        initComponents();
        getContentPane().setBackground(new Color(0, 100, 100));
        setLocationRelativeTo(null);
        setSize(861, 513); // Atur ukuran 861x513

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/com/raven/icon/emojis.com grocery-cart.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Gagal memuat logo: " + e.getMessage());
            e.printStackTrace();
        }
         // === Tambahin KeyListener ke txtUsername ===
    txtNik.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                txtNik.requestFocus(); // Enter di Username --> Pindah ke Password
            }
        }
    });

    // === Tambahin KeyListener ke txtPassword ===
    txtNewPassword.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                btnLogin.doClick(); // Enter di Password --> Klik tombol Login
            }
        }
    });
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblNik = new javax.swing.JLabel();
        txtNik = new javax.swing.JTextField();
        lblNewPassword = new javax.swing.JLabel();
        txtNewPassword = new javax.swing.JTextField();
        btnLogin = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Forget Password");

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Forget");

        lblNik.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblNik.setForeground(new java.awt.Color(255, 255, 255));
        lblNik.setText("NIK");
        txtNik.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtNik.setText("NIK");

        lblNewPassword.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblNewPassword.setForeground(new java.awt.Color(255, 255, 255));
        lblNewPassword.setText("new password");
        txtNewPassword.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtNewPassword.setText("new password");

        btnLogin.setBackground(new java.awt.Color(255, 255, 255));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(280, 280, 280)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblNik)
                    .addComponent(txtNik)
                    .addComponent(lblNewPassword)
                    .addComponent(txtNewPassword)
                    .addComponent(btnLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addGap(280, 280, 280))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(lblTitle)
                .addGap(50, 50, 50)
                .addComponent(lblNik)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNik, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblNewPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80))
        );

        pack();
    }

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        String nik = txtNik.getText().trim();
        String newPassword = txtNewPassword.getText().trim();

        if (nik.isEmpty() || nik.equals("NIK")) {
            JOptionPane.showMessageDialog(this, "NIK tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (newPassword.isEmpty() || newPassword.equals("new password")) {
            JOptionPane.showMessageDialog(this, "Kata sandi baru tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String url = "jdbc:mysql://localhost/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);

            String checkSql = "SELECT nik FROM karyawan WHERE nik = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, nik);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String updateSql = "UPDATE karyawan SET password = ? WHERE nik = ?";
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, newPassword);
                pstmt.setString(2, nik);
                int rowsUpdated = pstmt.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Kata sandi berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    new Form_Login().setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui kata sandi!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "NIK tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Koneksi database gagal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Form_Forget().setVisible(true);
            }
        });
    }

    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel lblNik;
    private javax.swing.JLabel lblNewPassword;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTextField txtNik;
    private javax.swing.JTextField txtNewPassword;
}