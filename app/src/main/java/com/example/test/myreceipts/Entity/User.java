package com.example.test.myreceipts.Entity;

import java.util.Map;

/**
 * Created by thomas on 23-04-2018.
 */

public class User {

    private String username;
    private String firstname;
    private String lastname;

    public User(Map<String, Object> map)
    {
        username = (String)map.get("username");
        firstname = (String)map.get("firstname");
        lastname = (String)map.get("lastname");
    }


    public User( String username, String firstname, String lastname) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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


}
