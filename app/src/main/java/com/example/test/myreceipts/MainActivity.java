package com.example.test.myreceipts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.myreceipts.BLL.ReceiptService;
import com.example.test.myreceipts.Entity.Receipt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends CustomMenu {

    ImageView btnCapture;
    GridView gridView;
    TextView tvGroupHeader;

    private String currentUserId;

    List<String> categories = new ArrayList<>();
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
    }

    private void createListeners() {
        createCaptureButtonListener();
        createOnCategoryRetrievedListener();
    }


    // TODO Move database call to DAO
    private void createOnCategoryRetrievedListener() {
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        mStore.collection("users").document(currentUserId).collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getId().equals("favorites")) {
                                categories.add(0, document.getId());
                            } else {
                                categories.add(document.getId());
                            }
                        }

                        ButtonAdapter buttonAdapter = new ButtonAdapter(getBaseContext(), categories);
                        gridView.setAdapter(buttonAdapter);
                    }
                });

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
