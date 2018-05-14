package com.example.test.myreceipts;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.test.myreceipts.BLL.ReceiptService;

/**
 * Created by James on 08-05-2018.
 */

public class CategoryActivity extends CustomMenu {
    TextView tvGroupName;
    Spinner spinner;
    ScrollView svContainer;
    ImageView ivTestImage;
    ImageView ivTestImage2;
    ImageView ivTestImage3;

    public CategoryActivity() {
        super(true, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);
        tvGroupName = findViewById(R.id.tvGroupName);
        svContainer = findViewById(R.id.svContainer);
        ivTestImage = findViewById(R.id.ivTestImage);
        ivTestImage2 = findViewById(R.id.ivTestImage2);
        ivTestImage3 = findViewById(R.id.ivTestImage3);
        tvGroupName.setText(getIntent().getExtras().getString("categoryName"));

        ReceiptService receiptService = new ReceiptService();

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
