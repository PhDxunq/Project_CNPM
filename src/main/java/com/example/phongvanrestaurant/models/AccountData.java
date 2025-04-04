package com.example.phongvanrestaurant.models;

import java.util.Date;

public class AccountData {
    private Integer id;
    private Integer staffid;
    private String fullname;
    private String role;
    private String username;
    private String password;
    private Date datecreate;
    private Date dateupdate;

    public AccountData(Integer id, Integer staffid, String fullname, String role, String username, String password, Date datecreate, Date dateupdate) {
        this.id = id;
        this.staffid = staffid;
        this.fullname = fullname;
        this.role = role;
        this.username = username;
        this.password = password;
        this.datecreate = datecreate;
        this.dateupdate = dateupdate;
    }

    public AccountData() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStaffid() {
        return staffid;
    }

    public void setStaffid(Integer staffid) {
        this.staffid = staffid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDatecreated() {
        return datecreate;
    }

    public void setDatecreated(Date datecreated) {
        this.datecreate = datecreated;
    }

    public Date getDateupdate() {
        return dateupdate;
    }

    public void setDateupdate(Date dateupdate) {
        this.dateupdate = dateupdate;
    }

    public String getFullname() {
        return fullname;
    }

    public String getRole() {
        return role;
    }

    ;
}
