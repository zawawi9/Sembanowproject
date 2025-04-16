package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class koneksi {
    private static Connection koneksi;
    // Definisikan variabel URL, user, dan password sebagai variabel global
    private static final String url = "jdbc:mysql://localhost:3306/sembakogrok";
    private static final String user = "root";
    private static final String password = "";

    public static Connection getKoneksi() {
        if (koneksi == null) {
            try {
                // Menggunakan DriverManager untuk membuat koneksi
                koneksi = DriverManager.getConnection(url, user, password);
                System.out.println("Berhasil terhubung ke database.");
            } catch (SQLException e) {
                System.out.println("Gagal terhubung ke database: " + e.getMessage());
            }
        }
        return koneksi;
    }

    // Method tambahan yang digunakan untuk mengembalikan koneksi
    public static Connection getConnection() throws SQLException {
        // Memastikan variabel global url, user, dan password digunakan
        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        getKoneksi(); // Coba koneksi database saat menjalankan program
}
}
