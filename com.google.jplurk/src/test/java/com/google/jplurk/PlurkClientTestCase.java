package com.google.jplurk;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class PlurkClientTestCase extends AbstractJPlurkSessionTestCase {

    protected static List<JSONObject> PLURKS = new LinkedList<JSONObject>();
    protected static int PLURK_API_UID = 0;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // prepare the plurks for testing
        if (PLURKS.isEmpty()) {
            JSONArray plurkData = client.getPlurks(DateTime.now(), 0, false,
                    false, false).getJSONArray("plurks");
            assertNotNull(plurkData);
            for (int i = 0; i < plurkData.length(); i++) {
                PLURKS.add(plurkData.getJSONObject(i));
            }
            assertFalse(PLURKS.isEmpty());
        }
        
        PLURK_API_UID = client.getPublicProfile("plurkapi").getJSONObject("user_info").getInt("uid");
    }
    
    protected void isSuccessTextOk(JSONObject json) throws JSONException {
        assertTrue("ok".equals(json.get("success_text")));
    }

//    private JSONObject pickTestData() throws JSONException {
//        Collections.shuffle(PLURKS);
//        return PLURKS.get(0);
//    }
//
//    public void testMute() throws Exception {
//        JSONObject ret = client.mutePlurks(new String[] { ""
//                + pickTestData().getLong("plurk_id") });
//        assertEquals("ok", ret.get("success_text"));
//
//        ret = client.unmutePlurks(new String[] { ""
//                + pickTestData().getLong("plurk_id") });
//        assertEquals("ok", ret.get("success_text"));
//    }
//    
//    public void testFavortie() throws Exception {
//        JSONObject ret = client.favoritePlurks(new String[] { ""
//                + pickTestData().getLong("plurk_id") });
//        assertEquals("ok", ret.get("success_text"));
//
//        ret = client.unfavoritePlurks(new String[] { ""
//                + pickTestData().getLong("plurk_id") });
//        assertEquals("ok", ret.get("success_text"));
//    }
//
//    public void testSearchUser() throws Exception {
//        JSONObject ret = client.searchUser("plurkbuddy");
//        assertNotNull(ret);
//    }
//
//    public void testGetPublicProfile() throws Exception {
//        JSONObject ret = client.getPublicProfile("qrtt1");
//        assertNotNull(ret);
//        assertEquals("qrtt1", ret.getJSONObject("user_info").getString(
//                "nick_name"));
//    }
//    
//    public void testBecomeFriend() throws Exception {
//        isSuccessTextOk(client.becomeFriend(PLURK_API_UID));
//    }
    

}
