package org.example.perpustakaan.Model;

import javafx.scene.control.CheckBox;

public class Book {
    private String id;
    private String title;
    private String category;
    private String author;
    private String released;
    private String photo;
    private CheckBox select;

    // Static = Akses public, tanpa perlu membuat objek (tidak perlu melakukan new Book)
    public static String idBook, titleBook, authorBook;


    public Book(String id, String title, String category, String author, String released, String photo, boolean select) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.author = author;
        this.released = released;
        this.photo = photo;
        this.select = new CheckBox();
    }

    public Book(String title, String category, String author, String photo){
        this.title = title;
        this.category = category;
        this.author = author;
        this.photo = photo;
    }
    public Book(String title, String author){
        this.title = title;
        this.author = author;

    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getReleased() {
        return released;
    }

    public String getPhoto() {
        return photo;
    }

    public CheckBox getSelect() {
        return select;
    }
}
