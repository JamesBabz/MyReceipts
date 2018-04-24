package com.example.test.myreceipts.BLL;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.myreceipts.Entity.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by thomas on 23-04-2018.
 */

public class UserService {
    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private User returnUser;

    public UserService() {
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public FirebaseUser getCurrentUser(){
        return fAuth.getCurrentUser();
    }
    public void signOut(){
        fAuth.signOut();
    }

    public void getUser(String currentUserId, final Context context, final Callback callback){
        DocumentReference docRef = db.collection("users").document(currentUserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        returnUser = new User(document.getData());
                        callback.act(returnUser);
                    } else {
                        setToast(context, "No such document");
                    }
                } else {
                    setToast(context, task.getException().toString());
                }
            }
        });
    }

    public void updateUser(String currentUserId, final Context context, User user){
        DocumentReference docRef = db.collection("users").document(currentUserId);

        docRef
                .update(
                        "username", user.getUsername(),
                        "firstname", user.getFirstname(),
                        "lastname", user.getLastname())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setToast(context, "Update successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setToast(context, e.getMessage());
                    }
                });

    }

    public void newUser(User user)
    {
        db.collection("users").document(getCurrentUser().getUid()).set(user);
    }

    private void setToast(Context context, CharSequence text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
