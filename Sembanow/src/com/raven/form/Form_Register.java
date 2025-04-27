package com.raven.form;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JTextField;

public class Form_Register extends javax.swing.JFrame {

    public Form_Register() {
        initComponents();
        getContentPane().setBackground(new Color(0, 100, 100));
        setLocationRelativeTo(null);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/com/raven/icon/emojis.com grocery-cart.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Gagal memuat logo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();
        lblRole = new javax.swing.JLabel();
        txtRole = new javax.swing.JTextField();
        lblNik = new javax.swing.JLabel();
        txtNik = new javax.swing.JTextField();
        lblSalary = new javax.swing.JLabel();
        txtSalary = new javax.swing.JTextField();
        btnRegister = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Register");

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Register");

        lblUsername.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblUsername.setForeground(new java.awt.Color(255, 255, 255));
        lblUsername.setText("username");
        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtUsername.setText("username");

        lblPassword.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblPassword.setForeground(new java.awt.Color(255, 255, 255));
        lblPassword.setText("password");
        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtPassword.setText("password");

        lblRole.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblRole.setForeground(new java.awt.Color(255, 255, 255));
        lblRole.setText("role");
        txtRole.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtRole.setText("role");

        lblNik.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblNik.setForeground(new java.awt.Color(255, 255, 255));
        lblNik.setText("NIK");
        txtNik.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtNik.setText("NIK");

        lblSalary.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblSalary.setForeground(new java.awt.Color(255, 255, 255));
        lblSalary.setText("salary");
        txtSalary.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtSalary.setText("salary");

        btnRegister.setBackground(new java.awt.Color(255, 255, 255));
        btnRegister.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnRegister.setText("Register");
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUsername)
                    .addComponent(txtUsername)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword)
                    .addComponent(lblRole)
                    .addComponent(txtRole)
                    .addComponent(lblNik)
                    .addComponent(txtNik)
                    .addComponent(lblSalary)
                    .addComponent(txtSalary)
                    .addComponent(btnRegister, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addGap(50, 50, 50))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lblTitle)
                .addGap(30, 30, 30)
                .addComponent(lblUsername)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblRole)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRole, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblNik)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNik, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblSalary)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );

        pack();
    }

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String role = txtRole.getText().trim();
        String nik = txtNik.getText().trim();
        String salaryStr = txtSalary.getText().trim();

        if (username.isEmpty() || username.equals("username")) {
            JOptionPane.showMessageDialog(this, "Username tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.isEmpty() || password.equals("password")) {
            JOptionPane.showMessageDialog(this, "Password tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (role.isEmpty() || role.equals("role")) {
            JOptionPane.showMessageDialog(this, "Role tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("karyawan")) {
            JOptionPane.showMessageDialog(this, "Role harus 'admin' atau 'karyawan'!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nik.isEmpty() || nik.equals("NIK")) {
            JOptionPane.showMessageDialog(this, "NIK tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (salaryStr.isEmpty() || salaryStr.equals("salary")) {
            JOptionPane.showMessageDialog(this, "Salary tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
            if (salary <= 0) {
                JOptionPane.showMessageDialog(this, "Salary harus lebih dari 0!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Salary harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);

            String sql = "INSERT INTO karyawan (username, password, nik, status, gaji) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, nik);
            pstmt.setString(4, role.toLowerCase());
            pstmt.setDouble(5, salary);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Registrasi berhasil!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                new Form_Login().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registrasi gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Koneksi database gagal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
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
                new Form_Register().setVisible(true);
            }
        });
    }

    private javax.swing.JButton btnRegister;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblNik;
    private javax.swing.JLabel lblSalary;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtRole;
    private javax.swing.JTextField txtNik;
    private javax.swing.JTextField txtSalary;
}