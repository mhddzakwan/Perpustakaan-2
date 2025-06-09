package org.example.perpustakaan.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.perpustakaan.Database.BookDb;
import org.example.perpustakaan.Model.Admin;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.print.*;
import java.io.*;

public class DashboardController implements Initializable {

    // Komponen FXML yang terhubung ke tombol dan label di antarmuka pengguna (UI)
    @FXML
    private Button btnAdmin, btnBook, btnDashboard, btnIssue, btnReserved, btnMember, btnCategory, btnReturn, btnName;
    @FXML
    private Label totalBook, totalBorrow, totalReturn, totalCategory, totalAdmin, totalMember;

    // Logger digunakan untuk mencatat aktivitas, berguna untuk debugging
    private Logger logger = Logger.getLogger(this.getClass().getName());

    // Objek untuk berinteraksi dengan database buku
    BookDb db = new BookDb();

    /**
     * Method ini menangani semua klik tombol menu di dashboard.
     * Berdasarkan tombol yang diklik, method ini akan memuat halaman FXML yang sesuai.
     * Setelah halaman dimuat, scene diganti dan ditampilkan di stage yang sama.
     */
    public void handleClicks(ActionEvent event) throws Exception {
        Parent page = null;

        // Periksa tombol mana yang diklik, lalu load file FXML yang sesuai
        if (event.getSource() == btnDashboard) {
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Dashboard.fxml"));
        } else if (event.getSource() == btnAdmin) {
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Admin.fxml"));
        } else if (event.getSource() == btnMember) {
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Member.fxml"));
        } else if (event.getSource() == btnCategory) {
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Category.fxml"));
        } else if (event.getSource() == btnBook) {
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Book.fxml"));
        } else if (event.getSource() == btnReserved) {
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Reserve.fxml"));
        } else if (event.getSource() == btnIssue) {
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Issue.fxml"));
        } else if (event.getSource() == btnReturn) {
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Return.fxml"));
        }

        // Ganti tampilan scene dengan halaman baru
        Scene scene = new Scene(page);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

        // Catat aktivitas tombol yang ditekan ke log
        logger.info("Tombol: " + event.getSource().toString());
    }

    /**
     * Method ini dipanggil otomatis saat halaman dashboard dimuat.
     * Digunakan untuk mengisi label statistik dan menyapa admin.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Mengisi label dengan jumlah data dari database
        totalBook.setText(db.countDashboard("copy_buku")); // jumlah buku
        totalBorrow.setText(db.countDashboard("peminjaman", "status = 'Dipinjam' ")); // buku sedang dipinjam
        totalReturn.setText(db.countDashboard("peminjaman", "status = 'Dikembalikan' ")); // buku telah dikembalikan
        totalCategory.setText(db.countDashboard("kategori")); // jumlah kategori buku
        totalAdmin.setText(db.countDashboard("admin")); // jumlah admin
        totalMember.setText(db.countDashboard("anggota")); // jumlah anggota perpustakaan

        // Menampilkan nama admin yang sedang login
        btnName.setText("Hi, " + Admin.nama_user + "!");
    }
}
