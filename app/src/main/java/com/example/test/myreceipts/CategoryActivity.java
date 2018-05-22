package com.example.test.myreceipts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.test.myreceipts.BLL.ImageHandler;
import com.example.test.myreceipts.BLL.ReceiptService;
import com.example.test.myreceipts.BLL.UserService;
import com.example.test.myreceipts.Entity.Receipt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 08-05-2018.
 */

public class CategoryActivity extends CustomMenu {
    ReceiptService receiptService;
    UserService userService;
    ListView listViewCategories;
    ListAdapter listAdapter;
    List<Receipt> returnList = new ArrayList<>();
    TextView categoryHeadline;
    LinearLayout llListContainer;
    private FirebaseFirestore mStore;
    private StorageReference mStorage;
    private ImageHandler imageHandler = new ImageHandler();
    private String categoryName;

    public CategoryActivity() {
        super(true, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);
        listViewCategories = findViewById(R.id.listViewCategories);
        llListContainer = findViewById(R.id.llListContainer);

        receiptService = new ReceiptService();
        userService = new UserService();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        listViewCategories = findViewById(R.id.listViewCategories);
        categoryHeadline = findViewById(R.id.tvGroupName);

        final String userUid = userService.getCurrentUser().getUid();
        categoryName = getIntent().getExtras().getString("categoryName");
        categoryHeadline.setText(categoryName.toUpperCase());

        Thread thread = getFilesFromFirebase(userUid);
        thread.start();
        setList();
        addListenerOnList();

        checkStrictMode();

    }

    /**
     * start a new thread to handle the calls for Firebase, to reduce the work on main thread
     *
     * @param userUid the UID for current user
     * @return a thread
     */
    @NonNull
    private Thread getFilesFromFirebase(final String userUid) {
        return new Thread() {
            @Override
            public void run() {
                getAllReceiptsForCategory(userUid, categoryName);
            }
        };
    }

    /**
     * Checks for internet connection and permission
     */
    private void checkStrictMode() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    /**
     * Get all file uids from database, in the selected category
     *
     * @param userUid  UID for current user
     * @param category gets all the file UIDs in database for this category
     */
    public void getAllReceiptsForCategory(final String userUid, final String category) {

        // get the reference to to the file uid, adds it to a list, and calls the method for getting the file in storage
        mStore.document("users/" + userUid).collection("categories").document(category).collection("fileuids").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> fileUids = new ArrayList<>();
                    if (task.getResult().size() <= 1) {
                        if (categoryName.equalsIgnoreCase("favorites")) {
                            createTextView(llListContainer, "No images in category", "Add images to favorites by clicking the star icon");
                        } else {
                            createTextView(llListContainer, "No images in category", "Add images to categories when you take them");
                        }
                        return;
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (!document.getId().equals("0")) {
                            fileUids.add(document.getId());
                        }
                    }
                    listAdapter.notifyDataSetChanged(); // new changes to the list
                    getFilesFromStorage(userUid, fileUids);
                } else {
                    Log.w("error", "Error getting documents.", task.getException());
                }
            }
        });

    }

    /**
     * Create a textview in parent
     *
     * @param parent the view that the text has to bee shown in
     * @param text   Array
     */
    private void createTextView(ViewGroup parent, String... text) {
        parent.removeAllViews();
        for (int i = 0; i < text.length; i++) {
            TextView tvText = new TextView(this);
            tvText.setText(text[i]);
            parent.addView(tvText);
        }
    }

    /**
     * Gets the image from firebase storage with the file uid and user uid
     *
     * @param userUid  UID for current user
     * @param fileuids all the File UIDs for the category
     */
    private void getFilesFromStorage(String userUid, List<String> fileuids) {

        //for all file uids in the list, it wil:
        for (final String fileuid : fileuids) {
            listAdapter.notifyDataSetChanged(); // notify the list list about changes
            // gets the reference to the single file in storage, in the user's folder in storage
            final StorageReference storageReference = mStorage.child("receipts/").child(userUid + "/" + fileuid);

            // gets the file name, in the metadata which saved on the file by upload
            storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    final String fileName = storageMetadata.getCustomMetadata("name");
                    //gets the donwnload url on the file from firebase
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            final Receipt rec = new Receipt();
                            rec.setName(fileName);
                            rec.setId(fileuid);
                            rec.setURL(uri.toString());
                            rec.setBitmap(imageHandler.getImageBitmap(uri.toString()));    //to make sure it is correct form, it will be converted to bitmap
                            returnList.add(rec); // the arrayList for the ListAdapter, to set the list
                            listAdapter.notifyDataSetChanged(); // notify the list list about changes
                        }
                    });
                }
            });
        }
    }

    /**
     * Creates the listadapter to set the listview
     */
    private void setList() {
        listAdapter = new ListAdapter(this, R.layout.cell_extended, returnList);
        listViewCategories.setAdapter(listAdapter);
    }

    /**
     * Listens on witch item is clicked
     */
    private void addListenerOnList() {
        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Receipt entry = (Receipt) parent.getItemAtPosition(position);
                openReceiptView(entry);
            }
        });
    }

    /**
     * Opens ReceiptActivity with information about the selected receipt
     *
     * @param entry a Receipt entity
     */
    private void openReceiptView(Receipt entry) {
        //Creates a new entity, with the only needed data for next view
        Receipt receiptForNextView = new Receipt();
        receiptForNextView.setName(entry.getName());
        receiptForNextView.setDate(entry.getDate());
        receiptForNextView.setURL(entry.getURL());
        receiptForNextView.setId(entry.getId());

        Intent intent = new Intent(this, ReceiptActivity.class);

        intent.putExtra("RECEIPT", receiptForNextView);
        startActivity(intent);

    }

}
