package com.raven.form;

import com.raven.main.Main;
import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JTextField;

public class Form_Login extends javax.swing.JFrame {

    public Form_Login() {
        initComponents();
        getContentPane().setBackground(new Color(0, 100, 100));
        setLocationRelativeTo(null);
        setSize(861, 513); // Atur ukuran 861x513

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/com/raven/icon/sembakofixed.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Gagal memuat logo: " + e.getMessage());
            e.printStackTrace();
        }
         // === Tambahin KeyListener ke txtUsername ===
    txtUsername.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                txtPassword.requestFocus(); // Enter di Username --> Pindah ke Password
            }
        }
    });

    // === Tambahin KeyListener ke txtPassword ===
    txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
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
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();
        btnLogin = new javax.swing.JButton();
        lblRegister = new javax.swing.JLabel();
        lblForget = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Selamat datang di Sembanow");

        lblUsername.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblUsername.setForeground(new java.awt.Color(255, 255, 255));
        lblUsername.setText("username");
        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 14));

        lblPassword.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblPassword.setForeground(new java.awt.Color(255, 255, 255));
        lblPassword.setText("password");
        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 14));

        btnLogin.setBackground(new java.awt.Color(255, 255, 255));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        lblRegister.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblRegister.setForeground(new java.awt.Color(255, 255, 255));
        lblRegister.setText("Register");
        lblRegister.setCursor(new java.awt

.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new Form_Register().setVisible(true);
                dispose();
            }
        });

        lblForget.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblForget.setForeground(new java.awt.Color(255, 255, 255));
        lblForget.setText("Forget");
        lblForget.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblForget.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new Form_Forget().setVisible(true);
                dispose();
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
                    .addComponent(lblUsername)
                    .addComponent(txtUsername)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword)
                    .addComponent(btnLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblRegister)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblForget)))
                .addGap(280, 280, 280))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(lblTitle)
                .addGap(50, 50, 50)
                .addComponent(lblUsername)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRegister)
                    .addComponent(lblForget))
                .addGap(80, 80, 80))
        );

        pack();
    }

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || username.equals("username")) {
            JOptionPane.showMessageDialog(this, "Username tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.isEmpty() || password.equals("password")) {
            JOptionPane.showMessageDialog(this, "Password tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
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
                new Form_Login().setVisible(true);
            }
        });
    }

    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblRegister;
    private javax.swing.JLabel lblForget;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JTextField txtPassword;
}