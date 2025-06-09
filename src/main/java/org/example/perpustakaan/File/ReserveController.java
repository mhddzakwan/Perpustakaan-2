package org.example.perpustakaan.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import org.example.perpustakaan.Adapter.ThermalPrinterAdapter;
import org.example.perpustakaan.Database.ReserveDb;
import org.example.perpustakaan.Model.*;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ReserveController implements Initializable {
    @FXML TextField fieldIdMember, fieldName, fieldEmail, fieldContact, fieldDepartment, fieldIdBook, fieldTitle, fieldCategory, fieldAuthor;
    @FXML private Button btnAdmin,btnBook,btnDashboard,btnIssue,btnReserved,btnMember, btnCategory, btnReturn;
    @FXML Button btnFindMember, btnFindBook, btnAddBook, btnReserve, btnDeleteList, btnName;
    @FXML ImageView imageMember, imageBook;
    @FXML private TableView<BookRow> tableRowBook;
    @FXML private TableColumn<BookRow, Boolean> colCheck;
    @FXML private TableColumn<BookRow, String> colId, colTitle;
    private final ObservableList<BookRow> bookRowList = FXCollections.observableArrayList();
    private Logger logger = Logger.getLogger(this.getClass().getName());
    ReserveDb db = new ReserveDb();

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
    void addBookList(ActionEvent event) {
        String idBook = fieldIdBook.getText().trim();
        String title = fieldTitle.getText().trim();
        String idMember = fieldIdMember.getText().trim();
        String name = fieldName.getText().trim();

        if (idBook.isEmpty() || title.isEmpty() || idMember.isEmpty() || name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Data Not Complete");
            alert.setContentText("Please make sure to complete the data");
            alert.showAndWait();
            return;
        }

        if(!db.cekPinjamBuku(idBook)){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Book Currently Borrowed");
            alert.setContentText("This book is currently borrowed.");
            alert.showAndWait();
        }else{
            BookRow bookRow = new BookRow(idBook, title);
            bookRowList.add(bookRow); // Ini akan langsung update tabel karena tableRowBook sudah bind ke list
            logger.info("Buku ditambahkan ke daftar: " + idBook + " - " + title);
            for (BookRow row : bookRowList) {
                System.out.println("Row: " + row.getId() + " - " + row.getTitle());
            }
        }

        clearBook();

    }
//    @FXML TextField fieldIdMember, fieldName, fieldEmail, fieldContact, fieldDepartment, fieldIdBook, fieldTitle, fieldCategory, fieldAuthor;
    @FXML
    void reserveBook(ActionEvent event) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate tenggatDate = today.plusWeeks(1);

        String tenggat = tenggatDate.format(formatter);
        String id_peminjaman = generateRandomId();
        String nama = fieldName.getText();
        String nim = fieldIdMember.getText();
        String tanggal_peminjaman = today.format(formatter);
        String tanggal_pengembalian = " ";
        int denda = 0;
        String status = "Dipinjam";
        List<BookRow> BookList = new ArrayList<>();

        for (BookRow row : bookRowList) {
            System.out.println("Row: " + row.getId() + " - " + row.getTitle());
            String id_buku = row.getId();
            String judul =  row.getTitle();
            db.insertReserve(id_peminjaman, id_buku, nim, nama, judul, tanggal_peminjaman, tenggat,tanggal_pengembalian, denda, status );
            BookList.add(row);
        }
        Task<Void> printTask = new Task<>() {
            //THREAD
            @Override
            protected Void call() {
                try {

                    // Cetak teks pinjaman
                    Printer printer = new ThermalPrinterAdapter();
                    printer.printText(id_peminjaman, nama, nim, tanggal_peminjaman, tenggat, BookList);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(printTask).start();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Book Borrowed Successfully");
        alert.setContentText("The book has been successfully borrowed.");
        alert.showAndWait();
        bookRowList.clear();
        clearMember();
        clearBook();
        // Threading untuk proses printing



    }
    public String generateRandomId() {
        Random rand = new Random();
        return String.format("%05d", rand.nextInt(100000)); // 5 digits, padded with zeroes
    }




    @FXML
    void deleteBookList(ActionEvent event){
        BookRow toDelete = null;
        for (BookRow row : bookRowList) {
            if (row.isSelected()) {
                toDelete = row;
                break;
            }
        }
        if (toDelete != null) {
            bookRowList.remove(toDelete);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No Row Selected");
            alert.setContentText("Please select a row to delete.");
            alert.showAndWait();

        }

    }


    @FXML
    void findIdBook(ActionEvent event) {
        String idBook = fieldIdBook.getText().trim();
        ReserveDb bookModel = new ReserveDb(); // Atau singleton, tergantung implementasi Anda

        Book book = bookModel.getBook(idBook);
        if (book != null) {
            fieldTitle.setText(book.getTitle());
            fieldCategory.setText(book.getCategory());
            fieldAuthor.setText(book.getAuthor());
            String photoFileName = book.getPhoto();
            try {
                File file = new File("book/" + photoFileName);
                if (file.exists()) {
                    imageBook.setImage(new Image(file.toURI().toString()));
                } else {
                    imageBook.setImage(null);
                    System.out.println("Gambar tidak ditemukan.");
                }
            } catch (Exception e) {
                imageBook.setImage(null);
                System.out.println("Gagal memuat gambar: " + e.getMessage());
            }
            System.out.println("Book found: " + book.getTitle());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Book Not Found");
            alert.setHeaderText("Book ID Not Found");
            alert.setContentText("The Book ID you entered does not exist in the system. Please check and try again.");
            alert.showAndWait();
            fieldTitle.setText("");
            fieldCategory.setText("");
            fieldAuthor.setText("");
        }
    }


    @FXML
    void findIdMember(ActionEvent event) {
        String idMember = fieldIdMember.getText().trim();
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
            fieldName.setText("");
            fieldEmail.setText("");
            fieldContact.setText("");
            fieldDepartment.setText("");
        }
    }

    @FXML void clearMember(){
        fieldEmail.setText("");
        fieldCategory.setText("");
        fieldContact.setText("");
        fieldIdMember.setText("");
        fieldName.setText("");
        fieldDepartment.setText("");
        imageMember.setImage(null);
    }

    @FXML void clearBook(){
        fieldTitle.setText("");
        fieldCategory.setText("");
        fieldAuthor.setText("");
        fieldIdBook.setText("");
        imageBook.setImage(null);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));

        tableRowBook.setItems(bookRowList); // Set hanya sekali di sini
        tableRowBook.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        btnName.setText("Hi, " + Admin.nama_user + "!");

        colCheck.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colCheck.setCellFactory(column -> new CheckBoxTableCell<>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    this.setOnMouseClicked(event -> {
                        // Hanya izinkan satu checkbox aktif
                        BookRow currentRow = getTableRow().getItem();
                        for (BookRow row : bookRowList) {
                            row.setSelected(row == currentRow);
                        }
                    });
                }
            }
        });
        tableRowBook.setRowFactory(tv -> {
            TableRow<BookRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    BookRow clickedRow = row.getItem();
                    // Set semua baris ke tidak dicentang, kecuali yang diklik
                    for (BookRow book : bookRowList) {
                        book.setSelected(book == clickedRow);
                    }
                }
            });
            return row;
        });


    }

}
