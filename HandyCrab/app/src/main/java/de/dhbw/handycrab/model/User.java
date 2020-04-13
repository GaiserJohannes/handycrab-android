package de.dhbw.handycrab.model;

import org.bson.types.ObjectId;

public class User {
    private ObjectId _id;
    private String username;
    private String email;

    public User() {
    }

    public User(ObjectId id, String username, String email) {
        _id = id;
        this.username = username;
        this.email = email;
    }

    public ObjectId getId() {
        return _id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
