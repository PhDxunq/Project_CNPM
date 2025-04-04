package com.example.phongvanrestaurant.models;

import java.util.Date;

public class InventoryData {
    private Integer id;
    private String itemname;
    private Integer quantity;
    private Integer categoryid;
    private Double price;
    private Double discount;
    private String image;
    private Date datecreate;
    private Date dateupdate;

    public InventoryData(Integer id, String itemname, Integer quantity, Integer categoryid, Double price, Double discount, String image, Date datecreate, Date dateupdate) {
        this.id = id;
        this.itemname = itemname;
        this.quantity = quantity;
        this.categoryid = categoryid;
        this.price = price;
        this.discount = discount;
        this.image = image;
        this.datecreate = datecreate;
        this.dateupdate = dateupdate;
    }

    public InventoryData() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(Integer categoryid) {
        this.categoryid = categoryid;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getDatecreate() {
        return datecreate;
    }

    public void setDatecreate(Date datecreate) {
        this.datecreate = datecreate;
    }

    public Date getDateupdate() {
        return dateupdate;
    }

    public void setDateupdate(Date dateupdate) {
        this.dateupdate = dateupdate;
    }
}
