package com.example.test.myreceipts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.myreceipts.BLL.ImageHandler;
import com.example.test.myreceipts.BLL.ReceiptService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jacob Enemark on 16-04-2018.
 */

public class ImageActivity extends CustomMenu {

    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    String currDate = System.currentTimeMillis() + "";

    Bitmap bitmap;
    Bitmap rotatedBitmap;
    File mFile;
    Uri uriSavedImage;
    List<String> categories = new ArrayList<>();

    private String mTimestamp;
    private ImageHandler imgHandler;
    private ReceiptService receiptService;

    boolean setFavorite = false;

    @BindView(R.id.tvDate)
    TextView date;
    @BindView(R.id.ivPicture)
    ImageView ivPicture;
    @BindView(R.id.favorite)
    ImageButton favorite;
    @BindView(R.id.btnSave)
    TextView save;
    @BindView(R.id.etName)
    TextView etName;
    @BindView(R.id.spinner)
    Spinner spinner;

    public ImageActivity() {
        super(true, true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

        ButterKnife.bind(this);

        date.setText("Date:");
        receiptService = new ReceiptService();

        createListeners();
        mTimestamp = getTimeStamp();
        imgHandler = new ImageHandler();
    }

    //Listeners to avoid having them all in onCreate
    public void createListeners() {
        takePicture();
        isSetFavorite();
        saveReceipt();
        createOnCategoryRetrievedListener();
    }

    //Creates or check if there is a folder for pictures
    private String appFolderCheckandCreate() {

        String appFolderPath = "";
        File externalStorage = Environment.getExternalStorageDirectory();

        if (externalStorage.canWrite()) {
            appFolderPath = externalStorage.getAbsolutePath() + "/MyApp";
            File dir = new File(appFolderPath);

            if (!dir.exists()) {
                dir.mkdirs();
            }

        } else {
            Toast.makeText(this, "  Storage media not found or is full ! ", Toast.LENGTH_LONG).show();
        }

        return appFolderPath;
    }

    //Opens the intent for camera
    private void takePicture() {
        ivPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onClickTakePics();
            }
        });
    }

    //returns a given timestamp for a picture
    private String getTimeStamp() {

        final long timestamp = new Date().getTime();

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);

        final String timeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());


        return timeString;
    }

    //Creates the intent
    private void onClickTakePics() {
        mFile = new File(appFolderCheckandCreate(), "img" + mTimestamp + ".jpg");
        uriSavedImage = Uri.fromFile(mFile);

        // create Intent to take a picture
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        intent.putExtra("return-data", true);

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    //On result OK runs the picture through the exifInterface to set ORIENTATION correctly
    //sets the taken picture on imageView
    //if no resultcode OK toast
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                exifInterface();

                date.setText("Date: " + mTimestamp);

                ivPicture.setImageURI(imgHandler.bitmapToUriConverter(getBaseContext(), rotatedBitmap));

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled...", Toast.LENGTH_LONG).show();
                return;

            } else
                Toast.makeText(this, "Picture NOT taken - unknown error...", Toast.LENGTH_LONG).show();
        }
    }


    //gets the current rotation of the bitmap, rotating it if needed.
    public void exifInterface() {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriSavedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(mFile + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        rotatedBitmap = null;
        int rotation = 0;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotation = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotation = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotation = 270;
                break;

            default:
                rotatedBitmap = bitmap;
        }
        rotatedBitmap = imgHandler.rotateImage(bitmap, rotation);
    }

    //Set an receipt as favorite
    public void isSetFavorite() {

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!setFavorite) {
                    favorite.setImageResource(android.R.drawable.star_big_on);
                } else if (setFavorite) {
                    favorite.setImageResource(android.R.drawable.star_big_off);
                }
                setFavorite = !setFavorite;
            }
        });
    }

    //Saves the receipe on button save click
    public void saveReceipt() {
        final String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Map<String, Object> information = new HashMap<>();

        save.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                information.put("name", etName.getText());
                information.put("user", user);
                information.put("category", spinner.getSelectedItem().toString().toLowerCase());
                information.put("timestamp", mTimestamp);
                information.put("favorite", setFavorite);
                receiptService.saveReceipt(getBaseContext(), rotatedBitmap, information);
            }
        });
    }

    // TODO Move database call to DAO. Duplicated code in MainActivity and CustomMenu
    private void createOnCategoryRetrievedListener() {
        categories = new ArrayList<>();
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        mStore.collection("users").document(user).collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String cat = document.getId();
                            if (!cat.equals("favorites")) {
                                cat = cat.substring(0, 1).toUpperCase() + cat.substring(1);
                                categories.add(cat);
                            }
                        }
                        createSpinner();
                    }
                });

    }

    private void createSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}









