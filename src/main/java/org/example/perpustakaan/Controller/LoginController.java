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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.perpustakaan.Database.Db;
import org.example.perpustakaan.Model.Admin;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class LoginController  {

    // Field input untuk password, terhubung dengan komponen di file FXML
    @FXML
    private PasswordField inputPassword;

    // Label untuk menampilkan pesan kesalahan saat login gagal
    @FXML
    private Label Wrong;

    // Tombol login di UI
    @FXML
    private Button btnLogin;

    // Field input untuk email
    @FXML
    private TextField inputEmail;

    // Logger untuk mencatat aktivitas dan debugging
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Method ini dipanggil saat tombol login ditekan.
     * Method ini memverifikasi email dan password, lalu menampilkan dashboard jika login berhasil.
     */
    @FXML
    void onLogin(ActionEvent event) throws IOException {
        Db db = new Db(); // Inisialisasi koneksi database

        // Ambil nilai dari input email dan password
        String email = inputEmail.getText();
        String password = inputPassword.getText();

        // Verifikasi pengguna dengan metode dari kelas Db
        String user = db.authenticateUser(email, password);

        // Jika autentikasi berhasil (user tidak kosong)
        if (user != "") {
            System.out.println("Login berhasil. nama : " + user);

            // Simpan nama user ke variabel statis Admin
            Admin.nama_user = user.split(" ")[0]; // Misalnya hanya ambil nama depan

            // Load halaman dashboard setelah login berhasil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/perpustakaan/Dashboard.fxml"));
            Parent dashboard = loader.load();

            // Ganti scene saat ini ke halaman dashboard
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboard));
            stage.show();

        } else {
            // Jika login gagal, tampilkan label error
            Wrong.setVisible(true);
        }
    }

}

