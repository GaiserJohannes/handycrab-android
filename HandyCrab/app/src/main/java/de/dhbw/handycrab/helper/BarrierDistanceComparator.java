package de.dhbw.handycrab.helper;

import de.dhbw.handycrab.model.Barrier;

import java.util.Comparator;

public class BarrierDistanceComparator implements Comparator<Barrier> {
    @Override
    public int compare(Barrier barrier, Barrier t1) {
        return (int)(t1.getDistance() - barrier.getDistance());
    }
}
