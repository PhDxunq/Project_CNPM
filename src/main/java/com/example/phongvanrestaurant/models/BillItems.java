package com.example.phongvanrestaurant.models;

public class BillItems {
    private int orderId;
    private String orderStatus;
    private int orderDetailsId;
    private int itemId;
    private int quantity;
    private String orderDetailsStatus;
    private int totalAmount;
    private String itemName;
    private int price;
    private int discount;


    public BillItems(int orderId, String orderStatus, int orderDetailsId, int itemId, int quantity, String orderDetailsStatus, int totalAmount, String itemName, int discount, int price) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderDetailsId = orderDetailsId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.orderDetailsStatus = orderDetailsStatus;
        this.totalAmount = totalAmount;
        this.itemName = itemName;
        this.discount = discount;
        this.price = price;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public int getOrderDetailsId() {
        return orderDetailsId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getOrderDetailsStatus() {
        return orderDetailsStatus;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public String getItemName() {
        return itemName;
    }

    public int getDiscount() {
        return discount;
    }

    public int getPrice() {
        return price;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderDetailsId(int orderDetailsId) {
        this.orderDetailsId = orderDetailsId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOrderDetailsStatus(String orderDetailsStatus) {
        this.orderDetailsStatus = orderDetailsStatus;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
