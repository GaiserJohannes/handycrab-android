package de.dhbw.handycrab.model;

public interface Votable {
    int getUpVotes();

    void setUpVotes(int upVotes);

    int getDownVotes();

    void setDownVotes(int downVotes);

    Vote getVote();

    void setVote(Vote vote);
}
