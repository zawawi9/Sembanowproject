package com.raven.form;

import config.koneksi;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Form_transaksi extends javax.swing.JPanel {
    
    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();

        public Form_transaksi() {
        initComponents();
        keyListener();
        setupTableModel();
    }
    
    public void call() {
        try {
            // Ambil input dari JTextField
            int id = Integer.parseInt(jtxId.getText());
            String stn = jtxSatuan.getText();
            int jml = Integer.parseInt(jtxJumlah.getText());

            // Siapkan CallableStatement untuk memanggil stored procedure
            String sql = "{CALL HitungHargaTransaksi(?, ?, ?)}";
            CallableStatement stmt = cn.prepareCall(sql);

            // Set parameter
            stmt.setInt(1, id);       // p_id_produk
            stmt.setString(2, stn);   // p_satuan
            stmt.setInt(3, jml);      // p_jumlah_produk

            // Eksekusi stored procedure
            boolean hasResult = stmt.execute();

            // Ambil hasil dari ResultSet
            if (hasResult) {
                ResultSet rs = stmt.getResultSet();
                if (rs.next()) {
                    // Isi JTextField dengan hasil
                    jtxSatuan.setText(rs.getString("satuan"));
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
            // Tangani error dari stored procedure (misalnya stok tidak mencukupi)
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
        
    // Method untuk menambahkan KeyListener ke jtxJumlah
    private void keyListener() {
        jtxId.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                    String idProduk = jtxId.getText().trim();
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (!idProduk.isEmpty()) {
                        try {
                            String sql = "SELECT nama FROM produk WHERE id_produk = ?";
                            PreparedStatement stmt = cn.prepareStatement(sql);
                            stmt.setString(1, idProduk);
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {
                                jtxNama.setText(rs.getString("nama"));
                                jtxJumlah.requestFocusInWindow();
                            } else {
                                jtxNama.setText("Produk tidak ditemukan");
                            }
                        } catch (SQLException ex) {
                            jtxNama.setText("Error: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Error: ID tidak boleh kosong");
                    }
                }else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    if (table.getRowCount() > 0) {
                    table.requestFocus();
                    }else {
                        JOptionPane.showMessageDialog(null, "Error: tabel kosong !!");
                    }
                }else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F12) {
                    if (table.getRowCount() > 0) {
                    jtxBayar.requestFocus();
                    }else {
                        JOptionPane.showMessageDialog(null, "Error: tabel kosong !!");
                    }
                }
            }
        });
        jtxJumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    jtxSatuan.requestFocusInWindow();
                }
            }
        });
        jtxSatuan.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    call();
                    showData(); // Panggil showData saat Enter ditekan
                    jtxId.requestFocusInWindow();
                }
            }
        });
        jtxBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    calculateKembalian(); // Panggil calculateKembalian saat Enter ditekan
                }
            }
        });
        table.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    jtxId.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(null, "Pilih baris yang ingin dihapus!");
                    }
                }
        });
        table.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F5) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        // Hapus baris dari model
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        model.removeRow(selectedRow);
                        updateTotalLabel();
                        clearTextFields();
                        jtxId.requestFocusInWindow();
                    } else {
                        JOptionPane.showMessageDialog(null, "Pilih baris yang ingin dihapus!");
                    }
                }
            }
        });
        table.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                // Pastikan perubahan seleksi sudah selesai dan ada baris yang dipilih
                if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    // Dapatkan baris yang dipilih
                    int selectedRow = table.getSelectedRow();

                    // Ambil data dari baris yang dipilih
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    String idProduk = model.getValueAt(selectedRow, 1).toString(); // ID Produk
                    String nama = model.getValueAt(selectedRow, 2).toString(); // Nama
                    String satuan = model.getValueAt(selectedRow, 3).toString(); // Satuan
                    String jumlah = model.getValueAt(selectedRow, 4).toString(); // Jumlah
                    String tipeHarga = model.getValueAt(selectedRow, 5).toString(); // Tipe Harga
                    String hargaSatuan = model.getValueAt(selectedRow, 6).toString().replace(".", ""); // Harga Satuan (hapus pemisah titik)

                    // Isi data ke JTextField
                    jtxId.setText(idProduk);
                    jtxNama.setText(nama);
                    jtxJumlah.setText(satuan);
                    jtxSatuan.setText(jumlah);
                    jtxDiscount.setText(tipeHarga);
                    jtxHarga.setText(hargaSatuan);
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
        model.addColumn("Tipe Harga");
        model.addColumn("Harga Satuan");
        model.addColumn("Total");

        table.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {50, 200, 100, 100, 100, 150, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Sesuaikan tabel dengan scroll pane (diasumsikan fixTable adalah method custom)
        table.fixTable(jScrollPane1);
    }
    public void showData() {
        try {
            // Format untuk pemisah ribuan dengan titik
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.'); // Pemisah ribuan menggunakan titik
            DecimalFormat df = new DecimalFormat("#,###", symbols);

            // Ambil data dari JTextField
            String idProduk = jtxId.getText();
            String nama = jtxNama.getText();
            String satuan = jtxSatuan.getText();
            String jumlah = jtxJumlah.getText();
            String tipeHarga = jtxDiscount.getText();
            String hargaSatuan = jtxHarga.getText();
            String total = jtxTotal.getText();

            // Validasi input
            if (idProduk.isEmpty() || nama.isEmpty() || satuan.isEmpty() || jumlah.isEmpty() || 
                tipeHarga.isEmpty() || hargaSatuan.isEmpty() || total.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field harus diisi!");
                return;
            }

            // Konversi hargaSatuan dan total ke angka untuk diformat
            double hargaSatuanValue = Double.parseDouble(hargaSatuan);
            double totalValue = Double.parseDouble(total);

            // Setup model tabel jika belum ada
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int rowNumber = 1;
            Object[] data = {
                rowNumber++,      // NO
                idProduk,         // ID Produk
                nama,             // Nama
                satuan,           // Satuan
                jumlah,           // Jumlah
                tipeHarga,        // Tipe Harga
                df.format(hargaSatuanValue), // Harga Satuan (diformat)
                df.format(totalValue)        // Total (diformat)
            };
            model.addRow(data);
            updateTotalLabel();

            // Optional: Bersihkan JTextField setelah data ditambahkan
            clearTextFields();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Harga Satuan dan Total harus berupa angka!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
    
        // Method untuk menghitung total dari kolom Total di tabel dan memperbarui Ltotal
    private void updateTotalLabel() {
        try {
            // Format untuk pemisah ribuan dengan titik
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.'); // Pemisah ribuan menggunakan titik
            DecimalFormat df = new DecimalFormat("#,###", symbols);

            // Ambil model tabel
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            // Hitung total dari kolom Total (indeks 7)
            double totalSum = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                // Ambil nilai dari kolom Total (indeks 7), hapus pemisah titik untuk parsing
                String totalStr = model.getValueAt(i, 6).toString().replace(".", "");
                double totalValue = Double.parseDouble(totalStr);
                totalSum += totalValue;
            }

            // Update label Ltotal dengan format "TOTAL: Rp [jumlah]"
            Ltotal.setText("TOTAL: Rp " + df.format(totalSum));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saat menghitung total: " + e.getMessage());
        }
    }

    // Method untuk membersihkan JTextField (opsional)
    private void clearTextFields() {
        jtxId.setText("");
        jtxNama.setText("");
        jtxSatuan.setText("");
        jtxJumlah.setText("");
        jtxDiscount.setText("");
        jtxHarga.setText("");
        jtxTotal.setText("");
    }
   

    // Method untuk menghitung kembalian
    private void calculateKembalian() {
        try {
            // Format untuk pemisah ribuan dengan titik
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.'); // Pemisah ribuan menggunakan titik
            DecimalFormat df = new DecimalFormat("#,###", symbols);

            // Ambil nilai total dari Ltotal (hapus "TOTAL: Rp" dan pemisah titik)
            String totalStr = Ltotal.getText().replace("TOTAL: Rp ", "").replace(".", "");
            if (totalStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Total transaksi belum ada!");
                return;
            }
            double totalValue = Double.parseDouble(totalStr);

            // Ambil nilai dari jtxBayar
            String bayarStr = jtxBayar.getText().replace(".", ""); // Hapus pemisah titik jika ada
            if (bayarStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Masukkan jumlah yang dibayar!");
                return;
            }
            double bayarValue = Double.parseDouble(bayarStr);

            // Validasi: Pastikan jumlah yang dibayar cukup
            if (bayarValue < totalValue) {
                JOptionPane.showMessageDialog(null, "Jumlah yang dibayar kurang! Total: Rp " + df.format(totalValue));
                jtxKembalian.setText("");
                return;
            }

            // Hitung kembalian
            double kembalianValue = bayarValue - totalValue;

            // Tampilkan kembalian di jtxKembalian dengan format pemisah ribuan
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
        jtxSatuan = new jtextfield.TextFieldSuggestion();
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
        jtxPelanggan = new jtextfield.TextFieldSuggestion();
        jLabel10 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(250, 250, 250));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
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

        jtxSatuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxSatuanActionPerformed(evt);
            }
        });

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

        jtxPelanggan.setText("Umum");
        jtxPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxPelangganActionPerformed(evt);
            }
        });

        jLabel10.setText("Pelanggan");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxId, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxNama, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                            .addComponent(jtxPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(96, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(Ltotal, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jtxBayar, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                                    .addComponent(jtxKembalian, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jScrollPane1))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Ltotal, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jtxJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxJumlahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxJumlahActionPerformed

    private void jtxDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxDiscountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxDiscountActionPerformed

    private void jtxSatuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxSatuanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxSatuanActionPerformed

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

    private void jtxPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxPelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxPelangganActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Ltotal;
    private com.raven.component.Card card3;
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
    private jtextfield.TextFieldSuggestion jtxPelanggan;
    private jtextfield.TextFieldSuggestion jtxSatuan;
    private jtextfield.TextFieldSuggestion jtxTotal;
    private com.raven.swing.Table1 table;
    // End of variables declaration//GEN-END:variables
}
