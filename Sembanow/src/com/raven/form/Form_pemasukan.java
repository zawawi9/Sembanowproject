package com.raven.form;

import chart.ModelChart;
import config.koneksi;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;

public class Form_pemasukan extends javax.swing.JPanel {
    
    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();
    private final DecimalFormat df;

    public Form_pemasukan() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); 
        df = new DecimalFormat("#,###", symbols);
        initComponents();
        chart();
        table();
       
    }
    
    private void chart() {
    chart.clear();
    chart.setTitle("Pemasukan Tahunan");
    chart.addLegend("Pemasukan ", Color.decode("#2c3ebd"), Color.decode("#49c3fb"));

    try { 
        String sql = "SELECT MONTH(tanggal) AS bulan, SUM(total_keseluruhan) AS total_pemasukan " +
                     "FROM detail_transaksi " +
                     "WHERE YEAR(tanggal) = YEAR(CURDATE()) " +
                     "GROUP BY MONTH(tanggal)";
        PreparedStatement stmt = cn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        // Array untuk menyimpan total per bulan (Januari-Desember)
        double[] totals = new double[12];
        String[] months = {"January", "February", "March", "April", "May", "June", 
                          "July", "August", "September", "October", "November", "December"};

        // Inisialisasi array dengan 0
        for (int i = 0; i < 12; i++) {
            totals[i] = 0;
        }

        // Isi total berdasarkan data dari query
        while (rs.next()) {
            int bulan = rs.getInt("bulan"); // Bulan dalam angka (1-12)
            double totalPemasukan = rs.getDouble("total_pemasukan");
            totals[bulan - 1] = totalPemasukan; // Indeks array dimulai dari 0, bulan dari 1
        }

        // Tambahkan data ke chart
        for (int i = 0; i < 12; i++) {
            chart.addData(new ModelChart(months[i], new double[]{totals[i]}));
        }

        chart.start();
    } catch (SQLException e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data chart: " + e.getMessage());
    }
}
    
    public void table() {
    DefaultTableModel model = new DefaultTableModel();
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("Nama Pelanggan");
    model.addColumn("Total Belanja");
    model.addColumn("Tanggal");

    try {
        String sql = "SELECT pelanggan, total_keseluruhan, tanggal " +
                     "FROM detail_transaksi " +
                     "WHERE YEAR(tanggal) = YEAR(CURDATE()) AND MONTH(tanggal) = MONTH(CURDATE()) " +
                     "ORDER BY tanggal DESC";
        PreparedStatement stmt = cn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        double totalSum = 0; // Variabel untuk menyimpan jumlah total
        while (rs.next()) {
            double totalKeseluruhan = rs.getDouble("total_keseluruhan");
            model.addRow(new Object[]{
                rs.getString("pelanggan"),
                df.format(totalKeseluruhan),
                new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
            });
            totalSum += totalKeseluruhan; // Tambahkan ke totalSum
        }

        table1.setModel(model);
        
        // Pengaturan lebar kolom (opsional, sesuaikan sesuai kebutuhan)
        int[] columnWidths = {200, 150, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        
        // Sesuaikan tabel dengan scroll pane (jika ada method custom)
        table1.fixTable(jScrollPane1);

        // Set total ke jtx bernama "total"
        total.setText(df.format(totalSum));

    } catch (SQLException e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel: " + e.getMessage());
    }
}
    
    public void cariBerdasarkanTanggal() {
    DefaultTableModel model = new DefaultTableModel();
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("Nama Pelanggan");
    model.addColumn("Total Belanja");
    model.addColumn("Tanggal");

    try {
        String inputTanggal = pencarian.getText().trim();
        if (inputTanggal.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(null, "Masukkan tanggal terlebih dahulu!");
            return;
        }

        String sql = "";
        String[] dateParts = inputTanggal.split("-");
        
        double totalSum = 0;

        if (dateParts.length == 3) {
            if (!dateParts[0].matches("\\d{4}") || !dateParts[1].matches("\\d{2}") || !dateParts[2].matches("\\d{2}")) {
                javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy-MM-dd (contoh: 2025-04-26).");
                return;
            }
            
            // Validasi nilai bulan dan tanggal
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

            sql = "SELECT pelanggan, total_keseluruhan, tanggal " +
                  "FROM detail_transaksi " +
                  "WHERE DATE(tanggal) = ? " +
                  "ORDER BY tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setString(1, inputTanggal.replace("/", "-")); // Ubah format ke yyyy-MM-dd untuk SQL
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double totalKeseluruhan = rs.getDouble("total_keseluruhan");
                model.addRow(new Object[]{
                    rs.getString("pelanggan"),
                    df.format(totalKeseluruhan),
                    new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
                });
                totalSum += totalKeseluruhan; // Tambahkan ke totalSum
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

            sql = "SELECT pelanggan, total_keseluruhan, tanggal " +
                  "FROM detail_transaksi " +
                  "WHERE YEAR(tanggal) = ? AND MONTH(tanggal) = ? " +
                  "ORDER BY tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(dateParts[0])); // Tahun
            stmt.setInt(2, bulan); // Bulan
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double totalKeseluruhan = rs.getDouble("total_keseluruhan");
                model.addRow(new Object[]{
                    rs.getString("pelanggan"),
                    df.format(totalKeseluruhan),
                    new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
                });
                totalSum += totalKeseluruhan; // Tambahkan ke totalSum
            }
        } else if (dateParts.length == 1) {
            // Format: yyyy (tahun spesifik)
            if (!dateParts[0].matches("\\d{4}")) {
                javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy (contoh: 2025).");
                return;
            }

            sql = "SELECT pelanggan, total_keseluruhan, tanggal " +
                  "FROM detail_transaksi " +
                  "WHERE YEAR(tanggal) = ? " +
                  "ORDER BY tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(dateParts[0])); // Tahun
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double totalKeseluruhan = rs.getDouble("total_keseluruhan");
                model.addRow(new Object[]{
                    rs.getString("pelanggan"),
                    df.format(totalKeseluruhan),
                    new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
                });
                totalSum += totalKeseluruhan; // Tambahkan ke totalSum
            }
        } else {
            // Jika format tidak valid
            javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy/MM/dd, yyyy/MM, atau yyyy.");
            return;
        }

        // Set model ke tabel
        table1.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {200, 150, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Sesuaikan tabel dengan scroll pane
        table1.fixTable(jScrollPane1);

        // Set total ke jtx bernama "total"
        total.setText(df.format(totalSum));

    } catch (SQLException e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel: " + e.getMessage());
    }
}
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        card3 = new com.raven.component.Card();
        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new com.raven.swing.Table1();
        chart = new chart.CurveLineChart();
        pencarian = new jtextfield.TextFieldSuggestion();
        jLabel1 = new javax.swing.JLabel();
        total = new jtextfield.TextFieldSuggestion();
        jLabel2 = new javax.swing.JLabel();
        comboBox = new jtextfield.ComboBoxSuggestion();

        setBackground(new java.awt.Color(250, 250, 250));

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane1.setViewportView(table1);

        pencarian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pencarianActionPerformed(evt);
            }
        });

        jLabel1.setText("Pencarian :");

        total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalActionPerformed(evt);
            }
        });

        jLabel2.setText("Total :");

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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(17, 17, 17))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pencarianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pencarianActionPerformed
        cariBerdasarkanTanggal();
    }//GEN-LAST:event_pencarianActionPerformed

    private void totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.component.Card card3;
    private chart.CurveLineChart chart;
    private jtextfield.ComboBoxSuggestion comboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private jtextfield.TextFieldSuggestion pencarian;
    private com.raven.swing.Table1 table1;
    private jtextfield.TextFieldSuggestion total;
    // End of variables declaration//GEN-END:variables
}
