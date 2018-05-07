package com.example.test.myreceipts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.test.myreceipts.BLL.UserService;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Jacob Enemark on 07-05-2018.
 */

public class CustomMenu extends AppCompatActivity {

    UserService mUserService;
    boolean backBtn;
    boolean profileMenuItem;

    public CustomMenu(Boolean backBtn, Boolean profileMenuItem)
    {
        mUserService = new UserService();
        this.backBtn = backBtn;
        this.profileMenuItem = profileMenuItem;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top, menu);
        if(backBtn) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if(!profileMenuItem) {
            MenuItem item = menu.findItem(R.id.optProfile);
            item.setVisible(false);
        }
        return true;
    }

    public boolean onSupportNavigateUp(){
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void signOut(){
        mUserService.signOut();
        Intent intent = new Intent(CustomMenu.this, SignInActivity.class);
        startActivity(intent);
    }

    private void openProfileView() {
        Intent intent = new Intent(CustomMenu.this, ProfileActivity.class);
        intent.putExtra("USER", mUserService.getCurrentUser().getUid());
        startActivity(intent);
    }
}
