package com.example.test.myreceipts.BLL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.test.myreceipts.ButtonAdapter;
import com.example.test.myreceipts.DAL.DAO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by James on 14-05-2018.
 */

public class CategoryService {

    private final CollectionReference catRef;
    private FirebaseFirestore db;
    private DAO dao;
    private FirebaseAuth fAuth;
    private String user;

    public CategoryService() {
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser().getUid();
        catRef = db.collection("users").document(user).collection("categories");
        dao = new DAO();
    }

    /**
     * Create a new category
     * @param catName Name of the category to add
     */
    public void createCategory(String catName) {
        //creates exist field
        Map<String, Boolean> exists = new HashMap<>();
        exists.put("exists", true);
        catRef.document(catName.toLowerCase()).set(exists);
    }

    /**
     * Delete a category by name
     * @param catName The name of the category
     */
    public void deleteCategory(String catName){
        dao.deleteCategory(catName);
    }

    /**
     * The method to call if you want to add the categories to a spinner
     *
     * @param spinner        The spinner to populate with the categories
     * @param showFavorites  Should the "favorites" category be shown?
     * @param showUnassigned Should the "unassigned" category be shown?
     */
    public void addCategoriesToSpinner(Spinner spinner, boolean showFavorites, boolean showUnassigned) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("spinner", spinner);
        parameters.put("showFavorites", showFavorites);
        parameters.put("showUnassigned", showUnassigned);
        handleGetCategories(parameters, "spinner");
    }

    /**
     * The method to call if you want to add the categories to a button adapter
     *
     * @param gridView     The gridview where the buttons should be inserted into
     * @param mProgressBar A ProgressBar to show incase it takes too long to get the categories
     */
    public void addCategoriesToButtonAdapter(GridView gridView, ProgressBar mProgressBar) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("gridView", gridView);
        parameters.put("progressBar", mProgressBar);
        handleGetCategories(parameters, "buttonAdapter");
    }

    /**
     * The private method to handle all category calls from the outside
     *
     * @param parameters  Whatever parameters have been supplied from previous methods
     * @param requestedBy The identifier of which method called this method
     */
    private void handleGetCategories(final HashMap<String, Object> parameters, final String requestedBy) {
        FirebaseFirestore mStore = FirebaseFirestore.getInstance(); // the database
        //We create a new list to hold the categories
        final List<String> categories = new ArrayList<>();
        parameters.put("categories", categories);

        mStore.collection("users").document(user).collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //Check which method called this one and run the task accordingly
                        switch (requestedBy) {
                            case "spinner":
                                createSpinnerTask(task, parameters);
                                break;
                            case "buttonAdapter":
                                createButtonAdapterTask(task, parameters);
                        }

                    }
                });

    }

    /**
     * The private task method that will be called when we want categories for the button adapter
     *
     * @param task       The task being run when categories have been gotten from the database
     * @param parameters The paramaters from "addCategoriesToButtonAdapter" with the category added
     */
    private void createButtonAdapterTask(@NonNull Task<QuerySnapshot> task, HashMap<String, Object> parameters) {
        // Get all the parameters from the hashmap
        // Because these methods are tightly coupled we can safely cast the objects
        List<String> categories = (List<String>) parameters.get("categories");
        GridView gridView = (GridView) parameters.get("gridView");

        for (QueryDocumentSnapshot document : task.getResult()) {
            if (document.getId().equals("favorites")) {
                categories.add(0, document.getId()); // Put favorites first
            } else if (document.getId().equals("unassigned")) {
                categories.add(1, document.getId()); // Put unassigned second
            } else {
                categories.add(document.getId()); // Add the rest
            }
        }

        // Create the adapter and set it
        ButtonAdapter buttonAdapter = new ButtonAdapter(gridView.getContext(), categories);
        gridView.setAdapter(buttonAdapter);

        // We are done now so we can hide the progressbar
        ProgressBar progressBar = (ProgressBar) parameters.get("progressBar");
        progressBar.setVisibility(View.GONE);

    }

    /**
     * The private task method that will be called when we want categories for the spinner
     *
     * @param task       The task being run when categories have been gotten from the database
     * @param parameters The paramaters from "addCategoriesToSpinner" with the category added
     */
    private void createSpinnerTask(@NonNull Task<QuerySnapshot> task, HashMap<String, Object> parameters) {
        // Get all the parameters from the hashmap
        // Because these methods are tightly coupled we can safely cast the objects
        List<String> categories = (List<String>) parameters.get("categories");
        Spinner spinner = (Spinner) parameters.get("spinner");
        boolean showFavorites = (boolean) parameters.get("showFavorites");
        boolean showUnassigned = (boolean) parameters.get("showUnassigned");

        for (QueryDocumentSnapshot document : task.getResult()) {
            String cat = document.getId();
            // Check if we want to show the favorites and the unassigned categories
            if ((cat.equals("favorites") && !showFavorites) || (cat.equals("unassigned") && !showUnassigned)) {
                continue;
            }
            cat = cat.substring(0, 1).toUpperCase() + cat.substring(1); // Capitalize
            if (cat.equals("Unassigned")) {
                categories.add(0, cat); // Unassigned should be the default
            } else {
                categories.add(cat);
            }

        }
        createSpinner(spinner, categories);
    }

    /**
     * Creates the spinner with custom array adapter
     *
     * @param spinner    The spinner to be created
     * @param categories The items for the spinner
     */
    private void createSpinner(Spinner spinner, List<String> categories) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
