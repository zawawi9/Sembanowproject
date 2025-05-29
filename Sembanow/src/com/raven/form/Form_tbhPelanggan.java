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
import java.util.UUID;
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
import raven.dialog.TipeOnly;

/**
 *
 * @author Fitrah
 */
public class Form_tbhPelanggan extends javax.swing.JDialog {

    private List<String> pelanggantype = Arrays.asList("h1", "h2", "h3");
    private DocumentListener myListener;
    public Form_tbhPelanggan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(parent);
        fadeIn();
        TipePelanggan.getDocument().addDocumentListener(new DocumentListener() {
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
        RFIDpelanggan1.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != '\b') {
            evt.consume(); // Mengabaikan input jika bukan angka atau backspace
        }
    }
});
        Telepon_Pelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) && c != '\b') {
            evt.consume(); // Mengabaikan input jika bukan angka atau backspace
        }
    }
});
        Nama_Pelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!Character.isLetter(c) && !Character.isWhitespace(c) && !Character.isISOControl(c)) {
            evt.consume(); // Mengabaikan input selain huruf, spasi, dan karakter kontrol seperti backspace
        }
    }
});

        
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
    public int generateRandomID() {
    Random rand = new Random();
    return rand.nextInt(90000) + 10000; // 10000 - 99999
}

public boolean isIDExist(Connection conn, int id) throws SQLException {
    String sql = "SELECT COUNT(*) FROM pelanggan WHERE id_pelanggan = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setInt(1, id);
    ResultSet rs = stmt.executeQuery();
    rs.next();
    return rs.getInt(1) > 0;
}

public int generateUniqueID(Connection conn) throws SQLException {
    int id;
    do {
        id = generateRandomID();
    } while (isIDExist(conn, id));
    return id;
}

    private void Tambahkan(){
        
        int ID = generateRandomID();
        String RFID = RFIDpelanggan1.getText();
        String Nama = Nama_Pelanggan.getText();
        String Telepon = Telepon_Pelanggan.getText();
        String Alamat = Alamat_Pelanggan.getText();
        String Tipe = TipePelanggan.getText();
        
        if(RFID.isEmpty() || Nama.isEmpty() || Telepon.isEmpty() || Alamat.isEmpty() || Tipe.isEmpty()){
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
        if(!(Tipe.equalsIgnoreCase("h1") || Tipe.equalsIgnoreCase("h2") || Tipe.equalsIgnoreCase("h3"))){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
            TipeOnly tipe = new TipeOnly(parent, true);
            tipe.setVisible(true);
            return;
        }
        try {
            Connection conn;
            PreparedStatement pstmt;
            String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
            
            String checkSql = "SELECT COUNT(*) FROM pelanggan WHERE id_pelanggan = ? OR rfidpelanggan = ?";
            PreparedStatement check = conn.prepareStatement(checkSql);
            check.setInt(1, ID);
            check.setString(2, RFID);
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1)>0) {
                java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(this);
                DataAda ada = new DataAda(parent, true);
            ada.setVisible(true);
            return;
            }
            
            String sql = "INSERT INTO pelanggan (id_pelanggan, rfidpelanggan, nama, alamat, tipe_harga, no_hp) VALUES (?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, ID);
            pstmt.setString(2, RFID);
            pstmt.setString(3, Nama);
            pstmt.setString(4, Alamat);
            pstmt.setString(5, Tipe);
            pstmt.setString(6, Telepon);
            
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
        
        RFIDpelanggan1.setText("");
        Nama_Pelanggan.setText("");
        Telepon_Pelanggan.setText("");
        Alamat_Pelanggan.setText("");
        TipePelanggan.setText("");
        
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox_Custom1 = new com.raven.swing.jComboBox_Custom();
        jPanel1 = new javax.swing.JPanel();
        Nama_Pelanggan = new jtextfield.TextFieldSuggestion();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Alamat_Pelanggan = new jtextfield.TextFieldSuggestion();
        tomboltambah = new com.raven.swing.CustomButton_Rounded();
        tombolbatal = new com.raven.swing.CustomButton_Rounded();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        Close = new Custom.Custom_ButtonRounded();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Telepon_Pelanggan = new jtextfield.TextFieldSuggestion();
        TipePelanggan = new jtextfield.TextFieldSuggestion();
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
        jPanel1.add(Nama_Pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 270, -1));

        jLabel2.setText("Nama");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        jLabel4.setText("Alamat");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, -1, -1));

        Alamat_Pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Alamat_PelangganActionPerformed(evt);
            }
        });
        jPanel1.add(Alamat_Pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, 270, -1));

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
        jPanel1.add(tomboltambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 300, 101, 40));

        tombolbatal.setText("Batalkan");
        tombolbatal.setFillClick(new java.awt.Color(153, 0, 0));
        tombolbatal.setFillOriginal(new java.awt.Color(255, 0, 0));
        tombolbatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tombolbatalActionPerformed(evt);
            }
        });
        jPanel1.add(tombolbatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 300, 100, 40));

        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), null));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel5.setText("Tambah Pelanggan");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 468, Short.MAX_VALUE)
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

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 630, -1));

        jLabel6.setText("Tipe Pelanggan");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 130, -1, -1));

        jLabel3.setText("Telepon");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, -1));

        Telepon_Pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Telepon_PelangganActionPerformed(evt);
            }
        });
        jPanel1.add(Telepon_Pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 270, -1));

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
        jPanel1.add(TipePelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 160, 270, 40));

        BoxTipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BoxTipeActionPerformed(evt);
            }
        });
        jPanel1.add(BoxTipe, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 200, 270, 1));

        RFIDpelanggan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RFIDpelanggan1ActionPerformed(evt);
            }
        });
        jPanel1.add(RFIDpelanggan1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 270, -1));

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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void tomboltambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tomboltambahActionPerformed
        Tambahkan();
    }//GEN-LAST:event_tomboltambahActionPerformed

    private void CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseActionPerformed
        dispose();
    }//GEN-LAST:event_CloseActionPerformed

    private void Telepon_PelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Telepon_PelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Telepon_PelangganActionPerformed

    private void TipePelangganKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TipePelangganKeyReleased
        listen();
    }//GEN-LAST:event_TipePelangganKeyReleased

    private void TipePelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TipePelangganActionPerformed
   
    }//GEN-LAST:event_TipePelangganActionPerformed

    private void BoxTipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BoxTipeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BoxTipeActionPerformed

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
            java.util.logging.Logger.getLogger(Form_tbhPelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Form_tbhPelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Form_tbhPelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Form_tbhPelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                Form_tbhPelanggan dialog = new Form_tbhPelanggan(new javax.swing.JFrame(), true);
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
    private com.raven.swing.jComboBox_Custom jComboBox_Custom1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private com.raven.swing.CustomButton_Rounded tombolbatal;
    private com.raven.swing.CustomButton_Rounded tomboltambah;
    // End of variables declaration//GEN-END:variables
}
