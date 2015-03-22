package com.adobe.assignment.http;

import java.util.Iterator;

/**
 * A partial encapsulation of an HTTP message (i.e., request or response)
 * 
 * @author Prof. David Bernstein, James Madison University
 * @version 0.2
 */
abstract class HttpMessage {

	protected byte[] content;
	protected NameValueMapper headers;

	/**
	 * Default Constructor
	 */
	public HttpMessage() {
		content = null;
		headers = NameValueMapper.createNameValueMap();
	}

	/**
	 * Get the content of this HttpMessage
	 * 
	 * @return The content
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * Returns the length, in bytes, of the content contained in the request and
	 * sent by way of the input stream
	 * 
	 * @return The length or -1 if the length is not known
	 */
	public int getContentLength() {
		int result;

		try {
			result = Integer.parseInt(getHeader("Content-Length"));
		} catch (Exception e) {
			result = -1;
		}

		return result;
	}

	/**
	 * Returns the value of the specified header
	 * 
	 * @param name
	 *            The name of the header
	 * @return The value of the header
	 */
	public String getHeader(String name) {
		return headers.getValue(name);
	}

	/**
	 * Returns the names of all headers
	 * 
	 * @return The names of all headers
	 */
	public Iterator<String> getHeaderNames() {
		return headers.getNames();
	}

	/**
	 * Set the content (i.e., payload) for this HttpMessage
	 * 
	 * @param content
	 *            The payload/content
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}

	/**
	 * Sets the content length
	 * 
	 * @param contentLength
	 *            The contentLength
	 */
	public void setContentLength(int contentLength) {
		setHeader("Content-Length", Integer.toString(contentLength));
	}

	/**
	 * Sets the type of the content the server returns to the client
	 * 
	 * @param type
	 *            The type
	 */
	public void setContentType(String type) {
		setHeader("Content-Type", type);
	}

	/**
	 * Adds a field (with the given name and value) to the response header
	 * 
	 * Note: This method must be called before getOutputStream()
	 * 
	 * @param name
	 *            The name of the field
	 * @param value
	 *            The value of the field
	 */
	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	/**
	 * Set the headers
	 * 
	 * @param headers
	 *            The headers
	 */
	public void setHeaders(NameValueMapper headers) {
		this.headers = headers;
	}
}
