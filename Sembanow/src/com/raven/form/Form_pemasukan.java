package com.raven.form;

import chart.ModelChart;
import com.raven.model.ModelCard;
import com.raven.swing.icon.GoogleMaterialDesignIcons;
import com.raven.swing.icon.IconFontSwing;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Icon;

public class Form_pemasukan extends javax.swing.JPanel {

    public Form_pemasukan() {
        initComponents();
        initChartData();
        initTableData(); // Tambahkan inisialisasi data tabel
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                chart1.revalidate();
                chart1.repaint();
            }
        });
    }

    private void initChartData() {
        // Atur judul dan legenda chart
        chart1.setTitle("Chart Pemasukan");
        chart1.addLegend("Mingguan", Color.decode("#7b4397"), Color.decode("#dc2430"));
        chart1.addLegend("3 Bulanan", Color.decode("#e65c00"), Color.decode("#F9D423"));
        chart1.addLegend("Tahunan", Color.decode("#0099F7"), Color.decode("#F11712"));

        // Data dummy
        List<String> labelsMingguan = new ArrayList<>();
        List<Double> totalsMingguan = new ArrayList<>();
        List<String> labels3Bulanan = new ArrayList<>();
        List<Double> totals3Bulanan = new ArrayList<>();
        List<String> labelsTahunan = new ArrayList<>();
        List<Double> totalsTahunan = new ArrayList<>();

        // Data Mingguan (contoh 5 hari)
        labelsMingguan.add("Senin"); totalsMingguan.add(5000.0);
        labelsMingguan.add("Selasa"); totalsMingguan.add(7000.0);
        labelsMingguan.add("Rabu"); totalsMingguan.add(4500.0);
        labelsMingguan.add("Kamis"); totalsMingguan.add(8000.0);
        labelsMingguan.add("Jumat"); totalsMingguan.add(6000.0);

        // Data 3 Bulanan (contoh 3 minggu)
        labels3Bulanan.add("Minggu 1"); totals3Bulanan.add(20000.0);
        labels3Bulanan.add("Minggu 2"); totals3Bulanan.add(25000.0);
        labels3Bulanan.add("Minggu 3"); totals3Bulanan.add(18000.0);

        // Data Tahunan (contoh 4 bulan)
        labelsTahunan.add("Januari"); totalsTahunan.add(100000.0);
        labelsTahunan.add("Februari"); totalsTahunan.add(120000.0);
        labelsTahunan.add("Maret"); totalsTahunan.add(90000.0);
        labelsTahunan.add("April"); totalsTahunan.add(110000.0);

        // Tentukan jumlah maksimum data untuk sinkronisasi
        int maxSize = Math.max(labelsMingguan.size(), Math.max(labels3Bulanan.size(), labelsTahunan.size()));
        
        chart1.clear(); // Bersihkan data sebelumnya

        // Tambahkan data ke chart
        for (int i = 0; i < maxSize; i++) {
            String labelMingguan = (i < labelsMingguan.size()) ? labelsMingguan.get(i) : "";
            double totalMingguan = (i < totalsMingguan.size()) ? totalsMingguan.get(i) : 0.0;
            String label3Bulanan = (i < labels3Bulanan.size()) ? labels3Bulanan.get(i) : "";
            double total3Bulanan = (i < totals3Bulanan.size()) ? totals3Bulanan.get(i) : 0.0;
            String labelTahunan = (i < labelsTahunan.size()) ? labelsTahunan.get(i) : "";
            double totalTahunan = (i < totalsTahunan.size()) ? totalsTahunan.get(i) : 0.0;

            chart1.addData(new ModelChart(
                new String[]{labelMingguan, label3Bulanan, labelTahunan},
                new double[]{totalMingguan, total3Bulanan, totalTahunan}
            ));
        }

        chart1.start(); // Render ulang chart
    }


    private void initTableData() {
        // Data dummy untuk tabel
        String[] names = {"Budi", "Siti", "Andi", "Rina", "Tono", "Dewi", "Eko", "Fani", "Gita", "Hadi"};
        String[] addresses = {"Jl. Merdeka", "Jl. Sudirman", "Jl. Thamrin", "Jl. Gatot Subroto", "Jl. Diponegoro"};
        String[] houses = {"Tipe A", "Tipe B", "Tipe C", "Tipe D", "Tipe E"};
        String[] descriptions = {"Lunas", "Belum Lunas", "Proses", "Menunggu", "Selesai"};

        Random random = new Random();

        // Tambahkan 20 baris data acak
        for (int i = 0; i < 20; i++) {
            String idTransaksi = "TRX" + String.format("%03d", i + 1); // Contoh: TRX001, TRX002, ...
            String nama = names[random.nextInt(names.length)];
            String alamat = addresses[random.nextInt(addresses.length)];
            String rumah = houses[random.nextInt(houses.length)];
            String keterangan = descriptions[random.nextInt(descriptions.length)];

            table.addRow(new String[]{idTransaksi, nama, alamat, rumah, keterangan});
            table.fixTable(jScrollPane1);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        card3 = new com.raven.component.Card();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new com.raven.swing.Table1();
        chart1 = new chart.CurveLineChart();
        textFieldSuggestion1 = new jtextfield.TextFieldSuggestion();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(250, 250, 250));

        table.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(table);

        textFieldSuggestion1.setText("textFieldSuggestion1");

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
                                .addComponent(textFieldSuggestion1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chart1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(17, 17, 17))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chart1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldSuggestion1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.component.Card card3;
    private chart.CurveLineChart chart1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private com.raven.swing.Table1 table;
    private jtextfield.TextFieldSuggestion textFieldSuggestion1;
    // End of variables declaration//GEN-END:variables
}
