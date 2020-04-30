package de.dhbw.handycrab.helper;

import java.util.HashMap;
import java.util.Map;

public class InMemoryCache implements IDataCache {

    Map<String, Object> data = new HashMap<>();

    @Override
    public boolean contains(String id) {
        return data.containsKey(id);
    }

    @Override
    public void store(String id, Object object) {
        data.put(id, object);
    }

    @Override
    public Object retrieve(String id) {
        Object object = data.get(id);
        if (object != null) {
            return object;
        }
        return new Object();
    }

    @Override
    public void delete(String id) {
        data.remove(id);
    }
}
