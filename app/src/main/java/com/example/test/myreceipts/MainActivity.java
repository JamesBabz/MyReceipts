package com.example.test.myreceipts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    Button btnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTest = findViewById(R.id.btnTest);
        createListeners();
        createSpinner();

        String[] categories = {
                "Electronics",
                "Furniture",
                "Consumables"
        };

        GridView gridView = (GridView)findViewById(R.id.gvShowAll);
        CategoryAdapter categoryAdapter = new CategoryAdapter(this);
        gridView.setAdapter(categoryAdapter);
    }

    private void createSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerSortBy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private void createListeners() {
        createTestButtonListener();
    }

    //Opens FriendActivity with all information about the selected friend
    private void createTestButtonListener(){

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                startActivity(intent);
            }
        });
    }
}
