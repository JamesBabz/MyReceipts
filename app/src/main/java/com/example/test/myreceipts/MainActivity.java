package com.example.test.myreceipts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.test.myreceipts.BLL.GroupCollectionService;
import com.example.test.myreceipts.BLL.ReceiptService;
import com.example.test.myreceipts.Entity.Receipt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnCapture;
    GridView gridView;
    Spinner spinner;
    TextView tvGroupHeader;

    private String currentUserId;

    List<String> categories = new ArrayList<String>();
    List<String> cities = new ArrayList<String>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    List<String> urlList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = findViewById(R.id.gvShowAll);
        btnCapture = findViewById(R.id.btnCapture);
        tvGroupHeader = findViewById(R.id.tvGroupHeader);

        Bundle extras = getIntent().getExtras();

        currentUserId = extras.getString("USER");

        GroupCollectionService groupCollectionService = new GroupCollectionService();
        cities = groupCollectionService.getAllCitiesForUser("tWFxSCtcjQ50wGweZcMU");
        categories = groupCollectionService.getAllCategoriesForUser("tWFxSCtcjQ50wGweZcMU");


        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        GetAllReceiptsForCurrentUser(user);


        createSpinner();
        createListeners();

    }

    private void GetAllReceiptsForCurrentUser(String user) {
        FirebaseFirestore.getInstance()
                .collection("receipts") // Which collection
                .whereEqualTo("UID", user) // Only get those for current user
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() { // Wait till complete
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) { // If receipts where downloaded
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                final String currUrl = document.getData().get("URL").toString();
                                final StorageReference storageRef = storage.getReferenceFromUrl(currUrl);

                                //Convert image at reference point to a byte array
                                final long SIXTYFOUR_MEGABYTES = 1024 * 1024 * 64;
                                storageRef.getBytes(SIXTYFOUR_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // Use byte array to create bitmap
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        
                                        // Create receipt
                                        ReceiptService.getInstance().addReceipt(new Receipt(bitmap));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });


                                Log.d("TAG", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
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
