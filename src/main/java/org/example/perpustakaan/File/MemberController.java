package org.example.perpustakaan.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.perpustakaan.Database.MemberDb;
import org.example.perpustakaan.Model.Admin;
import org.example.perpustakaan.Model.Member;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MemberController implements Initializable {
    @FXML private Button btnAdmin,btnBook,btnDashboard,btnIssue,btnReserved,btnMember, btnCategory, btnReturn;
    @FXML private Button btnAdd, btnEdit,btnName;
    @FXML private TextField fieldSearch;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    @FXML private TableView<Member> tableMember;
    @FXML private TableColumn<Member, String> colId, colName, colEmail, colPhone, colDepartment;
    @FXML private TableColumn<Member, CheckBox> colSelect;
    @FXML private ImageView imageProfile;
    MemberDb memberDb = new MemberDb();
    Member selected;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colSelect.setCellValueFactory(new PropertyValueFactory<>("select"));
        loadMemberTable();
        btnName.setText("Hi, " + Admin.nama_user + "!");

        // Set gambar ke null jika belum ada yang dicentang
        imageProfile.setImage(null);

        tableMember.setRowFactory(tv -> {
            TableRow<Member> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                     selected = row.getItem();
                     Member.member = selected;
                    for (Member m : tableMember.getItems()) {
                        m.getSelect().setSelected(false);
                    }
                    selected.getSelect().setSelected(true);
                    String photoFileName = selected.getPhoto();
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
                } else {
                    imageProfile.setImage(null);
                }
            });
            return row;
        });
    }
    //Sidebar
    public void handleClicks(ActionEvent event) throws Exception {
        Parent page = null;
        if(event.getSource() == btnDashboard){
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Dashboard.fxml"));
        }else if(event.getSource() == btnAdmin){
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Admin.fxml"));
        }else if(event.getSource() == btnMember){
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Member.fxml"));
        }else if(event.getSource() == btnCategory){
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Category.fxml"));
        }else if(event.getSource() == btnBook){
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Book.fxml"));
        }else if(event.getSource() == btnReserved){
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Reserve.fxml"));
        } else if(event.getSource() == btnIssue){
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Issue.fxml"));
        }else if(event.getSource() == btnReturn){
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Return.fxml"));
        }else if (event.getSource() == btnAdd) {
            page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Member_Detail.fxml"));

        } else if (event.getSource() == btnEdit) {
            ObservableList<Member> members = tableMember.getItems();
            Member selectedMember = null;

            for (Member m : members) {
                if (m.getSelect().isSelected()) {
                    selectedMember = m;
                    break;
                }
            }
            if(selectedMember != null){
                page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Member_Edit.fxml"));
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Selection Required");
                alert.setHeaderText("No Member Selected");
                alert.setContentText("Please select a member from the list before continuing.");
                alert.showAndWait();
                page = FXMLLoader.load(getClass().getResource("/org/example/perpustakaan/Member.fxml"));
            }

        }
        Scene bookScene = new Scene(page);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(bookScene);
        stage.show();

        logger.info("TOmbol: " + event.getSource().toString());
    }

    private void loadMemberTable() {
        List<Member> members = memberDb.getAllMembers(); // ambil dari database
        ObservableList<Member> observableList = FXCollections.observableArrayList(members);
        tableMember.setItems(observableList);
    }
    @FXML
    private void deleteMember() {
        ObservableList<Member> members = tableMember.getItems();
        Member selectedMember = members.stream().filter(m -> m.getSelect().isSelected()).findFirst().orElse(null);
        if (selectedMember != null) {
            // Konfirmasi penghapusan
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Are you sure you want to delete this data? (Name : " + selectedMember.getName() + ")");
            alert.setContentText("This action cannot be undone.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Hapus dari database
                    memberDb.deleteMemberById(selectedMember.getId());

                    // Hapus gambar jika ada
                    String photoFileName = selectedMember.getPhoto();
                    if (photoFileName != null && !photoFileName.isEmpty()) {
                        File photoFile = new File("profile/" + photoFileName);
                        if (photoFile.exists()) {
                            if (photoFile.delete()) {
                                logger.info("Gambar profil berhasil dihapus.");
                            } else {
                                logger.warning("Gagal menghapus gambar profil.");
                            }
                        } else {
                            logger.warning("Gambar tidak ditemukan.");
                        }
                    }

                    // Hapus dari tabel
                    tableMember.getItems().remove(selectedMember);
                    imageProfile.setImage(null); // Kosongkan tampilan gambar
                    logger.info("Member dihapus.");
                }
            });

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Required");
            alert.setHeaderText("No Member Selected");
            alert.setContentText("Please select a member from the list before continuing.");
            alert.showAndWait();
        }
    }

    @FXML
    void findMember() {
        String keyword = fieldSearch.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadMemberTable(); // Tampilkan semua jika kosong
            return;
        }

        // Pecah keyword jadi kata-kata individual
        String[] keywords = keyword.split("\\s+");

        List<Member> members = memberDb.getAllMembers();
        ObservableList<Member> filteredList = FXCollections.observableArrayList();

        for (Member m : members) {
            String combined = String.join(" ",
                    m.getId() != null ? m.getId().toLowerCase() : "",
                    m.getName() != null ? m.getName().toLowerCase() : "",
                    m.getEmail() != null ? m.getEmail().toLowerCase() : "",
                    m.getPhoneNumber() != null ? m.getPhoneNumber().toLowerCase() : "",
                    m.getDepartment() != null ? m.getDepartment().toLowerCase() : ""
            );

            boolean match = true;
            for (String key : keywords) {
                if (!combined.contains(key)) {
                    match = false;
                    break;
                }
            }

            if (match) {
                filteredList.add(m);
            }
        }

        tableMember.setItems(filteredList);
    }


    @FXML
    void clearMember() {
        fieldSearch.clear();
        loadMemberTable(); // Kembalikan ke semua data
    }



}
