package org.example.perpustakaan.Adapter;

import org.example.perpustakaan.Model.BookRow;
import org.example.perpustakaan.Model.Printer;

import javax.print.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Adapter untuk mencetak menggunakan printer thermal.
 * Mengimplementasikan interface Printer dan menyediakan metode
 * untuk mencetak teks dan barcode ke printer thermal.
 */
public class ThermalPrinterAdapter implements Printer {

    /**
     * Mencetak struk teks peminjaman ke printer thermal.
     *
     * @param idPeminjaman ID dari transaksi peminjaman
     * @param nama         Nama peminjam
     * @param nim          NIM peminjam
     * @param tanggalPinjam Tanggal peminjaman
     * @param tenggat      Tanggal tenggat pengembalian
     * @param books        Daftar buku yang dipinjam
     */
    public void printText(String idPeminjaman, String nama, String nim, String tanggalPinjam, String tenggat, List<?> books) throws Exception {
        StringBuilder sb = new StringBuilder();

        // ESC a 1 : Center alignment
        sb.append((char) 27).append((char) 97).append((char) 1);
        sb.append("PERPUSTAKAAN\n========================\n\n");

        // ESC a 0 : Left alignment
        sb.append((char) 27).append((char) 97).append((char) 0);
        sb.append("ID Peminjaman : ").append(idPeminjaman).append("\n");
        sb.append("Nama          : ").append(nama).append("\n");
        sb.append("NIM           : ").append(nim).append("\n");
        sb.append("Peminjaman    : ").append(tanggalPinjam).append("\n");
        sb.append("Deadline      : ").append(tenggat).append("\n");
        sb.append("----------------------------\nDaftar Buku:\n");

        // Menambahkan daftar buku ke teks
        for (Object obj : books) {
            BookRow book = (BookRow) obj;
            sb.append("- ").append(book.getId()).append(" : ").append(book.getTitle()).append("\n");
        }

        sb.append("\nTerima kasih!\n============================\n");

        // Beri jarak 1 line
        sb.append((char) 27).append((char) 100).append((char) 1);

        // Mengakhiri Teks yg akan ditampilkan
        sb.append((char) 29).append((char) 86).append((char) 66).append((char) 0);

        // Mengubah string ke byte array dengan encoding Cp437 (kompatibel dengan banyak printer thermal)
        byte[] bytes = sb.toString().getBytes("Cp437");

        // Kirim ke printer
        sendToPrinter(bytes);
    }

    /**
     * Mencetak barcode berdasarkan ID peminjaman ke printer thermal.
     *
     * @param id ID yang ingin dicetak sebagai barcode
     */
    @Override
    public void printBarcode(String id) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(new byte[]{0x1B, 0x61, 0x01}); // Center alignment
        outputStream.write("PERPUSTAKAAN\n\n".getBytes("Cp437"));

        outputStream.write(new byte[]{0x1D, 0x48, 0x02}); // Print HRI (text) below barcode
        outputStream.write(new byte[]{0x1D, 0x66, 0x00}); // Font A
        outputStream.write(new byte[]{0x1D, 0x77, 0x02}); // Barcode width
        outputStream.write(new byte[]{0x1D, 0x68, 0x50}); // Barcode height

        outputStream.write(new byte[]{0x1D, 0x6B, 0x04}); // Code39 barcode
        outputStream.write(id.getBytes("US-ASCII")); // Data barcode
        outputStream.write(0x00); // Null terminator

        outputStream.write("\n\n".getBytes("Cp437"));

        // Potong kertas
        outputStream.write(new byte[]{0x1D, 0x56, 0x42, 0x00});

        sendToPrinter(outputStream.toByteArray());
    }

    /**
     * Mengirim data byte array ke printer thermal yang ditemukan.
     *
     * @param data Byte array yang berisi data cetakan
     */
    private void sendToPrinter(byte[] data) throws Exception {
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

        // Mencari semua printer yang mendukung byte array
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        PrintService thermalPrinter = null;

        // Memilih printer dengan nama mengandung kata 'thermal'
        for (PrintService service : services) {
            if (service.getName().toLowerCase().contains("thermal")) {
                thermalPrinter = service;
                break;
            }
        }

        if (thermalPrinter != null) {
            DocPrintJob job = thermalPrinter.createPrintJob();
            Doc doc = new SimpleDoc(data, flavor, null);
            job.print(doc, null);
        } else {
            System.out.println("PrinterThermal thermal tidak ditemukan.");
        }
    }

}
