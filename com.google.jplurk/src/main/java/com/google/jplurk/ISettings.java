package com.google.jplurk;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface ISettings {

	public abstract String getApiKey();

	public abstract String getDefaultProxyHost();

	public abstract int getDefaultProxyPort();

	public abstract String getDefaultProxyUser();

	public abstract String getDefaultProxyPassword();

	public abstract Args args();

	public abstract String getLang();
	
	public CookieStore getCookieStore();
	
	public JSONArray exportCookies();
	
	public void importCookies(JSONArray cookies);
	
	
	static class CookieUtil {
		static Log logger = LogFactory.getLog(CookieUtil.class);
		
		public static JSONArray exportCookies(CookieStore cookieStore){
			JSONArray ret = new JSONArray();
			for (Cookie c : cookieStore.getCookies()) {
				JSONObject cookie = new JSONObject();
				try {
					cookie.put("domain", c.getDomain());
					cookie.put("expiryDate", c.getExpiryDate().getTime());
					cookie.put("name", c.getName());
					cookie.put("value", c.getValue());
					cookie.put("path", c.getPath());
					ret.put(cookie);
				} catch (JSONException e) {
					logger.error(e.getMessage(), e);
				}
			}
			return ret;
		}	
		
		public static void importCookies(CookieStore cookieStore, JSONArray cookies) {
			cookieStore.clear();
			
			for (int i = 0; i < cookies.length(); i++) {
				try {
					JSONObject json = cookies.getJSONObject(i);
					if (json.has("domain") && json.has("expiryDate")
						&& json.has("name") && json.has("value")) {
						BasicClientCookie cookie = new BasicClientCookie(json.getString("name"), json.getString("value"));
						cookie.setDomain(json.getString("domain"));
						cookie.setPath(json.getString("path"));
						long expiryTs = json.getLong("expiryDate");
						cookie.setExpiryDate(new Date(expiryTs));
						cookieStore.addCookie(cookie);
					}
				} catch (JSONException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	static class Simple implements ISettings {

		static Log logger = LogFactory.getLog(Simple.class);
		
		private String apiKey;
		private Lang lang;
		private BasicCookieStore cookieStore = new BasicCookieStore();

		public Simple(String apiKey, Lang lang) {
			this.apiKey = apiKey;
			this.lang = (lang == null ? Lang.en : lang);
		}

		public Args args() {
			Args m = new Args();
			m.name("api_key").value(getApiKey());
			return m;
		}

		public String getApiKey() {
			return apiKey;
		}

		public String getDefaultProxyHost() {
			return null;
		}

		public String getDefaultProxyPassword() {
			return null;
		}

		public int getDefaultProxyPort() {
			return 0;
		}

		public String getDefaultProxyUser() {
			return null;
		}

		public String getLang() {
			return lang.toString();
		}

		public CookieStore getCookieStore() {
			return cookieStore;
		}
		
		public JSONArray exportCookies(){
			return CookieUtil.exportCookies(cookieStore);
		}
		
		public void importCookies(JSONArray cookies) {
			CookieUtil.importCookies(cookieStore, cookies);
		}
	}

}