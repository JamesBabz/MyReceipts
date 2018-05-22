package com.example.test.myreceipts.DAL;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

/**
 * Created by James on 16-04-2018.
 */

public class DAO {


    private static final String ERROR_TAG = "Error"; // an error tag for logging
    // The static names of collections and documents in our firebase
    private static final String RECEIPTS_COLLECTION = "receipts";
    private static final String USERS_COLLECTION = "users";
    private static final String CATEGORIES_COLLECTION = "categories";
    private static final String FILEUIDS_COLLECTION = "fileuids";
    private static final String ALLFILES_COLLECTION = "allFiles";
    private static final String FAVORITES_DOCUMENT = "favorites";
    private static final String UNASSIGNED_DOCUMENT = "unassigned";
    final HashMap<String, Boolean> exists = new HashMap<>();
    CollectionReference catRef;
    private FirebaseFirestore mStore;
    private StorageReference mStorage;
    private String user;

    public DAO() {
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        user = getInstance().getUid();
        catRef = mStore.collection(USERS_COLLECTION)
                .document(user)
                .collection(CATEGORIES_COLLECTION);

        exists.put("exists", true);
    }

    /**
     * Save the receipt to the firebase and the firestore
     *
     * @param context     Context for making toast on success
     * @param uri         The uri of the image to save
     * @param information All the information necesarry from the image
     */
    public void saveReceipt(final Context context, Uri uri, final Map<String, Object> information) {
        // Generates a random uid
        final String uuid = UUID.randomUUID().toString();
        StorageReference filepath = mStorage.child("receipts/").child(information.get("user") + "/" + uuid);


        // Sets the uid to the image
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("name", information.get("name").toString())
                .build();
        filepath.putFile(uri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                addToCategoryInDB(uuid, information);
                addToAllFilesInDB(uuid, information);
                if (information.get("favorite").equals(true)) { // Add to favorites if true
                    addToFavoritesInDB(uuid);
                }

                Toast.makeText(context, "The receipt was saved successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Add to "favorites" in firebase
     *
     * @param storageuid The reference of the image to the firestore
     */
    private void addToFavoritesInDB(String storageuid) {
        catRef.document(FAVORITES_DOCUMENT)
                .collection(FILEUIDS_COLLECTION)
                .document(storageuid)
                .set(exists);
    }

    /**
     * Add to "AllFiles" in firebase
     *
     * @param storageuid  The reference of the image to the firestore
     * @param information All the information needed to set the reference
     */
    private void addToAllFilesInDB(String storageuid, Map<String, Object> information) {
        // Reference to document
        DocumentReference allFileDocRef = mStore.collection(USERS_COLLECTION)
                .document(information.get("user").toString())
                .collection(ALLFILES_COLLECTION)
                .document(storageuid);


        HashMap<String, Object> timeMap = new HashMap<>();
        timeMap.put("timestamp", information.get("timestamp"));
        // Create fieid so it later can be accessed
        allFileDocRef.set(timeMap);

    }

    /**
     * Add to "category" in firebase
     *
     * @param storageuid  The reference of the image to the firestore
     * @param information All the information needed to set the reference
     */
    @NonNull
    private void addToCategoryInDB(String storageuid, Map<String, Object> information) {
        // Reference to document
        DocumentReference catDocRef = catRef.document(information.get("category").toString().toLowerCase());
        // Create fieid so it later can be accessed
        catDocRef.set(exists);

        catDocRef.collection(FILEUIDS_COLLECTION)
                .document(storageuid)
                .set(exists);


    }

    /**
     * Deletes a category from firebase
     *
     * @param catName The name of the category to delete
     */
    public void deleteCategory(String catName) {
        moveAllFilesFromCategoryToCategory(catName, UNASSIGNED_DOCUMENT);
    }

    /**
     * Renames a category
     *
     * @param fromCatName The current category name
     * @param toCatName   The desired category name
     */
    public void renameCategory(String fromCatName, String toCatName) {

        catRef.document(toCatName).set(exists); // Create the category

        moveAllFilesFromCategoryToCategory(fromCatName, toCatName); // Move the files
    }

    /**
     * "Moves" the references to the images from one category to another by duplicating
     * before deleting
     *
     * @param fromCatName The name of the category to move from used for document reference
     * @param toCatName   The name of the category to move to used for document reference
     */
    private void moveAllFilesFromCategoryToCategory(String fromCatName, final String toCatName) {

        catRef.document(fromCatName)
                .collection(FILEUIDS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            catRef.document(toCatName)
                                    .collection(FILEUIDS_COLLECTION)
                                    .document(document.getId()).set(exists);
                        }
                    }
                });
        catRef.document(fromCatName).delete();
    }
}
