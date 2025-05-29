package com.raven.form;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import jtextfield.TextFieldSuggestion;
import raven.dialog.Delete;
import raven.dialog.Loading;
import raven.dialog.Pilihdahulu;
import raven.dialog.Pilihsalahsatu;

public class Form_Supplier extends javax.swing.JPanel {
    
    Connection cn = koneksi.getKoneksi();
    private final DecimalFormat df;
    private List<String>daftarIDsupplier = new ArrayList<>();

    public Form_Supplier() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); 
        df = new DecimalFormat("#,###", symbols);
        initComponents();
        table1(); // Inisialisasi table1
        table2(null); // Inisialisasi table2 (kosong pada awalnya)
        setupTableListener();
        EditSupplier();
        Window window = SwingUtilities.getWindowAncestor(Form_Supplier.this);
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
                Window window = SwingUtilities.getWindowAncestor(Form_Supplier.this);
        Form_tbhSupplier tbhSupplier = new Form_tbhSupplier((java.awt.Frame) window, true);
        tbhSupplier.setVisible(true);
        showData();
            }
        });
        
        // Tambahkan ListSelectionListener untuk table1
        table1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Pastikan event selesai
                    int selectedRow = table1.getSelectedRow();
                    if (selectedRow >= 0) {
                        // Ambil id_supplier dari baris yang dipilih
                        String idSupplier = table1.getValueAt(selectedRow, 0).toString();
                        table2(idSupplier); // Perbarui table2 berdasarkan id_supplier
                    } else {
                        table2(null); // Kosongkan table2 jika tidak ada baris yang dipilih
                    }
                }
            }
        });
    }
    public void showData(){
        daftarIDsupplier.clear();
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
            
            String SQL = "SELECT * FROM supplier";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(SQL);
            while(rs.next()){
                Object[]row = {rs.getString("id_supplier"),
                    rs.getString("nama"),
                rs.getString("no_hp"),
                rs.getString("alamat")};
                model.addRow(row);
                
                
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + e.getMessage());
        }
        
    }
    public void UpdateTabel(List<UrutanDataSupplier>SortSupplier){
        DefaultTableModel model = (DefaultTableModel)table1.getModel();
        model.setRowCount(0);
        for(UrutanDataSupplier dataSupplier : SortSupplier){
            model.addRow(new Object[]{dataSupplier.getID(), dataSupplier.getNama(), dataSupplier.getTelepon(), dataSupplier.getAlamat()});
        }
    }
    public void UrutanData(String choose){
                
        String sql = "SELECT id_supplier, nama, no_hp, alamat FROM supplier";
        switch (choose) {
            case "Terbaru":
                sql += " ORDER BY id_supplier";
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
            default:sql += " ORDER BY id_supplier";
                System.out.println("Data diurut terbaru");
                break;
                
        }
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:/sembakogrok", "root", "");
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()){
            List<UrutanDataSupplier>SortSupplier = new ArrayList<>();
            while (rs.next()) {                
                UrutanDataSupplier urutanSupplier = new UrutanDataSupplier();
                urutanSupplier.setID(rs.getString("id_supplier"));
                urutanSupplier.setNama(rs.getString("nama"));
                urutanSupplier.setTelepon(rs.getString("no_hp"));
                urutanSupplier.setAlamat(rs.getString("alamat"));
                SortSupplier.add(urutanSupplier);
            }
            Window window = SwingUtilities.getWindowAncestor(Form_Supplier.this);
                Loading muat = new Loading((java.awt.Frame) window, true);
        muat.setVisible(true);
            UpdateTabel(SortSupplier);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void table1() {
        DefaultTableModel model = new DefaultTableModel() ;
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("ID");
        model.addColumn("Nama");
        model.addColumn("Nomor Telepon");
        model.addColumn("Alamat");

        try {
            String sql = "SELECT id_supplier, nama, no_hp, alamat " +
                         "FROM supplier " +
                         "ORDER BY id_supplier ASC";
            PreparedStatement stmt = cn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_supplier"),
                    rs.getString("nama"),
                    rs.getString("no_hp"),
                    rs.getString("alamat")
                });
                
            }

            table1.setModel(model);

            // Pengaturan lebar kolom
            int[] columnWidths = {0, 150, 200, 150};
            for (int i = 0; i < columnWidths.length; i++) {
                table1.getColumnModel().getColumn(0).setMinWidth(0);
table1.getColumnModel().getColumn(0).setMaxWidth(0);
table1.getColumnModel().getColumn(0).setWidth(0);
                table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
            }

            // Sesuaikan tabel dengan scroll pane
            table1.fixTable(jScrollPane1);

            // Pilih baris pertama secara otomatis jika ada data
            if (table1.getRowCount() > 0) {
                table1.setRowSelectionInterval(0, 0);
                String idSupplier = table1.getValueAt(0, 0).toString();
                table2(idSupplier);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel 1: " + e.getMessage());
        }
    }

    public void table2(String idSupplier) {
        DefaultTableModel model = new DefaultTableModel();
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("ID Produk");
        model.addColumn("Nama Produk");
        model.addColumn("Harga Beli");

        if (idSupplier != null) {
            try {
                String sql = "SELECT id_produk, nama, harga_beli " +
                             "FROM produk " +
                             "WHERE id_supplier = ? " +
                             "ORDER BY id_produk ASC";
                PreparedStatement stmt = cn.prepareStatement(sql);
                stmt.setString(1, idSupplier);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("id_produk"),
                        rs.getString("nama"),
                        rs.getDouble("harga_beli")
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null, "Error saat mengambil data tabel 2: " + e.getMessage());
            }
        }

        table2.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {100, 150, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table2.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Sesuaikan tabel dengan scroll pane
        table2.fixTable(jScrollPane1);
    }

    private void searchSupplier() {
    String searchText = pencarian.getText().trim();

    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Membuat tabel tidak dapat diedit
        }
    };
    model.setRowCount(0);
    model.setColumnCount(0);
    model.addColumn("ID");
    model.addColumn("Nama");
    model.addColumn("Nomor Telepon");
    model.addColumn("Alamat");

    try {
        // Query untuk mencari data berdasarkan id_supplier atau nama
        String sql = "SELECT " +
                     "    id_supplier, " +
                     "    nama, " +
                     "    no_hp, " +
                     "    alamat " +
                     "FROM " +
                     "    supplier " +
                     "WHERE " +
                     "    id_supplier LIKE ? OR nama LIKE ? " +
                     "ORDER BY " +
                     "    id_supplier ASC";
        PreparedStatement stmt = cn.prepareStatement(sql);
        String searchPattern = "%" + searchText + "%";
        stmt.setString(1, searchPattern); // Untuk id_supplier
        stmt.setString(2, searchPattern); // Untuk nama
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String idSupplier = rs.getString("id_supplier");
            String nama = rs.getString("nama");
            String alamat = rs.getString("alamat");
            String noHp = rs.getString("no_hp");
            model.addRow(new Object[]{
                idSupplier,
                nama,
                alamat,
                noHp
            });
        }

        table1.setModel(model);

        // Pengaturan lebar kolom
        int[] columnWidths = {0, 150, 200, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table1.getColumnModel().getColumn(0).setMinWidth(0);
table1.getColumnModel().getColumn(0).setMaxWidth(0);
table1.getColumnModel().getColumn(0).setWidth(0);
            table1.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Sesuaikan tabel dengan scroll pane
        table1.fixTable(jScrollPane1);

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saat mencari data: " + e.getMessage());
    }
}

    private void updateSupplier(int selectedRow) {
    // Ambil data dari baris yang dipilih untuk mengisi field secara default
    String id = table1.getValueAt(selectedRow, 0).toString();
    String nama = table1.getValueAt(selectedRow, 1).toString();
    String alamat = table1.getValueAt(selectedRow, 2).toString();
    String noHp = table1.getValueAt(selectedRow, 3).toString();

    // Membuat field input menggunakan TextFieldSuggestion
    TextFieldSuggestion idField = new TextFieldSuggestion();
    TextFieldSuggestion namaField = new TextFieldSuggestion();
    TextFieldSuggestion alamatField = new TextFieldSuggestion();
    TextFieldSuggestion noHpField = new TextFieldSuggestion();

    // Atur panjang TextFieldSuggestion
    Dimension fieldSize = new Dimension(200, 35);
    idField.setPreferredSize(fieldSize);
    namaField.setPreferredSize(fieldSize);
    alamatField.setPreferredSize(fieldSize);
    noHpField.setPreferredSize(fieldSize);

    // Isi field dengan data dari baris yang dipilih
    idField.setText(id);
    namaField.setText(nama);
    alamatField.setText(alamat);
    noHpField.setText(noHp);

    // Label untuk setiap field
    JLabel l0 = new JLabel("ID Supplier:");
    JLabel l1 = new JLabel("Nama:");
    JLabel l2 = new JLabel("Alamat:");
    JLabel l3 = new JLabel("No. HP:");

    // Panel utama dengan GridLayout 4x1 (4 baris, 1 kolom, gap 5)
    JPanel mainPanel = new JPanel(new GridLayout(4, 1, 5, 5));

    // Tambahkan pasangan label dan field ke mainPanel dalam grid 4x1
    mainPanel.add(createInputPanel(l0, idField));
    mainPanel.add(createInputPanel(l1, namaField));
    mainPanel.add(createInputPanel(l2, alamatField));
    mainPanel.add(createInputPanel(l3, noHpField));

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

    // Terapkan KeyListener ke setiap field
    idField.addKeyListener(enterKeyListener);
    namaField.addKeyListener(enterKeyListener);
    alamatField.addKeyListener(enterKeyListener);
    noHpField.addKeyListener(enterKeyListener);

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
    JDialog dialog = optionPane.createDialog(this, "Update Data Supplier");

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
    String valAlamat = alamatField.getText().trim();
    String valNoHp = noHpField.getText().trim();

    // Lanjutkan dengan penyimpanan data
    try {
        // Validasi input
        if (valId.isEmpty() || valNama.isEmpty() || valAlamat.isEmpty() || valNoHp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Supplier, Nama, Alamat, dan No. HP wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable autocommit untuk memulai transaksi
        cn.setAutoCommit(false);

        // Query untuk update data di tabel supplier
        String sql = "UPDATE supplier SET nama = ?, alamat = ?, no_hp = ? WHERE id_supplier = ?";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            pstmt.setString(1, valNama);
            pstmt.setString(2, valAlamat);
            pstmt.setString(3, valNoHp);
            pstmt.setString(4, valId);
            pstmt.executeUpdate();
        }

        cn.commit(); // Commit transaksi
        JOptionPane.showMessageDialog(this, "Data supplier berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

        // Refresh tabel setelah update
        table1();

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

    private void addSupplier() {
    // Membuat field input menggunakan TextFieldSuggestion
    TextFieldSuggestion id = new TextFieldSuggestion();
    TextFieldSuggestion nama = new TextFieldSuggestion();
    TextFieldSuggestion alamat = new TextFieldSuggestion();
    TextFieldSuggestion noHp = new TextFieldSuggestion();

    // Atur panjang TextFieldSuggestion
    Dimension fieldSize = new Dimension(200, 35);
    id.setPreferredSize(fieldSize);
    nama.setPreferredSize(fieldSize);
    alamat.setPreferredSize(fieldSize);
    noHp.setPreferredSize(fieldSize);

    // Label untuk setiap field
    JLabel l0 = new JLabel("ID Supplier:");
    JLabel l1 = new JLabel("Nama:");
    JLabel l2 = new JLabel("Alamat:");
    JLabel l3 = new JLabel("No. HP:");

    // Panel utama dengan GridLayout 4x1 (4 baris, 1 kolom, gap 5)
    JPanel mainPanel = new JPanel(new GridLayout(4, 1, 5, 5));

    // Tambahkan pasangan label dan field ke mainPanel dalam grid 4x1
    mainPanel.add(createInputPanel(l0, id));
    mainPanel.add(createInputPanel(l1, nama));
    mainPanel.add(createInputPanel(l2, alamat));
    mainPanel.add(createInputPanel(l3, noHp));

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

    // Terapkan KeyListener ke setiap field
    id.addKeyListener(enterKeyListener);
    nama.addKeyListener(enterKeyListener);
    alamat.addKeyListener(enterKeyListener);
    noHp.addKeyListener(enterKeyListener);

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
    JDialog dialog = optionPane.createDialog(this, "Masukkan Data Supplier");

    // Pastikan semua komponen di dalam dialog juga putih
    setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);

    // Show dialog
    dialog.setVisible(true);

    // Atur fokus ke field id setelah dialog muncul
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
    String valNama = nama.getText().trim();
    String valAlamat = alamat.getText().trim();
    String valNoHp = noHp.getText().trim();

    // Lanjutkan dengan penyimpanan data
    try {
        // Validasi input
        if (valId.isEmpty() || valNama.isEmpty() || valAlamat.isEmpty() || valNoHp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Supplier, Nama, Alamat, dan No. HP wajib diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable autocommit untuk memulai transaksi
        cn.setAutoCommit(false);

        // Query untuk insert ke tabel supplier
        String sql = "INSERT INTO supplier (id_supplier, nama, alamat, no_hp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            pstmt.setString(1, valId);
            pstmt.setString(2, valNama);
            pstmt.setString(3, valAlamat);
            pstmt.setString(4, valNoHp);
            pstmt.executeUpdate();
        }

        cn.commit(); // Commit transaksi
        JOptionPane.showMessageDialog(this, "Data supplier berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

        // Refresh tabel setelah insert
        table1();

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
    
    private void setupTableListener() {
    table1.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            int selectedRow = table1.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                updateSupplier(selectedRow);
            } 
        }
    });

    // Pastikan tabel bisa menerima input keyboard
    table1.setFocusable(true);
    table1.requestFocusInWindow();
}
    private String valueToString(Object value) {
    return value != null ? value.toString() : "";
    }
    
    public void EditSupplier(){
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("3"),"editkaryawan");
        getActionMap().put("editkaryawan", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
        int[]selectedRows=table1.getSelectedRows();
                
        if(selectedRows.length==1){
            int rows = selectedRows[0];
            DefaultTableModel Model = (DefaultTableModel) table1.getModel();
            String ID = Model.getValueAt(rows, 0).toString();
            String Nama = valueToString(Model.getValueAt(rows, 1));
            String Telepon = valueToString(Model.getValueAt(rows, 2));
            String Alamat = valueToString(Model.getValueAt(rows, 3));
            
            Window window = SwingUtilities.getWindowAncestor(Form_Supplier.this);
            Form_editSupplier editsup = new Form_editSupplier((Frame)window, true);
            editsup.setID(ID);
            editsup.ambilData(ID, Nama, Telepon, Alamat);
            editsup.setVisible(true);
            String[]updateData=editsup.getData();
            if(updateData != null){
                Model.setValueAt(updateData[0], rows, 1);//ID
                Model.setValueAt(updateData[1], rows, 1);//Nama
                Model.setValueAt(updateData[2], rows, 2);//Telepon
                Model.setValueAt(updateData[3], rows, 3);//Alamat
                
                
            }
        
        }else if(selectedRows.length>1){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_Supplier.this);
            Pilihsalahsatu salah = new Pilihsalahsatu(parent, true);
            salah.setVisible(true);
            return;
        }else{
            
        }
        showData();
            }
        });
    }
    private void deleteSupplier(int selectedRow) {
        // Ambil id_karyawan dari baris yang dipilih
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"),"hapuskaryawan");
        getActionMap().put("hapuskaryawan", new AbstractAction(){
            public void actionPerformed(ActionEvent e){
        int[]selectedRows=table1.getSelectedRows();
        if(selectedRows.length>0){
            List<String>DeleteID=new ArrayList<>();
            for(int row : selectedRows){
                Object value = table1.getValueAt(row, 0);
                if(value!=null){
                    DeleteID.add(value.toString());
                }
            }
            Window window = SwingUtilities.getWindowAncestor(Form_Supplier.this);
            Delete hapus = new Delete((Frame)window, true);
            hapus.setVisible(true);
            if(hapus.isConfirmed()){
                String sql = "DELETE FROM supplier WHERE id_supplier IN "
                        + "("+String.join(",", Collections.nCopies(DeleteID.size(),"?"))+")";
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:/sembakogrok", "root", "");
                     PreparedStatement pstmt = conn.prepareStatement(sql)){
                    for(int i = 0; i < DeleteID.size();i++){
                        pstmt.setString(i+1, DeleteID.get(i));
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
    
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new com.raven.swing.Table1();
        pencarian = new jtextfield.TextFieldSuggestion();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table2 = new com.raven.swing.Table1();
        Tambah = new Custom.Custom_ButtonRounded();
        jLabel2 = new javax.swing.JLabel();
        jComboBox_Custom1 = new com.raven.swing.jComboBox_Custom();
        Edit = new Custom.Custom_ButtonRounded();
        Hapus = new Custom.Custom_ButtonRounded();

        setBackground(new java.awt.Color(250, 250, 250));

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nama", "Nomor Telepon", "Alamat"
            }
        ));
        table1.fixTable(jScrollPane1);
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
        jLabel1.setText("Supplier");

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
        table1.fixTable(jScrollPane2);
        jScrollPane2.setViewportView(table2);

        Tambah.setText("Tambah");
        Tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TambahActionPerformed(evt);
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
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2)
                    .addComponent(jComboBox_Custom1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox_Custom1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(224, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pencarianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pencarianActionPerformed
       searchSupplier();
    }//GEN-LAST:event_pencarianActionPerformed

    private void TambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahActionPerformed
        Window window = SwingUtilities.getWindowAncestor(Form_Supplier.this);
        Form_tbhSupplier tbhsupplier = new Form_tbhSupplier((java.awt.Frame) window, true);
        tbhsupplier.setVisible(true);
        showData();
    }//GEN-LAST:event_TambahActionPerformed

    private void pencarianFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pencarianFocusGained
        pencarian.setText("");
    }//GEN-LAST:event_pencarianFocusGained

    private void pencarianFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pencarianFocusLost
        pencarian.setText("Cari");
        pencarian.setForeground(Color.gray);
    }//GEN-LAST:event_pencarianFocusLost

    private void EditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditActionPerformed
        int[]selectedRows=table1.getSelectedRows();
                
        if(selectedRows.length==1){
            int rows = selectedRows[0];
            DefaultTableModel Model = (DefaultTableModel) table1.getModel();
            String ID = Model.getValueAt(rows, 0).toString();
            String Nama = valueToString(Model.getValueAt(rows, 1));
            String Telepon = valueToString(Model.getValueAt(rows, 2));
            String Alamat = valueToString(Model.getValueAt(rows, 3));
            
            Window window = SwingUtilities.getWindowAncestor(Form_Supplier.this);
            Form_editSupplier editsup = new Form_editSupplier((Frame)window, true);
            editsup.setID(ID);
            editsup.ambilData(ID, Nama, Telepon, Alamat);
            editsup.setVisible(true);
            String[]updateData=editsup.getData();
            if(updateData != null){
                Model.setValueAt(updateData[0], rows, 1);//ID
                Model.setValueAt(updateData[1], rows, 1);//Nama
                Model.setValueAt(updateData[2], rows, 2);//Telepon
                Model.setValueAt(updateData[3], rows, 3);//Alamat
                
                
            }
        
        }else if(selectedRows.length>1){
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_Supplier.this);
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
            List<String>DeleteID=new ArrayList<>();
            for(int row : selectedRows){
                Object value = table1.getValueAt(row, 0);
                if(value!=null){
                    DeleteID.add(value.toString());
                }
            }
            Window window = SwingUtilities.getWindowAncestor(Form_Supplier.this);
            Delete hapus = new Delete((Frame)window, true);
            hapus.setVisible(true);
            if(hapus.isConfirmed()){
                String sql = "DELETE FROM supplier WHERE id_supplier IN "
                        + "("+String.join(",", Collections.nCopies(DeleteID.size(),"?"))+")";
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:/sembakogrok", "root", "");
                     PreparedStatement pstmt = conn.prepareStatement(sql)){
                    for(int i = 0; i < DeleteID.size();i++){
                        pstmt.setString(i+1, DeleteID.get(i));
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

    private void jComboBox_Custom1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_Custom1ActionPerformed
        String selectedOption = (String)jComboBox_Custom1.getSelectedItem();
        UrutanData(selectedOption);
    }//GEN-LAST:event_jComboBox_Custom1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Custom.Custom_ButtonRounded Edit;
    private Custom.Custom_ButtonRounded Hapus;
    private Custom.Custom_ButtonRounded Tambah;
    private com.raven.swing.jComboBox_Custom jComboBox_Custom1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private jtextfield.TextFieldSuggestion pencarian;
    private com.raven.swing.Table1 table1;
    private com.raven.swing.Table1 table2;
    // End of variables declaration//GEN-END:variables
}
