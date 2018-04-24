package com.example.test.myreceipts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.test.myreceipts.BLL.Callback;
import com.example.test.myreceipts.BLL.UserService;
import com.example.test.myreceipts.Entity.User;
/**
 * Created by thomas on 23-04-2018.
 */

public class ProfileActivity extends AppCompatActivity {

    private EditText txtUsername;
    private EditText txtFirstname;
    private EditText txtLastname;

    private EditText txtFirstPassword;
    private Button btnResetpassword;

     private String currentUserId;

     private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        txtUsername = findViewById(R.id.txtProfileEmail);
        txtFirstname = findViewById(R.id.txtProfileFirstname);
        txtLastname = findViewById(R.id.txtProfileLastname);
        btnResetpassword = findViewById(R.id.btnResetPassword);
        txtFirstPassword = findViewById(R.id.txtFirstNewPassword);
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
        Context context = this;
       userService.getUser(currentUserId, context, new Callback() {
           @Override
           public void act(User model) {
               txtUsername.setText(model.getUsername());
               txtUsername.setTag(txtUsername.getKeyListener());
               txtUsername.setKeyListener(null);
               txtFirstname.setText(model.getFirstname());
               txtLastname.setText(model.getLastname());
           }
           });

    }

    private void updateUser(){

        Context context = this;
        User updatedUser = new User(txtUsername.getText().toString(), txtFirstname.getText().toString(), txtLastname.getText().toString());
        userService.updateUser(currentUserId, context, updatedUser);
    }

    public void btnUpdateUser(View v){
        updateUser();
    }

    private void updateUserPassword(){
        Context context = this;
        String password = txtFirstPassword.getText().toString();
        userService.ResetUserPassword(password, context);

    }

    public void btnResetPassword(View v){
        updateUserPassword();
    }
}
