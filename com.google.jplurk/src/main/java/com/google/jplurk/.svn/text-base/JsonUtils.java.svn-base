package com.google.jplurk;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtils {

    private static Log logger = LogFactory.getLog(JsonUtils.class);

    public static JSONArray toArray(String json) {
        try {
            return new JSONArray(json);
        } catch (Exception e) {
            logger.warn("json source[" + json
                    + "] is not valid. return empty json-array instead. ");
            return new JSONArray();
        }
    }

    public static JSONObject toObject(String json) {
        try {
            return new JSONObject(json);
        } catch (Exception e) {
            logger.warn("json source[" + json
                    + "] is not valid. return empty json-object instead. ");
            return new JSONObject();
        }
    }
}
