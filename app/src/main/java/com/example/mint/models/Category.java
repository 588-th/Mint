package com.example.mint.models;

import android.graphics.Bitmap;

public class Category {

    //region Fields
    private long id;
    private String name;
    private Bitmap icon;
    //endregion

    //region Constructors
    public Category(long id, String name, Bitmap icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }
    //endregion

    //region Getters
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Bitmap getIcon() {
        return icon;
    }
    //endregion

    //region Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
    //endregion
}
