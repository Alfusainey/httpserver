package com.adobe.assignment.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A Wrapper class for a java.io.InputStream. This class wraps the 
 * underlying InputStream into a buffer and reads from the buffer, saving
 * time from reading from the underlaying input stream.
 * 
 * @author Alfusainey Jallow, University of the Gambia.
 *
 */
public class HttpInputStream {

	private final BufferedReader bufferedReader;
	
	public HttpInputStream(InputStream inputStream) {
		this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	}

	public String readHttpLine() throws IOException {
		return bufferedReader.readLine();
	}

	public boolean ready() throws IOException {
		return bufferedReader.ready();
	}
}
