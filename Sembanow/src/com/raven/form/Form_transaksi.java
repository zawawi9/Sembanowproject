package com.raven.form;

import config.koneksi;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Form_transaksi extends javax.swing.JPanel {
    
    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();

    public Form_transaksi() {
        initComponents();
        keyListener();
        setupTableModel();
        jtxkasir.setText(data.getUsername());
        combobox();
    }
    
    private void combobox(){
        try {
            st = cn.createStatement();
            rs = st.executeQuery("SELECT nama FROM pelanggan WHERE nama IS NOT NULL AND id_pelanggan != 6");
            if (comboBox == null) {
                System.out.println("JComboBox belum diinisialisasi!");
                return;
            }
            comboBox.removeAllItems();
            comboBox.addItem("umum"); 
            while (rs.next()) {
                String namaPelanggan = rs.getString("nama");
                comboBox.addItem(namaPelanggan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void call() {
        try {
            int id = Integer.parseInt(jtxId.getText());
            String stn = satuan.getSelectedItem().toString();
            int jml = Integer.parseInt(jtxJumlah.getText());
            
            String sql = "{CALL HitungTransaksi(?, ?, ?)}";
            CallableStatement stmt = cn.prepareCall(sql);

            stmt.setInt(1, id);       
            stmt.setString(2, stn);  
            stmt.setInt(3, jml);     

            boolean hasResult = stmt.execute();

            if (hasResult) {
                ResultSet rs = stmt.getResultSet();
                if (rs.next()) {
                    jtxDiscount.setText(rs.getString("tipe_harga"));
                    jtxHarga.setText(String.valueOf(rs.getInt("harga_satuan")));
                    jtxTotal.setText(String.valueOf(rs.getInt("total")));
                } else {
                    JOptionPane.showMessageDialog(null, "Data tidak ditemukan!");
                }
                rs.close();
            } else {
                JOptionPane.showMessageDialog(null, "Procedure tidak mengembalikan hasil!");
            }

            stmt.close();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID dan Jumlah harus berupa angka!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
    
    private void keyListener() {
        jtxId.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                String idProduk = jtxId.getText().trim();
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (!idProduk.isEmpty()) {
                        try {
                            String sql = "SELECT nama,satuan FROM produk WHERE id_produk = ?";
                            PreparedStatement stmt = cn.prepareStatement(sql);
                            stmt.setString(1, idProduk);
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {
                            jtxNama.setText(rs.getString("nama"));
                            satuan.removeAllItems(); 
                            satuan.addItem("pcs");

                            String satuanProduk = rs.getString("satuan");
                            if (satuanProduk != null && !satuanProduk.trim().isEmpty()) {
                                if (!satuanProduk.equalsIgnoreCase("pcs")) {
                                    satuan.addItem(satuanProduk);
                                }
                            }

                            satuan.requestFocusInWindow();
                        } else {
                            jtxNama.setText("Produk tidak ditemukan");
                        }
                        } catch (SQLException ex) {
                            jtxNama.setText("Error: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    } else {
                        ProductSearchDialog dialog = new ProductSearchDialog((JFrame) SwingUtilities.getWindowAncestor(jtxId), cn);
                        dialog.setVisible(true);
                        String selectedId = dialog.getSelectedId();
                        if (!selectedId.isEmpty()) {
                            jtxId.setText(selectedId); // Set ID ke jtxId
                        }
                    }
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    if (table.getRowCount() > 0) {
                        table.requestFocusInWindow(); 
                        table.setRowSelectionInterval(0, 0);
                    }
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F12) {
                    if (table.getRowCount() > 0) {
                        jtxBayar.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error: tabel kosong !!");
                    }
                }else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    jtxId.requestFocusInWindow();
                    clearTextFields();
                }
            }
        });

        jtxJumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    call();
                    showData();
                    jtxId.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
                    jtxId.requestFocusInWindow();
                }else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    jtxId.requestFocusInWindow();
                    clearTextFields();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    if (table.getRowCount() > 0) {
                        table.requestFocusInWindow(); 
                        table.setRowSelectionInterval(0, 0);
                    }
                }
            }
        });

        satuan.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_ENTER:
                        jtxJumlah.requestFocusInWindow();
                        break;
                    case java.awt.event.KeyEvent.VK_LEFT:
                        jtxId.requestFocusInWindow();
                        break;
                    case java.awt.event.KeyEvent.VK_ESCAPE:
                        jtxId.requestFocusInWindow();
                        clearTextFields();
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                        if (table.getRowCount() > 0) {
                            table.requestFocusInWindow();
                            table.setRowSelectionInterval(0, 0);
                        }
                        break;
                }
            }
        });
        
        jtxBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    calculateKembalian();
                    jtxKembalian.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    jtxId.requestFocusInWindow();
                    clearTextFields();
                }
            }
        });

        jtxKembalian.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    saveTransaction(); 
                }else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    jtxBayar.requestFocusInWindow();
                }else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    jtxId.requestFocusInWindow();
                    clearTextFields();
                }
            }
        });

        table.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                    int selectedRow = table.getSelectedRow();
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F5) {
                    if (selectedRow >= 0) {
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        model.removeRow(selectedRow);
                        updateTotalLabel();
                        clearTextFields();
                        jtxId.requestFocusInWindow();
                    } else {
                        JOptionPane.showMessageDialog(null, "Pilih baris yang ingin dihapus!");
                    }
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    jtxId.requestFocusInWindow();
                    clearTextFields();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (selectedRow >= 0) {
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        String id = model.getValueAt(selectedRow, 0).toString();
                        String nama = model.getValueAt(selectedRow, 1).toString(); 
                        model.removeRow(selectedRow);

                        jtxId.setText(id);
                        jtxNama.setText(nama);
                        jtxId.requestFocusInWindow();
                        table.clearSelection();
                    }
                } 
            }
        });
    }
    
    private void setupTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Produk");
        model.addColumn("Nama");
        model.addColumn("Satuan");
        model.addColumn("Jumlah");
        model.addColumn("Discount");
        model.addColumn("Harga");
        model.addColumn("Total");

        table.setModel(model);
        int[] columnWidths = {50, 200, 100, 100, 100, 150, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        table.fixTable(jScrollPane1);
    }

    public void showData() {
    try {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); 
        DecimalFormat df = new DecimalFormat("#,###", symbols);

        String idProduk = jtxId.getText();
        String nama = jtxNama.getText();
        String csatuan = satuan.getSelectedItem().toString();
        String jumlah = jtxJumlah.getText();
        String tipeHarga = jtxDiscount.getText();
        String hargaSatuan = jtxHarga.getText();
        String total = jtxTotal.getText();

        if (idProduk.isEmpty() || nama.isEmpty() || csatuan.isEmpty() || jumlah.isEmpty() || 
            tipeHarga.isEmpty() || hargaSatuan.isEmpty() || total.isEmpty()) {
            clearTextFields();
            return;
        }

        double hargaSatuanValue = Double.parseDouble(hargaSatuan);
        double totalValue = Double.parseDouble(total);

        // Default nilai pcs_per_dos
        int pcsPerDos = 1;
        String sql = "SELECT pcs_per_dos FROM produk WHERE id_produk = ?";
        PreparedStatement pst = cn.prepareStatement(sql);
        pst.setString(1, idProduk);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            pcsPerDos = rs.getInt("pcs_per_dos");
        }

        // Kalau bukan pcs, berarti dianggap dos → harga dikalikan pcs_per_dos
        double hargaSatuanFinal = hargaSatuanValue;
        if (!"pcs".equalsIgnoreCase(csatuan)) {
            hargaSatuanFinal = hargaSatuanValue * pcsPerDos;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Object[] data = {
            idProduk,        
            nama,            
            csatuan,           
            jumlah,           
            tipeHarga,       
            df.format(hargaSatuanFinal), 
            df.format(totalValue)        
        };
        model.addRow(data);
        updateTotalLabel();
        clearTextFields();

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Harga Satuan dan Total harus berupa angka!");
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }
}

    
    private void updateTotalLabel() {
        try {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            DecimalFormat df = new DecimalFormat("#,###", symbols);

            DefaultTableModel model = (DefaultTableModel) table.getModel();

            double totalSum = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                String totalStr = model.getValueAt(i, 6).toString().replace(".", "");
                double totalValue = Double.parseDouble(totalStr);
                totalSum += totalValue;
            }

            Ltotal.setText("TOTAL: Rp " + df.format(totalSum));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saat menghitung total: " + e.getMessage());
        }
    }

    private void calculateKembalian() {
        try {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.'); 
            DecimalFormat df = new DecimalFormat("#,###", symbols);

            String totalStr = Ltotal.getText().replace("TOTAL: Rp ", "").replace(".", "");
            if (totalStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Total transaksi belum ada!");
                return;
            }
            double totalValue = Double.parseDouble(totalStr);

            String bayarStr = jtxBayar.getText().replace(".", "");
            if (bayarStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Masukkan jumlah yang dibayar!");
                return;
            }
            double bayarValue = Double.parseDouble(bayarStr);

            if (bayarValue < totalValue) {
                JOptionPane.showMessageDialog(null, "Jumlah yang dibayar kurang! Total: Rp " + df.format(totalValue));
                jtxKembalian.setText("");
                return;
            }
            
            double kembalianValue = bayarValue - totalValue;

            jtxKembalian.setText(df.format(kembalianValue));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Jumlah yang dibayar harus berupa angka!");
            jtxKembalian.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            jtxKembalian.setText("");
        }
    }
    
    private int getIdPelanggan(String namaPelanggan) {
        try {
            String sql = "SELECT id_pelanggan FROM pelanggan WHERE nama = ?";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setString(1, namaPelanggan);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_pelanggan");
            } else {
                sql = "INSERT INTO pelanggan (nama) VALUES (?)";
                PreparedStatement insertStmt = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                insertStmt.setString(1, namaPelanggan);
                insertStmt.executeUpdate();
                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error mencari/menambah pelanggan: " + e.getMessage());
        }
        return -1;
    }

    private int getIdKaryawan(String username) {
        try {
            String sql = "SELECT id_karyawan FROM karyawan WHERE username = ?";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_karyawan");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error mencari karyawan: " + e.getMessage());
        }
        return -1; 
    }

    private void saveTransaction() {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Tabel transaksi kosong!");
                return;
            }

            String namaPelanggan = comboBox.getSelectedItem().toString();
            String usernameKasir = jtxkasir.getText();
            String totalStr = Ltotal.getText().replace("TOTAL: Rp ", "").replace(".", "");
            String bayarStr = jtxBayar.getText().replace(".", "");
            String kembalianStr = jtxKembalian.getText().replace(".", "");

            if (namaPelanggan.isEmpty() || usernameKasir.isEmpty() || totalStr.isEmpty() || 
                bayarStr.isEmpty() || kembalianStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field transaksi harus diisi!");
                return;
            }

            double totalValue = Double.parseDouble(totalStr);
            double bayarValue = Double.parseDouble(bayarStr);
            double kembalianValue = Double.parseDouble(kembalianStr);

            cn.setAutoCommit(false);

            int idPelanggan = getIdPelanggan(namaPelanggan);
            int idKaryawan = getIdKaryawan(usernameKasir);
            if (idPelanggan == -1 || idKaryawan == -1) {
                cn.rollback();
                JOptionPane.showMessageDialog(null, "Gagal mendapatkan ID pelanggan atau karyawan!");
                return;
            }

            String sqlPenjualan = "INSERT INTO penjualan (id_pelanggan, tanggal, total_keseluruhan, bayar, kembalian, id_karyawan) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmtPenjualan = cn.prepareStatement(sqlPenjualan, Statement.RETURN_GENERATED_KEYS);
            stmtPenjualan.setInt(1, idPelanggan);
            stmtPenjualan.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmtPenjualan.setDouble(3, totalValue);
            stmtPenjualan.setDouble(4, bayarValue);
            stmtPenjualan.setDouble(5, kembalianValue);
            stmtPenjualan.setInt(6, idKaryawan);
            stmtPenjualan.executeUpdate();

            ResultSet generatedKeys = stmtPenjualan.getGeneratedKeys();
            int idPenjualan = -1;
            if (generatedKeys.next()) {
                idPenjualan = generatedKeys.getInt(1);
            } else {
                cn.rollback();
                JOptionPane.showMessageDialog(null, "Gagal mendapatkan ID penjualan!");
                return;
            }

            String sqlTransaksi = "INSERT INTO transaksi (id_penjualan, id_produk, jumlah_produk, satuan, harga_satuan, total, tipe_harga) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmtTransaksi = cn.prepareStatement(sqlTransaksi);
            for (int i = 0; i < model.getRowCount(); i++) {
                int idProduk = Integer.parseInt(model.getValueAt(i, 0).toString());
                String satuan = model.getValueAt(i, 2).toString();
                int jumlahProduk = Integer.parseInt(model.getValueAt(i, 3).toString());
                double hargaSatuan = Double.parseDouble(model.getValueAt(i, 5).toString().replace(".", ""));
                double total = Double.parseDouble(model.getValueAt(i, 6).toString().replace(".", ""));
                String tipeHarga = model.getValueAt(i, 4).toString();

                stmtTransaksi.setInt(1, idPenjualan);
                stmtTransaksi.setInt(2, idProduk);
                stmtTransaksi.setInt(3, jumlahProduk);
                stmtTransaksi.setString(4, satuan);
                stmtTransaksi.setDouble(5, hargaSatuan);
                stmtTransaksi.setDouble(6, total);
                stmtTransaksi.setString(7, tipeHarga);
                stmtTransaksi.addBatch();
            }
            stmtTransaksi.executeBatch();

            cn.commit();
            JOptionPane.showMessageDialog(null, "Transaksi berhasil disimpan!");

            model.setRowCount(0); 
            clearTextFields();
            jtxBayar.setText("");
            jtxKembalian.setText("");
            Ltotal.setText("TOTAL: Rp 0");
            jtxId.requestFocusInWindow();

        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Error menyimpan transaksi: " + e.getMessage());
        } finally {
            try {
                cn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void clearTextFields() {
        jtxId.setText("");
        jtxNama.setText("");
        satuan.removeAllItems();
        jtxJumlah.setText("");
        jtxDiscount.setText("");
        jtxHarga.setText("");
        jtxTotal.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        card3 = new com.raven.component.Card();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new com.raven.swing.Table1();
        jtxId = new jtextfield.TextFieldSuggestion();
        jtxJumlah = new jtextfield.TextFieldSuggestion();
        jtxDiscount = new jtextfield.TextFieldSuggestion();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jtxHarga = new jtextfield.TextFieldSuggestion();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtxTotal = new jtextfield.TextFieldSuggestion();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jtxNama = new jtextfield.TextFieldSuggestion();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Ltotal = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jtxBayar = new jtextfield.TextFieldSuggestion();
        jtxKembalian = new jtextfield.TextFieldSuggestion();
        jLabel10 = new javax.swing.JLabel();
        jtxkasir = new jtextfield.TextFieldSuggestion();
        kasir = new javax.swing.JLabel();
        comboBox = new jtextfield.ComboBoxSuggestion();
        satuan = new jtextfield.ComboBoxSuggestion();

        setBackground(new java.awt.Color(250, 250, 250));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        table.fixTable(jScrollPane1);
        jScrollPane1.setViewportView(table);

        jtxId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxIdActionPerformed(evt);
            }
        });

        jtxJumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxJumlahActionPerformed(evt);
            }
        });

        jtxDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxDiscountActionPerformed(evt);
            }
        });

        jLabel1.setText("ID");

        jLabel2.setText("Satuan");

        jLabel3.setText("Jumlah");

        jtxHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxHargaActionPerformed(evt);
            }
        });

        jLabel4.setText("Harga");

        jLabel5.setText("Discount");

        jtxTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxTotalActionPerformed(evt);
            }
        });

        jLabel6.setText("Total");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel7.setText("Kembalian :");

        jtxNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxNamaActionPerformed(evt);
            }
        });

        jLabel8.setText("Nama");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel9.setText("-> Transaksi");

        Ltotal.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        Ltotal.setText("Total : Rp");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel11.setText("Bayar           :");

        jtxBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxBayarActionPerformed(evt);
            }
        });

        jtxKembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxKembalianActionPerformed(evt);
            }
        });

        jLabel10.setText("Pelanggan");

        jtxkasir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxkasirActionPerformed(evt);
            }
        });

        kasir.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        kasir.setText("Kasir :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(Ltotal, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(kasir, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxkasir, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtxBayar, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                            .addComponent(jtxKembalian, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtxId, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtxNama, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(satuan, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 68, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel10)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(satuan, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Ltotal, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtxkasir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(kasir, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jtxJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxJumlahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxJumlahActionPerformed

    private void jtxDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxDiscountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxDiscountActionPerformed

    private void jtxHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxHargaActionPerformed

    private void jtxTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxTotalActionPerformed

    private void jtxBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxBayarActionPerformed

    private void jtxKembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxKembalianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxKembalianActionPerformed

    private void jtxNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxNamaActionPerformed

    private void jtxIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxIdActionPerformed

    private void jtxkasirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxkasirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxkasirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Ltotal;
    private com.raven.component.Card card3;
    private jtextfield.ComboBoxSuggestion comboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private jtextfield.TextFieldSuggestion jtxBayar;
    private jtextfield.TextFieldSuggestion jtxDiscount;
    private jtextfield.TextFieldSuggestion jtxHarga;
    private jtextfield.TextFieldSuggestion jtxId;
    private jtextfield.TextFieldSuggestion jtxJumlah;
    private jtextfield.TextFieldSuggestion jtxKembalian;
    private jtextfield.TextFieldSuggestion jtxNama;
    private jtextfield.TextFieldSuggestion jtxTotal;
    private jtextfield.TextFieldSuggestion jtxkasir;
    private javax.swing.JLabel kasir;
    private jtextfield.ComboBoxSuggestion satuan;
    private com.raven.swing.Table1 table;
    // End of variables declaration//GEN-END:variables
}
