package com.example.phongvanrestaurant.models;

public class TimesheetData {
    private int id;
    private int staffId;
    private String fullName;
    private String timeCheckin;
    private String timeCheckout;

    public TimesheetData(int id, int staffId, String fullName, String timeCheckin, String timeCheckout) {
        this.id = id;
        this.staffId = staffId;
        this.fullName = fullName;
        this.timeCheckin = timeCheckin;
        this.timeCheckout = timeCheckout;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTimeCheckin() {
        return timeCheckin;
    }

    public void setTimeCheckin(String timeCheckin) {
        this.timeCheckin = timeCheckin;
    }

    public String getTimeCheckout() {
        return timeCheckout;
    }

    public void setTimeCheckout(String timeCheckout) {
        this.timeCheckout = timeCheckout;
    }
}