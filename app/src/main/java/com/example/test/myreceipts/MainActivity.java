package com.example.test.myreceipts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.myreceipts.BLL.ReceiptService;
import com.example.test.myreceipts.Entity.Receipt;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends CustomMenu {

    ImageView btnCapture;
    GridView gridView;
    TextView tvGroupHeader;

    private String currentUserId;

    List<String> categories = new ArrayList<String>();
    List<Receipt> receipts = new ArrayList<>();

    public MainActivity() {
        super(false, true);
    }


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
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
}
