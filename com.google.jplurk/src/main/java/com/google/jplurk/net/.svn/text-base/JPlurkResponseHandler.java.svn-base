package com.google.jplurk.net;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public class JPlurkResponseHandler implements ResponseHandler<String> {

    private static Log logger = LogFactory.getLog(JPlurkResponseHandler.class);

    public String handleResponse(final HttpResponse response)
            throws HttpResponseException, IOException {
        HttpEntity entity = response.getEntity();
        String ret = (entity == null ? null : EntityUtils.toString(entity));
        
        if (logger.isDebugEnabled()) {
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                logger.debug("Response Header: " + header.toString());
            }
            logger.info(response.getStatusLine().toString());
        }
        logger.info("HttpContent: " + ret);

        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() >= 300) {
            logger.warn("Http Response Body: \n" + ret);
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }

        logger.debug("Response: " + ret);

        return ret;
    }

}
