package org.example.perpustakaan.Model;

import java.util.logging.Logger;

public class LoggingBookDecorator implements BookDisplay {
    private BookDisplay bookDisplay;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public LoggingBookDecorator(BookDisplay bookDisplay) {
        this.bookDisplay = bookDisplay;
    }

    @Override
    public String display() {
        String result = bookDisplay.display();
        logger.info("Menampilkan info buku: " + result);
        return result;
    }
}

