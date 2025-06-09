package org.example.perpustakaan.Database;

import org.example.perpustakaan.Model.BookCopy;
import org.example.perpustakaan.Model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class BookCopyDb {
    Db db = new Db();
    private Connection connection;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public ArrayList<BookCopy> getBookCopyList(String idBuku)  {
        ArrayList<BookCopy> list = new ArrayList<>();
        String query = "SELECT * FROM copy_buku WHERE id_buku = ?";

        connection = db.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, idBuku); // Mengisi parameter pertama dengan idBuku

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookCopy cat = new BookCopy(
                            rs.getString("id_copy"),
                            rs.getString("id_buku"),
                            rs.getString("status_peminjaman"),
                            rs.getString("tanggal_peminjaman"),
                            rs.getString("tanggal_pengembalian"),
                            false
                    );
                    list.add(cat);
                }
            }
            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Error retrieving Copy Book: " + e.toString());
        }

        return list;
    }


    public void insertBookCopy(String id_copy,String id_book, String status, String tanggal_peminjaman, String tanggal_pengembalian) {
        String query = "INSERT INTO copy_buku (id_copy,id_buku, status_peminjaman,tanggal_peminjaman, tanggal_pengembalian ) VALUES (?, ?, ?, ?, ?)";
        connection = db.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id_copy);
            statement.setString(2, id_book);
            statement.setString(3, status);
            statement.setString(4, tanggal_peminjaman);
            statement.setString(5, tanggal_pengembalian);
            statement.executeUpdate();
            logger.info("CopyBuku Inserted");
            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Insert CopyBuku failed: " + e.toString());
        }
    }

    public void deleteCopyBuku(String id) {
        String query = "DELETE FROM copy_buku WHERE id_copy = ?";
        connection = db.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("BUku Copy with ID " + id + " deleted successfully.");
            } else {
                logger.warning("No BUku Copy found with ID: " + id);
            }

            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Delete BUku Copy failed: " + e.toString());
        }
    }


}
