package io.mincong.shop.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShopExceptionData {

  @JsonProperty("code")
  private final String errorCode;

  @JsonProperty("msg")
  private final String errorMessage;

  @JsonCreator
  public ShopExceptionData(@JsonProperty("code") String code, @JsonProperty("msg") String msg) {
    this.errorCode = code;
    this.errorMessage = msg;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
