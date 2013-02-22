package com.google.jplurk.action;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;

import com.google.jplurk.action.Headers.Header;
import com.google.jplurk.exception.PlurkException;
import com.google.jplurk.validator.EmailValidator;
import com.google.jplurk.validator.IDListValidator;
import com.google.jplurk.validator.IValidator;
import com.google.jplurk.validator.NicknameValidator;
import com.google.jplurk.validator.NonNegativeIntegerValidator;
import com.google.jplurk.validator.PositiveIntegerValidator;
import com.google.jplurk.validator.QualifierValidator;
import com.google.jplurk.validator.TimeOffsetValidator;
import com.google.jplurk.validator.TrueFalseLiteralValidator;
import com.google.jplurk.validator.Validation;
import com.google.jplurk.validator.Validation.Validator;

public final class PlurkActionSheet {

    private static Log logger = LogFactory.getLog(PlurkActionSheet.class);
    private final static PlurkActionSheet self = new PlurkActionSheet();

    private PlurkActionSheet() {
    }

    public static PlurkActionSheet getInstance() {
        return self;
    }

    private static String getSecuredApiUri(String uri) {
        return "https://www.plurk.com/API" + uri;
    }
    
    private static String getApiUri(String uri) {
        return "http://www.plurk.com/API" + uri;
    }

    // <editor-fold defaultstate="collapsed" desc="Users">
    @Meta(uri = "/Users/register",
    require = {"api_key", "nick_name", "full_name", "password", "gender", "date_of_birth"}, isHttps = true)
    @Validation({@Validator(field = "email", validator = EmailValidator.class)})
    public HttpUriRequest register(Map<String, String> params)
            throws PlurkException {
        return prepare("register", params);
    }

	@Meta(uri = "/Users/login", require = { "api_key", "username", "password" }, isHttps = true)
    public HttpUriRequest login(Map<String, String> params)
            throws PlurkException {
        return prepare("login", params);
    }

    @Meta(uri = "/Users/updatePicture", require = {"api_key", "profile_image"}, type = Type.POST)
    @Headers(headers = {@Header(key = "Content-Type", value = "multipart/form-data")})
    public HttpUriRequest updatePicture(Map<String, String> params)
            throws PlurkException {
        return prepare("updatePicture", params);
    }

