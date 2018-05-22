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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomas on 14-05-2018.
 */



public class ReceiptActivity extends CustomMenu {

    private String currentReceipt;

    private FirebaseFirestore db;
    private StorageReference mStorage;

    UserService mUserService;

    Receipt receipt;

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.ivPicture)
    ImageView ivPicture;
    @BindView(R.id.buttonDelete)
    Button buttonTest;

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

/*        Bundle extras = getIntent().getExtras();
        Receipt receipt = ((Receipt) extras.getSerializable("FRIEND"));*/

        Bundle extras = getIntent().getExtras();
        receipt = (Receipt) extras.getSerializable("RECEIPT");

        tvName.setText(receipt.getName());
        ImageHandler imageHandler = new ImageHandler();
        ivPicture.setImageBitmap(imageHandler.getImageBitmap(receipt.getURL()));


        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                StorageReference storageRef = mStorage;
                StorageReference deleteRef = storageRef.child("receipts").child(mUserService.getCurrentUser().getUid()).child(receipt.getId());
                Log.d("HELLO", deleteRef.toString());
                deleteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });

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


                Toast toast = Toast.makeText(ReceiptActivity.this, "You deleted the receipt", Toast.LENGTH_LONG);
                toast.show();

                finish();
            }
        });




    }

    public boolean loadImageFromURL(String fileUrl,
                                    ImageView iv){
        try {

            URL myFileUrl = new URL (fileUrl);
            HttpURLConnection conn =
                    (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            iv.setImageBitmap(BitmapFactory.decodeStream(is));

            return true;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
