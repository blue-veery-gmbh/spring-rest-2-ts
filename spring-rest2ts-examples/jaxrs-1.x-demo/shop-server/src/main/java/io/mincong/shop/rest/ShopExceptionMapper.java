package io.mincong.shop.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ShopExceptionMapper implements ExceptionMapper<ShopException> {

  private static final Logger logger = Logger.getLogger(ShopException.class.getName());

  @Override
  public Response toResponse(ShopException ex) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String entity = mapper.writeValueAsString(ex.getData());
      return Response.status(ex.getStatusCode())
          .type(MediaType.APPLICATION_JSON)
          .entity(entity)
          .build();
    } catch (JsonProcessingException e) {
      logger.log(Level.SEVERE, e.getMessage(), e.getCause());
      throw new IllegalStateException("This should never happen.", e);
    }
  }
}
