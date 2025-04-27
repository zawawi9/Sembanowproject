package com.raven.form;

import config.koneksi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import jtextfield.TextFieldSuggestion;


public class Form_restok extends javax.swing.JPanel {
    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();

    public Form_restok() {
        initComponents();
        keyListener();
    }

    private void keyListener() {
        sts.setEnabled(false);
        jmlh.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    spp.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
                    sts.requestFocusInWindow();
                }
                
            }
        });
        spp.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    ttl.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
                    jmlh.requestFocusInWindow();
                }
                
            }
        });
        ttl.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    barkot.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
                    spp.requestFocusInWindow();
                }
                
            }
        });
        // bagian bawah
        barkot.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                String idProduk = barkot.getText().trim();
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (!idProduk.isEmpty()) {
                        try {
                            String sql = "SELECT nama FROM produk WHERE id_produk = ?";
                            PreparedStatement stmt = cn.prepareStatement(sql);
                            stmt.setString(1, idProduk);
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {
                                pro.setText(rs.getString("nama"));
                                stk.requestFocusInWindow();
                            } else {
                                pro.setText("Produk tidak ditemukan");
                            }
                        } catch (SQLException ex) {
                            pro.setText("Error: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Error: ID tidak boleh kosong");
                    }
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    sts.requestFocusInWindow();
                }
                
            }
        });
        stk.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    stn.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    pro.requestFocusInWindow();
                }
                
            }
        });
        stn.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    hb.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    stk.requestFocusInWindow();
                }
                
            }
        });
        hb.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    h.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
                    barkot.requestFocusInWindow();
                }
                
            }
        });
        h.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    hh.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    hb.requestFocusInWindow();
                }
                
            }
        });
        hh.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    hhh.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    h.requestFocusInWindow();
                }
                
            }
        });
        hhh.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    expired.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    hh.requestFocusInWindow();
                }
                
            }
        });
        expired.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    DefaultTableModel model = (DefaultTableModel) productTable.getModel();
                    int currentRowCount = model.getRowCount();
                    String maxRowsText = jmlh.getText().trim();
                    int maxRows = Integer.parseInt(maxRowsText);
                    if (currentRowCount < maxRows) {
                        showData();
                        barkot.requestFocusInWindow();
                        clear();
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(null, 
                            "Jumlah baris telah mencapai batas maksimum (" + maxRows + ")!", 
                            "Peringatan", 
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    }
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                  hhh.requestFocusInWindow();
                }
                
            }
        });
    }
    
    public void clear() {
        barkot.setText("");
        pro.setText("");
        stk.setText("");
        stn.setText("");
        hb.setText("");
        h.setText("");
        hh.setText("");
        hhh.setText("");
        expired.setText("");
    }
    
    public void showData() {
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
    
        // Collect data from text fields
        String idText = barkot.getText();
        String produkText = pro.getText();
        String stokText = stk.getText();
        String satuanText = stn.getText();
        String hargabeliText = hb.getText();
        String harga1Text = h.getText();
        String harga2Text = hh.getText();
        String harga3Text = hhh.getText();
        String expiredText = expired.getText();
        String supplierText = spp.getText();
        
        model.addRow(new Object[]{
        idText, produkText, stokText, satuanText,hargabeliText, harga1Text, harga2Text, harga3Text, expiredText, supplierText});
    }
    
