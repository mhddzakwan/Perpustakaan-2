package org.example.perpustakaan.Model;

import javafx.scene.control.CheckBox;

public class ReturnBook {
    private String idDetail;
    private String bookId;
    private String title;
    private String category;
    private String author;

    private String loanDate;
    private String deadline;
    private CheckBox select;
    private String gambar;

    public ReturnBook(String idDetail, String bookId, String title, String category, String author , String loanDate, String deadline, String gambar){
        this.idDetail = idDetail;
        this.bookId = bookId;
        this.title = title;
        this.category = category;
        this.author = author;
        this.gambar = gambar;
        this.loanDate = loanDate;
        this.deadline = deadline;

        this.select = new CheckBox();
    }
    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public CheckBox getSelect() {
        return select;
    }

    public void setSelect(CheckBox select) {
        this.select = select;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
