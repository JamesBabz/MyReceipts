package com.example.test.myreceipts.BLL;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.example.test.myreceipts.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private FirebaseAuth fAuth; // connection to Firebase authentication service
    private FirebaseFirestore db; // connection Firebase database
    private User returnUser;

    public UserService() {
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * @return the current user from Firebase authentication service.
     */
    public FirebaseUser getCurrentUser(){
        return fAuth.getCurrentUser();
    }

    /**
     * Firebase authentication service takes care of the sign out part, by calling the 'signOut()'
     */
    public void signOut(){
        fAuth.signOut();
    }

    /**
     * gets the current user data from from database, and sets it to the callback
     * @param currentUserId the UID for the user to find
     * @param context  witch context the method is called from
     * @param callback to set the user from database
     */
    public void getUser(String currentUserId, final Context context, final Callback callback){

        DocumentReference docRef = db.collection("users").document(currentUserId); // the path to find the current user in database
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) // if it finds the right user in the database
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) // to make sure there is some data
                    {
                        //creates a new user and sets it in the callback
                        returnUser = new User(document.getData());
                        callback.act(returnUser);
                    }
                } else // if the right user is not found, a toast will show the error message
                    {
                    setToast(context, task.getException().toString());
                }
            }
        });
    }


    /**
     * update the password on current user in firebase auth
     * @param password the new password
     * @param context witch context the method is called from
     */
    public void ResetUserPassword(String password, final Context context){
        FirebaseUser user = getCurrentUser();
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) // if it succeeded or not a a toast will show
                        {
                            setToast(context, "Password reset successful");
                        } else {
                            setToast(context, task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * updates the user in the database
     * @param user the user information to set
     * @return as a task to make it possible for set a completelistener in a higher level class/ activity
     */
    public Task updateUser(User user)
    {
        return db.collection("users").document(getCurrentUser().getUid()).set(user);
    }

    /**
     * sets a toast with the given information
     * @param context witch context to set the toast
     * @param text the text to be shown
     */
    private void setToast(Context context, CharSequence text){
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    /**
     * Sets the standard folders for a new user
     */
    public void setStandardFolders(){
        // new hashmap with the value 'exists' to 'true'
        Map<String, Boolean> exists = new HashMap<>();
        exists.put("exists", true);

        //the reference to the collection that contains all categories for current user
        CollectionReference catReference = db.collection("users").document(getCurrentUser().getUid()).collection("categories");

        // It is needed to set the 'exists' on each new generated collection as a property, to initialize it. or it is not allowed or able to connect for the collection later

        catReference.document("favorites").set(exists); //generate the 'favorites' collection
        catReference.document("favorites").collection("fileuids").document("0").set(exists); // generate the 'fileuids' collection in 'favorites'
        catReference.document("unassigned").set(exists); //generate the 'unassigned' collection
        catReference.document("unassigned").collection("fileuids").document("0").set(exists); // generate the 'fileuids' collection in 'unassigned'
    }

}
