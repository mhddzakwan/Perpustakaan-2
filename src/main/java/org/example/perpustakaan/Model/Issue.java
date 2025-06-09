package org.example.perpustakaan.Model;
import javafx.scene.control.CheckBox;

public class Issue {
    private String id_detail;
    private String id;
    private String memberId;
    private String name;
    private String bookId;
    private String title;
    private String loanDate;
    private String deadline;
    private String returnDate;
    private int fine;
    private String status;
    private CheckBox select;
    private String foto;
    private String gambar;
    // This checkbox will be used to select/unselect rows
    public Issue(String id_detail, String id, String memberId, String name, String bookId, String title,
                 String loanDate, String deadline, String returnDate, int fine,
                 String status, String foto, String gambar) {
        this.id_detail = id_detail;
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.bookId = bookId;
        this.title = title;
        this.loanDate = loanDate;
        this.deadline = deadline;
        this.returnDate = returnDate;
        this.fine = fine;
        this.status = status;
        this.select = new CheckBox();
        this.gambar = gambar;
        this.foto = foto;
    }


    public String getId_detail() {
        return id_detail;
    }

    public void setId_detail(String id_detail) {
        this.id_detail = id_detail;
    }


    // Getters and setters for each field
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLoanDate() { return loanDate; }
    public void setLoanDate(String loanDate) { this.loanDate = loanDate; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    public int getFine() { return fine; }
    public void setFine(int fine) { this.fine = fine; }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public CheckBox getSelect() { return select; }
    public void setSelect(CheckBox select) { this.select = select; }
}

