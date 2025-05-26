package cetak;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.io.IOException;
import java.math.BigDecimal;

// --- IMPORT KELAS KONEKSI ANDA ---
import config.koneksi; // Import kelas koneksi Anda
// -------------------------------

// Import StrukItem dan StrukEscposDesigner (pastikan di package yang sama
// atau diimport dengan benar jika menggunakan package lain)
// import com.your_package.StrukItem;
// import com.your_package.StrukEscposDesigner;


/**
 * Kelas utama untuk mengambil data penjualan dari database
 * menggunakan koneksi yang sudah ada dari kelas config.koneksi
 * dan mencetak struk menggunakan ESC/POS.
 * Sekarang dengan metode yang bisa dipanggil dari luar (misal dari form).
 */
public class PenjualanPrinterApp {

    // Query SQL ASLI yang kamu berikan, DIUBAH agar menggunakan parameter (?)
    private static final String STRUK_QUERY_BY_ID =
        "SELECT " +
        " p.id_penjualan," +
        " pel.nama AS nama_pelanggan," +
        " p.tanggal," +
        " k.username AS nama_kasir," +
        " pr.nama AS nama_produk," +
        " t.jumlah_produk," +
        " t.harga_satuan," +
        " t.total AS total_per_item," +
        " p.total_keseluruhan," +
        " p.bayar," +
        " p.kembalian " + // Perhatikan spasi di akhir baris
        "FROM penjualan p " +
        "JOIN pelanggan pel ON p.id_pelanggan = pel.id_pelanggan " +
        "JOIN karyawan k ON p.id_karyawan = k.id_karyawan " +
        "JOIN transaksi t ON p.id_penjualan = t.id_penjualan " +
        "JOIN produk pr ON t.id_produk = pr.id_produk " +
        "WHERE p.id_penjualan = ?" +
        "ORDER BY t.id_transaksi ASC;"; // Tanpa \n di akhir jika tidak perlu baris kosong


    // Kelas Helper untuk menampung data yang dibutuhkan untuk struk
    // Tetap sama seperti versi sebelumnya yang disederhanakan
    static class StrukData {
        // --- Data dari DB (sesuai query asli) ---
        String idPenjualan; // Bill
        String namaPelanggan; // Diambil tapi tidak dicetak di struk gambar ini
        Date tanggal;
        String namaKasir; // Waiter
        List<StrukItem> items; // Dari transaksi & produk
        BigDecimal totalKeseluruhan; // GRAND TOTAL
        BigDecimal bayar;
        BigDecimal kembalian;

        // --- Data Dihitung ---
        BigDecimal subtotalSebelumPajak; // Sum dari item.subTotal

        // --- Info Toko & Ucapan Terima Kasih (kustom, harus diisi manual atau dari config) ---
        String namaToko = "Nama Toko Anda"; // <<< GANTI DI SINI ATAU SETELAH GET DATA
        String alamatToko = "Alamat Lengkap Toko Anda"; // <<< GANTI DI SINI ATAU SETELAH GET DATA
        String teleponToko = "0812-3456-7890"; // <<< GANTI DI SINI ATAU SETELAH GET DATA (Diasumsikan ada bersama info toko)
        String pesanTerimaKasih = "Terima Kasih Telah Berbelanja!"; // <<< GANTI DI SINI ATAU SETELAH GET DATA
    }


