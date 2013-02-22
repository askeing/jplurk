package com.google.jplurk.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.jplurk.DateTime;
import com.google.jplurk.PlurkClient;

import org.apache.commons.logging.LogFactory;

import org.apache.commons.logging.Log;

import org.json.JSONException;

import org.json.JSONArray;

import org.json.JSONObject;

public class SimplePlurk {

    private static Log logger = LogFactory.getLog(SimplePlurk.class);
    private PlurkClient client;
    private JSONObject ownProfile;

    public SimplePlurk(PlurkClient client) {
        this.client = client;
    }
    
    public List<Entry> getPlurks(List<Entry> plurks) {
        if (plurks == null || plurks.isEmpty()) {
            return new ArrayList<Entry>();
        }

        Collections.sort(plurks);
        return getPlurks(DateTime.create(plurks.get(0).getPosted()));
    }

    public List<Entry> getPlurks(DateTime offset) {
        List<Entry> plurks = new ArrayList<Entry>();
        if (ownProfile == null) {
            ownProfile = client.getOwnProfile();
        }

        int userId = 0;
        try {
            userId = ownProfile.getJSONObject("user_info").getInt("uid");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        JSONObject obj = client.getPlurks(offset == null ? DateTime.now()
                : offset, 100, userId, false, false);
        try {
            JSONArray plurkArray = obj.getJSONArray("plurks");
            for (int i = 0; i < plurkArray.length(); i++) {
                JSONObject o = plurkArray.getJSONObject(i);
                try {
                    plurks.add(new Entry(o));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }

        return plurks;
    }

}
