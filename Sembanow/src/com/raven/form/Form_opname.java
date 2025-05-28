package com.raven.form;

import config.koneksi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import jtextfield.ComboBoxSuggestion;
import jtextfield.TextFieldSuggestion;
import raven.dialog.LengkapiData;
import raven.dialog.Loading;

public class Form_opname extends javax.swing.JPanel {

    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();

    public Form_opname() {
        initComponents();
        setupListeners();
        comboboxTanggalOpname();
        Window window = SwingUtilities.getWindowAncestor(Form_opname.this);
                Loading muat = new Loading((java.awt.Frame) window, true);
        muat.setVisible(true);
    }

    private void setupListeners() {
        jtxId.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (!jtxId.isEnabled()) {
                    JOptionPane.showMessageDialog(null,
                            "Input dinonaktifkan.\nSilakan ubah pencarian menjadi 'Pilih Tanggal' terlebih dahulu.",
                            "Peringatan",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        jtxId.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                String idProduk = jtxId.getText().trim();
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    if (!idProduk.isEmpty()) {
                        try {
                            String sql = "CALL GetProductStock(?);";
                            PreparedStatement stmt = cn.prepareStatement(sql);
                            stmt.setString(1, idProduk);
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {
                                awaldos.setText(rs.getString("total_stok_dos"));
                                awalpcs.setText(rs.getString("total_stok_pcs"));
                                jtxNama.setText(rs.getString("nama"));
                                akhirdos.requestFocusInWindow();
                            } else {
                                jtxNama.setText("ID Produk tidak ditemukan");
                            }
                        } catch (SQLException ex) {
                            jtxNama.setText("Error: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Error: ID tidak boleh kosong");
                    }
                }
            }
        });

