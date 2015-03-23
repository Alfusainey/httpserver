package com.adobe.assignment.http.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.adobe.assignment.http.MIMETyper;
import com.adobe.assignment.http.methods.HttpMethodHandler;

/**
 * This class bundles the information required to bootstrap the web-server.
 * 
 * @author Alfusainey Jallow, University of the Gambia
 */

public class ServerConfig {

	private static Logger log = LoggerFactory.getLogger(ServerConfig.class);

	private static String ELEMENT_SUPPORTED_METHODS = "supportedmethods";
	private static String ELEMENT_SUPPORTED_METHOD = "supportedmethod";
	private static final String ELEMENT_CLASS = "class";
	private static final String ATTR_NAME = "name";

	/**
	 * The configured web server port. If the port number configuration is
	 * missing, a default port of zero is selected.
	 */
	private static final String PORT = "webserver.port";

	/**
	 * The host machine this webserver is running. This server defaults to
	 * localhost if no server is configured.
	 */
	private static final String HOST = "webserver.host";

	/**
	 * The configured root directory of the webserver.
	 */
	private static final String WEB_ROOT = "webserver.webroot";

	private List<HttpMethodHandler> handlers;
	private final Properties props;

	public ServerConfig() {
		handlers = new ArrayList<HttpMethodHandler>();
		props = new Properties();
	}

	public void load(InputStream inputStream) throws IOException {
		props.load(inputStream);
	}

	public int getPort() {
		return Integer.parseInt(props.getProperty(PORT, "0"));
	}

	public String getWebRoot() {
		return props.getProperty(WEB_ROOT);
	}

	/**
	 * Retrieve the configured host of the HTTP Server.
	 * 
	 * @return The host the server is running on. Localhost is returned in the
	 *         event of a missing host configuration.
	 */
	public String getHost() {
		return props.getProperty(HOST, "localhost");
	}

	public List<HttpMethodHandler> getHandlers() {
		return Collections.unmodifiableList(handlers);
	}

	/**
	 * 
	 * @param is
	 * @throws SAXException
	 *             if the document cannot be parsed.
	 */
	public void parse(InputStream is) throws SAXException {
		try {
			Element config = DomUtil.parseDocument(is).getDocumentElement();
			if (config == null) {
				log.warn("Missing mandatory config element");
				return;
			}
			Element el = DomUtil.getChildElement(config, ELEMENT_SUPPORTED_METHODS, null);
			if (el != null) {
				ElementIterator handlerElements = DomUtil.getChildren(el, ELEMENT_SUPPORTED_METHOD, null);
				while (handlerElements.hasNext()) {
					Element handler = handlerElements.nextElement();
					HttpMethodHandler instance = buildClassFromConfig(handler);
					if (instance != null) {
						instance.init(this);
						handlers.add(instance);
					}
				}
			}
		} catch (ParserConfigurationException e) {
			log.error("A serious configuration error occured during XML config parsing");
			throw new SAXException(e.getMessage());
		} catch (IOException e) {
			log.error("Exception " + e.getMessage());
			throw new SAXException(e.getMessage());
		}
	}

	private static HttpMethodHandler buildClassFromConfig(Element parent) {
		// copied from org.apache.jackrabbit.webdav.simple.ResourceConfig and
		// modified for our purpose.
		HttpMethodHandler instance = null;
		Element classElem = DomUtil.getChildElement(parent, ELEMENT_CLASS, null);
		if (classElem != null) {
			// contains a 'class' child node
			try {
				String className = DomUtil.getAttribute(classElem, ATTR_NAME, null);
				if (className != null) {
					Class<?> clazz = Class.forName(className);
					if (HttpMethodHandler.class.isAssignableFrom(clazz)) {
						instance = (HttpMethodHandler) clazz.newInstance();
					} else {
						log.warn(className + " must implement the HttpMethodHandler interface");
					}
				}
			} catch (Exception e) {
				log.warn(e.getMessage());
			}
		}
		return instance;
	}

}
