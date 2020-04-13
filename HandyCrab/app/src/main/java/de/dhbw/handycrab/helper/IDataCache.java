package de.dhbw.handycrab.helper;

public interface IDataCache {
    void store(String id, Object object);

    Object retrieve(String id);
}
