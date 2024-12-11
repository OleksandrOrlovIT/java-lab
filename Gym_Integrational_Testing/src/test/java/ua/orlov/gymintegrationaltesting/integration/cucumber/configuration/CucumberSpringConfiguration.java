package ua.orlov.gymintegrationaltesting.integration.cucumber.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ua.orlov.gymintegrationaltesting.GymIntegrationalTestingApplication;
import ua.orlov.gymintegrationaltesting.integration.IntegrationTest;
import ua.orlov.gymintegrationaltesting.model.HttpRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@ContextConfiguration(classes = GymIntegrationalTestingApplication.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {

    @Autowired
    protected CloseableHttpClient httpClient;

    @Autowired
    protected ObjectMapper objectMapper;

    private CloseableHttpResponse response;

    private static final String workloadUrl = IntegrationTest.getWorkloadUrl();
    private static final String gymUrl = IntegrationTest.getGymUrl();

    @After
    public void tearDown() throws IOException {
        response.close();
    }

    public HttpRequest createSpringGymHttpRequest(String uri, String method) {
        return new HttpRequest(gymUrl + uri, method);
    }

    public HttpRequest createTrainerWorkloadHttpRequest(String uri, String method) {
        return new HttpRequest(gymUrl + uri, method);
    }

    public void executeRequest(HttpRequest request) throws Exception {
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
