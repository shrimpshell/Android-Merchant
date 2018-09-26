package com.example.hsinhwang.android_merchant.Classes;

import java.io.Serializable;
import java.sql.Blob;

public class Rooms implements Serializable {
    private int id, price, roomQuantity, adultQuantity, childQuantity;
    private String name, roomSize, bed;
    private Blob image;
    public Rooms(int id, String name, String roomSize, String bed, int adultQuantity, int childQuantity, int roomQuantity, int price) {
        super();
        this.price = price;
        this.roomQuantity = roomQuantity;
        this.adultQuantity = adultQuantity;
        this.childQuantity = childQuantity;
        this.name = name;
        this.roomSize = roomSize;
        this.bed = bed;
        this.id = id;
    }
    public String getRoomSize() {
        return roomSize;
    }
    public void setRoomSize(String roomSize) {
        this.roomSize = roomSize;
    }
    public String getBed() {
        return bed;
    }
    public void setBed(String bed) {
        this.bed = bed;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Blob getImage() {
        return image;
    }
    public void setImage(Blob image) {
        this.image = image;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public int getRoomQuantity() {
        return roomQuantity;
    }
    public void setRoomQuantity(int roomQuantity) {
        this.roomQuantity = roomQuantity;
    }
    public int getAdultQuantity() {
        return adultQuantity;
    }
    public void setAdultQuantity(int adultQuantity) {
        this.adultQuantity = adultQuantity;
    }
    public int getChildQuantity() {
        return childQuantity;
    }
    public void setChildQuantity(int childQuantity) {
        this.childQuantity = childQuantity;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDetail() {
        String str = this.getName() + "\n" +
                "房間型號：" + this.getRoomSize() + "\n" +
                "床型：" + this.getBed() + "\n" +
                "大人人數：" + this.getAdultQuantity() + "\n" +
                "小孩人數：" + this.getChildQuantity() + "\n" +
                "定價：＄" + this.getPrice();
        return "房間資訊\n" + str;
    }
}
