package com.adobe.assignment.http.server;

import java.io.IOException;
import java.net.Socket;

import com.adobe.assignment.http.HttpInputStream;
import com.adobe.assignment.http.HttpOutputStream;
import com.adobe.assignment.http.HttpRequest;
import com.adobe.assignment.http.HttpResponse;
import com.adobe.assignment.http.methods.HttpMethodHandler;

/**
 * Handle an HTTP 1.0 connection in a new thread of execution
 * 
 * This version:
 * 
 * Addition: I have added logic for providing pluggable support for 
 * handling Http requests. This has the advantage that the HttpServer
 * be able to service any type of HTTP requests simply by means of 
 * configuration. This adheres to the 'configuration
 * over code modification' design principle, since different Http request
 * handlers can be configured to work with the HTTP server.
 * 
 * @author Prof. David Bernstein, James Madison University
 * @author Alfusainey Jallow,	University of the Gambia
 * 
 * @version 0.3
 */
class HttpConnectionHandler implements Runnable {

	private Socket socket;

	private final ServerConfig config;
	
	/**
	 * The HTTP Request object for this connection.
	 */
	private HttpRequest httpRequest;
	
	/**
	 * The HTTP response object for this connection.
	 */
	private HttpResponse httpResponse;
	/**
	 * Explicit Value Constructor (Starts the thread of execution)
	 * 
	 * @param s
	 *            The TCP socket for the connection
	 * @param
	 * 			  The Server's configuration
	 */
	public HttpConnectionHandler(Socket s, ServerConfig config) {
		socket = s;
		this.config = config;
	}

	/**
	 * The entry point for the thread
	 */
	public void run() {		

		String method;

		try {

			// Create an empty request and response
			HttpRequest request = getRequest();
			HttpResponse response = getResponse();

			try {
				// Read and parse the request information
				request.read();

				// Determine the method to use
				method = request.getMethod();

				// Respond to the request
				if ((request == null) || (method == null)) {
					response.sendError(HttpResponse.SC_BAD_REQUEST);
				} else {
					handle(request, response);
				}
			} catch (Exception e) {
				response.sendError(HttpResponse.SC_INTERNAL_ERROR);
			}
		} catch (IOException e) {
			// fail to get request and response.
			// The server should close the socket.
		} finally {
			// close();
		}
	}
	
	public HttpRequest getRequest() throws IOException {
		if (httpRequest == null) {
			httpRequest = new HttpRequest(getInputStream());
		}
		return httpRequest;
	}
	
	public HttpResponse getResponse() throws IOException {
		if (httpResponse == null) {
			httpResponse = new HttpResponse(getOutputStream());
		}
		return httpResponse;
	}
	
	private HttpInputStream getInputStream() throws IOException {
		return new HttpInputStream(socket.getInputStream());
	}
	
	private HttpOutputStream getOutputStream() throws IOException {
		return new HttpOutputStream(socket.getOutputStream());
	}
	
	/**
	 * Checks whether this HttpConnection should be kept
	 * alive i.e left open. If the request present with this
	 * connection has its keep-alive header set, this method
	 * returns true and false otherwise.
	 * 
	 * Note: This method is not used anywhere yet. It would be
	 * needed if the server should support persistent connections.
	 * 
	 * @return
	 * @throws IOException 
	 */
	public boolean keepAlive() throws IOException {
		String keepAlive = getRequest().getHeader("Connection");
		return (keepAlive == null) ? false : keepAlive.equalsIgnoreCase(keepAlive);
	}
	
	/**
	 * Handles the HTTP request. Handling the request involves
	 * consulting a list of HttpMethodHandler(s) that knows how to
	 * service/deal with the specified request.
	 * 
	 * If there exist a handler in the list that understands the request,
	 * then the handling of the request is delegated to that handler.
	 * 
	 * @param request
	 *            Contents of the request
	 * @param response
	 *            Used to generate the response
	 */
	private void handle(HttpRequest request, HttpResponse response) {
		for (HttpMethodHandler handler : config.getHandlers()) {
			if (handler.handle(request, response)) {
				return;
			}
		} // else: HTTP method is not supported by this server.
		response.sendError(HttpResponse.SC_NOT_IMPLEMENTED);
	}
	
	/**
	 * Closes the connection.
	 * 
	 * @throws IOException 
	 */
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			// log("Error while closing the socket connection")
		}
		
	}
}
