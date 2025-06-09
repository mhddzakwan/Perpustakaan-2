package org.example.perpustakaan.Model;

public class Admin extends Person {
    private int id;
    public static String nama_user = "";

    public Admin(int id, String name, String email, String phoneNumber) {
        super(name, email, phoneNumber); // memanggil constructor dari Person
        this.id = id;
    }

    public int getId() { return id; }

}
