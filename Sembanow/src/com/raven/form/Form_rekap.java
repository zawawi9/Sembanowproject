package com.raven.form;

import config.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class Form_rekap extends javax.swing.JPanel {
    
    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();
    private final DecimalFormat df;

    public Form_rekap() {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setGroupingSeparator('.'); 
    df = new DecimalFormat("#,###", symbols);
    initComponents();
    table1(); // Inisialisasi table1
    table2(); // Inisialisasi table2 (kosong pada awalnya)
    
    // Tambahkan ListSelectionListener untuk table1
    table1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) { // Pastikan event selesai
                int selectedRow = table1.getSelectedRow();
                if (selectedRow >= 0) {
                    // Ambil id_penjualan dari baris yang dipilih
                    String idPenjualan = table1.getValueAt(selectedRow, 0).toString();
                    updateTable2(idPenjualan); // Perbarui table2 berdasarkan id_penjualan
                } else {
                    updateTable2(null); // Kosongkan table2 jika tidak ada baris yang dipilih
                }
            }
        }
    });
}
private void updateTable2(String idPenjualan) {
    DefaultTableModel model = new DefaultTableModel();
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("ID Penjualan");
    model.addColumn("Nama Produk");
    model.addColumn("Satuan");
    model.addColumn("jumlah");
    model.addColumn("Harga Satuan");
    model.addColumn("Total");

    if (idPenjualan != null) {
        try {
            String sql = "SELECT t.id_penjualan, p.nama AS nama_produk, t.satuan,t.jumlah_produk, t.harga_satuan, t.total " +
                         "FROM transaksi t " +
                         "JOIN produk p ON t.id_produk = p.id_produk " +
                         "WHERE t.id_penjualan = ?";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setString(1, idPenjualan);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_penjualan"),
                    rs.getString("nama_produk"),
                    rs.getString("satuan"),
                    rs.getString("jumlah_produk"),
                    df.format(rs.getDouble("harga_satuan")),
                    df.format(rs.getDouble("total"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel 2: " + e.getMessage());
        }
    }

    table2.setModel(model);

    // Pengaturan lebar kolom
    int[] columnWidths = {100, 150, 100, 150, 150,150};
    for (int i = 0; i < columnWidths.length; i++) {
        table2.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
    }

    // Sesuaikan tabel dengan scroll pane
    table2.fixTable(jScrollPane2);
}

public void table2() {
    DefaultTableModel model = new DefaultTableModel();
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("ID Penjualan");
    model.addColumn("Nama Produk");
    model.addColumn("Satuan");
    model.addColumn("Harga Satuan");
    model.addColumn("Total");

    table2.setModel(model);

    // Pengaturan lebar kolom
    int[] columnWidths = {100, 150, 100, 150, 150};
    for (int i = 0; i < columnWidths.length; i++) {
        table2.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
    }

    // Sesuaikan tabel dengan scroll pane (asumsikan jScrollPane2 untuk table2)
    table2.fixTable(jScrollPane2);
}
public void table1() {
    DefaultTableModel model = new DefaultTableModel();
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("ID Penjualan");
    model.addColumn("Nama Pelanggan");
    model.addColumn("Nama Karyawan");
    model.addColumn("Total Keseluruhan");
    model.addColumn("Bayar");
    model.addColumn("Kembalian");
    model.addColumn("Tanggal");

    try {
        String sql = "SELECT pen.id_penjualan, p.nama AS nama_pelanggan, k.username AS nama_karyawan, " +
                     "pen.tanggal, pen.total_keseluruhan, pen.bayar, pen.kembalian " +
                     "FROM penjualan pen " +
                     "JOIN pelanggan p ON pen.id_pelanggan = p.id_pelanggan " +
                     "LEFT JOIN karyawan k ON pen.id_karyawan = k.id_karyawan " +
                     "ORDER BY pen.tanggal DESC";
        PreparedStatement stmt = cn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("id_penjualan"),
                rs.getString("nama_pelanggan"),
                rs.getString("nama_karyawan"),
                df.format(rs.getDouble("total_keseluruhan")),
                df.format(rs.getDouble("bayar")),
                df.format(rs.getDouble("kembalian")),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("tanggal"))
            });
        }

        table1.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {100, 150, 150, 150, 150, 150, 150};
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
        javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel 1: " + e.getMessage());
    }
}

