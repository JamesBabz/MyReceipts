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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
                    }
                } else {
                    setToast(context, task.getException().toString());
                }
            }
        });
    }


    public void ResetUserPassword(String password, final Context context){
        FirebaseUser user = getCurrentUser();
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            setToast(context, "Password reset successful");
                        } else {
                            setToast(context, task.getException().getMessage());
                        }
                    }
                });
    }

    public Task updateUser(User user)
    {
        return db.collection("users").document(getCurrentUser().getUid()).set(user);
    }

    private void setToast(Context context, CharSequence text){
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void setStandardFolders(){
        Map<String, Boolean> exists = new HashMap<>();
        exists.put("exists", true);
        CollectionReference catReference = db.collection("users").document(getCurrentUser().getUid()).collection("categories");

        catReference.document("favorites").set(exists);
        catReference.document("favorites").collection("fileuids").document("0").set(exists);
        catReference.document("unassigned").set(exists);
        catReference.document("unassigned").collection("fileuids").document("0").set(exists);
    }

}
