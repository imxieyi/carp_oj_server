package org.ai.carp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarpServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(CarpServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CarpServerApplication.class, args);
    }

}
