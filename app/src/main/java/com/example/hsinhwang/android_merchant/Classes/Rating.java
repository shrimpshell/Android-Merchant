package com.example.hsinhwang.android_merchant.Classes;

import java.io.Serializable;

public class Rating implements Serializable{
    private int IdRating, IdRoomReservation;
    private float ratingStar;
    private String opinion, review, time, Name;

    public Rating(int IdRoomReservation, String review){
        this.review = review;
        this.IdRoomReservation = IdRoomReservation;
    }

    public Rating(int IdRating, float ratingStar, String time, String opinion, String review,
                  int IdRoomReservation) {
        this.IdRating = IdRating;
        this.ratingStar = ratingStar;
        this.time = time;
        this.opinion = opinion;
        this.review = review;
        this.IdRoomReservation = IdRoomReservation;
    }

    public Rating(int IdRating, float ratingStar, String time, String opinion, String review,
                  int IdRoomReservation, String Name) {
        this.IdRating = IdRating;
        this.ratingStar = ratingStar;
        this.time = time;
        this.opinion = opinion;
        this.review = review;
        this.IdRoomReservation = IdRoomReservation;
        this.Name = Name;
    }

    public Rating(int IdRoomReservation, float ratingStar, String opinion){
        this.opinion = opinion;
        this.IdRoomReservation = IdRoomReservation;
        this.ratingStar = ratingStar;

    }

    public int getIdRating() {
        return IdRating;
    }

    public void setIdRating(int idRating) {
        IdRating = idRating;
    }

    public float getRatingStar() {
        return ratingStar;
    }

    public void setRatingStar(float ratingStar) {
        this.ratingStar = ratingStar;
    }

    public int getIdRoomReservation() {
        return IdRoomReservation;
    }

    public void setIdRoomReservation(int idRoomReservation) {
        IdRoomReservation = idRoomReservation;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
