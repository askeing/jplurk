package com.google.jplurk;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;

import com.google.jplurk.org.apache.commons.lang.math.NumberUtils;

public class Utils {

    public static String base36(long value) {
        return Long.toString(value, 36).toLowerCase();
    }

    public static JSONArray toIds(String... ids) {
        Set<Integer> idSet = new HashSet<Integer>();
        for (String id : ids) {
            idSet.add(NumberUtils.toInt(id, 0));
        }
        idSet.remove(0);
        return new JSONArray(idSet);
    }

}
