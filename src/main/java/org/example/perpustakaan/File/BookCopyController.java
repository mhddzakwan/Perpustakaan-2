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
import javafx.stage.Stage;
import org.example.perpustakaan.Adapter.ThermalPrinterAdapter;
import org.example.perpustakaan.Model.*;
import org.example.perpustakaan.Database.BookCopyDb;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static org.example.perpustakaan.Adapter.ThermalPrinterAdapter.*;

public class BookCopyController implements Initializable {
    BookCopyDb db = new BookCopyDb();
    @FXML private Button btnAdmin,btnBook,btnDashboard,btnIssue,btnReserved,btnMember, btnCategory, btnReturn;
    @FXML private Button btnAdd, btnDelete,btnName;
    @FXML private TableView<BookCopy> tableBook;
    @FXML private TableColumn<BookCopy, String> colId,colStatus,colLoan, colBack;
    @FXML private TableColumn<BookCopy, Boolean> colSelect;
    @FXML private Label labelInfo, warning;
    @FXML private TableColumn<BookCopy, Void> colPrint;

    private String idCopy;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableBook.getItems().clear();
        colId.setCellValueFactory(new PropertyValueFactory<>("id_copy"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colLoan.setCellValueFactory(new PropertyValueFactory<>("tanggal_peminjaman"));
        colBack.setCellValueFactory(new PropertyValueFactory<>("tanggal_kembali"));
        colSelect.setCellValueFactory(new PropertyValueFactory<>("select"));
        addPrintButtonToTable();

        loadBookCopyTable();
        btnName.setText("Hi, " + Admin.nama_user + "!");
    }

    private void addPrintButtonToTable() {
        colPrint.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Print");

            {
                btn.setOnAction(event -> {
                    BookCopy bookCopy = getTableView().getItems().get(getIndex());
                    String idCopy = bookCopy.getId_copy();
                    try {
                        Printer printer = new ThermalPrinterAdapter();
                        printer.printBarcode(idCopy);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                btn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }


    // Di BookCopyController.java
    public void setBookInfo() {
        Book book = new Book(Book.titleBook, Book.authorBook);
        BookDisplay basicDisplay = new BasicBookDisplay(book);

        // Tambahkan logging
        BookDisplay loggingDisplay = new LoggingBookDecorator(basicDisplay);

        // Tambahkan label
        BookDisplay labelledDisplay = new LabelledBookDecorator(loggingDisplay, "Book Information\n");

        System.out.println("Book Title : " + Book.titleBook + " Book Author : " + Book.authorBook);
        labelInfo.setText(labelledDisplay.display());
    }

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
    private void addBook() {
        String baseId = Book.idBook; // Misalnya "MAT"
        int maxNumber = 0;
// Ambil semua salinan buku dari database atau list
        List<BookCopy> allCopies = db.getBookCopyList(Book.idBook); // Anda harus punya method ini

        for (BookCopy copy : allCopies ) {
            String id = copy.getId_copy(); // Misalnya "MAT-1"
            if (id.startsWith(baseId + "-")) {
                try {
                    int number = Integer.parseInt(id.substring((baseId + "-").length()));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Skip ID yang formatnya salah
                }
            }
        }

        int nextNumber = maxNumber + 1;
        String newId = baseId + "-" + nextNumber;

        db.insertBookCopy(newId, Book.idBook, "Tersedia", "-", "-"); // Anda harus punya method insert ini
        loadBookCopyTable();
    }


    @FXML private void deleteBook(){
        if (idCopy.isEmpty()) {
            warning.setText("*Mohon lengkapi data");
            warning.setVisible(true);
            return;
        }

        List<BookCopy> bookCopies = db.getBookCopyList(Book.idBook);

        // Pengecekan apakah ID sudah ada di list kategori
        for (BookCopy existingBookCopy : bookCopies) {
            if (existingBookCopy.getId_copy().equals(idCopy)) {
                // Konfirmasi penghapusan
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Konfirmasi");
                alert.setHeaderText("Anda yakin ingin menghapus kategori dengan ID: " + idCopy + "?");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        db.deleteCopyBuku(idCopy);
                        loadBookCopyTable();
                    }
                });
                warning.setVisible(false);
                return;
            }
        }
        warning.setText("*ID tidak ditemukan");
        warning.setVisible(true);

    }

    private void loadBookCopyTable() {
        List<BookCopy> bookCopies = db.getBookCopyList(Book.idBook);

        // Set checkbox action listener
        for (BookCopy bookCopy : bookCopies) {
            CheckBox checkBox = bookCopy.getSelect();

            // Set listener untuk checkbox agar tidak bisa lebih dari satu yang dicentang
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    idCopy = bookCopy.getId_copy();
                }
            });
        }

        // Set listener untuk klik baris
        tableBook.setRowFactory(tv -> {
            TableRow<BookCopy> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    BookCopy bookCopy = row.getItem();
                    // Set checkbox untuk baris yang diklik
                    bookCopy.getSelect().setSelected(true);
                    idCopy = bookCopy.getId_copy();
                    // Uncheck checkbox pada semua baris lainnya
                    for (BookCopy otherBookCopy : tableBook.getItems()) {
                        if (otherBookCopy != bookCopy) {
                            otherBookCopy.getSelect().setSelected(false);
                        }
                    }
                }
            });
            return row;
        });

        ObservableList<BookCopy> observableList = FXCollections.observableArrayList(bookCopies);
        tableBook.setItems(observableList);
    }


}

