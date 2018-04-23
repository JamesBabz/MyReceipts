package com.example.test.myreceipts.BLL;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by thomas on 23-04-2018.
 */

public class UserService {
    FirebaseAuth fAuth;

    public UserService() {
        fAuth = FirebaseAuth.getInstance();
    }

    public void signOut(){
        fAuth.signOut();
    }


}
