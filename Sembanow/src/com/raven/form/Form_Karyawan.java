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
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import jtextfield.ComboBoxSuggestion;
import jtextfield.TextFieldSuggestion;


public class Form_Karyawan extends javax.swing.JPanel {

    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();
    private final DecimalFormat df;
    
    public Form_Karyawan() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); 
        df = new DecimalFormat("#,###", symbols);
        initComponents();
        table();
        updateTable2(null);
        setupTableListener();
        
    }

    public void table() {
    DefaultTableModel model = new DefaultTableModel();
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("ID");
    model.addColumn("Username");
    model.addColumn("No HP"); // Added no_hp column
    model.addColumn("Password");
    model.addColumn("NIK");
    model.addColumn("Status");
    model.addColumn("Gaji");

    try {
        String sql = "SELECT " +
                     "    id_karyawan, " +
                     "    username, " +
                     "    no_hp, " +
                     "    password, " +
                     "    nik, " +
                     "    status, " +
                     "    gaji " +
                     "FROM " +
                     "    karyawan";
        PreparedStatement stmt = cn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String idKaryawan = rs.getString("id_karyawan");
            String username = rs.getString("username");
            String noHp = rs.getString("no_hp"); // Added no_hp data
            String password = rs.getString("password");
            String nik = rs.getString("nik");
            String status = rs.getString("status");
            double gaji = rs.getDouble("gaji");
            model.addRow(new Object[]{
                idKaryawan,
                username,
                noHp, // Added no_hp to row
                password,
                nik,
                status,
                df.format(gaji)
            });
        }

        table1.setModel(model);

        int[] columnWidths = {100, 150, 150, 150, 150, 100, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        table1.fixTable(jScrollPane1);

        if (table1.getRowCount() > 0) {
            table1.setRowSelectionInterval(0, 0);
            String idKaryawan = table1.getValueAt(0, 0).toString();
            updateTable2(idKaryawan);
        }
        

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel: " + e.getMessage());
    }
}

    public void updateTable2(String idKaryawan) {
    DefaultTableModel model = new DefaultTableModel();
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("ID Karyawan");
    model.addColumn("Tanggal");
    model.addColumn("Check In");
    model.addColumn("Check Out");

    try {
        String sql = "SELECT id_karyawan, tanggal, check_in, check_out " +
                     "FROM absensi " +
                     "WHERE id_karyawan = ? " +
                     "ORDER BY tanggal ASC";
        PreparedStatement stmt = cn.prepareStatement(sql);
        stmt.setString(1, idKaryawan);
        ResultSet rs = stmt.executeQuery();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        while (rs.next()) {
            String checkInTime = timeFormat.format(rs.getTimestamp("check_in"));
            String checkOutTime = timeFormat.format(rs.getTimestamp("check_out"));
            model.addRow(new Object[]{
                rs.getString("id_karyawan"),
                rs.getDate("tanggal"),
                checkInTime,
                checkOutTime
            });
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel 2: " + e.getMessage());
    }

    table2.setModel(model);

    int[] columnWidths = {100, 150, 100, 100};
    for (int i = 0; i < columnWidths.length; i++) {
        table2.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
    }

    table2.fixTable(jScrollPane2);
}
    
    private void searchKaryawan() {
    String searchText = pencarian.getText().trim();

    DefaultTableModel model = new DefaultTableModel();
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("ID Karyawan");
    model.addColumn("Username");
    model.addColumn("No HP"); // Added no_hp column
    model.addColumn("Password");
    model.addColumn("NIK");
    model.addColumn("Status");
    model.addColumn("Gaji");

    try {
        // Query untuk mencari data berdasarkan id_karyawan atau username
        String sql = "SELECT " +
                     "    id_karyawan, " +
                     "    username, " +
                     "    no_hp, " + // Added no_hp field
                     "    password, " +
                     "    nik, " +
                     "    status, " +
                     "    gaji " +
                     "FROM " +
                     "    karyawan " +
                     "WHERE " +
                     "    id_karyawan LIKE ? OR username LIKE ? " +
                     "ORDER BY " +
                     "    id_karyawan ASC";
        PreparedStatement stmt = cn.prepareStatement(sql);
        String searchPattern = "%" + searchText + "%";
        stmt.setString(1, searchPattern); // Untuk id_karyawan
        stmt.setString(2, searchPattern); // Untuk username
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String idKaryawan = rs.getString("id_karyawan");
            String username = rs.getString("username");
            String noHp = rs.getString("no_hp"); // Added no_hp data
            String password = rs.getString("password");
            String nik = rs.getString("nik");
            String status = rs.getString("status");
            double gaji = rs.getDouble("gaji");
            model.addRow(new Object[]{
                idKaryawan,
                username,
                noHp, // Added no_hp to row
                password,
                nik,
                status,
                df.format(gaji)
            });
        }

        table1.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {100, 150, 150, 150, 150, 100, 150};
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
                if (selectedRow == -1) { // Pastikan ada baris yang dipilih
                    JOptionPane.showMessageDialog(null, "Pilih baris terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    updateKaryawan(selectedRow);
                } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteKaryawan(selectedRow);
                }
            }
        });
        table1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Pastikan event selesai
                    int selectedRow = table1.getSelectedRow();
                    if (selectedRow >= 0) {
                        // Ambil id_supplier dari baris yang dipilih
                        String idSupplier = table1.getValueAt(selectedRow, 0).toString();
                        updateTable2(idSupplier); // Perbarui table2 berdasarkan id_supplier
                    } else {
                        updateTable2(null); // Kosongkan table2 jika tidak ada baris yang dipilih
                    }
                }
            }
        });
        

        // Pastikan tabel bisa menerima input keyboard
        table1.setFocusable(true);
        table1.requestFocusInWindow();
    }

    private void deleteKaryawan(int selectedRow) {
        // Ambil id_karyawan dari baris yang dipilih
        String id = table1.getValueAt(selectedRow, 0).toString();

        // Konfirmasi penghapusan
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus karyawan dengan ID " + id + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Lanjutkan dengan penghapusan data
        try {
            // Disable autocommit untuk memulai transaksi
            cn.setAutoCommit(false);

            // Query untuk menghapus karyawan berdasarkan id_karyawan
            String sql = "DELETE FROM karyawan WHERE id_karyawan = ?";
            try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.executeUpdate();
            }

            cn.commit(); // Commit transaksi
            JOptionPane.showMessageDialog(this, "Karyawan berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Refresh tabel setelah penghapusan
            table();

        } catch (SQLException ex) {
            try {
                if (cn != null) cn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Error menghapus karyawan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } 
    }

    private void updateKaryawan(int selectedRow) {
    // Ambil data dari baris yang dipilih untuk mengisi field secara default
    String id = table1.getValueAt(selectedRow, 0).toString();
    String username = table1.getValueAt(selectedRow, 1).toString();
    String noHp = table1.getValueAt(selectedRow, 2).toString(); // Added no_hp
    String password = table1.getValueAt(selectedRow, 3).toString();
    String nik = table1.getValueAt(selectedRow, 4).toString();
    String status = table1.getValueAt(selectedRow, 5).toString();
    String gaji = table1.getValueAt(selectedRow, 6).toString();

    // Hapus format satuan ribu dari gaji (misalnya, "1,234,567" menjadi "1234567")
    gaji = gaji.replaceAll("[^0-9]", ""); // Hapus semua karakter kecuali angka dan titik

    // Membuat field input menggunakan TextFieldSuggestion
    TextFieldSuggestion idField = new TextFieldSuggestion();
    TextFieldSuggestion usernameField = new TextFieldSuggestion();
    TextFieldSuggestion noHpField = new TextFieldSuggestion(); // Added no_hp field
    TextFieldSuggestion passwordField = new TextFieldSuggestion();
    TextFieldSuggestion nikField = new TextFieldSuggestion();
    TextFieldSuggestion gajiField = new TextFieldSuggestion();
    ComboBoxSuggestion<String> statusField = new ComboBoxSuggestion<>();

    // Atur panjang TextFieldSuggestion
    Dimension fieldSize = new Dimension(200, 35);
    idField.setPreferredSize(fieldSize);
    usernameField.setPreferredSize(fieldSize);
    noHpField.setPreferredSize(fieldSize); // Added no_hp field size
    passwordField.setPreferredSize(fieldSize);
    nikField.setPreferredSize(fieldSize);
    gajiField.setPreferredSize(fieldSize);
    statusField.setPreferredSize(fieldSize);

    // Isi ComboBoxSuggestion untuk status dan atur selected item
    statusField.addItem("admin");
    statusField.addItem("karyawan");
    statusField.setSelectedItem(status);

    // Isi field dengan data dari baris yang dipilih
    idField.setText(id);
    usernameField.setText(username);
    noHpField.setText(noHp); // Added no_hp field text
    passwordField.setText(password);
    nikField.setText(nik);
    gajiField.setText(gaji);

    // Label untuk setiap field
    JLabel l0 = new JLabel("ID Karyawan:");
    JLabel l1 = new JLabel("Username:");
    JLabel l2 = new JLabel("No HP:"); // Added no_hp label
    JLabel l3 = new JLabel("Password:");
    JLabel l4 = new JLabel("NIK:");
    JLabel l5 = new JLabel("Gaji:");
    JLabel l6 = new JLabel("Status:");

    // Panel utama dengan GridLayout 7x1 (7 baris, 1 kolom, gap 5)
    JPanel mainPanel = new JPanel(new GridLayout(7, 1, 5, 5));

    // Tambahkan pasangan label dan field ke mainPanel dalam grid 7x1
    mainPanel.add(createInputPanel(l0, idField));
    mainPanel.add(createInputPanel(l1, usernameField));
    mainPanel.add(createInputPanel(l2, noHpField)); // Added no_hp field
    mainPanel.add(createInputPanel(l3, passwordField));
    mainPanel.add(createInputPanel(l4, nikField));
    mainPanel.add(createInputPanel(l5, gajiField));
    mainPanel.add(createInputPanel(l6, statusField));

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
    usernameField.addKeyListener(enterKeyListener);
    noHpField.addKeyListener(enterKeyListener); // Added no_hp field listener
    passwordField.addKeyListener(enterKeyListener);
    nikField.addKeyListener(enterKeyListener);
    gajiField.addKeyListener(enterKeyListener);

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
    JDialog dialog = optionPane.createDialog(this, "Update Data Karyawan");

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
    String valUsername = usernameField.getText().trim();
    String valNoHp = noHpField.getText().trim(); // Added no_hp input
    String valPassword = passwordField.getText().trim();
    String valNik = nikField.getText().trim();
    String valGaji = gajiField.getText().trim();
    String valStatus = (String) statusField.getSelectedItem();

    // Lanjutkan dengan penyimpanan data
    try {
        // Validasi input
        if (valId.isEmpty() || valUsername.isEmpty() || valPassword.isEmpty() || valNik.isEmpty() || valStatus == null) {
            JOptionPane.showMessageDialog(this, "ID Karyawan, Username, Password, NIK, dan Status wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double valGajiNum = Double.parseDouble(valGaji.isEmpty() ? "0" : valGaji);

        // Disable autocommit untuk memulai transaksi
        cn.setAutoCommit(false);

        // Query untuk update data di tabel karyawan
        String sql = "UPDATE karyawan SET username = ?, no_hp = ?, password = ?, nik = ?, status = ?, gaji = ? WHERE id_karyawan = ?";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            pstmt.setString(1, valUsername);
            pstmt.setString(2, valNoHp); // Added no_hp parameter
            pstmt.setString(3, valPassword);
            pstmt.setString(4, valNik);
            pstmt.setString(5, valStatus);
            pstmt.setDouble(6, valGajiNum);
            pstmt.setString(7, valId);
            pstmt.executeUpdate();
        }

        cn.commit(); // Commit transaksi
        JOptionPane.showMessageDialog(this, "Data karyawan berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

        // Refresh tabel setelah update
        table();

    } catch (SQLException | NumberFormatException ex) {
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

    private void addKaryawan() {
    // Membuat field input menggunakan TextFieldSuggestion
    TextFieldSuggestion id = new TextFieldSuggestion();
    TextFieldSuggestion username = new TextFieldSuggestion();
    TextFieldSuggestion noHp = new TextFieldSuggestion(); // Added no_hp field
    TextFieldSuggestion password = new TextFieldSuggestion();
    TextFieldSuggestion nik = new TextFieldSuggestion();
    TextFieldSuggestion gaji = new TextFieldSuggestion();
    ComboBoxSuggestion<String> status = new ComboBoxSuggestion<>();

    // Atur panjang TextFieldSuggestion
    Dimension fieldSize = new Dimension(200, 35);
    id.setPreferredSize(fieldSize);
    username.setPreferredSize(fieldSize);
    noHp.setPreferredSize(fieldSize); // Added no_hp field size
    password.setPreferredSize(fieldSize);
    nik.setPreferredSize(fieldSize);
    gaji.setPreferredSize(fieldSize);
    status.setPreferredSize(fieldSize);

    // Isi ComboBoxSuggestion untuk status dan atur selected item ke -1
    status.addItem("admin");
    status.addItem("karyawan");
    status.setSelectedIndex(-1); // Tidak ada item yang terpilih secara default

    // Label untuk setiap field
    JLabel l0 = new JLabel("ID Karyawan:");
    JLabel l1 = new JLabel("Username:");
    JLabel l2 = new JLabel("No HP:"); // Added no_hp label
    JLabel l3 = new JLabel("Password:");
    JLabel l4 = new JLabel("NIK:");
    JLabel l5 = new JLabel("Gaji:");
    JLabel l6 = new JLabel("Status:");

    // Panel utama dengan GridLayout 7x1 (7 baris, 1 kolom, gap 5)
    JPanel mainPanel = new JPanel(new GridLayout(7, 1, 5, 5));

    // Tambahkan pasangan label dan field ke mainPanel dalam grid 7x1
    mainPanel.add(createInputPanel(l0, id));
    mainPanel.add(createInputPanel(l1, username));
    mainPanel.add(createInputPanel(l2, noHp)); // Added no_hp field
    mainPanel.add(createInputPanel(l3, password));
    mainPanel.add(createInputPanel(l4, nik));
    mainPanel.add(createInputPanel(l5, gaji));
    mainPanel.add(createInputPanel(l6, status)); // Status dipindah ke paling bawah

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
    id.addKeyListener(enterKeyListener);
    username.addKeyListener(enterKeyListener);
    noHp.addKeyListener(enterKeyListener); // Added no_hp field listener
    password.addKeyListener(enterKeyListener);
    nik.addKeyListener(enterKeyListener);
    gaji.addKeyListener(enterKeyListener);

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
    id.requestFocusInWindow();

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
    String valId = id.getText().trim();
    String valUsername = username.getText().trim();
    String valNoHp = noHp.getText().trim(); // Added no_hp input
    String valPassword = password.getText().trim();
    String valNik = nik.getText().trim();
    String valGaji = gaji.getText().trim();
    String valStatus = (String) status.getSelectedItem();

    // Lanjutkan dengan penyimpanan data
    try {
        // Validasi input
        if (valId.isEmpty() || valUsername.isEmpty() || valPassword.isEmpty() || valNik.isEmpty() || valStatus == null) {
            JOptionPane.showMessageDialog(this, "ID Karyawan, Username, Password, NIK, dan Status wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double valGajiNum = Double.parseDouble(valGaji.isEmpty() ? "0" : valGaji);

        // Disable autocommit untuk memulai transaksi
        cn.setAutoCommit(false);

        // Query untuk insert ke tabel karyawan
        String sql = "INSERT INTO karyawan (id_karyawan, username, no_hp, password, nik, status, gaji) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            pstmt.setString(1, valId);
            pstmt.setString(2, valUsername);
            pstmt.setString(3, valNoHp); // Added no_hp parameter
            pstmt.setString(4, valPassword);
            pstmt.setString(5, valNik);
            pstmt.setString(6, valStatus);
            pstmt.setDouble(7, valGajiNum);
            pstmt.executeUpdate();
        }

        cn.commit(); // Commit transaksi
        JOptionPane.showMessageDialog(this, "Data karyawan berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException | NumberFormatException ex) {
        try {
            if (cn != null) cn.rollback();
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
    
    private void gaji() {
    // Check if a row is selected in the table
    int selectedRow = table1.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(null, 
            "Pilih karyawan terlebih dahulu dari tabel!", 
            "Peringatan", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Get username and gaji from the selected row
    String username = table1.getValueAt(selectedRow, 1).toString(); // Username is in column 1
    String gajiStr = table1.getValueAt(selectedRow, 6).toString(); // Gaji is in column 6
    
    

    // Parse gaji to double (remove thousand separators and handle decimal)
    double gaji;
    try {
        // Remove thousand separators (dots) and replace comma with dot for decimal
        String cleanGajiStr = gajiStr.replace(".", "").replace(",", ".");
        gaji = Double.parseDouble(cleanGajiStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, 
            "Gaji tidak valid: " + gajiStr, 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        return;
    }

    // Define status
    String status = "penggajian";

    // Insert into pengeluaran table
    String sql = "INSERT INTO pengeluaran (keterangan, total, status) VALUES (?, ?, ?)";
    try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
        pstmt.setString(1, username); // keterangan = username
        pstmt.setDouble(2, gaji);     // total = gaji
        pstmt.setString(3, status);   // status = penggajian

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, 
                "Data penggajian untuk " + username + " berhasil disimpan!", 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, 
                "Gagal menyimpan data penggajian!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, 
            "Error saat menyimpan data penggajian: " + ex.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
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
        jScrollPane2 = new javax.swing.JScrollPane();
        table2 = new com.raven.swing.Table1();
        jButton2 = new javax.swing.JButton();

        setBackground(new java.awt.Color(250, 250, 250));

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nama", "Nomor Telepon", "Alamat", "Username", "Password"
            }
        ));
        table1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table1MouseClicked(evt);
            }
        });
        table1.fixTable(jScrollPane1);
        jScrollPane1.setViewportView(table1);

        pencarian.setText("Search ");
        pencarian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pencarianActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel1.setText("Karyawan");

        jButton1.setText("tambah");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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
        table2.fixTable(jScrollPane2);
        jScrollPane2.setViewportView(table2);

        jButton2.setText("gaji");
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
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 724, Short.MAX_VALUE)
                            .addComponent(jScrollPane2))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton2)
                                    .addComponent(jButton1))))
                        .addGap(52, 52, 52))))
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
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void table1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_table1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        addKaryawan();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void pencarianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pencarianActionPerformed
        searchKaryawan();
    }//GEN-LAST:event_pencarianActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        gaji();
    }//GEN-LAST:event_jButton2ActionPerformed

   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private jtextfield.TextFieldSuggestion pencarian;
    private com.raven.swing.Table1 table1;
    private com.raven.swing.Table1 table2;
    // End of variables declaration//GEN-END:variables
}
