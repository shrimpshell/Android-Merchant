package com.example.hsinhwang.android_merchant.Classes;

public class ChatMessage {

    // groupId Customer:0 clean:1 room:2 dinling:3
    private String senderId, receiverId, senderGroupId, receiverGroupId;
    private int serviceId,instantNumber;

    public ChatMessage(String senderId, String receiverId, String senderGroupId,
                       String receiverGroupId, int serviceId, int instantNumber) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderGroupId = senderGroupId;
        this.receiverGroupId = receiverGroupId;
        this.serviceId = serviceId;
        this.instantNumber = instantNumber;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderGroupId() {
        return senderGroupId;
    }

    public void setSenderGroupId(String senderGroupId) {
        this.senderGroupId = senderGroupId;
    }

    public String getReceiverGroupId() {
        return receiverGroupId;
    }

    public void setReceiverGroupId(String receiverGroupId) {
        this.receiverGroupId = receiverGroupId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getInstantNumber() {
        return instantNumber;
    }

    public void setInstantNumber(int instantNumber) {
        this.instantNumber = instantNumber;
    }
}
