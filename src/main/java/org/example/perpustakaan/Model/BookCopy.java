package org.example.perpustakaan.Model;

import javafx.scene.control.CheckBox;

public class BookCopy {
    private String id_copy;
    private String id_buku;
    private String status;
    private String tanggal_peminjaman;
    private String tanggal_kembali;
    private CheckBox select;

    public BookCopy(String id_copy, String id_buku, String status, String tanggal_peminjaman, String tanggal_kembali, boolean select) {
        this.id_copy = id_copy;
        this.id_buku = id_buku;
        this.status = status;
        this.tanggal_peminjaman = tanggal_peminjaman;
        this.tanggal_kembali = tanggal_kembali;
        this.select = new CheckBox();
    }

    public String getId_copy() {
        return id_copy;
    }

    public String getId_buku() {
        return id_buku;
    }

    public String getStatus() {
        return status;
    }

    public String getTanggal_peminjaman() {
        return tanggal_peminjaman;
    }

    public String getTanggal_kembali() {
        return tanggal_kembali;
    }

    public CheckBox getSelect() {
        return select;
    }
}
