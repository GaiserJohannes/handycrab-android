package de.dhbw.handycrab.helper;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class DataHolder implements IDataHolder {
    // TODO change WeakReference. Vielleicht räumt der Garbagge Collector zu früh auf. Keine Ahnung -> Testen
    Map<String, WeakReference<Object>> data = new HashMap<>();

    @Override
    public void store(String id, Object object) {
        data.put(id, new WeakReference<>(object));
    }

    @Override
    public Object retrieve(String id) {
        WeakReference<Object> objectWeakReference = data.get(id);
        if (objectWeakReference != null) {
            return objectWeakReference.get();
        }
        return new Object();
    }
}
