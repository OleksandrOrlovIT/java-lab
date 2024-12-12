package ua.orlov.gymintegrationaltesting.integration.cucumber.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import ua.orlov.gymintegrationaltesting.integration.cucumber.configuration.CucumberSpringConfiguration;
import ua.orlov.gymintegrationaltesting.model.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class SpringGymStep {

    @Autowired
    private CucumberSpringConfiguration configuration;

    @Autowired
    private ObjectMapper objectMapper;

    private String userPassword;

    private String authenticationToken;

    @When("user sends request to monolith to create trainee with body:")
    public void registerTraineeWithValidBody(String jsonPayload) throws Exception {
        StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createSpringGymHttpRequest("/api/v1/trainee/create", "POST");
        post.setEntity(entity);

        configuration.executeRequest(post);
    }

    @When("user sends request to monolith to create trainer and get trainer password with body:")
    public void registerTrainerWithValidBody(String jsonPayload) throws Exception {
        StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createSpringGymHttpRequest("/api/v1/trainer", "POST");
        post.setEntity(entity);

        configuration.executeRequest(post);

        CloseableHttpResponse response = configuration.getCurrentResponse();
        Map mapResponse = objectMapper.readValue(response.getEntity().getContent(), Map.class);
        userPassword = (String) mapResponse.get("password");
    }

    @When("user sends request to monolith to login as a trainer and get auth token with username = {string} and saved password")
    public void loginUserWithUsernameAndSavedPassword(String username) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", userPassword);

        HttpRequest post = configuration.createSpringGymHttpRequest("/api/v1/session", "POST");
        post.setEntity(new StringEntity(objectMapper.writeValueAsString(params), ContentType.APPLICATION_JSON));

        configuration.executeRequest(post);

        CloseableHttpResponse response = configuration.getCurrentResponse();
        Map mapResponse = objectMapper.readValue(response.getEntity().getContent(), Map.class);
        authenticationToken = (String) mapResponse.get("token");
    }

    @When("authorized user sends request to monolith to create training with body:")
    public void registerTrainingWithValidBody(String jsonPayload) throws Exception {
        StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createSpringGymHttpRequest("/api/v1/training", "POST");
        post.setEntity(entity);
        post.setHeader("Authorization", "Bearer " + authenticationToken);

        configuration.executeRequest(post);
    }

    @And("wait for 5 seconds")
    public void waitFor5Seconds() throws Exception {
        Thread.sleep(5000);
    }
}
