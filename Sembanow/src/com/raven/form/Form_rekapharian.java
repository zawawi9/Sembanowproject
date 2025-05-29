package com.raven.form;

import config.koneksi;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import raven.dialog.FailCount;
import raven.dialog.FailLoaded;
import raven.dialog.Loading;

public class Form_rekapharian extends javax.swing.JPanel {
    
    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();
    private final DecimalFormat df;

    public Form_rekapharian() {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setGroupingSeparator('.'); 
    df = new DecimalFormat("#,###", symbols);
    initComponents();
    table1();
    addcost();
    java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_rekapharian.this);
                Loading load = new Loading(parent, true);
            load.setVisible(true);
    
    // Tambahkan ListSelectionListener untuk table1
    
}
    public void addcost(){
        tambah.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String input = tambah.getText().trim();
                    if (!input.isEmpty()) {
                        double value = Double.parseDouble(input.replace(".", "").replace(",", ""));
                        String formattedValue = df.format(value);
                        DefaultTableModel model = (DefaultTableModel) table2.getModel();
                        model.addRow(new Object[]{formattedValue});
                        tambah.setText("");
                        tambah.requestFocus();
                        p();
                    }
            }else if (e.getKeyCode() == KeyEvent.VK_UP) {
                pemasukan1.requestFocus();
            }
        }
    });
        pemasukan1.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String input = pemasukan1.getText().trim();
                    if (!input.isEmpty()) {
                        double value = Double.parseDouble(input.replace(".", "").replace(",", ""));
                        String formattedValue = df.format(value);
                        kembalian.requestFocus();
                        pemasukan1.setText(formattedValue);
                    }
            }else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                tambah.requestFocus();
            }
        }
    });
        kembalian.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String input = kembalian.getText().trim();
                    String pemasukan = pemasukan1.getText().trim();
                    String pengeluaran = pengeluaran1.getText().trim();
                    if (!input.isEmpty()&&!pemasukan.isEmpty()&&!pemasukan.isEmpty()) {
                        double value = Double.parseDouble(input.replace(".", "").replace(",", ""));
                        double inflow = Double.parseDouble(pemasukan.replace(".", "").replace(",", ""));
                        double outflow = Double.parseDouble(pengeluaran.replace(".", "").replace(",", ""));
                        double saldo1= inflow+value-outflow;
                        String formattedValue = df.format(value);
                        String saldoakhir = df.format(saldo1);
                        kembalian.setText(formattedValue);
                        saldo.setText(saldoakhir);
                        table2.requestFocus();
                    }
            }
        }
    });
        table2.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            int selectedRow = table2.getSelectedRow();
            if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (selectedRow >= 0) {
                        DefaultTableModel model = (DefaultTableModel) table2.getModel();
                        String p = model.getValueAt(selectedRow,0).toString().replace(".", ""); // Harga Satuan
                        tambah.setText("");
                        tambah.setText(p);
                        tambah.requestFocus();
                        model.removeRow(selectedRow);
                    }
                } 
        }
    });
    }
    
    private void p() {
        try {
            // Ambil model tabel
            DefaultTableModel model = (DefaultTableModel) table2.getModel();

            // Hitung total dari kolom Total (indeks 7)
            double totalSum = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                // Ambil nilai dari kolom Total (indeks 7), hapus pemisah titik untuk parsing
                String totalStr = model.getValueAt(i, 0).toString().replace(".", "");
                double totalValue = Double.parseDouble(totalStr);
                totalSum += totalValue;
            }

            pengeluaran1.setText( df.format(totalSum));
        } catch (Exception e) {
            e.printStackTrace();
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_rekapharian.this);
                FailCount load = new FailCount(parent, true);
            load.setVisible(true);
        }
    }
    
    public void table1() {
    DefaultTableModel model = new DefaultTableModel();
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("ID Karyawan");
    model.addColumn("Nama Karyawan");
    model.addColumn("Total");

    try {
        String sql = "SELECT p.id_karyawan, k.username AS nama_karyawan, SUM(p.total_keseluruhan) AS total " +
                     "FROM penjualan p " +
                     "JOIN karyawan k ON p.id_karyawan = k.id_karyawan " +
                     "WHERE DATE(p.tanggal) = CURDATE() " +
                     "GROUP BY p.id_karyawan, k.username";
        PreparedStatement stmt = cn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("id_karyawan"),
                rs.getString("nama_karyawan"),
                df.format(rs.getDouble("total"))
            });
        }

        table1.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {100, 150, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Sesuaikan tabel dengan scroll pane
        table1.fixTable(jScrollPane1);

        // Pilih baris pertama secara otomatis jika ada data
        if (table1.getRowCount() > 0) {
            table1.setRowSelectionInterval(0, 0);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_rekapharian.this);
                FailLoaded load = new FailLoaded(parent, true);
            load.setVisible(true);
    }
}
    
  

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        card3 = new com.raven.component.Card();
        pemasukan1 = new jtextfield.TextFieldSuggestion();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table2 = new com.raven.swing.Table1();
        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new com.raven.swing.Table1();
        jLabel2 = new javax.swing.JLabel();
        pengeluaran1 = new jtextfield.TextFieldSuggestion();
        jLabel3 = new javax.swing.JLabel();
        saldo = new jtextfield.TextFieldSuggestion();
        jLabel4 = new javax.swing.JLabel();
        tambah = new jtextfield.TextFieldSuggestion();
        kembalian = new jtextfield.TextFieldSuggestion();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(250, 250, 250));

        pemasukan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pemasukan1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Total pemasukan");

        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "pengeluaran"
            }
        ));
        jScrollPane2.setViewportView(table2);

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(table1);

        jLabel2.setText("total pengeluaran");

        pengeluaran1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pengeluaran1ActionPerformed(evt);
            }
        });

        jLabel3.setText("saldo akhir");

        saldo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saldoActionPerformed(evt);
            }
        });

        jLabel4.setText("tambah pengeluaran");

        tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahActionPerformed(evt);
            }
        });

        kembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kembalianActionPerformed(evt);
            }
        });

        jLabel5.setText("uang kembalian");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel6.setText("+");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setText("-");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel8.setText("=");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(49, 49, 49))
                                .addComponent(tambah, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(pemasukan1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(49, 49, 49))
                                    .addComponent(kembalian, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(49, 49, 49))
                                    .addComponent(pengeluaran1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(49, 49, 49))
                                    .addComponent(saldo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(pemasukan1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(saldo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(kembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(pengeluaran1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tambah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(72, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pemasukan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pemasukan1ActionPerformed
       
    }//GEN-LAST:event_pemasukan1ActionPerformed

    private void pengeluaran1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pengeluaran1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pengeluaran1ActionPerformed

    private void saldoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saldoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saldoActionPerformed

    private void tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tambahActionPerformed

    private void kembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kembalianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kembalianActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.component.Card card3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private jtextfield.TextFieldSuggestion kembalian;
    private jtextfield.TextFieldSuggestion pemasukan1;
    private jtextfield.TextFieldSuggestion pengeluaran1;
    private jtextfield.TextFieldSuggestion saldo;
    private com.raven.swing.Table1 table1;
    private com.raven.swing.Table1 table2;
    private jtextfield.TextFieldSuggestion tambah;
    // End of variables declaration//GEN-END:variables
}
