package de.dhbw.handycrab.model;

import org.bson.types.ObjectId;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Barrier {
    private ObjectId _id;
    private ObjectId userId;
    private String title;
    private double longitude;
    private double latitude;
    private URL picture;
    private String description;
    private String postcode;
    private List<Solution> solution = new ArrayList<>();
    private int upvotes;
    private int downvotes;
    private Vote vote;

    public ObjectId getId() {
        return _id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public URL getPicture() {
        return picture;
    }

    public String getDescription() {
        return description;
    }

    public String getPostcode() {
        return postcode;
    }

    public List<Solution> getSolution() {
        return solution;
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
