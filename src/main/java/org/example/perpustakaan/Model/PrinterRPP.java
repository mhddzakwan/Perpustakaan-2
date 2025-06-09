package org.example.perpustakaan.Model;

import java.util.List;

public class PrinterRPP implements Printer {
    @Override
    public void printText(String idPeminjaman, String nama, String nim, String tanggalPinjam, String tenggat, List<?> books) throws Exception {

    }
    @Override
    public void printBarcode(String id) throws Exception {

    }

    public void print(String text) {
        System.out.println("Canon mencetak: " + text);
    }
}
