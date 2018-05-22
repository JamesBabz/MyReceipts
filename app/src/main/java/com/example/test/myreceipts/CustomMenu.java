package com.example.test.myreceipts;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.test.myreceipts.BLL.CategoryService;
import com.example.test.myreceipts.BLL.UserService;

import org.w3c.dom.Text;

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
                openCreateCategoryDialog();
                return true;
            case R.id.optDeleteCategory:
                openDeleteCategoryDialog();
                return true;
            case R.id.optRenameCategory:
                openRenameCategoryDialog();
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

    private void openCreateCategoryDialog() {
        Builder builder = new Builder(this);
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

    private void openDeleteCategoryDialog() {
        Builder builder = new Builder(this);
        builder.setTitle("Delete category");


        // Setup the spinner
        final Spinner spinner = new Spinner(this);
        categoryService.addCategoriesToSpinner(spinner, false, false);
        builder.setView(spinner);

        // Setup the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (spinner.getSelectedItem() == null) {
                    dialog.cancel();
                    return;
                }
                categoryService.deleteCategory(spinner.getSelectedItem().toString().toLowerCase());

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

    private void openRenameCategoryDialog() {


        // Setup the spinner
        final Spinner spinner = new Spinner(this);
        categoryService.addCategoriesToSpinner(spinner, false, false);

        // Setup inputfield
        final EditText input = new EditText(this);


        Builder builder = createRenameView(spinner, input);


        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (spinner.getSelectedItem() == null) {
                    dialog.cancel();
                    return;
                }
                categoryService.moveCategory(spinner.getSelectedItem().toString().toLowerCase(), input.getText().toString());

                //Creates category with input text as name
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

    private Builder createRenameView(Spinner spinner, EditText input) {
        Builder builder = new Builder(this);
        builder.setTitle("Rename category");
        LinearLayout outerContainer = new LinearLayout(this);
        outerContainer.setOrientation(LinearLayout.VERTICAL);

        // Create the spinner and label and put it into a container
        LinearLayout spinnerContainer = createSpinnerContainer(spinner);

        LinearLayout textContainer = createTextContainer(input);

        outerContainer.addView(spinnerContainer);
        outerContainer.addView(textContainer);

        builder.setView(outerContainer);
        return builder;
    }

    private LinearLayout createTextContainer(EditText input) {
        // Create the renameText and label and put it into a container
        LinearLayout textContainer = new LinearLayout(this);
        // Setup the input
        TextView renameText = new TextView(this);
        renameText.setText("New name:");
        input.setHint(" Enter a new name ");
        // Specify which input type
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        textContainer.addView(renameText);
        textContainer.addView(input);
        return textContainer;

    }


    private LinearLayout createSpinnerContainer(Spinner spinner) {
        LinearLayout spinnerContainer = new LinearLayout(this);
        spinnerContainer.setGravity(Gravity.CENTER);
        TextView selectCatText = new TextView(this);
        selectCatText.setText("Select category:");
        spinnerContainer.addView(selectCatText);
        spinnerContainer.addView(spinner);
        return spinnerContainer;
    }

    private void refreshPage() {
        //Refresh page for new categories
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
