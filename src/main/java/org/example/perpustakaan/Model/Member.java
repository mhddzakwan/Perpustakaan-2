package org.example.perpustakaan.Model;

import javafx.scene.control.CheckBox;

import java.util.logging.Logger;

public class Member extends Person {
    private String id, department, photo;
    private CheckBox select;
    private Logger logger = Logger.getLogger(this.getClass().getName());


    public Member(String id, String name, String email, String phone, String department, String photo) {
        super(name, email, phone);
        this.id = id;
        this.department = department;
        this.photo = photo;
        this.select = new CheckBox();
    }

    // Untuk ngambil data dari ReserveDb
    public Member(String name, String email, String phone, String department, String photo){
        super(name, email, phone);
        this.department = department;
        this.photo = photo;

    }

    public static Member member;

    public String getId() { return id; }
    public String getDepartment() { return department; }
    public String getPhoto() { return photo; }
    public CheckBox getSelect() { return select; }

    public void setSelect(CheckBox select) {
        this.select = select;
    }
}
