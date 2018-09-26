package com.example.hsinhwang.android_merchant.Classes;

public class EmployeeRoom {
    private int imageStatus;
    private String tvRoomId,tvItem,tvQuantity,tvStatusNumber;

    public EmployeeRoom(int imageStatus, String tvStatusNumber, String tvRoomId, String tvItem, String tvQuantity) {
        this.imageStatus = imageStatus;
        this.tvStatusNumber = tvStatusNumber;
        this.tvRoomId = tvRoomId;
        this.tvItem = tvItem;
        this.tvQuantity = tvQuantity;
    }

    public int getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(int imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getTvRoomId() {
        return tvRoomId;
    }

    public void setTvRoomId(String tvRoomId) {
        this.tvRoomId = tvRoomId;
    }

    public String getTvItem() {
        return tvItem;
    }

    public void setTvItem(String tvItem) {
        this.tvItem = tvItem;
    }

    public String getTvQuantity() {
        return tvQuantity;
    }

    public void setTvQuantity(String tvQuantity) {
        this.tvQuantity = tvQuantity;
    }

    public String getTvStatusNumber() {
        return tvStatusNumber;
    }

    public void setTvStatusNumber(String tvStatusNumber) {
        this.tvStatusNumber = tvStatusNumber;
    }
}
