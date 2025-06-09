package org.example.perpustakaan.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.perpustakaan.Database.MemberDb;
import org.example.perpustakaan.Model.Member;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MemberEditController implements Initializable {

    @FXML private Button btnSave, btnCancel, btnUpload;
    @FXML private TextField fieldNim, fieldEmail, fieldFirst,fieldPhone;
    @FXML private ImageView imageProfile;
    @FXML private ComboBox<String> fieldDepartment;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private File selectedImageFile; // Untuk menyimpan file gambar sementara
    MemberDb memberDb = new MemberDb();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fieldDepartment.getItems().addAll("Faculty of Engineering", "Faculty of Computer Science", "Faculty of Medicine");
        // Tidak perlu lagi memanggil setMember() di sini, karena sudah dipanggil sebelumnya
        setMember(Member.member);
    }

    public void handleClicks(ActionEvent event) throws Exception {
        Parent page = null;
        if (event.getSource() == btnCancel) {
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Member.fxml"));
        }else if(event.getSource() == btnSave){
            handleAddPhoto();
            if(editMember()){
                page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Member.fxml"));
                Scene bookScene = new Scene(page);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(bookScene);
                stage.show();
            }
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Member.fxml"));

        }
        Scene bookScene = new Scene(page);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(bookScene);
        stage.show();

        logger.info("TOmbol: " + event.getSource().toString());
    }
    // Setter untuk menerima data member
    public void setMember(Member member) {
        // Isi field dengan data member yang diteruskan
        if (member != null) {
            System.out.println(member.getName());
            fieldNim.setText(member.getId());
            fieldFirst.setText(member.getName());
            fieldEmail.setText(member.getEmail());
            fieldPhone.setText(member.getPhoneNumber());
            fieldDepartment.setValue(member.getDepartment());

            // Memuat gambar profil
            String photoFileName = member.getPhoto();
            try {
                File file = new File("profile/" + photoFileName);
                if (file.exists()) {
                    imageProfile.setImage(new Image(file.toURI().toString()));
                } else {
                    imageProfile.setImage(null);
                    System.out.println("Gambar tidak ditemukan.");
                }
            } catch (Exception e) {
                imageProfile.setImage(null);
                System.out.println("Gagal memuat gambar: " + e.getMessage());
            }
        }
    }

    @FXML
    private boolean editMember() {
        String firstName = fieldFirst.getText().trim();
        String fullName = firstName;

        String email = fieldEmail.getText().trim();
        String phone = fieldPhone.getText().trim();
        String department = fieldDepartment.getValue().trim();
        String nim = fieldNim.getText().trim();

        // Ganti foto
        String imageName = null;
        if (selectedImageFile != null) {
            imageName = selectedImageFile.getName();
        } else if (imageProfile.getImage() != null) {
            String url = imageProfile.getImage().getUrl();
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

        try {
            // Hapus gambar lama jika diganti
            if (selectedImageFile != null && Member.member.getPhoto() != null) {
                File oldFile = new File("profile/" + Member.member.getPhoto());
                if (oldFile.exists()) oldFile.delete();
            }

            // Simpan gambar baru


            // Update ke database
            memberDb.updateMember(fullName, email, phone, department, nim, imageName);
            System.out.println("Member berhasil diperbarui.");
            clearMemberFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void clearMemberFields() {
        fieldFirst.clear();
        fieldEmail.clear();
        fieldPhone.clear();
        fieldDepartment.setValue(null);
        fieldNim.clear();
        imageProfile.setImage(null);
    }



    // Buat nampilin gambar yg diupload
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

    // Buat mindahin gambar yg di upload ke folder profile
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
