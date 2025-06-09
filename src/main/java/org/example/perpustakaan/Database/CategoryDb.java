package org.example.perpustakaan.Database;

import org.example.perpustakaan.Model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class CategoryDb {
    Db db = new Db();
    private Connection connection;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public void insertCategory(String id,String category, String description) {
        String query = "INSERT INTO kategori (id_kategori,nama_kategori, deskripsi) VALUES (?, ?, ?)";
        connection = db.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            statement.setString(2, category);
            statement.setString(3, description);
            statement.executeUpdate();
            logger.info("Category Inserted");
            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Insert category failed: " + e.toString());
        }
    }

    public ArrayList<Category> getCategoryList() {
        ArrayList<Category> list = new ArrayList<>();
        String query = "SELECT * FROM kategori";

        connection = db.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Category cat = new Category(
                        rs.getString("id_kategori"),
                        rs.getString("nama_kategori"),
                        rs.getString("deskripsi"),
                        false
                );
                list.add(cat);
            }
            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Error retrieving categories: " + e.toString());
        }

        return list;
    }


    public boolean updateCategory(String id, String category, String description) {
        String query = "UPDATE kategori SET nama_kategori = ?, deskripsi = ? WHERE id_kategori = ?";
        connection = db.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category);
            statement.setString(2, description);
            statement.setString(3, id);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Category updated successfully for ID: " + id);
                return true;
            } else {
                logger.warning("No category found with ID: " + id);
            }
            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Update category failed: " + e.toString());
        }
        return false;
    }

    public void deleteCategory(String id) {
        String query = "DELETE FROM kategori WHERE id_kategori = ?";
        connection = db.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Category with ID " + id + " deleted successfully.");
            } else {
                logger.warning("No category found with ID: " + id);
            }

            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Delete category failed: " + e.toString());
        }
    }

    public void deleteEmptyCategoryIds() {
        String query = "DELETE FROM kategori WHERE LENGTH(id_kategori) = 0";
        Connection conn = db.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted + " baris dengan id_kategori kosong telah dihapus.");
            db.closeConnection();
        } catch (SQLException e) {
            System.out.println("Gagal menghapus data: " + e.getMessage());
        }
    }



}
