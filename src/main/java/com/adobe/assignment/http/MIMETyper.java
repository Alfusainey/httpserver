package com.adobe.assignment.http;

import org.apache.commons.io.FilenameUtils;


/**
 * A utility class for working with MIME types
 * 
 * Note: This class makes use of the Singleton Pattern since there is never need
 * for more than one MIMETyper. The MIMETyper class is thread-safe,
 * 
 * @author Prof. David Bernstein, James Madison University
 * @author Alfusainey Jallow, University of the Gambia
 * 
 * @version 1.1
 */
public class MIMETyper {
	
	private NameValueMapper types;

	private static MIMETyper instance = new MIMETyper();
	private static final String DEFAULT = "text/html";

	/**
	 * Default Constructor
	 */
	private MIMETyper() {
		types = NameValueMapper.createConcurrentNameValueMap();
		initializeTypes();
	}

	/**
	 * Create an instance of a MIMETyper if necessary. Otherwise, return the
	 * existing instance.
	 * 
	 * @return The instance
	 */
	public static MIMETyper createInstance() {
		return instance;
	}

	/**
	 * Guess the MIME type from a file extension
	 * 
	 * @param ext
	 *            The extension (e.g., ".gif")
	 * @return The MIME type (e.g., "image/gif")
	 */
	public String getContentTypeForExtension(String ext) {
		String type;

		type = types.getValue(ext.toLowerCase());
		if (type == null)
			type = DEFAULT;

		return type;
	}

	/**
	 * Guess the MIME type from a file name (possibly including a path)
	 * 
	 * @param name
	 *            The name (e.g., "/pictures/dome.gif")
	 * @return The MIME type (e.g., "image/gif")
	 */
	public String getContentTypeFor(String name) {
		String ext = FilenameUtils.getExtension(name);

		return getContentTypeForExtension(ext);
	}

	/**
	 * Initialize the types table.
	 * Improvement: This can be improved by using a file to store the
	 * mime types. This call can then load the file of mime-types as a resource.
	 * That has the advantage that different mime-types can be added to work
	 * with the server.
	 */
	private void initializeTypes() {
		types.put(".htm", "text/html");
		types.put(".html", "text/html");
		types.put(".text", "text/plain");
	}
}
