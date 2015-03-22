package com.adobe.assignment.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpOutputStream {

	private final DataOutputStream outputStream;
	private static final String HEADER_SEPARATOR = ":";
	
	public HttpOutputStream(OutputStream outputStream) {
		this.outputStream = new DataOutputStream(outputStream);
	}
	
	public void write(byte[] b) throws IOException {
		outputStream.write(b);
	}
	
	public void flush() throws IOException {
		outputStream.flush();
	}
	
	public void close() throws IOException {
		outputStream.close();
	}

	public void printHttpLine(String string) throws IOException {
		outputStream.write(string.getBytes());
	}

	public void printHeaderLine(String name, String value) throws IOException {		
		String hl = name+HEADER_SEPARATOR+" "+value;
		print(hl);
		printEOL();
	}

	public void printEOL() throws IOException {
		outputStream.write(HttpConstants.HTTP_LINE_SEPARATOR.getBytes());
	}

	public void print(String string) throws IOException {		
		outputStream.write(string.getBytes());
	}
		
}
