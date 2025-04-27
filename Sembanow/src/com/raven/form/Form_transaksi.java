package com.raven.form;

import config.koneksi;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;

public class Form_transaksi extends javax.swing.JPanel {
    
    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();

    public Form_transaksi() {
        initComponents();
        keyListener();
        setupTableModel();
        // Isi jtxKasir dengan username saat inisialisasi
        jtxkasir.setText(data.getUsername());
        // Set default pelanggan (opsional, bisa dihapus jika tidak diperlukan)
        jtxPelanggan.setText("Umum");
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
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    if (table.getRowCount() > 0) {
                        table.requestFocusInWindow(); // Pindahkan fokus ke tabel
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
                    jtxSatuan.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
                    jtxId.requestFocusInWindow();
                }else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    jtxId.requestFocusInWindow();
                    clearTextFields();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    if (table.getRowCount() > 0) {
                        table.requestFocusInWindow(); // Pindahkan fokus ke tabel
                        table.setRowSelectionInterval(0, 0);
                    }
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
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
                    jtxJumlah.requestFocusInWindow();
                }else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    jtxId.requestFocusInWindow();
                    clearTextFields();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                    if (table.getRowCount() > 0) {
                        table.requestFocusInWindow(); // Pindahkan fokus ke tabel
                        table.setRowSelectionInterval(0, 0);
                    }
                }
            }
        });

        jtxBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    calculateKembalian(); // Panggil calculateKembalian saat Enter ditekan
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
                    saveTransaction(); // Simpan transaksi saat Enter ditekan
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
                        // Hapus baris dari model
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
                        String id = model.getValueAt(selectedRow, 0).toString(); // ID Produk
                        String nama = model.getValueAt(selectedRow, 1).toString(); // Nama
                        String jumlah = model.getValueAt(selectedRow, 2).toString(); // Jumlah
                        String satuan = model.getValueAt(selectedRow, 3).toString(); // Satuan
                        String tipeHarga = model.getValueAt(selectedRow, 4).toString(); // Tipe Harga
                        String hargaSatuan = model.getValueAt(selectedRow, 5).toString().replace(".", ""); // Harga Satuan

                        // Isi data ke JTextField
                        jtxId.setText(id);
                        jtxNama.setText(nama);
                        jtxJumlah.setText(jumlah);
                        jtxSatuan.setText(satuan);
                        jtxDiscount.setText(tipeHarga);
                        jtxHarga.setText(hargaSatuan);
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
        model.addColumn("Jumlah");
        model.addColumn("Satuan");
        model.addColumn("Discount");
        model.addColumn("Harga");
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
            Object[] data = {     // NO
                idProduk,         // ID Produk
                nama,             // Nama
                jumlah,           // Jumlah
                satuan,           // Satuan
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
    
    // Method untuk mencari id_pelanggan berdasarkan nama pelanggan
    private int getIdPelanggan(String namaPelanggan) {
        try {
            String sql = "SELECT id_pelanggan FROM pelanggan WHERE nama = ?";
            PreparedStatement stmt = cn.prepareStatement(sql);
            stmt.setString(1, namaPelanggan);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_pelanggan");
            } else {
                // Jika pelanggan tidak ditemukan, masukkan sebagai pelanggan baru
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
        return -1; // Return -1 jika gagal
    }

    // Method untuk mencari id_karyawan berdasarkan username
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
        return -1; // Return -1 jika gagal
    }

    // Method untuk menyimpan data transaksi ke database
    private void saveTransaction() {
        try {
            // Validasi tabel tidak kosong
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Tabel transaksi kosong!");
                return;
            }

            // Ambil data dari form
            String namaPelanggan = jtxPelanggan.getText();
            String usernameKasir = jtxkasir.getText();
            String totalStr = Ltotal.getText().replace("TOTAL: Rp ", "").replace(".", "");
            String bayarStr = jtxBayar.getText().replace(".", "");
            String kembalianStr = jtxKembalian.getText().replace(".", "");

            // Validasi input
            if (namaPelanggan.isEmpty() || usernameKasir.isEmpty() || totalStr.isEmpty() || 
                bayarStr.isEmpty() || kembalianStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field transaksi harus diisi!");
                return;
            }

            double totalValue = Double.parseDouble(totalStr);
            double bayarValue = Double.parseDouble(bayarStr);
            double kembalianValue = Double.parseDouble(kembalianStr);

            // Mulai transaksi database
            cn.setAutoCommit(false);

            // Cari id_pelanggan dan id_karyawan
            int idPelanggan = getIdPelanggan(namaPelanggan);
            int idKaryawan = getIdKaryawan(usernameKasir);
            if (idPelanggan == -1 || idKaryawan == -1) {
                cn.rollback();
                JOptionPane.showMessageDialog(null, "Gagal mendapatkan ID pelanggan atau karyawan!");
                return;
            }

            // Simpan ke tabel penjualan
            String sqlPenjualan = "INSERT INTO penjualan (id_pelanggan, tanggal, total_keseluruhan, bayar, kembalian, id_karyawan) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmtPenjualan = cn.prepareStatement(sqlPenjualan, Statement.RETURN_GENERATED_KEYS);
            stmtPenjualan.setInt(1, idPelanggan);
            stmtPenjualan.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmtPenjualan.setDouble(3, totalValue);
            stmtPenjualan.setDouble(4, bayarValue);
            stmtPenjualan.setDouble(5, kembalianValue);
            stmtPenjualan.setInt(6, idKaryawan);
            stmtPenjualan.executeUpdate();

            // Ambil id_penjualan yang baru dihasilkan
            ResultSet generatedKeys = stmtPenjualan.getGeneratedKeys();
            int idPenjualan = -1;
            if (generatedKeys.next()) {
                idPenjualan = generatedKeys.getInt(1);
            } else {
                cn.rollback();
                JOptionPane.showMessageDialog(null, "Gagal mendapatkan ID penjualan!");
                return;
            }

            // Simpan detail barang ke tabel transaksi
            String sqlTransaksi = "INSERT INTO transaksi (id_penjualan, id_produk, jumlah_produk, satuan, harga_satuan, total, tipe_harga) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmtTransaksi = cn.prepareStatement(sqlTransaksi);
            for (int i = 0; i < model.getRowCount(); i++) {
                int idProduk = Integer.parseInt(model.getValueAt(i, 0).toString());
                int jumlahProduk = Integer.parseInt(model.getValueAt(i, 2).toString());
                String satuan = model.getValueAt(i, 3).toString();
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

            // Commit transaksi
            cn.commit();
            JOptionPane.showMessageDialog(null, "Transaksi berhasil disimpan!");

            // Bersihkan form setelah transaksi selesai
            model.setRowCount(0); // Kosongkan tabel
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
        jtxkasir = new jtextfield.TextFieldSuggestion();
        kasir = new javax.swing.JLabel();

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

        jtxPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxPelangganActionPerformed(evt);
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
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(96, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
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

    private void jtxkasirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxkasirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxkasirActionPerformed


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
    private jtextfield.TextFieldSuggestion jtxkasir;
    private javax.swing.JLabel kasir;
    private com.raven.swing.Table1 table;
    // End of variables declaration//GEN-END:variables
}
