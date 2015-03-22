An extensible HTTP server implementation written in Java. It is extensible in the sense that different HttpMethodHandler implementations can be configured to work with the server. The server currently supports only Get and DELETE HTTP requests. The HttpServer uses thread-pool concurrency model offerred by the java.util concurrency package to execute connection requests coming from http clients.

The tests folder contains some test that tests the implementation of this HTTP server. Note, that this is far from a production ready server, so please avoid:). The implementation uses the http-server-example from David Bernstein's lecture on Network Applications Development and can be found here [0]. I have extended his implementation to provide the following:

a. A pluggable/generic server implementation with support for any type of HTTP requests. different http handler implementations can be configured using the XML configuration file found in main/resources folder. 

c. Using a properties file to store the HttpServer's webroot, port and host configurations. A properties file is also used to configure HTTP statues phrases used by the HttpResponse class.

b. Using maven for dependency management and improve folder structure.

c. Using slf4j for logging.

Installation

This project is a maven project and can be installed using -- mvn clean install

Test classes

The HttpServerTest class test our multithreaded server implementation while the ServerConfigTest tests the ServerConfig class that is responsible for parsing all configuration files needed by the HttpServer during startup

[0] https://users.cs.jmu.edu/bernstdh/web/common/lectures/slides_http-server-example_java.php