public void cariBerdasarkanTanggal() {
    DefaultTableModel model = new DefaultTableModel();
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("ID Penjualan");
    model.addColumn("Nama Pelanggan");
    model.addColumn("Nama Karyawan");
    model.addColumn("Total Keseluruhan");
    model.addColumn("Bayar");
    model.addColumn("Kembalian");
    model.addColumn("Tanggal");

    try {
        // Ambil input dari pencarian
        String inputTanggal = pencarian.getText().trim();
        if (inputTanggal.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(null, "Masukkan tanggal terlebih dahulu!");
            return;
        }

        // Validasi format tanggal
        String sql = "";
        String[] dateParts = inputTanggal.split("-");

        if (dateParts.length == 3) {
            if (!dateParts[0].matches("\\d{4}") || !dateParts[1].matches("\\d{2}") || !dateParts[2].matches("\\d{2}")) {
                javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy-MM-dd (contoh: 2025-04-26).");
                return;
            }

            int bulan = Integer.parseInt(dateParts[1]);
            int tanggal = Integer.parseInt(dateParts[2]);
            if (bulan < 1 || bulan > 12) {
                javax.swing.JOptionPane.showMessageDialog(null, "Bulan harus antara 01 dan 12!");
                return;
            }
            if (tanggal < 1 || tanggal > 31) {
                javax.swing.JOptionPane.showMessageDialog(null, "Tanggal harus antara 01 dan 31!");
                return;
            }

            sql = "SELECT pen.id_penjualan, p.nama AS nama_pelanggan, k.username AS nama_karyawan, " +
                  "pen.tanggal, pen.total_keseluruhan, pen.bayar, pen.kembalian " +
                  "FROM penjualan pen " +
                  "JOIN pelanggan p ON pen.id_pelanggan = p.id_pelanggan " +
                  "LEFT JOIN karyawan k ON pen.id_karyawan = k.id_karyawan " +
                  "WHERE DATE(pen.tanggal) = ? " +
                  "ORDER BY pen.tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setString(1, inputTanggal.replace("/", "-"));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_penjualan"),
                    rs.getString("nama_pelanggan"),
                    rs.getString("nama_karyawan"),
                    df.format(rs.getDouble("total_keseluruhan")),
                    df.format(rs.getDouble("bayar")),
                    df.format(rs.getDouble("kembalian")),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("tanggal"))
                });
            }
        } else if (dateParts.length == 2) {
            // Format: yyyy/MM (bulan spesifik)
            if (!dateParts[0].matches("\\d{4}") || !dateParts[1].matches("\\d{2}")) {
                javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy-MM (contoh: 2025-04).");
                return;
            }

            int bulan = Integer.parseInt(dateParts[1]);
            if (bulan < 1 || bulan > 12) {
                javax.swing.JOptionPane.showMessageDialog(null, "Bulan harus antara 01 dan 12!");
                return;
            }

            sql = "SELECT pen.id_penjualan, p.nama AS nama_pelanggan, k.username AS nama_karyawan, " +
                  "pen.tanggal, pen.total_keseluruhan, pen.bayar, pen.kembalian " +
                  "FROM penjualan pen " +
                  "JOIN pelanggan p ON pen.id_pelanggan = p.id_pelanggan " +
                  "LEFT JOIN karyawan k ON pen.id_karyawan = k.id_karyawan " +
                  "WHERE YEAR(pen.tanggal) = ? AND MONTH(pen.tanggal) = ? " +
                  "ORDER BY pen.tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(dateParts[0]));
            stmt.setInt(2, bulan);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_penjualan"),
                    rs.getString("nama_pelanggan"),
                    rs.getString("nama_karyawan"),
                    df.format(rs.getDouble("total_keseluruhan")),
                    df.format(rs.getDouble("bayar")),
                    df.format(rs.getDouble("kembalian")),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("tanggal"))
                });
            }
        } else if (dateParts.length == 1) {
            // Format: yyyy (tahun spesifik)
            if (!dateParts[0].matches("\\d{4}")) {
                javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy (contoh: 2025).");
                return;
            }

            sql = "SELECT pen.id_penjualan, p.nama AS nama_pelanggan, k.username AS nama_karyawan, " +
                  "pen.tanggal, pen.total_keseluruhan, pen.bayar, pen.kembalian " +
                  "FROM penjualan pen " +
                  "JOIN pelanggan p ON pen.id_pelanggan = p.id_pelanggan " +
                  "LEFT JOIN karyawan k ON pen.id_karyawan = k.id_karyawan " +
                  "WHERE YEAR(pen.tanggal) = ? " +
                  "ORDER BY pen.tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(dateParts[0]));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_penjualan"),
                    rs.getString("nama_pelanggan"),
                    rs.getString("nama_karyawan"),
                    df.format(rs.getDouble("total_keseluruhan")),
                    df.format(rs.getDouble("bayar")),
                    df.format(rs.getDouble("kembalian")),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("tanggal"))
                });
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy/MM/dd, yyyy/MM, atau yyyy.");
            return;
        }

        table1.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {100, 150, 150, 150, 150, 150, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        table1.fixTable(jScrollPane1);

        // Setelah table1 diperbarui, perbarui table2 berdasarkan baris pertama (jika ada)
        if (model.getRowCount() > 0) {
            table1.setRowSelectionInterval(0, 0); // Pilih baris pertama
            String idPenjualan = table1.getValueAt(0, 0).toString();
            updateTable2(idPenjualan);
        } else {
            updateTable2(null); // Kosongkan table2 jika tidak ada data
        }

    } catch (SQLException e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel 1: " + e.getMessage());
    }
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        card3 = new com.raven.component.Card();
        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new com.raven.swing.Table1();
        pencarian = new jtextfield.TextFieldSuggestion();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table2 = new com.raven.swing.Table1();

        setBackground(new java.awt.Color(250, 250, 250));

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

        pencarian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pencarianActionPerformed(evt);
            }
        });

        jLabel1.setText("Pencarian :");

        table2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(table2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1139, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1145, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pencarianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pencarianActionPerformed
        cariBerdasarkanTanggal();
    }//GEN-LAST:event_pencarianActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.component.Card card3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private jtextfield.TextFieldSuggestion pencarian;
    private com.raven.swing.Table1 table1;
    private com.raven.swing.Table1 table2;
    // End of variables declaration//GEN-END:variables
}
