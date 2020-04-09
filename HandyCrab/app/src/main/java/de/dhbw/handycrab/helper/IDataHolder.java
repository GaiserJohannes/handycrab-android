package de.dhbw.handycrab.helper;

public interface IDataHolder {

    void store(String id, Object object);

    Object retrieve(String id);
}
