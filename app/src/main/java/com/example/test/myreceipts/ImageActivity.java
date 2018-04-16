package com.example.test.myreceipts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Jacob Enemark on 16-04-2018.
 */

public class ImageActivity extends AppCompatActivity {

    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    Bitmap bitmap;
    Bitmap rotatedBitmap;
    File mFile;
    Uri uriSavedImage;

    TextView name;
    TextView date;
    ImageView ivPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

        name = findViewById(R.id.tvName);
        date = findViewById(R.id.tvDate);
        ivPicture = findViewById(R.id.ivPicture);


        takePicture();
    }

    //Creates or check if there is a folder for pictures
    private String appFolderCheckandCreate(){

        String appFolderPath="";
        File externalStorage = Environment.getExternalStorageDirectory();

        if (externalStorage.canWrite())
        {
            appFolderPath = externalStorage.getAbsolutePath() + "/MyApp";
            File dir = new File(appFolderPath);

            if (!dir.exists())
            {
                dir.mkdirs();
            }

        }
        else
        {
            Toast.makeText(this, "  Storage media not found or is full ! ", Toast.LENGTH_LONG).show();
        }

        return appFolderPath;
    }

    //Opens the intent for camera
    private void takePicture()
    {
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



        return timeString;}

    //Creates the intent
    private void onClickTakePics()
    {
        mFile = new File(appFolderCheckandCreate(), "img" + getTimeStamp() + ".jpg");
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
                name.setText(mFile.toString());
                date.setText(getTimeStamp());
                ivPicture.setImageURI(bitmapToUriConverter(rotatedBitmap));

            } else
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled...", Toast.LENGTH_LONG).show();
                return;

            } else
                Toast.makeText(this, "Picture NOT taken - unknown error...", Toast.LENGTH_LONG).show();
        }
    }

    //gets the bitmap and rotate it
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    //get the bitmaps uri, to save as string in DB
    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, mBitmap.getWidth(), mBitmap.getHeight(), true);
            File file = new File(this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = this.openFileOutput(file.getName(),
                    Context.MODE_WORLD_READABLE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }

    //gets the current rotation of the bitmap, rotating it if needed.
    public void exifInterface()
    {
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
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
    }

}
