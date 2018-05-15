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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

import static com.google.firebase.auth.FirebaseAuth.*;

/**
 * Created by James on 16-04-2018.
 */

public class DAO {


    private static final String ERROR_TAG = "Error"; // an error tag for logging

    // The static names of collections and documents in our firebase
    private static final String USERS_COLLECTION = "users";
    private static final String CATEGORIES_COLLECTION = "categories";
    private static final String FILEUIDS_COLLECTION = "fileuids";
    private static final String ALLFILES_COLLECTION = "allFiles";
    private static final String FAVORITES_DOCUMENT = "favorites";
    private static final String UNASSIGNED_DOCUMENT = "unassigned";

    private FirebaseFirestore mStore;
    private StorageReference mStorage;
    private String user;

    public DAO() {
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        user = getInstance().getUid();
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

    /**
     * Save the receipt to the firebase and the firestore
     *
     * @param context     - Context for making toast on success
     * @param uri         - The uri of the image to save
     * @param information - All the information necesarry from the image
     */
    public void saveReceipt(final Context context, Uri uri, final Map<String, Object> information) {
        // Generates a random uid
        final String uuid = UUID.randomUUID().toString();
        StorageReference filepath = mStorage.child("receipts/").child(information.get("user") + "/" + uuid);

        final HashMap<String, Boolean> exists = new HashMap<>();
        exists.put("exists", true);
        // Sets the uid to the image
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("name", information.get("name").toString())
                .build();
        filepath.putFile(uri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                addToCategoryInDB(uuid, information, exists);
                addToAllFilesInDB(uuid, information, exists);
                if (information.get("favorite").equals(true)) { // Add to favorites if true
                    addToFavoritesInDB(uuid, information, exists);
                }

                Toast.makeText(context, "The receipt is saved successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Add to "favorites" in firebase
     *
     * @param storageuid  - The reference of the image to the firestore
     * @param information - All the information needed to set the reference
     * @param exists      - A HashMap to set a default value
     */
    private void addToFavoritesInDB(String storageuid, Map<String, Object> information, HashMap<String, Boolean> exists) {
        mStore.collection(USERS_COLLECTION)
                .document(information.get("user").toString())
                .collection(CATEGORIES_COLLECTION)
                .document(FAVORITES_DOCUMENT)
                .collection(FILEUIDS_COLLECTION)
                .document(storageuid)
                .set(exists);
    }

    /**
     * Add to "AllFiles" in firebase
     *
     * @param storageuid  - The reference of the image to the firestore
     * @param information - All the information needed to set the reference
     * @param exists      - A HashMap to set a default value
     */
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

    /**
     * Add to "category" in firebase
     *
     * @param storageuid  - The reference of the image to the firestore
     * @param information - All the information needed to set the reference
     * @param exists      - A HashMap to set a default value
     */
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

    /**
     * Deletes a category from firebase
     *
     * @param catName - The name of the category to delete
     */
    public void deleteCategory(String catName) {
        CollectionReference catRef = mStore.collection(USERS_COLLECTION)
                .document(user)
                .collection(CATEGORIES_COLLECTION);

        moveCategoriesToUnassigned(catRef, catName);
        catRef.document(catName).delete();
    }

    /**
     * "Moves" the references to the images from the category to the "unassigned" by duplicating
     * before deleting
     *
     * @param catRef  - The reference to the "category" collection
     * @param catName - The name of the category used for document reference
     */
    private void moveCategoriesToUnassigned(final CollectionReference catRef, String catName) {
        final HashMap<String, Boolean> exists = new HashMap<>();
        exists.put("exists", true);
        catRef.document(catName)
                .collection(FILEUIDS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            catRef.document(UNASSIGNED_DOCUMENT)
                                    .collection(FILEUIDS_COLLECTION)
                                    .document(document.getId()).set(exists);
                        }
                    }
                });
    }
}
