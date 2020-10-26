package com.example.mytip;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ListView;

import java.util.List;

public class ListViewItem {
    private Bitmap ticket;
    private Uri uri;
    private String title;
    private String date;

    ListViewItem(){
    }

    ListViewItem(Uri uri, String title, String date){
        this.uri = uri;
        this.title = title;
        this.date = date;
    }
    public Bitmap getTicket() {
        return ticket;
    }

    public void setTicket(Bitmap ticket) {
        this.ticket = ticket;
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

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
