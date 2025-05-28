package cetak;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.Font;

public class BarcodePrintable implements Printable {

    private Image barcodeImage;
    private String idProduk;
    private String namaProduk;
    private int jumlahSalinanDiminta;

    private final int DESIRED_BARCODE_WIDTH_POINTS = (int) ((40.0 / 25.4) * 72);
    private final int DESIRED_BARCODE_HEIGHT_POINTS = (int) ((20.0 / 25.4) * 72);
    private final int TEXT_LINE_HEIGHT_POINTS = 10;
    private final int HORIZONTAL_SPACING_POINTS = (int) ((3.0 / 25.4) * 72);
    private final int VERTICAL_SPACING_POINTS = (int) ((3.0 / 25.4) * 72);

    public BarcodePrintable(Image barcodeImage, String idProduk, String namaProduk, int jumlahSalinanDiminta) {
        this.barcodeImage = barcodeImage;
        this.idProduk = idProduk;
        this.namaProduk = namaProduk;
        this.jumlahSalinanDiminta = jumlahSalinanDiminta;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        int totalBlockHeight = DESIRED_BARCODE_HEIGHT_POINTS + (TEXT_LINE_HEIGHT_POINTS * 2) + VERTICAL_SPACING_POINTS;
        int barcodesPerRow = (int) (pageFormat.getImageableWidth() / (DESIRED_BARCODE_WIDTH_POINTS + HORIZONTAL_SPACING_POINTS));
        if (barcodesPerRow == 0) {
            barcodesPerRow = 1;
        }
        int barcodesPerColumn = (int) (pageFormat.getImageableHeight() / totalBlockHeight);
        if (barcodesPerColumn == 0) {
            barcodesPerColumn = 1;
        }
        int totalBarcodesPerPage = barcodesPerRow * barcodesPerColumn;

        // Hitung total halaman yang dibutuhkan
        int totalPagesNeeded = (int) Math.ceil((double) jumlahSalinanDiminta / totalBarcodesPerPage);

        // Jika pageIndex melebihi total halaman yang dibutuhkan, berarti sudah selesai mencetak
        if (pageIndex >= totalPagesNeeded) {
            return NO_SUCH_PAGE;
        }

        // Hitung barcode awal untuk halaman ini
        int currentBarcodeIndexOnPage = 0; // Index barcode yang sedang digambar di halaman ini
        int startBarcodeOverallIndex = pageIndex * totalBarcodesPerPage; // Index barcode global untuk halaman ini

        // Atur font untuk teks di bawah barcode
        g2d.setFont(new Font("Arial", Font.PLAIN, 8)); // Sesuaikan ukuran font (misal: 8pt atau 9pt)

        // Loop untuk menggambar barcode di baris dan kolom
        for (int row = 0; row < barcodesPerColumn; row++) {
            for (int col = 0; col < barcodesPerRow; col++) {
                // Periksa apakah kita sudah mencetak semua barcode yang diminta oleh user
                if ((startBarcodeOverallIndex + currentBarcodeIndexOnPage) >= jumlahSalinanDiminta) {
                    break; // Keluar dari loop jika jumlah yang diminta sudah tercapai
                }

                // Hitung posisi X dan Y untuk barcode saat ini
                int x = col * (DESIRED_BARCODE_WIDTH_POINTS + HORIZONTAL_SPACING_POINTS);
                int y = row * totalBlockHeight;

                // Gambar barcode
                if (barcodeImage != null) {
                    // Gambar barcode dengan ukuran yang diinginkan
                    g2d.drawImage(barcodeImage, x, y, DESIRED_BARCODE_WIDTH_POINTS, DESIRED_BARCODE_HEIGHT_POINTS, null);
                }

                // Gambar informasi produk di bawah barcode
                // Posisi Y untuk baris pertama teks
                int textYOffset = y + DESIRED_BARCODE_HEIGHT_POINTS + TEXT_LINE_HEIGHT_POINTS;
                if (namaProduk != null && !namaProduk.isEmpty()) {
                    g2d.drawString(namaProduk, x, textYOffset);
                    textYOffset += TEXT_LINE_HEIGHT_POINTS; // Pindah ke baris berikutnya untuk ID
                }
                if (idProduk != null && !idProduk.isEmpty()) {
                    g2d.drawString(idProduk, x, textYOffset);
                }

                currentBarcodeIndexOnPage++; // Lanjut ke barcode berikutnya di halaman ini
            }
            if ((startBarcodeOverallIndex + currentBarcodeIndexOnPage) >= jumlahSalinanDiminta) {
                break; // Keluar dari loop baris jika jumlah yang diminta sudah tercapai
            }
        }

        return PAGE_EXISTS; // Halaman ini telah berhasil dicetak
    }
}
