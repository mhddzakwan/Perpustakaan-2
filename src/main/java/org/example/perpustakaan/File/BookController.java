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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.perpustakaan.Database.BookDb;
import org.example.perpustakaan.Database.CategoryDb;
import org.example.perpustakaan.Model.Admin;
import org.example.perpustakaan.Model.Book;
import org.example.perpustakaan.Model.Category;
import org.example.perpustakaan.Model.Member;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class BookController implements Initializable {
    BookDb db = new BookDb();
    @FXML private Button btnAdd, btnEdit,btnDelete, btnUpload,btnName;
    @FXML private Button btnAdmin,btnBook,btnDashboard,btnIssue,btnReserved,btnMember, btnCategory, btnReturn;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    @FXML private TextField fieldId, fieldTitle, fieldAuthor, fieldReleased, fieldSearch;
    @FXML private ChoiceBox<Category> fieldCategory;
    private File selectedImageFile;
    @FXML private Label warning;
    @FXML private TableView<Book> tableBook;
    @FXML private TableColumn<Book, String> colId, colTitle, colCategory, colAuthor, colReleased;
    @FXML private TableColumn<Book, CheckBox> colSelect;
    @FXML private TableColumn<Book, Button> colCopy;
    @FXML private ImageView imageBook;
    private String bookName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colReleased.setCellValueFactory(new PropertyValueFactory<>("released"));
        colSelect.setCellValueFactory(new PropertyValueFactory<>("select"));

        loadCategoryTable();
        CategoryDb kategoriDb = new CategoryDb();
        List<Category> kategoriList = kategoriDb.getCategoryList();
        ObservableList<Category> observableKategori = FXCollections.observableArrayList(kategoriList);
        fieldCategory.setItems(observableKategori);
        btnName.setText("Hi, " + Admin.nama_user + "!");

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
    void findBook() {
        String keyword = fieldSearch.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadCategoryTable(); // Tampilkan semua jika kosong
            return;
        }

        // Pecah keyword jadi kata-kata individual
        String[] keywords = keyword.split("\\s+");

        List<Book> books = db.getBookList();
        ObservableList<Book> filteredList = FXCollections.observableArrayList();

        for (Book m : books) {
            String combined = String.join(" ",
                    m.getId() != null ? m.getId().toLowerCase() : "",
                    m.getTitle() != null ? m.getTitle().toLowerCase() : "",
                    m.getCategory() != null ? m.getCategory().toLowerCase() : "",
                    m.getAuthor() != null ? m.getAuthor().toLowerCase() : "",
                    m.getReleased() != null ? m.getReleased().toLowerCase() : ""
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

        tableBook.setItems(filteredList);
    }

    @FXML
    void clearBook() {
        fieldSearch.clear();
        loadCategoryTable(); // Kembalikan ke semua data
    }

    private void loadCategoryTable() {
        List<Book> books = db.getBookList();

        // Set checkbox action listener
        for (Book book : books) {
            CheckBox checkBox = book.getSelect();

            // Set listener untuk checkbox agar tidak bisa lebih dari satu yang dicentang
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    // Jika checkbox dicentang, tampilkan data ke textfield
                    fieldId.setText(book.getId());
                    fieldTitle.setText(book.getTitle());
                    for (Category kategori : fieldCategory.getItems()) {
                        if (kategori.getCategory().equalsIgnoreCase(book.getCategory())) {
                            fieldCategory.setValue(kategori);
                            break;
                        }
                    }
                    fieldAuthor.setText(book.getAuthor());
                    fieldReleased.setText(book.getReleased());
                    String photoFileName = book.getPhoto();
                    bookName =photoFileName;
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
                } else {
                    clearFields();
                }
            });
        }

        // Set listener untuk klik baris
        tableBook.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Book book = row.getItem();
                    fieldId.setText(book.getId());
                    fieldTitle.setText(book.getTitle());
                    for (Category kategori : fieldCategory.getItems()) {
                        if (kategori.getCategory().equalsIgnoreCase(book.getCategory())) {
                            fieldCategory.setValue(kategori);
                            break;
                        }
                    }
                    fieldAuthor.setText(book.getAuthor());
                    fieldReleased.setText(book.getReleased());
                    String photoFileName = book.getPhoto();
                    bookName =photoFileName;
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
                    book.getSelect().setSelected(true);

                    // Uncheck checkbox pada semua baris lainnya
                    for (Book otherBook : tableBook.getItems()) {
                        if (otherBook != book) {
                            otherBook.getSelect().setSelected(false);
                        }
                    }
                }
            });
            return row;
        });

        ObservableList<Book> observableList = FXCollections.observableArrayList(books);
        tableBook.setItems(observableList);

        colCopy.setCellFactory(param -> new TableCell<>() {
            private final Button copyButton = new Button("Copy");

            {
                copyButton.setOnAction(event -> {
                    // Dapatkan item Book dari baris ini
                    Book book = getTableView().getItems().get(getIndex());
                    Book.idBook = book.getId();
                    Book.titleBook = book.getTitle();
                    Book.authorBook = book.getAuthor();

                    try {
                        // Load halaman BookCopy.fxml
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/perpustakaan/BookCopy.fxml"));
                        Parent root = loader.load();

                        BookCopyController controller = loader.getController();
                        controller.setBookInfo();

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(copyButton);
                }
            }
        });


    }




    @FXML void addBook(){
        handleAddPhoto();
        String id = fieldId.getText().trim();
        String title = fieldTitle.getText().trim();
        Category selectedCategory = (Category) fieldCategory.getValue();
        String selectedName = (selectedCategory == null || selectedCategory.getCategory() == null)
                ? ""
                : selectedCategory.getCategory().trim();

        String author = fieldAuthor.getText().trim();
        String released = fieldReleased.getText().trim();

        String imageName = "";
        if (imageBook.getImage() != null) {
            String url = imageBook.getImage().getUrl(); // file:/C:/....../resources/images/filename.jpg
            if (url != null && url.contains("/")) {
                imageName = url.substring(url.lastIndexOf("/") + 1);
            }
        }

        if (id.isEmpty() || title.isEmpty() || selectedName.isEmpty() || author.isEmpty() || released.isEmpty() || imageName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please fill in all required fields before proceeding.");
            alert.showAndWait();
            return;
        }

        // Ambil list kategori dari database untuk pengecekan ID
        List<Book> books = db.getBookList();

        // Pengecekan apakah ID sudah ada di list kategori
        for (Book existingBook : books) {
            if (existingBook.getId().equals(id)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Duplicate Book ID");
                alert.setHeaderText("Book ID Already Exists");
                alert.setContentText("The Book ID you entered is already registered. Please enter a different Book ID.");
                alert.showAndWait();
                return;
            }
        }

        try {
            // Jika semua pengecekan lolos, masukkan data ke database
            db.insertBook(id, title, selectedName, author, released, imageName);
            clearFields();
//            loadCategoryTable();
            loadCategoryTable();
            warning.setVisible(false);
        } catch (Exception e) {

        }


    }
    @FXML void deleteBook(){
        String id = fieldId.getText();

        // Pengecekan apakah ada field yang kosong
        if (id.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please fill in all required fields before proceeding.");
            alert.showAndWait();
            return;
        }

        List<Book> books = db.getBookList();

        // Pengecekan apakah ID sudah ada di list kategori
        for (Book existingBook : books) {
            if (existingBook.getId().equals(id)) {
                // Konfirmasi penghapusan
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Are you sure you want to delete this data? (Book Title : " + existingBook.getTitle() + ")");
                alert.setContentText("This action cannot be undone.");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        db.deleteBook(id);
                        System.out.println("Book name : " + bookName);
                        File oldFile = new File("book/" + bookName);
                        if (oldFile.exists()) oldFile.delete();
                        clearFields();
                        loadCategoryTable();
                    }
                });
                warning.setVisible(false);
                return;
            }
        }
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Book Not Found");
        alert.setHeaderText("Book ID Not Found");
        alert.setContentText("The Book ID you entered does not exist in the system. Please check and try again.");
        alert.showAndWait();
    }
    public void editBook() {
        handleAddPhoto();
        String id = fieldId.getText().trim();
        String title = fieldTitle.getText().trim();
        Category selectedCategory = (Category) fieldCategory.getValue();
        String selectedName = (selectedCategory == null || selectedCategory.getCategory() == null)
                ? ""
                : selectedCategory.getCategory().trim();
        String author = fieldAuthor.getText().trim();
        String released = fieldReleased.getText().trim();

        String imageName;
        if (imageBook.getImage() != null) {
            String url = imageBook.getImage().getUrl(); // file:/C:/....../resources/images/filename.jpg
            if (url != null && url.contains("/")) {
                imageName = url.substring(url.lastIndexOf("/") + 1);
            } else {
                imageName = null;
            }
        } else {
            imageName = null;
        }

        if (id.isEmpty() || title.isEmpty() || selectedName.isEmpty() || author.isEmpty() || released.isEmpty() || imageName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please fill in all required fields before proceeding.");
            alert.showAndWait();
            return;
        }

        // Ambil list kategori dari database untuk pengecekan ID
        List<Book> books = db.getBookList();
        // Pengecekan apakah ID sudah ada di list kategori
        for (Book existingBook : books) {
            if (existingBook.getId().equals(id)) {
                File oldFile = new File("book/" + bookName);
                if (oldFile.exists()) oldFile.delete();
                db.updateBook(id, title, selectedName, author, released, imageName);
                clearFields();
                loadCategoryTable();
                return;
            }
        }
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Book Not Found");
        alert.setHeaderText("Book ID Not Found");
        alert.setContentText("The Book ID you entered does not exist in the system. Please check and try again.");
        alert.showAndWait();
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
            imageBook.setImage(image); // Preview
        }
    }


    private void handleAddPhoto() {
        if (selectedImageFile == null) {
            System.out.println("Belum ada gambar yang dipilih.");
            return;
        }

        File destDir = new File("book"); // buat folder di luar resources
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
    private void clearFields(){
        fieldId.clear();
        fieldTitle.clear();
        fieldAuthor.clear();
        fieldReleased.clear();
        imageBook.setImage(null);
        fieldCategory.setValue(null);
    }

}
