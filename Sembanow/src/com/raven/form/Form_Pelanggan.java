/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.form;

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
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import jtextfield.ComboBoxSuggestion;
import jtextfield.TextFieldSuggestion;

/**
 *
 * @author Fitrah
 */
public class Form_Pelanggan extends javax.swing.JPanel {

    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();
    private final DecimalFormat df;
    
    public Form_Pelanggan() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); 
        df = new DecimalFormat("#,###", symbols);
        initComponents();
        table();
        setupTableListener();}

public void table() {
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Membuat tabel tidak dapat diedit
        }
    };
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("ID Pelanggan");
    model.addColumn("Nama");
    model.addColumn("Nomor Telepon");
    model.addColumn("Alamat");
    model.addColumn("Tipe Harga");

    try {
        String sql = "SELECT " +
                     "    id_pelanggan, " +
                     "    nama, " +
                     "    alamat, " +
                     "    no_hp, " +
                     "    tipe_harga " +
                     "FROM " +
                     "    pelanggan " +
                "WHERE " +
                     "    nama != 'umum' " +
                     "ORDER BY " +
                     "    id_pelanggan ASC";
        PreparedStatement stmt = cn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String idPelanggan = rs.getString("id_pelanggan");
            String nama = rs.getString("nama");
            String no_hp = rs.getString("no_hp");
            String alamat = rs.getString("alamat");
            String tipeHarga = rs.getString("tipe_harga");
            model.addRow(new Object[]{
                idPelanggan,
                nama,
                no_hp,
                alamat,
                tipeHarga
            });
        }

        table1.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {100, 150,200, 200, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Sesuaikan tabel dengan scroll pane
        table1.fixTable(jScrollPane1);

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel: " + e.getMessage());
    }
}

private void searchPelanggan() {
    String searchText = pencarian.getText().trim();

    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Membuat tabel tidak dapat diedit
        }
    };
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("ID Pelanggan");
    model.addColumn("Nama");
    model.addColumn("Nomor Telepon");
    model.addColumn("Alamat");
    model.addColumn("Tipe Harga");

    try {
        // Query untuk mencari data berdasarkan id_pelanggan atau nama
        String sql = "SELECT " +
                     "    id_pelanggan, " +
                     "    nama, " +
                     "    alamat, " +
                     "    no_hp, " +
                     "    tipe_harga " +
                     "FROM " +
                     "    pelanggan " +
                     "WHERE " +
                     "    id_pelanggan LIKE ? OR nama LIKE ? " +
                     "ORDER BY " +
                     "    id_pelanggan ASC";
        PreparedStatement stmt = cn.prepareStatement(sql);
        String searchPattern = "%" + searchText + "%";
        stmt.setString(1, searchPattern); // Untuk id_pelanggan
        stmt.setString(2, searchPattern); // Untuk nama
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String idPelanggan = rs.getString("id_pelanggan");
            String nama = rs.getString("nama");
            String no_hp = rs.getString("no_hp");
            String alamat = rs.getString("alamat");
            String tipeHarga = rs.getString("tipe_harga");
            model.addRow(new Object[]{
                idPelanggan,
                nama,
                no_hp,
                alamat,
                tipeHarga
            });
        }

        table1.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {100, 150,200, 200, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Sesuaikan tabel dengan scroll pane
        table1.fixTable(jScrollPane1);

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saat mencari data: " + e.getMessage());
    }
}

private void setupTableListener() {
    // Tambahkan KeyListener pada tabel untuk mendeteksi tombol Enter dan Delete
    table1.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            int selectedRow = table1.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Pilih baris terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                updatePelanggan(selectedRow);
            } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                // Konfirmasi sebelum menghapus
                int confirm = JOptionPane.showConfirmDialog(null, 
                    "Apakah Anda yakin ingin menghapus pelanggan ini?", 
                    "Konfirmasi Hapus", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    String idPelanggan = table1.getValueAt(selectedRow, 0).toString();
                    deletePelanggan(idPelanggan);
                }
            }
        }
    });

    // Pastikan tabel bisa menerima input keyboard
    table1.setFocusable(true);
    table1.requestFocusInWindow();
}

