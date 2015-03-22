package com.adobe.assignment.http;

public interface HttpConstants {

	//--------------------------------< Headers (Names and Value Constants) >---
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    public static final String HTTP_LINE_SEPARATOR = "\r\n";
    
    public static final String HTTP_VERSION = "HTTP/1.1";
    
    //--------------------------------< Methods constants >---
    public static final int METHOD_GET_CODE = 1;
    public static final String METHOD_GET = "GET";
    
    public static final int METHOD_PUT_CODE = METHOD_GET_CODE + 1;
    public static final String METHOD_PUT = "PUT";
    
    public static final int METHOD_DELETE_CODE = METHOD_PUT_CODE + 1;
    public static final String METHOD_DELETE = "DELETE";
    
    public static final String METHOD_OPTIONS = "OPTIONS";
    
    
}