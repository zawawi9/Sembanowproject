package com.raven.form;

import config.koneksi;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    // Display data from the exp_near_expiry_view in the JTable
    public void showData() {
        String[] columnNames = {"ID", "Nama", "Stok Dos", "Stok Pcs", "Harga Beli", "Stok Per Dos", "Tanggal Exp"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        
        String sql = "SELECT id_exp, nama, quantity_dos, quantity_pcs, harga_beli, pcs_per_dos, exp_date " +
                     "FROM exp_near_expiry_view";
        
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("id_exp");
                row[1] = rs.getString("nama");
                row[2] = rs.getInt("quantity_dos");
                row[3] = rs.getInt("quantity_pcs");
                row[4] = rs.getInt("harga_beli");
                row[5] = rs.getInt("pcs_per_dos");
                row[6] = rs.getDate("exp_date");
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
    String[] columnNames = {"ID", "Nama", "Stok Dos", "Stok Pcs", "Harga Beli", "Stok Per Dos", "Tanggal Exp"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

    // Base SQL query
    String sql = "SELECT id_exp, nama, quantity_dos, quantity_pcs, harga_beli, pcs_per_dos, exp_date " +
                 "FROM exp_near_expiry_view " +
                 "WHERE nama LIKE ? OR DATE_FORMAT(exp_date, '%Y-%m-%d') LIKE ?";

    try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
        String searchPattern = "%" + searchText + "%";
        pstmt.setString(1, searchPattern); // For nama
        pstmt.setString(2, searchPattern); // For exp_date (formatted as YYYY-MM-DD)

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Object[] row = new Object[7];
            row[0] = rs.getInt("id_exp");
            row[1] = rs.getString("nama");
            row[2] = rs.getInt("quantity_dos");
            row[3] = rs.getInt("quantity_pcs");
            row[4] = rs.getInt("harga_beli");
            row[5] = rs.getInt("pcs_per_dos");
            row[6] = rs.getDate("exp_date");
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

    // Set up listeners for search and delete functionality
    private void setupListeners() {
        // Search functionality
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
                    return; // No row selected, do nothing
                }

                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                    // Handle DELETE key (existing functionality)
                    deleteAndTransferToPengeluaran(selectedRow);
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    // Handle ENTER key (new functionality for return and update exp_date)
                    handleReturnAndUpdateExpDate(selectedRow);
                }
            }
        });
        
        
    }
    
    private void handleReturnAndUpdateExpDate(int selectedRow) {
    int idExp = (int) table.getValueAt(selectedRow, 0);
    TextFieldSuggestion tanggalField = new TextFieldSuggestion();
    JLabel labelTanggal = new JLabel("Enter new expiration date (YYYY-MM-DD):");

    // Create panel
    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(labelTanggal);
    panel.add(tanggalField); // Add the text field to the panel

    // Create JOptionPane with custom header
    JOptionPane optionPane = new JOptionPane(
        panel,
        JOptionPane.PLAIN_MESSAGE,
        JOptionPane.OK_CANCEL_OPTION
    );

    // Create dialog
    JDialog dialog = optionPane.createDialog(this, "Update Expiration Date");

    // Set background recursively
    setBackgroundRecursively(dialog.getContentPane(), Color.WHITE);

    // Show dialog
    dialog.setVisible(true);

    // Get result from dialog
    Object selectedValue = optionPane.getValue();
    if (selectedValue == null || (Integer) selectedValue != JOptionPane.OK_OPTION) {
        JOptionPane.showMessageDialog(this, "Operasi dibatalkan.");
        return;
    }

    // Get and validate input
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

    // Parse the date string to java.sql.Date
    java.sql.Date newExpDate;
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false); // Strict parsing
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

    // Update the database
    String updateSql = "UPDATE exp SET exp_date = ? WHERE id_exp = ?";
    try (PreparedStatement pstmt = cn.prepareStatement(updateSql)) {
        pstmt.setDate(1, newExpDate); // Use the parsed java.sql.Date
        pstmt.setInt(2, idExp);
        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(
                this,
                "Expiration date updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
            // Refresh the table to reflect the change
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

// Method to set background recursively
private void setBackgroundRecursively(Container container, Color color) {
    container.setBackground(color);
    for (Component comp : container.getComponents()) {
        comp.setBackground(color);
        if (comp instanceof Container) {
            setBackgroundRecursively((Container) comp, color);
        }
    }
}

    // Delete the exp record and transfer data to pengeluaran table
    private void deleteAndTransferToPengeluaran(int selectedRow) {
        int idExp = (int) table.getValueAt(selectedRow, 0); // ID (id_exp)
        String nama = (String) table.getValueAt(selectedRow, 1); // Nama (keterangan)
        int stokDos = (int) table.getValueAt(selectedRow, 2); // Stok Dos
        int stokPcs = (int) table.getValueAt(selectedRow, 3); // Stok Pcs
        int hargaBeli = (int) table.getValueAt(selectedRow, 4); // Harga Beli
        int pcsPerDos = (int) table.getValueAt(selectedRow, 5); // Pcs Per Dos

        // Convert stokDos to pcs and calculate total pcs (jumlah)
        int totalPcs = (stokDos * pcsPerDos) + stokPcs;
        // Calculate total cost (total = harga_beli * totalPcs)
        long total = (long) hargaBeli * totalPcs;

        // Start a transaction
        try {
            cn.setAutoCommit(false);

            // Insert into pengeluaran table
            String insertPengeluaranSql = "INSERT INTO pengeluaran (status, keterangan, jumlah, total) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = cn.prepareStatement(insertPengeluaranSql)) {
                pstmt.setString(1, "exp"); // Status
                pstmt.setString(2, nama);  // Keterangan
                pstmt.setInt(3, totalPcs); // Jumlah (total pcs)
                pstmt.setLong(4, total);   // Total (cost)
                pstmt.executeUpdate();
            }

            // Delete from exp table
            String deleteExpSql = "DELETE FROM exp WHERE id_exp = ?";
            try (PreparedStatement pstmt = cn.prepareStatement(deleteExpSql)) {
                pstmt.setInt(1, idExp);
                pstmt.executeUpdate();
            }

            cn.commit(); // Commit the transaction
            JOptionPane.showMessageDialog(null,
                "Data transferred to pengeluaran and deleted from exp successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            // Refresh the table
            showData();

        } catch (SQLException ex) {
            try {
                cn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(null,
                "Error processing data: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                cn.setAutoCommit(true); // Re-enable autocommit
            } catch (SQLException finallyEx) {
                finallyEx.printStackTrace();
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