private void deletePelanggan(String idPelanggan) {
    try {
        // Disable autocommit untuk memulai transaksi
        cn.setAutoCommit(false);

        // Query untuk menghapus pelanggan berdasarkan id_pelanggan
        String sql = "DELETE FROM pelanggan WHERE id_pelanggan = ?";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            pstmt.setString(1, idPelanggan);
            pstmt.executeUpdate();
        }

        cn.commit(); // Commit transaksi
        JOptionPane.showMessageDialog(this, "Pelanggan berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

        // Refresh tabel setelah hapus
        table();

    } catch (SQLException ex) {
        try {
            if (cn != null) cn.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "Error menghapus data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

private void updatePelanggan(int selectedRow) {
    // Ambil data dari baris yang dipilih untuk mengisi field secara default
    String id = table1.getValueAt(selectedRow, 0).toString();
    String nama = table1.getValueAt(selectedRow, 1).toString();
    String no = table1.getValueAt(selectedRow, 2).toString();
    String alamat = table1.getValueAt(selectedRow, 3).toString();
    String tipeHarga = table1.getValueAt(selectedRow, 4).toString();

    // Membuat field input menggunakan TextFieldSuggestion
    TextFieldSuggestion idField = new TextFieldSuggestion();
    TextFieldSuggestion namaField = new TextFieldSuggestion();
    TextFieldSuggestion no_telp = new TextFieldSuggestion();
    TextFieldSuggestion alamatField = new TextFieldSuggestion();
    ComboBoxSuggestion<String> tipeHargaField = new ComboBoxSuggestion<>();

    // Atur panjang TextFieldSuggestion
    Dimension fieldSize = new Dimension(200, 35);
    idField.setPreferredSize(fieldSize);
    namaField.setPreferredSize(fieldSize);
    no_telp.setPreferredSize(fieldSize);
    alamatField.setPreferredSize(fieldSize);
    tipeHargaField.setPreferredSize(fieldSize);

    tipeHargaField.addItem("h2");
    tipeHargaField.addItem("h3");
    tipeHargaField.setSelectedItem(tipeHarga); // Set nilai default sesuai data dari tabel

    // Isi field dengan data dari baris yang dipilih
    idField.setText(id);
    namaField.setText(nama);
    no_telp.setText(no);
    alamatField.setText(alamat);

    // Label untuk setiap field
    JLabel l0 = new JLabel("ID Pelanggan:");
    JLabel l1 = new JLabel("Nama:");
    JLabel l2 = new JLabel("No Telepon:");
    JLabel l3 = new JLabel("Alamat:");
    JLabel l4 = new JLabel("Tipe Harga:");

    // Panel utama dengan GridLayout 4x1 (4 baris, 1 kolom, gap 5)
    JPanel mainPanel = new JPanel(new GridLayout(5, 1, 5, 5));

    // Tambahkan pasangan label dan field ke mainPanel dalam grid 4x1
    mainPanel.add(createInputPanel(l0, idField));
    mainPanel.add(createInputPanel(l1, namaField));
    mainPanel.add(createInputPanel(l2, no_telp));
    mainPanel.add(createInputPanel(l3, alamatField));
    mainPanel.add(createInputPanel(l4, tipeHargaField));

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
    idField.addKeyListener(enterKeyListener);
    namaField.addKeyListener(enterKeyListener);
    no_telp.addKeyListener(enterKeyListener);
    alamatField.addKeyListener(enterKeyListener);

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
    JDialog dialog = optionPane.createDialog(this, "Update Data Pelanggan");

    // Pastikan semua komponen di dalam dialog juga putih
    setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);

    // Show dialog
    dialog.setVisible(true);

    // Atur fokus ke field id setelah dialog muncul
    idField.requestFocusInWindow();

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
    String valId = idField.getText().trim();
    String valNama = namaField.getText().trim();
    String valnotelepon = no_telp.getText().trim();
    String valAlamat = alamatField.getText().trim();
    String valTipeHarga = (String) tipeHargaField.getSelectedItem();

    // Lanjutkan dengan penyimpanan data
    try {
        // Validasi input
        if (valId.isEmpty() || valNama.isEmpty() ||valnotelepon.isEmpty() || valAlamat.isEmpty() || valTipeHarga == null) {
            JOptionPane.showMessageDialog(this, "ID Pelanggan, Nama, No Telepon, Alamat, dan Tipe Harga wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable autocommit untuk memulai transaksi
        cn.setAutoCommit(false);

        // Query untuk update data di tabel pelanggan
        String sql = "UPDATE pelanggan SET nama = ?,no_hp = ?, alamat = ?, tipe_harga = ? WHERE id_pelanggan = ?";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            pstmt.setString(1, valNama);
            pstmt.setString(2, valnotelepon);
            pstmt.setString(3, valAlamat);
            pstmt.setString(4, valTipeHarga);
            pstmt.setString(5, valId);
            pstmt.executeUpdate();
        }

        cn.commit(); // Commit transaksi
        JOptionPane.showMessageDialog(this, "Data pelanggan berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

        // Refresh tabel setelah update
        table();

    } catch (SQLException ex) {
        try {
            if (cn != null) cn.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "Error memperbarui data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

private void addPelanggan() {
    TextFieldSuggestion id = new TextFieldSuggestion();
    TextFieldSuggestion nama = new TextFieldSuggestion();
    TextFieldSuggestion noHp = new TextFieldSuggestion();
    TextFieldSuggestion alamat = new TextFieldSuggestion();
    ComboBoxSuggestion<String> tipeHarga = new ComboBoxSuggestion<>();

    Dimension fieldSize = new Dimension(200, 35);
    id.setPreferredSize(fieldSize);
    nama.setPreferredSize(fieldSize);
    noHp.setPreferredSize(fieldSize);
    alamat.setPreferredSize(fieldSize);
    tipeHarga.setPreferredSize(fieldSize);

    tipeHarga.addItem("h1");
    tipeHarga.addItem("h2");
    tipeHarga.addItem("h3");
    tipeHarga.setSelectedIndex(-1);

    JLabel l0 = new JLabel("ID Pelanggan:");
    JLabel l1 = new JLabel("Nama:");
    JLabel l1b = new JLabel("No Telepon:");
    JLabel l2 = new JLabel("Alamat:");
    JLabel l3 = new JLabel("Tipe Harga:");

    JPanel mainPanel = new JPanel(new GridLayout(5, 1, 5, 5));
    mainPanel.add(createInputPanel(l0, id));
    mainPanel.add(createInputPanel(l1, nama));
    mainPanel.add(createInputPanel(l1b, noHp)); // No HP setelah nama
    mainPanel.add(createInputPanel(l2, alamat));
    mainPanel.add(createInputPanel(l3, tipeHarga));

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

    id.addKeyListener(enterKeyListener);
    nama.addKeyListener(enterKeyListener);
    noHp.addKeyListener(enterKeyListener);
    alamat.addKeyListener(enterKeyListener);

    JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
    dialogPanel.add(mainPanel, BorderLayout.CENTER);
    dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JOptionPane optionPane = new JOptionPane(
        dialogPanel,
        JOptionPane.PLAIN_MESSAGE,
        JOptionPane.OK_CANCEL_OPTION
    );

    JDialog dialog = optionPane.createDialog(this, "Masukkan Data Pelanggan");
    setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);
    dialog.setVisible(true);
    id.requestFocusInWindow();

    Object selectedValue = optionPane.getValue();
    if (selectedValue == null || (Integer) selectedValue != JOptionPane.OK_OPTION) {
        JOptionPane.showMessageDialog(this, "Operasi dibatalkan.");
        return;
    }

    String valId = id.getText().trim();
    String valNama = nama.getText().trim();
    String valNoHp = noHp.getText().trim();
    String valAlamat = alamat.getText().trim();
    String valTipeHarga = (String) tipeHarga.getSelectedItem();

    if (valId.isEmpty() || valNama.isEmpty() || valNoHp.isEmpty() || valAlamat.isEmpty() || valTipeHarga == null) {
        JOptionPane.showMessageDialog(this, "Semua field wajib diisi, termasuk No. HP.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        cn.setAutoCommit(false);
        String sql = "INSERT INTO pelanggan (id_pelanggan, nama, no_hp, alamat, tipe_harga) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            pstmt.setString(1, valId);
            pstmt.setString(2, valNama);
            pstmt.setString(3, valNoHp);
            pstmt.setString(4, valAlamat);
            pstmt.setString(5, valTipeHarga);
            pstmt.executeUpdate();
        }

        cn.commit();
        JOptionPane.showMessageDialog(this, "Data pelanggan berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        table();

    } catch (SQLException ex) {
        try {
            if (cn != null) cn.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "Error menyimpan data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    } finally {
        try {
            if (cn != null) cn.setAutoCommit(true);
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

public void table2() {
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    model.setRowCount(0);
    model.setColumnCount(0);

    model.addColumn("ID Pelanggan");
    model.addColumn("Nama");
    model.addColumn("No Telepon");         // Tambahan
    model.addColumn("Alamat");
    model.addColumn("Tipe Harga");
    model.addColumn("Total Transaksi");
    model.addColumn("Total Uang");

    try {
        String sql = "SELECT " +
                     "    p.id_pelanggan, " +
                     "    p.nama, " +
                     "    p.no_hp, " +
                     "    p.alamat, " +
                     "    p.tipe_harga, " +
                     "    COUNT(pj.id_penjualan) AS total_transaksi, " +
                     "    SUM(pj.total_keseluruhan) AS total_uang " +
                     "FROM " +
                     "    pelanggan p " +
                     "LEFT JOIN " +
                     "    penjualan pj ON p.id_pelanggan = pj.id_pelanggan " +
                     "WHERE " +
                     "    YEAR(pj.tanggal) = YEAR(CURDATE()) " +
                     "GROUP BY " +
                     "    p.id_pelanggan, p.nama, p.no_hp, p.alamat, p.tipe_harga " +
                     "ORDER BY " +
                     "    total_transaksi DESC";

        PreparedStatement stmt = cn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String idPelanggan = rs.getString("id_pelanggan");
            String nama = rs.getString("nama");
            String noHp = rs.getString("no_hp");
            String alamat = rs.getString("alamat");
            String tipeHarga = rs.getString("tipe_harga");
            int totalTransaksi = rs.getInt("total_transaksi");
            long totalUang = rs.getLong("total_uang");

            model.addRow(new Object[]{
                idPelanggan,
                nama,
                noHp,
                alamat,
                tipeHarga,
                totalTransaksi,
                df.format(totalUang)
            });
        }

        table1.setModel(model);

        int[] columnWidths = {100, 150, 120, 200, 100, 120, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        table1.fixTable(jScrollPane1);

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel: " + e.getMessage());
    }
}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new com.raven.swing.Table1();
        pencarian = new jtextfield.TextFieldSuggestion();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setBackground(new java.awt.Color(250, 250, 250));

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(table1);

        pencarian.setText("Search ");
        pencarian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pencarianActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel1.setText("Pelanggan");

        jButton1.setText("tambah");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("ranking");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2)
                            .addComponent(jButton1))))
                .addGap(52, 52, 52))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47)
                        .addComponent(jButton1)
                        .addGap(27, 27, 27)
                        .addComponent(jButton2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
         table2();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        addPelanggan();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void pencarianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pencarianActionPerformed
        searchPelanggan();
    }//GEN-LAST:event_pencarianActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private jtextfield.TextFieldSuggestion pencarian;
    private com.raven.swing.Table1 table1;
    // End of variables declaration//GEN-END:variables
}
