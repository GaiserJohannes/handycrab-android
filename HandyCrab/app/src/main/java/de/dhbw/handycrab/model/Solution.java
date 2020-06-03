package de.dhbw.handycrab.model;

import org.bson.types.ObjectId;

public class Solution implements Votable {

    private ObjectId _id;
    private String text;
    private ObjectId userId;
    private int upVotes;
    private int downVotes;
    private Vote vote;

    public Solution() {
    }

    public Solution(ObjectId id, String text, ObjectId userID, int upvotes, int downvotes, Vote vote) {
        _id = id;
        this.text = text;
        this.userId = userID;
        this.upVotes = upvotes;
        this.downVotes = downvotes;
        this.vote = vote;
    }

    public ObjectId getId() {
        return _id;
    }

    public String getText() {
        return text;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }
}
