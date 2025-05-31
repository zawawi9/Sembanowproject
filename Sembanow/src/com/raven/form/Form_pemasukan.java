package com.raven.form;

import cetak.ReportGeneratorExcel;
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
import java.util.Calendar;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import raven.dialog.FailLoaded;
import raven.dialog.Loading;
import raven.dialog.MasukkanTanggal;
import raven.dialog.SesuaiFormat_YYYYMMDD;
import raven.dialog.SesuaiFormat_MM;
import raven.dialog.SesuaiFormat2_2;
import raven.dialog.SesuaiFormat_DD;
import raven.dialog.SesuaiFormat_Tanggal;
import raven.dialog.SesuaiFormat_YYYY;

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
        java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(Form_pemasukan.this);
        Loading load = new Loading(parent, true);
        load.setVisible(true);
    }

    private void chart() {
        chart.clear();
        chart.setTitle("Pemasukan Tahunan");
        chart.addLegend("Pemasukan ", Color.decode("#2c3ebd"), Color.decode("#49c3fb"));

        try {
            String sql = "SELECT MONTH(tanggal) AS bulan, SUM(total_keseluruhan) AS total_pemasukan "
                    + "FROM detail_transaksi "
                    + "WHERE YEAR(tanggal) = YEAR(CURDATE()) "
                    + "GROUP BY MONTH(tanggal)";
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
            java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(Form_pemasukan.this);
            FailLoaded load = new FailLoaded(parent, true);
            load.setVisible(true);
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
            String sql = "SELECT pelanggan, total_keseluruhan, tanggal "
                    + "FROM detail_transaksi "
                    + "WHERE YEAR(tanggal) = YEAR(CURDATE()) AND MONTH(tanggal) = MONTH(CURDATE()) "
                    + "ORDER BY tanggal DESC";
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
            java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(Form_pemasukan.this);
            FailLoaded load = new FailLoaded(parent, true);
            load.setVisible(true);
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
                java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(Form_pemasukan.this);
                MasukkanTanggal load = new MasukkanTanggal(parent, true);
                load.setVisible(true);
                return;
            }

            String sql = "";
            String[] dateParts = inputTanggal.split("-");

            double totalSum = 0;

            if (dateParts.length == 3) {
                if (!dateParts[0].matches("\\d{4}") || !dateParts[1].matches("\\d{2}") || !dateParts[2].matches("\\d{2}")) {
                    java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(Form_pemasukan.this);
                    SesuaiFormat_YYYYMMDD load = new SesuaiFormat_YYYYMMDD(parent, true);
                    load.setVisible(true);
                    return;
                }

                // Validasi nilai bulan dan tanggal
                int bulan = Integer.parseInt(dateParts[1]);
                int tanggal = Integer.parseInt(dateParts[2]);
                if (bulan < 1 || bulan > 12) {
                    java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(Form_pemasukan.this);
                    SesuaiFormat_MM load = new SesuaiFormat_MM(parent, true);
                    load.setVisible(true);
                    return;
                }
                if (tanggal < 1 || tanggal > 31) {
                    java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(Form_pemasukan.this);
                    SesuaiFormat_DD load = new SesuaiFormat_DD(parent, true);
                    load.setVisible(true);
                    return;
                }

                sql = "SELECT pelanggan, total_keseluruhan, tanggal "
                        + "FROM detail_transaksi "
                        + "WHERE DATE(tanggal) = ? "
                        + "ORDER BY tanggal DESC";
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
                    java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(Form_pemasukan.this);
                    SesuaiFormat2_2 load = new SesuaiFormat2_2(parent, true);
                    load.setVisible(true);
                    return;
                }

                int bulan = Integer.parseInt(dateParts[1]);
                if (bulan < 1 || bulan > 12) {
                    java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(Form_pemasukan.this);
                    SesuaiFormat_MM load = new SesuaiFormat_MM(parent, true);
                    load.setVisible(true);
                    return;
                }

                sql = "SELECT pelanggan, total_keseluruhan, tanggal "
                        + "FROM detail_transaksi "
                        + "WHERE YEAR(tanggal) = ? AND MONTH(tanggal) = ? "
                        + "ORDER BY tanggal DESC";
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
                    java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(Form_pemasukan.this);
                    SesuaiFormat_YYYY load = new SesuaiFormat_YYYY(parent, true);
                    load.setVisible(true);
                    return;
                }

                sql = "SELECT pelanggan, total_keseluruhan, tanggal "
                        + "FROM detail_transaksi "
                        + "WHERE YEAR(tanggal) = ? "
                        + "ORDER BY tanggal DESC";
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
                java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(Form_pemasukan.this);
                SesuaiFormat_Tanggal load = new SesuaiFormat_Tanggal(parent, true);
                load.setVisible(true);
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
            java.awt.Frame parent = (java.awt.Frame) SwingUtilities.getWindowAncestor(Form_pemasukan.this);
            FailLoaded load = new FailLoaded(parent, true);
            load.setVisible(true);
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
        ExportPemasukan = new Custom.Custom_ButtonRounded();

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

        ExportPemasukan.setText("Export");
        ExportPemasukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportPemasukanActionPerformed(evt);
            }
        });

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
                                .addComponent(ExportPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                    .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ExportPemasukan, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void ExportPemasukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportPemasukanActionPerformed
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        JComboBox<String> monthChooser = new JComboBox<>(months);

        // Set bulan saat ini sebagai default
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH); // 0-11
        monthChooser.setSelectedIndex(currentMonth);

        // Untuk tahun, bisa pakai JSpinner atau JTextField
        SpinnerModel yearModel = new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), // initial value
                2000, // min
                Calendar.getInstance().get(Calendar.YEAR) + 10, // max
                1); // step
        JSpinner yearChooser = new JSpinner(yearModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(yearChooser, "####"); // "####" menghilangkan pemisah ribuan
        yearChooser.setEditor(editor);

        panel.add(new JLabel("Pilih Bulan:"));
        panel.add(monthChooser);
        panel.add(Box.createVerticalStrut(10)); // Spasi
        panel.add(new JLabel("Pilih Tahun:"));
        panel.add(yearChooser);

        int result = JOptionPane.showConfirmDialog(null, panel, "Pilih Periode Export",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int selectedMonthIndex = monthChooser.getSelectedIndex(); // 0-11
            int selectedMonth = selectedMonthIndex + 1; // Konversi ke 1-12 untuk SQL MONTH()
            int selectedYear = (int) yearChooser.getValue();

            // 2. Panggil fungsi export
            ReportGeneratorExcel generator = new ReportGeneratorExcel();
            generator.exportPemasukanToExcel(selectedYear, selectedMonth);
        }
    }//GEN-LAST:event_ExportPemasukanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Custom.Custom_ButtonRounded ExportPemasukan;
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
