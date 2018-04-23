package com.example.test.myreceipts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by James on 23-04-2018.
 */

public class ImageGroup extends AppCompatActivity {

    TextView tvGroupName;
    Spinner spinner;
    ScrollView svContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_group);
        tvGroupName = findViewById(R.id.tvGroupName);
        svContainer = findViewById(R.id.svContainer);
        tvGroupName.setText(getIntent().getExtras().getString("groupName"));
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
