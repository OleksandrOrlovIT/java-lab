package ua.orlov.gymtrainerworkload.controller.cucumber.configuration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest
public abstract class AbstractIntegrationTest {

    private static MongoDBContainer mongoDBContainer;

    @BeforeAll
    static void startContainer() {
        mongoDBContainer = new MongoDBContainer("mongo:6.0");
        mongoDBContainer.start();
        System.setProperty("MONGODB_URI", mongoDBContainer.getReplicaSetUrl());
    }

    @AfterAll
    static void stopContainer() {
        if (mongoDBContainer != null) {
            mongoDBContainer.stop();
        }
    }
}
