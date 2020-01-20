package io.mincong.shop.rest;

import io.mincong.shop.rest.dto.ShopExceptionData;

public class ShopException extends RuntimeException {

  private ShopExceptionData data;

  private int statusCode;

  public ShopException(ShopError err) {
    this.data = new ShopExceptionData(err.code, err.message);
    this.statusCode = err.status.getStatusCode();
  }

  public ShopException(int statusCode, ShopExceptionData data) {
    this.data = data;
    this.statusCode = statusCode;
  }

  public ShopExceptionData getData() {
    return data;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
