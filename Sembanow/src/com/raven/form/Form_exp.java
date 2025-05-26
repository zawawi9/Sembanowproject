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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import jtextfield.TextFieldSuggestion;

public class Form_exp extends javax.swing.JPanel {
    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();

    public Form_exp() {
        initComponents();
        showData(); // Load the data
        setupListeners(); // Set up listeners for search and delete
    }

    public void showData() {
        String[] columnNames = {"Kode","ID", "Nama", "Stok Dos", "Stok Pcs", "Harga Beli", "Stok Per Dos", "Tanggal Exp"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        
        String sql = "SELECT id_exp, id_produk, nama, quantity_dos, quantity_pcs, harga_beli, pcs_per_dos, exp_date " +
                     "FROM exp_near_expiry_view";
        
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getInt("id_exp");
                row[1] = rs.getInt("id_produk");
                row[2] = rs.getString("nama");
                row[3] = rs.getInt("quantity_dos");
                row[4] = rs.getInt("quantity_pcs");
                row[5] = rs.getInt("harga_beli");
                row[6] = rs.getInt("pcs_per_dos");
                row[7] = rs.getDate("exp_date");
                model.addRow(row);
            }
            table.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Error loading data: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void filterData() {
    String searchText = search.getText().trim();
    String[] columnNames = {"Kode","ID", "Nama", "Stok Dos", "Stok Pcs", "Harga Beli", "Stok Per Dos", "Tanggal Exp"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

    // Base SQL query
    String sql = "SELECT id_exp,id_produk, nama, quantity_dos, quantity_pcs, harga_beli, pcs_per_dos, exp_date " +
                 "FROM exp_near_expiry_view " +
                 "WHERE id_produk LIKE ? OR nama LIKE ? OR DATE_FORMAT(exp_date, '%Y-%m-%d') LIKE ?";

    try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
        String searchPattern = "%" + searchText + "%";
        pstmt.setString(1, searchPattern); // For nama
        pstmt.setString(2, searchPattern); // For exp_date (formatted as YYYY-MM-DD)
        pstmt.setString(3, searchPattern); // For exp_date (formatted as YYYY-MM-DD)

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Object[] row = new Object[8];
            row[0] = rs.getInt("id_exp");
            row[1] = rs.getInt("id_produk");
            row[2] = rs.getString("nama");
            row[3] = rs.getInt("quantity_dos");
            row[4] = rs.getInt("quantity_pcs");
            row[5] = rs.getInt("harga_beli");
            row[6] = rs.getInt("pcs_per_dos");
            row[7] = rs.getDate("exp_date");
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

    private void setupListeners() {
        search.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterData();
            }
        });

        table.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    return; 
                }

                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                    deleteAndTransferToPengeluaran(selectedRow);
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    handleReturnAndUpdateExpDate(selectedRow);
                }
            }
        });
        
        
    }
    
    private void handleReturnAndUpdateExpDate(int selectedRow) {
    int idExp = (int) table.getValueAt(selectedRow, 0);
    TextFieldSuggestion tanggalField = new TextFieldSuggestion();
    JLabel labelTanggal = new JLabel("Enter new expiration date (YYYY-MM-DD):");
    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(labelTanggal);
    panel.add(tanggalField); 
    JOptionPane optionPane = new JOptionPane(
        panel,
        JOptionPane.PLAIN_MESSAGE,
        JOptionPane.OK_CANCEL_OPTION
    );

    JDialog dialog = optionPane.createDialog(this, "Update Expiration Date");

    setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);

    dialog.setVisible(true);

    Object selectedValue = optionPane.getValue();
    if (selectedValue == null || (Integer) selectedValue != JOptionPane.OK_OPTION) {
        JOptionPane.showMessageDialog(this, "Operasi dibatalkan.");
        return;
    }
    String newExpDateStr = tanggalField.getText().trim();
    if (newExpDateStr.isEmpty()) {
        JOptionPane.showMessageDialog(
            this,
            "Tanggal tidak boleh kosong!",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    java.sql.Date newExpDate;
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false); 
        java.util.Date parsedDate = sdf.parse(newExpDateStr);
        newExpDate = new java.sql.Date(parsedDate.getTime());
    } catch (ParseException ex) {
        JOptionPane.showMessageDialog(
            this,
            "Format tanggal tidak valid! Gunakan YYYY-MM-DD.",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    String updateSql = "UPDATE exp SET exp_date = ? WHERE id_exp = ?";
    try (PreparedStatement pstmt = cn.prepareStatement(updateSql)) {
        pstmt.setDate(1, newExpDate); 
        pstmt.setInt(2, idExp);
        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(
                this,
                "Expiration date updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
            showData();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to update expiration date. Record not found.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
            this,
            "Error updating expiration date: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        ex.printStackTrace();
    }
}

private void deleteAndTransferToPengeluaran(int selectedRow) {
    // Extract data from the selected row
    int idExp = (int) table.getValueAt(selectedRow, 0); 
    String nama = (String) table.getValueAt(selectedRow, 2); 
    int stokDos = (int) table.getValueAt(selectedRow, 3); 
    int stokPcs = (int) table.getValueAt(selectedRow, 4); 
    int hargaBeli = (int) table.getValueAt(selectedRow, 5); 
    int pcsPerDos = (int) table.getValueAt(selectedRow, 6); 
    Object dateObj = table.getValueAt(selectedRow, 7);
    String tanggalExp;
    if (dateObj instanceof java.sql.Date) {
        tanggalExp = dateObj.toString(); // Converts java.sql.Date to String
    } else {
        tanggalExp = dateObj.toString(); // Fallback
    }
    String satuan = "pcs"; 

    // Initialize input fields
    TextFieldSuggestion jtxId = new TextFieldSuggestion();
    TextFieldSuggestion jtxNama = new TextFieldSuggestion();
    TextFieldSuggestion jtxStokDos = new TextFieldSuggestion();
    TextFieldSuggestion jtxStokPcs = new TextFieldSuggestion();
    TextFieldSuggestion jtxStokBuang = new TextFieldSuggestion();
    TextFieldSuggestion jtxDisplayBuang = new TextFieldSuggestion();

    // Set field sizes
    Dimension fieldSize = new Dimension(200, 35);
    jtxId.setPreferredSize(fieldSize);
    jtxNama.setPreferredSize(fieldSize);
    jtxStokDos.setPreferredSize(fieldSize);
    jtxStokPcs.setPreferredSize(fieldSize);
    jtxStokBuang.setPreferredSize(fieldSize);
    jtxDisplayBuang.setPreferredSize(fieldSize);

    // Set labels
    JLabel lId = new JLabel("ID:");
    JLabel lNama = new JLabel("Nama:");
    JLabel lStokDos = new JLabel("Stok Dos:");
    JLabel lStokPcs = new JLabel("Stok Pcs:");
    JLabel lStokBuang = new JLabel("Stok yang Dibuang:");
    JLabel lDisplayBuang = new JLabel("Display yang Dibuang:");

    // Prefill fields with table data and disable all except jtxStokBuang & jtxDisplayBuang
    jtxId.setText(String.valueOf(idExp));
    jtxId.setEnabled(false);
    jtxNama.setText(nama);
    jtxNama.setEnabled(false);
    jtxStokDos.setText(String.valueOf(stokDos));
    jtxStokDos.setEnabled(false);
    jtxStokPcs.setText(String.valueOf(stokPcs));
    jtxStokPcs.setEnabled(false);
    jtxStokBuang.setText("0"); // Default to 0
    jtxDisplayBuang.setText("0"); // Default to 0

    // Create the main panel for the dialog
    JPanel mainPanel = new JPanel(new GridLayout(3, 2, 2, 2));
    mainPanel.add(createInputPanel(lId, jtxId));
    mainPanel.add(createInputPanel(lNama, jtxNama));
    mainPanel.add(createInputPanel(lStokDos, jtxStokDos));
    mainPanel.add(createInputPanel(lStokPcs, jtxStokPcs));
    mainPanel.add(createInputPanel(lStokBuang, jtxStokBuang));
    mainPanel.add(createInputPanel(lDisplayBuang, jtxDisplayBuang));

    // Create the dialog panel
    JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
    dialogPanel.add(mainPanel, BorderLayout.CENTER);
    dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Create the dialog
    JOptionPane optionPane = new JOptionPane(
        dialogPanel,
        JOptionPane.PLAIN_MESSAGE,
        JOptionPane.OK_CANCEL_OPTION
    ) {
        @Override
        public void selectInitialValue() {
            jtxStokBuang.requestFocusInWindow();
        }
    };

    JDialog dialog = optionPane.createDialog(this, "Buang Produk");
    setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);

    // Add key listener to handle Enter key navigation
    jtxStokBuang.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyPressed(java.awt.event.KeyEvent e) {
            if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                e.consume(); // Prevent default Enter action
                jtxDisplayBuang.requestFocusInWindow(); // Move focus to next field
            }
        }
    });

    

    dialog.setVisible(true);

    // Handle dialog result
    Object selectedValue = optionPane.getValue();
    if (selectedValue == null || (Integer) selectedValue != JOptionPane.OK_OPTION) {
        String message = (selectedValue != null && (Integer) selectedValue == JOptionPane.CLOSED_OPTION)
            ? "Dialog ditutup. Operasi dibatalkan."
            : "Operasi dibatalkan.";
        JOptionPane.showMessageDialog(this, message);
        return;
    }

    // Get user input for quantities to discard
    String stokBuangStr = jtxStokBuang.getText().trim();
    String displayBuangStr = jtxDisplayBuang.getText().trim();

    try {
        int stokBuang = 0;
        int displayBuang = 0;
        if (!stokBuangStr.isEmpty()) stokBuang = Integer.parseInt(stokBuangStr);
        if (!displayBuangStr.isEmpty()) displayBuang = Integer.parseInt(displayBuangStr);
        if (stokBuangStr.isEmpty() && displayBuangStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada jumlah yang dibuang.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (stokBuang < 0 || displayBuang < 0) {
            JOptionPane.showMessageDialog(this, "Jumlah yang dibuang tidak boleh negatif.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (stokBuang > stokDos || displayBuang > stokPcs) {
            JOptionPane.showMessageDialog(this, "Jumlah yang dibuang melebihi stok yang tersedia.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int totalPcsBuang = (stokBuang * pcsPerDos) + displayBuang;
        long totalCost = (long) hargaBeli * totalPcsBuang;

        cn.setAutoCommit(false);

        if (totalPcsBuang > 0) {
            String insertPengeluaranSql = "INSERT INTO pengeluaran (status, keterangan, jumlah, total, satuan) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = cn.prepareStatement(insertPengeluaranSql)) {
                pstmt.setString(1, "exp");
                pstmt.setString(2, nama);
                pstmt.setInt(3, totalPcsBuang);
                pstmt.setLong(4, totalCost);
                pstmt.setString(5, satuan);
                pstmt.executeUpdate();
            }
        }

        if (stokBuang == stokDos && displayBuang == stokPcs) {
            String deleteExpSql = "DELETE FROM exp WHERE id_exp = ?";
            try (PreparedStatement pstmt = cn.prepareStatement(deleteExpSql)) {
                pstmt.setInt(1, idExp);
                pstmt.executeUpdate();
            }
        } else if (stokBuang > 0 || displayBuang > 0) {
            String updateExpSql = "UPDATE exp SET quantity_dos = quantity_dos - ?, quantity_pcs = quantity_pcs - ? WHERE id_exp = ? AND exp_date = ?";
            try (PreparedStatement pstmt = cn.prepareStatement(updateExpSql)) {
                pstmt.setInt(1, stokBuang);
                pstmt.setInt(2, displayBuang);
                pstmt.setInt(3, idExp);
                pstmt.setString(4, tanggalExp);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Tidak ada data yang diperbarui di tabel exp.");
                }
            }
        }

        cn.commit();
        JOptionPane.showMessageDialog(this, "Operasi berhasil dilakukan!", "Success", JOptionPane.INFORMATION_MESSAGE);
        showData(); 

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


    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        card3 = new com.raven.component.Card();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new com.raven.swing.Table1();
        search = new jtextfield.TextFieldSuggestion();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(250, 250, 250));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Produk", "H1", "H2", "H3"
            }
        ));
        table.fixTable(jScrollPane1);
        jScrollPane1.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(4).setResizable(false);
        }

        search.setText("almil");
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });

        jLabel6.setText("Pencarian");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel9.setText("-> Produk");

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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 781, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.component.Card card3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private jtextfield.TextFieldSuggestion search;
    private com.raven.swing.Table1 table;
    // End of variables declaration//GEN-END:variables
}
