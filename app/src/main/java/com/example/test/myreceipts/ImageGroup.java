package com.example.test.myreceipts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by James on 23-04-2018.
 */

public class ImageGroup extends AppCompatActivity {

    TextView tvGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_group);
        tvGroupName = findViewById(R.id.tvGroupName);

        tvGroupName.setText(getIntent().getExtras().getString("groupName"));
    }
}
