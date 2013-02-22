package com.google.jplurk;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.jplurk.exception.PlurkException;

public class PlurkNotifier extends TimerTask {
    private static Log logger = LogFactory.getLog(PlurkNotifier.class);

    private HttpClient client;
    private List<NotificationListener> listeners = new ArrayList<NotificationListener>();
    private StringBuffer cometQueryUrl = new StringBuffer();

    private static NotificationListener NOOP = new NotificationListener() {
        public void onNotification(JSONObject message) throws Exception {
            logger.warn(message);
        }
    };

    public PlurkNotifier(HttpClient client, JSONObject userChannel)
            throws PlurkException {
        this.client = client;
        try {
            cometQueryUrl.setLength(0);
            cometQueryUrl.append(userChannel.getString("comet_server"));
        } catch (Exception e) {
            throw new PlurkException(
                    "Something is wrong when creating the plurk notifier.", e);
        }
    }

    public void addNotificationListener(NotificationListener listener) {
        this.listeners.add(listener);
    }

    public void removeNotificationListener(NotificationListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void run() {
        boolean isTimeOut = false;
        HttpResponse resp = null;
        String rawContent = null;
        try {
            logger.info("query: " + cometQueryUrl);
            resp = client.execute(new HttpGet(cometQueryUrl.toString()));
            rawContent = EntityUtils.toString(resp.getEntity());
            rawContent = checkAndFix(rawContent);
            JSONObject ret = new JSONObject(rawContent);
            logger.info("response: " + ret);
            dispatchNotifications(ret);
            updateNextOffset(ret);

        } catch (SocketTimeoutException e) {
            isTimeOut = true;
            logger.debug("need timeout retry.");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("HttpResponse: " + resp);
            logger.error("RawContent: " + rawContent);
        }
        if (isTimeOut) {
            run();
        }
    }

    private String checkAndFix(String jsonRaw) {
        String jsonText = jsonRaw.trim();
        if(jsonText.startsWith("CometChannel.scriptCallback(") && jsonText.endsWith(");"))
        {
            jsonText = jsonText.substring("CometChannel.scriptCallback(".length());
            jsonText = jsonText.substring(0, jsonText.length() -2);
        }
        return jsonText;
    }

    private void dispatchNotifications(JSONObject ret) {
        try {
            logger.info(ret);
            if (!ret.has("data")) {
                logger.info("no data");
                return;
            }

            logger.info("number of listeners: " + listeners.size());
            JSONArray data = ret.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (listeners.isEmpty()) {
                        NOOP.onNotification(data.getJSONObject(i));
                        continue;
                    }

                    for (NotificationListener listener : listeners) {
                        try {
                            listener.onNotification(data.getJSONObject(i));
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void updateNextOffset(JSONObject ret) {
        try {
            if (!ret.has("new_offset")) {
                return;
            }
            int offset = ret.getInt("new_offset");
            cometQueryUrl.setLength(cometQueryUrl.indexOf("offset="));
            cometQueryUrl.append("offset=");
            cometQueryUrl.append(offset);
            logger.info("new offset is " + offset);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static interface NotificationListener {
        public void onNotification(JSONObject message) throws Exception;
    }

}
