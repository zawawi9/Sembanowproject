package com.raven.form;

import chart.ModelChart;
import com.raven.model.ModelCard;
import com.raven.swing.icon.GoogleMaterialDesignIcons;
import com.raven.swing.icon.IconFontSwing;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Icon;
import javax.swing.table.DefaultTableModel;

public class Form_pengeluaran extends javax.swing.JPanel {


    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();
    private final DecimalFormat df;

    public Form_pengeluaran() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); 
        df = new DecimalFormat("#,###", symbols);
        initComponents();
        chart();
        table();
       
    }
    
    private void chart() {
    chart.clear();
    chart.setTitle("pengeluaran Tahunan");
    chart.addLegend("pengeluaran ", Color.decode("#e65c00"), Color.decode("#F9D423"));

    try {
        // Query untuk mengambil total pemasukan per bulan dalam tahun 2025
        String sql = "SELECT MONTH(tanggal) AS bulan, SUM(total_keseluruhan) AS total_pemasukan " +
                     "FROM dashboard " +
                     "WHERE YEAR(tanggal) = YEAR(CURDATE()) " +
                     "GROUP BY MONTH(tanggal) " +
                     "ORDER BY bulan ASC";
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
        model.addColumn("Total");
        model.addColumn("Tanggal");

        try {
            // Query untuk mengambil data dari view, hanya nama_pelanggan, total_keseluruhan, dan tanggal
            String sql =    "SELECT \n" +
                            "    p.nama AS nama_pelanggan, \n" +
                            "    pen.total_keseluruhan, \n" +
                            "    pen.tanggal\n" +
                            "FROM \n" +
                            "    penjualan pen\n" +
                            "JOIN \n" +
                            "    pelanggan p ON pen.id_pelanggan = p.id_pelanggan\n" +
                            "ORDER BY \n" +
                            "    pen.tanggal DESC;";
            PreparedStatement stmt = cn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nama_pelanggan"),
                    df.format(rs.getDouble("total_keseluruhan")),
                    new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
                });
            }

            table1.setModel(model);
            
            // Pengaturan lebar kolom (opsional, sesuaikan sesuai kebutuhan)
            int[] columnWidths = {150, 150, 150};
            for (int i = 0; i < columnWidths.length; i++) {
                table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
            }
            
            // Sesuaikan tabel dengan scroll pane (jika ada method custom)
            table1.fixTable(jScrollPane1);

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
    model.addColumn("Total");
    model.addColumn("Tanggal");

    try {
        // Ambil input dari jtxPencarian
        String inputTanggal = pencarian.getText().trim();
        if (inputTanggal.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(null, "Masukkan tanggal terlebih dahulu!");
            return;
        }

        // Validasi format tanggal
        String sql = "";
        String[] dateParts = inputTanggal.split("/");
        
        // Validasi apakah setiap bagian adalah angka
        if (dateParts.length == 3) {
            // Format: yyyy/MM/dd (tanggal spesifik)
            // Validasi bahwa setiap bagian adalah angka
            if (!dateParts[0].matches("\\d{4}") || !dateParts[1].matches("\\d{2}") || !dateParts[2].matches("\\d{2}")) {
                javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy/MM/dd (contoh: 2025/04/26).");
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

            sql = "SELECT p.nama AS nama_pelanggan, pen.total_keseluruhan, pen.tanggal " +
                  "FROM penjualan pen " +
                  "JOIN pelanggan p ON pen.id_pelanggan = p.id_pelanggan " +
                  "WHERE DATE(pen.tanggal) = ? " +
                  "ORDER BY pen.tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setString(1, inputTanggal.replace("/", "-")); // Ubah format ke yyyy-MM-dd untuk SQL
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nama_pelanggan"),
                    df.format(rs.getDouble("total_keseluruhan")),
                    new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
                });
            }
        } else if (dateParts.length == 2) {
            // Format: yyyy/MM (bulan spesifik)
            if (!dateParts[0].matches("\\d{4}") || !dateParts[1].matches("\\d{2}")) {
                javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy/MM (contoh: 2025/04).");
                return;
            }

            int bulan = Integer.parseInt(dateParts[1]);
            if (bulan < 1 || bulan > 12) {
                javax.swing.JOptionPane.showMessageDialog(null, "Bulan harus antara 01 dan 12!");
                return;
            }

            sql = "SELECT p.nama AS nama_pelanggan, pen.total_keseluruhan, pen.tanggal " +
                  "FROM penjualan pen " +
                  "JOIN pelanggan p ON pen.id_pelanggan = p.id_pelanggan " +
                  "WHERE YEAR(pen.tanggal) = ? AND MONTH(pen.tanggal) = ? " +
                  "ORDER BY pen.tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(dateParts[0])); // Tahun
            stmt.setInt(2, bulan); // Bulan
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nama_pelanggan"),
                    df.format(rs.getDouble("total_keseluruhan")),
                    new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
                });
            }
        } else if (dateParts.length == 1) {
            // Format: yyyy (tahun spesifik)
            if (!dateParts[0].matches("\\d{4}")) {
                javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy (contoh: 2025).");
                return;
            }

            sql = "SELECT p.nama AS nama_pelanggan, pen.total_keseluruhan, pen.tanggal " +
                  "FROM penjualan pen " +
                  "JOIN pelanggan p ON pen.id_pelanggan = p.id_pelanggan " +
                  "WHERE YEAR(pen.tanggal) = ? " +
                  "ORDER BY pen.tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(dateParts[0])); // Tahun
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nama_pelanggan"),
                    df.format(rs.getDouble("total_keseluruhan")),
                    new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
                });
            }
        } else {
            // Jika format tidak valid
            javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy/MM/dd, yyyy/MM, atau yyyy.");
            return;
        }

        // Set model ke tabel
        table1.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {150, 150, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Sesuaikan tabel dengan scroll pane
        table1.fixTable(jScrollPane1);

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
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pencarianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pencarianActionPerformed
        cariBerdasarkanTanggal();
    }//GEN-LAST:event_pencarianActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.component.Card card3;
    private chart.CurveLineChart chart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private jtextfield.TextFieldSuggestion pencarian;
    private com.raven.swing.Table1 table1;
    // End of variables declaration//GEN-END:variables
}