    /**
     * Mengambil data penjualan dari database berdasarkan ID menggunakan koneksi yang sudah ada.
     * Hanya mengambil kolom-kolom yang ada di STRUK_QUERY_BY_ID.
     *
     * @param conn Objek Connection JDBC yang sudah terbuka. Metode ini TIDAK menutup Connection.
     * @param idPenjualan ID penjualan yang ingin diambil datanya.
     * @return Objek StrukData yang berisi data lengkap sesuai query, atau null jika gagal atau tidak ada data.
     * @throws SQLException Jika terjadi error saat eksekusi query database.
     */
    public StrukData getStrukDataById(Connection conn, String idPenjualan) throws SQLException {
        StrukData data = new StrukData();
        data.items = new ArrayList<>();
        BigDecimal itemsSubtotalSum = BigDecimal.ZERO;

        // Menggunakan PreparedStatement untuk query dengan parameter
        try (PreparedStatement pstmt = conn.prepareStatement(STRUK_QUERY_BY_ID)) {

            // Set parameter ID Penjualan
            pstmt.setString(1, idPenjualan); // Sesuaikan setString jika id_penjualan di DB bukan tipe String

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean isFirstRow = true;

                while (rs.next()) {
                    if (isFirstRow) {
                        data.idPenjualan = rs.getString("id_penjualan");
                        data.tanggal = rs.getTimestamp("tanggal");
                        data.namaPelanggan = rs.getString("nama_pelanggan");
                        data.namaKasir = rs.getString("nama_kasir");
                        data.totalKeseluruhan = rs.getBigDecimal("total_keseluruhan");
                        data.bayar = rs.getBigDecimal("bayar");
                        data.kembalian = rs.getBigDecimal("kembalian");

                        isFirstRow = false;
                    }

                    String namaProduk = rs.getString("nama_produk");
                    int jumlahProduk = rs.getInt("jumlah_produk");
                    BigDecimal hargaSatuan = rs.getBigDecimal("harga_satuan");
                    BigDecimal totalPerItem = rs.getBigDecimal("total_per_item");

                    StrukItem item = new StrukItem(namaProduk, jumlahProduk, hargaSatuan, totalPerItem);
                    data.items.add(item);

                    itemsSubtotalSum = itemsSubtotalSum.add(item.getSubTotal());
                }

                data.subtotalSebelumPajak = itemsSubtotalSum;

            } // ResultSet otomatis ditutup

        } // PreparedStatement otomatis ditutup
        // Connection TIDAK ditutup

        if (data.items.isEmpty() || data.idPenjualan == null) {
             System.out.println("Tidak ada data penjualan ditemukan untuk ID: " + idPenjualan);
             return null;
        }

