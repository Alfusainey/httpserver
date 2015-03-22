package com.adobe.assignment.http.methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.MIMETyper;
import com.adobe.assignment.http.server.ServerConfig;


/**
 *  A GET request handler. This handler knows how to service HTTP GET requests.
 *  This implementation simply reads the file present in the request URI from
 *  the file system and sends it back to the client.
 *  
 */
class GetMethodHandler implements HttpMethodHandler {

	private ServerConfig config;
	
	private static final int EOF = -1;
	/**
	 * @see HttpMethodHandler#init(ServerConfig, MIMETyper)
	 */
	public void init(ServerConfig config) {		
		this.config = config;
	}
	
	/**
	 * @see HttpMethodHandler#handle(HttpRequest, HttpResponse)
	 */
	public boolean handle(HttpRequest request, HttpResponse response) {
		if (canHandle(request)) {
			doGet(request, response);
			return true;
		}
		return false;
	}
	
	/**
	 * Determines if this handler can handle the specified
	 * HTTP request.
	 * 
	 * @param request		The request to handle.
	 * 
	 * @return true if the specified request is a GET request
	 * 		   and false otherwise.
	 */
	private boolean canHandle(HttpRequest request) {
		return request.getMethod().equals(HttpConstants.METHOD_GET);
	}
	
	private void doGet(HttpRequest request, HttpResponse response) {
		MIMETyper mt = MIMETyper.createInstance();
		
		FileInputStream fis = null;
		File file = new File(config.getWebRoot(), request.getRequestURI());
		
		int size = (int) file.length();		
		byte[] b = new byte[size];
		
		if (file.exists()) {
			try {
				fis = new FileInputStream(file);
				System.out.println(mt.getContentTypeFor(file.getName()));
				response.setContentType(mt.getContentTypeFor(file.getName()));
				response.setContentLength(size);
				int ch = fis.read(b, 0, b.length);
				while (ch != EOF) {
					response.setContent(b);
					ch = fis.read(b, 0, b.length);
				}				
				
				response.write();
			} catch (FileNotFoundException e) {
				response.sendError(HttpResponse.SC_NOT_FOUND);
			} catch (IOException e) {
				response.sendError(HttpResponse.SC_INTERNAL_ERROR);
			}
		} else {
			// file does not exist
			response.sendError(HttpResponse.SC_NOT_FOUND);
		}
	}	

}
