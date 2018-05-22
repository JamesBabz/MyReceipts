package com.example.test.myreceipts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.test.myreceipts.BLL.UserService;
import com.example.test.myreceipts.Entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by thomas on 16-04-2018.
 */

public class SignInActivity extends AppCompatActivity {

    private UserService userService;

    private TextView headLine;
    private EditText mEmailField;
    private EditText mPasswordField;
    Button btnSignIn;
    Button btnSignUp;
    Button btnCancel;
    ProgressBar progressBar;

    private FirebaseAuth firebaseAuth; // connection for firebase authentication
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        headLine = findViewById(R.id.txtHeadline);
        mEmailField = findViewById(R.id.txtUsername);
        mPasswordField = findViewById(R.id.txtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
        btnSignUp.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        userService = new UserService();

        // [START initialize_auth]
        firebaseAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

    }

    /**
     * Check if there is an user on start (AUTO LOGIN)
     * if there is an user, the main activity will open
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        try {
                Log.d("UserLogin", currentUser.getUid());
                startNewActivity(currentUser.getUid());

        } catch (@NonNull Exception exception) {
            Log.d("UserLogin", "fail");
        }
    }
    // [END on_start_check_user]

    /**
     * Calls the the method to create a account with the given properties
     * @param v to make it at click function in XML
     */
    public void btnCreateAccount(View v){
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        createAccount(email, password );
    }

    /**
     * creates an auth user and an user in database
     * @param email for the user
     * @param password for the user
     */
    private void createAccount(String email, String password) {
        // if the validator is not true, it will do nothing
        if (!validateForm())
        {
            return;
        }

        btnSignUp.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        // [START create_user_with_email]
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) // if it succeeded to create an auth user
                        {
                            // Sign in success, create an user for the account, and start new activity.
                            FirebaseUser user = userService.getCurrentUser();
                            //create a new user to make the update
                            User newUser = new User(user.getUid(),user.getEmail(), "", "");
                            // creates the user in database
                            userService.updateUser(newUser).addOnCompleteListener((new OnCompleteListener<DocumentSnapshot>() {
                                //when the user has been created in database
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    //if success it generate new folders for the user. 'favorites and unassigned'. The user will then always get the folders, which is needed
                                    userService.setStandardFolders();
                                }
                            }));

                            startNewActivity(user.getUid());

                        } else {
                            // If sign in fails, display a message to the user.
                            btnSignUp.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });
        // [END create_user_with_email]
    }

    /**
     * calls the signIn method with the given properties
     * @param v to make it at click function in XML
     */
    public void btnSignIn(View v){
        signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());

    }

    /**
     * sign in and checks in firebase auth service if user exists
     * @param email for the user
     * @param password for the user
     */
    private void signIn(String email, String password) {
        // if the validator is not true, it will do nothing
        if (!validateForm())
        {
            return;
        }

        btnSignIn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // [START sign_in_with_email]
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startNewActivity(user.getUid());

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            btnSignIn.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
        // [END sign_in_with_email]
    }


    /**
     * Validate the textfields. If one og both are empty, the field will set the error message.
     * @return a boolean . If the form is correctly or not
     */
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }


    /**
     * @param menu creates the menu for signIn view with given items
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    /**
     * @param item in the menu
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.optNewAccount:
                showCreateAccountView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * starts mainActivity
     * @param userUID sending to the new view in intent
     */
    private void startNewActivity(String userUID){
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra("USER", userUID);
        startActivity(intent);
    }

    /**
     * If new account is selected the view updates
     */
    private void showCreateAccountView(){
        headLine.setText("Create account");
        mPasswordField.setText("");
        mEmailField.setText("");
        btnSignIn.setVisibility(View.GONE);
        btnSignUp.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);

    }

    /**
     * sets the signIn view again
     * @param v to make it at click function in XML
     */
    public void btnCancelCreate(View v) {
        btnSignUp.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
        btnSignIn.setVisibility(View.VISIBLE);
        headLine.setText("Sign in");
        mPasswordField.setText("");
        mEmailField.setText("");
    }

    /**
     * overrides the main back button
     * @param keyCode
     * @param event
     * @return
     */
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
