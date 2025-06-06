/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.raven.form;
import config.koneksi;
import com.raven.form.Form_Supplier;
import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import raven.dialog.DataAda;
import raven.dialog.LengkapiData;
import raven.dialog.Loading;
import raven.dialog.SesuaiFormat;
public class Form_editSupplier extends javax.swing.JDialog {
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
    public Form_editSupplier(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(null);
        fadeIn();
        Telepon_Supplier.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != '\b') {
            evt.consume(); // Mengabaikan input jika bukan angka atau backspace
        }
    }
});
        Nama_Supplier.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!Character.isLetter(c) && c != ' ' && c != '\b') {
            evt.consume(); // Mengabaikan input jika bukan angka atau backspace
        }
    }
});
        Nama_Supplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                Telepon_Supplier.requestFocus();
            }
            }
        });
        Telepon_Supplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                Alamat_Supplier.requestFocus();
            }
            }
        });
        Alamat_Supplier.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt){
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                Alamat_Supplier.requestFocus();
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
    public void ambilData(String ID, String Nama, String Telepon, String Alamat){
        Nama_Supplier.setText(Nama);
        Telepon_Supplier.setText(Telepon);
        Alamat_Supplier.setText(Alamat);
        
    }
    public String[]getData(){
        String Nama = Nama_Supplier.getText();
        String Telepon = Telepon_Supplier.getText();
        String Alamat = Alamat_Supplier.getText();
        
        return new String[]{ID, Nama, Telepon, Alamat};
    }
    public boolean validasi(){
        String[]data = getData();
        String Nama = data[1];
        String Telepon = data[2];
        String Alamat = data[3];
        
        if(Nama.isEmpty() || Telepon.isEmpty() || Alamat.isEmpty()){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            LengkapiData lengkap = new LengkapiData(parent, true);
            lengkap.setVisible(true);
            return false;
        }
        if(!Nama.matches("[a-zA-Z\\s]+")){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            SesuaiFormat frmt = new SesuaiFormat(parent, true);
            frmt.setVisible(true);
            return false;
        }
        if(!Telepon.matches("\\d+")){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            SesuaiFormat frmt = new SesuaiFormat(parent, true);
            frmt.setVisible(true);
            return false;
        }
        return true;
    }
    public void Refresh(){
        try {
           String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
           String sql = "SELECT * FROM supplier WHERE id_supplier = ?";
           pstmt=conn.prepareStatement(sql);
           pstmt.setString(1, ID);
           rs=pstmt.executeQuery();
           if(rs.next()){
               String Nama = rs.getString("nama");
               String Telepon = rs.getString("no_hp");
               String Alamat = rs.getString("alamat");
               
               Nama_Supplier.setText(Nama);
               Telepon_Supplier.setText(Telepon);
               Alamat_Supplier.setText(Alamat);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
    private boolean isDuplicate(String ID, String Nama, String Alamat, String Telepon){
        String sql = "SELECT COUNT(*) FROM supplier WHERE nama = ? AND alamat = ? AND no_hp = ? AND id_supplier != ?";
        try {
           String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
           pstmt=conn.prepareStatement(sql);
           pstmt.setString(1, Nama);
           pstmt.setString(2, Alamat);
           pstmt.setString(3, Telepon);
           pstmt.setString(4, ID);
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
        Nama_Supplier = new jtextfield.TextFieldSuggestion();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Telepon_Supplier = new jtextfield.TextFieldSuggestion();
        jLabel4 = new javax.swing.JLabel();
        Alamat_Supplier = new jtextfield.TextFieldSuggestion();
        tomboledit = new com.raven.swing.CustomButton_Rounded();
        tombolbatal = new com.raven.swing.CustomButton_Rounded();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        Close = new Custom.Custom_ButtonRounded();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(250, 250, 250));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        Nama_Supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nama_SupplierActionPerformed(evt);
            }
        });

        jLabel2.setText("Nama");

        jLabel3.setText("Nomor Telepon");

        Telepon_Supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Telepon_SupplierActionPerformed(evt);
            }
        });

        jLabel4.setText("Alamat");

        Alamat_Supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Alamat_SupplierActionPerformed(evt);
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
        jLabel5.setText("Edit Supplier");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 160, Short.MAX_VALUE)
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(Alamat_Supplier, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Telepon_Supplier, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Nama_Supplier, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(tomboledit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(tombolbatal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Nama_Supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Telepon_Supplier, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Alamat_Supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tombolbatal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tomboledit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Nama_SupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nama_SupplierActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Nama_SupplierActionPerformed

    private void Telepon_SupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Telepon_SupplierActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Telepon_SupplierActionPerformed

    private void Alamat_SupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Alamat_SupplierActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Alamat_SupplierActionPerformed

    private void tombolbatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tombolbatalActionPerformed
        dispose();
    }//GEN-LAST:event_tombolbatalActionPerformed

    private void tomboleditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tomboleditActionPerformed
        String[]data=getData();
        if(!validasi()){
            return;
        }
        String ID = data[0];
        String Nama = data[1];
        String Telepon = data[2];
        String Alamat = data[3];  
        
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
        if (isDuplicate(ID,Nama,Alamat,Telepon)) {
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
                DataAda ada = new DataAda(parent, true);
            ada.setVisible(true);
            return;
        }
        String sql = "UPDATE supplier SET nama = ?, alamat = ?, no_hp = ? WHERE id_supplier = ?";
       
        try {
            conn.setAutoCommit(false);
            int rowUpdate = 0;
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, Nama);
                ps.setString(2, Alamat);
                ps.setString(3, Telepon);
                ps.setString(4, ID);
                
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
            java.util.logging.Logger.getLogger(Form_editSupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Form_editSupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Form_editSupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Form_editSupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                Form_editSupplier dialog = new Form_editSupplier(new javax.swing.JFrame(), true);
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
    private jtextfield.TextFieldSuggestion Alamat_Supplier;
    private Custom.Custom_ButtonRounded Close;
    private jtextfield.TextFieldSuggestion Nama_Supplier;
    private jtextfield.TextFieldSuggestion Telepon_Supplier;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private com.raven.swing.CustomButton_Rounded tombolbatal;
    private com.raven.swing.CustomButton_Rounded tomboledit;
    // End of variables declaration//GEN-END:variables
}
