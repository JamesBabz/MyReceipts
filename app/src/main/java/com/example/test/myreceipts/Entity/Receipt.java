package com.example.test.myreceipts.Entity;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by James on 16-04-2018.
 */

public class Receipt implements Serializable {


    String Id;
    String Name;
    Bitmap bitmap;
    String Date;
    String URL;

    public Receipt() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
