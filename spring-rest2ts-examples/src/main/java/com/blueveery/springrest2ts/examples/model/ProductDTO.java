package com.blueveery.springrest2ts.examples.model;

import com.blueveery.springrest2ts.examples.model.core.BaseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDTO extends BaseDTO {
    private String name;
    private BigDecimal price;
    private LocalDateTime expiryDate;
}
