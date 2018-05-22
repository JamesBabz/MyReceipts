package com.example.test.myreceipts;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.test.myreceipts.BLL.CategoryService;
import com.example.test.myreceipts.BLL.ImageHandler;
import com.example.test.myreceipts.BLL.ReceiptService;
import com.example.test.myreceipts.BLL.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends CustomMenu {

    ImageHandler imageHandler;

    @BindView(R.id.categoryRefresh)
    ProgressBar mProgressBar;
    @BindView(R.id.gvShowAll)
    GridView gridView;
    @BindView(R.id.btnCapture)
    ImageView btnCapture;
    @BindView(R.id.ivRecent1)
    ImageView img1;
    @BindView(R.id.ivRecent2)
    ImageView img2;
    @BindView(R.id.ivRecent3)
    ImageView img3;
    @BindView(R.id.ivRecent4)
    ImageView img4;

    private FirebaseFirestore mStore;
    private StorageReference mStorage;
    private ReceiptService receiptService;
<<<<<<< HEAD
    private UserService mUserService;
    private int x = 0;
=======
    private int x;
>>>>>>> Development
    private List<String> categories = new ArrayList<>();
    private ImageView[] images;
    private CategoryService categoryService;
    List<String> receiptUids = new ArrayList<>();


    public MainActivity() {
        super(false, true);
        categoryService = new CategoryService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        imageHandler = new ImageHandler();
        receiptService = new ReceiptService();
        mUserService = new UserService();


        ButtonAdapter buttonAdapter = new ButtonAdapter(getBaseContext(), categories);
        gridView.setAdapter(buttonAdapter);
        mProgressBar.setVisibility(View.INVISIBLE);

        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();


    }

    @Override
    protected void onResume() {
        super.onResume();
        x = 0;
        images = new ImageView[]{img1, img2, img3, img4};
        getAllReceiptsForCategory(currentUserId);
        createListeners();
    }

    /**
     * A method to create all listeners for the class
     */
    private void createListeners() {
        createCaptureButtonListener();
        createOnCategoryRetrievedListener();
    }

    /**
     * Sets progressbar for loading categories
     */
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

    /**
     * Intent that opens the receipt creation activity
     */
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

    /**
     * Overrides the main back button
     */
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    /**
     * sets the visibility of the progressbar
     */
    private void setCategoryProgress() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Get all file uids from database
     *
     * @param userUid the userUID for the user
     */
    private void getAllReceiptsForCategory(final String userUid) {
        // get the reference to to the file uid, adds it to a list, and calls the method for getting the file in storage
        mStore.document("users/" + userUid).collection("allFiles").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().getDocuments().size() >= 1) {
                    receiptUids = new ArrayList<>();
                    TreeMap<Date, String> timeMap = new TreeMap<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (!document.getId().equals("0")) {

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

    /**
     * Gets the image from firebase storage with the file uid and user uid
     *
     * @param userUid  the userUID for the user
     * @param fileuids the file ids matching for the users
     */
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

    /**
     * Sets the recent imageviews with the newest receipts.
     * Checking strictMode permissions.
     *
     * @param uri the uri needed for the images
     */
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