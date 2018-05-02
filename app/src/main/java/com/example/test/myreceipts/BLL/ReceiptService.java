package com.example.test.myreceipts.BLL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.example.test.myreceipts.DAL.DAO;
import com.example.test.myreceipts.Entity.Receipt;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 16-04-2018.
 */

public class ReceiptService {

    List<Receipt> receipts = new ArrayList<>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DAO dao;

    public ReceiptService() {
        dao = new DAO();
    }

    public List<Receipt> getReceipts() {
        return receipts;
    }

    public List<Receipt> getAllReceiptsForUser(String UID) {
        return dao.getAllReceiptsForUser(UID);
    }

    private void SetBitmap(final Receipt receipt) {
        String url = receipt.getURL();
        final StorageReference storageRef = storage.getReferenceFromUrl(url);

        //Convert image at reference point to a byte array
        final long SIXTYFOUR_MEGABYTES = 1024 * 1024 * 64;
        storageRef.getBytes(SIXTYFOUR_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Use byte array to create bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // Set bitmap receipt
                receipt.setBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }


    public List<String> getAllCategoriesForUser(String userId) {
        final List<String> categoryNames = new ArrayList<String>();
        dao.getAllCategoriesForUser(userId);  //TODO
        return categoryNames;
    }
}
