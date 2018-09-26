package com.example.hsinhwang.android_merchant.Classes;

public class EmployeeCall {
    private int imageStatus;
    private String tvRoomID;

    public EmployeeCall(int imageStatus, String tvRoomID) {
        this.imageStatus = imageStatus;
        this.tvRoomID = tvRoomID;
    }

    public int getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(int imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getTvRoomID() {
        return tvRoomID;
    }

    public void setTvRoomID(String tvRoomID) {
        this.tvRoomID = tvRoomID;
    }
}
