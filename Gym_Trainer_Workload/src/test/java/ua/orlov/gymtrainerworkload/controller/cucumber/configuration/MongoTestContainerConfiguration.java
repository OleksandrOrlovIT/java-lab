package ua.orlov.gymtrainerworkload.controller.cucumber.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;

@Configuration
@ActiveProfiles("test")
public class MongoTestContainerConfiguration {

    private static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer("mongo:6.0");

    static {
        MONGO_DB_CONTAINER.start();
        System.setProperty("MONGODB_URI", MONGO_DB_CONTAINER.getReplicaSetUrl());
    }

    @Bean
    @Primary
    public MongoDBContainer mongoDBContainer() {
        return MONGO_DB_CONTAINER;
    }
}
