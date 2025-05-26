package cetak;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.math.BigDecimal;
import java.text.DecimalFormat; // Import DecimalFormat
import java.text.DecimalFormatSymbols; // Import DecimalFormatSymbols
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale; // Import Locale

/**
 * Kelas ini bertanggung jawab menghasilkan byte array perintah ESC/POS untuk
 * mencetak struk berdasarkan data yang diberikan, hanya menggunakan data dari
 * query SQL asli + info toko/footer kustom. PERHATIAN: Nilai byte untuk
 * perintah ESC/POS mungkin perlu disesuaikan dengan manual spesifik printer
 * Anda.
 */
public class StrukEscposDesigner {

    // --- Perintah ESC/POS Umum (INI HANYA CONTOH! CEK MANUAL PRINTER ANDA!) ---
    // Inisialisasi printer: ESC @
    private static final byte[] ESC_INIT = {0x1B, 0x40};
    // Line Feed (\n)
    private static final byte[] LF = {0x0A};

    // Bold mode: ESC E n (n=1: on, n=0: off)
    private static final byte[] ESC_BOLD_ON = {0x1B, 0x45, 0x01};
    private static final byte[] ESC_BOLD_OFF = {0x1B, 0x45, 0x00};

    // Select print mode(s): ESC ! n (gabungan byte untuk ukuran/font)
    // Contoh: Normal font (0), Double Height (0x10), Double Width (0x20), Double HW (0x30)
    private static final byte[] ESC_FONT_NORMAL = {0x1B, 0x21, 0x00};
    private static final byte[] ESC_FONT_DOUBLE_HEIGHT = {0x1B, 0x21, 0x10};
    private static final byte[] ESC_FONT_DOUBLE_WIDTH = {0x1B, 0x21, 0x20};
    private static final byte[] ESC_FONT_DOUBLE_HW = {0x1B, 0x21, 0x30};
    private static final byte[] ESC_FONT_SMALL = {0x1B, 0x21, 0x01}; // Font A (default) atau B (kecil)

    // Select justification: ESC a n (n=0: left, n=1: center, n=2: right)
    private static final byte[] ESC_ALIGN_LEFT = {0x1B, 0x61, 0x00};
    private static final byte[] ESC_ALIGN_CENTER = {0x1B, 0x61, 0x01};
    private static final byte[] ESC_ALIGN_RIGHT = {0x1B, 0x61, 0x02};

    // Cut paper: GS V m (m=0: full cut, m=1: partial cut)
    private static final byte[] GS_CUT_FULL = {0x1D, 0x56, 0x00};

    // --- Konfigurasi Lebar Printer ---
    // Sesuaikan nilai ini dengan lebar karakter maksimal printer thermal Anda.
    // Umumnya 48 untuk printer 80mm, atau 32/42 untuk printer 58mm.
    // Anda mungkin perlu mencoba beberapa nilai untuk menemukan yang paling pas.
    private static final int PRINTER_CHAR_WIDTH = 48; // Lebar standar untuk printer 80mm.

    // --- Metode Utilitas ---
    // Fungsi untuk format rupiah (dari BigDecimal ke String)
    private String formatRupiah(BigDecimal value) {
        // Gunakan Locale Indonesia untuk pemisah ribuan dan desimal
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        // Atur format tanpa simbol mata uang di awal, karena kita tambahkan "Rp " manual
        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(value);
    }

    // Fungsi untuk padding string ke kanan dengan spasi
    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    // Fungsi untuk padding string ke kiri dengan spasi
    private String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    // --- Metode untuk menghasilkan byte array per bagian struk ---
    // Menghasilkan byte untuk Header Struk (Info Toko & Detail Transaksi Minimal)
    public byte[] generateHeaderBytes(String namaToko, String alamatToko, String teleponToko,
            String billNo, String waiter, Date tanggal) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm"); // Format tanggal & jam

        bos.write(ESC_ALIGN_CENTER);
        bos.write(ESC_FONT_DOUBLE_HW); // Font besar
        bos.write(namaToko.getBytes()); // Perhatikan encoding jika ada karakter non-ASCII
        bos.write(LF);

