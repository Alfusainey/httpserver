A Homework HTTP Server Implementation
---

An extensible HTTP server implementation written in Java. The server is made extensible since different `HttpMethodHandler` implementations can be dynamically configured to work with the server. Having the server be extended to support more http methods merely via XML configuration has the advantage of adding functionality with zero code modification. However, the server currently supports only GET and DELETE HTTP requests. Moreover, the server uses the thread-pool concurrency model offerred by the `java.util` concurrency package to service connection requests from http clients. This differs from the thread-per-request model that creates a single thread for each and every request sent to the server. The thread-pool model reduces the overhead in creating threads and is thus more efficient.

Note, that this is far from a production ready server. It is only a homework project so please avoid:).

The __src/main/java__ folder contains the java source files used to realize the implementation while the __src/test/java__ folder contains some tests for testing the multithreaded server implementation. The implementation uses the http server example from __David Bernstein's__ lecture on [Design and Implementation of an HTTP Server](https://users.cs.jmu.edu/bernstdh/web/common/lectures/slides_http-server-example_java.php). I have extended his implementation to provide the following:

1. A pluggable/generic server implementation with support for any type of HTTP requests. Different http handler implementations can be configured using the XML configuration file found in main/resources folder. The `ServerConfig` class is mainly used to parse the XML file during server startup.

2. Introduction of two `java.util.properties` configuration files. They are the *webserver.properties* and *statuscode.properties* files. The former is used to store needed config parameters for the HttpServer. These params include the server's `port`, `host` and `web-root` directory. The later is used to store HTTP status codes together with their corresponding status pharases which is needed and used in the `HttpResponse` class.

3. Using maven for dependency management and to provide a maven like folder sstructure.

4. Using slf4j for logging instead of the normal Java logging library.
 
Installation:
---

To install this project locally, do the following:

1. Clone the project: `$ git clone https://github.com/Alfusainey/httpserver`

2. Install the project using maven: `$ mvn clean install`

Libraries used in this project:
---
The following libraries are used to realize this project:

1. `junit 4.0`: Used for unit testing. See `HttpServerTest` and `ServerConfigTest` classes located in __src/test/java__ folder for details.

2. `httpclient 4.3.6`: The HTTP client mainly use to test the multithreaded.server implementation
3. `jackrabbit-webdav 2.8.0`: The DomUtil class used in the `ServerConfig` class to parse the XML configuration for the different HTTP handlers is from this library.
4. `net.iharder.base64`: This is used to encode `java.lang.String` instances to base64 format.
5. `slf4j` for logging.
6. `ch.qos.logback`: for providing log-back support.


Tests:
---

1. `HttpServerTest` class test the multithreaded server implementation. The server is tested under both single and multi-threaded case scenarios. It contains six test-cases.

2. `ServerConfigTest` tests the ServerConfig class that is responsible for parsing all configuration files needed by the HttpServer during startup.

