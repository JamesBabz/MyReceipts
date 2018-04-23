package com.example.test.myreceipts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnCapture;
    GridView gridView;
    Spinner spinner;
    TextView tvGroupHeader;

    String[] categories = {
            "Electronics",
            "Furniture",
            "Consumables"
    };

    String[] cities = {
            "Esbjerg",
            "Kolding",
            "Odense",
            "København",
            "Århus",
            "Vejle"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = findViewById(R.id.gvShowAll);
        btnCapture = findViewById(R.id.btnCapture);
        tvGroupHeader = findViewById(R.id.tvGroupHeader);


        createSpinner();
        createListeners();
    }

    private void createSpinner() {
        spinner = findViewById(R.id.spinnerSortBy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.main_sort_by_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private void createListeners() {
        createCaptureButtonListener();
        createSortListener();
        createFolderListener();
    }

    private void createSortListener() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ButtonAdapter buttonAdapter;
                switch (position) {
                    case 0:
                        buttonAdapter = new ButtonAdapter(getBaseContext(), categories);
                        tvGroupHeader.setText(getResources().getString(R.string.categories_header));
                        break;
                    case 1:
                        buttonAdapter = new ButtonAdapter(getBaseContext(), cities);
                        tvGroupHeader.setText(getResources().getString(R.string.cities_header));
                        break;
                    default:
                        buttonAdapter = new ButtonAdapter(getBaseContext(), null);

                        break;
                }
                gridView.setAdapter(buttonAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void createFolderListener() {

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
}
