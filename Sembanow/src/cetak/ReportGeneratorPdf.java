package cetak;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import config.koneksi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ReportGeneratorPdf {

    // Menyesuaikan ukuran font agar lebih sesuai dengan desain Jaspersoft
    private static final Font FONT_TITLE = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD); // Untuk LAPORAN PENGELUARAN & TOKO SEMBANOW
    private static final Font FONT_HEADER_ADDRESS_PHONE = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL); // Untuk alamat dan telepon
    private static final Font FONT_DETAIL_LABEL = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL); // Label detail (Nomor, Tanggal, Status, Keterangan)
    private static final Font FONT_DETAIL_VALUE = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL); // Value detail
    private static final Font FONT_ITEM_LABEL = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL); // Label item (Jumlah, Total)
    private static final Font FONT_ITEM_VALUE = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL); // Value item
    private static final Font FONT_FOOTER_APP = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC); // Footer

    // Path ke file logo Anda. PASTIKAN PATH INI AKURAT!
    private static final String LOGO_PATH = "D:/java/Sembanowproject/Sembanowproject/Sembanow/src/com/raven/icon/greencart.png";

    // --- Helper untuk format Rupiah ---
    private String formatRupiah(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
        return formatter.format(value);
    }

    public void generatePengeluaranReportPdf(String idPengeluaran) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Pengeluaran PDF");
        fileChooser.setSelectedFile(new java.io.File("Laporan_Pengeluaran_" + idPengeluaran + ".pdf"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".pdf");
            }

            Document document = new Document(com.itextpdf.text.PageSize.A4);
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                conn = koneksi.getConnection();
                if (conn == null) {
                    JOptionPane.showMessageDialog(null, "Gagal koneksi ke database.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // --- Ambil data pengeluaran utama dari tabel 'pengeluaran' ---
                String sqlPengeluaran = "SELECT id_pengeluaran, tanggal, status, keterangan, jumlah, total "
                        + "FROM pengeluaran WHERE id_pengeluaran = ?";
                ps = conn.prepareStatement(sqlPengeluaran);
                ps.setString(1, idPengeluaran);
                rs = ps.executeQuery();

                String noPengeluaran = idPengeluaran;
                Date tanggalPengeluaran = new Date(); // Default value
                String statusPengeluaran = "-"; // Default value
                String keteranganPengeluaran = "-"; // Default value
                int jumlahPengeluaran = 0; // Default value
                BigDecimal totalPengeluaran = BigDecimal.ZERO; // Default value

                if (rs.next()) {
                    tanggalPengeluaran = rs.getTimestamp("tanggal");
                    statusPengeluaran = rs.getString("status");
                    keteranganPengeluaran = rs.getString("keterangan");
                    jumlahPengeluaran = rs.getInt("jumlah");
                    totalPengeluaran = rs.getBigDecimal("total");
                } else {
                    JOptionPane.showMessageDialog(null, "Data pengeluaran tidak ditemukan untuk ID: " + idPengeluaran, "Informasi", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                rs.close();
                ps.close();

                // --- Tambahkan Konten ke Dokumen PDF ---
                // Header Top (Logo + Nama Toko + Alamat)
                PdfPTable headerTable = new PdfPTable(2);
                headerTable.setWidthPercentage(100);
                headerTable.setWidths(new float[]{1.5f, 5f}); // Lebih banyak ruang untuk teks header
                headerTable.setSpacingAfter(5f);

                // Kolom Kiri Header: Logo
                Image logo = null;
                try {
                    File logoFile = new File(LOGO_PATH);
                    if (logoFile.exists() && !logoFile.isDirectory()) {
                        logo = Image.getInstance(LOGO_PATH);
                        logo.scaleToFit(80, 80); // Sesuaikan ukuran logo jika perlu
                        logo.setAlignment(Element.ALIGN_LEFT);
                    } else {
                        System.err.println("Logo file not found or is a directory: " + LOGO_PATH);
                    }
                } catch (IOException e) {
                    System.err.println("Error loading logo image: " + e.getMessage());
                }

                PdfPCell logoCell = new PdfPCell();
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                logoCell.setPaddingLeft(0);
                if (logo != null) {
                    logoCell.addElement(logo);
                } else {
                    logoCell.addElement(new Paragraph("Logo Not Found", FONT_HEADER_ADDRESS_PHONE));
                }
                headerTable.addCell(logoCell);

                // Kolom Kanan Header: Judul Laporan, Nama Toko, Alamat, Telepon
                PdfPCell infoCellHeader = new PdfPCell();
                infoCellHeader.setBorder(Rectangle.NO_BORDER);
                infoCellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
                infoCellHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);

                Paragraph title = new Paragraph("LAPORAN PENGELUARAN", FONT_TITLE);
                title.setAlignment(Element.ALIGN_CENTER);
                infoCellHeader.addElement(title);

                Paragraph shopName = new Paragraph("TOKO SEMBANOW", FONT_TITLE);
                shopName.setAlignment(Element.ALIGN_CENTER);
                infoCellHeader.addElement(shopName);

                Paragraph address = new Paragraph("Jl. Karimata No.33, Gumuk Kerang, Sumbersari", FONT_HEADER_ADDRESS_PHONE);
                address.setAlignment(Element.ALIGN_CENTER);
                infoCellHeader.addElement(address);

                Paragraph phone = new Paragraph("Telepon: 08211455655", FONT_HEADER_ADDRESS_PHONE);
                phone.setAlignment(Element.ALIGN_CENTER);
                infoCellHeader.addElement(phone);

                headerTable.addCell(infoCellHeader);
                document.add(headerTable);

                // Garis Pemisah Penuh (solid line)
                PdfPCell solidLineCell = new PdfPCell();
                solidLineCell.setBorder(Rectangle.BOTTOM);
                solidLineCell.setBorderWidthBottom(1f);
                solidLineCell.setBorderColorBottom(BaseColor.BLACK);
                solidLineCell.setPadding(0);
                solidLineCell.setFixedHeight(1f);

                PdfPTable lineTable = new PdfPTable(1);
                lineTable.setWidthPercentage(100);
                lineTable.setSpacingBefore(5f);
                lineTable.setSpacingAfter(5f);
                lineTable.addCell(solidLineCell);
                document.add(lineTable);

                // Bagian Info Pengeluaran (Nomor, Status, Tanggal)
                PdfPTable detailInfoTable = new PdfPTable(4); // Label, Colon, Value, Spacer for status
                detailInfoTable.setWidthPercentage(100);
                detailInfoTable.setWidths(new float[]{1.7f, 0.1f, 3.2f, 3.0f}); // Label, colon, value, empty space for alignment
                detailInfoTable.setSpacingAfter(10f); // Spasi setelah tabel ini
                detailInfoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                // Nomor Pengeluaran
                detailInfoTable.addCell(createCell("Nomor Pengeluaran", FONT_DETAIL_LABEL, Element.ALIGN_LEFT, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f
                detailInfoTable.addCell(createCell(":", FONT_DETAIL_LABEL, Element.ALIGN_CENTER, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f
                detailInfoTable.addCell(createCell(noPengeluaran, FONT_DETAIL_VALUE, Element.ALIGN_LEFT, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f

                // Status
                detailInfoTable.addCell(createCell("Status : " + statusPengeluaran, FONT_DETAIL_VALUE, Element.ALIGN_LEFT, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f

                // Tanggal
                detailInfoTable.addCell(createCell("Tanggal", FONT_DETAIL_LABEL, Element.ALIGN_LEFT, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f
                detailInfoTable.addCell(createCell(":", FONT_DETAIL_LABEL, Element.ALIGN_CENTER, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f
                detailInfoTable.addCell(createCell(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(tanggalPengeluaran), FONT_DETAIL_VALUE, Element.ALIGN_LEFT, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f
                detailInfoTable.addCell(createCell("", FONT_DETAIL_VALUE, Element.ALIGN_LEFT, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // Empty cell for alignment, paddingBottom 8f

                document.add(detailInfoTable);

                // Garis Putus-putus (Sesuai desain)
                DottedLineSeparator separator = new DottedLineSeparator();
                separator.setOffset(-2);
                separator.setGap(1.5f);
                separator.setLineWidth(1f);
                document.add(separator);
                document.add(new Paragraph("\n")); // Tambahkan spasi setelah separator

                // Bagian Detail Item Pengeluaran (Keterangan, Jumlah, Total)
                PdfPTable itemDetailTable = new PdfPTable(3); // Label, Colon, Value
                itemDetailTable.setWidthPercentage(100);
                itemDetailTable.setWidths(new float[]{1.7f, 0.1f, 6.2f}); // Adjust widths for item details
                itemDetailTable.setSpacingAfter(10f); // Spasi setelah tabel ini
                itemDetailTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                // Keterangan
                itemDetailTable.addCell(createCell("Keterangan", FONT_ITEM_LABEL, Element.ALIGN_LEFT, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f
                itemDetailTable.addCell(createCell(":", FONT_ITEM_LABEL, Element.ALIGN_CENTER, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f
                itemDetailTable.addCell(createCell(keteranganPengeluaran, FONT_ITEM_VALUE, Element.ALIGN_LEFT, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f

                // Jumlah
                itemDetailTable.addCell(createCell("Jumlah", FONT_ITEM_LABEL, Element.ALIGN_LEFT, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f
                itemDetailTable.addCell(createCell(":", FONT_ITEM_LABEL, Element.ALIGN_CENTER, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f
                itemDetailTable.addCell(createCell(String.valueOf(jumlahPengeluaran), FONT_ITEM_VALUE, Element.ALIGN_LEFT, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f

                // Total
                itemDetailTable.addCell(createCell("Total", FONT_ITEM_LABEL, Element.ALIGN_LEFT, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f
                itemDetailTable.addCell(createCell(":", FONT_ITEM_LABEL, Element.ALIGN_CENTER, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f
                itemDetailTable.addCell(createCell("Rp " + formatRupiah(totalPengeluaran), FONT_ITEM_VALUE, Element.ALIGN_LEFT, Rectangle.NO_BORDER, 0, 0, 0, 8f)); // paddingBottom 8f

                document.add(itemDetailTable);

                document.add(new Paragraph("\n\n")); // Spasi sebelum footer

                // Garis bawah sebelum footer (sesuai desain Jaspersoft)
                PdfPCell solidLineCellFooter = new PdfPCell();
                solidLineCellFooter.setBorder(Rectangle.BOTTOM);
                solidLineCellFooter.setBorderWidthBottom(1f);
                solidLineCellFooter.setBorderColorBottom(BaseColor.BLACK);
                solidLineCellFooter.setPadding(0);
                solidLineCellFooter.setFixedHeight(1f);

                PdfPTable lineTableFooter = new PdfPTable(1);
                lineTableFooter.setWidthPercentage(100);
                lineTableFooter.setSpacingBefore(5f);
                lineTableFooter.setSpacingAfter(5f);
                lineTableFooter.addCell(solidLineCellFooter);
                document.add(lineTableFooter);

                // Footer Aplikasi
                Paragraph footer = new Paragraph("-SEMBANOW APPS", FONT_FOOTER_APP);
                footer.setAlignment(Element.ALIGN_LEFT);
                document.add(footer);

                document.close();

                JOptionPane.showMessageDialog(null, "Laporan pengeluaran berhasil dibuat di:\n" + fileToSave.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);

            } catch (DocumentException | IOException e) {
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat membuat PDF:\n" + e.getMessage(), "Error PDF", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan database saat mengambil data:\n" + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Helper method untuk membuat cell dengan padding yang lebih detail
    private PdfPCell createCell(String content, Font font, int alignment, int border, float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(border);
        cell.setPaddingLeft(paddingLeft);
        cell.setPaddingRight(paddingRight);
        cell.setPaddingTop(paddingTop);
        cell.setPaddingBottom(paddingBottom);
        return cell;
    }

    // Overloaded helper method for createCell to maintain compatibility with existing calls
    private PdfPCell createCell(String content, Font font, int alignment, int border) {
        // Default paddingBottom 5f, adjust as needed
        return createCell(content, font, alignment, border, 0, 0, 0, 5f);
    }
}
