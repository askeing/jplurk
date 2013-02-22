package com.google.jplurk.net;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ClientConnectionManager;

public class LazyIdleConnectionMonitor {

    private static Log logger = LogFactory.getLog(LazyIdleConnectionMonitor.class);
    private long lastCheckTime = System.currentTimeMillis();

    public void cleanIdleConnections(ClientConnectionManager connectionManager) {
        if ((System.currentTimeMillis() - lastCheckTime) < 30 * 1000) {
            return;
        }
        
        try {
            // Close expired connections
            connectionManager.closeExpiredConnections();

            // Optionally, close connections
            // that have been idle longer than 30 sec
            connectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
        } catch (Exception ignored) {
        }
        lastCheckTime = System.currentTimeMillis();
        logger.info("clean idle connections");
    }

}
