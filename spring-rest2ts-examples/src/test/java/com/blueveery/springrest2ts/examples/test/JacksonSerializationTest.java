package com.blueveery.springrest2ts.examples.test;

import com.blueveery.springrest2ts.examples.model.CategoryDTO;
import com.blueveery.springrest2ts.examples.model.enums.OrderDeliveryStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class JacksonSerializationTest {

    @Test
    public void serializeProductDTO() throws IOException, URISyntaxException {
        CategoryDTO.ProductDTO productDTO = new CategoryDTO.ProductDTO();
        productDTO.expirationDate = new Date();
        productDTO.websiteURI = new URI("/path");
        ObjectMapper mapper = new ObjectMapper();

        String productAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(productDTO);
        System.out.println(productAsString);

        CategoryDTO.ProductDTO newProductDTO = mapper.readValue(productAsString, CategoryDTO.ProductDTO.class);
    }

    @Test
    public void serializeOrderDeliveryStatus() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String productAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(OrderDeliveryStatus.DELIVERED);
        System.out.println(productAsString);
    }

}
