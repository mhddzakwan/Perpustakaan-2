package org.example.perpustakaan.Database;

import org.example.perpustakaan.Model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ReserveDb {
    Db db = new Db();
    private Connection connection;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public Member getMember(String idMember) {
        Member member = null;
        String query = "SELECT * FROM anggota WHERE nim = ?";
        connection = db.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, idMember);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    member = new Member(
                            rs.getString("nama_anggota"),
                            rs.getString("email"),
                            rs.getString("no_hp"),
                            rs.getString("prodi"),
                            rs.getString("foto")
                    );
                }
            }
            db.closeConnection();
        } catch (SQLException e) {
            System.out.println("Error retrieving book: " + e.toString());
        }
        if (member != null) {
            System.out.println("Member ditemukan");
        } else {
            System.out.println("Member tidak ditemukan.");
        }

        return member;
    }

    public Book getBook(String idBook) {
        Book book = null;
        String query = "SELECT " +
                "copy_buku.id_copy, copy_buku.status_peminjaman, copy_buku.tanggal_peminjaman, copy_buku.tanggal_pengembalian, " +
                "buku.id_buku, buku.nama_buku, buku.id_kategori, buku.penulis, buku.rilis, buku.gambar " +
                "FROM copy_buku " +
                "JOIN buku ON copy_buku.id_buku = buku.id_buku " +
                "WHERE copy_buku.id_copy = ?";

        connection = db.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, idBook);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    book = new Book(
                            rs.getString("nama_buku"),
                            rs.getString("id_kategori"),
                            rs.getString("penulis"),
                            rs.getString("gambar")
                    );
                }
            }
            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Error retrieving book: " + e.toString());
        }

        return book;
    }

    public void insertReserve(String id_peminjaman,String id_buku, String nim, String nama, String judul, String tanggal_peminjaman, String tenggat, String tanggal_pengembalian, int denda, String status) {
        String query = "INSERT INTO peminjaman (id_peminjaman, id_buku , nim ,nama ,judul ,tanggal_peminjaman ,tenggat,tanggal_pengembalian,denda,status    ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,  ?)";
        connection = db.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id_peminjaman);
            statement.setString(2, id_buku);
            statement.setString(3, nim);
            statement.setString(4, nama);
            statement.setString(5, judul);
            statement.setString(6, tanggal_peminjaman);
            statement.setString(7, tenggat);
            statement.setString(8, tanggal_pengembalian);
            statement.setInt(9, denda);
            statement.setString(10, status);
            statement.executeUpdate();
            logger.info("Reserve Inserted");
            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Insert reserve failed: " + e.toString());
        }

        String query2 = "UPDATE copy_buku SET status_peminjaman = ? ,tanggal_peminjaman = ?, tanggal_pengembalian = ? WHERE id_copy = ?";
        connection = db.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query2)) {
            statement.setString(1, "Dipinjam");
            statement.setString(2, tanggal_peminjaman);
            statement.setString(3, tenggat);
            statement.setString(4, id_buku);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info(" updated successfully for ID: " + id_buku);
            } else {
                logger.warning("No  found with ID: " + id_buku);
            }

            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Update copy_book failed: " + e.toString());
        }


    }

    public ArrayList<Issue> getIssueList() {
        ArrayList<Issue> list = new ArrayList<>();

        String query = "SELECT " +
                "peminjaman.id_detail," +
                "peminjaman.id_peminjaman," +
                "peminjaman.id_buku," +
                "peminjaman.nim," +
                "peminjaman.nama," +
                "peminjaman.judul," +
                "peminjaman.tanggal_peminjaman," +
                "peminjaman.tenggat," +
                "peminjaman.tanggal_pengembalian," +
                "peminjaman.denda," +
                "peminjaman.status," +
                "anggota.foto, " +
                "buku.gambar " +
                "FROM peminjaman " +
                "JOIN anggota ON peminjaman.nim = anggota.nim " +
                "JOIN copy_buku ON copy_buku.id_copy = peminjaman.id_buku " +
                "JOIN buku ON copy_buku.id_buku = buku.id_buku ";


        connection = db.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {

                System.out.println("Loop");
                Issue cat = new Issue(
                        rs.getString("id_detail"),
                        rs.getString("id_peminjaman"),
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("id_buku"),
                        rs.getString("judul"),
                        rs.getString("tanggal_peminjaman"),
                        rs.getString("tenggat"),
                        rs.getString("tanggal_pengembalian"),
                        rs.getInt("denda"),
                        rs.getString("status"),
                        rs.getString("foto"),
                        rs.getString("gambar")
//                        rs.getString("id_detail"),
//                        rs.getString("id_peminjaman"),
//                        rs.getString("id_buku"),
//                        rs.getString("nim"),
//                        rs.getString("nama"),
//                        rs.getString("judul"),
//                        rs.getString("tanggal_peminjaman"),
//                        rs.getString("tenggat"),
//                        rs.getString("tanggal_pengembalian"),
//                        rs.getInt("denda"),
//                        rs.getString("status"),
//                        rs.getString("foto"),
//                        rs.getString("gambar")

                );
                list.add(cat);
            }
            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Error retrieving categories: " + e.toString());
        }
        if (list.isEmpty()){
            System.out.println("Kosong");
        }else{
            System.out.println("ADA");
        }

        return list;
    }

    public boolean deleteIssue(String id_detail){
        String query = "DELETE FROM peminjaman WHERE id_detail = ?";
        connection = db.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id_detail);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Category with ID " + id_detail + " deleted successfully.");
            } else {
                logger.warning("No category found with ID: " + id_detail);
            }

            db.closeConnection();
            return true;
        } catch (SQLException e) {
            logger.severe("Delete category failed: " + e.toString());
            return false;
        }
    }

    // Book Return
    public ArrayList<ReturnBook> getReturnList(String idMember) {
        ReturnBook book = null;
        String query = "SELECT " +
                "peminjaman.id_detail, " +
                "peminjaman.id_buku, " +
                "peminjaman.judul, " +
                "peminjaman.tanggal_peminjaman, " +
                "peminjaman.tenggat, " +
                "buku.gambar, " +
                "buku.penulis, " +
                "kategori.nama_kategori " +
                "FROM peminjaman " +
                "JOIN anggota ON peminjaman.nim = anggota.nim " +
                "JOIN copy_buku ON copy_buku.id_copy = peminjaman.id_buku " +
                "JOIN buku ON copy_buku.id_buku = buku.id_buku  " +
                "JOIN kategori ON buku.id_kategori = kategori.nama_kategori " +
                "WHERE peminjaman.nim = ? AND peminjaman.status = 'Dipinjam' ";

        connection = db.getConnection();
        ArrayList<ReturnBook> list = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, idMember);
            System.out.println(idMember);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { // Ganti dari if menjadi while
                    book = new ReturnBook(
                            rs.getString("id_detail"),
                            rs.getString("id_buku"),
                            rs.getString("judul"),
                            rs.getString("nama_kategori"),
                            rs.getString("penulis"),
                            rs.getString("tanggal_peminjaman"),
                            rs.getString("tenggat"),
                            rs.getString("gambar")
                    );
                    list.add(book);
                }
            }
            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Error retrieving book: " + e.toString());
        }
        if(list.isEmpty()){
            System.out.println("Return Book Kosong");
        }
        return list;
    }

    public void updateReserved(String tanggal_pengembalian, String id_detail, String id_buku, int denda){
        String query = "UPDATE peminjaman SET tanggal_pengembalian = ?, status = ?, denda = ? WHERE id_detail = ?";
        connection = db.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tanggal_pengembalian);
            statement.setString(2, "Dikembalikan");
            statement.setInt(3, denda);
            statement.setString(4, id_detail);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Return updated successfully for ID: " + id_detail);
            } else {
                logger.warning("No Return found with ID: " + id_detail);
            }

            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Update category failed: " + e.toString());
        }

        String query2 = "UPDATE copy_buku SET status_peminjaman = ? ,tanggal_peminjaman = ?, tanggal_pengembalian = ? WHERE id_copy = ?";
        connection = db.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query2)) {
            statement.setString(1, "Tersedia");
            statement.setString(2, "-");
            statement.setString(3, "-");
            statement.setString(4, id_buku);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info(" updated successfully for ID: " + id_buku);
            } else {
                logger.warning("No  found with ID: " + id_buku);
            }

            db.closeConnection();
        } catch (SQLException e) {
            logger.severe("Update copy_book failed: " + e.toString());
        }
    }

    public boolean cekPinjamBuku(String id_book){
        String query = "SELECT * FROM copy_buku WHERE id_copy = ? AND status_peminjaman = 'Tersedia'";
        connection = db.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id_book);
            ResultSet resultSet = statement.executeQuery();

            boolean available = resultSet.next(); // true jika ada hasil (berarti tersedia)
            db.closeConnection();

            if (available) {
                logger.info("Copy book with ID " + id_book + " is available for borrowing.");
                return true;
            } else {
                logger.warning("No available copy found with ID: " + id_book);
                return false;
            }

        } catch (SQLException e) {
            logger.severe("Check book availability failed: " + e.toString());
            return false;
        }
    }



}
