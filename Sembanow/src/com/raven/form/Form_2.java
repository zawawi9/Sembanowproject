package com.raven.form;

import chart.ModelChart;
import com.raven.model.ModelCard;
import com.raven.swing.icon.GoogleMaterialDesignIcons;
import com.raven.swing.icon.IconFontSwing;
import config.koneksi;
import java.awt.Color;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.table.DefaultTableModel;

public class Form_2 extends javax.swing.JPanel {
    
    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();
    
    // Format untuk pemisah ribuan
    private final DecimalFormat df;

    public Form_2() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Pemisah ribuan menggunakan titik
        df = new DecimalFormat("#,###", symbols);

        initComponents();
        chart();
        card();
        card1();
        table();
    }

    private void chart() {
        chart.clear();
        chart.setTitle("Pemasukan 7 Hari Terakhir");
        chart.addLegend("Pemasukan Harian", Color.decode("#2c3ebd"), Color.decode("#49c3fb"));

        try {
            String sql = "SELECT DATE(tanggal) AS tanggal_hari, SUM(total_keseluruhan) AS total_harian " +
                        "FROM dashboard " +
                        "GROUP BY DATE(tanggal) " +
                        "ORDER BY tanggal_hari ASC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            double[] totals = new double[7];
            String[] days = new String[7];
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -6); 
            for (int i = 0; i < 7; i++) {
                totals[i] = 0;
                days[i] = sdf.format(cal.getTime());
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }

            while (rs.next()) {
                Date tanggal = rs.getDate("tanggal_hari");
                double totalHarian = rs.getDouble("total_harian");

                long diff = (new Date().getTime() - tanggal.getTime()) / (1000 * 60 * 60 * 24);
                int index = 6 - (int) diff; 
                if (index >= 0 && index < 7) {
                    totals[index] = totalHarian;
                }
            }

            for (int i = 0; i < 7; i++) {
                chart.addData(new ModelChart(days[i], new double[]{totals[i]}));
            }

            chart.start();
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data chart: " + e.getMessage());
        }
    }

    private void card() {
        Icon icon = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.PEOPLE, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15));
        
        try {
            String sql = "SELECT COUNT(*) AS jumlah_transaksi FROM dashboard";
            PreparedStatement stmt = cn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            int jumlahTransaksi = 0;
            if (rs.next()) {
                jumlahTransaksi = rs.getInt("jumlah_transaksi");
            }
            
            card1.setData(new ModelCard("Jumlah Transaksi", jumlahTransaksi, icon));
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data card: " + e.getMessage());
        }
    }

    private void card1() {
        Icon icon = IconFontSwing.buildIcon(GoogleMaterialDesignIcons.ACCOUNT_BALANCE, 60, new Color(255, 255, 255, 100), new Color(255, 255, 255, 15));
        
        try {
            String sql = "SELECT SUM(total_keseluruhan) AS total_akumulasi FROM dashboard";
            PreparedStatement stmt = cn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            double totalAkumulasi = 0;
            if (rs.next()) {
                totalAkumulasi = rs.getDouble("total_akumulasi");
            }
            
            card2.setData(new ModelCard("Total Pemasukan", totalAkumulasi, icon));
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data card1: " + e.getMessage());
        }
    }

    public void table() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nama");
        model.addColumn("Total");
        model.addColumn("Bayar");
        model.addColumn("Kembalian");
        model.addColumn("Kasir");
        model.addColumn("Tanggal");

        try {
            String sql = "SELECT id_penjualan, nama_pelanggan, tanggal, total_keseluruhan, bayar, kembalian, nama_karyawan " +
                        "FROM dashboard " +
                        "ORDER BY tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("nama_pelanggan"),
                    df.format(rs.getDouble("total_keseluruhan")),
                    df.format(rs.getDouble("bayar")),
                    df.format(rs.getDouble("kembalian")),
                    rs.getString("nama_karyawan"),
                    new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
                });
            }

            table1.setModel(model);
            
            int[] columnWidths = {100, 150, 150, 150, 150, 150};
            for (int i = 0; i < columnWidths.length; i++) {
                table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
            }
            
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
        card1 = new com.raven.component.Card();
        card2 = new com.raven.component.Card();

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

        chart.setFillColor(true);
        chart.setTitleColor(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, 777, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(card2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.component.Card card1;
    private com.raven.component.Card card2;
    private com.raven.component.Card card3;
    private chart.CurveLineChart chart;
    private javax.swing.JScrollPane jScrollPane1;
    private com.raven.swing.Table1 table1;
    // End of variables declaration//GEN-END:variables
}
