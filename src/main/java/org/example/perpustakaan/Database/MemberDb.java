package org.example.perpustakaan.Database;

import org.example.perpustakaan.Model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MemberDb {
    Db db = new Db();
    private Connection connection;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public void insertMember(String name, String email, String phone, String department, String nim, String imageName) {
        String query = "INSERT INTO anggota (nim, nama_anggota, prodi, email, no_hp, foto) VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = db.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nim);
            statement.setString(2, name);
            statement.setString(3, department);
            statement.setString(4, email);
            statement.setString(5, phone);
            statement.setString(6, imageName);
            statement.executeUpdate();
            logger.info("Insert member success.");
            db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Member> getAllMembers() {
        List<Member> list = new ArrayList<>();
        String query = "SELECT * FROM anggota";
        connection = db.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("nim");
                String name = rs.getString("nama_anggota");
                String email = rs.getString("email");
                String phone = rs.getString("no_hp");
                String department = rs.getString("prodi");
                String photo = rs.getString("foto");
                list.add(new Member(id, name, email, phone, department, photo));
            }
            db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteMemberById(String id) {
        String query = "DELETE FROM anggota WHERE nim = ?";
        connection = db.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
            db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMember(String name, String email, String phone, String department, String nim, String imageName) throws SQLException {
        String sql = "UPDATE anggota SET nama_anggota = ?, prodi = ?, email = ?, no_hp = ?, foto = ? WHERE nim = ?";
        connection = db.getConnection();
        try ( PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, department);
            statement.setString(3, email);
            statement.setString(4, phone);
            statement.setString(5, imageName);
            statement.setString(6, nim);
            statement.executeUpdate();
        }
    }

    public boolean findNim(String nim) {
        String query = "SELECT * FROM anggota WHERE nim = ?";
        connection = db.getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nim);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return true; // NIM ditemukan
                }
            }
            db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // NIM tidak ditemukan
    }



}
