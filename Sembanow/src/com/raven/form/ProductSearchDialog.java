package com.raven.form;

import com.raven.swing.Table1;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*; // <-- Pastikan ini diimpor untuk 'Frame'
import java.awt.event.*;
import java.sql.*;
import config.koneksi;
import jtextfield.TextFieldSuggestion;

public class ProductSearchDialog extends JDialog {

    private TextFieldSuggestion jtxSearch;
    private Connection cn;
    private Table1 table;
    private DefaultTableModel tableModel;
    private String selectedId = "";
    public Statement st;
    public ResultSet rs;

    // UBAH BARIS INI: dari 'JFrame parent' menjadi 'java.awt.Frame parent'
    public ProductSearchDialog(java.awt.Frame parent, Connection cn) {
        super(parent, "Cari Produk", true);
        this.cn = cn;
        setSize(600, 400); // Fixed dialog size
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Inisialisasi Statement
        try {
            st = cn.createStatement();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Koneksi gagal: " + e.getMessage());
            // Anda mungkin ingin membuang dialog ini jika koneksi gagal
            dispose();
            return;
        }

        // Main content panel to center everything
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around the content

        // Panel untuk pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); // Minimal gap
        jtxSearch = new TextFieldSuggestion();
        jtxSearch.setPreferredSize(new Dimension(300, 30));
        searchPanel.add(new JLabel("Cari Nama: "));
        searchPanel.add(jtxSearch);

        // Tabel untuk data produk
        String[] columns = {"id_produk", "nama", "stok_dos", "stok_pcs", "pcs_per_dos"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new Table1();
        table.setModel(tableModel);
        JScrollPane jScrollPane1 = new JScrollPane(table);
        table.fixTable(jScrollPane1); // Add fixTable method call

        // Combine search panel and table with minimal gap
        JPanel contentPanel = new JPanel(new BorderLayout(0, 5)); // 5px vertical gap between search and table
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(jScrollPane1, BorderLayout.CENTER);

        // Add content to main panel to keep it centered
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // Background putih bersih
        setBackgroundRecursively(this.getContentPane(), Color.WHITE);

        // Load data dari database
        loadDataFromDatabase();

        // Filter berdasarkan pencarian
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        jtxSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = jtxSearch.getText().toLowerCase();
                if (searchText.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    // Filter berdasarkan nama (kolom index 1)
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 1));
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (table.getRowCount() > 0) {
                        table.requestFocus();
                        table.setRowSelectionInterval(0, 0);
                    }
                }
            }
        });

        // Aksi Enter pada tabel
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedId = table.getValueAt(selectedRow, 0).toString();
                        dispose();
                    }
                }
            }
        });

        // Aksi Double Click pada tabel
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedId = table.getValueAt(selectedRow, 0).toString();
                        dispose();
                    }
                }
            }
        });

        // ESC untuk batal
        getRootPane().registerKeyboardAction(e -> {
            selectedId = ""; // Mengatur ID kosong jika dibatalkan
            dispose();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Fokuskan ke jtxSearch saat dialog muncul
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                jtxSearch.requestFocusInWindow(); // Gunakan requestFocusInWindow()
            }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void loadDataFromDatabase() {
        // Hati-hati, Statement st diinisialisasi di konstruktor.
        // Sebaiknya gunakan PreparedStatement di sini atau pastikan st tidak null.
        // Lebih baik lagi, buat connection dan statement lokal jika tidak sering digunakan.
        // Atau pastikan st diinisialisasi hanya sekali dan ditutup saat dialog ditutup.
        try {
            // Gunakan PreparedStatement untuk keamanan dan kinerja lebih baik
            String query = "SELECT id_produk, nama, stok_dos, stok_pcs, pcs_per_dos FROM view_produk_stok";
            // Jika st sudah diinisialisasi di konstruktor dan tidak null:
            rs = st.executeQuery(query); // Menggunakan Statement yang sudah ada

            // Atau, cara yang lebih robust jika st bisa null atau perlu Statement baru:
            // try (Statement localSt = cn.createStatement();
            //      ResultSet localRs = localSt.executeQuery(query)) {
            //     while (localRs.next()) {
            //         tableModel.addRow(new Object[]{
            //             localRs.getString("id_produk"),
            //             localRs.getString("nama"),
            //             localRs.getString("stok_dos"),
            //             localRs.getString("stok_pcs"),
            //             localRs.getString("pcs_per_dos")
            //         });
            //     }
            // }
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("id_produk"),
                    rs.getString("nama"),
                    rs.getString("stok_dos"),
                    rs.getString("stok_pcs"),
                    rs.getString("pcs_per_dos")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error memuat data: " + e.getMessage());
            e.printStackTrace(); // Penting untuk melihat stack trace
        }
    }

    public String getSelectedId() {
        return selectedId;
    }

    private void setBackgroundRecursively(Container container, Color color) {
        container.setBackground(color);
        for (Component comp : container.getComponents()) {
            if (comp instanceof JScrollPane) {
                comp.setBackground(color);
                ((JScrollPane) comp).getViewport().setBackground(color);
            } else {
                comp.setBackground(color);
                if (comp instanceof Container) {
                    setBackgroundRecursively((Container) comp, color);
                }
            }
        }
    }
}
