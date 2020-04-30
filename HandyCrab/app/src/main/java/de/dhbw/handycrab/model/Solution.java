package de.dhbw.handycrab.model;

import org.bson.types.ObjectId;

public class Solution {
    private ObjectId _id;
    private String text;
    private ObjectId userID;
    private int upvotes;
    private int downvotes;
    private Vote vote;

    public Solution() {
    }

    public Solution(ObjectId id, String text, ObjectId userID, int upvotes, int downvotes, Vote vote) {
        _id = id;
        this.text = text;
        this.userID = userID;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.vote = vote;
    }

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

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }
}
