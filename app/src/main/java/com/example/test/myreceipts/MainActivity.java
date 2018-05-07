package com.example.test.myreceipts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.test.myreceipts.BLL.ReceiptService;
import com.example.test.myreceipts.Entity.Receipt;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnCapture;
    GridView gridView;
    TextView tvGroupHeader;

    private String currentUserId;

    List<String> categories = new ArrayList<String>();
    List<Receipt> receipts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = findViewById(R.id.gvShowAll);
        btnCapture = findViewById(R.id.btnCapture);
        tvGroupHeader = findViewById(R.id.tvGroupHeader);

        Bundle extras = getIntent().getExtras();
        currentUserId = extras.getString("USER");

        ReceiptService receiptService = new ReceiptService();

        receipts = receiptService.getAllReceiptsForUser(currentUserId);
        categories = receiptService.getAllCategoriesForUser(currentUserId);

        ButtonAdapter buttonAdapter = new ButtonAdapter(getBaseContext(), categories);
        gridView.setAdapter(buttonAdapter);

        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();


        createListeners();

        receiptService.getAllCategoriesForUser(user);
    }

    private void createListeners() {
        createCaptureButtonListener();
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

    private void openProfileView() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra("USER", currentUserId);
        startActivity(intent);
    }
}
