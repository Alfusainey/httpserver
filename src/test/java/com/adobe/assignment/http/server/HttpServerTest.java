package com.adobe.assignment.http.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.TestCase;
import net.iharder.Base64;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.junit.Test;

import com.adobe.assignment.http.HttpResponse;

public class HttpServerTest extends TestCase {

	private HttpClient client;
	private HostConfiguration hostConfig;
	
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 8080;
	private static final int MAX_CONN_PER_HOST = 50;
	private static final int MAX_TOTAL_CONN = 50;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		MultiThreadedHttpConnectionManager conMgr = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();		
		
		hostConfig = new HostConfiguration();
		hostConfig.setHost(DEFAULT_HOST, DEFAULT_PORT);
		
		params.setMaxConnectionsPerHost(hostConfig, MAX_CONN_PER_HOST);
		params.setMaxTotalConnections(MAX_TOTAL_CONN);
		conMgr.setParams(params);
		
		client = new HttpClient(conMgr);
		client.setHostConfiguration(hostConfig);		
	}

	//-----------------------------------------< Single request test >---
	/**
	 * A single GET request to retrieve a resource. The index.html resource
	 * exist in the server's web-root directory and should be retrieved
	 * successfully.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetExistingRequest() throws Exception {
		String uri = "/index.html";
		GetMethod method = new GetMethod(uri);
		try {
			int status = client.executeMethod(method);
			assertEquals(HttpResponse.SC_OK, status);
		} finally {
			method.releaseConnection();
		}

	}

	/**
	 * Note that the HTTP DELETE implementation does not
	 * not remove the resource from the server.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetWithAuthentication() throws Exception {
		String uri = "/index.html";
		DeleteMethod method = new DeleteMethod(uri);
		try {
			// Execute a DELETE request without the Authorization header set.
			int status = client.executeMethod(method);
			assertEquals(HttpResponse.SC_UNAUTHORIZED, status);
			
			String encoding = Base64.encodeBytes("username:password".getBytes());
			method.setRequestHeader(new Header("Authorization", "Basic "+encoding));

			// Execute a DELETE request with the Authorization header set.
			status = client.executeMethod(method);
			assertEquals(HttpResponse.SC_OK, status);
		} finally {
			method.releaseConnection();
		}
	}
	
	/**
	 * Test getting a resource that does not exist on the server. In this case,
	 * a status code of 404 (NOT FOUND) should be returned.
	 * 
	 */
	@Test
	public void testGetNonExistingResource() throws Exception {
		GetMethod gm = new GetMethod("/alfu.html");
		int status = client.executeMethod(gm);
		
		assertEquals(HttpResponse.SC_NOT_FOUND, status);
	}

	/**
	 * The HTTP Options method is not yet implemented and hence unsupported by
	 * the server. We expect that the server returns a 501 status code
	 * indicating that the action is not implemented.
	 */
	@Test
	public void testMethodNotImplemented() throws Exception {
		String uri = "/";
		OptionsMethod om = new OptionsMethod(uri);
		int status = client.executeMethod(om);
		
		assertEquals(HttpResponse.SC_NOT_IMPLEMENTED, status);
	}

	// ----------------------------------------------< Concurrent request Test >---
	/**
	 * Test concurrent GET requests for a resource that exist on the
	 * server-side. All the requests should return a 200 status code indicating
	 * success for each request.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConcurrentGetRequests() throws Exception {
		// All get requests to /index.html in the server's web-root should
		// return success.
		ConcurrentGet getTask = new ConcurrentGet("index.html");
		submit(1000, HttpResponse.SC_OK, getTask);
	}

	/**
	 * All executing threads should return a 501 (Not Implemented) status code
	 * because the HttpServer does not yet provide this support.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConcurrentOptionsRequests() throws Exception {
		ConcurrentOptions optionsTask = new ConcurrentOptions("/index.html");
		submit(500, HttpResponse.SC_NOT_IMPLEMENTED, optionsTask);
	}

	/**
	 * Submits the task to the executor service.
	 * 
	 * @param threadCount         The number of threads in the pool
	 * @param expectedStatus	  The expected status code of each thread
	 * @param task				  The actual task to submit to the executor service
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void submit(int threadCount, int expectedStatus, Callable<Integer> task) throws Exception {		
		List<Callable<Integer>> tasks = Collections.nCopies(threadCount, task);

		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		List<Future<Integer>> futures = executorService.invokeAll(tasks);

		// # of successfully executed tasks(futures) should match threadCount.
		assertEquals(threadCount, futures.size());

		List<Integer> statusCodes = new ArrayList<Integer>(futures.size());
		for (Future<Integer> future : futures) {
			if (future.isDone()) {
				statusCodes.add(future.get());
			}
		}

		assertEquals(threadCount, statusCodes.size());

		for (int actualStatus : statusCodes) {
			assertEquals(expectedStatus, actualStatus);
		}

	}

	// ------------------------------------------------< Tester classes >---
	
	/**
	 * Concurrent GET request execution.
	 *
	 */
	private final class ConcurrentGet implements Callable<Integer> {

		private final String uri;
		ConcurrentGet(String uri) {
			this.uri = uri;
		}
		
 		public Integer call() throws Exception {
			HttpMethodBase method = null;
			try {
				method = new GetMethod(uri);
				int status = client.executeMethod(method);
				return status;
			} finally {
				if (method != null) {
					method.releaseConnection();
				}
			}
		}
	}
	
	/**
	 * Concurrent OPTIONS request execution.
	 *
	 */
	private final class ConcurrentOptions implements Callable<Integer> {

		private final String uri;
		
		ConcurrentOptions(String uri) {
			this.uri = uri;
		}
		
		public Integer call() throws Exception {
			HttpMethodBase method = null;
			try {
				method = new OptionsMethod(uri);
				int status = client.executeMethod(method);
				return status;
			} finally {
				if (method != null) {
					method.releaseConnection();
				}
			}
		}
		
	}
}