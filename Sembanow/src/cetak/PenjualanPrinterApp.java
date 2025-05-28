package cetak;

import config.koneksi;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PenjualanPrinterApp {
    private static final String STRUK_QUERY_BY_ID
            = "SELECT "
            + " p.id_penjualan,"
            + " pel.nama AS nama_pelanggan,"
            + " p.tanggal,"
            + " k.username AS nama_kasir,"
            + " pr.nama AS nama_produk,"
            + " t.jumlah_produk,"
            + " t.harga_satuan,"
            + " t.total AS total_per_item,"
            + " p.total_keseluruhan,"
            + " p.bayar,"
            + " p.kembalian "
            + "FROM penjualan p "
            + "JOIN pelanggan pel ON p.id_pelanggan = pel.id_pelanggan "
            + "JOIN karyawan k ON p.id_karyawan = k.id_karyawan "
            + "JOIN transaksi t ON p.id_penjualan = t.id_penjualan "
            + "JOIN produk pr ON t.id_produk = pr.id_produk "
            + "WHERE p.id_penjualan = ?"
            + "ORDER BY t.id_transaksi ASC;";

    /**
     * Inner static class untuk merepresentasikan seluruh data yang dibutuhkan untuk sebuah struk.
     * Ini mengkonsolidasi semua informasi dari database dan data kustom (info toko).
     */
    public static class StrukData { // Ubah menjadi public static agar bisa diakses dari luar jika diperlukan
        public String idPenjualan;
        public String namaPelanggan;
        public Date tanggal;
        public String namaKasir; // Nama kasir/waiter
        public List<StrukItem> items;
        public BigDecimal totalKeseluruhan;
        public BigDecimal bayar;
        public BigDecimal kembalian;
        public BigDecimal subtotalSebelumPajak; // Total dari semua sub_total_item

        // Data informasi toko yang akan diisi secara kustom
        public String namaToko;
        public String alamatToko;
        public String teleponToko;
        public String pesanTerimaKasih;

        public StrukData() {
            this.items = new ArrayList<>();
            this.subtotalSebelumPajak = BigDecimal.ZERO;
            this.totalKeseluruhan = BigDecimal.ZERO;
            this.bayar = BigDecimal.ZERO;
            this.kembalian = BigDecimal.ZERO;
        }
    }

    /**
     * Mengambil semua data yang dibutuhkan untuk struk dari database berdasarkan ID Penjualan.
     * Metode ini TIDAK menutup Connection yang diberikan.
     *
     * @param conn Koneksi database yang sudah terbuka.
     * @param idPenjualan ID transaksi penjualan (asumsi INT di DB, diterima sebagai String dari UI).
     * @return Objek StrukData yang berisi semua informasi struk, atau null jika data tidak ditemukan.
     * @throws SQLException jika terjadi error pada database.
     * @throws NumberFormatException jika idPenjualan tidak dapat di-parse menjadi integer.
     */
    public StrukData getStrukDataById(Connection conn, String idPenjualan) throws SQLException, NumberFormatException {
        StrukData data = new StrukData(); // StrukData sudah menginisialisasi items dan subtotal
        BigDecimal itemsSubtotalSum = BigDecimal.ZERO; // Akumulator untuk subtotal semua item

        // Menggunakan try-with-resources untuk memastikan PreparedStatement dan ResultSet tertutup
        try (PreparedStatement pstmt = conn.prepareStatement(STRUK_QUERY_BY_ID)) {

            // Set parameter ID Penjualan. Konversi String ke int karena asumsi ID di DB adalah INT.
            pstmt.setInt(1, Integer.parseInt(idPenjualan));

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean firstRow = true;

                while (rs.next()) {
                    if (firstRow) {
                        // Ambil data header hanya dari baris pertama
                        data.idPenjualan = rs.getString("id_penjualan");
                        data.tanggal = rs.getTimestamp("tanggal");
                        data.namaPelanggan = rs.getString("nama_pelanggan");
                        data.namaKasir = rs.getString("nama_kasir"); // Sesuai alias di query
                        data.totalKeseluruhan = rs.getBigDecimal("total_keseluruhan");
                        data.bayar = rs.getBigDecimal("bayar");
                        data.kembalian = rs.getBigDecimal("kembalian");
                        firstRow = false;
                    }

                    // Ambil detail item dari setiap baris
                    String namaProduk = rs.getString("nama_produk");
                    int jumlahProduk = rs.getInt("jumlah_produk");
                    BigDecimal hargaPerSatuan = rs.getBigDecimal("harga_satuan");
                    BigDecimal totalPerItem = rs.getBigDecimal("total_per_item"); // Sesuai alias di query

                    // Buat objek StrukItem dan tambahkan ke daftar
                    StrukItem item = new StrukItem(namaProduk, jumlahProduk, hargaPerSatuan, totalPerItem);
                    data.items.add(item);

                    // Akumulasikan subtotal untuk perhitungan subtotalSebelumPajak
                    itemsSubtotalSum = itemsSubtotalSum.add(item.getSubTotal());
                }

                data.subtotalSebelumPajak = itemsSubtotalSum; // Set subtotal sebelum pajak

            } // ResultSet otomatis ditutup oleh try-with-resources

        } // PreparedStatement otomatis ditutup oleh try-with-resources

        // Validasi apakah ada data yang ditemukan
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
     * @param printerPort Nama port printer (misal "LPT1", "COM3", atau "IP_PRINTER:PORT").
     * @param isNetworkPrinter True jika printer jaringan (menggunakan Socket), false jika port lokal.
     * @return true jika proses cetak berhasil dimulai (tidak ada error fatal sebelum mengirim data),
     * false jika terjadi kegagalan.
     */
    public boolean cetakStrukUntukTransaksi(String idPenjualan, String printerPort, boolean isNetworkPrinter) {
        StrukData dataStruk = null;
        Connection connection = null;
        boolean success = false;

        try {
            System.out.println("Mencoba mendapatkan koneksi database untuk mencetak struk...");
            connection = koneksi.getConnection(); // Menggunakan kelas koneksi dari package config
            if (connection == null || connection.isClosed()) {
                 System.err.println("Gagal mendapatkan koneksi database atau koneksi tertutup.");
                 return false;
            }
            System.out.println("Koneksi database berhasil diperoleh.");

            System.out.println("Mengambil data struk untuk ID: " + idPenjualan + "...");
            dataStruk = getStrukDataById(connection, idPenjualan);

            if (dataStruk != null) {
                System.out.println("Data struk berhasil diambil. Mengisi data kustom dan melanjutkan pencetakan...");

                // Mengisi data toko dan pesan terima kasih secara kustom
                dataStruk.namaToko = "TOKO SEMBANOW";
                dataStruk.alamatToko = "Jl. Diponegoro No. 100, Jember";
                dataStruk.teleponToko = "081231242000";
                dataStruk.pesanTerimaKasih = "Terima Kasih! Belanja Lagi Ya!";

                StrukEscposDesigner strukDesigner = new StrukEscposDesigner();

                try {
                    byte[] strukBytes = strukDesigner.buildFullStruk(
                            dataStruk.namaToko,
                            dataStruk.alamatToko,
                            dataStruk.teleponToko,
                            dataStruk.idPenjualan,
                            dataStruk.namaKasir,
                            dataStruk.tanggal,
                            dataStruk.namaPelanggan, // Mengirim nama pelanggan ke designer
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
                }

            } else {
                System.out.println("Data struk tidak ditemukan untuk ID: " + idPenjualan + ". Tidak mencetak.");
            }

        } catch (SQLException e) {
            System.err.println("Error SQL saat mendapatkan koneksi atau menjalankan query: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("ID Penjualan tidak valid (bukan angka): " + idPenjualan + ". " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) { // Tangkap exception umum lainnya
            System.err.println("Terjadi error tak terduga selama proses cetak: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Pastikan Connection ditutup di blok finally
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
        // Misalnya, jika ID penjualan Anda adalah 1, masukkan "1"
        String idPenjualanUntukDicetak = "1";
        // -----------------------------------------------------------------

        // <<< GANTI KONFIGURASI PRINTER INI SESUAI DENGAN SETUP ANDA >>>
        String printerPort = "LPT1"; // Contoh: "LPT1", "COM1", "/dev/usb/lp0", atau "192.168.1.100:9100"
        boolean isNetworkPrinter = false; // true jika printer jaringan (IP), false jika port lokal (LPT/COM/USB)
        // -----------------------------------------------------------

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