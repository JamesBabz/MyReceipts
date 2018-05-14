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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private FirebaseFirestore db;
    private FirebaseAuth fAuth;

    public CategoryService() {
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
    }

    public void createCategory(String catName) {
        //creates exist field
        Map<String, Boolean> exists = new HashMap<>();
        exists.put("exists", true);
        db.collection("users").document(getCurrentUser().getUid()).collection("categories").document(catName.toLowerCase()).set(exists);


    }

    public void addCategoriesToSpinner(Context context, Spinner spinner, boolean showFavorites, boolean showUnassigned) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("context", context);
        parameters.put("spinner", spinner);
        parameters.put("showFavorites", showFavorites);
        parameters.put("showUnassigned", showUnassigned);
        handleGetCategories(parameters, "spinner");
    }

    public void addCategoriesToButtonAdapter(Context context, GridView gridView, ProgressBar mProgressBar) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("context", context);
        parameters.put("gridView", gridView);
        parameters.put("progressBar", mProgressBar);
        handleGetCategories(parameters, "buttonAdapter");
    }


    private void handleGetCategories(final HashMap<String, Object> parameters, final String requestedBy) {
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        final List<String> categories = new ArrayList<>();
        parameters.put("categories", categories);
        mStore.collection("users").document(user).collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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

    private void createButtonAdapterTask(@NonNull Task<QuerySnapshot> task, HashMap<String, Object> parameters) {
        List<String> categories = (List<String>) parameters.get("categories");
        Context context = (Context) parameters.get("context");
        GridView gridView = (GridView) parameters.get("gridView");
        for (QueryDocumentSnapshot document : task.getResult()) {
            if (document.getId().equals("favorites")) {
                categories.add(0, document.getId());
            } else {
                categories.add(document.getId());
            }
        }

        ButtonAdapter buttonAdapter = new ButtonAdapter(context, categories);
        gridView.setAdapter(buttonAdapter);
        ProgressBar progressBar = (ProgressBar) parameters.get("progressBar");
        progressBar.setVisibility(View.GONE);

    }

    private void createSpinnerTask(@NonNull Task<QuerySnapshot> task, HashMap<String, Object> parameters) {
        List<String> categories = (List<String>) parameters.get("categories");
        Context context = (Context) parameters.get("context");
        Spinner spinner = (Spinner) parameters.get("spinner");
        boolean showFavorites = (boolean) parameters.get("showFavorites");
        boolean showUnassigned = (boolean) parameters.get("showUnassigned");

        for (QueryDocumentSnapshot document : task.getResult()) {
            String cat = document.getId();
            if ((cat.equals("favorites") && !showFavorites) || (cat.equals("unassigned") && !showUnassigned)) {
                continue;
            }
            cat = cat.substring(0, 1).toUpperCase() + cat.substring(1);
            categories.add(cat);
        }
        createSpinner(context, spinner, categories);
    }

    private void createSpinner(Context context, Spinner spinner, List<String> categories) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public FirebaseUser getCurrentUser() {
        return fAuth.getCurrentUser();
    }

}
