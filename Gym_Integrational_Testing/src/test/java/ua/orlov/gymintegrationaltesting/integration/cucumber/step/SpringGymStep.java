package ua.orlov.gymintegrationaltesting.integration.cucumber.step;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ua.orlov.gymintegrationaltesting.integration.cucumber.configuration.CucumberSpringConfiguration;
import ua.orlov.gymintegrationaltesting.integration.cucumber.model.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class SpringGymStep {

    @Autowired
    private CucumberSpringConfiguration configuration;

    @Autowired
    private ObjectMapper objectMapper;

    private static String userPassword;

    private static String authenticationToken;

    private static long trainingId;

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

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(params), ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createSpringGymHttpRequest("/api/v1/session", "POST");
        post.setEntity(entity);

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

    @When("authorized user sends request to monolith to delete training with body:")
    public void deleteTrainingWithValidBody(String jsonPayload) throws Exception {
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

    @When("user sends request to monolith to logout")
    public void theUserLogOut() throws Exception {
        HttpRequest post = configuration.createSpringGymHttpRequest("/api/v1/logout", "POST");
        post.setHeader("Authorization", "Bearer " + authenticationToken);

        configuration.executeRequest(post);
    }

    @When("the user sends request to get trainings by trainer and date to get id of training with body:")
    public void getTrainingsByTrainerAndDateWithBody(String jsonPayload) throws Exception {
        StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createSpringGymHttpRequest("/api/v1/training/trainer", "POST");
        post.setEntity(entity);
        post.setHeader("Authorization", "Bearer " + authenticationToken);

        configuration.executeRequest(post);

        CloseableHttpResponse response = configuration.getCurrentResponse();
        String responseString = EntityUtils.toString(response.getEntity());
        JsonNode rootNode = objectMapper.readTree(responseString);

        JsonNode firstTrainingNode = rootNode.get(0);
        trainingId = firstTrainingNode.get("trainingId").asLong();
    }

    @When("authorized user sends request to monolith to delete training with savedTrainingId")
    public void deleteTrainingWithSavedTrainingId() throws Exception {
        HttpRequest delete = configuration
                .createSpringGymHttpRequest("/api/v1/training?trainingId=" + trainingId, "DELETE");

        delete.setHeader("Authorization", "Bearer " + authenticationToken);

        configuration.executeRequest(delete);
    }
}
