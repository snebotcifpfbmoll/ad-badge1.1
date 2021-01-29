package com.snebot.fbmoll.data;

import java.util.HashMap;

/**
 * Clase para abstraer los datos de un CSV.
 *
 * @author Serafi Nebot Ginard
 * @since 1.0
 */
public class CSVObject {
    private HashMap<String, String> elements = new HashMap<>();

    public HashMap<String, String> getElements() {
        return elements;
    }

    public CSVObject() {
    }

    public void add(String tag, String value) {
        int iterations = 0;
        String newTag = tag;
        while (elements.containsKey(newTag)) {
            newTag = String.format("%s-%d", tag, iterations++);
        }
        elements.put(newTag, value);
    }

    public void remove(String tag) {
        elements.remove(tag);
    }
}
