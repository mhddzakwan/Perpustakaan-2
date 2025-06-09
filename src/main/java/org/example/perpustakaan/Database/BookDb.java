package org.example.perpustakaan.Database;

import org.example.perpustakaan.Model.Book;
import org.example.perpustakaan.Model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class BookDb {
    Db db = new Db();
    private Connection connection;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public void insertBook(String id,String title, String category, String author, String released, String photo ) {
        String query = "INSERT INTO buku (id_buku,nama_buku, id_kategori, penulis,rilis,gambar) VALUES (?, ?, ?, ?, ?, ?)";
        connection = db.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            statement.setString(2, title);
            statement.setString(3, category);
            statement.setString(4, author);
            statement.setString(5, released);
            statement.setString(6, photo);
            statement.executeUpdate();
            System.out.println("Book Inserted");
            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Insert book failed: " + e.toString());
        }
    }

    public ArrayList<Book> getBookList() {
        ArrayList<Book> list = new ArrayList<>();
        String query = "SELECT * FROM buku";

        connection = db.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Book cat = new Book(
                        rs.getString("id_buku"),
                        rs.getString("nama_buku"),
                        rs.getString("id_kategori"),
                        rs.getString("penulis"),
                        rs.getString("rilis"),
                        rs.getString("gambar"),
                        false
                );
                list.add(cat);
            }
            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Error retrieving book: " + e.toString());
        }

        return list;
    }

    public void deleteCopyBook(String id){
        String query = "DELETE FROM copy_buku WHERE id_buku = ?";
        connection = db.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("BookCopy with ID " + id + " deleted successfully.");
            } else {
                logger.warning("No BookCopy found with ID: " + id);
            }

            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Delete BookCopy failed: " + e.toString());
        }
    }

    public void deleteBook(String id) {
        deleteCopyBook(id);
        String query = "DELETE FROM buku WHERE id_buku = ?";
        connection = db.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Book with ID " + id + " deleted successfully.");
            } else {
                logger.warning("No Book found with ID: " + id);
            }

            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Delete Book failed: " + e.toString());
        }
    }

    public void updateBook(String id,String title, String category, String author, String released, String photo ) {
        String query = "UPDATE buku SET nama_buku = ?, id_kategori = ?, penulis = ?, rilis = ?, gambar = ? WHERE id_buku = ?";
        connection = db.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, title);
            statement.setString(2, category);
            statement.setString(3, author);
            statement.setString(4, released);
            statement.setString(5, photo);
            statement.setString(6, id);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Book updated successfully for ID: " + id);
            } else {
                logger.warning("No Book found with ID: " + id);
            }

            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Update book failed: " + e.toString());
        }
    }

    public String countDashboard(String tabel) {
        connection = db.getConnection();
        String query = "SELECT COUNT(*) FROM " + tabel;
        String totalRows = "0";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                totalRows = String.valueOf(resultSet.getInt(1));
            }

            logger.info("Total rows in copy_buku: " + totalRows);
            db.closeConnection();
        } catch (SQLException e) {
            logger.info(e.toString());
        }

        return totalRows;
    }

    public String countDashboard(String tabel, String condition) {
        connection = db.getConnection();
        String query = "SELECT COUNT(*) FROM " + tabel + " WHERE " + condition;
        String totalRows = "0";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                totalRows = String.valueOf(resultSet.getInt(1));
            }

            logger.info("Total rows in " + tabel + ": " + totalRows);
            db.closeConnection();
        } catch (SQLException e) {
            logger.info(e.toString());
        }

        return totalRows;
    }


}
