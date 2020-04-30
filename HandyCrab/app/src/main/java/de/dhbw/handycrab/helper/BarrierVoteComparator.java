package de.dhbw.handycrab.helper;

import de.dhbw.handycrab.model.Barrier;

import java.util.Comparator;

public class BarrierVoteComparator implements Comparator<Barrier> {
    @Override
    public int compare(Barrier o1, Barrier o2) {
        return (o1.getUpvotes() - o1.getDownvotes()) - (o2.getUpvotes() - o2.getDownvotes());
    }
}
