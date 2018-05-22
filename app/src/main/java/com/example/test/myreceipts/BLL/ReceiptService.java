package com.example.test.myreceipts.BLL;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.test.myreceipts.DAL.DAO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by James on 16-04-2018.
 */

public class ReceiptService {

    DAO dao;
    private ImageHandler imgHandler;

    public ReceiptService() {
        dao = new DAO();
        imgHandler = new ImageHandler();
    }

    /**
     * Saves the receipt
     * @param context The context used by the bitmap converter
     * @param bitmap The bitmap of the image to save
     * @param information All the information given to the image
     */
    public void saveReceipt(Context context, Bitmap bitmap, Map<String, Object> information) {
        Uri uri = (imgHandler.bitmapToUriConverter(context, bitmap));
        dao.saveReceipt(context, uri, information);
    }

    /**
     * Formats the date
     * @param stringDate The date as a string
     * @return The formatted date
     */
    public Date convertDate(String stringDate) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = format.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
