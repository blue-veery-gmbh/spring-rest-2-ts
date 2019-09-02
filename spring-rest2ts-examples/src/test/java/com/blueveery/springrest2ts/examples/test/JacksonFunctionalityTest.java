package com.blueveery.springrest2ts.examples.test;

import com.blueveery.springrest2ts.examples.model.ProductDTO;
import com.blueveery.springrest2ts.examples.model.enums.OrderDeliveryStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class JacksonFunctionalityTest {

    @Test
    public void serializeProductDTO() throws IOException {
        ProductDTO productDTO = new ProductDTO();
        productDTO.expirationDate = new Date();
        ObjectMapper mapper = new ObjectMapper();

        String productAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(productDTO);
        System.out.println(productAsString);

        ProductDTO newProductDTO = mapper.readValue(productAsString, ProductDTO.class);
    }

    @Test
    public void serializeOrderDeliveryStatus() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String productAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(OrderDeliveryStatus.DELIVERED);
        System.out.println(productAsString);
    }
}