        bos.write(ESC_FONT_NORMAL); // Font normal
        bos.write(alamatToko.getBytes());
        bos.write(LF);
        bos.write(teleponToko.getBytes()); // Telp Toko
        bos.write(LF);
        bos.write(LF); // Spasi

        // Detail Transaksi (sesuai data dari SQL asli)
        bos.write(ESC_ALIGN_LEFT);
        bos.write(("Nomor Transaksi: TRX-" + billNo).getBytes());
        bos.write(LF);
        bos.write(("Kasir: " + (waiter != null ? waiter : "-")).getBytes());
        bos.write(LF);
        bos.write(("Tanggal: " + dateFormat.format(tanggal)).getBytes());
        bos.write(LF);

        bos.write(LF); // Spasi

        return bos.toByteArray();
    }

    public byte[] generateSeparator(char character, int length) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(ESC_FONT_NORMAL); // Pastikan font normal untuk garis
        bos.write(ESC_ALIGN_LEFT); // Pastikan rata kiri untuk menggambar garis
        for (int i = 0; i < length; i++) {
            bos.write(character);
        }
        bos.write(LF);
        return bos.toByteArray();
    }

    // Menghasilkan byte untuk Detail Item (tidak ada perubahan, menggunakan List<StrukItem>)
    public byte[] generateItemBytes(List<StrukItem> items) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bos.write(ESC_FONT_NORMAL);
        bos.write(ESC_ALIGN_LEFT);
        bos.write(ESC_BOLD_ON);
        // Header kolom - sesuaikan jaraknya agar rapi di printer 80mm (sekitar 32-48 karakter)
        // Sesuaikan lebar kolom agar totalnya tidak melebihi PRINTER_CHAR_WIDTH
        // Contoh: Nama (20), Qty (4), Harga (10), Subtotal (10) -> Total 44 + spasi
        String headerLineSingle = String.format("%-20s %4s %10s %10s\n", "Nama", "Qty", "Harga", "Subtotal");
        bos.write(headerLineSingle.getBytes());
        bos.write(ESC_BOLD_OFF);

        for (StrukItem item : items) {
            String nama = item.getNama();
            // Potong nama jika terlalu panjang agar tidak merusak layout kolom lain
            if (nama.length() > 20) {
                nama = nama.substring(0, 17) + "..."; // 20 adalah lebar nama, 3 untuk "..."
            }
            String itemLine = String.format("%-20s %4d %10s %10s\n", // Sesuaikan lebar agar total = PRINTER_CHAR_WIDTH atau mendekati
                    nama,
                    item.getQty(),
                    formatRupiah(item.getHargaSatuan()), // Format rupiah di sini
                    formatRupiah(item.getSubTotal()));   // Format rupiah di sini

            bos.write(itemLine.getBytes());
        }
        bos.write(LF); // Spasi setelah item list

        return bos.toByteArray();
    }

    // Menghasilkan byte untuk Bagian Ringkasan (Subtotal Dihitung, GRAND TOTAL dari DB)
    public byte[] generateSummaryBytes(BigDecimal subtotalSebelumPajak, BigDecimal grandTotal) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bos.write(ESC_FONT_NORMAL); // Font normal
        bos.write(ESC_ALIGN_LEFT); // Kembali ke rata kiri untuk memulai baris

        // Subtotal (Dihitung dari total item)
        String subtotalLabel = "SUBTOTAL";
        String subtotalValue = "Rp " + formatRupiah(subtotalSebelumPajak);
        String subtotalLine = String.format("%-" + (PRINTER_CHAR_WIDTH - subtotalValue.length()) + "s%s\n",
                subtotalLabel, subtotalValue);
        bos.write(subtotalLine.getBytes());

        bos.write(LF); // Spasi

        // GRAND TOTAL (Bold dan Font Double Width)
        bos.write(ESC_BOLD_ON);
        bos.write(ESC_FONT_DOUBLE_WIDTH); // Ini membuat karakter 2x lebar
        bos.write(ESC_ALIGN_LEFT); // Penting: Kembali ke rata kiri untuk mengontrol padding

        String grandTotalLabel = "GRAND TOTAL";
        String grandTotalValue = "Rp " + formatRupiah(grandTotal);

        // Karena ESC_FONT_DOUBLE_WIDTH, karakter akan menjadi dua kali lebar.
        // Jadi kita perlu membagi PRINTER_CHAR_WIDTH dengan 2 untuk perhitungan padding yang akurat
        // untuk karakter double-width.
        int effectiveWidth = PRINTER_CHAR_WIDTH / 2; // Lebar efektif dalam karakter normal

        // Hitung spasi yang dibutuhkan
        // Perhatikan bahwa panjang string 'grandTotalLabel' dan 'grandTotalValue' dihitung dalam karakter normal.
        // Printer akan menggandakan lebarnya.
        int totalLengthOfContent = grandTotalLabel.length() + grandTotalValue.length();
        int spacesNeeded = effectiveWidth - totalLengthOfContent;

        // Pastikan spasi tidak negatif
        if (spacesNeeded < 1) {
            spacesNeeded = 1; // Minimal 1 spasi
        }

        StringBuilder grandTotalLineBuilder = new StringBuilder();
        grandTotalLineBuilder.append(grandTotalLabel);
        for (int i = 0; i < spacesNeeded; i++) {
            grandTotalLineBuilder.append(" ");
        }
        grandTotalLineBuilder.append(grandTotalValue);
        grandTotalLineBuilder.append("\n"); // Tambahkan newline di akhir

        bos.write(grandTotalLineBuilder.toString().getBytes());

        bos.write(ESC_BOLD_OFF);
        bos.write(ESC_FONT_NORMAL); // Kembali ke normal
        bos.write(ESC_ALIGN_LEFT); // Kembali ke kiri

        return bos.toByteArray();
    }

    // Menghasilkan byte untuk Bagian Pembayaran (Bayar, Kembalian dari DB)
    public byte[] generatePaymentDetailsBytes(BigDecimal bayar, BigDecimal kembalian) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bos.write(ESC_FONT_NORMAL);
        bos.write(ESC_ALIGN_LEFT); // Menggunakan rata kiri untuk semua baris dan padding manual

        // Bayar
        String bayarLabel = "Bayar";
        String bayarValue = "Rp " + formatRupiah(bayar);
        String bayarLine = String.format("%-" + (PRINTER_CHAR_WIDTH - bayarValue.length()) + "s%s\n",
                bayarLabel, bayarValue);
        bos.write(bayarLine.getBytes());

        // Kembalian
        String kembalianLabel = "Kembalian";
        String kembalianValue = "Rp " + formatRupiah(kembalian);
        String kembalianLine = String.format("%-" + (PRINTER_CHAR_WIDTH - kembalianValue.length()) + "s%s\n",
                kembalianLabel, kembalianValue);
        bos.write(kembalianLine.getBytes());

        return bos.toByteArray();
    }

    // Menghasilkan byte untuk Footer (Hanya Ucapan Terima Kasih)
    public byte[] generateFooterBytes(String pesanTerimaKasih) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bos.write(LF); // Spasi
        bos.write(ESC_ALIGN_CENTER); // Tengahkan footer
        bos.write(ESC_FONT_SMALL); // Mungkin gunakan font lebih kecil untuk footer
        bos.write(pesanTerimaKasih.getBytes());
        bos.write(LF);
        // Website dihapus

        bos.write(LF); // Spasi kosong sebelum potong
        bos.write(ESC_FONT_NORMAL); // Kembali ke normal
        return bos.toByteArray();
    }

    public byte[] generateCutCommand() {
        // Tambahkan spasi kosong untuk memudahkan potong kertas
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(LF);
            bos.write(LF);
            bos.write(LF);
            bos.write(GS_CUT_FULL);
        } catch (IOException e) {
            e.printStackTrace(); // Seharusnya tidak terjadi
        }
        return bos.toByteArray();
    }

    // --- Metode utama untuk menggabungkan byte ---
    /**
     * Membangun byte array lengkap untuk struk dalam format ESC/POS, hanya
     * menggunakan data dari query SQL asli + info toko/footer kustom.
     *
     * @param namaToko Nama toko (kustom)
     * @param alamatToko Alamat toko (kustom)
     * @param teleponToko Telepon toko (kustom, diasumsikan ada bersama info
     * toko)
     * @param billNo Nomor Bill / ID Penjualan (dari DB)
     * @param waiter Nama Kasir / Waiter (dari DB)
     * @param tanggal Tanggal dan waktu transaksi (dari DB)
     * @param items Daftar item yang dibeli (dari DB)
     * @param subtotalSebelumPajak Subtotal sebelum pajak/pembulatan (Dihitung)
     * @param grandTotal GRAND TOTAL (total_keseluruhan dari DB)
     * @param bayar Jumlah uang yang dibayarkan pelanggan (dari DB)
     * @param kembalian Jumlah kembalian (dari DB)
     * @param pesanTerimaKasih Ucapan terima kasih di footer (kustom)
     * @return Byte array yang siap dikirim ke printer ESC/POS
     * @throws IOException jika terjadi error saat menulis byte
     */
    public byte[] buildFullStruk(String namaToko, String alamatToko, String teleponToko,
            String billNo, String waiter, Date tanggal,
            List<StrukItem> items, BigDecimal subtotalSebelumPajak, BigDecimal grandTotal,
            BigDecimal bayar, BigDecimal kembalian,
            String pesanTerimaKasih) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bos.write(ESC_INIT); // Inisialisasi printer

        bos.write(generateHeaderBytes(namaToko, alamatToko, teleponToko, billNo, waiter, tanggal));
        // Sesuaikan panjang garis pemisah dengan PRINTER_CHAR_WIDTH
        bos.write(generateSeparator('-', PRINTER_CHAR_WIDTH));
        bos.write(generateItemBytes(items));
        bos.write(generateSeparator('-', PRINTER_CHAR_WIDTH));
        bos.write(generateSummaryBytes(subtotalSebelumPajak, grandTotal));
        bos.write(generateSeparator('-', PRINTER_CHAR_WIDTH));
        bos.write(generatePaymentDetailsBytes(bayar, kembalian)); // Detail Pembayaran
        bos.write(generateFooterBytes(pesanTerimaKasih)); // Footer

        bos.write(generateCutCommand()); // Perintah potong

        return bos.toByteArray();
    }

    /**
     * Mengirim byte array data ke printer melalui port yang ditentukan. Gunakan
     * FileOutputStream untuk port serial/USB/LPT yang terpetakan. Gunakan
     * Socket untuk printer jaringan.
     *
     * @param data Byte array data yang akan dikirim (hasil buildFullStruk).
     * @param printerPort Nama port printer (misal "COM3", "LPT1",
     * "/dev/usb/lp0", atau "IP_PRINTER:PORT").
     * @param isNetworkPrinter Set true jika printernya jaringan (pakai Socket),
     * false jika port lokal (pakai FileOutputStream).
     */
    public void sendToPrinter(byte[] data, String printerPort, boolean isNetworkPrinter) {
        OutputStream os = null;
        try {
            if (isNetworkPrinter) {
                System.out.println("Mengirim ke printer jaringan: " + printerPort);
                String[] parts = printerPort.split(":");
                String ip = parts[0];
                int port = (parts.length > 1) ? Integer.parseInt(parts[1]) : 9100; // Default port 9100
                Socket socket = new Socket(ip, port);
                os = socket.getOutputStream();
            } else {
                System.out.println("Mengirim ke printer lokal/port: " + printerPort);
                os = new FileOutputStream(printerPort); // Gunakan nama port fisik/virtual
            }

            os.write(data);
            os.flush();
            System.out.println("Data struk berhasil dikirim.");

        } catch (IOException e) {
            System.err.println("Gagal mengirim data ke printer " + printerPort + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
