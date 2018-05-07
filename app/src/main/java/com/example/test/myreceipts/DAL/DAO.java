package com.example.test.myreceipts.DAL;

import android.content.Context;
import android.net.Uri;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.test.myreceipts.BLL.ImageHandler;
import com.example.test.myreceipts.Entity.Receipt;
import com.example.test.myreceipts.ImageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by James on 16-04-2018.
 */

public class DAO {

    private static final String RECEIPTS_COLLECTION = "receipts";
    private static final String ERROR_TAG = "Error";

    private FirebaseFirestore mStore;
    private StorageReference mStorage;

    public DAO() {
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

    }


    // TODO Not working after DB changes
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


    public List<String> getAllCategoriesForUser(final String userId) {
        final List<String> returnList = new ArrayList<>();
        mStore.collection("users").document(userId).collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("DAO", userId);
                            Log.d("DAO", document.getId());
                        }
//                     returnList.add()
                    }
                });



        return null;
    }

    public void saveReceipt(final Context context, Uri uri, final Map<String, Object> information){
        // Generates a random uid
        final UUID uuid = UUID.randomUUID();
        StorageReference filepath = mStorage.child("receipts/"+information.get("name").toString());

        final HashMap<String, Boolean> exists = new HashMap<>();
        exists.put("exists", true);
        // Sets the uid to the image
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("UID", uuid.toString())
                .build();
        filepath.putFile(uri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String storageuid = uuid.toString();
                mStore.collection("users")
                        .document(information.get("user").toString())
                        .collection("categories")
                        .document(information.get("category").toString().toLowerCase())
                        .collection("fileuids")
                        .document(storageuid)
                        .set(exists);

                HashMap<String, Object> timeMap = new HashMap<>();
                timeMap.put("timestamp",information.get("timestamp"));

                mStore.collection("users")
                        .document(information.get("user").toString())
                        .collection("allFiles")
                        .document(storageuid)
                        .collection("fileuids")
                        .add(timeMap);

                if(information.get("favorite").equals(true)){
                    mStore.collection("users")
                            .document(information.get("user").toString())
                            .collection("categories")
                            .document("favorite")
                            .collection("fileuids")
                            .document(storageuid)
                            .set(exists);
                }

                Toast.makeText(context, "The receipt is saved correctly", Toast.LENGTH_LONG).show();
            }
        });
    }
}
