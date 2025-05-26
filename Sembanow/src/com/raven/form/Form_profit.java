package com.raven.form;
import config.koneksi;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Form_profit extends javax.swing.JPanel {
    
    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();
    private final DecimalFormat df;

    public Form_profit() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); 
        df = new DecimalFormat("#,###", symbols);
        initComponents();
        clean();
        comboboxTanggal();
        keylistener();
    }

    public Form_profit(DecimalFormat df) {
        this.df = df;
    }
    private void keylistener(){
        comboBox2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String awal = (String) comboBox.getSelectedItem();
                String akhir = (String) comboBox2.getSelectedItem();
                if ((awal!= null && !awal.equals("Pilih Tanggal"))&&(awal!= null && !awal.equals("Pilih Tanggal"))) {
                    sortTable1(awal, akhir);
                    sortTable2(awal, akhir);
                    clean();
                }
            }
        });
    }
    
    private void comboboxTanggal() {
    try {
        st = cn.createStatement();

        // Gunakan LinkedHashSet untuk menyimpan nilai unik dan urutan tetap
        Set<String> bulanTahunAsc = new LinkedHashSet<>();
        Set<String> bulanTahunDesc = new LinkedHashSet<>();

        // Ambil data ASC
        ResultSet rsAsc = st.executeQuery("SELECT DISTINCT tanggal FROM penjualan ORDER BY tanggal ASC");
        while (rsAsc.next()) {
            Date tanggal = rsAsc.getDate("tanggal");
            String bulanTahun = new SimpleDateFormat("yyyy-MM").format(tanggal);
            bulanTahunAsc.add(bulanTahun);
        }

        // Ambil data DESC
        ResultSet rsDesc = st.executeQuery("SELECT DISTINCT tanggal FROM penjualan ORDER BY tanggal DESC");
        while (rsDesc.next()) {
            Date tanggal = rsDesc.getDate("tanggal");
            String bulanTahun = new SimpleDateFormat("yyyy-MM").format(tanggal);
            bulanTahunDesc.add(bulanTahun);
        }

        if (comboBox == null || comboBox2 == null) {
            System.out.println("JComboBox belum diinisialisasi!");
            return;
        }

        // Isi comboBox (ASC)
        comboBox.removeAllItems();
        comboBox.addItem("Pilih Bulan");
        for (String bt : bulanTahunAsc) {
            comboBox.addItem(bt);
        }

        // Isi comboBox2 (DESC)
        comboBox2.removeAllItems();
        comboBox2.addItem("Pilih Bulan");
        for (String bt : bulanTahunDesc) {
            comboBox2.addItem(bt);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    
    public void clean() {
        try {
            String inflowText = inflow.getText().replace(".", "").trim();
            String outflowText = outflow.getText().replace(".", "").trim();

            double inflowValue = Double.parseDouble(inflowText);
            double outflowValue = Double.parseDouble(outflowText);

            double totalValue = inflowValue - outflowValue;

            total.setText(df.format(totalValue));

        } catch (NumberFormatException e) {
            System.out.println("Input tidak valid: " + e.getMessage()); 
        }
    }
   
    public void sortTable1(String awal, String akhir) {
    DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    model.setRowCount(0);
    model.setColumnCount(0);

    model.addColumn("ID Transaksi");
    model.addColumn("ID Produk");
    model.addColumn("Nama Produk");
    model.addColumn("Jumlah");
    model.addColumn("Satuan");
    model.addColumn("Harga Satuan");
    model.addColumn("Harga Beli");
    model.addColumn("Total");
    model.addColumn("Tanggal");

    try {
        String sql = "SELECT p2.id_penjualan AS id, t.id_produk, pr.nama, t.jumlah_produk, t.satuan, " +
                     "t.harga_satuan, t.harga_beli, p2.tanggal, pr.pcs_per_dos " +
                     "FROM transaksi t " +
                     "JOIN produk pr ON t.id_produk = pr.id_produk " +
                     "JOIN penjualan p2 ON t.id_penjualan = p2.id_penjualan " +
                     "WHERE DATE_FORMAT(p2.tanggal, '%Y-%m') BETWEEN ? AND ? " +
                     "ORDER BY p2.tanggal DESC";

        PreparedStatement stmt = cn.prepareStatement(sql);
        stmt.setString(1, awal); // Format: yyyy-MM
        stmt.setString(2, akhir); // Format: yyyy-MM
        ResultSet rs = stmt.executeQuery();

        double inflowSum = 0;

        while (rs.next()) {
            int id = rs.getInt("id");
            int idProduk = rs.getInt("id_produk");
            String nama = rs.getString("nama");
            int jumlahProduk = rs.getInt("jumlah_produk");
            String satuan = rs.getString("satuan");
            double hargaSatuan = rs.getDouble("harga_satuan");
            double hargaBeli = rs.getDouble("harga_beli");
            int pcsPerDos = rs.getInt("pcs_per_dos");
            Date tanggal = rs.getDate("tanggal");

            double hargaBeliFinal = hargaBeli;
            if (!"pcs".equalsIgnoreCase(satuan)) {
                hargaBeliFinal = hargaBeli * pcsPerDos;
            }

            double total = (hargaSatuan - hargaBeliFinal) * jumlahProduk;
            inflowSum += total;

            model.addRow(new Object[]{
                id,
                idProduk,
                nama,
                jumlahProduk,
                satuan,
                df.format(hargaSatuan),
                df.format(hargaBeliFinal),
                df.format(total),
                tanggal
            });
        }

        table.setModel(model);
        inflow.setText(df.format(inflowSum));

        int[] columnWidths = {100, 100, 150, 100, 100, 100, 100, 100, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        table.fixTable(jScrollPane2);

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel: " + e.getMessage());
    }
}



    public void sortTable2(String awal, String akhir) {
    DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("ID Pengeluaran");
    model.addColumn("Status");
    model.addColumn("Keterangan");
    model.addColumn("Total");
    model.addColumn("Tanggal");

    try {
        String sql = "SELECT id_pengeluaran, status, keterangan, total, tanggal " +
                     "FROM pengeluaran " +
                     "WHERE status IN ('penggajian', 'lainnya') " +
                     "AND DATE_FORMAT(tanggal, '%Y-%m') BETWEEN ? AND ? " +
                     "ORDER BY tanggal DESC";

        PreparedStatement stmt = cn.prepareStatement(sql);
        stmt.setString(1, awal); // Format: yyyy-MM
        stmt.setString(2, akhir); // Format: yyyy-MM
        ResultSet rs = stmt.executeQuery();

        double outflowSum = 0;

        while (rs.next()) {
            int idPengeluaran = rs.getInt("id_pengeluaran");
            String status = rs.getString("status");
            String keterangan = rs.getString("keterangan");
            double total = rs.getDouble("total");
            Date tanggal = rs.getDate("tanggal");
            outflowSum += total;

            model.addRow(new Object[]{
                idPengeluaran,
                status,
                keterangan,
                df.format(total),
                tanggal
            });
        }

        table2.setModel(model);

        int[] columnWidths = {100, 150, 200, 150, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table2.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        table2.fixTable(jScrollPane1);
        outflow.setText(df.format(outflowSum));

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel: " + e.getMessage());
    }
}

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        card3 = new com.raven.component.Card();
        jScrollPane1 = new javax.swing.JScrollPane();
        table2 = new com.raven.swing.Table1();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table = new com.raven.swing.Table1();
        jLabel2 = new javax.swing.JLabel();
        total = new jtextfield.TextFieldSuggestion();
        outflow = new jtextfield.TextFieldSuggestion();
        inflow = new jtextfield.TextFieldSuggestion();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        comboBox = new jtextfield.ComboBoxSuggestion();
        comboBox2 = new jtextfield.ComboBoxSuggestion();

        setBackground(new java.awt.Color(250, 250, 250));

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
        jScrollPane1.setViewportView(table2);

        jLabel1.setText("akhir :");

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
        jScrollPane2.setViewportView(table);

        jLabel2.setText("awal :");

        total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalActionPerformed(evt);
            }
        });

        outflow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outflowActionPerformed(evt);
            }
        });

        inflow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inflowActionPerformed(evt);
            }
        });

        jLabel3.setText("in  :");

        jLabel4.setText("out :");

        jLabel5.setText("Clean :");

        comboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBox2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1139, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inflow, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(outflow, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1139, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outflow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inflow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalActionPerformed

    private void outflowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outflowActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_outflowActionPerformed

    private void inflowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inflowActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inflowActionPerformed

    private void comboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBox2ActionPerformed
        
    }//GEN-LAST:event_comboBox2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.component.Card card3;
    private jtextfield.ComboBoxSuggestion comboBox;
    private jtextfield.ComboBoxSuggestion comboBox2;
    private jtextfield.TextFieldSuggestion inflow;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private jtextfield.TextFieldSuggestion outflow;
    private com.raven.swing.Table1 table;
    private com.raven.swing.Table1 table2;
    private jtextfield.TextFieldSuggestion total;
    // End of variables declaration//GEN-END:variables
}
