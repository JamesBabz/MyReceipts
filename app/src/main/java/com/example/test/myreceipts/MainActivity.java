package com.example.test.myreceipts;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.test.myreceipts.BLL.CategoryService;
import com.example.test.myreceipts.BLL.ImageHandler;
import com.example.test.myreceipts.BLL.ReceiptService;
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
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends CustomMenu {

    ImageView btnCapture;
    GridView gridView;
    TextView tvGroupHeader;
    ImageHandler imageHandler;
    private FirebaseFirestore mStore;
    private StorageReference mStorage;
    Boolean categoryProgress;
    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;

    private ReceiptService receiptService;
    private int x = 0;
    private List<String> categories = new ArrayList<>();
    private ImageView[] images;

    private CategoryService categoryService;
    private String currentUserId;

    @BindView(R.id.categoryRefresh)
    ProgressBar mProgressBar;

    public MainActivity() {
        super(false, true);
        categoryService = new CategoryService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = findViewById(R.id.gvShowAll);
        btnCapture = findViewById(R.id.btnCapture);
        tvGroupHeader = findViewById(R.id.tvGroupHeader);
        ButterKnife.bind(this);
        imageHandler = new ImageHandler();
        img1 = findViewById(R.id.ivRecent1);
        img2 = findViewById(R.id.ivRecent2);
        img3 = findViewById(R.id.ivRecent3);
        img4 = findViewById(R.id.ivRecent4);
        images = new ImageView[]{img1, img2, img3, img4};
        receiptService = new ReceiptService();

        ButtonAdapter buttonAdapter = new ButtonAdapter(getBaseContext(), categories);
        gridView.setAdapter(buttonAdapter);

        mProgressBar.setVisibility(View.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        currentUserId = extras.getString("USER");

        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        getAllReceiptsForCategory(currentUserId);

    }

    @Override
    protected void onResume() {
        super.onResume();
        createListeners();
    }

    private void createListeners() {
        createCaptureButtonListener();
        createOnCategoryRetrievedListener();
    }

    private void createOnCategoryRetrievedListener() {
        categoryProgressBar();
        categoryService.addCategoriesToButtonAdapter(gridView, mProgressBar);
    }

    private void categoryProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setCategoryProgress();
            }
        });
    }

    private void createCaptureButtonListener() {

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //Overrides the main back button
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    private void setCategoryProgress() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    // Get all file uids from database
    private void getAllReceiptsForCategory(final String userUid) {
        // get the reference to to the file uid, adds it to a list, and calls the method for getting the file in storage
        mStore.document("users/" + userUid).collection("allFiles").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().getDocuments().size() >= 1) {
                    List<String> receiptUids = new ArrayList<>();
                    TreeMap<Date, String> timeMap = new TreeMap<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (!document.getId().equals("0")) {

                            Log.d("receiptData", document.getId());
                            Log.d("receiptData", document.getData().get("timestamp").toString());
                            String stringDate = document.getData().get("timestamp").toString();
                            timeMap.put(receiptService.convertDate(stringDate), document.getId());
                        }
                    }

                    // run throw the map 4 times, and gets the last in the sorted map every time.
                    // each time with 'pollLastEntry()' the last value will be deleted after is has been added to the arraylist
                    for (int i = 0; i < 4; i++) {
                        if (timeMap.size() >= 1) {
                            receiptUids.add(timeMap.pollLastEntry().getValue());
                        }

                    }

                    // call the storage method with the user uid and 4 the for latest images.
                    getFilesFromStorage(userUid, receiptUids);
                } else {
                    Log.w("error", "Error getting documents.", task.getException());
                }
            }
        });
    }

    //Gets the image from firebase storage with the file uid and user uid
    private void getFilesFromStorage(String userUid, final List<String> fileuids) {

        //for all file uids in the list, it wil:
        for (final String fileuid : fileuids) {
            // gets the reference to the single file in storage, in the user's folder in storage
            final StorageReference storageReference = mStorage.child("receipts/").child(userUid + "/" + fileuid);
            //gets the donwnload url on the file from firebase
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    populateImageViews(uri);
                }
            });
        }
    }

    private void populateImageViews(final Uri uri) {
        //allows the thread. If this not added, the program will crash on main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Runs a UI thread to set the images, because it is asynk, and we only want the UI to update or set the images when ready.
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // the 4 image views are a added to a [], each time it gets through the loop, it will get the next imageview to set a new image.
                images[x].setImageBitmap(imageHandler.getImageBitmap(uri.toString()));
                x++;
            }
        });
    }
}