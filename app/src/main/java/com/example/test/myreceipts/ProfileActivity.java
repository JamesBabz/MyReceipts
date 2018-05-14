package com.example.test.myreceipts;

import android.content.Context;
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
import android.widget.Toast;

import com.example.test.myreceipts.BLL.Callback;
import com.example.test.myreceipts.BLL.UserService;
import com.example.test.myreceipts.Entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by thomas on 23-04-2018.
 */

public class ProfileActivity extends CustomMenu {

    private EditText txtUsername;
    private EditText txtFirstname;
    private EditText txtLastname;

    private EditText txtFirstPassword;
    private Button btnResetpassword;
    private Button btnUpdateUser;
private boolean isThereAnUser = true;
     private String currentUserId;

     private UserService userService;

    public ProfileActivity() {
        super(true, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        txtUsername = findViewById(R.id.txtProfileEmail);
        txtFirstname = findViewById(R.id.txtProfileFirstname);
        txtLastname = findViewById(R.id.txtProfileLastname);
        btnResetpassword = findViewById(R.id.btnResetPassword);
        txtFirstPassword = findViewById(R.id.txtFirstNewPassword);
        btnUpdateUser = findViewById(R.id.btnUpdateProfile);
        userService = new UserService();

        Bundle extras = getIntent().getExtras();
        currentUserId = extras.getString("USER");

        fillOutTextViews();

    }




    private void fillOutTextViews()
    {
        txtUsername.setText(userService.getCurrentUser().getEmail());
        txtUsername.setTag(txtUsername.getKeyListener());
        txtUsername.setKeyListener(null);
        Context context = this;
       userService.getUser(currentUserId, context, new Callback() {
           @Override
           public void act(User model) {
               isThereAnUser = true;
               txtFirstname.setText(model.getFirstname());
               txtLastname.setText(model.getLastname());
           }
           });

    }

    private void updateUser(){
        User updatedUser = new User(currentUserId,txtUsername.getText().toString(), txtFirstname.getText().toString(), txtLastname.getText().toString());
      userService.updateUser(updatedUser).addOnCompleteListener((new OnCompleteListener<DocumentSnapshot>() {
          @Override
          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              Toast.makeText(ProfileActivity.this, "Profile updated",
                      Toast.LENGTH_LONG).show();
          }
      }));
    }

    public void btnUpdateUser(View v){
        updateUser();
    }

    private void updateUserPassword(){
        Context context = this;
        String password = txtFirstPassword.getText().toString();
        btnResetpassword.setEnabled(false);
        userService.ResetUserPassword(password, context);

    }

    public void btnResetPassword(View v){
        updateUserPassword();
    }
}
