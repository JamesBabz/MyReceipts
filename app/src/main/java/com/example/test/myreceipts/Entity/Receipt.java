package com.example.test.myreceipts.Entity;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by James on 16-04-2018.
 */

public class Receipt implements Serializable {



    String Id;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    String Name;
    Bitmap bitmap;
    String Category;
    String Date;
    Boolean IsFavorite;
    String URL;

    public Receipt(Map<String, Object> map)
    {
        Date = (String)map.get("timestamp");
    }

    public Receipt() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Boolean getFavorite() {
        return IsFavorite;
    }

    public void setFavorite(Boolean favorite) {
        IsFavorite = favorite;
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
