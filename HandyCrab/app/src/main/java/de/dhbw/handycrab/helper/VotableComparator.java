package de.dhbw.handycrab.helper;

import de.dhbw.handycrab.model.Votable;

import java.util.Comparator;

public class VotableComparator implements Comparator<Votable> {
    @Override
    public int compare(Votable o1, Votable o2) {
        return (o2.getUpvotes() - o2.getDownvotes()) - (o1.getUpvotes() - o1.getDownvotes());
    }
}
