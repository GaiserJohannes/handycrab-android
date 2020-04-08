package de.dhbw.handycrab.model;

import org.bson.types.ObjectId;

public class User {
    private ObjectId _id;
    private String username = "hallo";
    private String email = "test";

    public ObjectId getId(){
        return _id;
    }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }
}
