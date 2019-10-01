package com.blueveery.springrest2ts.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ExampleApplication.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8999"));
        app.run(args);
    }
}
