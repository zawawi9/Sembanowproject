/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.form;

import Sortdata.UrutanDataKaryawan;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import raven.dialog.Delete;
import raven.dialog.Loading;
import raven.dialog.Pilihsalahsatu;

/**
 *
 * @author Fitrah
 */
public class Form_Karyawan extends javax.swing.JPanel {

    /**
     * Creates new form Form_Pelanggan
     */
    public Form_Karyawan() {
        initComponents();
        showData();
        TambahKaryawan();
        EditKaryawan();
        HapusData();
        kolompencarian.setText("Cari");
        kolompencarian.setForeground(Color.gray);
        
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("1"),"refresh");
        getActionMap().put("refresh", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                Window window = SwingUtilities.getWindowAncestor(Form_Karyawan.this);
                Loading muat = new Loading((java.awt.Frame) window, true);
        muat.setVisible(true);
        showData();
            }
        });
    }
    
    public void showData(){
        DefaultTableModel model = (DefaultTableModel) table11.getModel();
        model.setRowCount(0);
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
        try {
            String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
            
            String SQL = "SELECT * FROM karyawan";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(SQL);
            while(rs.next()){
                Object[]row = {rs.getString("id_karyawan"),
                        rs.getString("nama"),
                rs.getString("no_hp"),
                rs.getString("alamat"),
                rs.getString("username"),
                rs.getString("password")};
                model.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + e.getMessage());
        }
    }
    public void TambahKaryawan(){
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("T"),"tambahkaryawan");
        getActionMap().put("tambahkaryawan", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                Window window = SwingUtilities.getWindowAncestor(Form_Karyawan.this);
        Form_tbhKaryawan tambah = new Form_tbhKaryawan((java.awt.Frame) window, true);
        tambah.setVisible(true);
        showData();
            }
        });
    }
    public void EditKaryawan(){
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"),"editkaryawan");
        getActionMap().put("editkaryawan", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
        int[]selectedRows=table11.getSelectedRows();
                
        if(selectedRows.length==1){
            int rows = selectedRows[0];
            DefaultTableModel Model = (DefaultTableModel) table11.getModel();
            String ID = Model.getValueAt(rows, 0).toString();
            String Nama = Model.getValueAt(rows, 1).toString();
            String Telepon = Model.getValueAt(rows, 2).toString();
            String Alamat = Model.getValueAt(rows, 3).toString();
            String Username = Model.getValueAt(rows, 4).toString();
            String Password = Model.getValueAt(rows, 5).toString();
            
            String NIK = NIKKaryawan(ID);
            String Gaji = GajiKaryawan(ID);
            Window window = SwingUtilities.getWindowAncestor(Form_Karyawan.this);
            Form_editKaryawan editKaryawan = new Form_editKaryawan((Frame)window, true);
            editKaryawan.setID(ID);
            editKaryawan.ambilData(NIK, ID, Nama, Telepon, Alamat, Username, Password, Gaji);
            editKaryawan.setVisible(true);
            String[]updateData=editKaryawan.getData();
            if(updateData != null){
                Model.setValueAt(updateData[0], rows, 0);//ID
                Model.setValueAt(updateData[1], rows, 1);//Nama
                Model.setValueAt(updateData[2], rows, 2);//Telepon
                Model.setValueAt(updateData[3], rows, 3);//Alamat
                Model.setValueAt(updateData[3], rows, 4);//Username
                Model.setValueAt(updateData[3], rows, 5);//Password
                
            }
        
        }else if(selectedRows.length>1){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_Karyawan.this);
            Pilihsalahsatu salah = new Pilihsalahsatu(parent, true);
            salah.setVisible(true);
            return;
        }else{
            
        }
        showData();
            }
        });
    }
    private String NIKKaryawan(String ID){
        String NIK = "";
        try {
            Connection conn;
            PreparedStatement pstmt;
            String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
            
            String sql = "SELECT nik FROM karyawan WHERE id_karyawan = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                NIK = rs.getString("nik");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return NIK;
    }
    private String GajiKaryawan(String ID){
        String Gaji = "";
        try {
            Connection conn;
            PreparedStatement pstmt;
            String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
            
            String sql = "SELECT gaji FROM karyawan WHERE id_karyawan = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Gaji = rs.getString("gaji");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Gaji;
    }
    public void HapusData(){
         getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"),"hapuskaryawan");
        getActionMap().put("hapuskaryawan", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
        int[]selectedRows=table11.getSelectedRows();
        if(selectedRows.length>0){
            List<String>DeleteID=new ArrayList<>();
            for(int row : selectedRows){
                Object value = table11.getValueAt(row, 0);
                if(value!=null){
                    DeleteID.add(value.toString());
                }
            }
            Window window = SwingUtilities.getWindowAncestor(Form_Karyawan.this);
            Delete hapus = new Delete((Frame)window, true);
            hapus.setVisible(true);
            if(hapus.isConfirmed()){
                String sql = "DELETE FROM karyawan WHERE id_karyawan IN "
                        + "("+String.join(",", Collections.nCopies(DeleteID.size(),"?"))+")";
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:/sembakogrok", "root", "");
                     PreparedStatement pstmt = conn.prepareStatement(sql)){
                    for(int i = 0; i < DeleteID.size();i++){
                        pstmt.setString(i+1, DeleteID.get(i));
                    }
                    int rowsAffected = pstmt.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Data terhapus");
                        showData();
                    }else{
                        System.out.println("Tidak ada data yang terhapus");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
                }
        });
    }
    public void UpdateTabel(List<UrutanDataKaryawan>SortKaryawan){
        DefaultTableModel model = (DefaultTableModel)table11.getModel();
        model.setRowCount(0);
        for(UrutanDataKaryawan dataKaryawan : SortKaryawan){
            model.addRow(new Object[]{dataKaryawan.getID(), dataKaryawan.getNama(), dataKaryawan.getTelepon(), dataKaryawan.getAlamat(), 
                dataKaryawan.getUsername(), dataKaryawan.getPassword()});
        }
    }
    public void UrutanData(String choose){
                
        String sql = "SELECT id_karyawan, nama, no_hp, alamat, username, password FROM karyawan";
        switch (choose) {
            case "Terbaru":
                sql += " ORDER BY id_karyawan";
                System.out.println("Data diurut terbaru");
                break;
                case "Nama Paling Awal":
                sql += " ORDER BY nama ASC";
                System.out.println("Data diurut nama paling awal");
                break;
                case "Nama Paling Akhir":
                sql += " ORDER BY nama DESC";
                System.out.println("Data diurut nama paling akhir");
                break;
            default:
                
        }
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:/sembakogrok", "root", "");
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()){
            List<UrutanDataKaryawan>SortKaryawan = new ArrayList<>();
            while (rs.next()) {                
                UrutanDataKaryawan urutanKaryawan = new UrutanDataKaryawan();
                urutanKaryawan.setID(rs.getString("id_karyawan"));
                urutanKaryawan.setNama(rs.getString("nama"));
                urutanKaryawan.setTelepon(rs.getString("no_hp"));
                urutanKaryawan.setAlamat(rs.getString("alamat"));
                urutanKaryawan.setUsername(rs.getString("username"));
                urutanKaryawan.setPassword(rs.getString("password"));
                SortKaryawan.add(urutanKaryawan);
            }
            Window window = SwingUtilities.getWindowAncestor(Form_Karyawan.this);
                Loading muat = new Loading((java.awt.Frame) window, true);
        muat.setVisible(true);
            UpdateTabel(SortKaryawan);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    private void Pencarian(String Keyword){
        DefaultTableModel model = (DefaultTableModel)table11.getModel();
        model.setRowCount(0);
        String sql = "SELECT * FROM karyawan WHERE id_karyawan LIKE ? OR"
                + " nama LIKE ? OR no_hp LIKE ? OR alamat LIKE ? OR username LIKE ? OR password LIKE ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:/sembakogrok", "root", "");
                PreparedStatement pstmt = conn.prepareStatement(sql);){
            pstmt.setString(1, "%"+Keyword+"%");
            pstmt.setString(2, "%"+Keyword+"%");
            pstmt.setString(3, "%"+Keyword+"%");
            pstmt.setString(4, "%"+Keyword+"%");
            pstmt.setString(5, "%"+Keyword+"%");
            pstmt.setString(6, "%"+Keyword+"%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {                
                String ID = rs.getString("id_karyawan");
                String Nama = rs.getString("nama");
                String Telepon = rs.getString("no_hp");
                String Alamat = rs.getString("alamat");
                String Username = rs.getString("username");
                String Password = rs.getString("password");
                
                model.addRow(new Object[]{ID, Nama, Telepon, Alamat, Username, Password});
            }
            int total = table11.getRowCount();
            if (model.getRowCount()==0) {
                System.out.println("Total pencarian ditemukan : "+total);
            } else {
                System.out.println("Total pencarian ditemukan : "+total);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        table11 = new com.raven.swing.Table1();
        kolompencarian = new jtextfield.TextFieldSuggestion();
        jComboBox_Custom1 = new com.raven.swing.jComboBox_Custom();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(250, 250, 250));

        table11.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nama", "Nomor Telepon", "Alamat", "Username", "Password"
            }
        ));
        table11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table11MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table11);

        kolompencarian.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                kolompencarianFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                kolompencarianFocusLost(evt);
            }
        });
        kolompencarian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kolompencarianActionPerformed(evt);
            }
        });

        jComboBox_Custom1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Terbaru", "Nama Paling Awal", "Nama Paling Akhir" }));
        jComboBox_Custom1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_Custom1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel1.setText("Karyawan");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(kolompencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_Custom1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(kolompencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox_Custom1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void table11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table11MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_table11MouseClicked

    private void kolompencarianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kolompencarianActionPerformed
        Pencarian(kolompencarian.getText());
    }//GEN-LAST:event_kolompencarianActionPerformed

    private void kolompencarianFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_kolompencarianFocusGained
        if (kolompencarian.getText().equals("Cari")) {
            kolompencarian.setText("");
            kolompencarian.setForeground(Color.gray);
            
        }
    }//GEN-LAST:event_kolompencarianFocusGained

    private void kolompencarianFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_kolompencarianFocusLost
        if (kolompencarian.getText().equals("")) {
            kolompencarian.setText("Cari");
            kolompencarian.setForeground(Color.gray);
            
        }
    }//GEN-LAST:event_kolompencarianFocusLost

    private void jComboBox_Custom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_Custom1ActionPerformed
        String selectedOption = (String)jComboBox_Custom1.getSelectedItem();
        UrutanData(selectedOption);
    }//GEN-LAST:event_jComboBox_Custom1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.swing.jComboBox_Custom jComboBox_Custom1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private jtextfield.TextFieldSuggestion kolompencarian;
    private com.raven.swing.Table1 table11;
    // End of variables declaration//GEN-END:variables
}
