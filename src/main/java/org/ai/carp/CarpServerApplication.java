package org.ai.carp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableMongoRepositories("org.ai.carp.model")
public class CarpServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(CarpServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CarpServerApplication.class, args);
    }

}
