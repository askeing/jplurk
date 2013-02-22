package com.google.jplurk;

import java.io.File;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.jplurk.action.PlurkActionSheet;
import com.google.jplurk.exception.PlurkException;
import com.google.jplurk.net.ThinMultipartEntity;
import com.google.jplurk.org.apache.commons.lang.StringUtils;

/**
 * Main Client for Plurk API.
 * @author Askeing
 */
public class PlurkClient {

    // <editor-fold defaultstate="collapsed" desc="Init PlurkClient">
    private static Log logger = LogFactory.getLog(PlurkClient.class);
    protected HttpExecutor executor;
    private ISettings config;

    /**
     * Load Setting from property file.
     * @param settings
     */
    public PlurkClient(ISettings settings) {
        this.config = settings;
        this.executor = new HttpExecutor(settings);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Users/login">
    /**
     * /API/Users/login<br/>
     * Login an already created user. Login creates a session cookie, 
     * which can be used to access the other methods. 
     * On success it returns the data returned by /API/Profile/getOwnProfile.
     * @param user The user's nick name or email.
     * @param password The user's password.
     * @return The JSONObject of /API/Profile/getOwnProfile
     */
    public JSONObject login(String user, String password) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .login(config.args()
                            .name("username").value(user)
                            .name("password").value(password).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        } 
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Users/register">
    /**
     * /API/Users/register<br/>
     * Register a new user account.
     * @param nickName
     * @param fullName
     * @param password
     * @param gender
     * @param dateOfBirth
     * @return JSONObject with user info {"id": 42, "nick_name": "frodo_b", ...}
     */
    public JSONObject register(String nickName, String fullName,
            String password, Gender gender, String dateOfBirth) throws PlurkException {
        return this.register(nickName, fullName, password, gender, dateOfBirth, "");
    }

    /**
     * /API/Users/register<br/>
     * Register a new user account. (with optional parameters.)
     * @param nickName
     * @param fullName
     * @param password
     * @param gender
     * @param dateOfBirth
     * @param email (optional)
     * @return JSONObject with user info {"id": 42, "nick_name": "frodo_b", ...}
     */
    public JSONObject register(String nickName, String fullName,
            String password, Gender gender, String dateOfBirth, String email) throws PlurkException {
        final int FLAG = Pattern.DOTALL | Pattern.MULTILINE;
        Matcher m;
        // validation of nick_name
        m = Pattern.compile("[\\w]{3,}", FLAG).matcher(nickName);
        m.reset();
        if (!m.find()) {
            return null;
        }
        // validation of full_name
        m = Pattern.compile(".+", FLAG).matcher(fullName);
        m.reset();
        if (!m.find()) {
            return null;
        }
        // validation of password
        m = Pattern.compile(".{3,}", FLAG).matcher(password);
        m.reset();
        if (!m.find()) {
            return null;
        }
        // validation of date_of_birth
        m = Pattern.compile("[0-9]{4}\\-(0[1-9])|(1[0-2])\\-(0[1-9])|(1[0-9])|(2[0-9])|(3[0-1])", FLAG).matcher(dateOfBirth);
        m.reset();
        if (!m.find()) {
            return null;
        }
        try {
            Args paramMap = config.args().name("nick_name").value(nickName)
                    .name("full_name").value(fullName)
                    .name("password").value(password)
                    .name("gender").value(gender.toString())
                    .name("date_of_birth").value(dateOfBirth);
            
            if (email != null && !email.equals((""))) {
                paramMap = paramMap.name("email").value(email);
            }
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance().register(paramMap.getMap());

            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Users/update">
    /**
     * /API/Users/update
     * @param currentPassword User's current password.
     * @param fullName Change full name.
     * @param newPassword Change password.
     * @param email Change email.
     * @param displayName User's display name, can be empty and full unicode. Must be shorter than 15 characters.
     * @param privacyPolicy User's privacy settings. The option can be world (whole world can view the profile), only_friends (only friends can view the profile) or only_me (only the user can view own plurks).
     * @param birth Should be YYYY-MM-DD, example 1985-05-13.
     * @return
     * @throws PlurkException 
     */
    public JSONObject update(String currentPassword, String fullName,
            String newPassword, String email, String displayName,
            PrivacyPolicy privacyPolicy, DateTime birth) throws PlurkException {
        if (StringUtils.isBlank(currentPassword)) {
            logger.warn("current password can not be null.");
            throw new PlurkException("current password can not be null");
        }

        Args args = config.args();
        args.name("current_password").value(currentPassword);
        if (StringUtils.isNotBlank(fullName)) {
            args.name("full_name").value(fullName);
        }
        if (StringUtils.isNotBlank(newPassword)) {
            args.name("new_password").value(newPassword);
        }
        if (StringUtils.isNotBlank(displayName)) {
            args.name("display_name").value(displayName);
        }
        if (StringUtils.isNotBlank(email)) {
            args.name("email").value(email);
        }
        if (privacyPolicy != null) {
            args.name("privacy").value(privacyPolicy.toString());
        }
        if (birth != null) {
            args.name("privacy").value(birth.birthday());
        }

        try {
            return JsonUtils.toObject(executor.execute(PlurkActionSheet.getInstance().update(args.getMap())));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Users/updatePicture">
    /**
     * /API/Users/updatePicture <br />
     * @param file a image file will be uploaded
     * @return
     * @throws PlurkException 
     */
    public JSONObject updatePicture(File file) throws PlurkException {
        if (file == null || !file.exists() || !file.isFile()) {
            logger.warn("not a valid file: " + file);
            throw new PlurkException("not a valid file: " + file);
        }

        HttpPost method = new HttpPost("http://www.plurk.com/API/Users/updatePicture");
        try {
            ThinMultipartEntity entity = new ThinMultipartEntity();
            entity.addPart("api_key", config.getApiKey());
            entity.addPart("profile_image", file);
            method.setEntity(entity);
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }

    /**
     * /API/Users/updatePicture <br />
     * @param fileName a image file will be uploaded
     * @param inputStream a input stream can read image data
     * @return
     * @throws PlurkException 
     */
    public JSONObject updatePicture(String fileName, InputStream inputStream) throws PlurkException {

        HttpPost method = new HttpPost("http://www.plurk.com/API/Users/updatePicture");
        try {
            ThinMultipartEntity entity = new ThinMultipartEntity();
            entity.addPart("api_key", config.getApiKey());
            entity.addPart("profile_image", fileName, inputStream);
            method.setEntity(entity);
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/FriendsFans/getFriendsByOffset">
    /**
     * /API/FriendsFans/getFriendsByOffset <br/>
     * @param userId
     * @param offset
     * @return
     * @throws PlurkException 
     */
    public JSONArray getFriendsByOffset(String userId, int offset) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getFriendsByOffset(config.args()
                            .name("user_id").value(userId)
                            .name("offset").value((offset < 0 ? 0 : offset)).getMap());
            return JsonUtils.toArray(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/FriendsFans/getFansByOffset">
    /**
     * /API/FriendsFans/getFansByOffset <br />
     * @param userId
     * @param offset
     * @return
     * @throws PlurkException 
     */
    public JSONArray getFansByOffset(String userId, int offset) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getFansByOffset(config.args()
                            .name("user_id").value(userId)
                            .name("offset").value((offset < 0 ? 0 : offset)).getMap());
            return JsonUtils.toArray(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/FriendsFans/getFollowingByOffset">
    /**
     * /API/FriendsFans/getFollowingByOffset
     * @param offset The offset, can be 10, 20, 30 etc.
     * @return Returns a list of JSON objects users, e.g. [{"id": 3, "nick_name": "alvin", ...}, ...]
     * @throws PlurkException 
     */
    public JSONArray getFollowingByOffset(int offset) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getFollowingByOffset(config.args()
                            .name("offset").value((offset < 0 ? 0 : offset)).getMap());
            return JsonUtils.toArray(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/FriendsFans/becomeFriend">
    /**
     * /API/FriendsFans/becomeFriend
     * @param friendId The ID of the user you want to befriend.
     * @return {"success_text": "ok"} if a friend request has been made.
     * @throws PlurkException 
     */
    public JSONObject becomeFriend(int friendId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .becomeFriend(config.args()
                            .name("friend_id").value(friendId).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/FriendsFans/removeAsFriend">
    /**
     * /API/FriendsFans/removeAsFriend
     * @param friendId The ID of the user you want to remove
     * @return {"success_text": "ok"} if friend_id has been removed as friend.
     * @throws PlurkException 
     */
    public JSONObject removeAsFriend(int friendId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .removeAsFriend(config.args()
                            .name("friend_id").value(friendId).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/FriendsFans/becomeFan">
    /**
     * /API/FriendsFans/becomeFan
     * @param fanId The ID of the user you want to become fan of
     * @return {"success_text": "ok"} if the current logged in user is a fan of fan_id.
     * @throws PlurkException 
     */
    public JSONObject becomeFan(int fanId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .becomeFan(config.args().name("fan_id").value("" + fanId).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/FriendsFans/setFollowing">
    /**
     * /API/FriendsFans/setFollowing
     * @param userId
     * @param follow true if the user should be followed, and false if the user should be unfollowed.
     * @return
     * @throws PlurkException 
     */
    public JSONObject setFollowing(int userId, boolean follow) throws PlurkException {
        try {
            Boolean isFollow = follow;
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .setFollowing(config.args()
                            .name("user_id").value(userId)
                            .name("follow").value(isFollow.toString()).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/FriendsFans/getCompletion">
    /**
     * /API/FriendsFans/getCompletion <br/>
     * Returns a JSON object of the logged in users friends (nick name and full name).
     * This information can be used to construct auto-completion for private plurking.
     * Notice that a friend list can be big, depending on how many friends a user has, so this list should be lazy-loaded in your application.
     * @return
     * @throws PlurkException 
     */
    public JSONObject getCompletion() throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getCompletion(config.args().getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Timeline/getPlurk">
    /**
     * /API/Users/getPlurk
     * @param plurkId
     * @return JSON object of the plurk and owner
     * @throws PlurkException 
     */
    public JSONObject getPlurk(String plurkId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getPlurk(config.args().name("plurk_id").value(plurkId).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="API/Timeline/getPlurks">
    /**
     * /API/Timeline/getPlurks
     * @param offset (optional) Return plurks older than offset, formatted as 2009-6-20T21:55:34.
     * @param limit (optional) How many plurks should be returned? Default is 20. 
     * @param onlyUser (optional) filter of My Plurks
     * @param onlyResponsed (optional) filter of Private
     * @param onlyPrivate (optional) filter of Responded
     * @return
     * @throws PlurkException 
     */
    public JSONObject getPlurks(DateTime offset, int limit, boolean onlyUser, boolean onlyResponsed, boolean onlyPrivate) throws PlurkException {
        try {
            String _offset = (offset == null ? DateTime.now() : offset).toTimeOffset();
            Args args = config.args().name("offset").value(_offset)
                    .name("limit").value((limit <= 0 ? 20 : limit));
            if (onlyUser) {
                args.name("only_user").value("true");
            }
            if (onlyResponsed) {
                args.name("only_responded").value("true");
            }
            if (onlyPrivate) {
                args.name("only_private").value("true");
            }

            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getPlurks(args.getMap());

            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Timeline/getUnreadPlurks">
    /**
     * /API/Timeline/getUnreadPlurks<br/>
     * Get unread plurks.
     * @return JSONObject of unread plurks and their users
     * @throws PlurkException 
     */
    public JSONObject getUnreadPlurks() throws PlurkException {
        return this.getUnreadPlurks(null);
    }

    /**
     * /API/Timeline/getUnreadPlurks<br/>
     * Get unread plurks older than offset.
     * @param offset (optional), formatted as 2009-6-20T21:55:34
     * @return JSONObject of unread plurks and their users
     * @throws PlurkException 
     */
    public JSONObject getUnreadPlurks(DateTime offset) throws PlurkException {
        return this.getUnreadPlurks(offset, 0);
    }

    /**
     * /API/Timeline/getUnreadPlurks<br/>
     * Get the limited unread plurks.
     * @param limit (optional), Limit the number of plurks
     * @return JSONObject of unread plurks and their users
     * @throws PlurkException 
     */
    public JSONObject getUnreadPlurks(int limit) throws PlurkException {
        return this.getUnreadPlurks(null, limit);
    }

    /**
     * /API/Timeline/getUnreadPlurks<br/>
     * Get the limited unread plurks older than offset.
     * @param offset (optional), formatted as 2009-6-20T21:55:34
     * @param limit (optional), limit the number of plurks. 0 as default, which will get 10 plurks
     * @return JSONObject of unread plurks and their users
     * @throws PlurkException 
     */
    public JSONObject getUnreadPlurks(DateTime offset, int limit) throws PlurkException {
        try {
            Args args = config.args();
            if (offset != null) {
                args = args.name("offset").value(offset.toTimeOffset());
            }
            if (limit > 0) {
                args = args.name("limit").value(limit);
            } else if (limit == 0) {
                args = args.name("limit").value(10);
            }
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance().getUnreadPlurks(args.getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Timeline/plurkAdd">
    /**
     * /API/Timeline/plurkAdd<br/>
     * Add new plurk to timeline.
     * @param content
     * @param qualifier
     * @return JSON object of the new plurk
     * @throws PlurkException 
     */
    public JSONObject plurkAdd(String content, Qualifier qualifier) throws PlurkException {
        return this.plurkAdd(content, qualifier, CommentBy.All);
    }

    /**
     * /API/Timeline/plurkAdd<br/>
     * Add new plurk to timeline.
     * @param content
     * @param qualifier
     * @param commentBy (optional), true or false
     * @return JSON object of the new plurk
     * @throws PlurkException 
     */
    public JSONObject plurkAdd(String content, Qualifier qualifier, CommentBy commentBy) throws PlurkException {
        return this.plurkAdd(content, qualifier, null, commentBy, null);
    }

    /**
     * /API/Timeline/plurkAdd<br/>
     * Add new plurk to timeline.
     * @param content
     * @param qualifier
     * @param lang (optional)
     * @return JSON object of the new plurk
     * @throws PlurkException 
     */
    public JSONObject plurkAdd(String content, Qualifier qualifier, Lang lang) throws PlurkException {
        return this.plurkAdd(content, qualifier, null, CommentBy.All, lang);
    }

    /**
     * /API/Timeline/plurkAdd<br/>
     * Add new plurk to timeline.
     * @param content
     * @param qualifier
     * @param limited_to (optional), JSON Array contains friends ids. If limited_to is [0], then the Plurk is privatley posted to the poster's friends.
     * @param commentBy (optional), true or false
     * @param lang (optional)
     * @return JSON object of the new plurk
     * @throws PlurkException 
     */
    public JSONObject plurkAdd(String content, Qualifier qualifier, String limited_to, CommentBy commentBy, Lang lang) throws PlurkException {
        try {
            Args args = config.args().name("content").value(content)
                    .name("qualifier").value(qualifier.toString())
                    .name("no_comments").value(commentBy.toString())
                    .name("lang").value(lang == null ? config.getLang() : lang.toString());
            if (StringUtils.isNotBlank(limited_to)) {
                args = args.name("limited_to").value(limited_to);
            }
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance().plurkAdd(args.getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Timeline/plurkDelete">
    /**
     * /API/Timeline/plurkDelete<br/>
     * Deletes a plurk.
     * @param plurkId
     * @return {"success_text": "ok"}
     * @throws PlurkException 
     */
    public JSONObject plurkDelete(String plurkId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .plurkDelete(config.args().name("plurk_id").value(plurkId).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Timeline/plurkEdit">
    /**
     * /API/Timeline/plurkEdit<br/>
     * Edits a plurk.
     * @param plurkId
     * @param content
     * @return JSON object of the updated plurk
     * @throws PlurkException 
     */
    public JSONObject plurkEdit(String plurkId, String content) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .plurkEdit(config.args()
                            .name("plurk_id").value(plurkId)
                            .name("content").value(content).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Timeline/mutePlurks">
    /**
     * /API/Timeline/mutePlurks
     * @param ids the plurk ids will be muted
     * @return JSONObject represent the {"success_text": "ok"}
     * @throws PlurkException 
     */
    public JSONObject mutePlurks(String... ids) throws PlurkException {
        try {
            HttpUriRequest method = PlurkActionSheet.getInstance().mutePlurks(
                    config.args().name("ids").value(Utils.toIds(ids).toString()).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Timeline/unmutePlurks">
    /**
     * /API/Timeline/unmutePlurks
     * @param ids the plurk ids will be unmuted.
     * @return JSONObject represent the {"success_text": "ok"}
     * @throws PlurkException 
     */
    public JSONObject unmutePlurks(String... ids) throws PlurkException {
        try {
            HttpUriRequest method = PlurkActionSheet.getInstance().unmutePlurks(
                    config.args().name("ids").value(Utils.toIds(ids).toString()).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Timeline/favoritePlurks">
    /**
     * /API/Timeline/favoritePlurks
     * @param ids The plurk ids, formated as JSON, e.g. [342,23242,2323]
     * @return JSONObject represent the {"success_text": "ok"}
     * @throws PlurkException 
     */
    public JSONObject favoritePlurks(String... ids) throws PlurkException {
        try {
            HttpUriRequest method = PlurkActionSheet.getInstance().favoritePlurks(
                    config.args().name("ids").value(Utils.toIds(ids).toString()).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }        
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Timeline/unfavoritePlurks">
    /**
     * /API/Timeline/unfavoritePlurks
     * @param ids The plurk ids, formated as JSON, e.g. [342,23242,2323]
     * @return JSONObject represent the {"success_text": "ok"}
     * @throws PlurkException 
     */
    public JSONObject unfavoritePlurks(String... ids) throws PlurkException {
        try {
            HttpUriRequest method = PlurkActionSheet.getInstance().unfavoritePlurks(
                    config.args().name("ids").value(Utils.toIds(ids).toString()).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }        
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Timeline/markAsRead">
    /**
     * /API/Timeline/markAsRead
     * @param ids the plurk ids will mark as read.
     * @return JSONObject represent the {"success_text": "ok"}
     * @throws PlurkException 
     */
    public JSONObject markAsRead(String... ids) throws PlurkException {
        try {
            HttpUriRequest method = PlurkActionSheet.getInstance().markAsRead(
                    config.args().name("ids").value(Utils.toIds(ids).toString()).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }        
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Timeline/uploadPicture">
    /**
     * /API/Timeline/uploadPicture
     * @param file a image file will be uploaded
     * @return json with thumbnail url. for example <pre>{"thumbnail":"http://images.plurk.com/tn_3146394_fb04befc28fbca59318f16d83d5c78cc.gif","full":"http://images.plurk.com/3146394_fb04befc28fbca59318f16d83d5c78cc.jpg"}</pre>
     * @throws PlurkException 
     */
    public JSONObject uploadPicture(File file) throws PlurkException {
        if (file == null || !file.exists() || !file.isFile()) {
            logger.warn("not a valid file: " + file);
            return null;
        }

        HttpPost method = new HttpPost("http://www.plurk.com/API/Timeline/uploadPicture");
        try {
            ThinMultipartEntity entity = new ThinMultipartEntity();
            entity.addPart("api_key", config.getApiKey());
            entity.addPart("image", file);
            method.setEntity(entity);

            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Timeline/uploadPicture">
    /**
     * /API/Timeline/uploadPicture
     * @param fileName a image file will be uploaded
     * @param inputStream
     * @return json with thumbnail url. for example <pre>{"thumbnail":"http://images.plurk.com/tn_3146394_fb04befc28fbca59318f16d83d5c78cc.gif","full":"http://images.plurk.com/3146394_fb04befc28fbca59318f16d83d5c78cc.jpg"}</pre>
     * @throws PlurkException 
     */
    public JSONObject uploadPicture(String fileName, InputStream inputStream) throws PlurkException {

        HttpPost method = new HttpPost("http://www.plurk.com/API/Timeline/uploadPicture");
        try {
            ThinMultipartEntity entity = new ThinMultipartEntity();
            entity.addPart("api_key", config.getApiKey());
            entity.addPart("image", fileName, inputStream);
            method.setEntity(entity);

            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Responses/responseAdd">
    /**
     * /API/Responses/responseAdd
     * @param plurkId which plurk will be response
     * @param content the responsed content
     * @param qualifier
     * @return JSON object
     * @throws PlurkException 
     */
    public JSONObject responseAdd(String plurkId, String content, Qualifier qualifier) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .responseAdd(config.args()
                            .name("plurk_id").value(plurkId)
                            .name("content").value(content)
                            .name("qualifier").value(qualifier.toString()).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Responses/get">
    /**
     * /API/Responses/get
     * @param plurkId
     * @return JSON object
     * @throws PlurkException 
     */
    public JSONObject responseGet(String plurkId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .responseGet(config.args()
                            .name("plurk_id").value(plurkId)
                            .name("from_response").value("0").getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Responses/responseDelete">
    /**
     * /API/Responses/responseDelete
     * @param plurkId the plurk id contains the response
     * @param responseId the id of the response will be deleted.
     * @return {"success_text": "ok"} when deletion is success, otherwise null.
     * @throws PlurkException 
     */
    public JSONObject responseDelete(String plurkId, String responseId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .responseDelete(config.args()
                            .name("plurk_id").value(plurkId)
                            .name("response_id").value(responseId).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Alerts/getActive">
    /**
     * /API/Alerts/getActive <br />
     * Return a JSON list of current active alerts.
     * @return
     * @throws PlurkException 
     */
    public JSONArray getActive() throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance().getActive(config.args().getMap());
            return JsonUtils.toArray(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Alerts/getHistory">
    /**
     * /API/Alerts/getHistory <br />
     * Return a JSON list of past 30 alerts.
     * @return
     * @throws PlurkException 
     */
    public JSONArray getHistory() throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance().getHistory(config.args().getMap());
            return JsonUtils.toArray(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Alerts/addAsFan">
    /**
     * /API/Alerts/addAsFan <br />
     * Accept user_id as fan.
     * @param userId The user_id that has asked for friendship.
     * @return
     * @throws PlurkException 
     */
    public JSONObject addAsFan(int userId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .addAsFan(config.args().name("user_id").value(Integer.toString(userId)).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Alerts/addAsFriend">
    /**
     * /API/Alerts/addAsFriend <br />
     * Accept user_id as friend.
     * @param userId The user_id that has asked for friendship.
     * @return
     * @throws PlurkException 
     */
    public JSONObject addAsFriend(int userId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .addAsFriend(config.args().name("user_id").value(Integer.toString(userId)).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Alerts/addAllAsFan">
    /**
     * /API/Alerts/addAllAsFan <br />
     * Accept all friendship requests as fans
     * @return {"success_text": "ok"} when request success, otherwise null
     * @throws PlurkException 
     */
    public JSONObject addAllAsFan() throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance().addAllAsFan(config.args().getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Alerts/addAllAsFriends">
    /**
     * /API/Alerts/addAllAsFriends <br />
     * Accept all friendship requests as friends.
     * @return {"success_text": "ok"} when request success, otherwise null
     * @throws PlurkException 
     */
    public JSONObject addAllAsFriends() throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance().addAllAsFriends(config.args().getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Alerts/denyFriendship">
    /**
     * /API/Alerts/denyFriendship <br />
     * Deny friendship to user_id.
     * @param userId The user_id that has asked for friendship.
     * @return
     * @throws PlurkException 
     */
    public JSONObject denyFriendship(int userId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .denyFriendship(config.args().name("user_id").value(Integer.toString(userId)).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Alerts/removeNotification">
    /**
     * /API/Alerts/removeNotification <br />
     * Remove notification to user with id user_id.
     * @param userId The user_id that the current user has requested friendship for.
     * @return
     * @throws PlurkException 
     */
    public JSONObject removeNotification(int userId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .removeNotification(config.args().name("user_id").value(Integer.toString(userId)).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Profile/getOwnProfile">
    /**
     * /API/Profile/getOwnProfile
     * @return
     * @throws PlurkException 
     */
    public JSONObject getOwnProfile() throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getOwnProfile(config.args().getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Profile/getPublicProfile">
    /**
     * /API/Profile/getPublicProfile
     * @param userId
     * @return
     * @throws PlurkException 
     */
    public JSONObject getPublicProfile(String userId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getPublicProfile(config.args().name("user_id").value(userId).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Realtime/getUserChannel">
    /**
     * Get instant notifications when there are new plurks and responses on a
     * user's timeline. This is much more efficient and faster than polling so
     * please use it!
     *
     * This API works like this:
     *
     * <li /> A request is sent to /API/Realtime/getUserChannel and in it you get
     * an unique channel to currnetly logged in user's timeline
     * <li />You do requests to this unqiue channel in order to get notifications
     *
     * @return
     * @throws PlurkException 
     */
    public PlurkNotifier getUserChannel() throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getUserChannel(config.args().getMap());
            return new PlurkNotifier(executor.getHttpClient(), JsonUtils.toObject(executor.execute(method)));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Polling/getPlurks">
    /**
     * /API/Polling/getPlurks <br/>
     * You should use this call to find out if there any new plurks posted to the user's timeline.
     * It's much more efficient than doing it with /API/Timeline/getPlurks, so please use it :)
     * @param offset
     * @param limit
     * @return
     * @throws PlurkException 
     */
    public JSONObject getPollingPlurks(DateTime offset, int limit) throws PlurkException {
        try {
            String _offset = (offset == null ? DateTime.now().toTimeOffset() : offset.toTimeOffset());
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getPollingPlurks(config.args()
                            .name("offset").value(_offset)
                            .name("limit").value((limit <= 0 ? 20 : limit)).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Polling/getUnreadCount">
    /**
     * /API/Polling/getUnreadCount <br/>
     * Use this call to find out if there are unread plurks on a user's timeline.
     * @return
     * @throws PlurkException 
     */
    public JSONObject getPollingUnreadCount() throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getPollingUnreadCount(config.args().getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/PlurkSearch/search">
    /**
     * /API/PlurkSearch/search <br/>
     * Returns the latest 20 plurks on a search term.
     * @param query
     * @return
     * @throws PlurkException 
     */
    public JSONObject searchPlurk(String query) throws PlurkException {
        return searchPlurk(query, 0);
    }

    /**
     * /API/PlurkSearch/search <br/>
     * Returns the latest 20 plurks on a search term.
     * @param query
     * @param offset A plurk_id of the oldest Plurk in the last search result.
     * @return
     * @throws PlurkException 
     */
    public JSONObject searchPlurk(String query, int offset) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .searchPlurk(config.args()
                            .name("query").value(query)
                            .name("offset").value((offset < 0 ? 0 : offset)).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/UserSearch/search">
    /**
     * /API/UserSearch/search <br/>
     * Returns 10 users that match query, users are sorted by karma. <br/>
     * (Hint: This API seems like do nothing...)
     * @param query
     * @return
     * @throws PlurkException 
     */
    public JSONObject searchUser(String query) throws PlurkException {
        return searchUser(query, 10);
    }

    /**
     * /API/UserSearch/search <br/>
     * Returns 10 users that match query, users are sorted by karma.
     * @param query
     * @param offset Page offset, like 10, 20, 30 etc. .
     * @return
     * @throws PlurkException 
     */
    public JSONObject searchUser(String query, int offset) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .searchUser(config.args()
                            .name("query").value(query)
                            .name("offset").value((offset < 0 ? 0 : offset)).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Emoticons/get">
    /**
     * /API/Emoticons/get <br/>
     * This call returns a JSON object that looks like: <br/>
     * {"karma": {"0": [[":-))", "http:\/\/statics.plurk.com\/XXX.gif"], ...], ...},
     * "recuited": {"10": [["(bigeyes)", "http:\/\/statics.plurk.com\/XXX.gif"], ...], ...} }  <br/>
     * emoticons["karma"][25] denotes that the user has to have karma over 25 to use these emoticons.  <br/>
     * emoticons["recuited"][10] means that the user has to have user.recuited >= 10 to use these emoticons. <br/>
     * It's important to check for these things on the client as well, since the emoticon levels are checked in the models.
     * @return
     * @throws PlurkException 
     */
    public JSONObject getEmoticons() throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getEmoticons(config.args().getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Blocks/get">
    /**
     * /API/Blocks/get <br/>
     * default offset 0
     * @return
     * @throws PlurkException 
     */
    public JSONObject getBlocks() throws PlurkException {
        return getBlocks(0);
    }

    /**
     * /API/Blocks/get
     * @param offset
     * @return
     * @throws PlurkException 
     */
    public JSONObject getBlocks(int offset) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getBlocks(config.args()
                            .name("offset").value((offset < 0 ? 0 : offset)).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Blocks/block">
    /**
     * /API/Blocks/block
     * @param userId
     * @return
     * @throws PlurkException 
     */
    public JSONObject block(String userId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .block(config.args().name("user_id").value(userId).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Blocks/unblock">
    /**
     * /API/Blocks/unblock
     * @param userId
     * @return
     * @throws PlurkException 
     */
    public JSONObject unblock(String userId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .unblock(config.args().name("user_id").value(userId).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Cliques/get_cliques">
    /**
     * /API/Cliques/get_cliques
     * @return a JSON list of users current cliques
     * @throws PlurkException 
     */
    public JSONArray getCliques() throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getCliques(config.args().getMap());
            return JsonUtils.toArray(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Cliques/create_clique">
    /**
     * /API/Cliques/create_clique <br/>
     * Create the new clique.
     * @param cliqueName The name of the new clique
     * @return
     * @throws PlurkException 
     */
    public JSONObject createClique(String cliqueName) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .createClique(config.args().name("clique_name").value(cliqueName).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Cliques/get_clique">
    /**
     * /API/Cliques/get_clique <br/>
     * Get the user in the clique.
     * @param cliqueName
     * @return Returns the users in the clique, e.g. [{"display_name": "amix3", "gender": 0, "nick_name": "amix", "has_profile_image": 1, "id": 1, "avatar": null}]
     * @throws PlurkException 
     */
    public JSONArray getClique(String cliqueName) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .getClique(config.args().name("clique_name").value(cliqueName).getMap());
            return JsonUtils.toArray(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Cliques/renameClique">
    /**
     * /API/Cliques/renameClique <br/>
     * Rename the clique.
     * @param cliqueName
     * @param newName
     * @return
     * @throws PlurkException 
     */
    public JSONObject renameClique(String cliqueName, String newName) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .renameClique(config.args()
                            .name("clique_name").value(cliqueName)
                            .name("new_name").value(newName).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Cliques/delete_clique">
    /**
     * /API/Cliques/delete_clique <br/>
     * Delete the clique.
     * @param cliqueName
     * @return
     * @throws PlurkException 
     */
    public JSONObject deleteClique(String cliqueName) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .deleteClique(config.args().name("clique_name").value(cliqueName).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Cliques/add">
    /**
     * /API/Cliques/add <br/>
     * Add the user into the clique.
     * @param cliqueName
     * @param userId
     * @return
     * @throws PlurkException 
     */
    public JSONObject addToClique(String cliqueName, String userId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .addToClique(config.args().name("clique_name").value(cliqueName).name("user_id").value(userId).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/API/Cliques/remove">
    /**
     * /API/Cliques/remove <br/>
     * Remove user from the clique
     * @param cliqueName
     * @param userId
     * @return
     * @throws PlurkException 
     */
    public JSONObject removeFromClique(String cliqueName, String userId) throws PlurkException {
        try {
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance()
                    .removeFromClique(config.args()
                            .name("clique_name").value(cliqueName)
                            .name("user_id").value(userId).getMap());
            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
    // </editor-fold>


    /**
     * [Non-Offical API]
     * /API/Timeline/getPlurks
     * Add new function: filter of Liked.
     * Function getFavoritePlurks() also use getPlurks API, because onlyFavorite can NOT using with onlyUser, onlyResponsed, and onlyPrivate, so this function is split out.
     * @param offset (optional) Return plurks older than offset, formatted as 2009-6-20T21:55:34.
     * @param limit (optional) How many plurks should be returned? Default is 20.
     * @param onlyFavorite (optional) filter of Liked.
     * @return
     * @throws PlurkException 
     */
    public JSONObject getFavoritePlurks(DateTime offset, int limit, boolean onlyFavorite) throws PlurkException {
        try {
            String _offset = (offset == null ? DateTime.now() : offset).toTimeOffset();
            Args args = config.args()
                    .name("offset").value(_offset)
                    .name("limit").value((limit <= 0 ? 20 : limit));

            if (onlyFavorite) {
                args.name("only_favorite").value("true");
            }
            // Function getFavoritePlurks() also use getPlurks API,
            // because onlyFavorite can NOT using with onlyUser, onlyResponsed, and onlyPrivate, so this function is split out.
            HttpGet method = (HttpGet) PlurkActionSheet.getInstance().getPlurks(
                    args.getMap());

            return JsonUtils.toObject(executor.execute(method));
        } catch (Exception e) {
            throw PlurkException.create(e);
        }
    }
  
}
