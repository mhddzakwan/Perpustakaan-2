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
import javafx.stage.Stage;
import org.example.perpustakaan.Database.ReserveDb;
import org.example.perpustakaan.Model.Admin;
import org.example.perpustakaan.Model.Book;
import org.example.perpustakaan.Model.Issue;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class IssueController implements Initializable {
    ReserveDb db = new ReserveDb();
    @FXML private Button btnAdmin,btnBook,btnDashboard,btnIssue,btnReserved,btnMember, btnCategory, btnReturn;
    @FXML private Button btnAdd, btnDelete, btnEdit, btnName;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    @FXML private TableView<Issue> tableIssue;
    @FXML private TableColumn<Issue, String> colId, colMemberId, colName, colBookId, colTitle, colLoan, colDeadline, colReturn, colStatus;
    @FXML private TableColumn<Issue, Integer> colFine;
    @FXML private TableColumn<Issue, CheckBox> colSelect;
    @FXML private ImageView imageBook, imageMember;
    @FXML private TextField fieldSearch;



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
        }
        Scene bookScene = new Scene(page);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(bookScene);
        stage.show();
    }

    @FXML
    void findIssue() {
        String keyword = fieldSearch.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadIssueTable(); // Tampilkan semua jika kosong
            return;
        }

        // Pecah keyword jadi kata-kata individual
        String[] keywords = keyword.split("\\s+");

        List<Issue> issues = db.getIssueList();
        ObservableList<Issue> filteredList = FXCollections.observableArrayList();

        for (Issue m : issues) {
            String combined = String.join(" ",
                    m.getId() != null ? m.getId().toLowerCase() : "",
                    m.getTitle() != null ? m.getTitle().toLowerCase() : "",
                    m.getMemberId() != null ? m.getMemberId().toLowerCase() : "",
                    m.getName() != null ? m.getName().toLowerCase() : "",
                    m.getBookId() != null ? m.getBookId().toLowerCase() : "",
                    m.getLoanDate() != null ? m.getLoanDate().toLowerCase() : "",
                    m.getDeadline() != null ? m.getDeadline().toLowerCase() : "",
                    m.getReturnDate() != null ? m.getReturnDate().toLowerCase() : "",
                    m.getStatus() != null ? m.getStatus().toLowerCase() : ""
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

        tableIssue.setItems(filteredList);
    }

    @FXML void deleteBook(){
        Issue selectedIssue = null;
        for (Issue issue : tableIssue.getItems()) {
            if (issue.getSelect().isSelected()) {
                selectedIssue = issue;
                break;
            }
        }

        if (selectedIssue != null) {
            // Hapus dari database
            boolean deleted = db.deleteIssue(selectedIssue.getId_detail());
            if (deleted) {
                // Hapus dari tabel
                tableIssue.getItems().remove(selectedIssue);
                imageBook.setImage(null);
                imageMember.setImage(null);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Berhasil dihapus.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menghapus dari database.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Pilih satu data terlebih dahulu.");
            alert.showAndWait();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadIssueTable();
        btnName.setText("Hi, " + Admin.nama_user + "!");
    }

    private void loadIssueTable() {
        List<Issue> issues = db.getIssueList();

        // Set checkbox action listener (jika ingin aktifkan manual centang)
        for (Issue issue : issues) {
            CheckBox checkBox = issue.getSelect();
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    // Nonaktifkan checkbox lain
                    for (Issue other : tableIssue.getItems()) {
                        if (other != issue) {
                            other.getSelect().setSelected(false);
                        }
                    }

                    // Tampilkan gambar
                    String memberFileName = issue.getFoto();
                    String bookFileName = issue.getGambar();
                    try {
                        File fileBook = new File("book/" + bookFileName);
                        File fileMember = new File("profile/" + memberFileName);
                        imageBook.setImage(new Image(fileBook.toURI().toString()));
                        imageMember.setImage(new Image(fileMember.toURI().toString()));
                    } catch (Exception e) {
                        imageMember.setImage(null);
                        imageBook.setImage(null);
                        System.out.println("Gagal memuat gambar: " + e.getMessage());
                    }
                }
            });
        }

        // Set row factory agar klik baris mengaktifkan checkbox
        tableIssue.setRowFactory(tv -> {
            TableRow<Issue> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Issue selectedIssue = row.getItem();

                    // Aktifkan centang untuk baris ini, dan nonaktifkan yang lain
                    for (Issue issue : tableIssue.getItems()) {
                        issue.getSelect().setSelected(issue == selectedIssue);
                    }

                    // Tampilkan gambar
                    String memberFileName = selectedIssue.getFoto();
                    String bookFileName = selectedIssue.getGambar();
                    try {
                        File fileBook = new File("book/" + bookFileName);
                        File fileMember = new File("profile/" + memberFileName);
                        imageBook.setImage(new Image(fileBook.toURI().toString()));
                        imageMember.setImage(new Image(fileMember.toURI().toString()));
                    } catch (Exception e) {
                        imageMember.setImage(null);
                        imageBook.setImage(null);
                        System.out.println("Gagal memuat gambar: " + e.getMessage());
                    }
                }
            });
            return row;
        });

        // Set isi tabel
        ObservableList<Issue> observableList = FXCollections.observableArrayList(issues);
        tableIssue.setItems(observableList);

        // Binding kolom
        colSelect.setCellValueFactory(new PropertyValueFactory<>("select"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colLoan.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        colReturn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fine"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

    }
    @FXML
    void clearIssue() {
        fieldSearch.clear();
        loadIssueTable(); // Kembalikan ke semua data
    }


}
