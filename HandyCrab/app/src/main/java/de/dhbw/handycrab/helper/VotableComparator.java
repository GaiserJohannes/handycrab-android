package de.dhbw.handycrab.helper;

import de.dhbw.handycrab.model.Votable;

import java.util.Comparator;

public class VotableComparator implements Comparator<Votable> {
    @Override
    public int compare(Votable o1, Votable o2) {
        return (o2.getUpVotes() - o2.getDownVotes()) - (o1.getUpVotes() - o1.getDownVotes());
    }
}
