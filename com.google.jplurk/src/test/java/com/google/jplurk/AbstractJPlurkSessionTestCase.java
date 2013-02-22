package com.google.jplurk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

import com.google.jplurk.exception.PlurkException;

/**
 * JPlurkSessionTestCase is used for the testing some plurk operations which are
 * needed to login. The class provides exportCookieStore() method to help you
 * create the CookieStore data.
 * 
 * @author qrtt1
 */
public abstract class AbstractJPlurkSessionTestCase extends TestCase {

    protected static Log logger = LogFactory.getLog(PlurkClientTestCase.class);
    protected PlurkClient client;
    protected PlurkSettings settings;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            settings = new PlurkSettings();
            BufferedReader reader = new BufferedReader(new FileReader(
                    getCookieStore()));
            JSONArray cookies = new JSONArray(reader.readLine());
            settings.importCookies(cookies);
            logger.info("import cookies: " + cookies);
            client = new PlurkClient(settings);
        } catch (PlurkException e) {
            throw new Exception(e);
        }
    }

    public static File getCookieStore() {
        File dir = new File(System.getProperty("user.home", "."));
        dir.mkdirs();
        return new File(dir, ".jplurkCookies");
    }

}
