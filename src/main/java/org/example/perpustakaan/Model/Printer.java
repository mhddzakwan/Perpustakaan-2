package org.example.perpustakaan.Model;

import java.util.List;

public interface Printer {
    void printText(String idPeminjaman, String nama, String nim, String tanggalPinjam, String tenggat, java.util.List<?> books) throws Exception;
    void printBarcode(String id) throws Exception;

}
