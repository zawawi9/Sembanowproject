/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.raven.form;

import Sortdata.UrutanDataPelanggan;
import Sortdata.UrutanDataSupplier;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import jtextfield.ComboBoxSuggestion;
import jtextfield.TextFieldSuggestion;
import raven.dialog.Delete;
import raven.dialog.Loading;
import raven.dialog.Pilihdahulu;
import raven.dialog.Pilihsalahsatu;

/**
 *
 * @author Fitrah
 */
public class Form_Pelanggan extends javax.swing.JPanel {

    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();
    private final DecimalFormat df;
    private boolean ganti = false;
    
    public Form_Pelanggan() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); 
        df = new DecimalFormat("#,###", symbols);
        initComponents();
        table();
        setupTableListener();
        EditPelanggan();
        Window window = SwingUtilities.getWindowAncestor(Form_Pelanggan.this);
                Loading muat = new Loading((java.awt.Frame) window, true);
        muat.setVisible(true);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("1"),"refresh");
        getActionMap().put("refresh", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                showData();
            }
        });
        
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("2"),"tambahkaryawan");
        getActionMap().put("tambahkaryawan", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                Window window = SwingUtilities.getWindowAncestor(Form_Pelanggan.this);
        Form_tbhPelanggan tbhpelanggan = new Form_tbhPelanggan((java.awt.Frame) window, true);
        tbhpelanggan.setVisible(true);
        showData();
            }
        });
        Tambah1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(ganti){
            table2();
            Tambah1.setText("Produk");
            Tambah.setVisible(false);
        Edit.setVisible(false);
        Hapus.setVisible(false);
        }else{
            table();
            Tambah1.setText("Ranking");
            Tambah.setVisible(true);
        Edit.setVisible(true);
        Hapus.setVisible(true);
        }
        ganti = !ganti;
            }
        });
    
    }
    
    public void showData(){
        pencarian.setText("");
        Tambah1.setText("Ranking");
        Tambah.setVisible(true);
        Edit.setVisible(true);
        Hapus.setVisible(true);
        table();
        jComboBox_Custom1.setSelectedItem("Terbaru");
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        model.setRowCount(0);
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
        try {
            String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
            
            String SQL = "SELECT * FROM pelanggan";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(SQL);
            while(rs.next()){
                Object[]row = {
                        rs.getString("rfidpelanggan"),
                        rs.getString("nama"),
                rs.getString("no_hp"),
                rs.getString("alamat"),
                rs.getString("tipe_harga")};
                model.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + e.getMessage());
        }
        
    }
    public void UpdateTabel(List<UrutanDataPelanggan>SortPelanggan){
        DefaultTableModel model = (DefaultTableModel)table1.getModel();
        model.setRowCount(0);
        for(UrutanDataPelanggan dataPelanggan : SortPelanggan){
            model.addRow(new Object[]{dataPelanggan.getID(), dataPelanggan.getNama(), dataPelanggan.getTelepon(), dataPelanggan.getAlamat(), 
                dataPelanggan.getTipe()});
        }
    }
    public void UrutanData(String choose){
                
        String sql = "SELECT rfidpelanggan, nama, no_hp, alamat, tipe_harga FROM pelanggan";
        switch (choose) {
            case "Terbaru":
                sql += " ORDER BY id_pelanggan";
                System.out.println("Data diurut terbaru");
                break;
                case "Nama Paling Awal":
                sql += " ORDER BY nama ASC";
                System.out.println("Data diurut nama paling awal");
                break;
                case "Nama Paling Akhir":
                sql += " ORDER BY nama DESC";
                System.out.println("Data diurut nama paling akhir");
                break;
            default:sql += " ORDER BY id_pelanggan";
                System.out.println("Data diurut terbaru");
                break;
                
        }
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:/sembakogrok", "root", "");
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()){
            List<UrutanDataPelanggan>SortPelanggan = new ArrayList<>();
            while (rs.next()) {                
                UrutanDataPelanggan urutanPelanggan = new UrutanDataPelanggan();
                urutanPelanggan.setID(rs.getString("rfidpelanggan"));
                urutanPelanggan.setNama(rs.getString("nama"));
                urutanPelanggan.setTelepon(rs.getString("no_hp"));
                urutanPelanggan.setAlamat(rs.getString("alamat"));
                urutanPelanggan.setTipe(rs.getString("tipe_harga"));
                SortPelanggan.add(urutanPelanggan);
            }
            Window window = SwingUtilities.getWindowAncestor(Form_Pelanggan.this);
                Loading muat = new Loading((java.awt.Frame) window, true);
        muat.setVisible(true);
            UpdateTabel(SortPelanggan);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    private String getIDPelanggan(String RFID){
        String idPelanggan = "";
        try {
            Connection conn;
            PreparedStatement pstmt;
            String url = "jdbc:mysql://localhost:/sembakogrok";
            String dbUser = "root";
            String dbPass = "";
            conn = DriverManager.getConnection(url, dbUser, dbPass);
            
            String sql = "SELECT id_pelanggan FROM pelanggan WHERE rfidpelanggan = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, RFID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                idPelanggan = rs.getString("id_pelanggan");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idPelanggan;
    }
    public void EditPelanggan(){
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke("3"),"editpelanggan");
        getActionMap().put("editpelanggan", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
        int[]selectedRows=table1.getSelectedRows();
                
        if(selectedRows.length==1){
            int rows = selectedRows[0];
            DefaultTableModel Model = (DefaultTableModel) table1.getModel();
            
            String RFID = Model.getValueAt(rows, 0).toString();
            String Nama = Model.getValueAt(rows, 1).toString();
            String Telepon = Model.getValueAt(rows, 2).toString();
            String Alamat = Model.getValueAt(rows, 3).toString();
            String Tipe = Model.getValueAt(rows, 4).toString();
            
            String ID = getIDPelanggan(RFID);
            Window window = SwingUtilities.getWindowAncestor(Form_Pelanggan.this);
            Form_editPelanggan editPelanggan = new Form_editPelanggan((Frame)window, true);
            editPelanggan.setID(ID);
            editPelanggan.ambilData(ID, RFID, Nama, Telepon, Alamat, Tipe);
            editPelanggan.setVisible(true);
            String[]updateData=editPelanggan.getData();
            if(updateData != null){
                Model.setValueAt(updateData[0], rows, 0);//RFID
                Model.setValueAt(updateData[1], rows, 1);//Nama
                Model.setValueAt(updateData[2], rows, 2);//Telepon
                Model.setValueAt(updateData[3], rows, 3);//Alamat
                Model.setValueAt(updateData[4], rows, 4);//Tipe
                
                
            }
        
        }else if(selectedRows.length>1){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_Pelanggan.this);
            Pilihsalahsatu salah = new Pilihsalahsatu(parent, true);
            salah.setVisible(true);
            return;
        }else{
            
        }
        showData();
            }
        });
    }


