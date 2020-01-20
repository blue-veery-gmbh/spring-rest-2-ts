package io.mincong.shop.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

public class ShopApplication extends Application {

  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> set = new HashSet<>();
    set.add(ProductResourceImpl.class);
    return set;
  }

  @Override
  public Set<Object> getSingletons() {
    Set<Object> set = new HashSet<>();
    set.add(new ShopExceptionMapper());
    set.add(newJacksonJsonProvider());
    return set;
  }

  static JacksonJsonProvider newJacksonJsonProvider() {
    ObjectMapper mapper =
        new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule()); // new module, NOT JSR310Module
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.setDateFormat(new StdDateFormat());
    return new JacksonJsonProvider(mapper);
  }
}
