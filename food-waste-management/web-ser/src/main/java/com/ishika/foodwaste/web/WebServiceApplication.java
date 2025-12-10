package com.ishika.foodwaste.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.ishika.foodwaste"})
public class WebServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebServiceApplication.class, args);
    }
}
