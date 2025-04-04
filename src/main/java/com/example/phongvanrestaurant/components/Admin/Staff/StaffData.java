package com.example.phongvanrestaurant.components.Admin.Staff;

import java.sql.Date;

public class StaffData {
    private int id;
    private String fullname;
    private String phone;
    private String role;
    private Date datecreate;
    private Date dateupdate;

    public StaffData(int id, String fullname, String phone, String role, Date datecreate, Date dateupdate) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.role = role;
        this.datecreate = datecreate;
        this.dateupdate = dateupdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
