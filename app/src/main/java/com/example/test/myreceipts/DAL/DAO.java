package com.example.test.myreceipts.DAL;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.test.myreceipts.BLL.Callback;
import com.example.test.myreceipts.BLL.UserService;
import com.example.test.myreceipts.Entity.Receipt;
import com.example.test.myreceipts.Entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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

    private static final String ERROR_TAG = "Error";
    private static final String USERS_COLLECTION = "users";
    private static final String CATEGORIES_COLLECTION = "categories";
    private static final String FILEUIDS_COLLECTION = "fileuids";
    private static final String ALLFILES_COLLECTION = "allFiles";

    private FirebaseFirestore mStore;
    private StorageReference mStorage;
    List<Receipt> returnList = new ArrayList<>();
    List<Receipt> test = new ArrayList<>();
    public DAO() {
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

    }

    // TODO Not working after DB changes
    public List<Receipt> getAllReceiptsForUser(String UID) {
        List<Receipt> returnList = new ArrayList<>();

        mStore.collection("receipts")
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

                            }
                        } else {
                            Log.d(ERROR_TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        return returnList;
    }
    public void getAllReceiptsForCategory(final String userUid, String category, Callback callback) {

        mStore.document("users/" + userUid).collection("categories").document(category).collection(FILEUIDS_COLLECTION).get() .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        getFilesFromStorage(userUid, document.getId());
                    }
                } else {
                    Log.w("shiat", "Error getting documents.", task.getException());
                }
            }
        });

    }

    private void getFilesFromStorage(String userUid, final String fileUid){

        final StorageReference storageReference = mStorage.child("receipts/").child(userUid + "/" + fileUid );
        storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
              final String fileName = storageMetadata.getCustomMetadata("name");
                Log.d("fileMetadata", fileName);
                  storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                      @Override
                      public void onSuccess(Uri uri) {
                          Receipt rec = new Receipt();
                          rec.setName(fileName);
                          rec.setURL(uri);

                      }
                  }).addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception exception) {
                          // Handle any errors
                      }
                  });

            }
        });
    }

    public void saveReceipt(final Context context, Uri uri, final Map<String, Object> information) {
        // Generates a random uid
        final UUID uuid = UUID.randomUUID();
        StorageReference filepath = mStorage.child("receipts/").child(information.get("user") +"/" + information.get("name").toString());

        final HashMap<String, Boolean> exists = new HashMap<>();
        exists.put("exists", true);
        // Sets the uid to the image
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("name", information.get("name").toString())
                .build();
        filepath.putFile(uri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String storageuid = uuid.toString();


                addToCategoryInDB(storageuid, information, exists);
                addToAllFilesInDB(storageuid, information, exists);
                if (information.get("favorite").equals(true)) {
                    addToFavoritesInDB(storageuid, information, exists);
                }

                Toast.makeText(context, "The receipt is saved successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addToFavoritesInDB(String storageuid, Map<String, Object> information, HashMap<String, Boolean> exists) {
        mStore.collection(USERS_COLLECTION)
                .document(information.get("user").toString())
                .collection(CATEGORIES_COLLECTION)
                .document("favorite")
                .collection(FILEUIDS_COLLECTION)
                .document(storageuid)
                .set(exists);
    }

    private void addToAllFilesInDB(String storageuid, Map<String, Object> information, HashMap<String, Boolean> exists) {
        // Reference to document
        DocumentReference allFileDocRef = mStore.collection(USERS_COLLECTION)
                .document(information.get("user").toString())
                .collection(ALLFILES_COLLECTION)
                .document(storageuid);


        // Create fieid so it later can be accessed
        allFileDocRef.set(exists);

        HashMap<String, Object> timeMap = new HashMap<>();
        timeMap.put("timestamp", information.get("timestamp"));

        allFileDocRef.collection(FILEUIDS_COLLECTION)
                .add(timeMap);
    }

    @NonNull
    private void addToCategoryInDB(String storageuid, Map<String, Object> information, HashMap<String, Boolean> exists) {
        // Reference to document
        DocumentReference catDocRef = mStore.collection(USERS_COLLECTION)
                .document(information.get("user").toString())
                .collection(CATEGORIES_COLLECTION)
                .document(information.get("category").toString().toLowerCase());
        // Create fieid so it later can be accessed
        catDocRef.set(exists);

        catDocRef.collection(FILEUIDS_COLLECTION)
                .document(storageuid)
                .set(exists);


    }
}
