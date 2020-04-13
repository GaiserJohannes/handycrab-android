package de.dhbw.handycrab.helper;

import java.util.HashMap;
import java.util.Map;

public class InMemoryCache implements IDataCache {
    // TODO change WeakReference. Vielleicht räumt der Garbagge Collector zu früh auf. Keine Ahnung -> Testen
    Map<String, Object> data = new HashMap<>();

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
}
