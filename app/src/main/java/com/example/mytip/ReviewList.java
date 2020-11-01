package com.example.mytip;

import android.net.Uri;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ReviewList {
    private String title;
    private String date;
    private String img;
    private String review;
    private String seat;
    private String time;
    private Boolean show;

    public ReviewList() {    }

    public ReviewList(String title, String date, String img, String review,String seat, String time, Boolean show) {
        this.title = title;
        this.date = date;
        this.img = img;
        this.review = review;
        this.seat = seat;
        this.time = time;
        this.show = show;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }
}
