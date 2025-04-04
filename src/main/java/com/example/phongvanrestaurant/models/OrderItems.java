package com.example.phongvanrestaurant.models;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.sql.Timestamp;

public class OrderItems {
    private int id;
    private String ItemName;
    private String ItemId;
    private int Quantity;
    private double TotalAmount;
    private ComboBox statusCombo;
    private String Status;
    private Button actionButton;

    public OrderItems(int id, String itemName, String itemId, int quantity, double totalAmount, ObservableList data) {
        this.id = id;
        ItemName = itemName;
        ItemId = itemId;
        Quantity = quantity;
        TotalAmount = totalAmount;
        statusCombo = new ComboBox<>(data);
    }

    public OrderItems(int id, String itemName, String itemId, int quantity, double totalAmount, String status) {
        this.id = id;
        ItemName = itemName;
        ItemId = itemId;
        Quantity = quantity;
        TotalAmount = totalAmount;
        Status = status;
        if ("Kitchen Done".equalsIgnoreCase(status)) {
            this.actionButton = new Button("Xác nhận mang ra");
        } else {
            this.actionButton = null;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public double getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        TotalAmount = totalAmount;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Button getActionButton() {
        return actionButton;
    }

    public void setActionButton(Button actionButton) {
        this.actionButton = actionButton;
    }

    public ComboBox getStatusCombo() {
        return statusCombo;
    }

    public void setStatusCombo(ComboBox statusCombo) {
        this.statusCombo = statusCombo;
    }

}