package io.mincong.shop.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

/** @author Mincong Huang */
public class ProductCreated {

  @JsonProperty("created")
  private final ZonedDateTime created;

  @JsonProperty("url")
  private final String url;

  public ProductCreated(String url) {
    this.url = url;
    this.created = ZonedDateTime.now();
  }

  @JsonCreator
  public ProductCreated(
      @JsonProperty("url") String url, @JsonProperty("created") ZonedDateTime created) {
    this.url = url;
    this.created = created;
  }

  public String getUrl() {
    return url;
  }

  public ZonedDateTime getCreated() {
    return created;
  }
}
