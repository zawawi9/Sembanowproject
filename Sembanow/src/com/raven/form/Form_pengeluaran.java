package com.raven.form;

import chart.ModelChart;
import config.koneksi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import jtextfield.TextFieldSuggestion;

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
    chart.setTitle("Pengeluaran Tahunan");
    chart.addLegend("Pengeluaran ", Color.decode("#FF0000"), Color.decode("#FF6666")); // Ubah warna ke merah

    try {
        String sql = "SELECT MONTH(tanggal) AS bulan, SUM(total) AS total_pengeluaran " +
                     "FROM pengeluaran " +
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
            double totalPengeluaran = rs.getDouble("total_pengeluaran");
            totals[bulan - 1] = totalPengeluaran; // Indeks array dimulai dari 0, bulan dari 1
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
    model.addColumn("Status");
    model.addColumn("Keterangan");
    model.addColumn("Jumlah");
    model.addColumn("Total");
    model.addColumn("Tanggal");

    try {
        String sql = "SELECT status, jumlah, total, keterangan, tanggal " +
                     "FROM pengeluaran " +
                     "ORDER BY tanggal DESC";
        PreparedStatement stmt = cn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        double totalSum = 0; // Variabel untuk menyimpan jumlah total
        while (rs.next()) {
            double totalValue = rs.getDouble("total");
            Object jumlah = rs.getObject("jumlah");
            model.addRow(new Object[]{
                rs.getString("status"),
                rs.getString("keterangan"),
                jumlah != null ? jumlah : "kosong",
                df.format(totalValue),
                new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
            });
            totalSum += totalValue; // Tambahkan ke totalSum
        }

        table1.setModel(model);
        
        // Pengaturan lebar kolom (opsional, sesuaikan sesuai kebutuhan)
        int[] columnWidths = {150, 100, 200, 150, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        
        // Sesuaikan tabel dengan scroll pane (jika ada method custom)
        table1.fixTable(jScrollPane1);

        // Set total ke jtx bernama "total"
        pencarian1.setText(df.format(totalSum));

    } catch (SQLException e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel: " + e.getMessage());
    }
}
    
    public void cariBerdasarkanTanggal() {
    DefaultTableModel model = new DefaultTableModel();
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("Status");
    model.addColumn("Keterangan");
    model.addColumn("Jumlah");
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
        String[] dateParts = inputTanggal.split("-");
        
        // Variabel untuk menyimpan jumlah total
        double totalSum = 0;

        // Validasi apakah setiap bagian adalah angka
        if (dateParts.length == 3) {
            // Format: yyyy/MM/dd (tanggal spesifik)
            // Validasi bahwa setiap bagian adalah angka
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

            sql = "SELECT status, jumlah, total, keterangan, tanggal " +
                  "FROM pengeluaran " +
                  "WHERE DATE(tanggal) = ? " +
                  "ORDER BY tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setString(1, inputTanggal.replace("/", "-")); // Ubah format ke yyyy-MM-dd untuk SQL
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double totalValue = rs.getDouble("total");
                Object jumlah = rs.getObject("jumlah");
                model.addRow(new Object[]{
                    rs.getString("status"),
                    rs.getString("keterangan"),
                    jumlah != null ? jumlah : "kosong",
                    df.format(totalValue),
                    new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
                });
                totalSum += totalValue; // Tambahkan ke totalSum
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

            sql = "SELECT status, jumlah, total, keterangan, tanggal " +
                  "FROM pengeluaran " +
                  "WHERE YEAR(tanggal) = ? AND MONTH(tanggal) = ? " +
                  "ORDER BY tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(dateParts[0])); // Tahun
            stmt.setInt(2, bulan); // Bulan
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double totalValue = rs.getDouble("total");
                Object jumlah = rs.getObject("jumlah");
                model.addRow(new Object[]{
                    rs.getString("status"),
                    rs.getString("keterangan"),
                    jumlah != null ? jumlah : "kosong",
                    df.format(totalValue),
                    new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
                });
                totalSum += totalValue; // Tambahkan ke totalSum
            }
        } else if (dateParts.length == 1) {
            // Format: yyyy (tahun spesifik)
            if (!dateParts[0].matches("\\d{4}")) {
                javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy (contoh: 2025).");
                return;
            }

            sql = "SELECT status, jumlah, total, keterangan, tanggal " +
                  "FROM pengeluaran " +
                  "WHERE YEAR(tanggal) = ? " +
                  "ORDER BY tanggal DESC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(dateParts[0])); // Tahun
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double totalValue = rs.getDouble("total");
                Object jumlah = rs.getObject("jumlah");
                model.addRow(new Object[]{
                    rs.getString("status"),
                    rs.getString("keterangan"),
                    jumlah != null ? jumlah : "kosong",
                    df.format(totalValue),
                    new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("tanggal"))
                });
                totalSum += totalValue; // Tambahkan ke totalSum
            }
        } else {
            // Jika format tidak valid
            javax.swing.JOptionPane.showMessageDialog(null, "Format tanggal tidak valid! Gunakan yyyy/MM/dd, yyyy/MM, atau yyyy.");
            return;
        }

        // Set model ke tabel
        table1.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {150, 100, 150, 200, 150, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Sesuaikan tabel dengan scroll pane
        table1.fixTable(jScrollPane1);

        // Set total ke jtx bernama "total"
        pencarian1.setText(df.format(totalSum));

    } catch (SQLException e) {
        e.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel: " + e.getMessage());
    }
}
    
    private void addpengeluaran() {
         // Membuat field input menggunakan TextFieldSuggestion
        TextFieldSuggestion keterangan = new TextFieldSuggestion();
        TextFieldSuggestion total = new TextFieldSuggestion();

        // Atur panjang TextFieldSuggestion
        Dimension fieldSize = new Dimension(200, 35);
        total.setPreferredSize(fieldSize);
        keterangan.setPreferredSize(fieldSize);


        // Label untuk setiap field
        JLabel l0 = new JLabel("keterangan:");
        JLabel l1 = new JLabel("total:");

        // Panel utama dengan GridLayout 6x1 (6 baris, 1 kolom, gap 5)
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Tambahkan pasangan label dan field ke mainPanel dalam grid 6x1
        mainPanel.add(createInputPanel(l0, keterangan));
        mainPanel.add(createInputPanel(l1, total));

        // Tambahkan KeyListener untuk navigasi dengan Enter
        KeyListener enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    Component current = (Component) e.getSource();
                    current.transferFocus();
                }
            }
        };

        // Terapkan KeyListener ke setiap field (kecuali ComboBox)
        keterangan.addKeyListener(enterKeyListener);
        total.addKeyListener(enterKeyListener);

        // Panel utama dialog dengan BorderLayout
        JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
        dialogPanel.add(mainPanel, BorderLayout.CENTER);
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Buat JOptionPane dengan panel custom
        JOptionPane optionPane = new JOptionPane(
            dialogPanel,
            JOptionPane.PLAIN_MESSAGE,
            JOptionPane.OK_CANCEL_OPTION
        );

        // Buat dialog
        JDialog dialog = optionPane.createDialog(this, "Masukkan Data Karyawan");

        // Pastikan semua komponen di dalam dialog juga putih
        setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);

        // Show dialog
        dialog.setVisible(true);
        keterangan.requestFocusInWindow();

        // Dapatkan hasil dari dialog
        Object selectedValue = optionPane.getValue();
        if (selectedValue == null || (Integer) selectedValue != JOptionPane.OK_OPTION) {
            String message = (selectedValue != null && (Integer) selectedValue == JOptionPane.CLOSED_OPTION)
                ? "Dialog ditutup. Operasi dibatalkan."
                : "Operasi dibatalkan.";
            JOptionPane.showMessageDialog(this, message);
            return;
        }

        // Ambil input untuk penyimpanan
        String valketerangan = keterangan.getText().trim();
        String valtotal = total.getText().trim();
        String status = "lainnya";

        // Lanjutkan dengan penyimpanan data
        try {
            // Validasi input
            if (valketerangan.isEmpty() || valtotal.isEmpty() ) {
                JOptionPane.showMessageDialog(this, "harus di isi semua.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double valGajiNum = Double.parseDouble(valtotal.isEmpty() ? "0" : valtotal);

            // Disable autocommit untuk memulai transaksi
            cn.setAutoCommit(false);

            // Query untuk insert ke tabel karyawan
            String sql = "INSERT INTO pengeluaran ( keterangan, total,status) VALUES (?, ?, ?);";
            try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
                pstmt.setString(1, valketerangan);
                pstmt.setDouble(2, valGajiNum);
                pstmt.setString(3, status);
                pstmt.executeUpdate();
            }
            table();

            cn.commit(); // Commit transaksi
            JOptionPane.showMessageDialog(this, "Data pengeluaran berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException | NumberFormatException ex) {
            try {
                if (cn != null) cn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Error menyimpan data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } 
    }
    
    private JPanel createInputPanel(JLabel label, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.add(label, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }
    
    private void setBackgroundRecursively(Container container, Color color) {
        container.setBackground(color);
        for (Component comp : container.getComponents()) {
            comp.setBackground(color);
            if (comp instanceof Container) {
                setBackgroundRecursively((Container) comp, color);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new com.raven.swing.Table1();
        chart = new chart.CurveLineChart();
        pencarian = new jtextfield.TextFieldSuggestion();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        pencarian1 = new jtextfield.TextFieldSuggestion();
        jButton1 = new javax.swing.JButton();

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

        jLabel2.setText("Total :");

        jLabel1.setText("Pencarian :");

        pencarian1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pencarian1ActionPerformed(evt);
            }
        });

        jButton1.setText("tambah");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pencarian1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(17, 17, 17))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pencarian1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pencarianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pencarianActionPerformed
        cariBerdasarkanTanggal();
    }//GEN-LAST:event_pencarianActionPerformed

    private void pencarian1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pencarian1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pencarian1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        addpengeluaran();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private chart.CurveLineChart chart;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private jtextfield.TextFieldSuggestion pencarian;
    private jtextfield.TextFieldSuggestion pencarian1;
    private com.raven.swing.Table1 table1;
    // End of variables declaration//GEN-END:variables
}