public void table() {
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Membuat tabel tidak dapat diedit
        }
    };
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("RFID");
    model.addColumn("Nama");
    model.addColumn("Nomor Telepon");
    model.addColumn("Alamat");
    model.addColumn("Tipe Harga");

    try {
        String sql = "SELECT " +
                     "    rfidpelanggan, " +
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
            String rfid = rs.getString("rfidpelanggan");
            String nama = rs.getString("nama");
            String no_hp = rs.getString("no_hp");
            String alamat = rs.getString("alamat");
            String tipeHarga = rs.getString("tipe_harga");
            model.addRow(new Object[]{
                rfid,
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
    model.addColumn("RFID");
    model.addColumn("Nama");
    model.addColumn("Nomor Telepon");
    model.addColumn("Alamat");
    model.addColumn("Tipe Harga");

    try {
        // Query untuk mencari data berdasarkan id_pelanggan atau nama
        String sql = "SELECT " +
                     "    rfidpelanggan, " +
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
            String rfid = rs.getString("rfidpelanggan");
            String nama = rs.getString("nama");
            String no_hp = rs.getString("no_hp");
            String alamat = rs.getString("alamat");
            String tipeHarga = rs.getString("tipe_harga");
            model.addRow(new Object[]{
                rfid,
                nama,
                no_hp,
                alamat,
                tipeHarga
            });
        }

        table1.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {100, 150, 150,200, 200, 100};
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
                return;
            }

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                //updatePelanggan(selectedRow);
            } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                deletePelanggan(selectedRow);
            }
        }
    });

    // Pastikan tabel bisa menerima input keyboard
    table1.setFocusable(true);
    table1.requestFocusInWindow();
}

