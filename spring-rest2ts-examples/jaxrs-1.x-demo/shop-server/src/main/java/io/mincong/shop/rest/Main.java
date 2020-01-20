package io.mincong.shop.rest;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.jersey.api.core.ResourceConfig;
import java.io.IOException;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;

// https://github.com/jersey/jersey-1.x/blob/master/samples/jacksonjsonprovider/src/main/java/com/sun/jersey/samples/jacksonjsonprovider/Main.java
public class Main {

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost/api/").port(8080).build();
  }

  static final URI BASE_URI = getBaseURI();

  @SuppressWarnings("unchecked")
  static HttpServer startServer() throws IOException {
    ResourceConfig rc = new ApplicationAdapter(new ShopApplication());
    rc.getProperties().put("foo", "bar");
    rc.getContainerRequestFilters().add(new MyRequestFilter());
    return GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
  }

  public static void main(String[] args) throws IOException {
    System.out.println("Starting grizzly...");
    HttpServer httpServer = startServer();
    System.out.printf("Jersey app started with WADL available at %sapplication.wadl%n", BASE_URI);
    System.out.println("Hit enter to stop it...");
    System.in.read();
    httpServer.stop();
  }
}
