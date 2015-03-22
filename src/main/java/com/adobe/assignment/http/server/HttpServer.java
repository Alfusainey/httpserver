package com.adobe.assignment.http.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * A simplified HTTP server
 * 
 * Note: This version has been extended to support any type of HTTP request. The
 * extension mechanism uses 'configuration over code-modification'. To do so,
 * simply configure an implementation of the HttpMethodHandler interface. Such
 * an implementation will concretely specify how such an impl will handle and
 * service http request. Once implemented, add the FQN of the class to the XML
 * configuration file. This file is read once upon bootstraping this HTTP
 * Server.
 * 
 * I also dropped java.util.logging and instead used slf4j with logback
 * 
 * @author Prof. David Bernstein, James Madison University
 * @author Alfusainey Jallow, University of the Gambia
 * 
 * @version 3.0
 */
public class HttpServer {

	private static final Logger log = LoggerFactory.getLogger(HttpServer.class);
	
	/**
	 * Core server configuration file. Contains the host, port and web-root
	 * configuration parameters for this server.
	 */
	private static final String WEB_SERVER_PROPERTIES = "webserver.properties";

	/**
	 * Configuration file for the different HTTP request handlers. Different request
	 * handler implementations can be configured in this XML and read by the 
	 * server during startup.
	 * 
	 * Advantage: Offers configuration over code modification.
	 */
	private static final String WEB_SERVER_HTTP_HANDLERS_CONFIG = "http_handlers.xml";

	private final ExecutorService threadPool;
	private final ServerSocket serverSocket;

	private ServerConfig config;

	/**
	 * Default COnstructor
	 */
	public HttpServer() throws IOException {
		// Initialize the Server's configuration.
		init();

		serverSocket = new ServerSocket(config.getPort());
		
		log.info("Created Server Socket on " + config.getPort());

		threadPool = Executors.newCachedThreadPool();

		serverSocket.setSoTimeout(10000);
	}

	/**
	 * The entry point of the application
	 * 
	 * @param args
	 *            The command line arguments
	 */
	public static void main(String[] args) {
		HttpServer server = null;

		try {
			// Construct and start the server
			server = new HttpServer();
			server.start();
			
		} catch (IOException e) {
			// fail to initialize server.
		} finally {
			if (server != null) {
				server.stopPool();
			}
		}
	}

	/**
	 * Stop the threads in the pool
	 */
	private void stopPool() {
		// Prevent new Runnable objects from being submitted
		threadPool.shutdown();

		try {
			// Wait for existing connections to complete
			if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
				// Stop executing threads
				threadPool.shutdownNow();

				// Wait again
				if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
					log.info("Could not stop thread pool.");
				}
			}
		} catch (InterruptedException ie) {
			// Stop executing threads
			threadPool.shutdownNow();

			// Propagate the interrupt status --> for shutting down thread-pool.
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Start accepting connections from clients.
	 */
	public void start() {
		try {
			HttpConnectionHandler connection;
			Socket socket;

			while (true) {
				try {

					socket = serverSocket.accept();

					log.info("Accepted a connection from " + socket.getInetAddress().getHostAddress()
							+ ":" + socket.getPort());

					connection = new HttpConnectionHandler(socket, config);

					// Add the connection to a BlockingQueue<Runnable> object
					// and, ultimately, call it's run() method in a thread
					// in the pool
					threadPool.submit(connection);
				} catch (SocketTimeoutException ste) {
					log.info("The server has timeout waiting for incomming connection from clients");
				} catch (IOException e) {
					log.warn("Fatal error: "+e.getMessage());
				}
			}
		} finally {
			stopPool();
		}
	}

	/**
	 * Initializes the HTTP server. The initialization includes reading the
	 * configuration needed to bootstrap the HTTP Server. This includes the
	 * properties and XML file configurations.
	 * 
	 * @throws IOException
	 */
	private void init() {
		// Load both configuration as a resource.
		InputStream propsStream = HttpServer.class.getClassLoader().getResourceAsStream(WEB_SERVER_PROPERTIES);
		InputStream xmlStream = HttpServer.class.getClassLoader().getResourceAsStream(WEB_SERVER_HTTP_HANDLERS_CONFIG);
		if (propsStream != null && xmlStream != null) {
			config = new ServerConfig();
			try {
				config.load(propsStream);
				config.parse(xmlStream);
			} catch (IOException e) {
				log.warn("fatal error "+e.getMessage());
			} catch (SAXException e) {
				log.debug(e.getMessage());
			}			
		} else {
			log.debug("Fail to locate mandatory configuration files for this web-server. A default host and port will be used!");
		}
	}

}
