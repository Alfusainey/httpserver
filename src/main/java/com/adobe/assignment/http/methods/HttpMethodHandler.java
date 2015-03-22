package com.adobe.assignment.http.methods;

import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.MIMETyper;
import com.adobe.assignment.http.server.ServerConfig;

public interface HttpMethodHandler {

	/**
	 * Initializes this HttpMethod handler.
	 * 
	 * @param config		The server configuration
	 */
	public void init(ServerConfig config);
	
	/**
	 * Handle and service the given HTTP request.
	 * 
	 * @param request		The request to handle.
	 * @param response		The response to send to the client.
	 * @return				true, if this handler can handle the request
	 * 						and false otherwise.
	 */
	public boolean handle(HttpRequest request, HttpResponse response);
}
