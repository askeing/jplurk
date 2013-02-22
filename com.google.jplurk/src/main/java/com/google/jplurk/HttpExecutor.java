package com.google.jplurk;

import java.net.SocketTimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.google.jplurk.exception.PlurkException;
import com.google.jplurk.net.HttpClientFactory;
import com.google.jplurk.net.JPlurkResponseHandler;
import com.google.jplurk.net.LazyIdleConnectionMonitor;
import com.google.jplurk.net.ProxyProvider;
import com.google.jplurk.org.apache.commons.lang.StringUtils;

public class HttpExecutor {

    private static Log logger = LogFactory.getLog(HttpExecutor.class);
    private DefaultHttpClient client = HttpClientFactory
            .createThreadSafeHttpClient();
    private LazyIdleConnectionMonitor monitor = new LazyIdleConnectionMonitor();

    private ISettings config;

    public HttpExecutor(ISettings config) {
        this.config = config;
        ProxyProvider.loadDefaultProxy(config);
        ProxyProvider.loadDynamicProxy();
        configureProxy();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    client.getConnectionManager().shutdown();
                } catch (Exception ignored) {
                }
            }
        });
    }

    /**
     * ProgrammaticallySetting > Dynamic > Default. Load Default Proxy Setting,
     * then load Dynamic Proxy Setting.
     * */
    protected void configureProxy() {
        // Auth Proxy Setting
        if (StringUtils.isNotBlank(ProxyProvider.getUser())) {
            ((DefaultHttpClient) client).getCredentialsProvider()
                    .setCredentials(
                            new AuthScope(ProxyProvider.getHost(),
                                    ProxyProvider.getPort()),
                            new UsernamePasswordCredentials(ProxyProvider
                                    .getUser(), ProxyProvider.getPassword()));
            logger.debug("Proxy: Auth "
                + ((UsernamePasswordCredentials) ((DefaultHttpClient) client)
                        .getCredentialsProvider().getCredentials(AuthScope.ANY)).getUserName());
            logger.debug("Proxy: Auth "
                + ((UsernamePasswordCredentials) ((DefaultHttpClient) client)
                        .getCredentialsProvider().getCredentials(AuthScope.ANY)).getPassword());
        }
        // Proxy Host Setting
        if (StringUtils.isNotBlank(ProxyProvider.getHost())) {
            HttpHost proxy = new HttpHost(ProxyProvider.getHost(),
                    ProxyProvider.getPort());
            client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
                    proxy);
            logger.debug("Proxy: Host "
                    + ((HttpHost) client.getParams().getParameter(
                            ConnRoutePNames.DEFAULT_PROXY)).getHostName());
            logger.debug("Proxy: Prot "
                    + String.valueOf(((HttpHost) client.getParams()
                            .getParameter(ConnRoutePNames.DEFAULT_PROXY))
                            .getPort()));
        }
    }

    protected HttpClient getHttpClient() {
        return client;
    }

    @SuppressWarnings("unchecked")
    protected <T> T execute(HttpUriRequest method,
            Class<? extends ResponseHandler<T>> clazz) throws PlurkException {
        boolean isTimeOut = false;
        if (logger.isInfoEnabled()) {
            String uri = method.getURI().toString();
            logger.info("execute: " + StringUtils.substringBefore(uri, "?"));
        }
        T result = null;
        try {
            HttpContext ctx = new BasicHttpContext();
            ctx.setAttribute(ClientContext.COOKIE_STORE, config
                    .getCookieStore());
            result = (T) client.execute(method, clazz.newInstance(), ctx);
        } catch (SocketTimeoutException e) {
            isTimeOut = true;
            logger.debug("need timeout retry.");
        } catch (Exception e) {
            throw new PlurkException(e);
        } finally {
            monitor.cleanIdleConnections(client.getConnectionManager());
        }
        return (T) (isTimeOut ? execute(method) : result);

    }

    // <editor-fold defaultstate="collapsed" desc="Execution of HttpRequest">
    protected String execute(HttpUriRequest method) throws PlurkException {
        return execute(method, JPlurkResponseHandler.class);
    }
    // </editor-fold>
}