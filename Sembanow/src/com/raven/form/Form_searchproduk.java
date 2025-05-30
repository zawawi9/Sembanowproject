package com.raven.form;

import com.raven.swing.Table1;
import config.koneksi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import jtextfield.ComboBoxSuggestion;
import jtextfield.TextFieldSuggestion;
import raven.dialog.LengkapiData;
import raven.dialog.Loading;
import raven.dialog.Pilihdahulu;
import raven.dialog.SesuaiFormat_YYYYMMDD;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.Paper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import cetak.BarcodePrintable;
import raven.dialog.Cancelled;
import raven.dialog.FailLoaded;
import raven.dialog.FailSaved;
import raven.dialog.JumlahBarcode;
import raven.dialog.JumlahBarcodeInvalid;
import raven.dialog.PrintFailed;
import raven.dialog.Printed;

public class Form_searchproduk extends javax.swing.JPanel {

    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();

    public Form_searchproduk() {
        initComponents();
        showData1();
        setupListeners();

        Window window = SwingUtilities.getWindowAncestor(Form_searchproduk.this);
        Loading muat = new Loading((java.awt.Frame) window, true);
        muat.setVisible(true);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    return; // Tidak ada baris yang diklik, hindari error
                }
                String idProduk = table.getValueAt(selectedRow, 2).toString();
                TampilkanBarcode(idProduk);
            }
        });
    }

    private void setupListeners() {
        search.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterData();
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateTextFieldsBasedOnSelectedRow();
                }
            }
        });
        table.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    return; // No row selected, do nothing
                }

                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    String idProduk = table.getValueAt(selectedRow, 0).toString();
                    displayKartuStokTable(idProduk);
                }
            }
        });
    }

    private void TampilkanBarcode(String idProduk) {
        try {
            Code128Bean bean = new Code128Bean();
            final int dpi = 160;
            bean.setModuleWidth(0.3);
            bean.doQuietZone(false);

            File barcodeFile = new File("barcode-" + idProduk + ".png");
            FileOutputStream out = new FileOutputStream(barcodeFile);
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    out, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            bean.generateBarcode(canvas, idProduk);
            canvas.finish();

            BufferedImage barcodeImage = ImageIO.read(barcodeFile);
            ImageIcon icon = new ImageIcon(barcodeImage);
            barcode.setIcon(icon);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal generate barcode: " + e.getMessage());
        }
    }

    private void displayKartuStokTable(String idProduk) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nama");
        model.addColumn("Status");
        model.addColumn("Jml");
        model.addColumn("Stn");
        model.addColumn("Tanggal");
        model.addColumn("setelah");

        String sql = "SELECT p.nama, k.status, k.jumlah, k.satuan, k.tanggal, k.setelah "
                + "FROM kartustok k "
                + "JOIN produk p ON k.id_produk = p.id_produk "
                + "WHERE k.id_produk = ?"
                + "ORDER BY k.tanggal DESC";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            pstmt.setString(1, idProduk);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nama = rs.getString("nama");
                String status = rs.getString("status");
                int jumlah = rs.getInt("jumlah");
                String satuan = rs.getString("satuan");
                String tanggal = rs.getString("tanggal");
                String setelah = rs.getString("setelah");
                model.addRow(new Object[]{nama, status, jumlah, satuan, tanggal, setelah});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        Table1 table = new Table1();
        table.setModel(model);
        table.setPreferredScrollableViewportSize(new Dimension(600, 200));
        JScrollPane scrollPane = new JScrollPane(table);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{"OK"});
        JDialog dialog = optionPane.createDialog(this, "Kartu Stok");
        setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);
        table.fixTable(scrollPane);
        int[] columnWidths = {150, 50, 35, 35, 125, 125};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        dialog.setVisible(true);
    }

    private void updateTextFieldsBasedOnSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            discounth2.setText("");
            discounth3.setText("");
            QperD.setText("");
            table2(null);
            return;
        }

        String idProduk = table.getValueAt(selectedRow, 0).toString();
        updateDiscountFields(idProduk);
        updateQperDField(idProduk);
        table2(idProduk);

    }

    private void updateDiscountFields(String idProduk) {
        String sql = "SELECT min_pcs, tipe_harga FROM diskon WHERE id_produk = ?";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            pstmt.setString(1, idProduk);
            ResultSet rs = pstmt.executeQuery();
            discounth2.setText("");
            discounth3.setText("");
            while (rs.next()) {
                String minPcs = rs.getString("min_pcs") + " pcs";
                String tipeHarga = rs.getString("tipe_harga");
                if ("h2".equalsIgnoreCase(tipeHarga)) {
                    discounth2.setText(String.valueOf(minPcs));
                } else if ("h3".equalsIgnoreCase(tipeHarga)) {
                    discounth3.setText(String.valueOf(minPcs));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error loading discount data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateQperDField(String idProduk) {
        String sql = "SELECT pcs_per_dos FROM produk WHERE id_produk = ?";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            pstmt.setString(1, idProduk);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String pcsPerDos = rs.getString("pcs_per_dos") + " pcs";
                QperD.setText(String.valueOf(pcsPerDos));
            } else {
                QperD.setText("");
            }
        } catch (SQLException ex) {
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            Loading load = new Loading(parent, true);
            load.setVisible(true);
            FailLoaded fail = new FailLoaded(parent, true);
            fail.setVisible(true);
            ex.printStackTrace();
        }
    }

    public void table2(String idproduk) {
        DefaultTableModel model = new DefaultTableModel();
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("stok");
        model.addColumn("display");
        model.addColumn("exp");

        if (idproduk != null) {
            try {
                String sql = "SELECT e.id_produk, e.exp_date, e.quantity_dos, e.quantity_pcs, p.satuan\n"
                        + "FROM exp e\n"
                        + "JOIN produk p ON e.id_produk = p.id_produk\n"
                        + "WHERE e.id_produk = ?";
                PreparedStatement stmt = cn.prepareStatement(sql);
                stmt.setString(1, idproduk); // Set parameter idproduk
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String stok = rs.getString("quantity_dos") + " " + rs.getString("satuan");
                    String display = rs.getString("quantity_pcs") + " pcs";

                    model.addRow(new Object[]{
                        stok,
                        display,
                        rs.getString("exp_date")
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
                java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            FailLoaded load = new FailLoaded(parent, true);
            load.setVisible(true);
            }
        }

        table2.setModel(model);
        int[] columnWidths = {50, 50, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            table2.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        table2.fixTable(jScrollPane2);
    }

    private void filterData() {
        String searchText = search.getText().trim();
        String[] columnNames = {"ID", "Produk", "dos", "pcs", "H1", "H2", "H3", "Supplier"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String sql = "SELECT id_produk, nama, stok_dos, stok_pcs, h1, h2, h3, nama_supplier "
                + "FROM view_produk_stok "
                + "WHERE id_produk LIKE ? OR nama LIKE ? OR nama_supplier LIKE ?";

        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("id_produk");
                row[1] = rs.getString("nama");
                row[2] = rs.getInt("stok_dos");
                row[3] = rs.getInt("stok_pcs");
                row[4] = rs.getInt("h1");
                row[5] = rs.getInt("h2");
                row[6] = rs.getInt("h3");
                row[7] = rs.getString("nama_supplier");
                model.addRow(row);
            }
            table.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error filtering data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void showData1() {
        java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            Loading load = new Loading(parent, true);
            load.setVisible(true);
        String[] columnNames = {"ID", "Produk", "Harga1", "Harga2", "Harga3", "Supplier"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String sql = "SELECT id_produk, nama, h1, h2, h3, nama_supplier "
                + "FROM view_produk_stok";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("id_produk");
                row[1] = rs.getString("nama");
                row[2] = rs.getInt("h1");
                row[3] = rs.getInt("h2");
                row[4] = rs.getInt("h3");
                row[5] = rs.getString("nama_supplier");
                model.addRow(row);
            }
            table.setModel(model);
        } catch (SQLException ex) {
            FailLoaded fail = new FailLoaded(parent, true);
            fail.setVisible(true);
            ex.printStackTrace();
        }
    }

    private void restockProduk() {
        // UI Components
        TextFieldSuggestion id = new TextFieldSuggestion();
        TextFieldSuggestion produk = new TextFieldSuggestion();
        ComboBoxSuggestion satuan = new ComboBoxSuggestion();
        satuan.addItem("pcs");
        TextFieldSuggestion stok = new TextFieldSuggestion();
        TextFieldSuggestion hargabeli = new TextFieldSuggestion();
        TextFieldSuggestion harga1 = new TextFieldSuggestion();
        TextFieldSuggestion harga2 = new TextFieldSuggestion();
        TextFieldSuggestion harga3 = new TextFieldSuggestion();
        TextFieldSuggestion exp = new TextFieldSuggestion();

        Dimension fieldSize = new Dimension(200, 35);
        id.setPreferredSize(fieldSize);
        produk.setPreferredSize(fieldSize);
        satuan.setPreferredSize(fieldSize);
        stok.setPreferredSize(fieldSize);
        hargabeli.setPreferredSize(fieldSize);
        harga1.setPreferredSize(fieldSize);
        harga2.setPreferredSize(fieldSize);
        harga3.setPreferredSize(fieldSize);
        exp.setPreferredSize(fieldSize);

        JLabel l4 = new JLabel("ID:");
        JLabel l5 = new JLabel("Produk:");
        JLabel l6 = new JLabel("Satuan:");
        JLabel l7 = new JLabel("Jumlah:");
        JLabel l9 = new JLabel("Harga Beli:");
        JLabel l10 = new JLabel("Harga1:");
        JLabel l11 = new JLabel("Harga2:");
        JLabel l12 = new JLabel("Harga3:");
        JLabel l13 = new JLabel("Exp (yyyy-MM-dd):");

        JPanel mainPanel = new JPanel(new GridLayout(3, 3, 3, 3));
        mainPanel.add(createInputPanel(l4, id));
        mainPanel.add(createInputPanel(l5, produk));
        mainPanel.add(createInputPanel(l6, satuan));
        mainPanel.add(createInputPanel(l7, stok));
        mainPanel.add(createInputPanel(l9, hargabeli));
        mainPanel.add(createInputPanel(l10, harga1));
        mainPanel.add(createInputPanel(l11, harga2));
        mainPanel.add(createInputPanel(l12, harga3));
        mainPanel.add(createInputPanel(l13, exp));

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
        KeyListener angkasaja = new KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    evt.consume(); // Mengabaikan input jika bukan angka atau backspace
                }
            }
        };
        KeyListener hurufsaja = new KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    evt.consume(); // Mengabaikan input jika bukan angka atau backspace
                }
            }
        };
        id.addKeyListener(angkasaja);
        stok.addKeyListener(angkasaja);
        hargabeli.addKeyListener(angkasaja);
        harga1.addKeyListener(angkasaja);
        harga2.addKeyListener(angkasaja);
        harga3.addKeyListener(angkasaja);

        id.addKeyListener(enterKeyListener);
        produk.addKeyListener(enterKeyListener);
        satuan.getEditor().getEditorComponent().addKeyListener(enterKeyListener);
        stok.addKeyListener(enterKeyListener);
        hargabeli.addKeyListener(enterKeyListener);
        harga1.addKeyListener(enterKeyListener);
        harga2.addKeyListener(enterKeyListener);
        harga3.addKeyListener(enterKeyListener);
        exp.addKeyListener(enterKeyListener);

        // Get selected row from JTable and populate id and produk
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Window window = SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            Pilihdahulu pilih = new Pilihdahulu((Frame) window, true);
            pilih.setVisible(true);
            return;
        }
        id.setText(table.getValueAt(selectedRow, 0).toString().trim());
        produk.setText(table.getValueAt(selectedRow, 1).toString().trim());
        id.setEditable(false); // Make ID non-editable
        produk.setEditable(false); // Make Produk non-editable

        // Fetch satuan specific to the selected product
        String valId = id.getText().trim();
        try {
            String getSatuanSql = "SELECT satuan FROM produk WHERE id_produk = ?";
            try (PreparedStatement pstmt = cn.prepareStatement(getSatuanSql)) {
                pstmt.setInt(1, Integer.parseInt(valId));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String dbSatuan = rs.getString("satuan");
                        if (dbSatuan != null && !dbSatuan.isEmpty() && !dbSatuan.equals("pcs")) {
                            satuan.addItem(dbSatuan);
                            satuan.setSelectedItem(dbSatuan);
                        } else {
                            satuan.setSelectedItem("pcs");
                        }
                    } else {
                        satuan.setSelectedItem("pcs");
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching satuan from database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            satuan.setSelectedItem("pcs"); // Default to pcs on error
        }

        JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
        dialogPanel.add(mainPanel, BorderLayout.CENTER);
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JOptionPane optionPane = new JOptionPane(
                dialogPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );

        JDialog dialog = optionPane.createDialog(this, "Restok Produk");
        setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);
        dialog.setVisible(true);

        Object selectedValue = optionPane.getValue();
        if (selectedValue == null || (Integer) selectedValue != JOptionPane.OK_OPTION) {
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            Cancelled dibatal = new Cancelled(parent, true);
            dibatal.setVisible(true);
            return;
        }

        // Retrieve input values
        String valSatuan = satuan.getSelectedItem() != null ? satuan.getSelectedItem().toString().trim() : "pcs";
        String valStok = stok.getText().trim();
        String valHargaBeli = hargabeli.getText().trim();
        String valHarga1 = harga1.getText().trim();
        String valHarga2 = harga2.getText().trim();
        String valHarga3 = harga3.getText().trim();
        String valExp = exp.getText().trim();
        String valProduk = produk.getText().trim();

        try {
            // Validate and parse inputs
            int valStokNum = Integer.parseInt(valStok.isEmpty() ? "0" : valStok);
            int valHargaBeliNum = Integer.parseInt(valHargaBeli.isEmpty() ? "0" : valHargaBeli);
            int valHarga1Num = Integer.parseInt(valHarga1.isEmpty() ? "0" : valHarga1);
            int valHarga2Num = Integer.parseInt(valHarga2.isEmpty() ? "0" : valHarga2);
            int valHarga3Num = Integer.parseInt(valHarga3.isEmpty() ? "0" : valHarga3);

            // Mandatory fields validation
            if (valStok.isEmpty() || valExp.isEmpty() || valHargaBeli.isEmpty() || valHarga1.isEmpty()) {
                Window window = SwingUtilities.getWindowAncestor(Form_searchproduk.this);
                LengkapiData lengkapi = new LengkapiData((Frame) window, true);
                lengkapi.setVisible(true);
                return;
            }

            // Parse expiration date
            java.sql.Date expDate;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedDate = dateFormat.parse(valExp);
                expDate = new java.sql.Date(parsedDate.getTime());
            } catch (ParseException ex) {
                Window window = SwingUtilities.getWindowAncestor(Form_searchproduk.this);
                SesuaiFormat_YYYYMMDD lengkapi = new SesuaiFormat_YYYYMMDD((Frame) window, true);
                lengkapi.setVisible(true);
                return;
            }

            // Calculate quantity_dos and quantity_pcs based on satuan
            int quantityDos = 0;
            int quantityPcs = 0;
            if ("pcs".equalsIgnoreCase(valSatuan)) {
                quantityPcs = valStokNum;
            } else {
                quantityDos = valStokNum;
            }

            cn.setAutoCommit(false);

            // Update produk table
            String produkSql = "UPDATE produk SET nama = ?, harga_beli = ?, harga1 = ?, harga2 = ?, harga3 = ? WHERE id_produk = ?";
            try (PreparedStatement pstmt = cn.prepareStatement(produkSql)) {
                pstmt.setString(1, valProduk);
                pstmt.setInt(2, valHargaBeliNum);
                pstmt.setInt(3, valHarga1Num);
                pstmt.setInt(4, valHarga2Num);
                pstmt.setInt(5, valHarga3Num);
                pstmt.setInt(6, Integer.parseInt(valId));
                pstmt.executeUpdate();
            }

            // Check and update/insert exp table
            String checkExpSql = "SELECT quantity_dos, quantity_pcs FROM exp WHERE id_produk = ? AND exp_date = ?";
            boolean expExists = false;
            int existingQuantityDos = 0;
            int existingQuantityPcs = 0;
            try (PreparedStatement pstmt = cn.prepareStatement(checkExpSql)) {
                pstmt.setInt(1, Integer.parseInt(valId));
                pstmt.setDate(2, expDate);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        expExists = true;
                        existingQuantityDos = rs.getInt("quantity_dos");
                        existingQuantityPcs = rs.getInt("quantity_pcs");
                    }
                }
            }

            if (expExists) {
                String updateExpSql = "UPDATE exp SET quantity_dos = ?, quantity_pcs = ? WHERE id_produk = ? AND exp_date = ?";
                try (PreparedStatement pstmt = cn.prepareStatement(updateExpSql)) {
                    pstmt.setInt(1, existingQuantityDos + quantityDos);
                    pstmt.setInt(2, existingQuantityPcs + quantityPcs);
                    pstmt.setInt(3, Integer.parseInt(valId));
                    pstmt.setDate(4, expDate);
                    pstmt.executeUpdate();
                }
            } else {
                String insertExpSql = "INSERT INTO exp (id_produk, exp_date, quantity_dos, quantity_pcs) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = cn.prepareStatement(insertExpSql)) {
                    pstmt.setInt(1, Integer.parseInt(valId));
                    pstmt.setDate(2, expDate);
                    pstmt.setInt(3, quantityDos);
                    pstmt.setInt(4, quantityPcs);
                    pstmt.executeUpdate();
                }
            }

            // Insert into pengeluaran table
            String pengeluaranSql = "INSERT INTO pengeluaran (status, keterangan, jumlah, satuan, total) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = cn.prepareStatement(pengeluaranSql)) {
                pstmt.setString(1, "restok");
                pstmt.setString(2, valProduk);
                pstmt.setInt(3, valStokNum);
                pstmt.setString(4, valSatuan);
                pstmt.setLong(5, (long) valHargaBeliNum * valStokNum); // Calculate total as harga_beli * jumlah
                pstmt.executeUpdate();
            }

            cn.commit();
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
                                Loading load = new Loading(parent, true);
            load.setVisible(true);

        } catch (SQLException | NumberFormatException ex) {
            try {
                if (cn != null) {
                    cn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
                                FailSaved load = new FailSaved(parent, true);
            load.setVisible(true);
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

    private void addproduk() {
        TextFieldSuggestion keterangan = new TextFieldSuggestion();
        TextFieldSuggestion total = new TextFieldSuggestion();
        TextFieldSuggestion id = new TextFieldSuggestion();
        TextFieldSuggestion produk = new TextFieldSuggestion();
        ComboBoxSuggestion satuan = new ComboBoxSuggestion();
        satuan.addItem("pcs");
        satuan.addItem("dos");
        satuan.addItem("kotak");
        satuan.addItem("lusin");
        satuan.addItem("rtg");
        TextFieldSuggestion stok = new TextFieldSuggestion();
        TextFieldSuggestion qperdos = new TextFieldSuggestion();
        TextFieldSuggestion hargabeli = new TextFieldSuggestion();
        TextFieldSuggestion harga1 = new TextFieldSuggestion();
        TextFieldSuggestion harga2 = new TextFieldSuggestion();
        TextFieldSuggestion harga3 = new TextFieldSuggestion();
        TextFieldSuggestion exp = new TextFieldSuggestion();
        TextFieldSuggestion pcsh2 = new TextFieldSuggestion();
        TextFieldSuggestion pcsh3 = new TextFieldSuggestion();

        Dimension fieldSize = new Dimension(200, 35);
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

        JLabel l2 = new JLabel("Supplier:");
        JLabel l3 = new JLabel("Total:");
        JLabel l4 = new JLabel("ID:");
        JLabel l5 = new JLabel("Produk:");
        JLabel l6 = new JLabel("Satuan:");
        JLabel l7 = new JLabel("jumlah:");
        JLabel l8 = new JLabel("Q/Stok:");
        JLabel l9 = new JLabel("Harga Beli:");
        JLabel l10 = new JLabel("Harga1:");
        JLabel l11 = new JLabel("Harga2:");
        JLabel l12 = new JLabel("Harga3:");
        JLabel l13 = new JLabel("Exp (yyyy-MM-dd):");
        JLabel lh2 = new JLabel("h2:");
        JLabel lh3 = new JLabel("h3:");

        JPanel mainPanel = new JPanel(new GridLayout(3, 5, 5, 5));

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
        KeyListener angkasaja = new KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    evt.consume(); // Mengabaikan input jika bukan angka atau backspace
                }
            }
        };
        KeyListener hurufsaja = new KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    evt.consume(); // Mengabaikan input jika bukan angka atau backspace
                }
            }
        };
        id.addKeyListener(angkasaja);
        stok.addKeyListener(angkasaja);
        hargabeli.addKeyListener(angkasaja);
        harga1.addKeyListener(angkasaja);
        harga2.addKeyListener(angkasaja);
        harga3.addKeyListener(angkasaja);
        pcsh2.addKeyListener(angkasaja);
        pcsh3.addKeyListener(angkasaja);

        satuan.setEditable(true);

        // Ambil komponen editor-nya (biasanya JTextField)
        Component editor = satuan.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    stok.requestFocus();
                }
            }
        });

        keterangan.addKeyListener(enterKeyListener);
        total.addKeyListener(enterKeyListener);
        id.addKeyListener(enterKeyListener);
        produk.addKeyListener(enterKeyListener);
        stok.addKeyListener(enterKeyListener);
        qperdos.addKeyListener(enterKeyListener);
        hargabeli.addKeyListener(enterKeyListener);
        harga1.addKeyListener(enterKeyListener);
        harga2.addKeyListener(enterKeyListener);
        harga3.addKeyListener(enterKeyListener);
        exp.addKeyListener(enterKeyListener);
        pcsh2.addKeyListener(enterKeyListener);
        pcsh3.addKeyListener(enterKeyListener);

        JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
        dialogPanel.add(mainPanel, BorderLayout.CENTER);
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JOptionPane optionPane = new JOptionPane(
                dialogPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );
        JDialog dialog = optionPane.createDialog(this, "Tambah Produk");
        setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);
        dialog.setVisible(true);


        Object selectedValue = optionPane.getValue();
        if (!(selectedValue instanceof Integer) || (Integer) selectedValue != JOptionPane.OK_OPTION) {
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            Cancelled dibatal = new Cancelled(parent, true);
            dibatal.setVisible(true);
            return;
        }

        String valKeterangan = keterangan.getText().trim();
        String valTotal = total.getText().trim();
        String valId = id.getText().trim();
        String valProduk = produk.getText().trim();
        String valSatuan = satuan.getSelectedItem() != null ? satuan.getSelectedItem().toString().trim() : "";
        String valStok = stok.getText().trim();
        String valQperdos = qperdos.getText().trim();
        String valHargaBeli = hargabeli.getText().trim();
        String valHarga1 = harga1.getText().trim();
        String valHarga2 = harga2.getText().trim();
        String valHarga3 = harga3.getText().trim();
        String valExp = exp.getText().trim();
        String valPcsh2 = pcsh2.getText().trim();
        String valPcsh3 = pcsh3.getText().trim();

        try {
            long valTotalNum = Long.parseLong(valTotal.isEmpty() ? "0" : valTotal);
            int valStokNum = Integer.parseInt(valStok.isEmpty() ? "0" : valStok);
            int valQperdosNum = Integer.parseInt(valQperdos.isEmpty() ? "0" : valQperdos);
            int valHargaBeliNum = Integer.parseInt(valHargaBeli.isEmpty() ? "0" : valHargaBeli);
            int valHarga1Num = Integer.parseInt(valHarga1.isEmpty() ? "0" : valHarga1);
            int valHarga2Num = Integer.parseInt(valHarga2.isEmpty() ? "0" : valHarga2);
            int valHarga3Num = Integer.parseInt(valHarga3.isEmpty() ? "0" : valHarga3);
            int valPcsh2Num = valPcsh2.isEmpty() ? 0 : Integer.parseInt(valPcsh2);
            int valPcsh3Num = valPcsh3.isEmpty() ? 0 : Integer.parseInt(valPcsh3);

            if (valKeterangan.isEmpty() || valProduk.isEmpty() || valExp.isEmpty()) {
                java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            LengkapiData lengkapi = new LengkapiData(parent, true);
            lengkapi.setVisible(true);
                return;
            }

            java.sql.Date expDate;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedDate = dateFormat.parse(valExp);
                expDate = new java.sql.Date(parsedDate.getTime());
            } catch (ParseException ex) {
                java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            SesuaiFormat_YYYYMMDD lengkapi = new SesuaiFormat_YYYYMMDD(parent, true);
            lengkapi.setVisible(true);
                return;
            }

            cn.setAutoCommit(false);
            String status = "add item";

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

            boolean idProvided = valId != null && !valId.isEmpty();
            boolean includeSatuan = !valSatuan.equalsIgnoreCase("pcs");
            String produkSql;

            if (idProvided) {
                if (includeSatuan) {
                    produkSql = "INSERT INTO produk (id_produk, nama, harga1, harga2, harga3, harga_beli, pcs_per_dos, id_supplier, satuan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                } else {
                    produkSql = "INSERT INTO produk (id_produk, nama, harga1, harga2, harga3, harga_beli, pcs_per_dos, id_supplier) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                }
            } else {
                if (includeSatuan) {
                    produkSql = "INSERT INTO produk (nama, harga1, harga2, harga3, harga_beli, pcs_per_dos, id_supplier, satuan) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                } else {
                    produkSql = "INSERT INTO produk (nama, harga1, harga2, harga3, harga_beli, pcs_per_dos, id_supplier) VALUES (?, ?, ?, ?, ?, ?, ?)";
                }
            }

            String generatedIdProduk;
            try (PreparedStatement pstmt = cn.prepareStatement(produkSql, Statement.RETURN_GENERATED_KEYS)) {
                int paramIndex = 1;

                pstmt.setString(paramIndex++, valProduk);
                pstmt.setInt(paramIndex++, valHarga1Num);
                pstmt.setInt(paramIndex++, valHarga2Num);
                pstmt.setInt(paramIndex++, valHarga3Num);
                pstmt.setInt(paramIndex++, valHargaBeliNum);
                pstmt.setInt(paramIndex++, valQperdosNum);
                pstmt.setInt(paramIndex++, Integer.parseInt(idSupplier));

                if (includeSatuan) {
                    pstmt.setString(paramIndex++, valSatuan);
                }

                pstmt.executeUpdate();

                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedIdProduk = String.valueOf(rs.getInt(1));
                } else {
                    throw new SQLException("Failed to retrieve generated id_produk after insert.");
                }

                System.out.println("Produk berhasil ditambahkan, ID: " + generatedIdProduk);
            }

            String finalIdProduk = idProvided ? valId : generatedIdProduk;

            String insertExpSql = "INSERT INTO exp (id_produk, exp_date, quantity_dos, quantity_pcs) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = cn.prepareStatement(insertExpSql)) {
                int quantityDos = 0;
                int quantityPcs = 0;
                if ("pcs".equalsIgnoreCase(valSatuan)) {
                    quantityPcs = valStokNum;
                } else {
                    quantityDos = valStokNum;
                }

                pstmt.setInt(1, Integer.parseInt(finalIdProduk));
                pstmt.setDate(2, expDate);
                pstmt.setInt(3, quantityDos);
                pstmt.setInt(4, quantityPcs);
                pstmt.executeUpdate();
            }

            String insertDiskonSql = "INSERT INTO diskon (id_produk, min_pcs, tipe_harga) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = cn.prepareStatement(insertDiskonSql)) {
                if (valPcsh2Num > 0) {
                    pstmt.setInt(1, Integer.parseInt(finalIdProduk));
                    pstmt.setInt(2, valPcsh2Num);
                    pstmt.setString(3, "h2");
                    pstmt.executeUpdate();
                }

                if (valPcsh3Num > 0) {
                    pstmt.setInt(1, Integer.parseInt(finalIdProduk));
                    pstmt.setInt(2, valPcsh3Num);
                    pstmt.setString(3, "h3");
                    pstmt.executeUpdate();
                }
            }
            String pengeluaranSql = "INSERT INTO pengeluaran (status, keterangan, jumlah, satuan, total) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = cn.prepareStatement(pengeluaranSql)) {
                pstmt.setString(1, status);
                pstmt.setString(2, valProduk);
                pstmt.setLong(3, valStokNum);
                pstmt.setString(2, valSatuan);
                pstmt.setLong(3, valTotalNum);
                pstmt.executeUpdate();
            }

            cn.commit();

        } catch (SQLException | NumberFormatException ex) {
            try {
                if (cn != null) {
                    cn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            FailSaved gagal = new FailSaved(parent, true);
            gagal.setVisible(true);
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

    private void editProduk() {
        // UI Components
        TextFieldSuggestion id = new TextFieldSuggestion();
        TextFieldSuggestion produk = new TextFieldSuggestion();
        ComboBoxSuggestion satuan = new ComboBoxSuggestion();
        satuan.addItem("dos");
        satuan.addItem("kotak");
        satuan.addItem("lusin");
        satuan.addItem("rtg");
        TextFieldSuggestion qperdos = new TextFieldSuggestion();
        TextFieldSuggestion pcsh2 = new TextFieldSuggestion();
        TextFieldSuggestion pcsh3 = new TextFieldSuggestion();

        Dimension fieldSize = new Dimension(200, 35);
        id.setPreferredSize(fieldSize);
        produk.setPreferredSize(fieldSize);
        satuan.setPreferredSize(fieldSize);
        qperdos.setPreferredSize(fieldSize);
        pcsh2.setPreferredSize(fieldSize);
        pcsh3.setPreferredSize(fieldSize);

        JLabel l4 = new JLabel("ID:");
        JLabel l5 = new JLabel("Produk:");
        JLabel l6 = new JLabel("Satuan:");
        JLabel l8 = new JLabel("Q/Stok:");
        JLabel lh2 = new JLabel("Min Pcs H2:");
        JLabel lh3 = new JLabel("Min Pcs H3:");

        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 2, 2));
        mainPanel.add(createInputPanel(l4, id));
        mainPanel.add(createInputPanel(l5, produk));
        mainPanel.add(createInputPanel(l6, satuan));
        mainPanel.add(createInputPanel(l8, qperdos));
        mainPanel.add(createInputPanel(lh2, pcsh2));
        mainPanel.add(createInputPanel(lh3, pcsh3));

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
        KeyListener angkasaja = new KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    evt.consume(); // Mengabaikan input jika bukan angka atau backspace
                }
            }
        };
        KeyListener hurufsaja = new KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    evt.consume(); // Mengabaikan input jika bukan angka atau backspace
                }
            }
        };
        id.addKeyListener(angkasaja);
        qperdos.addKeyListener(angkasaja);
        pcsh2.addKeyListener(angkasaja);
        pcsh3.addKeyListener(angkasaja);

        id.addKeyListener(enterKeyListener);
        produk.addKeyListener(enterKeyListener);
        satuan.getEditor().getEditorComponent().addKeyListener(enterKeyListener);
        qperdos.addKeyListener(enterKeyListener);
        pcsh2.addKeyListener(enterKeyListener);
        pcsh3.addKeyListener(enterKeyListener);

        // Get selected row from JTable and populate id and produk
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            Pilihdahulu pilih = new Pilihdahulu(parent, true);
            pilih.setVisible(true);
            return;
        }
        String valId = table.getValueAt(selectedRow, 0).toString().trim();
        id.setText(valId);
        produk.setText(table.getValueAt(selectedRow, 1).toString().trim());
        id.setEditable(false); // Make ID non-editable
        produk.setEditable(false); // Make Produk non-editable

        JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
        dialogPanel.add(mainPanel, BorderLayout.CENTER);
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JOptionPane optionPane = new JOptionPane(
                dialogPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );

        JDialog dialog = optionPane.createDialog(this, "Edit Produk");
        setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);
        dialog.setVisible(true);

        Object selectedValue = optionPane.getValue();
        if (selectedValue == null || (Integer) selectedValue != JOptionPane.OK_OPTION) {
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            Cancelled dibatal = new Cancelled(parent, true);
            dibatal.setVisible(true);

            return;
        }

        // Retrieve input values
        String valProduk = produk.getText().trim();
        String valSatuan = satuan.getSelectedItem() != null ? satuan.getSelectedItem().toString().trim() : "";
        String valQperdos = qperdos.getText().trim();
        String valPcsh2 = pcsh2.getText().trim();
        String valPcsh3 = pcsh3.getText().trim();

        try {
            // Validate all fields are filled
            if (valProduk.isEmpty() || valSatuan.isEmpty()
                    || valQperdos.isEmpty() || valPcsh2.isEmpty() || valPcsh3.isEmpty()) {
                Window window = SwingUtilities.getWindowAncestor(Form_searchproduk.this);
                LengkapiData lengkapi = new LengkapiData((Frame) window, true);
                lengkapi.setVisible(true);
                return;
            }

            int valQperdosNum = Integer.parseInt(valQperdos);
            int valPcsh2Num = Integer.parseInt(valPcsh2);
            int valPcsh3Num = Integer.parseInt(valPcsh3);

            cn.setAutoCommit(false);

            // Check if product exists
            boolean productExists = false;
            String checkProdukSql = "SELECT id_produk FROM produk WHERE id_produk = ?";
            try (PreparedStatement pstmt = cn.prepareStatement(checkProdukSql)) {
                pstmt.setInt(1, Integer.parseInt(valId));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        productExists = true;
                    }
                }
            }

            // Update or insert into produk table
            if (productExists) {
                String updateProdukSql = "UPDATE produk SET nama = ?, pcs_per_dos = ?, satuan = ? WHERE id_produk = ?";
                try (PreparedStatement pstmt = cn.prepareStatement(updateProdukSql)) {
                    pstmt.setString(1, valProduk);
                    pstmt.setInt(2, valQperdosNum);
                    pstmt.setString(3, valSatuan);
                    pstmt.setInt(4, Integer.parseInt(valId));
                    pstmt.executeUpdate();
                }
            } else {
                String insertProdukSql = "INSERT INTO produk (nama, pcs_per_dos, satuan) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = cn.prepareStatement(insertProdukSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, valProduk);
                    pstmt.setInt(2, valQperdosNum);
                    pstmt.setString(3, valSatuan);
                    pstmt.executeUpdate();
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            valId = String.valueOf(rs.getInt(1)); // Update valId with the generated ID
                        }
                    }
                }
            }

            // Handle diskon table (update or insert)
            String checkDiskonSql = "SELECT min_pcs FROM diskon WHERE id_produk = ? AND tipe_harga = ?";
            String updateDiskonSql = "UPDATE diskon SET min_pcs = ? WHERE id_produk = ? AND tipe_harga = ?";
            String insertDiskonSql = "INSERT INTO diskon (id_produk, min_pcs, tipe_harga) VALUES (?, ?, ?)";

            // Handle h2 discount
            try (PreparedStatement pstmt = cn.prepareStatement(checkDiskonSql)) {
                pstmt.setInt(1, Integer.parseInt(valId));
                pstmt.setString(2, "h2");
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Update existing h2 discount
                        try (PreparedStatement updatePstmt = cn.prepareStatement(updateDiskonSql)) {
                            updatePstmt.setInt(1, valPcsh2Num);
                            updatePstmt.setInt(2, Integer.parseInt(valId));
                            updatePstmt.setString(3, "h2");
                            updatePstmt.executeUpdate();
                        }
                    } else if (valPcsh2Num > 0) {
                        // Insert new h2 discount
                        try (PreparedStatement insertPstmt = cn.prepareStatement(insertDiskonSql)) {
                            insertPstmt.setInt(1, Integer.parseInt(valId));
                            insertPstmt.setInt(2, valPcsh2Num);
                            insertPstmt.setString(3, "h2");
                            insertPstmt.executeUpdate();
                        }
                    }
                }
            }

            // Handle h3 discount
            try (PreparedStatement pstmt = cn.prepareStatement(checkDiskonSql)) {
                pstmt.setInt(1, Integer.parseInt(valId));
                pstmt.setString(2, "h3");
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Update existing h3 discount
                        try (PreparedStatement updatePstmt = cn.prepareStatement(updateDiskonSql)) {
                            updatePstmt.setInt(1, valPcsh3Num);
                            updatePstmt.setInt(2, Integer.parseInt(valId));
                            updatePstmt.setString(3, "h3");
                            updatePstmt.executeUpdate();
                        }
                    } else if (valPcsh3Num > 0) {
                        // Insert new h3 discount
                        try (PreparedStatement insertPstmt = cn.prepareStatement(insertDiskonSql)) {
                            insertPstmt.setInt(1, Integer.parseInt(valId));
                            insertPstmt.setInt(2, valPcsh3Num);
                            insertPstmt.setString(3, "h3");
                            insertPstmt.executeUpdate();
                        }
                    }
                }
            }

            cn.commit();

        } catch (SQLException | NumberFormatException ex) {
            try {
                if (cn != null) {
                    cn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            FailSaved gagal = new FailSaved(parent, true);
            gagal.setVisible(true);
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
    private String notif(){
        java.awt.Frame parent = (java.awt.Frame)SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            FailSaved gagal = new FailSaved(parent, true);
            gagal.setVisible(true);
            return null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        card3 = new com.raven.component.Card();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new com.raven.swing.Table1();
        search = new jtextfield.TextFieldSuggestion();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        discounth2 = new jtextfield.TextFieldSuggestion();
        discounth3 = new jtextfield.TextFieldSuggestion();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        QperD = new jtextfield.TextFieldSuggestion();
        jScrollPane2 = new javax.swing.JScrollPane();
        table2 = new com.raven.swing.Table1();
        jLabel12 = new javax.swing.JLabel();
        Tambah = new Custom.Custom_ButtonRounded();
        Restock = new Custom.Custom_ButtonRounded();
        Edit = new Custom.Custom_ButtonRounded();
        jLabel1 = new javax.swing.JLabel();
        barcode = new javax.swing.JLabel();
        btnCetakBarcode = new Custom.Custom_ButtonRounded();

        setBackground(new java.awt.Color(250, 250, 250));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Produk", "H1", "H2", "H3", "supplier"
            }
        ));
        table.fixTable(jScrollPane1);
        jScrollPane1.setViewportView(table);

        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });

        jLabel6.setText("Pencarian");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel9.setText("stok");

        jLabel7.setText("minimal belanja Harga2");

        discounth2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discounth2ActionPerformed(evt);
            }
        });

        discounth3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discounth3ActionPerformed(evt);
            }
        });

        jLabel8.setText("minimal belanja Harga3");

        jLabel11.setText("Quantity/Stok");

        QperD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                QperDActionPerformed(evt);
            }
        });

        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "stok", "satuan", "display", "satuan", "exp"
            }
        ));
        table2.fixTable(jScrollPane2);
        jScrollPane2.setViewportView(table2);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel12.setText(" Produk");

        Tambah.setText("Tambah");
        Tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TambahActionPerformed(evt);
            }
        });

        Restock.setText("Restock");
        Restock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RestockActionPerformed(evt);
            }
        });

        Edit.setText("Edit");
        Edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditActionPerformed(evt);
            }
        });

        jLabel1.setText("Barcode :");

        btnCetakBarcode.setText("Print");
        btnCetakBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakBarcodeActionPerformed(evt);
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
                        .addGap(0, 913, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(discounth3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                                .addComponent(discounth2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addComponent(QperD, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(Tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Restock, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCetakBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1)
                            .addComponent(barcode)
                            .addComponent(jLabel11))
                        .addGap(5, 5, 5))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(32, 32, 32)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(discounth2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(discounth3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(QperD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Restock, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Edit, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCetakBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barcode)
                .addContainerGap(160, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchActionPerformed

    private void discounth2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discounth2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_discounth2ActionPerformed

    private void discounth3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discounth3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_discounth3ActionPerformed

    private void QperDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_QperDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_QperDActionPerformed

    private void TambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TambahActionPerformed
        addproduk();
    }//GEN-LAST:event_TambahActionPerformed

    private void RestockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RestockActionPerformed
        restockProduk();
    }//GEN-LAST:event_RestockActionPerformed

    private void EditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditActionPerformed
        editProduk();
    }//GEN-LAST:event_EditActionPerformed

    private void btnCetakBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakBarcodeActionPerformed
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Window window = SwingUtilities.getWindowAncestor(Form_searchproduk.this);
            Pilihdahulu pilih = new Pilihdahulu((Frame) window, true);
            pilih.setVisible(true);
            return;
        }

        String idProduk = table.getValueAt(selectedRow, 0).toString(); // Ambil ID Produk dari kolom pertama
        String namaProduk = table.getValueAt(selectedRow, 1).toString(); // Ambil Nama Produk dari kolom kedua (sesuaikan indeks jika berbeda)

        String inputJumlah = notif();
        int jumlahCetak = 1;
        if (inputJumlah != null && !inputJumlah.trim().isEmpty()) {
            try {
                jumlahCetak = Integer.parseInt(inputJumlah.trim());
                if (jumlahCetak <= 0) {
                    Window window = SwingUtilities.getWindowAncestor(Form_searchproduk.this);
                    JumlahBarcode pilih = new JumlahBarcode((Frame) window, true);
            pilih.setVisible(true);
                    return;
                }
            } catch (NumberFormatException e) {
                Window window = SwingUtilities.getWindowAncestor(Form_searchproduk.this);
                    JumlahBarcodeInvalid pilih = new JumlahBarcodeInvalid((Frame) window, true);
            pilih.setVisible(true);
                return;
            }
        } else {
            return;
        }

        BufferedImage barcodeImageToPrint = null;
        try {
            Code128Bean bean = new Code128Bean();
            final int barcodeDPI = 300;
            bean.setModuleWidth(0.2);
            bean.doQuietZone(true);

            // Generate barcode ke BufferedImage langsung tanpa file temporer
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    baos, "image/png", barcodeDPI, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            bean.generateBarcode(canvas, idProduk); // Gunakan idProduk sebagai data barcode
            canvas.finish();
            barcodeImageToPrint = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menyiapkan barcode untuk dicetak: " + e.getMessage(), "Error Cetak", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        if (barcodeImageToPrint == null) {
            JOptionPane.showMessageDialog(this, "Gambar barcode tidak tersedia untuk dicetak.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pageFormat = job.defaultPage();

        Paper paper = new Paper();
        double A4_WIDTH_POINTS = 8.27 * 72;
        double A4_HEIGHT_POINTS = 11.69 * 72;
        paper.setSize(A4_WIDTH_POINTS, A4_HEIGHT_POINTS);
        double defaultMargin = 0.25 * 72; // 0.25 inch = 18 points
        paper.setImageableArea(defaultMargin, defaultMargin,
                A4_WIDTH_POINTS - 2 * defaultMargin,
                A4_HEIGHT_POINTS - 2 * defaultMargin);
        pageFormat.setPaper(paper);
        pageFormat.setOrientation(PageFormat.PORTRAIT);
        job.setPrintable(new BarcodePrintable(barcodeImageToPrint, idProduk, namaProduk, jumlahCetak), pageFormat);

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print(); // Lakukan pencetakan
                Window window = SwingUtilities.getWindowAncestor(Form_searchproduk.this);
                Printed berhasil = new Printed((Frame) window, true);
            berhasil.setVisible(true);
            } catch (PrinterException ex) {
                Window window = SwingUtilities.getWindowAncestor(Form_searchproduk.this);
                PrintFailed gagal = new PrintFailed((Frame) window, true);
            gagal.setVisible(true);
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnCetakBarcodeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Custom.Custom_ButtonRounded Edit;
    private jtextfield.TextFieldSuggestion QperD;
    private Custom.Custom_ButtonRounded Restock;
    private Custom.Custom_ButtonRounded Tambah;
    private javax.swing.JLabel barcode;
    private Custom.Custom_ButtonRounded btnCetakBarcode;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.raven.component.Card card3;
    private jtextfield.TextFieldSuggestion discounth2;
    private jtextfield.TextFieldSuggestion discounth3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private jtextfield.TextFieldSuggestion search;
    private com.raven.swing.Table1 table;
    private com.raven.swing.Table1 table2;
    // End of variables declaration//GEN-END:variables
}
