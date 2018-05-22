package com.example.test.myreceipts;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.myreceipts.BLL.Callback;
import com.example.test.myreceipts.BLL.UserService;
import com.example.test.myreceipts.Entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;

/**
 * Created by thomas on 23-04-2018.
 */

public class ProfileActivity extends CustomMenu {

    @BindView(R.id.txtProfileEmail)
    EditText txtUsername;
    @BindView(R.id.txtProfileFirstname)
    EditText txtFirstname;
    @BindView(R.id.txtProfileLastname)
    EditText txtLastname;
    @BindView(R.id.txtFirstNewPassword)
    EditText txtFirstPassword;
    @BindView(R.id.btnResetPassword)
    Button btnResetpassword;

    private String currentUserId;

    private UserService userService;


    public ProfileActivity() {
        super(true, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userService = new UserService();

        Bundle extras = getIntent().getExtras();
        currentUserId = extras.getString("USER");

        fillOutTextViews();

    }


    // Sets the user information
    //Waits to set the information when the callback has received the data from the service.
    private void fillOutTextViews() {
        txtUsername.setText(userService.getCurrentUser().getEmail());

        //Makes the textView non editable or clickable
        txtUsername.setTag(txtUsername.getKeyListener());
        txtUsername.setKeyListener(null);
        Context context = this;
        userService.getUser(currentUserId, context, new Callback() {
            @Override
            public void act(User model) {
                txtFirstname.setText(model.getFirstname());
                txtLastname.setText(model.getLastname());
            }

        });

    }

    //creates a new user entity with the properties from the textview, and sends it to the service
    //when the task is completed, a toast will show
    private void updateUser() {
        User updatedUser = new User(currentUserId, txtUsername.getText().toString(), txtFirstname.getText().toString(), txtLastname.getText().toString());
        userService.updateUser(updatedUser).addOnCompleteListener((new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Toast.makeText(ProfileActivity.this, "Profile updated",
                        Toast.LENGTH_LONG).show();
            }
        }));
    }

    public void btnUpdateUser(View v) {
        updateUser();
    }

    //sends the new password to the service
    private void updateUserPassword() {
        Context context = this;
        String password = txtFirstPassword.getText().toString();
        btnResetpassword.setEnabled(false);
        userService.ResetUserPassword(password, context);

    }

    public void btnResetPassword(View v) {
        updateUserPassword();
    }
}
