package com.example.phongvanrestaurant.models;

import java.util.Date;

public class Product {
    private int ID;
    private String itemName;
    private int quantity;
    private int categoryId;
    private double price;
    private double discount;
    private String image;
    private Date dateCreate;
    private Date dateUpdate;
    private String categoryType;

    public Product(int ID, String itemName, int quantity, int categoryId, double price,
                    double discount, String image, Date dateCreate, Date dateUpdate, String categoryType) {
        this.ID = ID;
        this.itemName = itemName;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.price = price;
        this.discount = discount;
        this.image = image;
        this.dateCreate = dateCreate;
        this.dateUpdate = dateUpdate;
        this.categoryType = categoryType;
    }

    public Product() {
    }

    public Product(int ID, String itemName, int quantity, double price, double discount, String categoryType, String image){
        this.ID = ID;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.categoryType= categoryType;
        this.image = image;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Date getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(Date dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public void setCategoryType(String categoryType){
        this.categoryType = categoryType;
    }

    public String getCategoryType(){
        return  categoryType;
    }

    @Override
    public String toString() {
        return "Product ID: " + ID + ", Name: " + itemName + ", Quantity: " + quantity +
                ", Price: " + price + ", Discount: " + discount + ", Date Created: " + dateCreate +
                ", Date Updated: " + dateUpdate;
    }
}

