package com.google.jplurk.ext;

import java.util.Calendar;

import com.google.jplurk.DateTime;

import org.json.JSONException;

import org.json.JSONObject;

/*
 * {"qualifier_translated":"說","qualifier":"says","favorite":false,"lang":"tr_ch",
 * "posted":"Tue, 02 Feb 2010 05:23:51 GMT",
 * "content":"大家午安啊~剛剛看了一個笑話！真的很好笑~現在心情超好的！<a href=\"http://2do.twbbs.org/forums/viewthread.php?tid=39863&amp;extra=page%3D1\" class=\"ex_link\" rel=\"nofollow\">一起笑一下吧！<\/a>",
 * "limited_to":null,"plurk_type":0,"response_count":13,"owner_id":4090189,
 * "content_raw":"大家午安啊~剛剛看了一個笑話！真的很好笑~現在心情超好的！http://2do.twbbs.org/forums/viewthread.php?tid=39863&extra=page%3D1 (一起笑一下吧！)",
 * "user_id":3984997,"plurk_id":218147402,"is_unread":1,"responses_seen":0,"no_comments":0}

 * */
public class Entry implements Comparable<Entry> {

    private String content;
    private String contentRaw;
    private int plurkId;
    private int userId;
    private int ownerId;
    private Calendar posted;
    
    public Entry() {
    }
    
    public Entry(JSONObject object) {
        try {
            content = object.getString("content");
            contentRaw = object.getString("content_raw");
            plurkId = object.getInt("plurk_id");
            userId = object.getInt("user_id");
            ownerId = object.getInt("owner_id");
            posted = DateTime.create(object.getString("posted")).toCalendar();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentRaw() {
        return contentRaw;
    }

    public void setContentRaw(String contentRaw) {
        this.contentRaw = contentRaw;
    }

    public int getPlurkId() {
        return plurkId;
    }

    public void setPlurkId(int plurkId) {
        this.plurkId = plurkId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public Calendar getPosted() {
        return posted;
    }

    public void setPosted(Calendar posted) {
        this.posted = posted;
    }

    public int compareTo(Entry o) {
        if (o == null) {
            return 0;
        }
        try {
            return posted.compareTo(o.getPosted());
        } catch (Exception e) {
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return String.format("Entry[%s, %s]",
                posted != null ? posted.getTime() : "null", 
                        contentRaw);
    }
    
    @Override
    public int hashCode() {
        int cr = contentRaw == null ? 0 : contentRaw.hashCode();
        int po = posted == null ? 0 : posted.hashCode();
        return (cr << 16) + (po);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }

        return hashCode() == obj.hashCode();
    }

}
