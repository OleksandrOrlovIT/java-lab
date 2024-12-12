package ua.orlov.gymintegrationaltesting.integration.cucumber.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.orlov.gymintegrationaltesting.GymIntegrationalTestingApplication;
import ua.orlov.gymintegrationaltesting.model.HttpRequest;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@CucumberContextConfiguration
@ContextConfiguration(classes = GymIntegrationalTestingApplication.class)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {

    @Container
    static ComposeContainer environment = new ComposeContainer(new File("compose.yml"))
            .withLocalCompose(true)
            .withExposedService("gym-trainer-workload", 8080,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(40)))
            .withExposedService("spring-boot-gym", 8443,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(40)));

    private static String workloadUrl;
    private static String gymUrl;

    @BeforeAll
    public static void setUpUrls() {
        environment.start();
        workloadUrl = "https://" + environment.getServiceHost("gym-trainer-workload", 8080)
                + ":" + environment.getServicePort("gym-trainer-workload", 8080);

        gymUrl = "https://" + environment.getServiceHost("spring-boot-gym", 8443)
                + ":" + environment.getServicePort("spring-boot-gym", 8443);
    }

    @Autowired
    protected CloseableHttpClient httpClient;

    @Autowired
    protected ObjectMapper objectMapper;

    private static CloseableHttpResponse response;

    @After
    public void cleanUp() throws IOException {
        if(response != null) {
            response.close();
        }
    }

//    @AfterAll
//    public static void tearDownEnvironment() throws InterruptedException {
//        // Comment this out during debugging to keep containers running
//        // environment.stop();
//        Thread.sleep(1000000);
//    }

    public HttpRequest createSpringGymHttpRequest(String uri, String method) {
        return new HttpRequest(gymUrl + uri, method);
    }

    public HttpRequest createTrainerWorkloadHttpRequest(String uri, String method) {
        return new HttpRequest(workloadUrl + uri, method);
    }

    public void executeRequest(HttpRequest request) throws Exception {
        if(response != null) {
            response.close();
        }
        response = httpClient.execute(request);
    }

    public CloseableHttpResponse getCurrentResponse() {
        return response;
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertEquals(expectedStatus, response.getStatusLine().getStatusCode());
    }

    @And("the response body should not be null")
    public void theResponseBodyShouldNotBeNull() throws Exception {
        assertNotNull(EntityUtils.toString(response.getEntity()));
    }

    @And("the exception response body should contain message {string} and status {string}")
    public void theResponseBodyShouldContainMessageAndStatus(String message, String status) throws Exception {
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String actualMessage = jsonNode.get("message").asText();
        String actualStatus = jsonNode.get("status").asText();

        assertEquals(message, actualMessage, "Message field mismatch");
        assertEquals(status, actualStatus, "Status field mismatch");
    }

    @And("the response body should equal {string}")
    public void theResponseBodyShouldEqual(String expectedResponse) throws Exception {
        assertEquals(expectedResponse, EntityUtils.toString(response.getEntity()));
    }

    @And("the response body should contain {string}")
    public void theResponseBodyShouldContainSeveralStrings(String expectedResponse) throws Exception {
        String responseBody = EntityUtils.toString(response.getEntity());

        for(String s : expectedResponse.split(";")) {
            assertTrue(responseBody.contains(s));
        }
    }
}
