package cetak;

import java.math.BigDecimal;

/**
 * Kelas untuk merepresentasikan satu item dalam struk penjualan.
 * Menggunakan BigDecimal untuk nilai mata uang agar akurat.
 */
public class StrukItem {
    private String nama;
    private int qty;
    private BigDecimal hargaSatuan;
    private BigDecimal subTotal; // Lebih baik pakai BigDecimal

    // Constructor ketika subtotal dihitung di aplikasi
    public StrukItem(String nama, int qty, BigDecimal hargaSatuan) {
        if (hargaSatuan == null) throw new IllegalArgumentException("hargaSatuan tidak boleh null");
        if (qty < 0) throw new IllegalArgumentException("qty tidak boleh negatif");

        this.nama = nama != null ? nama : "Unknown Item";
        this.qty = qty;
        this.hargaSatuan = hargaSatuan;
        // Hitung subTotal menggunakan BigDecimal
        this.subTotal = hargaSatuan.multiply(BigDecimal.valueOf(qty));
    }

    // Constructor ketika subtotal sudah dihitung di database dan diambil
     public StrukItem(String nama, int qty, BigDecimal hargaSatuan, BigDecimal subTotal) {
         if (hargaSatuan == null) throw new IllegalArgumentException("hargaSatuan tidak boleh null");
         if (subTotal == null) throw new IllegalArgumentException("subTotal tidak boleh null");
         if (qty < 0) throw new IllegalArgumentException("qty tidak boleh negatif");

         this.nama = nama != null ? nama : "Unknown Item";
         this.qty = qty;
         this.hargaSatuan = hargaSatuan;
         this.subTotal = subTotal; // Ambil subtotal dari DB
     }


    // Getters
    public String getNama() { return nama; }
    public int getQty() { return qty; }
    public BigDecimal getHargaSatuan() { return hargaSatuan; }
    public BigDecimal getSubTotal() { return subTotal; }

    // Metode helper untuk representasi string yang bisa diformat
     public String getQtyString() { return String.valueOf(qty); }
     public String getHargaSatuanString() { return String.format("%.2f", hargaSatuan); }
     public String getSubTotalString() { return String.format("%.2f", subTotal); }
}