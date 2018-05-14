package com.example.test.myreceipts.Entity;

import java.util.Map;

/**
 * Created by thomas on 23-04-2018.
 */

public class User {

    private String UID;
    private String username;
    private String firstname;
    private String lastname;

    public User(Map<String, Object> map)
    {
        username = (String)map.get("username");
        firstname = (String)map.get("firstname");
        lastname = (String)map.get("lastname");
    }


    public User( String UID, String username, String firstname, String lastname) {
        this.UID = UID;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getUID() {
        return UID;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }


    public String getLastname() {
        return lastname;
    }


}
