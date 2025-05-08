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
import javax.swing.SwingUtilities;
import raven.dialog.DataAda;
import raven.dialog.LengkapiData;
import raven.dialog.Loading;
import raven.dialog.SesuaiFormat;

/**
 *
 * @author Fitrah
 */
public class Form_editKaryawan extends javax.swing.JDialog {
    private Runnable ondataEdited;
    private String ID;
    PreparedStatement pstmt;
           ResultSet rs;
           Connection conn;
    
    public boolean isConfirmed(){
        return confirmed;
    }
    public void setID(String ID){
        this.ID=ID;
    }
    public void ondataedit(){
        this.ondataEdited=ondataEdited;
    }

        private boolean confirmed = false;
    public Form_editKaryawan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(parent);
        fadeIn();
        
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
                IDKaryawan.requestFocus();
            }
            }
        });
        IDKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
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
    private String idLama;
    public void ambilData(String NIK, String ID, String Nama, String Telepon, String Alamat, String Username, String Password, String Gaji){
        NIKKaryawan.setText(NIK);
        IDKaryawan.setText(ID);
        Nama_Karyawan.setText(Nama);
        Telepon_Karyawan.setText(Telepon);
        Alamat_Karyawan.setText(Alamat);
        UNKaryawan.setText(Username);
        PWKaryawan.setText(Password);
        gaji.setText(Gaji);
        
        idLama = ID;
        
    }
    public String[]getData(){
        String NIK = NIKKaryawan.getText();
        String ID = IDKaryawan.getText();
        String Nama = Nama_Karyawan.getText();
        String Telepon = Telepon_Karyawan.getText();
        String Alamat = Alamat_Karyawan.getText();
        String Username = UNKaryawan.getText();
        String Password = PWKaryawan.getText();
        String Gaji = gaji.getText();
        
        
        if(NIK.isEmpty() || ID.isEmpty() || Nama.isEmpty() || Telepon.isEmpty() || Alamat.isEmpty() || Username.isEmpty() || Password.isEmpty() || Gaji.isEmpty()){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            LengkapiData lengkap = new LengkapiData(parent, true);
            lengkap.setVisible(true);
            return null;
        }
        if(!NIK.matches("\\d+")){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            SesuaiFormat frmt = new SesuaiFormat(parent, true);
            frmt.setVisible(true);
            return null;
        }
        if(!Nama.matches("[a-zA-Z\\s]+")){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            SesuaiFormat frmt = new SesuaiFormat(parent, true);
            frmt.setVisible(true);
            return null;
        }
        if(!Telepon.matches("\\d+")){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            SesuaiFormat frmt = new SesuaiFormat(parent, true);
            frmt.setVisible(true);
            return null;
        }
        return new String[]{NIK, ID, Nama, Telepon, Alamat, Username, Password, Gaji};
    }
    public void Refresh(){
        try {
           String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
           String sql = "SELECT * FROM karyawan WHERE id_karyawan = ?";
           pstmt=conn.prepareStatement(sql);
           pstmt.setString(1, ID);
           rs=pstmt.executeQuery();
           if(rs.next()){
               String NIK = rs.getString("nik");
               String ID = rs.getString("id_karyawan");
               String Nama = rs.getString("nama");
               String Telepon = rs.getString("no_hp");
               String Alamat = rs.getString("alamat");
               String Username = rs.getString("username");
               String Password = rs.getString("password");
               String Gaji = rs.getString("gaji");
               
               NIKKaryawan.setText(NIK);
               IDKaryawan.setText(ID);
               Nama_Karyawan.setText(Nama);
               Telepon_Karyawan.setText(Telepon);
               Alamat_Karyawan.setText(Alamat);
               UNKaryawan.setText(Username);
               PWKaryawan.setText(Password);
               gaji.setText(Gaji);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
    private boolean isDuplicate(String NIK, String NewID, String IDLama){
        String sql = "SELECT COUNT(*) FROM karyawan WHERE (nik = ? OR id_karyawan = ?) AND id_karyawan != ?";
        try {
           String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
           pstmt=conn.prepareStatement(sql);
           pstmt.setString(1, NIK);
           pstmt.setString(2, NewID);
           pstmt.setString(3, IDLama);
           rs=pstmt.executeQuery();
           if(rs.next()){
               int count = rs.getInt(1);
               return count > 0;
           }
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        tomboledit = new com.raven.swing.CustomButton_Rounded();
        tombolbatal = new com.raven.swing.CustomButton_Rounded();
        jLabel5 = new javax.swing.JLabel();
        IDKaryawan = new jtextfield.TextFieldSuggestion();
        jLabel6 = new javax.swing.JLabel();
        UNKaryawan = new jtextfield.TextFieldSuggestion();
        jLabel7 = new javax.swing.JLabel();
        PWKaryawan = new jtextfield.TextFieldSuggestion();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        Close = new Custom.Custom_ButtonRounded();
        jLabel9 = new javax.swing.JLabel();
        gaji = new jtextfield.TextFieldSuggestion();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(250, 250, 250));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        NIKKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NIKKaryawanActionPerformed(evt);
            }
        });

        jLabel1.setText("NIK");

        Nama_Karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nama_KaryawanActionPerformed(evt);
            }
        });

        jLabel2.setText("Nama");

        jLabel3.setText("Nomor Telepon");

        Telepon_Karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Telepon_KaryawanActionPerformed(evt);
            }
        });

        jLabel4.setText("Alamat");

        Alamat_Karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Alamat_KaryawanActionPerformed(evt);
            }
        });

        tomboledit.setForeground(new java.awt.Color(0, 0, 0));
        tomboledit.setText("Edit");
        tomboledit.setFillClick(new java.awt.Color(102, 102, 102));
        tomboledit.setFillOriginal(new java.awt.Color(224, 221, 221));
        tomboledit.setFillOver(new java.awt.Color(242, 238, 238));
        tomboledit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tomboleditActionPerformed(evt);
            }
        });

        tombolbatal.setText("Batalkan");
        tombolbatal.setFillClick(new java.awt.Color(153, 0, 0));
        tombolbatal.setFillOriginal(new java.awt.Color(255, 0, 0));
        tombolbatal.setFillOver(new java.awt.Color(255, 102, 102));
        tombolbatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tombolbatalActionPerformed(evt);
            }
        });

        jLabel5.setText("ID");

        IDKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IDKaryawanActionPerformed(evt);
            }
        });

        jLabel6.setText("Username");

        UNKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UNKaryawanActionPerformed(evt);
            }
        });

        jLabel7.setText("Password");

        PWKaryawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PWKaryawanActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), null));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel8.setText("Edit Karyawan");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        jLabel9.setText("Gaji");

        gaji.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gajiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(tomboledit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(Nama_Karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                            .addComponent(Alamat_Karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(NIKKaryawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Telepon_Karyawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6)
                    .addComponent(UNKaryawan, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(IDKaryawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PWKaryawan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tombolbatal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(gaji, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(20, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(NIKKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Nama_Karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Telepon_Karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(IDKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(UNKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(PWKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Alamat_Karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(gaji, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tombolbatal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tomboledit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

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

    private void tomboleditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tomboleditActionPerformed
        String[]data=getData();
        if(data==null){
            return;
        }
        String NIK = data[0];
        String Nama = data[2];
        String Telepon = data[3];
        String Alamat = data[4];  
        String Username = data[5];  
        String Password = data[6];  
        String Gaji = data[7];
        String IDLama = idLama;
        String NewID = IDKaryawan.getText();
        if(conn==null){
            String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            try{
            conn = DriverManager.getConnection(url, dbUser, dbPass);
            }catch(SQLException e){
                e.printStackTrace();
                return;
            }
        }
        if (isDuplicate(NIK, NewID, IDLama)) {
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
                DataAda ada = new DataAda(parent, true);
            ada.setVisible(true);
            return;
        }
        String sql = "UPDATE karyawan SET id_karyawan = ?, nik = ?, nama = ?, no_hp = ?, alamat = ?, username = ?, password = ?, gaji = ? WHERE id_karyawan = ?";
       
        try {
            conn.setAutoCommit(false);
            int rowUpdate = 0;
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                
                ps.setString(1, NewID);
                ps.setString(2, NIK);
                ps.setString(3, Nama);
                ps.setString(4, Telepon);
                ps.setString(5, Alamat);
                ps.setString(6, Username);
                ps.setString(7, Password);
                ps.setString(8, Gaji);
                ps.setString(9, IDLama);
                
                rowUpdate = ps.executeUpdate();
            }
            if(rowUpdate>0){
                conn.commit();
                System.out.println("Berhasil diperbarui : "+rowUpdate);
                Refresh();
            }else{
                conn.rollback();
                System.out.println("Gagal updata : "+rowUpdate);
            }
            if(ondataEdited!=null){
                ondataEdited.run();
            }
           dispose();
           java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
                Loading muat = new Loading(parent, true);
            muat.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_tomboleditActionPerformed

    private void IDKaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IDKaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IDKaryawanActionPerformed

    private void UNKaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UNKaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_UNKaryawanActionPerformed

    private void PWKaryawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PWKaryawanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PWKaryawanActionPerformed

    private void CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseActionPerformed
        dispose();
    }//GEN-LAST:event_CloseActionPerformed

    private void gajiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gajiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gajiActionPerformed

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
            java.util.logging.Logger.getLogger(Form_editKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Form_editKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Form_editKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Form_editKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                Form_editKaryawan dialog = new Form_editKaryawan(new javax.swing.JFrame(), true);
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
    private Custom.Custom_ButtonRounded Close;
    private jtextfield.TextFieldSuggestion IDKaryawan;
    private jtextfield.TextFieldSuggestion NIKKaryawan;
    private jtextfield.TextFieldSuggestion Nama_Karyawan;
    private jtextfield.TextFieldSuggestion PWKaryawan;
    private jtextfield.TextFieldSuggestion Telepon_Karyawan;
    private jtextfield.TextFieldSuggestion UNKaryawan;
    private jtextfield.TextFieldSuggestion gaji;
    private javax.swing.JLabel jLabel1;
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
    private com.raven.swing.CustomButton_Rounded tombolbatal;
    private com.raven.swing.CustomButton_Rounded tomboledit;
    // End of variables declaration//GEN-END:variables
}
