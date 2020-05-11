package de.dhbw.handycrab.helper;

import java.util.Comparator;

import de.dhbw.handycrab.model.Barrier;

public class BarrierDistanceComparator implements Comparator<Barrier> {
    @Override
    public int compare(Barrier barrier, Barrier t1) {
        return Float.compare(barrier.getDistance(), t1.getDistance());
    }
}