private void addproduk() {
    // Membuat field input pakai TextFieldSuggestion
    TextFieldSuggestion status = new TextFieldSuggestion();
    TextFieldSuggestion keterangan = new TextFieldSuggestion();
    TextFieldSuggestion total = new TextFieldSuggestion();
    TextFieldSuggestion id = new TextFieldSuggestion();
    TextFieldSuggestion produk = new TextFieldSuggestion();
    TextFieldSuggestion satuan = new TextFieldSuggestion();
    TextFieldSuggestion stok = new TextFieldSuggestion();
    TextFieldSuggestion qperdos = new TextFieldSuggestion();
    TextFieldSuggestion hargabeli = new TextFieldSuggestion();
    TextFieldSuggestion harga1 = new TextFieldSuggestion();
    TextFieldSuggestion harga2 = new TextFieldSuggestion();
    TextFieldSuggestion harga3 = new TextFieldSuggestion();
    TextFieldSuggestion exp = new TextFieldSuggestion();
    TextFieldSuggestion pcsh2 = new TextFieldSuggestion();
    TextFieldSuggestion pcsh3 = new TextFieldSuggestion();

    // Atur panjang TextFieldSuggestion
    Dimension fieldSize = new Dimension(200, 35); // Lebar diperbesar menjadi 200 piksel
    status.setPreferredSize(fieldSize);
    keterangan.setPreferredSize(fieldSize);
    total.setPreferredSize(fieldSize);
    id.setPreferredSize(fieldSize);
    produk.setPreferredSize(fieldSize);
    satuan.setPreferredSize(fieldSize);
    stok.setPreferredSize(fieldSize);
    qperdos.setPreferredSize(fieldSize);
    hargabeli.setPreferredSize(fieldSize);
    harga1.setPreferredSize(fieldSize);
    harga2.setPreferredSize(fieldSize);
    harga3.setPreferredSize(fieldSize);
    exp.setPreferredSize(fieldSize);
    pcsh2.setPreferredSize(fieldSize);
    pcsh3.setPreferredSize(fieldSize);

    JLabel l1 = new JLabel("Status:");
    JLabel l2 = new JLabel("Supplier:");
    JLabel l3 = new JLabel("Total:");
    JLabel l4 = new JLabel("ID:");
    JLabel l5 = new JLabel("Produk:");
    JLabel l6 = new JLabel("Satuan:");
    JLabel l7 = new JLabel("Stok:");
    JLabel l8 = new JLabel("Q/Dos:");
    JLabel l9 = new JLabel("Harga Beli:");
    JLabel l10 = new JLabel("Harga1:");
    JLabel l11 = new JLabel("Harga2:");
    JLabel l12 = new JLabel("Harga3:");
    JLabel l13 = new JLabel("Exp (yyyy-MM-dd):");
    JLabel lh2 = new JLabel("h2:");
    JLabel lh3 = new JLabel("h3:");

    // Panel utama dengan GridLayout 3x5 (3 baris, 5 kolom, gap 5)
    JPanel mainPanel = new JPanel(new GridLayout(3, 5, 5, 5));

    // Tambahkan pasangan label dan field ke mainPanel dalam grid 3x5
    mainPanel.add(createInputPanel(l1, status));
    mainPanel.add(createInputPanel(l2, keterangan));
    mainPanel.add(createInputPanel(l3, total));
    mainPanel.add(createInputPanel(l4, id));
    mainPanel.add(createInputPanel(l5, produk));

    mainPanel.add(createInputPanel(l6, satuan));
    mainPanel.add(createInputPanel(l7, stok));
    mainPanel.add(createInputPanel(l8, qperdos));
    mainPanel.add(createInputPanel(l9, hargabeli));
    mainPanel.add(createInputPanel(l10, harga1));

    mainPanel.add(createInputPanel(l11, harga2));
    mainPanel.add(createInputPanel(l12, harga3));
    mainPanel.add(createInputPanel(l13, exp));
    mainPanel.add(createInputPanel(lh2, pcsh2));
    mainPanel.add(createInputPanel(lh3, pcsh3));

    // Tambahkan KeyListener untuk navigasi dengan Enter
    KeyListener enterKeyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                e.consume(); // Cegah Enter memicu OK
                Component current = (Component) e.getSource();
                current.transferFocus(); // Pindah ke field berikutnya
            }
        }
    };

    // Terapkan KeyListener ke setiap field
    status.addKeyListener(enterKeyListener);
    status.setText("add item");
    keterangan.addKeyListener(enterKeyListener);
    total.addKeyListener(enterKeyListener);
    id.addKeyListener(enterKeyListener);
    produk.addKeyListener(enterKeyListener);
    satuan.addKeyListener(enterKeyListener);
    stok.addKeyListener(enterKeyListener);
    qperdos.addKeyListener(enterKeyListener);
    hargabeli.addKeyListener(enterKeyListener);
    harga1.addKeyListener(enterKeyListener);
    harga2.addKeyListener(enterKeyListener);
    harga3.addKeyListener(enterKeyListener);
    exp.addKeyListener(enterKeyListener);
    pcsh2.addKeyListener(enterKeyListener);
    pcsh3.addKeyListener(enterKeyListener);

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
    JDialog dialog = optionPane.createDialog(this, "Masukkan Data");

    // Pastikan semua komponen di dalam dialog juga putih
    setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);

    // Show dialog
    dialog.setVisible(true);

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
    String valStatus = status.getText().trim();
    String valKeterangan = keterangan.getText().trim();
    String valTotal = total.getText().trim();
    String valId = id.getText().trim();
    String valProduk = produk.getText().trim();
    String valSatuan = satuan.getText().trim();
    String valStok = stok.getText().trim();
    String valQperdos = qperdos.getText().trim();
    String valHargaBeli = hargabeli.getText().trim();
    String valHarga1 = harga1.getText().trim();
    String valHarga2 = harga2.getText().trim();
    String valHarga3 = harga3.getText().trim();
    String valExp = exp.getText().trim();
    String valPcsh2 = pcsh2.getText().trim();
    String valPcsh3 = pcsh3.getText().trim();

    // Lanjutkan dengan penyimpanan data
    try {
        // Ambil input untuk penyimpanan
        long valTotalNum = Long.parseLong(valTotal.isEmpty() ? "0" : valTotal); // Use long for bigint
        int valStokNum = Integer.parseInt(valStok.isEmpty() ? "0" : valStok);
        int valQperdosNum = Integer.parseInt(valQperdos.isEmpty() ? "0" : valQperdos);
        int valHargaBeliNum = Integer.parseInt(valHargaBeli.isEmpty() ? "0" : valHargaBeli);
        int valHarga1Num = Integer.parseInt(valHarga1.isEmpty() ? "0" : valHarga1);
        int valHarga2Num = Integer.parseInt(valHarga2.isEmpty() ? "0" : valHarga2);
        int valHarga3Num = Integer.parseInt(valHarga3.isEmpty() ? "0" : valHarga3);
        int valPcsh2Num = valPcsh2.isEmpty() ? 0 : Integer.parseInt(valPcsh2);
        int valPcsh3Num = valPcsh3.isEmpty() ? 0 : Integer.parseInt(valPcsh3);

        // Validate required fields and enum values
        if (valStatus.isEmpty() || valKeterangan.isEmpty() || valProduk.isEmpty() || valExp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Status, Keterangan, Produk, and Exp are required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate satuan
        if (!valSatuan.equalsIgnoreCase("dos") && !valSatuan.equalsIgnoreCase("pcs")) {
            JOptionPane.showMessageDialog(this, "Satuan must be 'dos' or 'pcs'.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Parse exp_date into java.sql.Date (assuming format "yyyy-MM-dd")
        java.sql.Date expDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = dateFormat.parse(valExp);
            expDate = new java.sql.Date(parsedDate.getTime());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format for Exp. Use 'yyyy-MM-dd'.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable autocommit to start a transaction
        cn.setAutoCommit(false);

        // Step 1: Insert into pengeluaran table
        String pengeluaranSql = "INSERT INTO pengeluaran (status, keterangan, total, tanggal) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = cn.prepareStatement(pengeluaranSql)) {
            pstmt.setString(1, valStatus);
            pstmt.setString(2, valKeterangan);
            pstmt.setLong(3, valTotalNum); // Use setLong for bigint
            pstmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis())); // Set current timestamp
            pstmt.executeUpdate();
        }

        // Step 2: Handle id_supplier (check if supplier exists, insert if not)
        String supplierName = valKeterangan;
        String idSupplier = null;
        String checkSupplierSql = "SELECT id_supplier FROM supplier WHERE nama = ?";
        try (PreparedStatement pstmt = cn.prepareStatement(checkSupplierSql)) {
            pstmt.setString(1, supplierName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                idSupplier = rs.getString("id_supplier");
            }
        }

        if (idSupplier == null) {
            String insertSupplierSql = "INSERT INTO supplier (nama) VALUES (?)";
            try (PreparedStatement pstmt = cn.prepareStatement(insertSupplierSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, supplierName);
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    idSupplier = String.valueOf(rs.getInt(1));
                } else {
                    throw new SQLException("Failed to retrieve new id_supplier after insert.");
                }
            }
        }

        // Step 3: Insert into produk table (dynamically handle id_produk)
        boolean idProvided = valId != null && !valId.isEmpty();
        String produkSql;
        if (idProvided) {
            produkSql = "INSERT INTO produk (id_produk, nama, harga1, harga2, harga3, harga_beli, pcs_per_dos, id_supplier) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            produkSql = "INSERT INTO produk (nama, harga1, harga2, harga3, harga_beli, pcs_per_dos, id_supplier) VALUES (?, ?, ?, ?, ?, ?, ?)";
        }

        String generatedIdProduk;
        try (PreparedStatement pstmt = cn.prepareStatement(produkSql, Statement.RETURN_GENERATED_KEYS)) {
            int paramIndex = 1;
            if (idProvided) {
                pstmt.setInt(paramIndex++, Integer.parseInt(valId)); // id_produk
            }
            pstmt.setString(paramIndex++, valProduk); // nama
            pstmt.setInt(paramIndex++, valHarga1Num); // harga1
            pstmt.setInt(paramIndex++, valHarga2Num); // harga2
            pstmt.setInt(paramIndex++, valHarga3Num); // harga3
            pstmt.setInt(paramIndex++, valHargaBeliNum); // harga_beli
            pstmt.setInt(paramIndex++, valQperdosNum); // pcs_per_dos
            pstmt.setInt(paramIndex++, Integer.parseInt(idSupplier)); // id_supplier
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                generatedIdProduk = String.valueOf(rs.getInt(1));
            } else {
                throw new SQLException("Failed to retrieve generated id_produk after insert.");
            }
        }

        String finalIdProduk = idProvided ? valId : generatedIdProduk;

        // Step 4: Insert into exp table
        String insertExpSql = "INSERT INTO exp (id_produk, exp_date, quantity_dos, quantity_pcs) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = cn.prepareStatement(insertExpSql)) {
            int quantityDos = 0;
            int quantityPcs = 0;
            if ("dos".equalsIgnoreCase(valSatuan)) {
                quantityDos = valStokNum;
            } else if ("pcs".equalsIgnoreCase(valSatuan)) {
                quantityPcs = valStokNum;
            }

            pstmt.setInt(1, Integer.parseInt(finalIdProduk));
            pstmt.setDate(2, expDate);
            pstmt.setInt(3, quantityDos);
            pstmt.setInt(4, quantityPcs);
            pstmt.executeUpdate();
        }

        // Step 5: Insert into diskon table
        String insertDiskonSql = "INSERT INTO diskon (id_produk, min_pcs, tipe_harga) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = cn.prepareStatement(insertDiskonSql)) {
            // Handle h2 discount
            if (valPcsh2Num > 0) {
                pstmt.setInt(1, Integer.parseInt(finalIdProduk));
                pstmt.setInt(2, valPcsh2Num);
                pstmt.setString(3, "h2"); // Use fixed value since lh2 text is inconsistent
                pstmt.executeUpdate();
            }

            // Handle h3 discount
            if (valPcsh3Num > 0) {
                pstmt.setInt(1, Integer.parseInt(finalIdProduk));
                pstmt.setInt(2, valPcsh3Num);
                pstmt.setString(3, "h3"); // Use fixed value since lh3 text is inconsistent
                pstmt.executeUpdate();
            }
        }

        cn.commit(); // Commit transaction
        JOptionPane.showMessageDialog(this, "Data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException | NumberFormatException ex) {
        try {
            if (cn != null) cn.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    } finally {
        try {
            if (cn != null) {
                cn.setAutoCommit(true);
            }
        } catch (SQLException finallyEx) {
            finallyEx.printStackTrace();
        }
    }
}

