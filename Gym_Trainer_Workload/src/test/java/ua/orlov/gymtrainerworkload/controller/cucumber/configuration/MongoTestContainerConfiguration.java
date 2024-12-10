package ua.orlov.gymtrainerworkload.controller.cucumber.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.MongoDBContainer;

@TestConfiguration
public class MongoTestContainerConfiguration {

    @Bean
    @Primary
    public MongoDBContainer mongoDBContainer() {
        MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");
        mongoDBContainer.start();
        return mongoDBContainer;
    }
}
