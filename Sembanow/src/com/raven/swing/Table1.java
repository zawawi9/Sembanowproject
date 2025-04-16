package com.raven.swing;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Table1 extends JTable {

    public Table1() {
        setShowHorizontalLines(true);
        setGridColor(new Color(230, 230, 230));
        setRowHeight(40);
        getTableHeader().setReorderingAllowed(false);

        // Renderer untuk header
        getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                TableHeader header = new TableHeader(o + "");
                header.setBackground(new Color(0, 100, 100)); // Warna lebih gelap dari Card
                header.setForeground(Color.WHITE); // Teks putih agar kontras
                if (i1 == 0) { // Kolom pertama (indeks 0) di-center
                    header.setHorizontalAlignment(JLabel.CENTER);
                }
                return header;
            }
        });

        // Renderer untuk isi tabel
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean selected, boolean focus, int i, int i1) {
                Component com = super.getTableCellRendererComponent(jtable, o, selected, focus, i, i1);
                setBorder(noFocusBorder);
                com.setForeground(new Color(102, 102, 102));
                if (selected) {
                    com.setBackground(new Color(239, 244, 255));
                } else {
                    com.setBackground(Color.WHITE);
                }
                // Kolom pertama (indeks 0) di-center
                if (i1 == 0) {
                    ((JLabel) com).setHorizontalAlignment(JLabel.CENTER);
                } else {
                    ((JLabel) com).setHorizontalAlignment(JLabel.LEFT); // Sisanya default (kiri)
                }
                return com;
            }
        });
    }

    // Menonaktifkan edit secara langsung
    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // Tabel tidak bisa diedit
    }

    // Menambahkan baris dengan data String
    public void addRow(String[] row) {
        DefaultTableModel mod = (DefaultTableModel) getModel();
        mod.addRow(row);
    }

    // Fix tampilan JScrollPane
    public void fixTable(JScrollPane scroll) {
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setVerticalScrollBar(new ScrollBar());
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        scroll.setBorder(new EmptyBorder(5, 10, 5, 10));
    }
}