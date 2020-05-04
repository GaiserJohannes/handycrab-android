package de.dhbw.handycrab.model;

public interface Votable {
    int getUpvotes();

    void setUpvotes(int upvotes);

    int getDownvotes();

    void setDownvotes(int downvotes);

    Vote getVote();

    void setVote(Vote vote);
}
