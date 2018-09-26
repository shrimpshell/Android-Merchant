package com.example.hsinhwang.android_merchant.Classes;


import java.io.Serializable;
import java.sql.Blob;

public class Events implements Serializable {
    private int eventId;
    private float discount;
    private String name, description, start, end;
    private Blob eventImage;

    public Events(float discount) {
        super();
        this.discount = discount;
    }

    public Events(int eventId, String name, String description, String start, String end, float discount) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
        this.discount = discount;

    }

    public String toString() {
        String text = "活動名稱: " + name +
                "\n活動期間: " + start + " ~ " + end +
                "\n活動內容: " + description +
                "\n折扣: " + (int)(discount * 100) + "%";
        return text;
    }

    public void setEventImage(Blob eventImage) {
        this.eventImage = eventImage;
    }

    public Blob getEventImage() {
        return eventImage;
    }

    public int getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public float getDiscount() {
        return discount;
    }
}
