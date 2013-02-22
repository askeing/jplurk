package com.google.jplurk;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.google.jplurk.exception.PlurkException;

/**
 * Test required params only (no network access need.)
 * @author qrtt1
 */
public class PlurkClientRequiredParamsTest extends TestCase {

    static Pattern QUERY_PARAM = Pattern
            .compile("([_a-zA-Z0-9]+)=([_a-zA-Z0-9%]+)");

    PlurkClient client;
    HttpUriRequest method;

    protected void setUp() throws Exception {
        super.setUp();
        final ISettings settings = new ISettings.Simple("__apiKey__",
                Lang.tr_ch);
        client = new PlurkClient(settings) {
            {
                executor = new HttpExecutor(settings) {
                    protected String execute(HttpUriRequest method)
                            throws PlurkException {
                        PlurkClientRequiredParamsTest.this.method = method;
                        return new JSONObject().toString();
                    };
                };
            }
        };
    }

    protected void validateRequired(String... params) throws Exception {

        if ("GET".equals(method.getMethod())) {
            List<String> keys = new ArrayList<String>();
            String uri = method.getURI().toString();
            uri = uri.substring(uri.indexOf("?") + 1);
            Matcher matcher = QUERY_PARAM.matcher(uri);
            while (matcher.find()) {
                keys.add(matcher.group(1));
            }
            for (String p : params) {
                assertTrue(String.format("not found the required param: %s", p),
                        keys.contains(p));
            }            
        }

        if ("POST".equals(method.getMethod())) {
            HttpEntity entity = ((HttpPost) method).getEntity();
            String content = EntityUtils.toString(entity);
            for (String p : params) {
                assertTrue(
                        String.format("not found the required param: %s", p),
                        content.contains(String.format("name=\"%s\"", p)));
            }
        }

    }

    public void testLogin() throws Exception {
        client.login("foo", "bar");
        validateRequired("api_key", "username", "password");
    }

    public void testRegisterStringStringStringGenderString() throws Exception {
        client.register("nickName", "fullName", "password", Gender.Female,
                "1985-05-13");
        validateRequired("api_key", "nick_name", "full_name", "password",
                "gender", "date_of_birth");
    }

    public void testUpdate() throws Exception {
        client.update("currentPassword", "fullName", "newPassword", "email@mail.com",
                "displayName", PrivacyPolicy.OnlyFriends, DateTime.now());
        validateRequired("api_key", "current_password");
    }

    public void testUpdatePictureFile() throws Exception {
        File testfile = File.createTempFile("test", "jplurk");
        testfile.deleteOnExit();
        client.updatePicture(testfile);
        
        validateRequired("api_key", "profile_image");
    }

    public void testUpdatePictureStringInputStream() throws Exception {
        File testfile = File.createTempFile("test", "jplurk");
        testfile.deleteOnExit();

        client.updatePicture("abc", new FileInputStream(testfile));
        validateRequired("api_key", "profile_image");
    }

    public void testGetFriendsByOffset() throws Exception  {
        client.getFriendsByOffset("1234", 4);
        validateRequired("api_key", "user_id");
    }

    public void testGetFansByOffset() throws Exception {
        client.getFansByOffset("1234", 4);
        validateRequired("api_key", "user_id");
    }

    public void testGetFollowingByOffset() throws Exception {
        client.getFollowingByOffset(4);
        validateRequired("api_key");
    }

    public void testBecomeFriend() throws Exception {
        client.becomeFriend(123);
        validateRequired("api_key", "friend_id");
    }

    public void testRemoveAsFriend() throws Exception {
        client.removeAsFriend(123);
        validateRequired("api_key", "friend_id");
    }

    public void testBecomeFan() throws Exception {
        client.becomeFan(123);
        validateRequired("api_key", "fan_id");
    }

    public void testSetFollowing() throws Exception {
        client.setFollowing(123, true);
        validateRequired("api_key", "user_id", "follow");
    }

    public void testGetCompletion() throws Exception {
        client.getCompletion();
        validateRequired("api_key");
    }

    public void testGetPlurk() throws Exception {
        client.getPlurk("123");
        validateRequired("api_key", "plurk_id");
    }

    public void testGetPlurks() throws Exception {
        client.getPlurks(DateTime.now(), 30, true, true, true);
        validateRequired("api_key");
    }

    public void testGetUnreadPlurks() throws Exception {
        client.getUnreadPlurks();
        validateRequired("api_key");
    }

    public void testGetUnreadPlurksDateTime() throws Exception {
        client.getUnreadPlurks(DateTime.now());
        validateRequired("api_key");
    }

    public void testGetUnreadPlurksInt() throws Exception {
        client.getUnreadPlurks(30);
        validateRequired("api_key");
    }

    public void testGetUnreadPlurksDateTimeInt() throws Exception {
        client.getUnreadPlurks(DateTime.now(), 30);
        validateRequired("api_key");
    }

    public void testPlurkAddStringQualifier() throws Exception {
        client.plurkAdd("content", Qualifier.ASKS);
        validateRequired("api_key", "content", "qualifier");
    }

    public void testPlurkAddStringQualifierCommentBy() throws Exception {
        client.plurkAdd("content", Qualifier.ASKS, CommentBy.All);
        validateRequired("api_key", "content", "qualifier");
    }

    public void testPlurkAddStringQualifierLang() throws Exception {
        client.plurkAdd("content", Qualifier.ASKS, Lang.ar);
        validateRequired("api_key", "content", "qualifier");
    }

    public void testPlurkAddStringQualifierStringCommentByLang()
            throws Exception {
        // TODO limit to should be easy
        client.plurkAdd("content", Qualifier.FREESTYLE, "[12,34]",
                CommentBy.Friends, Lang.de);
        validateRequired("api_key", "content", "qualifier");
    }

