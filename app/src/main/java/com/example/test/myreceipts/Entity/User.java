package com.example.test.myreceipts.Entity;

/**
 * Created by thomas on 23-04-2018.
 */

public class User {

    private String uid;
    private String email;
    private String Firstname;
    private String Lastname;
    private String password;



    public User(String uid, String email, String firstname, String lastname, String password) {
        this.uid = uid;

        this.email = email;
        Firstname = firstname;
        Lastname = lastname;
        this.password = password;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
