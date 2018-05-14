package com.example.test.myreceipts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.test.myreceipts.BLL.CategoryService;
import com.example.test.myreceipts.BLL.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jacob Enemark on 07-05-2018.
 */

public class CustomMenu extends AppCompatActivity {

    private UserService mUserService;
    private CategoryService categoryService;

    private String m_Text = "";

    boolean backBtn;
    boolean profileMenuItem;

    public CustomMenu(Boolean backBtn, Boolean profileMenuItem) {
        mUserService = new UserService();
        categoryService = new CategoryService();
        this.backBtn = backBtn;
        this.profileMenuItem = profileMenuItem;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top, menu);
        if (backBtn) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (!profileMenuItem) {
            MenuItem item = menu.findItem(R.id.optProfile);
            item.setVisible(false);
        }
        return true;
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.optSignOut:
                signOut();
                return true;
            case R.id.optProfile:
                openProfileView();
                return true;
            case R.id.optCreateCategory:
                createCategory();
                return true;
            case R.id.optDeleteCategory:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void signOut() {
        mUserService.signOut();
        Intent intent = new Intent(CustomMenu.this, SignInActivity.class);
        startActivity(intent);
    }

    private void openProfileView() {
        Intent intent = new Intent(CustomMenu.this, ProfileActivity.class);
        intent.putExtra("USER", mUserService.getCurrentUser().getUid());
        startActivity(intent);
    }

    private void createCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create category");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify which input type
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Text input for AlertDialog
                m_Text = input.getText().toString();

                //Creates category with input text as name
                categoryService.createCategory(m_Text);
                refreshPage();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void deleteCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete category");


        // Set up the input
        final EditText input = new EditText(this);
        // Specify which input type
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Text input for AlertDialog
                m_Text = input.getText().toString();

                //Creates category with input text as name
                categoryService.createCategory(m_Text);
                refreshPage();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void refreshPage() {
        //Refresh page for new categories
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