/**
 * Membuat panel untuk pasangan label dan input, dengan label di atas input.
 * @param label JLabel untuk label.
 * @param input Komponen input (TextFieldSuggestion).
 * @return JPanel yang berisi label dan input.
 */
private JPanel createInputPanel(JLabel label, TextFieldSuggestion input) {
    JPanel panel = new JPanel(new BorderLayout(0, 2)); // Gap 2 piksel antara label dan input
    panel.add(label, BorderLayout.NORTH);
    panel.add(input, BorderLayout.CENTER);
    return panel;
}

// Method untuk mengatur background secara rekursif
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

        card3 = new com.raven.component.Card();
        jLabel7 = new javax.swing.JLabel();
        pro = new jtextfield.TextFieldSuggestion();
        stk = new jtextfield.TextFieldSuggestion();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        hb = new jtextfield.TextFieldSuggestion();
        h = new jtextfield.TextFieldSuggestion();
        jLabel10 = new javax.swing.JLabel();
        hh = new jtextfield.TextFieldSuggestion();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        hhh = new jtextfield.TextFieldSuggestion();
        jLabel13 = new javax.swing.JLabel();
        stn = new jtextfield.TextFieldSuggestion();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        barkot = new jtextfield.TextFieldSuggestion();
        expired = new jtextfield.TextFieldSuggestion();
        jScrollPane1 = new javax.swing.JScrollPane();
        productTable = new com.raven.swing.Table1();
        jLabel18 = new javax.swing.JLabel();
        sts = new jtextfield.TextFieldSuggestion();
        jLabel19 = new javax.swing.JLabel();
        jmlh = new jtextfield.TextFieldSuggestion();
        jLabel20 = new javax.swing.JLabel();
        ttl = new jtextfield.TextFieldSuggestion();
        jLabel21 = new javax.swing.JLabel();
        spp = new jtextfield.TextFieldSuggestion();
        restock = new Custom.Custom_ButtonRounded();
        Tambah = new Custom.Custom_ButtonRounded();

        setBackground(new java.awt.Color(250, 250, 250));

        jLabel7.setText("Nama Produk");

        pro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proActionPerformed(evt);
            }
        });

        stk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stkActionPerformed(evt);
            }
        });

        jLabel8.setText("Jumlah");

        jLabel9.setText("Harga beli");

        hb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbActionPerformed(evt);
            }
        });

        h.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hActionPerformed(evt);
            }
        });

        jLabel10.setText("Harga 1");

        hh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hhActionPerformed(evt);
            }
        });

        jLabel11.setText("Harga 2");

        jLabel12.setText("Harga 3");

        hhh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hhhActionPerformed(evt);
            }
        });

        jLabel13.setText("Satuan");

        stn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stnActionPerformed(evt);
            }
        });

        jLabel14.setText("Expired Date");

        jLabel15.setText("ID");

        barkot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barkotActionPerformed(evt);
            }
        });

        expired.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expiredActionPerformed(evt);
            }
        });

        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "nama", "jumlah", "satuan", "harga beli", "harga 1", "harga 2", "harga 3", "expired"
            }
        ));
        jScrollPane1.setViewportView(productTable);

        jLabel18.setText("Status");

        sts.setText("restok");
        sts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stsActionPerformed(evt);
            }
        });

        jLabel19.setText("Jumlah produk");

        jmlh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmlhActionPerformed(evt);
            }
        });

        jLabel20.setText("Total");

        ttl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ttlActionPerformed(evt);
            }
        });

        jLabel21.setText("supplier");

        spp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sppActionPerformed(evt);
            }
        });

        restock.setText("Restock");
        restock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restockActionPerformed(evt);
            }
        });

        Tambah.setText("Tambah");
        Tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TambahActionPerformed(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sts, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jmlh, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spp, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ttl, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(stk, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .addComponent(pro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(barkot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(stn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(hb, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(h, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                                    .addComponent(hh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(hhh, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(expired, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(restock, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19)
                            .addComponent(jLabel21)
                            .addComponent(jLabel20))
                        .addGap(42, 42, 42))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ttl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jmlh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addContainerGap())
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(barkot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(hb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(pro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel11))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(stk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(hh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel12))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(stn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(hhh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14)
                                .addGap(12, 12, 12)
                                .addComponent(expired, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 72, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(restock, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void proActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_proActionPerformed

    private void stkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stkActionPerformed

    private void hbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hbActionPerformed

    private void hActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hActionPerformed

    private void hhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hhActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hhActionPerformed

    private void hhhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hhhActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hhhActionPerformed

    private void stnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stnActionPerformed

    private void barkotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barkotActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_barkotActionPerformed

    private void expiredActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expiredActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_expiredActionPerformed

    private void stsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stsActionPerformed

    private void jmlhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmlhActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jmlhActionPerformed

    private void ttlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ttlActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ttlActionPerformed

    private void sppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sppActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sppActionPerformed

    private void restockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restockActionPerformed
        try {
            cn.setAutoCommit(false); // Start transaction

            String pengeluaranSql = "INSERT INTO pengeluaran (status, jumlah, keterangan, total) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = cn.prepareStatement(pengeluaranSql)) {
                pstmt.setString(1, sts.getText());
                pstmt.setInt(2, Integer.parseInt(jmlh.getText()));
                pstmt.setString(3, spp.getText());
                pstmt.setDouble(4, Double.parseDouble(ttl.getText()));
                pstmt.executeUpdate();
            }

            // Step 2: Get id_supplier from supplier name
            String supplierName = spp.getText();
            String idSupplier = null;
            String getSupplierSql = "SELECT id_supplier FROM supplier WHERE nama = ?";
            try (PreparedStatement pstmt = cn.prepareStatement(getSupplierSql)) {
                pstmt.setString(1, supplierName);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    idSupplier = rs.getString("id_supplier");
                } else {
                    throw new SQLException("Supplier not found: " + supplierName);
                }
            }

            // Step 3: Update produk table (based on id_produk)
            String produkSql = "UPDATE produk SET nama = ?, harga_beli = ?, harga1 = ?, harga2 = ?, harga3 = ?, id_supplier = ? WHERE id_produk = ?";
            try (PreparedStatement pstmt = cn.prepareStatement(produkSql)) {
                for (int i = 0; i < productTable.getRowCount(); i++) {
                    String idProduk = productTable.getValueAt(i, 0).toString(); // ID (id_produk)
                    pstmt.setString(1, productTable.getValueAt(i, 1).toString()); // nama
                    pstmt.setDouble(2, Double.parseDouble(productTable.getValueAt(i, 4).toString())); // harga_beli
                    pstmt.setDouble(3, Double.parseDouble(productTable.getValueAt(i, 5).toString())); // harga1
                    pstmt.setDouble(4, Double.parseDouble(productTable.getValueAt(i, 6).toString())); // harga2
                    pstmt.setDouble(5, Double.parseDouble(productTable.getValueAt(i, 7).toString())); // harga3
                    pstmt.setString(6, idSupplier); // id_supplier
                    pstmt.setString(7, idProduk); // id_produk
                    pstmt.executeUpdate();
                }
            }

            // Step 4: Insert/Update into exp table (using satuan, jumlah, expired from productTable)
            String checkExpSql = "SELECT quantity_dos, quantity_pcs FROM exp WHERE id_produk = ? AND exp_date = ?";
            String updateExpSql = "UPDATE exp SET quantity_dos = ?, quantity_pcs = ? WHERE id_produk = ? AND exp_date = ?";
            String insertExpSql = "INSERT INTO exp (id_produk, exp_date, quantity_dos, quantity_pcs) VALUES (?, ?, ?, ?)";

            for (int i = 0; i < productTable.getRowCount(); i++) {
                String idProduk = productTable.getValueAt(i, 0).toString(); // ID (id_produk)
                String expDate = productTable.getValueAt(i, 8).toString(); // expired (exp_date, format YYYY/MM/DD)
                int jumlah = Integer.parseInt(productTable.getValueAt(i, 2).toString()); // jumlah
                String satuan = productTable.getValueAt(i, 3).toString(); // satuan

                // Calculate quantity_dos and quantity_pcs based on satuan
                int quantityDos = 0;
                int quantityPcs = 0;
                if ("dos".equalsIgnoreCase(satuan)) {
                    quantityDos = jumlah;
                } else if ("pcs".equalsIgnoreCase(satuan)) {
                    quantityPcs = jumlah;
                } else {
                    throw new IllegalArgumentException("Invalid satuan value: " + satuan);
                }

                // Check if record exists in exp table
                boolean recordExists = false;
                int existingDos = 0, existingPcs = 0;
                try (PreparedStatement pstmt = cn.prepareStatement(checkExpSql)) {
                    pstmt.setString(1, idProduk);
                    pstmt.setString(2, expDate);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        recordExists = true;
                        existingDos = rs.getInt("quantity_dos");
                        existingPcs = rs.getInt("quantity_pcs");
                    }
                }

                if (recordExists) {
                    // Update existing record
                    try (PreparedStatement pstmt = cn.prepareStatement(updateExpSql)) {
                        pstmt.setInt(1, existingDos + quantityDos);
                        pstmt.setInt(2, existingPcs + quantityPcs);
                        pstmt.setString(3, idProduk);
                        pstmt.setString(4, expDate);
                        pstmt.executeUpdate();
                    }
                } else {
                    // Insert new record
                    try (PreparedStatement pstmt = cn.prepareStatement(insertExpSql)) {
                        pstmt.setString(1, idProduk);
                        pstmt.setString(2, expDate);
                        pstmt.setInt(3, quantityDos);
                        pstmt.setInt(4, quantityPcs);
                        pstmt.executeUpdate();
                    }
                }
            }

            cn.commit(); // Commit transaction
            JOptionPane.showMessageDialog(this, "Data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            DefaultTableModel model = (DefaultTableModel) productTable.getModel();
            model.setRowCount(0);
            jmlh.setText("");
            ttl.setText("");
            spp.setText("");
            jmlh.requestFocusInWindow();

        } catch (SQLException | NumberFormatException ex) {
            try {
                if (cn != null) cn.rollback(); // Rollback on error
            } catch (SQLException e) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                if (cn != null) {
                    cn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_restockActionPerformed

    private void TambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahActionPerformed
    addproduk();
    }//GEN-LAST:event_TambahActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Custom.Custom_ButtonRounded Tambah;
    private jtextfield.TextFieldSuggestion barkot;
    private com.raven.component.Card card3;
    private jtextfield.TextFieldSuggestion expired;
    private jtextfield.TextFieldSuggestion h;
    private jtextfield.TextFieldSuggestion hb;
    private jtextfield.TextFieldSuggestion hh;
    private jtextfield.TextFieldSuggestion hhh;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private jtextfield.TextFieldSuggestion jmlh;
    private jtextfield.TextFieldSuggestion pro;
    private com.raven.swing.Table1 productTable;
    private Custom.Custom_ButtonRounded restock;
    private jtextfield.TextFieldSuggestion spp;
    private jtextfield.TextFieldSuggestion stk;
    private jtextfield.TextFieldSuggestion stn;
    private jtextfield.TextFieldSuggestion sts;
    private jtextfield.TextFieldSuggestion ttl;
    // End of variables declaration//GEN-END:variables
}
