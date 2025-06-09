package org.example.perpustakaan.Model;

public class BasicBookDisplay implements BookDisplay {
    private Book book;

    public BasicBookDisplay(Book book) {
        this.book = book;
    }

    @Override
    public String display() {
        return "Title: " + book.getTitle() + "\nAuthor: " + book.getAuthor();
    }
}

