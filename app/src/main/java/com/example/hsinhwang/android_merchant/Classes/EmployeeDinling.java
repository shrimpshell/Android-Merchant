package com.example.hsinhwang.android_merchant.Classes;

public class EmployeeDinling {
    private int IdInstantDetail, IdInstantService, Status, Quantity, IdInstantType, IdRoomStatus;
    private String RoomNumber;

    public EmployeeDinling(int idInstantDetail, int idInstantService, int status, int quantity,
                           int idInstantType, int idRoomStatus, String roomNumber) {
        IdInstantDetail = idInstantDetail;
        IdInstantService = idInstantService;
        Status = status;
        Quantity = quantity;
        IdInstantType = idInstantType;
        IdRoomStatus = idRoomStatus;
        RoomNumber = roomNumber;
    }


    public int getIdInstantDetail() {
        return IdInstantDetail;
    }

    public void setIdInstantDetail(int idInstantDetail) {
        IdInstantDetail = idInstantDetail;
    }

    public int getIdInstantService() {
        return IdInstantService;
    }

    public void setIdInstantService(int idInstantService) {
        IdInstantService = idInstantService;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public int getIdInstantType() {
        return IdInstantType;
    }

    public void setIdInstantType(int idInstantType) {
        IdInstantType = idInstantType;
    }

    public int getIdRoomStatus() {
        return IdRoomStatus;
    }

    public void setIdRoomStatus(int idRoomStatus) {
        IdRoomStatus = idRoomStatus;
    }

    public String getRoomNumber() {
        return RoomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        RoomNumber = roomNumber;
    }
}
