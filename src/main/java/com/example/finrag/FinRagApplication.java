package com.example.finrag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FinRagApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinRagApplication.class, args);
    }
}