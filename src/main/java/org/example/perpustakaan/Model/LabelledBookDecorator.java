package org.example.perpustakaan.Model;

public class LabelledBookDecorator implements BookDisplay {
    protected BookDisplay bookDisplay;
    private String label;

    public LabelledBookDecorator(BookDisplay bookDisplay, String label) {
        this.bookDisplay = bookDisplay;
        this.label = label;
    }

    @Override
    public String display() {
        return label + ": " + bookDisplay.display();
    }
}