    public void testPlurkDelete() throws Exception {
        client.plurkDelete("123");
        validateRequired("api_key", "plurk_id");
    }

    public void testPlurkEdit() throws Exception {
        client.plurkEdit("123", "abc");
        validateRequired("api_key", "plurk_id", "content");
    }

    public void testMutePlurks() throws Exception {
        client.mutePlurks("1", "3", "5");
        validateRequired("api_key", "ids");
    }

    public void testUnmutePlurks() throws Exception {
        client.unmutePlurks("1", "3", "5");
        validateRequired("api_key", "ids");
    }

    public void testFavoritePlurks() throws Exception {
        client.favoritePlurks("1", "3", "5");
        validateRequired("api_key", "ids");
    }

    public void testUnfavoritePlurks() throws Exception {
        client.unfavoritePlurks("1", "3", "5");
        validateRequired("api_key", "ids");
    }

    public void testMarkAsRead() throws Exception {
        client.markAsRead("1", "3", "5");
        validateRequired("api_key", "ids");
    }

    public void testUploadPictureFile() throws Exception {
        File testfile = File.createTempFile("test", "jplurk");
        testfile.deleteOnExit();
        client.uploadPicture(testfile);
        
        validateRequired("api_key", "image");
    }

    public void testUploadPictureStringInputStream() throws Exception {
        File testfile = File.createTempFile("test", "jplurk");
        testfile.deleteOnExit();
        client.uploadPicture("abc", new FileInputStream(testfile));

        validateRequired("api_key", "image");
    }

    public void testResponseAdd() throws Exception {
        client.responseAdd("123", "abc", Qualifier.LIKES);
        validateRequired("api_key", "plurk_id", "content", "qualifier");
    }

    public void testResponseGet() throws Exception {
        client.responseGet("123");

        validateRequired("api_key", "plurk_id", "from_response");
    }

    public void testResponseDelete() throws Exception {
        client.responseDelete("123", "456");
        validateRequired("api_key", "response_id", "plurk_id");
    }

    public void testGetActive() throws Exception {
        client.getActive();
        validateRequired("api_key");
    }

    public void testGetHistory() throws Exception {
        client.getHistory();
        validateRequired("api_key");
    }

    public void testAddAsFan() throws Exception {
        client.addAsFan(123);
        validateRequired("api_key", "user_id");
    }

    public void testAddAsFriend() throws Exception {
        client.addAsFriend(123);
        validateRequired("api_key", "user_id");
    }

    public void testAddAllAsFan() throws Exception {
        client.addAllAsFan();
        validateRequired("api_key");
    }

    public void testAddAllAsFriends() throws Exception {
        client.addAllAsFriends();
        validateRequired("api_key");
    }

    public void testDenyFriendship() throws Exception {
        client.denyFriendship(123);
        validateRequired("api_key", "user_id");
    }

    public void testRemoveNotification() throws Exception {
        client.removeNotification(123);
        validateRequired("api_key", "user_id");
    }

    public void testGetOwnProfile() throws Exception {
        client.getOwnProfile();
        validateRequired("api_key");
    }

    public void testGetPublicProfile() throws Exception {
        client.getPublicProfile("123");
        validateRequired("api_key", "user_id");
    }

    public void testGetPollingPlurks() throws Exception {
        client.getPollingPlurks(DateTime.now(), 10);
        validateRequired("api_key", "offset");
    }

    public void testGetPollingUnreadCount() throws Exception {
        client.getPollingUnreadCount();
        validateRequired("api_key");
    }

    public void testSearchPlurkString() throws Exception {
        client.searchPlurk("query");
        validateRequired("api_key", "query");
    }

    public void testSearchPlurkStringInt() throws Exception {
        client.searchPlurk("query", 10);
        validateRequired("api_key", "query");
    }

    public void testSearchUserString() throws Exception {
        client.searchUser("user");
        validateRequired("api_key", "query");
    }

    public void testSearchUserStringInt() throws Exception {
        client.searchUser("user", 10);
        validateRequired("api_key", "query");
    }

    public void testGetBlocks() throws Exception {
        client.getBlocks();
        validateRequired("api_key");
    }

    public void testGetBlocksInt() throws Exception {
        client.getBlocks(10);
        validateRequired("api_key");
    }

    public void testBlock() throws Exception {
        client.block("123");
        validateRequired("api_key", "user_id");
    }

    public void testUnblock() throws Exception {
        client.unblock("123");
        validateRequired("api_key", "user_id");
    }

    public void testGetCliques() throws Exception {
        client.getCliques();
        validateRequired("api_key");
    }

    public void testCreateClique() throws Exception {
        client.createClique("cliqueName");
        validateRequired("api_key", "clique_name");
    }

    public void testGetClique() throws Exception {
        client.getClique("cliqueName");
        validateRequired("api_key", "clique_name");
    }

    public void testRenameClique() throws Exception {
        client.renameClique("old name", "new name");
        validateRequired("api_key", "clique_name", "new_name");
    }

    public void testDeleteClique() throws Exception {
        client.removeFromClique("cliqueName", "123");
        validateRequired("api_key", "clique_name", "user_id");
    }

    public void testAddToClique() throws Exception {
        client.addToClique("cliqueName", "123");
        validateRequired("api_key", "clique_name", "user_id");
    }

    public void testRemoveFromClique() throws Exception {
        client.removeFromClique("cliqueName", "123");
        validateRequired("api_key", "clique_name", "user_id");
    }

    public void testGetFavoritePlurks() throws Exception {
        client.getFavoritePlurks(DateTime.now(), 30, true);
        validateRequired("api_key");
    }

}
