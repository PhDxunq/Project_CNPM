package com.example.phongvanrestaurant.models;



import javafx.scene.control.CheckBox;

import java.util.Date;

public class Orders {
    private int id;
    private int tableId;
    private String status;
    private String note;
    private Date dateCreate;
    private CheckBox select;


    public Orders(int id, int tableId, String status, String note, Date dateCreate) {
        this.id = id;
        this.tableId = tableId;
        this.status = status;
        this.note = note;
        this.dateCreate = dateCreate;
        this.select = new CheckBox();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public CheckBox getCheckBox() {
        return select;
    }

    public void getCheckBox(CheckBox seclect) {
        this.select = seclect;
    }
}
