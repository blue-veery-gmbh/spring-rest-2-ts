package io.mincong.shop.rest;

import io.mincong.shop.rest.dto.Product;
import io.mincong.shop.rest.dto.ProductCreated;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.regex.Pattern;

public class ProductResourceImpl implements ProductResource {

  private static final Pattern PATTERN_ID = Pattern.compile("\\p{Alnum}+");

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Product getProduct(@PathParam("id") String id) {
    if (id == null || !PATTERN_ID.matcher(id).matches()) {
      throw new ShopException(ShopError.PRODUCT_ID_INVALID);
    }
    return new Product(id, "foo");
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public ProductCreated createProduct(Product p) {
    String url = Main.BASE_URI.resolve("products").resolve(p.getId()).toString();
    return new ProductCreated(url);
  }

}
