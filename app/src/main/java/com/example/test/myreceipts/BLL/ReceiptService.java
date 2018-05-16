package com.example.test.myreceipts.BLL;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.test.myreceipts.DAL.DAO;
import com.example.test.myreceipts.Entity.Receipt;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by James on 16-04-2018.
 */

public class ReceiptService {

    List<Receipt> receipts = new ArrayList<>();
    DAO dao;
    private ImageHandler imgHandler;

    public ReceiptService() {
        dao = new DAO();
        imgHandler = new ImageHandler();
    }

    public List<Receipt> getReceipts() {
        return receipts;
    }

    public List<Receipt> getAllReceiptsForUser(String UID) {
        return dao.getAllReceiptsForUser(UID);
    }

    public void saveReceipt(Context context, Bitmap bitmap, Map<String, Object> information) {
        Uri uri = (imgHandler.bitmapToUriConverter(context, bitmap));
        dao.saveReceipt(context, uri, information);
    }

    public Date convertDate(String stringDate){
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
