package com.google.jplurk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.json.JSONArray;

import com.google.jplurk.exception.PlurkException;
import com.google.jplurk.org.apache.commons.lang.StringUtils;

/**
 * PlurkSettings class will read a property file which contains plurk api key at least.
 *
 * @author qty
 */
public class PlurkSettings implements ISettings {

    public final static String settings = "jplurk.properties";
    private Properties prop;
    private BasicCookieStore cookieStore = new BasicCookieStore();

    /**
     * Create settings object from ~/jplurk.properties file
     *
     * @throws PlurkException
     */
    public PlurkSettings() throws PlurkException {
        prop = getSettings();
    }

    /**
     * Create settings object from given file object.
     *
     * @param settingFile
     * @throws PlurkException
     */
    public PlurkSettings(File settingFile) throws PlurkException {
        prop = getSettings(settingFile);
    }

    /* (non-Javadoc)
	 * @see com.google.jplurk.ISettings#getApiKey()
	 */
    public String getApiKey() {
        return prop.getProperty("api_key");
    }

    /* (non-Javadoc)
	 * @see com.google.jplurk.ISettings#getDefaultProxyHost()
	 */
    public String getDefaultProxyHost() {
        return prop.getProperty("default_proxy_host");
    }

    /* (non-Javadoc)
	 * @see com.google.jplurk.ISettings#getDefaultProxyPort()
	 */
    public int getDefaultProxyPort() {
        int port = 80;
        try {
            port = Integer.valueOf(prop.getProperty("default_proxy_port"));
        } catch (NumberFormatException e) {
        }
        return port;
    }

    /* (non-Javadoc)
	 * @see com.google.jplurk.ISettings#getDefaultProxyUser()
	 */
    public String getDefaultProxyUser() {
        return prop.getProperty("default_proxy_user");
    }

    /* (non-Javadoc)
	 * @see com.google.jplurk.ISettings#getDefaultProxyPassword()
	 */
    public String getDefaultProxyPassword() {
        return prop.getProperty("default_proxy_password");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.jplurk.ISettings#createParamMap()
     */
    public Args args() {
        return new Args(new HashMap<String, String>() {
            private static final long serialVersionUID = -5306686629368927936L;
            {
                put("api_key", getApiKey());
            }
        });
    }

    /* (non-Javadoc)
	 * @see com.google.jplurk.ISettings#getLang()
	 */
    public String getLang() {
        return prop.getProperty("lang", "en");
    }

    private Properties getSettings() throws PlurkException {
        File dir = new File(System.getProperty("user.home", "."));
        dir.mkdirs();
        File setting = new File(dir, settings);
        return getSettings(setting);
    }

    private Properties getSettings(File settingFile) throws PlurkException {
        Properties prop = new Properties();
        if (!settingFile.exists()) {
            throw new PlurkException("settings file is not found: " + settingFile.getAbsolutePath());
        }

        FileInputStream fin = null;
        try {
            fin = new FileInputStream(settingFile);
            prop.load(fin);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                }
            }
        }

        if (StringUtils.isBlank(prop.getProperty("api_key"))) {
            throw new PlurkException("settings has no api_key.");
        }

        return prop;
    }

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	public JSONArray exportCookies() {
		return CookieUtil.exportCookies(cookieStore);
	}

	public void importCookies(JSONArray cookies) {
		CookieUtil.importCookies(cookieStore, cookies);
	}
}
