package com.example.hsinhwang.android_merchant.Classes;

public class EmployeeClean {
    private int imageStatus;
    private String tvRooId,tvStatusNumber;

    public EmployeeClean(int imageStatus, String tvStatusNumber, String tvRooId) {
        this.imageStatus = imageStatus;
        this.tvStatusNumber = tvStatusNumber;
        this.tvRooId = tvRooId;
    }

    public int getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(int imageStatus) {
        this.imageStatus = imageStatus;
    }

    public String getTvRooId() {
        return tvRooId;
    }

    public void setTvRooId(String tvRooId) {
        this.tvRooId = tvRooId;
    }

    public String getTvStatusNumber() {
        return tvStatusNumber;
    }

    public void setTvStatusNumber(String tvStatusNumber) {
        this.tvStatusNumber = tvStatusNumber;
    }
}
