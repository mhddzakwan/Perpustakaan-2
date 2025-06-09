package org.example.perpustakaan.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.perpustakaan.Model.Admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Db {
    private Connection connection;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:sqlite:perpustakaan.db");
                logger.info("Connected Database");
                createTable();
            }
        } catch (SQLException e) {
            logger.severe("Connection failed: " + e.toString());
        }
        return connection;  // Mengembalikan koneksi yang valid
    }

    private void createTable(){
        Connection connection = getConnection();

        String[] queries = {
                "create table if not exists admin " +
                        "(id_admin integer not null primary key autoincrement, " +
                        "nama_admin text not null, " +
                        "email text not null, "+
                        "password text not null, " +
                        "no_hp text not null)",

                "create table if not exists anggota " +
                        "(nim text not null primary key, " +
                        "nama_anggota text not null, " +
                        "prodi text not null, " +
                        "email text not null, " +
                        "no_hp text not null, "+
                        "foto text not null)",

                "create table if not exists kategori " +
                        "(id_kategori text not null primary key, " +
                        "nama_kategori text not null, " +
                        "deskripsi text not null)",

                "create table if not exists buku " +
                        "(id_buku text not null primary key, " +
                        "nama_buku text not null, " +
                        "id_kategori text not null, "+
                        "penulis text not null, " +
                        "rilis text not null, " +
                        "gambar text not null)" ,

                "create table if not exists copy_buku " +
                        "(id_copy text not null primary key, " +
                        "id_buku text not null, " +
                        "status_peminjaman text not null, " +
                        "tanggal_peminjaman text , " +
                        "tanggal_pengembalian text )",

                "create table if not exists peminjaman " +
                        "(id_detail integer not null primary key AUTOINCREMENT, " +
                        "id_peminjaman text not null, " +
                        "id_buku text not null, " +
                        "nim text not null, " +
                        "nama text not null, " +
                        "judul text not null, " +
                        "tanggal_peminjaman text not null," +
                        "tenggat text not null," +
                        "tanggal_pengembalian text not null, "+
                        "denda integer not null, " +
                        "status text not null)",
        };

        try {
            for (String query : queries) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.executeUpdate();
                }
            }
            logger.info("Tables created successfully (if not exist)");
        } catch (SQLException e) {
            logger.info(e.toString());
        }
    }
    public void closeConnection() throws SQLException {
        if(connection == null || connection.isClosed()){
            connection.close();
        }
    }
    public void insertAdmin(String nama_admin, String email, String Password, String no_hp){
        connection = getConnection();
        String query = "INSERT INTO admin (nama_admin, email, password, no_hp) VALUES (?,?,?,?)";
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,nama_admin);
            statement.setString(2, email);
            statement.setString(3,Password);
            statement.setString(4, no_hp);
            statement.executeUpdate();
            logger.info("Admin Inserted");

            closeConnection();
        } catch (SQLException e){
            logger.info(e.toString());
        }
    }

    public void deleteAdmin(String email) {
        connection = getConnection();
        String query = "DELETE FROM admin WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Admin deleted: " + email);
            } else {
                logger.info("No admin found with email: " + email);
            }

            closeConnection();
        } catch (SQLException e) {
            logger.info(e.toString());
        }
    }


    public String authenticateUser(String email, String password) {
        connection = getConnection();
        String user ="";
        String query = "SELECT  nama_admin FROM admin WHERE email = ? AND password = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                user = rs.getString("nama_admin");
            }

            logger.info("Berhasil Mengambil ID : " + user);
            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public ArrayList<Admin> getAdminList() {
        ArrayList<Admin> adminList = new ArrayList<>();

        String query = "SELECT * FROM admin";
        connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Admin admin = new Admin(
                        rs.getInt("id_admin"),
                        rs.getString("nama_admin"),
                        rs.getString("email"),
                        rs.getString("no_hp")
                );
                adminList.add(admin);

            }
            closeConnection(); // Singleton

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return adminList;
    }


}
