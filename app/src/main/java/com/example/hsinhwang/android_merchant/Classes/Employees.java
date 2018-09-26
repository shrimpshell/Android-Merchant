package com.example.hsinhwang.android_merchant.Classes;

import java.io.Serializable;
import java.sql.Blob;

public class Employees implements Serializable {
    private int id, departmentId, isDeleted;
    private String code, name, password, email, gender, phone, address;
    private Blob image;

    public Employees(int id, String code, String name, String password, String email, String gender, String phone,
                     String address, int departmentId) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.departmentId = departmentId;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

}
