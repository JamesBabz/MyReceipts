package com.example.test.myreceipts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.test.myreceipts.BLL.UserService;
import com.example.test.myreceipts.Entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Created by thomas on 23-04-2018.
 */

public class ProfileActivity extends AppCompatActivity {

    private EditText txtUsername;
    private EditText txtFirstname;
    private EditText txtLastname;
    Button btnTest;

     private String currentUserId;

     private UserService userService;

     private User newUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        txtUsername = findViewById(R.id.txtProfileEmail);
        txtFirstname = findViewById(R.id.txtProfileFirstname);
        txtLastname = findViewById(R.id.txtProfileLastname);
        btnTest = findViewById(R.id.btnTestProfile);
        userService = new UserService();

        Bundle extras = getIntent().getExtras();
        currentUserId = extras.getString("USER");
        Log.d("userId", currentUserId+"");

        fillOutTextViews();

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
            case R.id.optSignOut:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut(){
        userService.signOut();
        Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
        startActivity(intent);
    }


    private void fillOutTextViews()
    {
        DocumentReference docRef = db.collection("users").document(currentUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        newUser = new User(document.getData());
                        txtFirstname.setText(newUser.getFirstname());
                        txtUsername.setText(newUser.getEmail());
                        txtLastname.setText(newUser.getLastname());
                    } else {
                        Log.d("DATAUSER", "No such document");
                    }
                } else {
                    Log.d("DATAUSER", "get failed with ", task.getException());
                }
            }
        });
    }

    private void updateUser(){
        DocumentReference docRef = db.collection("users").document(currentUserId);

// Set the "isCapital" field of the city 'DC'
        docRef
                .update(
                        "username", txtUsername.getText().toString(),
                        "firstname", txtFirstname.getText().toString(),
                        "lastname", txtLastname.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DATAUSER", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DATAUSER", "Error updating document", e);
                    }
                });

    }

    public void testShit(View v){
        updateUser();
    }
}
