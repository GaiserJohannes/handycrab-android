package de.dhbw.handycrab.helper;

public interface IDataCache {
    boolean contains(String id);

    void store(String id, Object object);

    Object retrieve(String id);
}
