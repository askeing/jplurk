package com.google.jplurk.net;

import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class HttpClientFactory {
    
    public final static long SECOND = 1000L;
    public final static int TIMEOUT = (int) (30 * SECOND);
    private static Log logger = LogFactory.getLog(HttpClientFactory.class);
    
    public final static TrustManager TRUST_EVEYONE_MANAGER = new X509TrustManager() {

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] chain, String authType)
                throws java.security.cert.CertificateException {
            logger.debug("trust the client certificate");
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] chain, String authType)
                throws java.security.cert.CertificateException {
            logger.debug("trust the server certificate");
        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    public static DefaultHttpClient createThreadSafeHttpClient() {
        // Create and initialize HTTP parameters
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT);
        
        ConnManagerParams.setMaxTotalConnections(params, 100);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

        // Create and initialize scheme registry
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));

        try {
            try {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(new KeyManager[0], new TrustManager[] { TRUST_EVEYONE_MANAGER }, new SecureRandom());
                SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslcontext);
                sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
            } catch (NoSuchMethodError e) {
                /* fallback to use custom AndroidSSLSocketFactory */
                logger.warn(e.getMessage());
                logger.warn("try android ssl socket factory: " + com.google.jplurk.net.AndroidSSLSocketFactory.class);
                com.google.jplurk.net.AndroidSSLSocketFactory androidSSLSocketFactory = new com.google.jplurk.net.AndroidSSLSocketFactory((KeyStore)null);
                androidSSLSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                schemeRegistry.register(new Scheme("https", androidSSLSocketFactory, 443));
            }
           
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
                schemeRegistry);
        return new DefaultHttpClient(cm, params);
    }
}
