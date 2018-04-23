package com.example.test.myreceipts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.test.myreceipts.Entity.User;

public class MainActivity extends AppCompatActivity {

    Button btnCapture;
    GridView gridView;
    Spinner spinner;

    private String currentUserId;

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


        createSpinner();
        createListeners();
        Bundle extras = getIntent().getExtras();

        currentUserId = extras.getString("USER");
    }

    private void createSpinner() {
        spinner = findViewById(R.id.spinnerSortBy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by_array, android.R.layout.simple_spinner_item);
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
                if (position == 0) {
                    buttonAdapter = new ButtonAdapter(getBaseContext(), categories);
                } else if (position == 1) {
                    buttonAdapter = new ButtonAdapter(getBaseContext(), cities);
                } else {
                    buttonAdapter = new ButtonAdapter(getBaseContext(), null);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.optProfile:
                openProfileView();
                return true;
            case R.id.optSignOut:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openProfileView(){
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra("USER", currentUserId);
        startActivity(intent);
    }
}