        return data;
    }


    /**
     * Metode PUBLIK yang bisa dipanggil dari form atau bagian aplikasi lain
     * untuk mencetak struk sebuah transaksi berdasarkan ID Penjualan.
     *
     * @param idPenjualan ID penjualan yang struknya ingin dicetak.
     * @param printerPort Nama port printer thermal.
     * @param isNetworkPrinter True jika printer jaringan, false jika port lokal.
     * @return true jika proses cetak berhasil dimulai (tidak ada error fatal sebelum mengirim data), false jika gagal.
     */
    public boolean cetakStrukUntukTransaksi(String idPenjualan, String printerPort, boolean isNetworkPrinter) {
        StrukData dataStruk = null;
        Connection connection = null;
        boolean success = false;

        try {
            // --- Langkah 1: Dapatkan Koneksi BARU dari kelas config.koneksi ---
            System.out.println("Mencoba mendapatkan koneksi database untuk mencetak struk...");
            connection = config.koneksi.getConnection(); // Gunakan metode getConnection() dari kelas koneksi Anda
            System.out.println("Koneksi database berhasil diperoleh.");

            // --- Langkah 2: Ambil data struk menggunakan koneksi tersebut berdasarkan ID ---
            System.out.println("Mengambil data struk untuk ID: " + idPenjualan + "...");
            dataStruk = getStrukDataById(connection, idPenjualan);

            // --- Langkah 3: Jika data berhasil diambil, isi data kustom dan cetak ---
            if (dataStruk != null) {
                 System.out.println("Data struk berhasil diambil. Mengisi data kustom dan melanjutkan pencetakan...");

                 // --- ISI DATA KUSTOM NON-DB DI SINI ---
                 // Ini akan menimpa nilai default di StrukData
                 dataStruk.namaToko = "TOKO SEMBANOW"; // <<< ISI NAMA TOKO
                 dataStruk.alamatToko = "Jl. Diponegoro No. 100, Jember"; // <<< ISI ALAMAT TOKO
                 dataStruk.teleponToko = "081231242000"; // <<< ISI TELEPON TOKO
                 dataStruk.pesanTerimaKasih = "Terima Kasih! Belanja Lagi Ya!"; // <<< ISI UCAPAN TERIMA KASIH
                 // ------------------------------------


                 // --- Konfigurasi Printer (bisa juga diterima sebagai parameter metode ini) ---
                 // Kamu bisa pindahkan konfigurasi printerPort & isNetworkPrinter ke luar method ini
                 // jika ingin lebih fleksibel dari form pemanggil.
                 // String printerPort = "NAMA_PORT_PRINTER_ANDA"; // Contoh: "LPT1" atau "/dev/usb/lp0"
                 // boolean isNetworkPrinter = false; // Ganti false menjadi true jika printernya Ethernet/WiFi
                 // -----------------------------------------------------------------------


                 StrukEscposDesigner strukDesigner = new StrukEscposDesigner();

                 try {
                      // 1. Bangun byte array ESC/POS dari data yang lengkap
                      byte[] strukBytes = strukDesigner.buildFullStruk(
                          dataStruk.namaToko,
                          dataStruk.alamatToko,
                          dataStruk.teleponToko,
                          dataStruk.idPenjualan,
                          dataStruk.namaKasir,
                          dataStruk.tanggal,
                          dataStruk.items,
                          dataStruk.subtotalSebelumPajak,
                          dataStruk.totalKeseluruhan,
                          dataStruk.bayar,
                          dataStruk.kembalian,
                          dataStruk.pesanTerimaKasih
                      );

                      // 2. Kirim byte array ke printer
                      strukDesigner.sendToPrinter(strukBytes, printerPort, isNetworkPrinter);
                      success = true; // Set sukses jika pengiriman dimulai tanpa IOException

                 } catch (IOException e) {
                      System.err.println("Gagal membuat atau mengirim struk ESC/POS: " + e.getMessage());
                      e.printStackTrace();
                      // Dalam aplikasi GUI, mungkin tampilkan JOptionPane error
                 }

            } else {
                 System.out.println("Data struk tidak ditemukan untuk ID: " + idPenjualan + ". Tidak mencetak.");
                 // Dalam aplikasi GUI, mungkin tampilkan JOptionPane info "Data tidak ditemukan"
            }

        } catch (SQLException e) {
            System.err.println("Error SQL saat mendapatkan koneksi atau menjalankan query: " + e.getMessage());
            e.printStackTrace();
            // Dalam aplikasi GUI, mungkin tampilkan JOptionPane error
        } catch (Exception e) { // Tangkap exception umum lainnya
             System.err.println("Terjadi error tak terduga selama proses cetak: " + e.getMessage());
             e.printStackTrace();
             // Dalam aplikasi GUI, mungkin tampilkan JOptionPane error
        } finally {
            // --- Pastikan Connection ditutup di sini ---
            if (connection != null) {
                try {
                    connection.close();
                     System.out.println("Koneksi database ditutup setelah proses cetak.");
                } catch (SQLException e) {
                    System.err.println("Gagal menutup koneksi database setelah proses cetak: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return success; // Kembalikan status sukses
    }


    /**
     * Metode main hanya sebagai contoh penggunaan (misalnya untuk testing).
     * Dalam aplikasi nyata, logika ini akan dipanggil dari event handler di form.
     */
    public static void main(String[] args) {
        PenjualanPrinterApp app = new PenjualanPrinterApp();

        // <<< GANTI ID PENJUALAN CONTOH INI DENGAN ID YANG VALID DI DB ANDA >>>
        String idPenjualanUntukDicetak = "ID_PENJUALAN_ANDA"; // Contoh ID
        // -----------------------------------------------------------------

        // <<< GANTI KONFIGURASI PRINTER INI >>>
        String printerPort = "LPT1"; // Contoh: "LPT1" atau "/dev/usb/lp0"
        boolean isNetworkPrinter = false; // Ganti false jika printer jaringan
        // ------------------------------------

        System.out.println("Memulai proses cetak struk untuk ID: " + idPenjualanUntukDicetak);
        boolean cetakBerhasil = app.cetakStrukUntukTransaksi(idPenjualanUntukDicetak, printerPort, isNetworkPrinter);

        if (cetakBerhasil) {
            System.out.println("Proses cetak struk berhasil dimulai.");
        } else {
            System.out.println("Proses cetak struk gagal.");
        }
        System.out.println("Contoh aplikasi selesai.");
    }
}
