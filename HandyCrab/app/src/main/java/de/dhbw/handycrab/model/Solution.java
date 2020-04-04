package de.dhbw.handycrab.model;

import org.bson.types.ObjectId;

public class Solution {
    private ObjectId _id;
    private String text;
    private ObjectId userID;
    private int upvotes;
    private int downvotes;
    private Vote vote;

    public ObjectId getId() {
        return _id;
    }

    public String getText() {
        return text;
    }

    public ObjectId getUserID() {
        return userID;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public Vote getVote() {
        return vote;
    }
}
