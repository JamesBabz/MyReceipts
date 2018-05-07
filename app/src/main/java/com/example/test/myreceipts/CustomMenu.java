package com.example.test.myreceipts;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.test.myreceipts.BLL.UserService;

/**
 * Created by Jacob Enemark on 07-05-2018.
 */

public class CustomMenu extends AppCompatActivity {

    UserService mUserService;

    public CustomMenu()
    {
        mUserService = new UserService();
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
                mUserService.signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
