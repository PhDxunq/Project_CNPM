package com.example.phongvanrestaurant.components.Admin.Account;

public class StaffData {
    private int id;
    private String fullName;
    private String role;

    public StaffData(int id, String fullName, String role) {
        this.id = id;
        this.fullName = fullName;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}