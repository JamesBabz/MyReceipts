package com.example.test.myreceipts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.myreceipts.BLL.CategoryService;
import com.example.test.myreceipts.BLL.ImageHandler;
import com.example.test.myreceipts.BLL.ReceiptService;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jacob Enemark on 16-04-2018.
 */

public class ImageActivity extends CustomMenu {

    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    Bitmap bitmap;
    Bitmap rotatedBitmap;
    File mFile;
    Uri uriSavedImage;
    boolean setFavorite = false;
    boolean checkForPicture = false;
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
    private String mTimestamp;
    private ImageHandler imgHandler;
    private ReceiptService receiptService;
    private CategoryService categoryService;

    public ImageActivity() {
        super(true, true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);
        ButterKnife.bind(this); // Binds the views with their variable names

        save.setEnabled(false);
        date.setText("Date:");
        receiptService = new ReceiptService();
        categoryService = new CategoryService();
        imgHandler = new ImageHandler();
        mTimestamp = getTimeStamp();


        checkForDate();
        createListeners();
    }

    /**
     * A method to create all listeners for the class
     */
    private void createListeners() {
        createOnPictureButtonClickedListener();
        createIsSetFavoriteListener();
        createOnSaveReceiptListener();
        createOnCategoryRetrievedListener();
    }

    /**
     * Checks for app folder path and create if it doesnt exist
     *
     * @return The path for the app folder
     */
    private String appFolderCheckAndCreate() {

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

    /**
     * Handles onClick event for the camera button
     */
    private void createOnPictureButtonClickedListener() {
        ivPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startImageCaptureIntent();
            }
        });
    }

    /**
     * Gets the current time
     *
     * @return The current time formatted as "yyyy-MM-dd HH:mm:ss"
     */
    private String getTimeStamp() {

        final long timestamp = new Date().getTime();

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);

        final String timeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());


        return timeString;
    }

    /**
     * Starts the intent used for taking pictures
     */
    private void startImageCaptureIntent() {
        mFile = new File(appFolderCheckAndCreate(), "img" + mTimestamp + ".jpg");
        uriSavedImage = Uri.fromFile(mFile);

        // create Intent to take a picture
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        intent.putExtra("return-data", true);

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }


    /**
     * Checks if the returned activity result is ok or cancelled
     * Sets image if resultCode is ok
     *
     * @param requestCode The requested code for the activity
     * @param resultCode  The result sent from the activity
     * @param data        The Intent (not used)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // On result OK runs the picture through the exifInterface to set ORIENTATION correctly
                exifInterface();

                date.setText("Date: " + mTimestamp);

                // Uses the imageHandler to set the image
                ivPicture.setImageURI(imgHandler.bitmapToUriConverter(getBaseContext(), rotatedBitmap));
                checkForPicture = true;

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled...", Toast.LENGTH_LONG).show();
                return;

            } else
                Toast.makeText(this, "Picture NOT taken - unknown error...", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Gets the rotation of the image and rotates if needed
     */
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
        // The current orientation of the image
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

        //Rotate the image with the imageHandler
        rotatedBitmap = imgHandler.rotateImage(bitmap, rotation);
    }

    /**
     * Creates onClick listener for the favorite icon
     */
    public void createIsSetFavoriteListener() {

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!setFavorite) {
                    favorite.setImageResource(android.R.drawable.star_big_on);
                } else if (setFavorite) {
                    favorite.setImageResource(android.R.drawable.star_big_off);
                }
                setFavorite = !setFavorite; // Swaps between true or false
            }
        });
    }

    /**
     * Creates onClick listner for the save button
     */
    public void createOnSaveReceiptListener() {
        final String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Create a hashMap containing all needed information for the receipt
        final Map<String, Object> information = new HashMap<>();

        save.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                information.put("name", etName.getText()); // Name of the receipt
                information.put("user", user); // ID of current user
                information.put("category", spinner.getSelectedItem().toString().toLowerCase()); // Selected category
                information.put("timestamp", mTimestamp); // The time the receipt is created
                information.put("favorite", setFavorite); // is it a favorite?
                receiptService.saveReceipt(getBaseContext(), rotatedBitmap, information); // Save it
            }
        });
    }

    /**
     * Checks if the date is set, if date is not set save button will be disabled.
     * Date is set when picture is taken.
     */
    private void checkForDate() {

        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                save.setEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                save.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                save.setEnabled(true);
            }
        });
    }


    private void createOnCategoryRetrievedListener() {
        categoryService.addCategoriesToSpinner(spinner, false, true);
    }

}









