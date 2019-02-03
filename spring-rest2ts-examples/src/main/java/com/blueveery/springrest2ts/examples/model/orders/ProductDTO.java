package com.blueveery.springrest2ts.examples.model.orders;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class ProductDTO extends BaseDTO {
    private String name;
    private BigDecimal price;
    private ZonedDateTime expiryDate;
}
