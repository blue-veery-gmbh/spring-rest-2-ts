package io.mincong.shop.rest;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.util.logging.Logger;

public class MyRequestFilter implements ContainerRequestFilter {

  private static final Logger logger = Logger.getLogger(MyRequestFilter.class.getName());

  @Override
  public ContainerRequest filter(ContainerRequest request) {
    logger.info("filter");
    return request;
  }
}
