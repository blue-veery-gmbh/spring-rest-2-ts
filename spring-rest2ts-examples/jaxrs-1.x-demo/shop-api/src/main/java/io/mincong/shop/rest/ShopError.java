package io.mincong.shop.rest;

import javax.ws.rs.core.Response.Status;

public enum ShopError {
  PRODUCT_ID_INVALID(Status.BAD_REQUEST, "shop.product.invalidId", "Invalid product ID.");

  public final Status status;
  public final String code;
  public final String message;

  ShopError(Status status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }
}
