package com.example.test.myreceipts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.test.myreceipts.BLL.ReceiptService;

/**
 * Created by James on 23-04-2018.
 */

public class ImageGroup extends CustomMenu {

    TextView tvGroupName;
    Spinner spinner;
    ScrollView svContainer;
    ImageView ivTestImage;
    ImageView ivTestImage2;
    ImageView ivTestImage3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_group);
        tvGroupName = findViewById(R.id.tvGroupName);
        svContainer = findViewById(R.id.svContainer);
        ivTestImage = findViewById(R.id.ivTestImage);
        ivTestImage2 = findViewById(R.id.ivTestImage2);
        ivTestImage3 = findViewById(R.id.ivTestImage3);
        tvGroupName.setText(getIntent().getExtras().getString("groupName"));

        ReceiptService receiptService = new ReceiptService();


        ivTestImage.setImageBitmap(receiptService.getReceipts().get(0).getBitmap());
        ivTestImage2.setImageBitmap(receiptService.getReceipts().get(1).getBitmap());
        ivTestImage3.setImageBitmap(receiptService.getReceipts().get(2).getBitmap());


        createSpinner();
    }



    private void createSpinner() {
        spinner = findViewById(R.id.spinnerSortBy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.group_sort_by_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