        akhirdos.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    akhirpcs.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    jtxId.requestFocusInWindow();
                }
            }
        });
        akhirpcs.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    selisih.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    akhirdos.requestFocusInWindow();
                }
            }
        });
        akhirpcs.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    kalkulasiSelisih();
                    selisih.requestFocusInWindow();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    akhirdos.requestFocusInWindow();
                }
            }
        });
        selisih.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    showData();
                    clear();
                    jtxId.requestFocus();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                    akhirpcs.requestFocusInWindow();
                }
            }
        });
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDate = (String) comboBox.getSelectedItem();
                if (selectedDate != null && !selectedDate.equals("Pilih Tanggal")) {
                    tampilDataBerdasarkanTanggal(selectedDate);
                    clear();
                    nonEnabled();
                }
            }
        });
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDate = (String) comboBox.getSelectedItem();
                if (selectedDate.equals("Pilih Tanggal")) {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.setRowCount(0);
                    setEnabled();
                }
            }
        });
        table.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    int selectedRow = table.getSelectedRow();
                    int colCatatan = 7;
                    int colId = 0;

                    if (selectedRow != -1) {
                        String catatanValue = table.getValueAt(selectedRow, colCatatan).toString();
                        if ("hilang".equalsIgnoreCase(catatanValue)) {
                            String idProduk = table.getValueAt(selectedRow, colId).toString();
                            String selectedDate = (String) comboBox.getSelectedItem();

                            if (selectedDate == null || selectedDate.equals("Pilih Tanggal")) {
                                JOptionPane.showMessageDialog(null, "Pilih tanggal opname terlebih dahulu.");
                                return;
                            }

                            int pilihan = JOptionPane.showConfirmDialog(
                                    null,
                                    "Barang dicatat 'hilang'. Ganti catatan jadi 'aman'?",
                                    "Konfirmasi",
                                    JOptionPane.YES_NO_OPTION
                            );

                            if (pilihan == JOptionPane.YES_OPTION) {
                                table.setValueAt("aman", selectedRow, colCatatan);
                                try {
                                    String update = "UPDATE opname SET catatan = 'aman' WHERE id_produk = ? AND tanggal_opname = ?";
                                    PreparedStatement ps = cn.prepareStatement(update);
                                    ps.setString(1, idProduk);
                                    ps.setString(2, selectedDate);
                                    int rowsAffected = ps.executeUpdate();

                                    if (rowsAffected > 0) {
                                        System.out.println("Data berhasil diupdate ke database.");
                                    } else {
                                        System.out.println("Tidak ada data yang diupdate.");
                                    }

                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Gagal update catatan: " + ex.getMessage());
                                }
                            }
                        }
                    }
                    e.consume();
                }
            }
        });

    }

    private void tampilDataBerdasarkanTanggal(String tanggal) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        String query = "SELECT * FROM opname WHERE tanggal_opname = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, tanggal);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String idProduk = rs.getString("id_produk");
                String nama = rs.getString("nama_produk");
                String awalD = String.valueOf(rs.getInt("stok_awal_dos"));
                String awalP = String.valueOf(rs.getInt("stok_awal_pcs"));
                String akhirD = String.valueOf(rs.getInt("stok_akhir_dos"));
                String akhirP = String.valueOf(rs.getInt("stok_akhir_pcs"));
                String selisih = rs.getString("selisih");
                String catatan = rs.getString("catatan");

                Object[] data = {idProduk, nama, awalD, awalP, akhirD, akhirP, selisih, catatan};
                model.addRow(data);
            }
            clear();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menampilkan data: " + e.getMessage());
        }
    }

    private void comboboxTanggalOpname() {
        try {
            st = cn.createStatement();
            rs = st.executeQuery("SELECT DISTINCT tanggal_opname FROM opname ORDER BY tanggal_opname DESC");

            if (comboBox == null) {
                System.out.println("JComboBox belum diinisialisasi!");
                return;
            }

            comboBox.removeAllItems();
            comboBox.addItem("Pilih Tanggal");

            while (rs.next()) {
                Date tanggal = rs.getDate("tanggal_opname");
                comboBox.addItem(tanggal.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void kalkulasiSelisih() {
        int id = Integer.parseInt(jtxId.getText().trim());
        int awalDosVal = Integer.parseInt(awaldos.getText().trim());
        int awalPcsVal = Integer.parseInt(awalpcs.getText().trim());
        int akhirDosVal = Integer.parseInt(akhirdos.getText().trim());
        int akhirPcsVal = Integer.parseInt(akhirpcs.getText().trim());

        int selisihDos = akhirDosVal - awalDosVal;
        int selisihPcs = akhirPcsVal - awalPcsVal;

        String selisihText = "";

        if (selisihDos != 0 && selisihPcs != 0) {
            selisihText = (selisihDos > 0 ? "+" : "") + selisihDos + " dos & "
                    + (selisihPcs > 0 ? "+" : "") + selisihPcs + " pcs";
        } else if (selisihDos != 0) {
            selisihText = (selisihDos > 0 ? "+" : "") + selisihDos + " dos";
        } else if (selisihPcs != 0) {
            selisihText = (selisihPcs > 0 ? "+" : "") + selisihPcs + " pcs";
        } else {
            selisihText = "pas";
        }

        selisih.setText(selisihText);
    }

    private void clear() {
        jtxId.setText("");
        jtxNama.setText("");
        awaldos.setText("");
        awalpcs.setText("");
        akhirdos.setText("");
        akhirpcs.setText("");
        selisih.setText("");
    }

    private void nonEnabled() {
        jtxId.setEnabled(false);
        jtxNama.setEnabled(false);
        awaldos.setEnabled(false);
        awalpcs.setEnabled(false);
        akhirdos.setEnabled(false);
        akhirpcs.setEnabled(false);
        selisih.setEnabled(false);
    }

    private void setEnabled() {
        jtxId.setEnabled(true);
        jtxNama.setEnabled(true);
        awaldos.setEnabled(true);
        awalpcs.setEnabled(true);
        akhirdos.setEnabled(true);
        akhirpcs.setEnabled(true);
        selisih.setEnabled(true);
    }

    public void showData() {
        try {

            String idProduk = jtxId.getText();
            String nama = jtxNama.getText();
            String awalD = awaldos.getText();
            String awalP = awalpcs.getText();
            String akhirD = akhirdos.getText();
            String akhirP = akhirpcs.getText();
            String sel = selisih.getText().trim();
            String catatan;

            if (sel.equals("pas")) {
                catatan = "aman";
            } else {
                if (sel.contains("-")) {
                    catatan = "hilang";
                } else {
                    catatan = "aman";
                }
            }

            if (idProduk.isEmpty() || nama.isEmpty() || awalD.isEmpty() || awalP.isEmpty()
                    || akhirD.isEmpty() || akhirP.isEmpty() || sel.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Semua field harus diisi!");
                return;
            }

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            Object[] data = {
                idProduk,
                nama,
                awalD,
                awalP,
                akhirD,
                akhirP,
                sel,
                catatan
            };
            model.addRow(data);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void saveOpname() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Tabel opname kosong, tidak ada data untuk disimpan!");
            return;
        }

        String sqlOpname = "INSERT INTO opname (nama_produk, id_produk, stok_awal_dos, stok_awal_pcs, stok_akhir_dos, stok_akhir_pcs, selisih, catatan) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmtOpname = cn.prepareStatement(sqlOpname)) {
            cn.setAutoCommit(false); // Mulai transaksi manual

            for (int i = 0; i < model.getRowCount(); i++) {
                try {
                    String namaProduk = model.getValueAt(i, 1).toString();
                    int idProduk = Integer.parseInt(model.getValueAt(i, 0).toString());
                    int awalDos = Integer.parseInt(model.getValueAt(i, 2).toString());
                    int awalPcs = Integer.parseInt(model.getValueAt(i, 3).toString());
                    int akhirDos = Integer.parseInt(model.getValueAt(i, 4).toString());
                    int akhirPcs = Integer.parseInt(model.getValueAt(i, 5).toString());
                    String selisih = model.getValueAt(i, 6).toString();
                    String catatan = model.getValueAt(i, 7).toString();

                    stmtOpname.setString(1, namaProduk);
                    stmtOpname.setInt(2, idProduk);
                    stmtOpname.setInt(3, awalDos);
                    stmtOpname.setInt(4, awalPcs);
                    stmtOpname.setInt(5, akhirDos);
                    stmtOpname.setInt(6, akhirPcs);
                    stmtOpname.setString(7, selisih);
                    stmtOpname.setString(8, catatan);

                    stmtOpname.addBatch();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Data tidak valid pada baris " + (i + 1) + ": " + ex.getMessage());
                    cn.rollback(); // Batalkan transaksi jika ada kesalahan
                    return;
                }
            }

            stmtOpname.executeBatch();
            cn.commit();
            JOptionPane.showMessageDialog(null, "Data opname berhasil disimpan!");
            model.setRowCount(0); // Kosongkan tabel

        } catch (SQLException e) {
            try {
                cn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menyimpan data opname: " + e.getMessage());
        }
    }

    private void kelolaStok() {
        TextFieldSuggestion jtxId = new TextFieldSuggestion();
        TextFieldSuggestion jtxNama = new TextFieldSuggestion();
        ComboBoxSuggestion satuan = new ComboBoxSuggestion();
        TextFieldSuggestion jtxJumlah = new TextFieldSuggestion();
        ComboBoxSuggestion menu = new ComboBoxSuggestion();
        ComboBoxSuggestion comboBoxTanggalExp = new ComboBoxSuggestion();

        Dimension fieldSize = new Dimension(200, 35);
        jtxId.setPreferredSize(fieldSize);
        jtxNama.setPreferredSize(fieldSize);
        satuan.setPreferredSize(fieldSize);
        jtxJumlah.setPreferredSize(fieldSize);
        menu.setPreferredSize(fieldSize);
        comboBoxTanggalExp.setPreferredSize(fieldSize);

        JLabel lId = new JLabel("ID:");
        JLabel lNama = new JLabel("Nama:");
        JLabel lSatuan = new JLabel("Satuan:");
        JLabel lJumlah = new JLabel("Jumlah:");
        JLabel lMenu = new JLabel("Operasi:");
        JLabel lTanggalExp = new JLabel("Tanggal Exp:");

        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 2, 2));
        mainPanel.add(createInputPanel(lId, jtxId));
        mainPanel.add(createInputPanel(lNama, jtxNama));
        mainPanel.add(createInputPanel(lSatuan, satuan));
        mainPanel.add(createInputPanel(lJumlah, jtxJumlah));
        mainPanel.add(createInputPanel(lMenu, menu));
        mainPanel.add(createInputPanel(lTanggalExp, comboBoxTanggalExp));

        jtxId.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    e.consume();
                    String idProduk = jtxId.getText().trim();
                    if (idProduk.isEmpty()) {
                        Window window = SwingUtilities.getWindowAncestor(Form_opname.this);
                        LengkapiData barang = new LengkapiData((Frame)window, true);
                barang.setVisible(true);
                        return;
                    }
                    try {
                        PreparedStatement stmt = cn.prepareStatement("SELECT nama, satuan FROM produk WHERE id_produk = ?");
                        stmt.setString(1, idProduk);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            jtxNama.setText(rs.getString("nama"));
                            satuan.removeAllItems();
                            satuan.addItem("pcs");
                            String satuanProduk = rs.getString("satuan");
                            if (satuanProduk != null && !satuanProduk.trim().isEmpty()) {
                                satuan.addItem(satuanProduk);
                            }
                        } else {
                            jtxNama.setText("Produk tidak ditemukan");
                        }
                        comboBoxTanggalExp.removeAllItems();
                        PreparedStatement stmtExp = cn.prepareStatement("SELECT exp_date FROM exp WHERE id_produk = ? ORDER BY exp_date");
                        stmtExp.setString(1, idProduk);
                        ResultSet rsExp = stmtExp.executeQuery();
                        while (rsExp.next()) {
                            comboBoxTanggalExp.addItem(rsExp.getDate("exp_date").toString());
                        }
                        jtxJumlah.requestFocusInWindow(); // Move focus to Jumlah field
                    } catch (SQLException ex) {
                        jtxNama.setText("Error");
                        ex.printStackTrace();
                    }
                }
            }
        });

        menu.addItem("tambah");
        menu.addItem("kurangi");

        JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
        dialogPanel.add(mainPanel, BorderLayout.CENTER);
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JOptionPane optionPane = new JOptionPane(
                dialogPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        ) {
            @Override
            public void selectInitialValue() {
                jtxId.requestFocusInWindow();
            }
        };

        JDialog dialog = optionPane.createDialog(this, "Edit Produk");
        setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);
        dialog.setVisible(true);

        Object selectedValue = optionPane.getValue();
        if (selectedValue == null || (Integer) selectedValue != JOptionPane.OK_OPTION) {
            
            return;
        }

        String idProduk = jtxId.getText().trim();
        String valSatuan = satuan.getSelectedItem() != null ? satuan.getSelectedItem().toString().trim() : "";
        String valJumlah = jtxJumlah.getText().trim();
        String valMenu = menu.getSelectedItem() != null ? menu.getSelectedItem().toString().trim() : "";
        String valTanggalExp = comboBoxTanggalExp.getSelectedItem() != null ? comboBoxTanggalExp.getSelectedItem().toString().trim() : "";

        try {
            if (idProduk.isEmpty() || valSatuan.isEmpty() || valJumlah.isEmpty() || valMenu.isEmpty() || valTanggalExp.isEmpty()) {
                Window window = SwingUtilities.getWindowAncestor(Form_opname.this);
                        LengkapiData barang = new LengkapiData((Frame)window, true);
                barang.setVisible(true);
                return;
            }

            int quantity = Integer.parseInt(valJumlah);
            cn.setAutoCommit(false);

            String updateSql;
            if ("pcs".equalsIgnoreCase(valSatuan)) {
                updateSql = "UPDATE exp SET quantity_pcs = quantity_pcs + ? WHERE id_produk = ? AND exp_date = ?";
                if ("kurangi".equalsIgnoreCase(valMenu)) {
                    quantity = -quantity;
                }
            } else {
                updateSql = "UPDATE exp SET quantity_dos = quantity_dos + ? WHERE id_produk = ? AND exp_date = ?";
                if ("kurangi".equalsIgnoreCase(valMenu)) {
                    quantity = -quantity;
                }
            }

            try (PreparedStatement pstmt = cn.prepareStatement(updateSql)) {
                pstmt.setInt(1, quantity);
                pstmt.setString(2, idProduk);
                pstmt.setString(3, valTanggalExp);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    JOptionPane.showMessageDialog(this, "Data tidak ditemukan untuk update.", "Error", JOptionPane.ERROR_MESSAGE);
                    cn.rollback();
                    return;
                }
            }

            cn.commit();
            Window window = SwingUtilities.getWindowAncestor(Form_opname.this);
                        Loading barang = new Loading((Frame)window, true);
                barang.setVisible(true);

        } catch (SQLException | NumberFormatException ex) {
            try {
                if (cn != null) {
                    cn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Error menyimpan data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        card3 = new com.raven.component.Card();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new com.raven.swing.Table1();
        jtxNama = new jtextfield.TextFieldSuggestion();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        akhirdos = new jtextfield.TextFieldSuggestion();
        akhirpcs = new jtextfield.TextFieldSuggestion();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        selisih = new jtextfield.TextFieldSuggestion();
        jLabel10 = new javax.swing.JLabel();
        jtxId = new jtextfield.TextFieldSuggestion();
        jLabel12 = new javax.swing.JLabel();
        awaldos = new jtextfield.TextFieldSuggestion();
        jLabel13 = new javax.swing.JLabel();
        awalpcs = new jtextfield.TextFieldSuggestion();
        jLabel14 = new javax.swing.JLabel();
        comboBox = new jtextfield.ComboBoxSuggestion();
        tambah = new Custom.Custom_ButtonRounded();
        perbaikan = new Custom.Custom_ButtonRounded();

        setBackground(new java.awt.Color(250, 250, 250));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Produk", "awal dos", "awal pcs", "akhir dos", "akhir pcs", "selisih", "catatan"
            }
        ));
        jScrollPane1.setViewportView(table);

        jtxNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxNamaActionPerformed(evt);
            }
        });

        jLabel6.setText("produk");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel9.setText("-> Produk");

        jLabel7.setText("akhir dos");

        akhirdos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                akhirdosActionPerformed(evt);
            }
        });

        akhirpcs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                akhirpcsActionPerformed(evt);
            }
        });

        jLabel8.setText("akhir pcs");

        jLabel11.setText("selisih");

        selisih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selisihActionPerformed(evt);
            }
        });

        jLabel10.setText("id");

        jtxId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxIdActionPerformed(evt);
            }
        });

        jLabel12.setText("awal dos");

        awaldos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                awaldosActionPerformed(evt);
            }
        });

        jLabel13.setText("awal pcs");

        awalpcs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                awalpcsActionPerformed(evt);
            }
        });

        jLabel14.setText("seacrh");

        tambah.setText("Tambah");
        tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahActionPerformed(evt);
            }
        });

        perbaikan.setText("Perbaikan");
        perbaikan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                perbaikanActionPerformed(evt);
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
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(109, 109, 109))
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(awalpcs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(awaldos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jtxId, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(109, 109, 109))
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(akhirpcs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                .addComponent(akhirdos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jtxNama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selisih, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(perbaikan, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(akhirdos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(akhirpcs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(awaldos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(awalpcs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selisih, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(perbaikan, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jtxNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxNamaActionPerformed

    private void akhirdosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_akhirdosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_akhirdosActionPerformed

    private void akhirpcsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_akhirpcsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_akhirpcsActionPerformed

    private void jtxIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxIdActionPerformed

    private void awaldosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_awaldosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_awaldosActionPerformed

    private void awalpcsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_awalpcsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_awalpcsActionPerformed

    private void selisihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selisihActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selisihActionPerformed

    private void perbaikanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_perbaikanActionPerformed
        saveOpname();
    }//GEN-LAST:event_perbaikanActionPerformed

    private void tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahActionPerformed
        kelolaStok();
    }//GEN-LAST:event_tambahActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jtextfield.TextFieldSuggestion akhirdos;
    private jtextfield.TextFieldSuggestion akhirpcs;
    private jtextfield.TextFieldSuggestion awaldos;
    private jtextfield.TextFieldSuggestion awalpcs;
    private com.raven.component.Card card3;
    private jtextfield.ComboBoxSuggestion comboBox;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private jtextfield.TextFieldSuggestion jtxId;
    private jtextfield.TextFieldSuggestion jtxNama;
    private Custom.Custom_ButtonRounded perbaikan;
    private jtextfield.TextFieldSuggestion selisih;
    private com.raven.swing.Table1 table;
    private Custom.Custom_ButtonRounded tambah;
    // End of variables declaration//GEN-END:variables
}
