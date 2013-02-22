package com.google.jplurk.net;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

public class ThinMultipartEntity implements HttpEntity {

	static Log logger = LogFactory.getLog(ThinMultipartEntity.class);

	private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	private String boundary = null;

	ByteArrayOutputStream out = new ByteArrayOutputStream();
	boolean isSetLast = false;
	boolean isSetFirst = false;

	public ThinMultipartEntity() {
		StringBuffer buf = new StringBuffer();
		Random rand = new Random();
		for (int i = 0; i < 30; i++) {
			buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
		}
		this.boundary = buf.toString();

	}

	public void writeFirstBoundaryIfNeeds(){
		if(!isSetFirst){
			try {
				out.write(("--" + boundary + "\r\n").getBytes());
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		isSetFirst = true;
	}

	public void writeLastBoundaryIfNeeds() {
		if(isSetLast){
			return ;
		}
		try {
			out.write(("\r\n--" + boundary + "--\r\n").getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		isSetLast = true;
	}

	public void addPart(String key, String value) {
		writeFirstBoundaryIfNeeds();
		try {
			out.write(("Content-Disposition: form-data; name=\"" +key+"\"\r\n").getBytes());
			out.write("Content-Type: text/plain; charset=UTF-8\r\n".getBytes());
			out.write("Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes());
			out.write(value.getBytes());
			out.write(("\r\n--" + boundary + "\r\n").getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void addPart(String key, String fileName, InputStream fin){
		writeFirstBoundaryIfNeeds();
		try {
			out.write(("Content-Disposition: form-data; name=\""+ key+"\"; filename=\"" + fileName + "\"\r\n").getBytes());
			out.write("Content-Type: application/octet-stream\r\n".getBytes());
			out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

			int data = fin.read();
			while(data !=-1){
				out.write(data);
				data = fin.read();
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				fin.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public void addPart(String key, File value) {
		try {
			addPart(key, value.getName(), new FileInputStream(value));
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public long getContentLength() {
		writeLastBoundaryIfNeeds();
		return out.toByteArray().length;
	}

	public Header getContentType() {
		return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
	}

	public boolean isChunked() {
		return false;
	}

	public boolean isRepeatable() {
		return false;
	}

	public boolean isStreaming() {
		return false;
	}

	public void writeTo(OutputStream outstream) throws IOException {
		outstream.write(out.toByteArray());
	}

	public Header getContentEncoding() {
		return null;
	}

	public void consumeContent() throws IOException,
			UnsupportedOperationException {
		if (isStreaming()) {
			throw new UnsupportedOperationException(
					"Streaming entity does not implement #consumeContent()");
		}
	}

    public InputStream getContent() throws IOException,
            UnsupportedOperationException {
        return new ByteArrayInputStream(out.toByteArray());
    }

}
