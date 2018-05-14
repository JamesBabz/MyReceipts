package com.example.test.myreceipts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.test.myreceipts.BLL.Callback;
import com.example.test.myreceipts.BLL.ImageHandler;
import com.example.test.myreceipts.BLL.ReceiptService;
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
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 08-05-2018.
 */

public class CategoryActivity extends CustomMenu {
    ReceiptService receiptService;
    UserService userService;
    Spinner spinner;
    ListView listViewCategories;
    ListAdapter listAdapter;
    List<Receipt> returnList = new ArrayList<>();
    private FirebaseFirestore mStore;
    private StorageReference mStorage;
    private ImageHandler imageHandler = new ImageHandler();

    public CategoryActivity() {
        super(true, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);
        listViewCategories = findViewById(R.id.listViewCategories);

        receiptService = new ReceiptService();
        userService = new UserService();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        listViewCategories = findViewById(R.id.listViewCategories);
        String userUid = userService.getCurrentUser().getUid();
        getAllReceiptsForCategory(userUid, getIntent().getExtras().getString("categoryName"));
        createSpinner();
        addListenerOnList();

        setList();

    }


    private void createSpinner() {
        spinner = findViewById(R.id.spinnerSortBy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.group_sort_by_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // Get all file uids from database, in the selected category
    public void getAllReceiptsForCategory(final String userUid, final String category) {

        // get the reference to to the file uid, adds it to a list, and calls the method for getting the file in storage
        mStore.document("users/" + userUid).collection("categories").document(category).collection("fileuids").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> fileUids = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        fileUids.add(document.getId());
                    }
                    listAdapter.notifyDataSetChanged(); // new changes to the list
                    getFilesFromStorage(userUid, fileUids);
                } else {
                    Log.w("error", "Error getting documents.", task.getException());
                }
            }
        });

    }

    //Gets the image from firebase storage with the file uid and user uid
    private void getFilesFromStorage(String userUid, List<String> fileuids) {

        //for all file uids in the list, it wil:
        for (final String fileuid : fileuids) {

            // gets the reference to the single file in storage, in the user's folder in storage
            final StorageReference storageReference = mStorage.child("receipts/").child(userUid + "/" + fileuid);

            // gets the file name, in the metadata which saved on the file by upload
            storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    final String fileName = storageMetadata.getCustomMetadata("name");
                    // gets the uri / url (something mysterious about this, java does not if it is an Uri or url)
                    //-to make sure it is correct form, it will be converted to bitmap - line 147
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            final Receipt rec = new Receipt();
                            rec.setName(fileName);
                            rec.setId(fileuid);

                            //TODO not a good solution, how to refactor this?!
                            Thread thread = new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        rec.setBitmap(imageHandler.getImageBitmap(uri.toString()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                            listAdapter.notifyDataSetChanged(); // notify the list list about changes
                            returnList.add(rec); // the arrayList for the ListAdapter, to set the list
                        }
                    });
                }
            });
        }
    }




    private void setList() {
        listAdapter = new ListAdapter(this, R.layout.cell_extended, returnList);
        listViewCategories.setAdapter(listAdapter);

    }

    //Listens on witch item is clicked
    private void addListenerOnList() {
        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Receipt entry = (Receipt) parent.getItemAtPosition(position);
                openReceiptView(entry);
            }
        });
    }

    //Opens ReceiptActivity with all information about the selected receipt
    private void openReceiptView(Receipt entry) {

        Intent intent = new Intent(this, ReceiptActivity.class);
        intent.putExtra("RECEIPT", entry);
        startActivity(intent);
    }

}
