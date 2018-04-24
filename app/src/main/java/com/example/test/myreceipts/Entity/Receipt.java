package com.example.test.myreceipts.Entity;

import android.graphics.Bitmap;

import com.google.firebase.storage.StorageReference;

/**
 * Created by James on 16-04-2018.
 */

public class Receipt {

    Bitmap bitmap;

    public Receipt(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
