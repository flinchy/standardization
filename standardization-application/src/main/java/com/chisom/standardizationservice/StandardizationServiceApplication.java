package com.chisom.standardizationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class StandardizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StandardizationServiceApplication.class, args);
    }

}