    @Meta(uri = "/Users/update", require = {"api_key", "current_password"}, isHttps = true)
    @Validation({@Validator(field = "email", validator = EmailValidator.class)})
    public HttpUriRequest update(Map<String, String> params)
            throws PlurkException {
        return prepare("update", params);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Polling">
    @Meta(uri = "/Polling/getPlurks", require = {"api_key", "offset"})
    @Validation({
        @Validator(field = "offset", validator = TimeOffsetValidator.class),
        @Validator(field = "limit", validator = NonNegativeIntegerValidator.class)
    })
    public HttpUriRequest getPollingPlurks(Map<String, String> params) throws PlurkException {
        return prepare("getPollingPlurks", params);
    }

    @Meta(uri = "/Polling/getUnreadCount", require = {"api_key"})
    public HttpUriRequest getPollingUnreadCount(Map<String, String> params) throws PlurkException {
        return prepare("getPollingUnreadCount", params);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Timeline">
    @Meta(uri = "/Timeline/getPlurk", require = {"api_key", "plurk_id"})
    @Validation({
        @Validator(field = "plurk_id", validator = PositiveIntegerValidator.class)
    })
    public HttpUriRequest getPlurk(Map<String, String> params) throws PlurkException {
        return prepare("getPlurk", params);
    }

    @Meta(uri = "/Timeline/getPlurks", require = {"api_key"})
    @Validation({
        @Validator(field = "offset", validator = TimeOffsetValidator.class),
        @Validator(field = "limit", validator = PositiveIntegerValidator.class)
    })
    public HttpUriRequest getPlurks(Map<String, String> params) throws PlurkException {
        return prepare("getPlurks", params);
    }

    @Meta(uri = "/Timeline/getUnreadPlurks", require = {"api_key"})
    @Validation({
        @Validator(field = "offset", validator = TimeOffsetValidator.class),
        @Validator(field = "limit", validator = PositiveIntegerValidator.class)
    })
    public HttpUriRequest getUnreadPlurks(Map<String, String> params) throws PlurkException {
        return prepare("getUnreadPlurks", params);
    }

    @Meta(uri = "/Timeline/mutePlurks", require = {"api_key", "ids"})
    @Validation({@Validator(field = "ids", validator = IDListValidator.class)})
    public HttpUriRequest mutePlurks(Map<String, String> params) throws PlurkException {
        return prepare("mutePlurks", params);
    }

    @Meta(uri = "/Timeline/unmutePlurks", require = {"api_key", "ids"})
    @Validation({@Validator(field = "ids", validator = IDListValidator.class)})
    public HttpUriRequest unmutePlurks(Map<String, String> params) throws PlurkException {
        return prepare("unmutePlurks", params);
    }

    @Meta(uri = "/Timeline/favoritePlurks", require = {"api_key", "ids"})
    public HttpUriRequest favoritePlurks(Map<String, String> params) throws PlurkException {
        return prepare("favoritePlurks", params);
    }

    @Meta(uri = "/Timeline/unfavoritePlurks", require = {"api_key", "ids"})
    public HttpUriRequest unfavoritePlurks(Map<String, String> params) throws PlurkException {
        return prepare("unfavoritePlurks", params);
    }

    @Meta(uri = "/Timeline/markAsRead", require = {"api_key", "ids"})
    @Validation({@Validator(field = "ids", validator = IDListValidator.class)})
    public HttpUriRequest markAsRead(Map<String, String> params) throws PlurkException {
        return prepare("markAsRead", params);
    }

    @Meta(uri = "/Timeline/plurkAdd", require = {"api_key", "content", "qualifier"})
    @Validation(value = {
        @Validator(field = "limited_to", validator = IDListValidator.class),
        @Validator(field = "qualifier", validator = QualifierValidator.class)})
    public HttpUriRequest plurkAdd(Map<String, String> params) throws PlurkException {
        return prepare("plurkAdd", params);
    }

    @Meta(uri = "/Timeline/uploadPicture", require = {"api_key", "image"}, type = Type.POST)
    @Headers(headers = {@Header(key = "Content-Type", value = "multipart/form-data")})
    public HttpUriRequest uploadPicture(Map<String, String> params)
            throws PlurkException {
        return prepare("uploadPicture", params);
    }

    @Meta(uri = "/Timeline/plurkDelete", require = {"api_key", "plurk_id"})
    @Validation({
        @Validator(field = "plurk_id", validator = PositiveIntegerValidator.class)
    })
    public HttpUriRequest plurkDelete(Map<String, String> params) throws PlurkException {
        return prepare("plurkDelete", params);
    }

    @Meta(uri = "/Timeline/plurkEdit", require = {"api_key", "plurk_id", "content"})
    @Validation({
        @Validator(field = "plurk_id", validator = PositiveIntegerValidator.class)
    })
    public HttpUriRequest plurkEdit(Map<String, String> params) throws PlurkException {
        return prepare("plurkEdit", params);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Responses">
    @Meta(uri = "/Responses/get", require = {"api_key", "plurk_id", "from_response"})
    @Validation({
        @Validator(field = "plurk_id", validator = PositiveIntegerValidator.class)
    })
    public HttpUriRequest responseGet(Map<String, String> params) throws PlurkException {
        return prepare("responseGet", params);
    }

    @Meta(uri = "/Responses/responseAdd", require = {"api_key", "content", "qualifier", "plurk_id"})
    @Validation({
        @Validator(field = "qualifier", validator = QualifierValidator.class),
        @Validator(field = "plurk_id", validator = PositiveIntegerValidator.class)
    })
    public HttpUriRequest responseAdd(Map<String, String> params) throws PlurkException {
        return prepare("responseAdd", params);
    }

    @Meta(uri = "/Responses/responseDelete", require = {"api_key", "response_id", "plurk_id"})
    @Validation({
        @Validator(field = "response_id", validator = PositiveIntegerValidator.class),
        @Validator(field = "plurk_id", validator = PositiveIntegerValidator.class)
    })
    public HttpUriRequest responseDelete(Map<String, String> params) throws PlurkException {
        return prepare("responseDelete", params);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Profile">
    @Meta(uri = "/Profile/getOwnProfile", require = {"api_key"})
    public HttpUriRequest getOwnProfile(Map<String, String> params)
            throws PlurkException {
        return prepare("getOwnProfile", params);
    }

    @Meta(uri = "/Profile/getPublicProfile", require = {"api_key", "user_id"})
    @Validation({@Validator(field = "user_id", validator = NicknameValidator.class)})
    public HttpUriRequest getPublicProfile(Map<String, String> params)
            throws PlurkException {
        return prepare("getPublicProfile", params);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Realtime">
    @Meta(uri = "/Realtime/getUserChannel", require = {"api_key"})
    public HttpUriRequest getUserChannel(Map<String, String> params)
            throws PlurkException {
        return prepare("getUserChannel", params);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FriendsFans">
    @Meta(uri = "/FriendsFans/getFriendsByOffset", require = {"api_key", "user_id"})
    @Validation({
        @Validator(field = "user_id", validator = NonNegativeIntegerValidator.class),
        @Validator(field = "offset", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest getFriendsByOffset(Map<String, String> params)
            throws PlurkException {
        return prepare("getFriendsByOffset", params);
    }

    @Meta(uri = "/FriendsFans/getFansByOffset", require = {"api_key", "user_id"})
    @Validation({
        @Validator(field = "offset", validator = NonNegativeIntegerValidator.class),
        @Validator(field = "user_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest getFansByOffset(Map<String, String> params)
            throws PlurkException {
        return prepare("getFansByOffset", params);
    }

    @Meta(uri = "/FriendsFans/getFollowingByOffset", require = {"api_key"})
    @Validation({@Validator(field = "offset", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest getFollowingByOffset(Map<String, String> params)
            throws PlurkException {
        return prepare("getFollowingByOffset", params);
    }

    @Meta(uri = "/FriendsFans/becomeFriend", require = {"api_key", "friend_id"})
    @Validation({@Validator(field = "friend_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest becomeFriend(Map<String, String> params)
            throws PlurkException {
        return prepare("becomeFriend", params);
    }

    @Meta(uri = "/FriendsFans/removeAsFriend", require = {"api_key", "friend_id"})
    @Validation({@Validator(field = "friend_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest removeAsFriend(Map<String, String> params)
            throws PlurkException {
        return prepare("removeAsFriend", params);
    }

    @Meta(uri = "/FriendsFans/becomeFan", require = {"api_key", "fan_id"})
    @Validation({@Validator(field = "fan_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest becomeFan(Map<String, String> params)
            throws PlurkException {
        return prepare("becomeFan", params);
    }

    @Meta(uri = "/FriendsFans/setFollowing", require = {"api_key", "user_id", "follow"})
    @Validation({
        @Validator(field = "follow", validator = TrueFalseLiteralValidator.class),
        @Validator(field = "user_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest setFollowing(Map<String, String> params)
            throws PlurkException {
        return prepare("setFollowing", params);
    }

    @Meta(uri = "/FriendsFans/getCompletion", require = {"api_key"})
    public HttpUriRequest getCompletion(Map<String, String> params)
            throws PlurkException {
        return prepare("getCompletion", params);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Alerts">
    @Meta(uri = "/Alerts/getActive", require = {"api_key"})
    public HttpUriRequest getActive(Map<String, String> params) throws PlurkException {
        return prepare("getActive", params);
    }

    @Meta(uri = "/Alerts/getHistory", require = {"api_key"})
    public HttpUriRequest getHistory(Map<String, String> params) throws PlurkException {
        return prepare("getHistory", params);
    }

    @Meta(uri = "/Alerts/addAsFan", require = {"api_key", "user_id"})
    @Validation({@Validator(field = "user_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest addAsFan(Map<String, String> params) throws PlurkException {
        return prepare("addAsFan", params);
    }

    @Meta(uri = "/Alerts/addAllAsFan", require = {"api_key"})
    public HttpUriRequest addAllAsFan(Map<String, String> params)
            throws PlurkException {
        return prepare("addAllAsFan", params);
    }

    @Meta(uri = "/Alerts/addAllAsFriends", require = {"api_key"})
    public HttpUriRequest addAllAsFriends(Map<String, String> params)
            throws PlurkException {
        return prepare("addAllAsFriends", params);
    }

    @Meta(uri = "/Alerts/addAsFriend", require = {"api_key", "user_id"})
    @Validation({@Validator(field = "user_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest addAsFriend(Map<String, String> params) throws PlurkException {
        return prepare("addAsFriend", params);
    }

    @Meta(uri = "/Alerts/denyFriendship", require = {"api_key", "user_id"})
    @Validation({@Validator(field = "user_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest denyFriendship(Map<String, String> params) throws PlurkException {
        return prepare("denyFriendship", params);
    }

    @Meta(uri = "/Alerts/removeNotification", require = {"api_key", "user_id"})
    @Validation({@Validator(field = "user_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest removeNotification(Map<String, String> params) throws PlurkException {
        return prepare("removeNotification", params);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Search">
    @Meta(uri = "/PlurkSearch/search", require = {"api_key", "query"})
    @Validation({@Validator(field = "offset", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest searchPlurk(Map<String, String> params) throws PlurkException {
        return prepare("searchPlurk", params);
    }

    @Meta(uri = "/UserSearch/search", require = {"api_key", "query"})
    @Validation({@Validator(field = "offset", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest searchUser(Map<String, String> params) throws PlurkException {
        return prepare("searchUser", params);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Emoticons">
    @Meta(uri = "/Emoticons/get", require = {})
    public HttpUriRequest getEmoticons(Map<String, String> params) throws PlurkException {
        return prepare("getEmoticons", params);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Blocks">
    @Meta(uri = "/Blocks/get", require = {"api_key"})
    @Validation({@Validator(field = "offset", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest getBlocks(Map<String, String> params) throws PlurkException {
        return prepare("getBlocks", params);
    }

    @Meta(uri = "/Blocks/block", require = {"api_key", "user_id"})
    @Validation({@Validator(field = "user_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest block(Map<String, String> params) throws PlurkException {
        return prepare("block", params);
    }

    @Meta(uri = "/Blocks/unblock", require = {"api_key", "user_id"})
    @Validation({@Validator(field = "user_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest unblock(Map<String, String> params) throws PlurkException {
        return prepare("unblock", params);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Cliques">
    @Meta(uri = "/Cliques/get_cliques", require = {"api_key"})
    public HttpUriRequest getCliques(Map<String, String> params) throws PlurkException {
        return prepare("getCliques", params);
    }

    @Meta(uri = "/Cliques/create_clique", require = {"api_key", "clique_name"})
    public HttpUriRequest createClique(Map<String, String> params) throws PlurkException {
        return prepare("createClique", params);
    }

    @Meta(uri = "/Cliques/get_clique", require = {"api_key", "clique_name"})
    public HttpUriRequest getClique(Map<String, String> params) throws PlurkException {
        return prepare("getClique", params);
    }

    @Meta(uri = "/Cliques/rename_clique", require = {"api_key", "clique_name", "new_name"})
    public HttpUriRequest renameClique(Map<String, String> params) throws PlurkException {
        return prepare("renameClique", params);
    }

    @Meta(uri = "/Cliques/delete_clique", require = {"api_key", "clique_name"})
    public HttpUriRequest deleteClique(Map<String, String> params) throws PlurkException {
        return prepare("deleteClique", params);
    }

    @Meta(uri = "/Cliques/add", require = {"api_key", "clique_name", "user_id"})
    @Validation({@Validator(field = "user_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest addToClique(Map<String, String> params) throws PlurkException {
        return prepare("addToClique", params);
    }

    @Meta(uri = "/Cliques/remove", require = {"api_key", "clique_name", "user_id"})
    @Validation({@Validator(field = "user_id", validator = NonNegativeIntegerValidator.class)})
    public HttpUriRequest removeFromClique(Map<String, String> params) throws PlurkException {
        return prepare("removeFromClique", params);
    }
    // </editor-fold>

    private HttpUriRequest prepare(String methodName, Map<String, String> params) throws PlurkException {
    	// create method object from action sheet
        Method method = null;
        try {
            method = PlurkActionSheet.class.getMethod(methodName,
                    new Class<?>[]{Map.class});
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (method == null) {
            throw new PlurkException("can not find the method: " + methodName);
        }

        // get metadata to validate the user supplied data
        Meta meta = method.getAnnotation(Meta.class);
        if (meta == null) {
            throw new PlurkException("can not find the meta annotation");
        }

        // assemble the query string (the param-value will be url-encoded)
        final StringBuffer buf = new StringBuffer();
        for (String key : params.keySet()) {
            try {
                buf.append(key).append("=").append(URLEncoder.encode(params.get(key), "utf-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        buf.deleteCharAt(buf.length() - 1);

        // make the request url
        final String queryString = meta.uri() + "?" + buf.toString();
        final String uri = meta.isHttps() ? getSecuredApiUri(queryString) : getApiUri(queryString);
        final HttpRequestBase httpMethod = meta.type().equals(Type.GET) ? new HttpGet(uri) : new HttpPost(uri);

        for (String key : meta.require()) {
            if (!params.containsKey(key)) {
                throw new PlurkException("require param [" + key + "] is not found");
            }
        }

        Headers headers = method.getAnnotation(Headers.class);
        if (headers != null) {
            logger.debug("found @Headers");
            for (Header header : headers.headers()) {
                logger.debug("add header => name[" + header.key() + "] value[" + header.value() + "]");
                httpMethod.addHeader(header.key(), header.value());
            }
        }

        Validation validation = method.getAnnotation(Validation.class);
        if (validation != null) {
            logger.debug("found @Validation");
            for (Validator v : validation.value()) {
                if (params.containsKey(v.field())) {
                    logger.debug("validate field[" + v.field() + "]");
                    boolean isPass = IValidator.ValidatorUtils.validate(v.validator(), params.get(v.field()));
                    if (!isPass) {
                        throw new PlurkException(
                                "validation failure. the field [" + v.field() + "] can not pass validation [" + v.validator() + "]");
                    }
                }
            }
        }

        if (logger.isInfoEnabled()) {
            Map<String, String> loggedParams = new HashMap<String, String>(params);
            for (String key : loggedParams.keySet()) {
                if (key.contains("key") || key.contains("password")) {
                    loggedParams.put(key, "**********");
                }
            }
            logger.info("Params: " + loggedParams.toString());
        }

        return httpMethod;
    }
}
