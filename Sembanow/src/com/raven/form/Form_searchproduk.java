package com.raven.form;

import config.koneksi;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;


public class Form_searchproduk extends javax.swing.JPanel {
    public Statement st;
    public ResultSet rs;
    Connection cn = koneksi.getKoneksi();

    public Form_searchproduk() {
        initComponents();
        showData();
        setupListeners(); // Set up listeners for search and table selection
    }

    // Method to set up the listeners for search and table row selection
    private void setupListeners() {
        // Add KeyListener to the search field for filtering
        search.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterData(); // Filter table data when user types in search field
            }
        });

        // Add ListSelectionListener to the table to update text fields based on selected row
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Ensure selection is complete
                    updateTextFieldsBasedOnSelectedRow();
                }
            }
        });
    }

    // Method to filter table data based on search input (id_produk or nama)
    private void filterData() {
        String searchText = search.getText().trim();
        String[] columnNames = {"ID", "Produk", "dos", "pcs", "H1", "H2", "H3"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Modify the SQL query to filter by id_produk or nama
        String sql = "SELECT id_produk, nama, total_quantity_dos, total_quantity_pcs, harga1, harga2, harga3 " +
                     "FROM viewproduk " +
                     "WHERE id_produk LIKE ? OR nama LIKE ?";

        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            // Use wildcards for partial matching
            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern); // For id_produk
            pstmt.setString(2, searchPattern); // For nama

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getString("id_produk");
                row[1] = rs.getString("nama");
                row[2] = rs.getInt("total_quantity_dos");
                row[3] = rs.getInt("total_quantity_pcs");
                row[4] = rs.getInt("harga1");
                row[5] = rs.getInt("harga2");
                row[6] = rs.getInt("harga3");
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

    // Method to update discounth2, discounth3, and QperD based on selected row
    private void updateTextFieldsBasedOnSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) { // No row selected
            discounth2.setText("");
            discounth3.setText("");
            QperD.setText("");
            return;
        }

        // Get the id_produk from the selected row (column 0)
        String idProduk = table.getValueAt(selectedRow, 0).toString();

        // Update discounth2 and discounth3 from diskon table
        updateDiscountFields(idProduk);
        // Update QperD from produk table
        updateQperDField(idProduk);
    }

    // Helper method to fetch min_pcs from diskon table and update discounth2 and discounth3
    private void updateDiscountFields(String idProduk) {
        String sql = "SELECT min_pcs, tipe_harga FROM diskon WHERE id_produk = ?";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            pstmt.setString(1, idProduk);
            ResultSet rs = pstmt.executeQuery();

            // Reset fields
            discounth2.setText("");
            discounth3.setText("");

            // Update fields based on tipe_harga
            while (rs.next()) {
                int minPcs = rs.getInt("min_pcs");
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

    // Helper method to fetch pcs_per_dos from produk table and update QperD
    private void updateQperDField(String idProduk) {
        String sql = "SELECT pcs_per_dos FROM produk WHERE id_produk = ?";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            pstmt.setString(1, idProduk);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int pcsPerDos = rs.getInt("pcs_per_dos");
                QperD.setText(String.valueOf(pcsPerDos));
            } else {
                QperD.setText(""); // Clear if no data found
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Error loading pcs_per_dos: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Existing showData method (unchanged, included for completeness)
    public void showData() {
        String[] columnNames = {"ID", "Produk", "dos", "pcs", "H1", "H2", "H3"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String sql = "SELECT id_produk, nama, total_quantity_dos, total_quantity_pcs, harga1, harga2, harga3 " +
                     "FROM viewproduk";
        try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getString("id_produk");
                row[1] = rs.getString("nama");
                row[2] = rs.getInt("total_quantity_dos");
                row[3] = rs.getInt("total_quantity_pcs");
                row[4] = rs.getInt("harga1");
                row[5] = rs.getInt("harga2");
                row[6] = rs.getInt("harga3");
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

    

    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        card3 = new com.raven.component.Card();
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

        setBackground(new java.awt.Color(250, 250, 250));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Produk", "dos", "pcs", "H1", "H2", "H3"
            }
        ));
        jScrollPane1.setViewportView(table);

        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });

        jLabel6.setText("Pencarian");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel9.setText("-> Produk");

        jLabel7.setText("h2");

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

        jLabel8.setText("h3");

        jLabel11.setText("Q/Dos");

        QperD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                QperDActionPerformed(evt);
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(discounth2, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(discounth3, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(QperD, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                .addGap(37, 37, 37)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(discounth2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(discounth3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(QperD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jtextfield.TextFieldSuggestion QperD;
    private com.raven.component.Card card3;
    private jtextfield.TextFieldSuggestion discounth2;
    private jtextfield.TextFieldSuggestion discounth3;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private jtextfield.TextFieldSuggestion search;
    private com.raven.swing.Table1 table;
    // End of variables declaration//GEN-END:variables
}
