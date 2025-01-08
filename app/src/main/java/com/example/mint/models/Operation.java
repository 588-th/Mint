package com.example.mint.models;

import android.graphics.Bitmap;

public class Operation {

    //region Fields
    private long id;
    private int categoryId;
    private OperationType type;
    private double sum;
    private String comment;
    private String date;
    private Bitmap photo;
    //endregion

    //region Constructor
    public Operation(long id, int categoryId, OperationType type, double sum, String comment, String date, Bitmap photo) {
        this.id = id;
        this.categoryId = categoryId;
        this.type = type;
        this.sum = sum;
        this.comment = comment;
        this.date = date;
        this.photo = photo;
    }
    //endregion

    //region Getters
    public long getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public OperationType getType() {
        return type;
    }

    public double getSum() {
        return sum;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    //endregion

    //region Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public void setSum(double sum) {
        if (sum > 0){
            this.sum = sum;
        }
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
    //endregion
}