package com.example.test.myreceipts;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.myreceipts.BLL.ImageHandler;
import com.example.test.myreceipts.BLL.UserService;
import com.example.test.myreceipts.Entity.Receipt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomas on 14-05-2018.
 */


public class ReceiptActivity extends CustomMenu {

    private static final String ERROR_TAG = "Error"; // an error tag for logging
    private static final String SUCCESS_TAG = "Success"; // a success tag for logging

    UserService mUserService;
    Receipt receipt;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.ivPicture)
    ImageView ivPicture;
    @BindView(R.id.deleteButton)
    ImageButton deleteButton;
    @BindView(R.id.tvDate)
    TextView tvDate;
    private FirebaseFirestore db;
    private StorageReference mStorage;

    public ReceiptActivity() {
        super(true, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        ButterKnife.bind(this);

        mUserService = new UserService();
        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        receipt = (Receipt) extras.getSerializable("RECEIPT");

        setInfo();
        listeners();
    }

    /**
     * A method to create all listeners for the class
     */
    private void listeners() {
        deleteReceipt();
        openImage();
    }

    /**
     * sets the info for the selected receipt
     */
    private void setInfo() {
        final String[] stringDate = new String[1];
        // get the reference to to the file uid, adds it to a list, and calls the method for getting the file in storage
        db.document("users/" + mUserService.getCurrentUser().getUid()).collection("allFiles").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().getDocuments().size() >= 1) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (!document.getId().equals("0")) {
                            stringDate[0] = document.getData().get("timestamp").toString();
                        }
                    }


        ImageHandler imageHandler = new ImageHandler();
        tvName.setText("Name: " + receipt.getName());
        tvDate.setText("Date: " + stringDate[0]);
        ivPicture.setImageBitmap(imageHandler.getImageBitmap(receipt.getURL())); //Sets bitmap from Firestorage download URL

    }}});}

    /**
     * Delete receipt and files with a reference to this receipt.
     * Opens main activity on delete.
     */
    private void deleteReceipt() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCategory();
                deleteFavorite();
                deleteFile();
                deleteToast();
                deleteFromAllFiles();
                openMainActivity();
            }
        });
    }

    /**
     * Creates a toast on delete
     */
    private void deleteToast() {
        Toast toast = Toast.makeText(ReceiptActivity.this, "You deleted the receipt", Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Deletes the file
     */
    private void deleteFile() {
        StorageReference storageRef = mStorage;
        StorageReference deleteRef = storageRef.child("receipts").child(mUserService.getCurrentUser().getUid()).child(receipt.getId());
        deleteRef.delete();
    }

    /**
     * Deletes the category
     */
    private void deleteCategory() {
        db.collection("users").document(mUserService.getCurrentUser().getUid()).collection("categories").document("unassigned").collection("fileuids").document(receipt.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(SUCCESS_TAG, "Receipt successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(ERROR_TAG, "Error deleting document", e);
                    }
                });
    }

    /**
     * Deletes the file from "all files"
     */
    private void deleteFromAllFiles() {
        db.collection("users").document(mUserService.getCurrentUser().getUid()).collection("allFiles").document(receipt.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(SUCCESS_TAG, "Favorite successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(ERROR_TAG, "Error deleting document", e);
                    }
                });
    }

    /**
     * Deletes from "favorites"
     */
    private void deleteFavorite() {
        db.collection("users").document(mUserService.getCurrentUser().getUid()).collection("categories").document("favorites").collection("fileuids").document(receipt.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(SUCCESS_TAG, "Favorite successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(ERROR_TAG, "Error deleting document", e);
                    }
                });
    }


    /**
     * Opens the main activity
     */
    private void openMainActivity() {
        Intent intent = new Intent(ReceiptActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Opens image in the build in android photo viewer.
     */
    private void openImage() {
        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                Uri data = Uri.parse(receipt.getURL());
                intent.setDataAndType(data, "image/*");
                startActivity(intent);
            }
        });
    }


}
