package com.example.phongvanrestaurant.models;

public class CategoryData {
    private Integer id;
    private String categoryname;

    public CategoryData(Integer id, String categoryname) {
        this.id = id;
        this.categoryname = categoryname;
    }

    public CategoryData() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    @Override
    public String toString() {
        return id + " - " + categoryname;
    }
}
