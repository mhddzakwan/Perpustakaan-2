module org.example.perpustakaan {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires java.desktop;
    requires escpos.coffee;

    opens org.example.perpustakaan to javafx.fxml;
    exports org.example.perpustakaan;
    exports org.example.perpustakaan.Database;
    opens org.example.perpustakaan.Database to javafx.fxml;
    exports org.example.perpustakaan.Controller;
    opens org.example.perpustakaan.Controller to javafx.fxml;
    exports org.example.perpustakaan.Model;
    opens org.example.perpustakaan.Model to javafx.base, javafx.fxml;
    exports org.example.perpustakaan.Adapter;
    opens org.example.perpustakaan.Adapter to javafx.base, javafx.fxml;
    exports org.example.perpustakaan.File;
    opens org.example.perpustakaan.File to javafx.fxml;
}