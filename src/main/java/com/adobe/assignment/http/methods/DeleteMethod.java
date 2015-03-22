package com.adobe.assignment.http.methods;

import java.io.IOException;
import java.util.Iterator;

import com.adobe.assignment.http.HttpConstants;
import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.server.ServerConfig;

/**
 *  A DELETE request handler. This handler knows how to service HTTP DELETE requests.
 *  This implementation will do a check on the request to test if the authorization
 *  header field is set. Otherwise deleting the resource will fail.
 *  
 */
class DeleteMethod implements HttpMethodHandler {

	private ServerConfig config;
	
	public void init(ServerConfig config) {
		this.config = config;
	}

	/**
	 * @see HttpMethodHandler#handle(HttpRequest, HttpResponse)
	 */
	public boolean handle(HttpRequest request, HttpResponse response) {
		if (canHandle(request)) {
			if (isAuthorizationPresent(request)) {
				doDelete(request, response);
			} else {
				response.sendError(HttpResponse.SC_UNAUTHORIZED);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the authorization header is present in the request.
	 * 
	 * Note: For now we do not do anything with the authorization header value.
	 * An improvement here will  be to decode the authorization header value
	 * and use it to authenticate the HTTP user performing the DELETE request.
	 * 
	 * @param request     The HTTP request
	 * @return            True, if the authorization header is present
	 * 				      in the request and false otherwise.
	 */
	private boolean isAuthorizationPresent(HttpRequest request) {
		Iterator<String> it = request.getHeaderNames();
		while (it.hasNext()) {
			String headerName = it.next();
			if (headerName.equals("Authorization")) {				
				return true;
			}
		}
		return false;
	}
	
	public void doDelete(HttpRequest request, HttpResponse response) {
		// TODO: delete the resource
		
		response.setStatus(HttpResponse.SC_OK);
		try {
			response.write();
		} catch (IOException e) {
			response.sendError(HttpResponse.SC_INTERNAL_ERROR);
		}
		
	}
	
	public boolean canHandle(HttpRequest request) {
		return request.getMethod().equals(HttpConstants.METHOD_DELETE);
	}

}
