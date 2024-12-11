package ua.orlov.gymintegrationaltesting.integration;

import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

@Testcontainers
public class IntegrationTest {

    @Container
    static ComposeContainer environment = new ComposeContainer(new File("compose.yml"))
            .withLocalCompose(true)
            .withExposedService("gym-trainer-workload", 8080,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
            .withExposedService("spring-boot-gym", 8443,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));

    private static String workloadUrl;
    private static String gymUrl;

    static {
        workloadUrl = "https://" + environment.getServiceHost("gym-trainer-workload", 8080)
                + ":" + environment.getServicePort("gym-trainer-workload", 8080);

        gymUrl = "https://" + environment.getServiceHost("spring-boot-gym", 8443)
                + ":" + environment.getServicePort("spring-boot-gym", 8443);
    }

    public static String getWorkloadUrl() {
        return workloadUrl;
    }

    public static String getGymUrl() {
        return gymUrl;
    }

}
