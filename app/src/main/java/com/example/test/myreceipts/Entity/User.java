package com.example.test.myreceipts.Entity;

import java.util.Map;

/**
 * Created by thomas on 23-04-2018.
 */

public class User {

    private String uid;
    private String email;
    private String firstname;
    private String lastname;
    private String password;

    public User(Map<String, Object> map)
    {
        email = (String)map.get("username");
        firstname = (String)map.get("firstname");
        lastname = (String)map.get("lastname");
    }


    public User( String email, String firstname, String lastname) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
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
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
