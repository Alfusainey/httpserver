package com.adobe.assignment.http.server;

import junit.framework.TestCase;

import net.iharder.Base64;

import org.junit.Test;

import com.adobe.assignment.http.NameValueMapper;

/**
 * Test class for testing the NameValueMapper class implementation.
 * 
 * @author Alfusainey Jallow, University of the Gambia 
 *
 */
public class NameValueMapperTest extends TestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void testNameValueMapping() {		
		NameValueMapper headers = NameValueMapper.createNameValueMap();
		headers.put("Content Type", "text/plain");
		headers.put("Content Length", "10");
		headers.put("Connection: Close", ":");
		headers.putPair("Host: localhost", ":");
		assertEquals(4, headers.size());
		
		String encoding = Base64.encodeBytes("admin:admin".getBytes());
		headers.put("Authorization", encoding);
		assertEquals(5, headers.size());
		assertEquals(encoding, headers.getValue("Authorization"));
		
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	
}
