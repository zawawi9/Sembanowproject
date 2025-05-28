/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.raven.form;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import raven.dialog.DataAda;
import raven.dialog.LengkapiData;
import raven.dialog.Loading;
import raven.dialog.SesuaiFormat;

/**
 *
 * @author Fitrah
 */

public class Form_editPelanggan extends javax.swing.JDialog {
private List<String> pelanggantype = Arrays.asList("h1", "h2", "h3");
    private DocumentListener myListener;
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
    public Form_editPelanggan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(parent);
        fadeIn();
        RFIDpelanggan1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                Nama_Pelanggan.requestFocus();
            }
            }
        });
        Nama_Pelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                Telepon_Pelanggan.requestFocus();
            }
            }
        });
        Telepon_Pelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                Alamat_Pelanggan.requestFocus();
            }
            }
        });
        Alamat_Pelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                TipePelanggan.requestFocus();
            }
            }
        });
        BoxTipe.addActionListener((evt) -> {
            String selectedType = (String)BoxTipe.getSelectedItem();
            if (selectedType!=null && !selectedType.isEmpty()) {
                TipePelanggan.setText(selectedType);
            }
        });
        
    }
     public void listen(){
        if (!TipePelanggan.isShowing() || !BoxTipe.isShowing()) {
            return;
            
        }
        String keyword = TipePelanggan.getText().toLowerCase();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        
        boolean hasResult = false;
        for (String Tipe : pelanggantype){
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
    public void ambilData(String ID, String RFID, String Nama, String Telepon, String Alamat, String Tipe){
        RFIDpelanggan1.setText(RFID);
        Nama_Pelanggan.setText(Nama);
        Telepon_Pelanggan.setText(Telepon);
        Alamat_Pelanggan.setText(Alamat);
        TipePelanggan.setText(Tipe);
        
    }
    public String[]getData(){
        String RFID = RFIDpelanggan1.getText();
        String Nama = Nama_Pelanggan.getText();
        String Telepon = Telepon_Pelanggan.getText();
        String Alamat = Alamat_Pelanggan.getText();
        String Tipe = TipePelanggan.getText();
        
        if(RFID.isEmpty() || Nama.isEmpty() || Telepon.isEmpty() || Alamat.isEmpty() || Tipe.isEmpty()){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            LengkapiData lengkap = new LengkapiData(parent, true);
            lengkap.setVisible(true);
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
        return new String[]{ID, RFID, Nama, Telepon, Alamat, Tipe};
    }
    public void Refresh(){
        try {
           String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
           String sql = "SELECT * FROM pelanggan WHERE id_pelanggan = ?";
           pstmt=conn.prepareStatement(sql);
           pstmt.setString(1, ID);
           rs=pstmt.executeQuery();
           if(rs.next()){
               String RFID = rs.getString("rfidpelanggan");
               String Nama = rs.getString("nama");
               String Telepon = rs.getString("no_hp");
               String Alamat = rs.getString("alamat");
               String Tipe = rs.getString("tipe_harga");
               
               RFIDpelanggan1.setText(RFID);
               Nama_Pelanggan.setText(Nama);
               Telepon_Pelanggan.setText(Telepon);
               Alamat_Pelanggan.setText(Alamat);
               TipePelanggan.setText(Tipe);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
    private boolean isDuplicate(String RFID, String ID){
        String sql = "SELECT COUNT(*) FROM pelanggan WHERE rfidpelanggan = ? AND id_pelanggan != ?";
        try {
           String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
           pstmt=conn.prepareStatement(sql);
           pstmt.setString(1, RFID);
           pstmt.setString(2, ID);
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
        Nama_Pelanggan = new jtextfield.TextFieldSuggestion();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Alamat_Pelanggan = new jtextfield.TextFieldSuggestion();
        tomboledit = new com.raven.swing.CustomButton_Rounded();
        tombolbatal = new com.raven.swing.CustomButton_Rounded();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        Close = new Custom.Custom_ButtonRounded();
        jLabel6 = new javax.swing.JLabel();
        TipePelanggan = new jtextfield.TextFieldSuggestion();
        jLabel3 = new javax.swing.JLabel();
        Telepon_Pelanggan = new jtextfield.TextFieldSuggestion();
        BoxTipe = new jtextfield.ComboBoxSuggestion();
        RFIDpelanggan1 = new jtextfield.TextFieldSuggestion();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(250, 250, 250));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Nama_Pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nama_PelangganActionPerformed(evt);
            }
        });
        jPanel1.add(Nama_Pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 260, -1));

        jLabel2.setText("Nama");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        jLabel4.setText("Alamat");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 50, -1, -1));

        Alamat_Pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Alamat_PelangganActionPerformed(evt);
            }
        });
        jPanel1.add(Alamat_Pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 80, 260, -1));

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
        jPanel1.add(tomboledit, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 300, 101, 40));

        tombolbatal.setText("Batalkan");
        tombolbatal.setFillClick(new java.awt.Color(153, 0, 0));
        tombolbatal.setFillOriginal(new java.awt.Color(255, 0, 0));
        tombolbatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tombolbatalActionPerformed(evt);
            }
        });
        jPanel1.add(tombolbatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 300, 100, 40));

        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), null));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel5.setText("Edit Pelanggan");

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
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 498, Short.MAX_VALUE)
                .addComponent(Close, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(Close, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 660, -1));

        jLabel6.setText("Tipe Pelanggan");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 130, -1, -1));

        TipePelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TipePelangganActionPerformed(evt);
            }
        });
        TipePelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                TipePelangganKeyReleased(evt);
            }
        });
        jPanel1.add(TipePelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 160, 260, -1));

        jLabel3.setText("Telepon");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, -1, -1));

        Telepon_Pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Telepon_PelangganActionPerformed(evt);
            }
        });
        jPanel1.add(Telepon_Pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 260, -1));

        BoxTipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BoxTipeActionPerformed(evt);
            }
        });
        jPanel1.add(BoxTipe, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 190, 260, 1));

        RFIDpelanggan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RFIDpelanggan1ActionPerformed(evt);
            }
        });
        jPanel1.add(RFIDpelanggan1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 260, -1));

        jLabel7.setText("RFID");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Nama_PelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nama_PelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Nama_PelangganActionPerformed

    private void Alamat_PelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Alamat_PelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Alamat_PelangganActionPerformed

    private void tombolbatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tombolbatalActionPerformed
        dispose();
    }//GEN-LAST:event_tombolbatalActionPerformed

    private void tomboleditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tomboleditActionPerformed
        String[]data=getData();
        if(data==null){
            return;
        }
        String ID = data[0];
        String RFID = data[1].trim();
        String Nama = data[2];
        String Telepon = data[3];
        String Alamat = data[4];
        String Tipe = data[5];
        
        
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
        if (isDuplicate(RFID, ID)) {
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
                DataAda ada = new DataAda(parent, true);
            ada.setVisible(true);
            return;
        }
        String sql = "UPDATE pelanggan SET rfidpelanggan = ?, nama = ?, no_hp = ?, alamat = ?, tipe_harga = ? WHERE id_pelanggan = ?";
       
        try {
            conn.setAutoCommit(false);
            int rowUpdate = 0;
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, RFID);
                ps.setString(2, Nama);
                ps.setString(3, Telepon);
                ps.setString(4, Alamat);
                ps.setString(5, Tipe);
                ps.setString(6, ID);
                
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_tomboleditActionPerformed

    private void CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseActionPerformed
        dispose();
    }//GEN-LAST:event_CloseActionPerformed

    private void TipePelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TipePelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TipePelangganActionPerformed

    private void Telepon_PelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Telepon_PelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Telepon_PelangganActionPerformed

    private void BoxTipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BoxTipeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BoxTipeActionPerformed

    private void TipePelangganKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TipePelangganKeyReleased
        listen();
    }//GEN-LAST:event_TipePelangganKeyReleased

    private void RFIDpelanggan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RFIDpelanggan1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RFIDpelanggan1ActionPerformed

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
            java.util.logging.Logger.getLogger(Form_editPelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Form_editPelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Form_editPelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Form_editPelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                Form_editPelanggan dialog = new Form_editPelanggan(new javax.swing.JFrame(), true);
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
    private jtextfield.TextFieldSuggestion Alamat_Pelanggan;
    private jtextfield.ComboBoxSuggestion BoxTipe;
    private Custom.Custom_ButtonRounded Close;
    private jtextfield.TextFieldSuggestion Nama_Pelanggan;
    private jtextfield.TextFieldSuggestion RFIDpelanggan1;
    private jtextfield.TextFieldSuggestion Telepon_Pelanggan;
    private jtextfield.TextFieldSuggestion TipePelanggan;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private com.raven.swing.CustomButton_Rounded tombolbatal;
    private com.raven.swing.CustomButton_Rounded tomboledit;
    // End of variables declaration//GEN-END:variables
}