private void deletePelanggan(int selectedRow) {
    getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"),"hapuspelanggan");
        getActionMap().put("hapuspelanggan", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
        int[]selectedRows=table1.getSelectedRows();
        if(selectedRows.length>0){
            java.util.List<String> idsToDelete = new java.util.ArrayList<>();
        java.util.List<String> rfidGagalDitemukan = new java.util.ArrayList<>();
            for(int row : selectedRows){
                Object value = table1.getValueAt(row, 0);
                if(value!=null){
                    String rfid = value.toString();
                    String idPelanggan = getIDPelanggan(rfid);
                if (idPelanggan != null && !idPelanggan.isEmpty()) {
                    idsToDelete.add(idPelanggan);
                } else {
                    rfidGagalDitemukan.add(rfid);
                    System.out.println("Tidak dapat menemukan id_pelanggan untuk RFID: " + rfid + " saat akan menghapus.");
                }
            }
        }


        if (idsToDelete.isEmpty()) {
            if (rfidGagalDitemukan.isEmpty() && selectedRows.length > 0) {
            } else if (selectedRows.length == 0) {
                 Window window = SwingUtilities.getWindowAncestor(Form_Pelanggan.this);
                 Pilihdahulu pilih = new Pilihdahulu((Frame)window, true);
                 pilih.setVisible(true);
            }
            return; // Tidak ada ID valid untuk dihapus
        }

        // LANGKAH 2: Konfirmasi dari pengguna
        // Ganti 'Delete' dengan nama kelas dialog konfirmasi Anda
        Window window = SwingUtilities.getWindowAncestor(Form_Pelanggan.this);
            Delete hapus = new Delete((Frame)window, true);
            hapus.setVisible(true);
            if(hapus.isConfirmed()){
                String sql = "DELETE FROM pelanggan WHERE id_pelanggan IN "
                        + "("+String.join(",", Collections.nCopies(idsToDelete.size(),"?"))+")";
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:/sembakogrok", "root", "");
                     PreparedStatement pstmt = conn.prepareStatement(sql)){
                    for(int i = 0; i < idsToDelete.size();i++){
                        pstmt.setString(i+1, idsToDelete.get(i));
                    }
                    int rowsAffected = pstmt.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Data terhapus");
                        showData();
                    }else{
                        System.out.println("Tidak ada data yang terhapus");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
                }
        });
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
    model.addColumn("RFID");
    model.addColumn("Nama");
    model.addColumn("No Telepon");         // Tambahan
    model.addColumn("Alamat");
    model.addColumn("Tipe Harga");
    model.addColumn("Total Transaksi");
    model.addColumn("Total Uang");

    try {
        String sql = "SELECT " +
                     "    p.id_pelanggan, " +
                     "    p.rfidpelanggan, " +
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
                     "    p.id_pelanggan, p.rfidpelanggan, p.nama, p.no_hp, p.alamat, p.tipe_harga " +
                     "ORDER BY " +
                     "    total_transaksi DESC";

        PreparedStatement stmt = cn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String idPelanggan = rs.getString("id_pelanggan");
            String rfid = rs.getString("rfidpelanggan");
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

        int[] columnWidths = {100, 150, 150, 120, 200, 100, 120, 150};
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
        Tambah = new Custom.Custom_ButtonRounded();
        Tambah1 = new Custom.Custom_ButtonRounded();
        jLabel2 = new javax.swing.JLabel();
        jComboBox_Custom1 = new com.raven.swing.jComboBox_Custom();
        Edit = new Custom.Custom_ButtonRounded();
        Hapus = new Custom.Custom_ButtonRounded();

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

        pencarian.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                pencarianFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                pencarianFocusLost(evt);
            }
        });
        pencarian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pencarianActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel1.setText("Pelanggan");

        Tambah.setText("Tambah");
        Tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TambahActionPerformed(evt);
            }
        });

        Tambah1.setText("Ranking");
        Tambah1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Tambah1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Urutkan berdasarkan :");

        jComboBox_Custom1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Terbaru", "Nama Paling Awal", "Nama Paling Akhir" }));
        jComboBox_Custom1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_Custom1ActionPerformed(evt);
            }
        });

        Edit.setText("Edit");
        Edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditActionPerformed(evt);
            }
        });

        Hapus.setText("Hapus");
        Hapus.setFillClick(new java.awt.Color(153, 0, 0));
        Hapus.setFillOriginal(new java.awt.Color(255, 0, 0));
        Hapus.setFillOver(new java.awt.Color(255, 51, 51));
        Hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HapusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Tambah1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox_Custom1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Tambah1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox_Custom1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pencarianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pencarianActionPerformed
        searchPelanggan();
    }//GEN-LAST:event_pencarianActionPerformed

    private void TambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahActionPerformed
        Window window = SwingUtilities.getWindowAncestor(Form_Pelanggan.this);
        Form_tbhPelanggan tbhpelanggan = new Form_tbhPelanggan((java.awt.Frame) window, true);
        tbhpelanggan.setVisible(true);
        showData();
    }//GEN-LAST:event_TambahActionPerformed

    private void Tambah1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Tambah1ActionPerformed
        
    }//GEN-LAST:event_Tambah1ActionPerformed

    private void jComboBox_Custom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_Custom1ActionPerformed
        String selectedOption = (String)jComboBox_Custom1.getSelectedItem();
        UrutanData(selectedOption);
    }//GEN-LAST:event_jComboBox_Custom1ActionPerformed

    private void EditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditActionPerformed
        int[]selectedRows=table1.getSelectedRows();
                
        if(selectedRows.length==1){
            int rows = selectedRows[0];
            DefaultTableModel Model = (DefaultTableModel) table1.getModel();
            
            String RFID = Model.getValueAt(rows, 0).toString();
            String Nama = Model.getValueAt(rows, 1).toString();
            String Telepon = Model.getValueAt(rows, 2).toString();
            String Alamat = Model.getValueAt(rows, 3).toString();
            String Tipe = Model.getValueAt(rows, 4).toString();
            
            String ID = getIDPelanggan(RFID);
            Window window = SwingUtilities.getWindowAncestor(Form_Pelanggan.this);
            Form_editPelanggan editPelanggan = new Form_editPelanggan((Frame)window, true);
            editPelanggan.setID(ID);
            editPelanggan.ambilData(ID, RFID, Nama, Telepon, Alamat, Tipe);
            editPelanggan.setVisible(true);
            String[]updateData=editPelanggan.getData();
            if(updateData != null){
                Model.setValueAt(updateData[0], rows, 0);//RFID
                Model.setValueAt(updateData[1], rows, 1);//Nama
                Model.setValueAt(updateData[2], rows, 2);//Telepon
                Model.setValueAt(updateData[3], rows, 3);//Alamat
                Model.setValueAt(updateData[4], rows, 4);//Tipe
                
                
            }
        
        }else if(selectedRows.length>1){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_Pelanggan.this);
            Pilihsalahsatu salah = new Pilihsalahsatu(parent, true);
            salah.setVisible(true);
            return;
        }else{
            
        }
        showData();
            
    }//GEN-LAST:event_EditActionPerformed

    private void HapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HapusActionPerformed
        int[]selectedRows=table1.getSelectedRows();
        if(selectedRows.length>0){
            java.util.List<String> idsToDelete = new java.util.ArrayList<>();
        java.util.List<String> rfidGagalDitemukan = new java.util.ArrayList<>();
            for(int row : selectedRows){
                Object value = table1.getValueAt(row, 0);
                if(value!=null){
                    String rfid = value.toString();
                    String idPelanggan = getIDPelanggan(rfid);
                if (idPelanggan != null && !idPelanggan.isEmpty()) {
                    idsToDelete.add(idPelanggan);
                } else {
                    rfidGagalDitemukan.add(rfid);
                    System.out.println("Tidak dapat menemukan id_pelanggan untuk RFID: " + rfid + " saat akan menghapus.");
                }
            }
        }

        if (!rfidGagalDitemukan.isEmpty()) {
            JOptionPane.showMessageDialog(this, // 'this' merujuk ke Frame/Dialog tempat method ini berada
                    "ID Pelanggan tidak ditemukan untuk RFID berikut (data mungkin tidak konsisten atau sudah terhapus):\n" + 
                    String.join("\n", rfidGagalDitemukan),
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
        }

        if (idsToDelete.isEmpty()) {
            if (rfidGagalDitemukan.isEmpty() && selectedRows.length > 0) {
                 JOptionPane.showMessageDialog(this, "Tidak ada data pelanggan valid yang bisa diproses untuk dihapus.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            } else if (selectedRows.length == 0) {
                 Window window = SwingUtilities.getWindowAncestor(Form_Pelanggan.this);
                 Pilihdahulu pilih = new Pilihdahulu((Frame)window, true);
                 pilih.setVisible(true);
            }
            return; // Tidak ada ID valid untuk dihapus
        }

        // LANGKAH 2: Konfirmasi dari pengguna
        // Ganti 'Delete' dengan nama kelas dialog konfirmasi Anda
        Window window = SwingUtilities.getWindowAncestor(Form_Pelanggan.this);
            Delete hapus = new Delete((Frame)window, true);
            hapus.setVisible(true);
            if(hapus.isConfirmed()){
                String sql = "DELETE FROM pelanggan WHERE id_pelanggan IN "
                        + "("+String.join(",", Collections.nCopies(idsToDelete.size(),"?"))+")";
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:/sembakogrok", "root", "");
                     PreparedStatement pstmt = conn.prepareStatement(sql)){
                    for(int i = 0; i < idsToDelete.size();i++){
                        pstmt.setString(i+1, idsToDelete.get(i));
                    }
                    int rowsAffected = pstmt.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Data terhapus");
                        showData();
                    }else{
                        System.out.println("Tidak ada data yang terhapus");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_HapusActionPerformed

    private void pencarianFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pencarianFocusGained
        pencarian.setText("");
    }//GEN-LAST:event_pencarianFocusGained

    private void pencarianFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pencarianFocusLost
        pencarian.setText("Cari");
        pencarian.setForeground(Color.gray);
    }//GEN-LAST:event_pencarianFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Custom.Custom_ButtonRounded Edit;
    private Custom.Custom_ButtonRounded Hapus;
    private Custom.Custom_ButtonRounded Tambah;
    private Custom.Custom_ButtonRounded Tambah1;
    private com.raven.swing.jComboBox_Custom jComboBox_Custom1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private jtextfield.TextFieldSuggestion pencarian;
    private com.raven.swing.Table1 table1;
    // End of variables declaration//GEN-END:variables
}
