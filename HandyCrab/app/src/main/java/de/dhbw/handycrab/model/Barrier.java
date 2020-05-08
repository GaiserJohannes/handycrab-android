package de.dhbw.handycrab.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.bson.types.ObjectId;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

public class Barrier implements Votable {

    private ObjectId _id;
    private ObjectId userId;
    private String title;
    private double longitude;
    private double latitude;
    private String picturePath;
    private String description;
    private String postcode;
    private List<Solution> solutions = new ArrayList<>();
    private int upvotes;
    private int downvotes;
    private Vote vote;
    private Bitmap imageBitmap;

    public Barrier() {
    }

    public Barrier(ObjectId id, ObjectId userId, String title, double longitude, double latitude, String picturePath, String description, String postcode, List<Solution> solutions, int upvotes, int downvotes, Vote vote) {
        _id = id;
        this.userId = userId;
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
        this.picturePath = picturePath;
        this.description = description;
        this.postcode = postcode;
        this.solutions = solutions;
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

    public String getPicture() {
        return picturePath;
    }

    public String getDescription() {
        return description;
    }

    public String getPostcode() {
        return postcode;
    }

    public List<Solution> getSolutions() {
        return solutions;
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

    public boolean downloadImage(){
        if(picturePath == null || picturePath.isEmpty()){
            return false;
        }
        if(imageBitmap == null){
            try {
                InputStream in = new URL(picturePath).openStream();
                imageBitmap = BitmapFactory.decodeStream(in);
            } catch (RuntimeException r){}
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void setImageBitmapCallback(BiConsumer<Boolean, Bitmap> function) {
        try {
            Boolean success = CompletableFuture.supplyAsync(() -> downloadImage()).get();
            function.accept(success, imageBitmap);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
