package cetak;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StrukEscposDesigner {

    // --- Perintah ESC/POS Umum (INI HANYA CONTOH! CEK MANUAL PRINTER ANDA!) ---
    private static final byte[] ESC_INIT = {0x1B, 0x40}; // Inisialisasi printer
    private static final byte[] LF = {0x0A}; // Line Feed (\n)

    private static final byte[] ESC_BOLD_ON = {0x1B, 0x45, 0x01};
    private static final byte[] ESC_BOLD_OFF = {0x1B, 0x45, 0x00};

    // ESC ! n (mode cetak gabungan)
    private static final byte[] ESC_FONT_NORMAL = {0x1B, 0x21, 0x00}; // Font A (default)
    private static final byte[] ESC_FONT_SMALL = {0x1B, 0x21, 0x01}; // Font B (kecil)
    // Untuk judul toko, gunakan kombinasi Double Width dan Double Height
    private static final byte[] ESC_FONT_DOUBLE_HW = {0x1B, 0x21, 0x30}; // Double Width, Double Height

    // Alignment
    private static final byte[] ESC_ALIGN_LEFT = {0x1B, 0x61, 0x00};
    private static final byte[] ESC_ALIGN_CENTER = {0x1B, 0x61, 0x01};
    private static final byte[] ESC_ALIGN_RIGHT = {0x1B, 0x61, 0x02};

    // Cut paper: GS V m
    private static final byte[] GS_CUT_FULL = {0x1D, 0x56, 0x00};
    // Open Cash Drawer (Biasanya ESC p m t1 t2) - Sesuaikan jika berbeda
    private static final byte[] ESC_OPEN_DRAWER = {0x1B, 0x70, 0x00, 0x32, 0x32}; // Contoh untuk Epson/Star

    // --- Konfigurasi Lebar Printer ---
    // Sesuaikan nilai ini dengan lebar karakter maksimal printer thermal Anda.
    // Umumnya 48 untuk printer 80mm (font normal), atau 32 untuk printer 58mm.
    private static final int PRINTER_CHAR_WIDTH = 48; // Lebar standar untuk printer 80mm.

    // --- Encoding Karakter (PENTING!) ---
    // Coba "UTF-8" atau "CP437" (IBM437) atau "Windows-1252"
    private static final String CHARACTER_ENCODING = "UTF-8"; // Sesuaikan dengan printer Anda

    // --- Metode Utilitas ---
    private byte[] generateLineBytes(String text) throws IOException {
        return (text + "\n").getBytes(CHARACTER_ENCODING);
    }

    private byte[] newLine() {
        return LF;
    }

    // Fungsi untuk format rupiah (dari BigDecimal ke String)
    private String formatRupiah(BigDecimal value) {
        // Gunakan Locale Indonesia untuk pemisah ribuan dan desimal
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        // Atur format tanpa desimal jika Anda tidak membutuhkannya, misal: "#,##0"
        // Jika perlu desimal: "#,##0.00"
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols); // Tanpa desimal
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

    // Fungsi untuk menengahkan teks
    private String centerText(String text) {
        if (text == null) return "";
        int padding = (PRINTER_CHAR_WIDTH - text.length()) / 2;
        if (padding < 0) padding = 0; // Hindari padding negatif
        return String.format("%" + (padding + text.length()) + "s", text);
    }

    // --- Metode untuk menghasilkan byte array per bagian struk ---
    // Menghasilkan byte untuk Header Struk (Info Toko & Detail Transaksi Minimal)
    // Tambahkan namaPelanggan sebagai parameter
    public byte[] generateHeaderBytes(String namaToko, String alamatToko, String teleponToko,
                                      String billNo, String waiter, Date tanggal, String namaPelanggan) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm"); // Format tanggal & jam

        bos.write(ESC_ALIGN_CENTER);
        bos.write(ESC_FONT_DOUBLE_HW); // Font besar (Double Width & Double Height)
        bos.write(namaToko.getBytes(CHARACTER_ENCODING));
        bos.write(LF);

        bos.write(ESC_FONT_NORMAL); // Font normal
        bos.write(alamatToko.getBytes(CHARACTER_ENCODING));
        bos.write(LF);
        bos.write(teleponToko.getBytes(CHARACTER_ENCODING));
        bos.write(LF);
        bos.write(LF); // Spasi

        // Detail Transaksi (sesuai data dari SQL asli)
        bos.write(ESC_ALIGN_LEFT);
        bos.write(generateLineBytes("Bill No  : " + billNo));
        bos.write(generateLineBytes("Kasir    : " + (waiter != null ? waiter : "-")));
        bos.write(generateLineBytes("Tanggal  : " + dateFormat.format(tanggal)));
        // Cetak Nama Pelanggan
        bos.write(generateLineBytes("Pelanggan: " + (namaPelanggan != null && !namaPelanggan.isEmpty() ? namaPelanggan : "Umum")));
        bos.write(LF); // Spasi

        return bos.toByteArray();
    }

    public byte[] generateSeparator(char character, int length) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(ESC_FONT_NORMAL); // Pastikan font normal untuk garis
        bos.write(ESC_ALIGN_LEFT); // Pastikan rata kiri untuk menggambar garis
        StringBuilder separator = new StringBuilder();
        for (int i = 0; i < length; i++) {
            separator.append(character);
        }
        bos.write(generateLineBytes(separator.toString()));
        return bos.toByteArray();
    }

    // Menghasilkan byte untuk Detail Item (dimodifikasi untuk Qty x Harga di bawah)
    public byte[] generateItemBytes(List<StrukItem> items) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bos.write(ESC_FONT_NORMAL);
        bos.write(ESC_ALIGN_LEFT);
        bos.write(ESC_BOLD_ON);
        // Header kolom baru: Hanya Nama Produk dan Subtotal
        // Asumsi Subtotal 10 karakter (Rp 1.000.000)
        int subtotalColWidth = 12; // "Rp 1.000.000" sekitar 10-12 karakter
        int namaProdukHeaderWidth = PRINTER_CHAR_WIDTH - subtotalColWidth;
        String headerLineSingle = String.format("%-" + namaProdukHeaderWidth + "s %" + subtotalColWidth + "s", "Nama Produk", "Subtotal");
        bos.write(generateLineBytes(headerLineSingle));
        bos.write(ESC_BOLD_OFF);

        for (StrukItem item : items) {
            String nama = item.getNama();
            String subTotalFormatted = "Rp " + formatRupiah(item.getSubTotal()); // Tambah "Rp"

            // Baris pertama: Nama Produk dan Subtotal
            // Sesuaikan lebar nama produk agar tidak tumpang tindih
            int actualNamaWidth = PRINTER_CHAR_WIDTH - subTotalFormatted.length();
            if (nama.length() > actualNamaWidth) {
                nama = nama.substring(0, actualNamaWidth - 3) + "..."; // Potong dan tambahkan "..."
            }
            String itemLine1 = String.format("%-" + actualNamaWidth + "s%s", nama, subTotalFormatted);
            bos.write(generateLineBytes(itemLine1));

            // Baris kedua: Qty x Harga Satuan (kecil di bawah nama produk)
            String qtyHargaLine = String.format("%d x Rp %s",
                    item.getQty(),
                    formatRupiah(item.getHargaSatuan()));

            bos.write(ESC_FONT_SMALL); // Atur font ke kecil
            bos.write(generateLineBytes(qtyHargaLine)); // generateLineBytes akan menangani newline dan padding
            bos.write(ESC_FONT_NORMAL); // Kembalikan font ke normal setelah baris qty x harga
        }
        bos.write(newLine()); // Spasi setelah item list

        return bos.toByteArray();
    }

    // Menghasilkan byte untuk Bagian Ringkasan (Subtotal Dihitung, GRAND TOTAL dari DB)
    public byte[] generateSummaryBytes(BigDecimal subtotalSebelumPajak, BigDecimal grandTotal) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bos.write(ESC_FONT_NORMAL); // Font normal
        bos.write(ESC_ALIGN_LEFT);

        // Subtotal (Dihitung dari total item)
        String subtotalLabel = "SUBTOTAL";
        String subtotalValue = "Rp " + formatRupiah(subtotalSebelumPajak);
        String subtotalLine = String.format("%-" + (PRINTER_CHAR_WIDTH - subtotalValue.length()) + "s%s",
                subtotalLabel, subtotalValue);
        bos.write(generateLineBytes(subtotalLine));

        bos.write(newLine()); // Spasi

        // GRAND TOTAL (Bold dan Font Double Width)
        // Cara paling mudah untuk GRAND TOTAL dengan font besar adalah di tengahkan
        bos.write(ESC_ALIGN_CENTER);
        bos.write(ESC_BOLD_ON);
        bos.write(ESC_FONT_DOUBLE_HW); // Double Height dan Double Width
        bos.write(generateLineBytes("TOTAL"));
        bos.write(generateLineBytes("Rp " + formatRupiah(grandTotal)));
        bos.write(ESC_BOLD_OFF);
        bos.write(ESC_FONT_NORMAL); // Kembali ke normal
        bos.write(ESC_ALIGN_LEFT); // Kembali ke rata kiri
        bos.write(newLine());

        return bos.toByteArray();
    }

    // Menghasilkan byte untuk Bagian Pembayaran (Bayar, Kembalian dari DB)
    public byte[] generatePaymentDetailsBytes(BigDecimal bayar, BigDecimal kembalian) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bos.write(ESC_FONT_NORMAL);
        bos.write(ESC_ALIGN_LEFT);

        // Bayar
        String bayarLabel = "Bayar";
        String bayarValue = "Rp " + formatRupiah(bayar);
        String bayarLine = String.format("%-" + (PRINTER_CHAR_WIDTH - bayarValue.length()) + "s%s",
                bayarLabel, bayarValue);
        bos.write(generateLineBytes(bayarLine));

        // Kembalian
        String kembalianLabel = "Kembalian";
        String kembalianValue = "Rp " + formatRupiah(kembalian);
        String kembalianLine = String.format("%-" + (PRINTER_CHAR_WIDTH - kembalianValue.length()) + "s%s",
                kembalianLabel, kembalianValue);
        bos.write(generateLineBytes(kembalianLine));

        return bos.toByteArray();
    }

    // Menghasilkan byte untuk Footer (Hanya Ucapan Terima Kasih)
    public byte[] generateFooterBytes(String pesanTerimaKasih) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bos.write(newLine()); // Spasi
        bos.write(ESC_ALIGN_CENTER); // Tengahkan footer
        bos.write(ESC_FONT_SMALL); // Mungkin gunakan font lebih kecil untuk footer
        bos.write(generateLineBytes(pesanTerimaKasih));
        bos.write(newLine()); // Spasi kosong sebelum potong
        bos.write(ESC_FONT_NORMAL); // Kembali ke normal
        return bos.toByteArray();
    }

    public byte[] generateCutAndDrawerCommands() {
        // Tambahkan spasi kosong untuk memudahkan potong kertas
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(newLine());
            bos.write(newLine());
            bos.write(newLine());
            bos.write(GS_CUT_FULL);
            bos.write(ESC_OPEN_DRAWER); // Buka laci kasir
        } catch (IOException e) {
            e.printStackTrace(); // Seharusnya tidak terjadi pada ByteArrayOutputStream
        }
        return bos.toByteArray();
    }

    // --- Metode utama untuk menggabungkan byte ---
    /**
     * Membangun byte array lengkap untuk struk dalam format ESC/POS.
     *
     * @param namaToko Nama toko (kustom)
     * @param alamatToko Alamat toko (kustom)
     * @param teleponToko Telepon toko (kustom, diasumsikan ada bersama info toko)
     * @param billNo Nomor Bill / ID Penjualan (dari DB)
     * @param waiter Nama Kasir / Waiter (dari DB)
     * @param tanggal Tanggal dan waktu transaksi (dari DB)
     * @param namaPelanggan Nama pelanggan (dari DB)
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
                                  String billNo, String waiter, Date tanggal, String namaPelanggan, // <<< TAMBAH NAMA PELANGGAN DI SINI
                                  List<StrukItem> items, BigDecimal subtotalSebelumPajak, BigDecimal grandTotal,
                                  BigDecimal bayar, BigDecimal kembalian,
                                  String pesanTerimaKasih) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bos.write(ESC_INIT); // Inisialisasi printer

        // Panggil generateHeaderBytes dengan namaPelanggan
        bos.write(generateHeaderBytes(namaToko, alamatToko, teleponToko, billNo, waiter, tanggal, namaPelanggan));
        bos.write(generateSeparator('-', PRINTER_CHAR_WIDTH));
        bos.write(generateItemBytes(items));
        bos.write(generateSeparator('-', PRINTER_CHAR_WIDTH));
        bos.write(generateSummaryBytes(subtotalSebelumPajak, grandTotal));
        bos.write(generateSeparator('-', PRINTER_CHAR_WIDTH));
        bos.write(generatePaymentDetailsBytes(bayar, kembalian));
        bos.write(generateFooterBytes(pesanTerimaKasih));

        bos.write(generateCutAndDrawerCommands()); // Perintah potong dan buka laci

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