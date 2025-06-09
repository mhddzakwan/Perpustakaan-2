package org.example.perpustakaan.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.perpustakaan.Database.MemberDb;
import org.example.perpustakaan.Model.Member;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MemberDetailController implements Initializable {

    @FXML private Button btnAdd, btnCancel, btnUpload, btnSave;
    @FXML private TextField fieldNim, fieldEmail, fieldFirst, fieldLast,fieldPhone;
    @FXML private ImageView imageProfile;
    @FXML private ComboBox<String> fieldDepartment;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private File selectedImageFile; // Untuk menyimpan file gambar sementara
    MemberDb memberDb = new MemberDb();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fieldDepartment.getItems().addAll("Faculty of Engineering", "Faculty of Computer Science", "Faculty of Medicine");
        // Tidak perlu lagi memanggil setMember() di sini, karena sudah dipanggil sebelumnya
    }

    public void handleClicks(ActionEvent event) throws Exception {
        Parent page = null;
        if (event.getSource() == btnCancel) {
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Member.fxml"));
            Scene bookScene = new Scene(page);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(bookScene);
            stage.show();
        }else if (event.getSource() == btnAdd) {
            handleAddPhoto();
            if (memberDb.findNim(fieldNim.getText())){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Duplicate NIM");
                alert.setHeaderText("NIM Already Exists");
                alert.setContentText("The NIM you entered is already registered. Please enter a different NIM.");
                alert.showAndWait();
            }
            else if(addMember()){
                page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Member.fxml"));
                Scene bookScene = new Scene(page);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(bookScene);
                stage.show();
            }
        }


        logger.info("TOmbol: " + event.getSource().toString());
    }

    // Function untuk membersihkan form (jika diperlukan)
    public void clearFields() {
        fieldNim.clear();
        fieldDepartment.setValue(null);
        fieldEmail.clear();
        fieldFirst.clear();
        fieldLast.clear();
        fieldPhone.clear();
        imageProfile.setImage(null);
    }

    @FXML
    private boolean addMember() {
        String fullName = fieldFirst.getText().trim();
        String email = fieldEmail.getText().trim();
        String phone = fieldPhone.getText().trim();
        String department = fieldDepartment.getValue() == null ? "" : fieldDepartment.getValue().trim();
        String nim = fieldNim.getText().trim();

        // Ambil nama file dari imageProfile
        String imageName = null;
        if (imageProfile.getImage() != null) {
            String url = imageProfile.getImage().getUrl(); // file:/C:/....../resources/images/filename.jpg
            if (url != null && url.contains("/")) {
                imageName = url.substring(url.lastIndexOf("/") + 1);
            }
        }

        // Validasi dasar
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || department.isEmpty() || nim.isEmpty() || imageName == null || !email.contains("@")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please fill in all required fields before proceeding.");
            alert.showAndWait();
            return false;
        }

        // Simpan ke database (panggil method insert)
        try {
            memberDb.insertMember(fullName, email, phone, department, nim, imageName);
            System.out.println("Member berhasil ditambahkan.");
            clearMemberFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    private void clearMemberFields() {
        fieldFirst.clear();
//        fieldLast.clear();
        fieldEmail.clear();
        fieldPhone.clear();
        fieldDepartment.setValue(null);
        fieldNim.clear();
        imageProfile.setImage(null);
    }




    @FXML
    private void handleUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) btnUpload.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imageProfile.setImage(image); // Preview
        }
    }


    private void handleAddPhoto() {
        if (selectedImageFile == null) {
            System.out.println("Belum ada gambar yang dipilih.");
            return;
        }

        File destDir = new File("profile"); // buat folder di luar resources
        if (!destDir.exists()) destDir.mkdirs();

        File destFile = new File(destDir, selectedImageFile.getName());

        try (FileInputStream fis = new FileInputStream(selectedImageFile);
             FileOutputStream fos = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            System.out.println("Gambar berhasil disimpan di: " + destFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
