package ua.orlov.springcoregym.controller.cucumber.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ua.orlov.springcoregym.SpringCoreGymApplication;
import ua.orlov.springcoregym.controller.integration.config.LoginComponent;
import ua.orlov.springcoregym.dto.trainingtype.TrainingTypeResponse;
import ua.orlov.springcoregym.model.HttpRequest;
import ua.orlov.springcoregym.service.message.MessageSender;
import ua.orlov.springcoregym.service.security.LoginAttemptService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@ContextConfiguration(classes = SpringCoreGymApplication.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {

    @LocalServerPort
    protected int randomServerPort;

    @Autowired
    protected CloseableHttpClient httpClient;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @MockBean
    private MessageSender messageSender;

    private LoginComponent loginComponent;

    private CloseableHttpResponse response;

    private String authToken;

    private Long specializationId;

    @Before
    public void setUp() {
        loginComponent = new LoginComponent(httpClient, objectMapper, randomServerPort);
    }

    @After
    public void tearDown() throws IOException {
        loginAttemptService.clearCache();
        response.close();
    }

    public HttpRequest createHttpRequest(String uri, String method) {
        return new HttpRequest("https://localhost:" + randomServerPort + uri, method);
    }

    public void executeRequest(HttpRequest request) throws Exception {
        response = httpClient.execute(request);
    }

    public void executeRequestWithAuthToken(HttpRequest request) throws Exception {
        request.setHeader("Authorization", "Bearer " + authToken);

        response = httpClient.execute(request);
    }

    public CloseableHttpResponse getCurrentResponse() {
        return response;
    }

    public Long getSpecializationId() {
        return specializationId;
    }

    @Given("the user gets all trainingTypes to get a specializationId")
    public void setSpecializationId() throws Exception {
        HttpRequest get = createHttpRequest("/api/v1/training/types", "GET");

        executeRequestWithAuthToken(get);

        TypeReference<List<TrainingTypeResponse>> typeReference = new TypeReference<>() {};
        List<TrainingTypeResponse> specializations = objectMapper.readValue(
                EntityUtils.toString(response.getEntity()), typeReference
        );

        specializationId = specializations.get(0).getId();
    }

    @Given("the user logs in with username {string} and password {string} to get token")
    public void userLogsInToGetToken(String username, String password) throws Exception {
        authToken = loginComponent.loginAsUser(username, password);
    }

    @Given("the user logs in with bad credentials username {string} password {string} with {int} times")
    public void userLogsInWithBadCredentials(String username, String password, int times) throws Exception {
        for(int i = 0; i < times; i++) {
            loginComponent.loginWithBadCredentials(username, password);
        }
    }

    @When("user sends request to logout")
    public void theUserLogOut() throws Exception {
        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/logout");
        post.setHeader("Authorization", "Bearer " + authToken);

        response = httpClient.execute(post);
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
