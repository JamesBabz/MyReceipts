package com.example.test.myreceipts.DAL;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.test.myreceipts.Entity.Receipt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 16-04-2018.
 */

public class DAO {

    private static final String RECEIPTS_COLLECTION = "receipts";
    private static final String ERROR_TAG = "Error";

    private FirebaseFirestore mStore;

    public DAO() {
        mStore = FirebaseFirestore.getInstance();
    }

    public List<Receipt> getAllReceiptsForUser(String UID) {
        List<Receipt> returnList = new ArrayList<>();

        mStore.collection(RECEIPTS_COLLECTION)
                .whereEqualTo("UID", UID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Create the receipt
                                Receipt receipt = new Receipt();
                                receipt.setName(document.get("name").toString());
                                receipt.setCategory(document.get("category").toString());
                                receipt.setDate(document.get("date").toString());
                                receipt.setFavorite(document.get("isFavorite").toString() == "true");
                                receipt.setURL(document.get("URL").toString());
                            }
                        } else {
                            Log.d(ERROR_TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });






        return returnList;
    }


    public List<String> getAllCategoriesForUser(String userId) {
        return null;
    }
}
