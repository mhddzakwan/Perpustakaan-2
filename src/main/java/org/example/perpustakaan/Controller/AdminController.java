package org.example.perpustakaan.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.perpustakaan.Database.Db;
import org.example.perpustakaan.Model.Admin;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

// Controller utama untuk tampilan Admin, mengatur interaksi UI dan pengambilan data dari database
public class AdminController implements Initializable {
    // Membuat instance dari class database
    Db db = new Db();

    // Deklarasi tombol navigasi sidebar
    @FXML private Button btnAdmin, btnBook, btnDashboard, btnIssue, btnReserved, btnMember, btnCategory, btnReturn, btnName;

    // Tabel dan kolom untuk menampilkan data admin
    @FXML
    private TableView<Admin> tableAdmin;

    @FXML
    private TableColumn<Admin, Integer> colId;

    @FXML
    private TableColumn<Admin, String> colName, colEmail, colPhone;

    // List observable untuk menyimpan dan menampilkan data admin di tabel
    private ObservableList<Admin> adminList = FXCollections.observableArrayList();

    // Logger untuk mencatat informasi (jika dibutuhkan debugging/logging)
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Method untuk menangani klik pada tombol navigasi sidebar.
     * Berdasarkan tombol yang diklik, halaman akan berganti menggunakan FXML yang sesuai.
     */
    public void handleClicks(ActionEvent event) throws Exception {
        Parent page = null;

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

        // Menampilkan halaman yang dimuat ke dalam stage saat ini
        Scene bookScene = new Scene(page);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(bookScene);
        stage.show();
    }

    /**
     * Method yang dijalankan saat controller diinisialisasi.
     * Menghubungkan kolom tabel ke properti objek Admin dan memuat data dari database.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Menghubungkan kolom-kolom tabel ke properti yang ada di class Admin
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // Menampilkan nama pengguna admin di tombol bagian atas
        btnName.setText("Hi, " + Admin.nama_user + "!");

        // Mengambil data admin dari database, lalu mengubahnya ke dalam bentuk ObservableList
        ArrayList<Admin> admins = db.getAdminList();
        ObservableList<Admin> observableAdmins = FXCollections.observableArrayList(admins);

        // Mengisi tabel dengan data admin
        tableAdmin.setItems(observableAdmins);
    }
}
