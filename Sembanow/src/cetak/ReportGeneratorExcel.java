package cetak;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress; // Pastikan ini diimpor

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import config.koneksi; // Sesuaikan dengan package koneksi database Anda

public class ReportGeneratorExcel {

    // Helper untuk format Rupiah (tetap dipertahankan, tapi tidak langsung digunakan untuk setCellValue)
    private String formatRupiah(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
        return formatter.format(value);
    }

    public void exportPemasukanToExcel(int year, int month) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Pemasukan Excel");
        // Gunakan Calendar untuk memastikan tanggal yang benar agar SimpleDateFormat bekerja dengan baik
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar months are 0-indexed
        String monthName = new SimpleDateFormat("MMMM", new Locale("id", "ID")).format(cal.getTime());

        fileChooser.setSelectedFile(new java.io.File("Laporan_Pemasukan_Per_Hari_" + monthName + "_" + year + ".xlsx")); // Nama file disesuaikan
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".xlsx");
            }

            // Inisialisasi Workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Laporan Pemasukan Per Hari"); // Nama sheet disesuaikan

            // Style untuk Header Tabel
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Tambahkan border ke header
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Style untuk Data Baris Biasa (opsional, jika ada teks yang tidak di-style khusus)
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            // Tambahkan border ke dataStyle
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Style untuk Data Tanggal (hanya tanggal, tanpa jam)
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy")); // Format hanya tanggal
            // Tambahkan border ke dateStyle
            dateStyle.setBorderBottom(BorderStyle.THIN);
            dateStyle.setBorderTop(BorderStyle.THIN);
            dateStyle.setBorderLeft(BorderStyle.THIN);
            dateStyle.setBorderRight(BorderStyle.THIN);

            // --- BARU: Style untuk Angka Integer (misal: "Jumlah Transaksi", atau Tahun jika ada) ---
            CellStyle integerNumberStyle = workbook.createCellStyle();
            integerNumberStyle.setDataFormat(createHelper.createDataFormat().getFormat("0")); // "0" untuk format bilangan bulat tanpa desimal
            integerNumberStyle.setAlignment(HorizontalAlignment.CENTER); // Tengah untuk jumlah transaksi
            // Tambahkan border ke integerNumberStyle
            integerNumberStyle.setBorderBottom(BorderStyle.THIN);
            integerNumberStyle.setBorderTop(BorderStyle.THIN);
            integerNumberStyle.setBorderLeft(BorderStyle.THIN);
            integerNumberStyle.setBorderRight(BorderStyle.THIN);

            // --- BARU: Style untuk Mata Uang (Rupiah) ---
            CellStyle currencyStyle = workbook.createCellStyle();
            // Menggunakan format Excel untuk Rupiah Indonesia: Rp 1.234.567,00
            currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("Rp #,##0.00"));
            currencyStyle.setAlignment(HorizontalAlignment.RIGHT); // Kanan untuk mata uang
            // Tambahkan border ke currencyStyle
            currencyStyle.setBorderBottom(BorderStyle.THIN);
            currencyStyle.setBorderTop(BorderStyle.THIN);
            currencyStyle.setBorderLeft(BorderStyle.THIN);
            currencyStyle.setBorderRight(BorderStyle.THIN);

            // Style untuk Total (Bold)
            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalStyle.setAlignment(HorizontalAlignment.RIGHT);
            // Tambahkan border ke totalStyle
            totalStyle.setBorderBottom(BorderStyle.THIN);
            totalStyle.setBorderTop(BorderStyle.THIN);
            totalStyle.setBorderLeft(BorderStyle.THIN);
            totalStyle.setBorderRight(BorderStyle.THIN);

            // --- BARU: Style Mata Uang untuk Grand Total (Bold dan Rupiah) ---
            CellStyle grandTotalCurrencyStyle = workbook.createCellStyle();
            grandTotalCurrencyStyle.setFont(totalFont); // Font bold dari totalStyle
            grandTotalCurrencyStyle.setAlignment(HorizontalAlignment.RIGHT);
            grandTotalCurrencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("Rp #,##0.00"));
            // Tambahkan border ke grandTotalCurrencyStyle
            grandTotalCurrencyStyle.setBorderBottom(BorderStyle.THIN);
            grandTotalCurrencyStyle.setBorderTop(BorderStyle.THIN);
            grandTotalCurrencyStyle.setBorderLeft(BorderStyle.THIN);
            grandTotalCurrencyStyle.setBorderRight(BorderStyle.THIN);

            // Row 0: Judul Laporan
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("LAPORAN PEMASUKAN TOKO SEMBANOW");
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            CellStyle titleCellStyle = workbook.createCellStyle();
            titleCellStyle.setFont(titleFont);
            titleCellStyle.setAlignment(HorizontalAlignment.CENTER); // Tengah untuk judul
            titleCell.setCellStyle(titleCellStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2)); // Merge dari kolom 0 sampai 2

            // Row 1: Alamat dan Telepon
            Row addressRow = sheet.createRow(1);
            Cell addressCell = addressRow.createCell(0);
            addressCell.setCellValue("Jl. Karimata No.33, Gumuk Kerang, Sumbersari - Telepon: 08211455655");
            CellStyle addressStyle = workbook.createCellStyle();
            addressStyle.setAlignment(HorizontalAlignment.CENTER); // Tengah untuk alamat
            addressCell.setCellStyle(addressStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));

            // Row 2: Kosong (spasi)
            sheet.createRow(2);

            // Row 3: Periode Laporan
            Row periodRow = sheet.createRow(3);
            Cell periodCell = periodRow.createCell(0);
            periodCell.setCellValue("Periode: " + monthName + " " + year);
            Font periodFont = workbook.createFont();
            periodFont.setItalic(true);
            CellStyle periodStyle = workbook.createCellStyle();
            periodStyle.setFont(periodFont);
            periodStyle.setAlignment(HorizontalAlignment.CENTER); // Tengah untuk periode
            periodCell.setCellStyle(periodStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 2));

            // Row 4: Kosong (spasi)
            sheet.createRow(4);

            // Row 5: Header Kolom
            Row headerRow = sheet.createRow(5);
            String[] headers = {"Tanggal", "Jumlah Transaksi", "Total Pemasukan"}; // Header disesuaikan
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            int rowNum = 6; // Mulai menulis data dari baris ke-6 (setelah header)
            double grandTotalAllPemasukan = 0; // Total keseluruhan pemasukan

            try {
                conn = koneksi.getConnection();
                if (conn == null) {
                    JOptionPane.showMessageDialog(null, "Gagal koneksi ke database.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Query SQL
                String sql = "SELECT DATE(tanggal) AS tanggal_harian, "
                        + "COUNT(id_penjualan) AS jumlah_transaksi, "
                        + "SUM(total_keseluruhan) AS total_pemasukan_harian "
                        + "FROM penjualan "
                        + "WHERE YEAR(tanggal) = ? AND MONTH(tanggal) = ? "
                        + "GROUP BY DATE(tanggal) "
                        + "ORDER BY DATE(tanggal) ASC";

                ps = conn.prepareStatement(sql);
                ps.setInt(1, year);
                ps.setInt(2, month);
                rs = ps.executeQuery();

                if (!rs.isBeforeFirst()) {
                    JOptionPane.showMessageDialog(null, "Tidak ada data pemasukan untuk bulan " + monthName + " tahun " + year + ".", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                    workbook.close();
                    return;
                }

                while (rs.next()) {
                    Row dataRow = sheet.createRow(rowNum++);

                    // Kolom 0: Tanggal
                    Date tanggalHarian = rs.getDate("tanggal_harian");
                    Cell dateCell = dataRow.createCell(0);
                    dateCell.setCellValue(tanggalHarian);
                    dateCell.setCellStyle(dateStyle);

                    // Kolom 1: Jumlah Transaksi (ini adalah integer)
                    int jumlahTransaksi = rs.getInt("jumlah_transaksi");
                    Cell jumlahTransaksiCell = dataRow.createCell(1);
                    jumlahTransaksiCell.setCellValue(jumlahTransaksi);
                    jumlahTransaksiCell.setCellStyle(integerNumberStyle); // Terapkan style integerNumberStyle

                    // Kolom 2: Total Pemasukan Harian (ini adalah double/mata uang)
                    double totalPemasukanHarian = rs.getDouble("total_pemasukan_harian");
                    Cell totalCell = dataRow.createCell(2);
                    totalCell.setCellValue(totalPemasukanHarian);
                    totalCell.setCellStyle(currencyStyle); // Terapkan currencyStyle untuk mata uang

                    grandTotalAllPemasukan += totalPemasukanHarian;
                }

                // Auto-size kolom setelah mengisi data
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Tambahkan spasi pada kolom agar tidak terlalu mepet
                sheet.setColumnWidth(0, sheet.getColumnWidth(0) + 1000); // Kolom Tanggal
                sheet.setColumnWidth(1, sheet.getColumnWidth(1) + 500);  // Kolom Jumlah Transaksi
                sheet.setColumnWidth(2, sheet.getColumnWidth(2) + 1000); // Kolom Total Pemasukan

                // Tambahkan baris total di bawah data
                rowNum++; // Tambah spasi 1 baris
                Row totalLabelRow = sheet.createRow(rowNum++);
                Cell labelCell = totalLabelRow.createCell(1); // Di bawah kolom "Jumlah Transaksi"
                labelCell.setCellValue("TOTAL KESELURUHAN:");
                labelCell.setCellStyle(totalStyle);

                Cell grandTotalCell = totalLabelRow.createCell(2); // Di bawah kolom "Total Pemasukan"
                grandTotalCell.setCellValue(grandTotalAllPemasukan); // Set nilai double
                grandTotalCell.setCellStyle(grandTotalCurrencyStyle); // Terapkan grandTotalCurrencyStyle

                // Footer
                rowNum += 2; // Spasi sebelum footer
                Row footerRow = sheet.createRow(rowNum);
                Cell footerCell = footerRow.createCell(0);
                footerCell.setCellValue("-SEMBANOW APPS");
                Font footerFont = workbook.createFont();
                footerFont.setItalic(true);
                footerFont.setFontHeightInPoints((short) 9);
                CellStyle footerStyle = workbook.createCellStyle();
                footerStyle.setFont(footerFont);
                footerCell.setCellStyle(footerStyle);

                // Tulis workbook ke file
                try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                    workbook.write(fileOut);
                }

                JOptionPane.showMessageDialog(null, "Laporan pemasukan per hari berhasil dibuat di:\n" + fileToSave.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan database saat mengambil data:\n" + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menulis file Excel:\n" + e.getMessage(), "Error Excel", JOptionPane.ERROR_MESSAGE);
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
                    if (workbook != null) {
                        workbook.close();
                    }
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
