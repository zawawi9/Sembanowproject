/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.raven.form;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import raven.dialog.DataAda;
import raven.dialog.JabatanOnly;
import raven.dialog.LengkapiData;
import raven.dialog.Loading;
import raven.dialog.SesuaiFormat;

/**
 *
 * @author Fitrah
 */
public class Form_tbhKaryawan extends javax.swing.JDialog {

    /**
     * Creates new form Form_tbhSupplier
     */
    private List<String> karyawantype = Arrays.asList("admin", "karyawan");
    private DocumentListener myListener;
    public Form_tbhKaryawan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(parent);
        fadeIn();
        jabatan.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                listen();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                listen();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                listen();
            }
        }
        );
        NIKKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != '\b') {
            evt.consume(); // Mengabaikan input jika bukan angka atau backspace
        }
    }
});
        Telepon_Karyawan.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != '\b') {
            evt.consume(); // Mengabaikan input jika bukan angka atau backspace
        }
    }
});
        Nama_Karyawan.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != '\b') {
            evt.consume(); // Mengabaikan input jika bukan angka atau backspace
        }
    }
});
        jabatan.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != '\b') {
            evt.consume(); // Mengabaikan input jika bukan angka atau backspace
        }
    }
});
        
        NIKKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                Nama_Karyawan.requestFocus();
            }
            }
        });
        Nama_Karyawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                Telepon_Karyawan.requestFocus();
            }
            }
        });
        Telepon_Karyawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                Alamat_Karyawan.requestFocus();
            }
            }
        });
        Alamat_Karyawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                RFIDKaryawan.requestFocus();
            }
            }
        });
        RFIDKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                UNKaryawan.requestFocus();
            }
            }
        });
        UNKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                PWKaryawan.requestFocus();
            }
            }
        });
        PWKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                jabatan.requestFocus();
            }
            }
        });
        jabatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                gaji.requestFocus();
            }
            }
        });
        BoxTipe.addActionListener((evt) -> {
            String selectedType = (String)BoxTipe.getSelectedItem();
            if (selectedType!=null && !selectedType.isEmpty()) {
                jabatan.setText(selectedType);
            }
        });
    }
    public void listen(){
        if (!jabatan.isShowing() || !BoxTipe.isShowing()) {
            return;
            
        }
        String keyword = jabatan.getText().toLowerCase();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        
        boolean hasResult = false;
        for (String Tipe : karyawantype){
            if (Tipe.toLowerCase().contains(keyword)) {
                model.addElement(Tipe);
                hasResult = true;
            }
        }
        BoxTipe.setModel(model);
        if (hasResult && !keyword.isEmpty()) {
            BoxTipe.showPopup();
        }else{
            BoxTipe.hidePopup();
        }
    }
    public void fadeIn() {
    setOpacity(0f); // Mulai dari transparan
    new Thread(() -> {
        try {
            for (float i = 0f; i <= 1f; i += 0.05f) {
                Thread.sleep(10);
                setOpacity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}
    public int generateRandomID() {
    Random rand = new Random();
    return rand.nextInt(90000) + 10000; // hasil: antara 10000 - 99999
}
    public int generateUniqueID(Connection conn) throws SQLException {
    int id;
    do {
        id = generateRandomID();
    } while (isIDExist(conn, id)); // Ulangi kalau sudah ada di DB
    return id;
}

public boolean isIDExist(Connection conn, int id) throws SQLException {
    String sql = "SELECT COUNT(*) FROM karyawan WHERE id_karyawan = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setInt(1, id);
    ResultSet rs = stmt.executeQuery();
    rs.next();
    return rs.getInt(1) > 0;
}
    private void Tambahkan(){
        int ID = generateRandomID();
        String NIK = NIKKaryawan.getText();
        String RFID = RFIDKaryawan.getText();
        String Nama = Nama_Karyawan.getText();
        String Telepon = Telepon_Karyawan.getText();
        String Alamat = Alamat_Karyawan.getText();
        String Username = UNKaryawan.getText();
        String Password = PWKaryawan.getText();
        String Jabatan = jabatan.getText();
        String Gaji = gaji.getText();
        
        if(RFID.isEmpty() || Nama.isEmpty() || Telepon.isEmpty() || Alamat.isEmpty() || Username.isEmpty() || Password.isEmpty() || Jabatan.isEmpty() || Gaji.isEmpty()){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            LengkapiData lengkap = new LengkapiData(parent, true);
            lengkap.setVisible(true);
            return;
        }
        if(!Nama.matches("[a-zA-Z\\s]+")){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            SesuaiFormat frmt = new SesuaiFormat(parent, true);
            frmt.setVisible(true);
            return;
        }
        if(!Telepon.matches("\\d+")){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            SesuaiFormat frmt = new SesuaiFormat(parent, true);
            frmt.setVisible(true);
            return;
        }
        if(!(Jabatan.equalsIgnoreCase("admin") || Jabatan.equalsIgnoreCase("karyawan"))){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            JabatanOnly jabat = new JabatanOnly(parent, true);
            jabat.setVisible(true);
            return;
        }
        try {
            Connection conn;
            PreparedStatement pstmt;
            String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
            
            String checkSql = "SELECT COUNT(*) FROM karyawan WHERE id_karyawan = ? OR username = ? OR nik = ?";
            PreparedStatement check = conn.prepareStatement(checkSql);
            check.setInt(1, ID);
            check.setString(2, Username);
            check.setString(3, NIK);
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1)>0) {
                java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
                DataAda ada = new DataAda(parent, true);
            ada.setVisible(true);
            return;
            }
            
            String sql = "INSERT INTO karyawan (id_karyawan, uidrfid, nama_karyawan, no_hp, alamat, username, password, nik, status, gaji) VALUES (?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, ID);
            pstmt.setString(2, RFID);
            pstmt.setString(3, Nama);
            pstmt.setString(4, Telepon);
            pstmt.setString(5, Alamat);
            pstmt.setString(6, Username);
            pstmt.setString(7, Password);
            pstmt.setString(8, NIK);
            pstmt.setString(9, Jabatan);
            pstmt.setString(10, Gaji);
            
            int success = pstmt.executeUpdate();
            if(success>0){
                System.out.println("Data ditambahkan");
                clearFields();
                dispose();
                java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
                Loading muat = new Loading(parent, true);
            muat.setVisible(true);
            }else{
                JOptionPane.showMessageDialog(null, "Datanya gabisa ditambahin ini T_T");
            }
            
        } catch (Exception e) {
            
                e.printStackTrace();
        }
    }
    private void clearFields(){
        NIKKaryawan.setText("");
        RFIDKaryawan.setText("");
        Nama_Karyawan.setText("");
        Telepon_Karyawan.setText("");
        Alamat_Karyawan.setText("");
        UNKaryawan.setText("");
        PWKaryawan.setText("");
        jabatan.setText("");
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        NIKKaryawan = new jtextfield.TextFieldSuggestion();
        jLabel1 = new javax.swing.JLabel();
        Nama_Karyawan = new jtextfield.TextFieldSuggestion();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Telepon_Karyawan = new jtextfield.TextFieldSuggestion();
        jLabel4 = new javax.swing.JLabel();
        Alamat_Karyawan = new jtextfield.TextFieldSuggestion();
        tomboltambah = new com.raven.swing.CustomButton_Rounded();
        tombolbatal = new com.raven.swing.CustomButton_Rounded();
        jLabel5 = new javax.swing.JLabel();
        RFIDKaryawan = new jtextfield.TextFieldSuggestion();
        jLabel6 = new javax.swing.JLabel();
        UNKaryawan = new jtextfield.TextFieldSuggestion();
        jLabel7 = new javax.swing.JLabel();
        PWKaryawan = new jtextfield.TextFieldSuggestion();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        Close = new Custom.Custom_ButtonRounded();
        jLabel9 = new javax.swing.JLabel();
        jabatan = new jtextfield.TextFieldSuggestion();
        jLabel10 = new javax.swing.JLabel();
        gaji = new jtextfield.TextFieldSuggestion();
        BoxTipe = new jtextfield.ComboBoxSuggestion();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(250, 250, 250));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        NIKKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NIKKaryawanActionPerformed(evt);
            }
        });
        jPanel1.add(NIKKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 74, 235, -1));

        jLabel1.setText("NIK");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 46, -1, -1));

        Nama_Karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nama_KaryawanActionPerformed(evt);
            }
        });
        jPanel1.add(Nama_Karyawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 150, 235, -1));

        jLabel2.setText("Nama");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 122, -1, -1));

        jLabel3.setText("Nomor Telepon");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 198, -1, -1));

        Telepon_Karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Telepon_KaryawanActionPerformed(evt);
            }
        });
        jPanel1.add(Telepon_Karyawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 226, 235, 38));

        jLabel4.setText("Alamat");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 284, -1, -1));

        Alamat_Karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Alamat_KaryawanActionPerformed(evt);
            }
        });
        jPanel1.add(Alamat_Karyawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 312, 235, -1));

        tomboltambah.setForeground(new java.awt.Color(0, 0, 0));
        tomboltambah.setText("Tambahkan");
        tomboltambah.setFillClick(new java.awt.Color(102, 102, 102));
        tomboltambah.setFillOriginal(new java.awt.Color(224, 221, 221));
        tomboltambah.setFillOver(new java.awt.Color(242, 238, 238));
        tomboltambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tomboltambahActionPerformed(evt);
            }
        });
        jPanel1.add(tomboltambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 450, 100, 40));

        tombolbatal.setText("Batalkan");
        tombolbatal.setFillClick(new java.awt.Color(153, 0, 0));
        tombolbatal.setFillOriginal(new java.awt.Color(255, 0, 0));
        tombolbatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tombolbatalActionPerformed(evt);
            }
        });
        jPanel1.add(tombolbatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(262, 450, 100, 40));

        jLabel5.setText("ID");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 46, -1, -1));

        RFIDKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RFIDKaryawanActionPerformed(evt);
            }
        });
        jPanel1.add(RFIDKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 74, 235, -1));

        jLabel6.setText("Username");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 122, -1, -1));

        UNKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UNKaryawanActionPerformed(evt);
            }
        });
        jPanel1.add(UNKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 150, 235, -1));

        jLabel7.setText("Password");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 198, -1, -1));

        PWKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PWKaryawanActionPerformed(evt);
            }
        });
        jPanel1.add(PWKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 226, 235, 38));

        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), null));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel8.setText("Tambahkan Karyawan");

        Close.setBackground(new java.awt.Color(242, 242, 242));
        Close.setForeground(new java.awt.Color(255, 0, 0));
        Close.setText("X");
        Close.setFillClick(new java.awt.Color(242, 242, 242));
        Close.setFillOriginal(new java.awt.Color(242, 242, 242));
        Close.setFillOver(new java.awt.Color(242, 242, 242));
        Close.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        Close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 338, Short.MAX_VALUE)
                .addComponent(Close, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(Close, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, -1, -1));

        jLabel9.setText("Jabatan");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 282, -1, -1));

        jabatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jabatanActionPerformed(evt);
            }
        });
        jPanel1.add(jabatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 310, 235, 38));

        jLabel10.setText("Gaji");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 360, -1, -1));

        gaji.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gajiActionPerformed(evt);
            }
        });
        jPanel1.add(gaji, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 388, 235, 38));

        BoxTipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BoxTipeActionPerformed(evt);
            }
        });
        jPanel1.add(BoxTipe, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 350, 240, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void NIKKaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NIKKaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NIKKaryawanActionPerformed

    private void Nama_KaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nama_KaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Nama_KaryawanActionPerformed

    private void Telepon_KaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Telepon_KaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Telepon_KaryawanActionPerformed

    private void Alamat_KaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Alamat_KaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Alamat_KaryawanActionPerformed

    private void tombolbatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tombolbatalActionPerformed
        dispose();
    }//GEN-LAST:event_tombolbatalActionPerformed

    private void tomboltambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tomboltambahActionPerformed
        Tambahkan();
    }//GEN-LAST:event_tomboltambahActionPerformed

    private void RFIDkaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RFIDkaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RFIDkaryawanActionPerformed

    private void UNKaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UNKaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_UNKaryawanActionPerformed

    private void PWKaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PWKaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PWKaryawanActionPerformed

    private void CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseActionPerformed
        dispose();
    }//GEN-LAST:event_CloseActionPerformed

    private void jabatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jabatanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jabatanActionPerformed

    private void gajiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gajiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gajiActionPerformed

    private void RFIDKaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RFIDKaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RFIDKaryawanActionPerformed

    private void BoxTipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BoxTipeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BoxTipeActionPerformed

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
            java.util.logging.Logger.getLogger(Form_tbhKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Form_tbhKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Form_tbhKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Form_tbhKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Form_tbhKaryawan dialog = new Form_tbhKaryawan(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jtextfield.TextFieldSuggestion Alamat_Karyawan;
    private jtextfield.ComboBoxSuggestion BoxTipe;
    private Custom.Custom_ButtonRounded Close;
    private jtextfield.TextFieldSuggestion NIKKaryawan;
    private jtextfield.TextFieldSuggestion Nama_Karyawan;
    private jtextfield.TextFieldSuggestion PWKaryawan;
    private jtextfield.TextFieldSuggestion RFIDKaryawan;
    private jtextfield.TextFieldSuggestion Telepon_Karyawan;
    private jtextfield.TextFieldSuggestion UNKaryawan;
    private jtextfield.TextFieldSuggestion gaji;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private jtextfield.TextFieldSuggestion jabatan;
    private com.raven.swing.CustomButton_Rounded tombolbatal;
    private com.raven.swing.CustomButton_Rounded tomboltambah;
    // End of variables declaration//GEN-END:variables
}
