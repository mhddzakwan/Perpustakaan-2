package org.example.perpustakaan.File;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.perpustakaan.Database.CategoryDb;
import org.example.perpustakaan.Database.Db;
import org.example.perpustakaan.Model.Admin;
import org.example.perpustakaan.Model.Category;
import org.example.perpustakaan.Model.Member;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class CategoryController implements Initializable {
    CategoryDb db = new CategoryDb();

    @FXML private Button btnAdmin,btnBook,btnDashboard,btnIssue,btnReserved,btnMember, btnCategory, btnReturn,btnName;

    @FXML private TableView<Category> tableCategory;
    @FXML private TableColumn<Category, String> colId,colCategory,colDescription;
    @FXML private Label warning;
    @FXML private TextField fieldId, fieldCategory, fieldSearch;
    @FXML private TextArea fieldDescription;
    @FXML private TableColumn<Category, Boolean> colSelect;

    private Logger logger = Logger.getLogger(this.getClass().getName());

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
        Scene scene = new Scene(page);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void findCategory() {
        String keyword = fieldSearch.getText().trim().toLowerCase();
        System.out.println("Keyword dicari: '" + keyword + "'");

        if (keyword.isEmpty()) {
            System.out.println("Keyword kosong, tampilkan semua kategori.");
            loadCategoryTable(); // Tampilkan semua jika kosong
            return;
        }

        // Pecah keyword jadi kata-kata individual
        String[] keywords = keyword.split("\\s+");
        System.out.println("Kata kunci dipecah:");
        for (String k : keywords) {
            System.out.println("- " + k);
        }

        List<Category> categories = db.getCategoryList();
        System.out.println("Jumlah kategori dari DB: " + categories.size());

        ObservableList<Category> filteredList = FXCollections.observableArrayList();

        for (Category m : categories) {
            String combined = String.join(" ",
                    m.getId() != null ? m.getId().toLowerCase() : "",
                    m.getCategory() != null ? m.getCategory().toLowerCase() : "",
                    m.getDescription() != null ? m.getDescription().toLowerCase() : ""
            );

            System.out.println("Periksa kategori: " + combined);

            boolean match = true;
            for (String key : keywords) {
                boolean contains = combined.contains(key);
                System.out.println(" - Cocok dengan '" + key + "'? " + contains);
                if (!contains) {
                    match = false;
                    break;
                }
            }

            if (match) {
                System.out.println(" -> DITAMBAHKAN");
                filteredList.add(m);
            } else {
                System.out.println(" -> TIDAK COCOK");
            }
        }

        System.out.println("Total hasil pencarian: " + filteredList.size());
        tableCategory.setItems(filteredList);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colSelect.setCellValueFactory(new PropertyValueFactory<>("select"));
        loadCategoryTable();
        btnName.setText("Hi, " + Admin.nama_user + "!");

    }
    private void loadCategoryTable() {
        List<Category> categories = db.getCategoryList();

        // Set checkbox action listener
        for (Category category : categories) {
            CheckBox checkBox = category.getSelect();

            // Set listener untuk checkbox agar tidak bisa lebih dari satu yang dicentang
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    // Jika checkbox dicentang, tampilkan data ke textfield
                    fieldId.setText(category.getId());
                    fieldCategory.setText(category.getCategory());
                    fieldDescription.setText(category.getDescription());
                } else {
                    clearFields();
                }
            });
        }

        // Set listener untuk klik baris
        tableCategory.setRowFactory(tv -> {
            TableRow<Category> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Category category = row.getItem();
                    // Set checkbox untuk baris yang diklik
                    category.getSelect().setSelected(true);
                    // Tampilkan data baris ke text field
                    fieldId.setText(category.getId());
                    fieldCategory.setText(category.getCategory());
                    fieldDescription.setText(category.getDescription());

                    // Uncheck checkbox pada semua baris lainnya
                    for (Category otherCategory : tableCategory.getItems()) {
                        if (otherCategory != category) {
                            otherCategory.getSelect().setSelected(false);
                        }
                    }
                }
            });
            return row;
        });

        ObservableList<Category> observableList = FXCollections.observableArrayList(categories);
        tableCategory.setItems(observableList);
    }



    @FXML
    private void addCategory() {
        String id = fieldId.getText().trim();
        String category = fieldCategory.getText().trim();
        String description = fieldDescription.getText().trim();

        // Pengecekan apakah ada field yang kosong
        if (category.isEmpty() || description.isEmpty() || id.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please fill in all required fields before proceeding.");
            alert.showAndWait();
            System.out.println("Kosong kategori");
        }else {
            // Ambil list kategori dari database untuk pengecekan ID
            List<Category> categories = db.getCategoryList();

            // Pengecekan apakah ID sudah ada di list kategori
            for (Category existingCategory : categories) {
                if (existingCategory.getId().equals(id)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Duplicate Category ID");
                    alert.setHeaderText("Category ID Already Exists");
                    alert.setContentText("The Category ID you entered is already registered. Please enter a different Category ID.");
                    alert.showAndWait();
                    return;
                }
            }

            try {
                // Jika semua pengecekan lolos, masukkan data ke database
                db.insertCategory(id, category, description);
                clearFields();
                loadCategoryTable();
            } catch (Exception e) {

            }
        }


    }


    @FXML
    public void editCategory() {
        String id = fieldId.getText().trim();
        String category = fieldCategory.getText().trim();
        String description = fieldDescription.getText().trim();

        if (category.isEmpty() || description.isEmpty() || id.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please fill in all required fields before proceeding.");
            alert.showAndWait();
            return;
        }
        // Ambil list kategori dari database untuk pengecekan ID
        List<Category> categories = db.getCategoryList();
        // Pengecekan apakah ID sudah ada di list kategori
        for (Category existingCategory : categories) {
            if (existingCategory.getId().equals(id)) {
                if(!db.updateCategory(id, category, description)){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Category Not Found");
                    alert.setHeaderText("Category ID Not Found");
                    alert.setContentText("The Student ID you entered does not exist in the system. Please check and try again.");
                    alert.showAndWait();

                }else{
                    clearFields();
                    loadCategoryTable();
                }

                return;
            }
        }


    }

    @FXML
    public void deleteCategory() {
        String id = fieldId.getText();

        if (id.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Missing Required Fields");
            alert.setContentText("Please fill in all required fields before proceeding.");
            alert.showAndWait();
            return;
        }

        List<Category> categories = db.getCategoryList();
        boolean found = false;

        for (Category existingCategory : categories) {
            if (existingCategory.getId().equals(id)) {
                found = true;

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Are you sure you want to delete this data? (Category : " + existingCategory.getCategory() + ")");
                alert.setContentText("This action cannot be undone.");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        db.deleteCategory(id);
                        clearFields();
                        loadCategoryTable();
                    }
                });
                break;
            }
        }

        if (!found) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Category Not Found");
            alert.setHeaderText("Category ID Not Found");
            alert.setContentText("The Category ID you entered does not exist in the system. Please check and try again.");
            alert.showAndWait();
        }
    }


    private void clearFields() {
        fieldId.clear();
        fieldCategory.clear();
        fieldDescription.clear();
    }

    @FXML
    void clearCategory() {
        fieldSearch.clear();
        loadCategoryTable(); // Kembalikan ke semua data
    }
}
