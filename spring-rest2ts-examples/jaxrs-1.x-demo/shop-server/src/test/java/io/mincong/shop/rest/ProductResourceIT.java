package io.mincong.shop.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import io.mincong.shop.rest.dto.Product;
import io.mincong.shop.rest.dto.ProductCreated;
import io.mincong.shop.rest.dto.ShopExceptionData;
import javax.ws.rs.core.MediaType;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Product resource integration test.
 *
 * @author Mincong Huang
 */
public class ProductResourceIT {

  private HttpServer server;

  private WebResource wr;

  @Before
  public void setUp() throws Exception {
    server = Main.startServer();

    ClientConfig cc = new DefaultClientConfig();
    cc.getSingletons().add(ShopApplication.newJacksonJsonProvider());
    wr = Client.create(cc).resource(Main.BASE_URI.resolve("products"));
    wr.addFilter(new ClientFilter() {
      @Override
      public ClientResponse handle(ClientRequest request) {
        ClientResponse response = getNext().handle(request);
        if (response.getStatus() >= 400) {
          ShopExceptionData data = response.getEntity(ShopExceptionData.class);
          throw new ShopException(response.getStatus(), data);
        }
        return response;
      }
    });
  }

  @After
  public void tearDown() {
    server.stop();
  }

  @Test
  public void getProduct_asString() {
    String s = wr.path("123").get(String.class);
    assertThat(s).isEqualTo("{\"id\":\"123\",\"name\":\"foo\"}");
  }

  @Test
  public void getProduct() {
    Product p = wr.path("123").get(Product.class);
    assertThat(p).isEqualTo(new Product("123", "foo"));
  }

  @Test
  public void getProduct_invalidId() {
    try {
      wr.path("123!").get(Product.class);
      fail("GET should raise an exception");
    } catch (ShopException e) {
      assertThat(e.getData().getErrorCode()).isEqualTo(ShopError.PRODUCT_ID_INVALID.code);
      assertThat(e.getData().getErrorMessage()).isEqualTo(ShopError.PRODUCT_ID_INVALID.message);
    }
  }

  @Test
  public void createProduct() {
    Product p = new Product("123", "foo");
    ProductCreated c =
        wr.type(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .post(ProductCreated.class, p);
    assertThat(c.getUrl()).endsWith(p.getId());
    assertThat(c.getCreated()).isNotNull();
  }
}
