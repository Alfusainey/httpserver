package com.adobe.assignment.http.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.adobe.assignment.http.methods.HttpMethodHandler;

public class ServerConfigTest extends TestCase {

    private static final String SINGLE_HANDLER_XML_CONFIG = 
    		"<config>\n" +            
            "    <supportedmethods>\n"+
            "        <supportedmethod>\n" +
            "            <class name=\"com.adobe.assignment.http.methods.GetMethodHandler\" />\n" +
            "        </supportedmethod>\n" +
            "    </supportedmethods>" +
            "</config>";
    
    private static final String MALFORMED_XML_CONFIG = 
    		"<config>\n" +            
            "    <supportedmethods>\n"+
            "        <supportedmethod>\n" +
            "            <class name=\"com.adobe.assignment.http.methods.GetHandler\" />\n" +
            "        </supportedmethods>\n" +
            "    </supportedmethods>" +
            "";
    
    private ServerConfig config = null;
    
    @Override
    public void setUp() throws Exception {
    	config = new ServerConfig();
    }
    
    @Test
    public void testSingleHandlerConfig() throws Exception {   	 
    	InputStream is = new ByteArrayInputStream(SINGLE_HANDLER_XML_CONFIG.getBytes("UTF-8"));

    	config.parse(is);    	     	 
    	List<HttpMethodHandler> handlers = config.getHandlers();

    	assertNotNull(handlers);    	 
    	assertEquals(1, handlers.size());
    	assertEquals("com.adobe.assignment.http.methods.GetMethodHandler", handlers.get(0).getClass().getName());      	    	
    }
    
    @Test
    public void testMalformXmlConfiguration() throws Exception {
    	try {
        	InputStream is = new ByteArrayInputStream(MALFORMED_XML_CONFIG.getBytes("UTF-8"));
        	config.parse(is);
        	fail("Must not get here");
    	} catch (SAXException e) {
    		// success
    	}
    }   
 }
