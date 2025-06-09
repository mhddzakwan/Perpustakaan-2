package org.example.perpustakaan.Model;

import javafx.scene.control.CheckBox;

public class Category {
    private String id;
    private String category;
    private String description;
//    private boolean selected = false;
    private CheckBox select;

    public Category(String id, String category, String description, boolean selected ){
        this.id = id;
        this.category = category;
        this.description = description;
        this.select = new CheckBox();
    }

    // Getter dan Setter
    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }

    public CheckBox getSelect(){
        return  select;
    }
    @Override
    public String toString() {
        return id + " - " + category;
    }
    public void setSelect(CheckBox select){
        this.select = select;
    }


}
