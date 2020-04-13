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

    public Barrier() {
    }

    public Barrier(ObjectId id, ObjectId userId, String title, double longitude, double latitude, URL picture, String description, String postcode, List<Solution> solution, int upvotes, int downvotes, Vote vote) {
        _id = id;
        this.userId = userId;
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
        this.picture = picture;
        this.description = description;
        this.postcode = postcode;
        this.solution = solution;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.vote = vote;
    }

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
