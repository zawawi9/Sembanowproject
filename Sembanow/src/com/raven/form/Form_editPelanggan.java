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
public class Form_editPelanggan extends javax.swing.JDialog {
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
        
        IDPelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                Nama_Pelanggan.requestFocus();
            }
            }
        });
        Nama_Pelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
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
    public void ambilData(String ID, String Nama, String Alamat, String Tipe){
        IDPelanggan.setText(ID);
        Nama_Pelanggan.setText(Nama);
        Alamat_Pelanggan.setText(Alamat);
        TipePelanggan.setText(Tipe);
        
        idLama = ID;
        
    }
    public String[]getData(){
        String ID = IDPelanggan.getText();
        String Nama = Nama_Pelanggan.getText();
        String Alamat = Alamat_Pelanggan.getText();
        String Tipe = TipePelanggan.getText();
        
        if(ID.isEmpty() || Nama.isEmpty() || Alamat.isEmpty() || Tipe.isEmpty()){
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
        return new String[]{ID, Nama, Alamat, Tipe};
    }
    public void Refresh(){
        try {
           String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
           String sql = "SELECT * FROM pelanggan";
           pstmt=conn.prepareStatement(sql);
           pstmt.setString(0, ID);
           rs=pstmt.executeQuery();
           if(rs.next()){
               String ID = rs.getString("id_pelanggan");
               String Nama = rs.getString("nama");
               String Alamat = rs.getString("alamat");
               String Tipe = rs.getString("tipe_harga");
               
               IDPelanggan.setText(ID);
               Nama_Pelanggan.setText(Nama);
               Alamat_Pelanggan.setText(Alamat);
               TipePelanggan.setText(Tipe);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
    private boolean isDuplicate(String NewID, String Tipe, String IDLama){
        String sql = "SELECT COUNT(*) FROM pelanggan WHERE (id_pelanggan = ? OR tipe_harga = ?) AND id_pelanggan != ?";
        try {
           String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
           pstmt=conn.prepareStatement(sql);
           pstmt.setString(1, NewID);
           pstmt.setString(2, Tipe);
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
        IDPelanggan = new jtextfield.TextFieldSuggestion();
        jLabel1 = new javax.swing.JLabel();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(250, 250, 250));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        IDPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IDPelangganActionPerformed(evt);
            }
        });

        jLabel1.setText("ID");

        Nama_Pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nama_PelangganActionPerformed(evt);
            }
        });

        jLabel2.setText("Nama");

        jLabel4.setText("Alamat");

        Alamat_Pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Alamat_PelangganActionPerformed(evt);
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
        tombolbatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tombolbatalActionPerformed(evt);
            }
        });

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 143, Short.MAX_VALUE)
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

        jLabel6.setText("Tipe Pelanggan");

        TipePelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TipePelangganActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(Alamat_Pelanggan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                                .addComponent(Nama_Pelanggan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(IDPelanggan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(TipePelanggan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(tomboledit, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tombolbatal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(IDPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Nama_Pelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Alamat_Pelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TipePelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tombolbatal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tomboledit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void IDPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IDPelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IDPelangganActionPerformed

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
        String Nama = data[1];
        String Alamat = data[2];
        String Tipe = data[3];
        String IDLama = idLama;
        String NewID = IDPelanggan.getText();
        
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
        if (isDuplicate(NewID, Tipe, IDLama)) {
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
                DataAda ada = new DataAda(parent, true);
            ada.setVisible(true);
            return;
        }
        String sql = "UPDATE pelanggan SET id_pelanggan = ?, nama = ?, alamat = ?, tipe_harga = ? WHERE id_pelanggan = ?";
       
        try {
            conn.setAutoCommit(false);
            int rowUpdate = 0;
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, NewID);
                ps.setString(2, Nama);
                ps.setString(3, Alamat);
                ps.setString(4, Tipe);
                ps.setString(5, IDLama);
                
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

    private void CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseActionPerformed
        dispose();
    }//GEN-LAST:event_CloseActionPerformed

    private void TipePelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TipePelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TipePelangganActionPerformed

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
    private Custom.Custom_ButtonRounded Close;
    private jtextfield.TextFieldSuggestion IDPelanggan;
    private jtextfield.TextFieldSuggestion Nama_Pelanggan;
    private jtextfield.TextFieldSuggestion TipePelanggan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private com.raven.swing.CustomButton_Rounded tombolbatal;
    private com.raven.swing.CustomButton_Rounded tomboledit;
    // End of variables declaration//GEN-END:variables
}
