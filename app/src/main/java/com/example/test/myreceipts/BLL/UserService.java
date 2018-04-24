package com.example.test.myreceipts.BLL;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.test.myreceipts.Entity.User;
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
    boolean isdone = false;

    public UserService() {
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void signOut(){
        fAuth.signOut();
    }

    public void getUser(String currentUserId,  final Callback callback){
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
                        Log.d("DATAUSER", "No such document");
                    }
                } else {
                    Log.d("DATAUSER", "get failed with ", task.getException());
                }
            }
        });
    }

}
