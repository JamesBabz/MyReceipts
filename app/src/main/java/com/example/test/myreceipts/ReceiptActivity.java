package com.example.test.myreceipts;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomas on 14-05-2018.
 */



public class ReceiptActivity extends CustomMenu {

    private FirebaseFirestore db;
    private StorageReference mStorage;
    UserService mUserService;
    Receipt receipt;

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.ivPicture)
    ImageView ivPicture;
    @BindView(R.id.deleteButton)
    ImageButton deleteButton;
    @BindView(R.id.tvDate) TextView tvDate;

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
        ImageHandler imageHandler = new ImageHandler();
        tvName.setText("Name: " + receipt.getName());
        tvDate.setText("Date: " + receipt.getDate());
        ivPicture.setImageBitmap(imageHandler.getImageBitmap(receipt.getURL())); //Sets bitmap from Firestorage download URL
    }

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

    private void deleteToast() {
        Toast toast = Toast.makeText(ReceiptActivity.this, "You deleted the receipt", Toast.LENGTH_LONG);
        toast.show();
    }

    private void deleteFile() {
        StorageReference storageRef = mStorage;
        StorageReference deleteRef = storageRef.child("receipts").child(mUserService.getCurrentUser().getUid()).child(receipt.getId());
        Log.d("HELLO", deleteRef.toString());
        deleteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    private void deleteCategory() {
        db.collection("users").document(mUserService.getCurrentUser().getUid()).collection("categories").document("unassigned").collection("fileuids").document(receipt.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("HELLO", "Receipt successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("HELLO", "Error deleting document", e);
                    }
                });
    }

    private void deleteFromAllFiles() {
        db.collection("users").document(mUserService.getCurrentUser().getUid()).collection("allFiles").document(receipt.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("HELLO", "Favorite successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("HELLO", "Error deleting document", e);
                    }
                });
    }

    private void deleteFavorite() {
        db.collection("users").document(mUserService.getCurrentUser().getUid()).collection("categories").document("favorites").collection("fileuids").document(receipt.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("HELLO", "Favorite successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("HELLO", "Error deleting document", e);
                    }
                });
    }


    private void openMainActivity() {
        Intent intent = new Intent(ReceiptActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Opens image in the build in android photo viewer.
     */
    private void openImage()
    {
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
