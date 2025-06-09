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
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.perpustakaan.Database.ReserveDb;
import org.example.perpustakaan.Model.*;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ReturnController implements Initializable {
    @FXML private Button btnAdmin,btnBook,btnDashboard,btnIssue,btnReserved,btnMember, btnCategory, btnReturn;
    @FXML TextField fieldID, fieldIdMember, fieldName, fieldEmail, fieldContact, fieldDepartment, fieldIdBook, fieldTitle, fieldCategory, fieldAuthor;
    @FXML Button btnFindMember, btnName;
    @FXML ImageView imageMember, imageBook;
    @FXML private TableView<ReturnBook> tableRowBook;
    @FXML private TableColumn<ReturnBook, Boolean> colSelect;
    @FXML private TableColumn<ReturnBook, String> colId, colTitle, colLoan, colDeadline;
    private final ObservableList<ReturnBook> bookRowList = FXCollections.observableArrayList();
    private Logger logger = Logger.getLogger(this.getClass().getName());
    ReserveDb db = new ReserveDb();
    List<ReturnBook> returnBooks = null;
    String deadline;

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

//    @FXML TextField fieldIdMember, fieldName, fieldEmail, fieldContact, fieldDepartment, fieldIdBook, fieldTitle, fieldCategory, fieldAuthor;
    @FXML
    void returnBook(ActionEvent event) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Konversi deadline String ke LocalDate
        LocalDate deadlineDate = LocalDate.parse(deadline, formatter);

        // Hitung selisih hari
        long lateDays = ChronoUnit.DAYS.between(deadlineDate, today);

        int denda = 0;
        if (lateDays > 0) {
            denda = (int) lateDays * 1000; //  Rp1000 per hari
        }

        String tanggal_pengembalian = today.format(formatter);
        for(ReturnBook returnBook: returnBooks){
            if(Objects.equals(fieldIdBook.getText(), returnBook.getBookId())){
                db.updateReserved(tanggal_pengembalian, returnBook.getIdDetail(), returnBook.getBookId(), denda);
                loadReturnBook();
                clearBook();
                return;
            }
        }

    }



    @FXML
    void findIdMember(ActionEvent event) {
        String idMember = fieldIdMember.getText();
        ReserveDb reserveDb = new ReserveDb();

        Member member = reserveDb.getMember(idMember);
        if (member != null) {
            fieldName.setText(member.getName());
            fieldEmail.setText(member.getEmail());
            fieldContact.setText(member.getPhoneNumber());
            fieldDepartment.setText(member.getDepartment());

            String photoFileName = member.getPhoto();
            try {
                File file = new File("profile/" + photoFileName);
                if (file.exists()) {
                    imageMember.setImage(new Image(file.toURI().toString()));
                } else {
                    imageMember.setImage(null);
                    System.out.println("Gambar tidak ditemukan.");
                }
            } catch (Exception e) {
                imageMember.setImage(null);
                System.out.println("Gagal memuat gambar: " + e.getMessage());
            }
            logger.info("Member found: " + member.getName());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Member Not Found");
            alert.setHeaderText("Member ID Not Found");
            alert.setContentText("The Member ID you entered does not exist in the system. Please check and try again.");
            alert.showAndWait();
        }

        loadReturnBook();
    }

    private void loadReturnBook() {
        returnBooks = db.getReturnList(fieldIdMember.getText());

        // Set checkbox action listener
        for (ReturnBook returnBook : returnBooks) {
            CheckBox checkBox = returnBook.getSelect();

            // Set listener untuk checkbox agar tidak bisa lebih dari satu yang dicentang
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    // Jika checkbox dicentang, tampilkan data ke textfield
                    fieldIdBook.setText(returnBook.getBookId());
                    fieldTitle.setText(returnBook.getTitle());
                    fieldCategory.setText(returnBook.getCategory());
                    fieldAuthor.setText(returnBook.getAuthor());
                    deadline = returnBook.getDeadline();
                    System.out.println("Deadline: " + deadline);
                    String bookFileName = returnBook.getGambar();
                    try {
                        File fileBook = new File("book/" + bookFileName);
                        imageBook.setImage(new Image(fileBook.toURI().toString()));
                    } catch (Exception e) {
                        imageBook.setImage(null);
                        System.out.println("Gagal memuat gambar: " + e.getMessage());
                    }
                } else {
                    clearFields();
                }
            });
        }

        // Set listener untuk klik baris
        tableRowBook.setRowFactory(tv -> {
            TableRow<ReturnBook> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    ReturnBook returnBook = row.getItem();
                    // Set checkbox untuk baris yang diklik
                    returnBook.getSelect().setSelected(true);
                    // Tampilkan data baris ke text field
                    fieldIdBook.setText(returnBook.getBookId());
                    fieldTitle.setText(returnBook.getTitle());
                    fieldCategory.setText(returnBook.getCategory());
                    fieldAuthor.setText(returnBook.getAuthor());
                    deadline = returnBook.getDeadline();
                    String bookFileName = returnBook.getGambar();
                    try {
                        File fileBook = new File("book/" + bookFileName);
                        imageBook.setImage(new Image(fileBook.toURI().toString()));
                    } catch (Exception e) {
                        imageBook.setImage(null);
                        System.out.println("Gagal memuat gambar: " + e.getMessage());
                    }
                    // Uncheck checkbox pada semua baris lainnya
                    for (ReturnBook otherReturnBook : tableRowBook.getItems()) {
                        if (otherReturnBook != returnBook) {
                            otherReturnBook.getSelect().setSelected(false);
                        }
                    }
                }
            });
            return row;
        });

        ObservableList<ReturnBook> observableList = FXCollections.observableArrayList(returnBooks);
        tableRowBook.setItems(observableList);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colLoan.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        colSelect.setCellValueFactory(new PropertyValueFactory<>("select"));


        btnName.setText("Hi, " + Admin.nama_user + "!");


    }
    private void clearFields() {
        fieldIdBook.clear();
        fieldCategory.clear();
        fieldTitle.clear();
        fieldAuthor.clear();
    }

    void clearBook(){
        fieldTitle.setText("");
        fieldCategory.setText("");
        fieldAuthor.setText("");
        fieldIdBook.setText("");
        imageBook.setImage(null);
    }


}
