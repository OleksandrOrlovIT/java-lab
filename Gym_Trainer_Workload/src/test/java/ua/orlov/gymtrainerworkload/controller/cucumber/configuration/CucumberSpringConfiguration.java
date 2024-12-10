package ua.orlov.gymtrainerworkload.controller.cucumber.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ua.orlov.gymtrainerworkload.GymTrainerWorkloadApplication;
import ua.orlov.gymtrainerworkload.dto.TrainerWorkload;
import ua.orlov.gymtrainerworkload.model.HttpRequest;
import ua.orlov.gymtrainerworkload.service.message.MessageReceiver;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@ContextConfiguration(classes = GymTrainerWorkloadApplication.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration extends AbstractIntegrationTest {

    @LocalServerPort
    protected int randomServerPort;

    @Autowired
    protected CloseableHttpClient httpClient;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private MessageReceiver messageReceiver;

    private CloseableHttpResponse response;

    @Autowired
    private MongoTemplate mongoTemplate;

    @After
    public void tearDown() throws IOException {
        response.close();
    }

    public HttpRequest createHttpRequest(String uri, String method) {
        return new HttpRequest("https://localhost:" + randomServerPort + uri, method);
    }

    public void executeRequest(HttpRequest request) throws Exception {
        response = httpClient.execute(request);
    }

    public CloseableHttpResponse getCurrentResponse() {
        return response;
    }

    @Test
    void testMongoDBConnection() {
        // Insert and query a test document
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainerUsername("user");
        mongoTemplate.save(trainerWorkload, "testCollection");
        TrainerWorkload retrievedDocument = mongoTemplate.findById("testId", TrainerWorkload.class, "testCollection");

        assertThat(retrievedDocument).isNotNull();
        assertThat(retrievedDocument.getTrainerUsername()).isEqualTo("user");
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
