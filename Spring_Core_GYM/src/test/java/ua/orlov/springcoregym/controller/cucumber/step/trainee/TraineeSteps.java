package ua.orlov.springcoregym.controller.cucumber.step.trainee;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ua.orlov.springcoregym.controller.cucumber.configuration.CucumberSpringConfiguration;
import ua.orlov.springcoregym.dto.trainee.*;
import ua.orlov.springcoregym.dto.trainer.TrainerResponse;
import ua.orlov.springcoregym.dto.user.UsernameIsActiveUser;
import ua.orlov.springcoregym.dto.user.UsernameUser;
import ua.orlov.springcoregym.model.HttpRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class TraineeSteps {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CucumberSpringConfiguration configuration;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before("@tagTraineeFeature")
    public void executeSetupSQL() {
        jdbcTemplate.execute("RUNSCRIPT FROM 'src/test/resources/sql/prune_tables.sql'");
        jdbcTemplate.execute("RUNSCRIPT FROM 'src/test/resources/sql/trainee/populate_encrypted_trainee.sql'");
    }

    @When("user register trainee without body")
    public void registerTraineeWithoutBody() throws Exception {
        HttpRequest post = configuration.createHttpRequest("/api/v1/trainee/create", "POST");

        configuration.executeRequest(post);
    }

    @When("user register trainee without any information")
    public void registerTraineeWithInvalidBody() throws Exception {
        TraineeRegister request = new TraineeRegister();

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/trainee/create", "POST");
        post.setEntity(entity);

        configuration.executeRequest(post);
    }

    @When("user registers trainee with the following details:")
    public void registerTraineeWithValidBody(String jsonPayload) throws Exception {
        StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/trainee/create", "POST");
        post.setEntity(entity);

        configuration.executeRequest(post);
    }

    @When("user get trainee without body")
    public void getTraineeWithoutBody() throws Exception {
        HttpRequest post = configuration.createHttpRequest("/api/v1/trainee/username", "POST");
        configuration.executeRequestWithAuthToken(post);
    }

    @When("user get trainee with empty body")
    public void getTraineeWithEmptyBody() throws Exception {
        UsernameUser request = new UsernameUser();
        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/trainee/username", "POST");
        post.setEntity(entity);

        configuration.executeRequestWithAuthToken(post);
    }

    @When("user get trainee with username {string}")
    public void getTraineeWithEmptyBody(String username) throws Exception {
        UsernameUser request = new UsernameUser(username);
        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/trainee/username", "POST");
        post.setEntity(entity);

        configuration.executeRequestWithAuthToken(post);
    }

    @When("user update trainee without body")
    public void updateTraineeWithoutBody() throws Exception {
        HttpRequest put = configuration.createHttpRequest("/api/v1/trainee", "PUT");
        configuration.executeRequestWithAuthToken(put);
    }

    @When("user update trainee with invalid body")
    public void updateTraineeWithInvalidBody() throws Exception {
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest put = configuration.createHttpRequest("/api/v1/trainee", "PUT");
        put.setEntity(entity);

        configuration.executeRequestWithAuthToken(put);
    }

    @When("user update trainee with a body:")
    public void updateTraineeWithBody(String jsonPayload) throws Exception {
        StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

        HttpRequest put = configuration.createHttpRequest("/api/v1/trainee", "PUT");
        put.setEntity(entity);

        configuration.executeRequestWithAuthToken(put);
    }

    @And("the trainee response should equal:")
    public void traineeFullResponseShouldEqual(String jsonPayload) throws Exception {
        TraineeFullUsernameResponse expectedResponse =
                objectMapper.readValue(jsonPayload, TraineeFullUsernameResponse.class);

        CloseableHttpResponse actualResponseJson = configuration.getCurrentResponse();
        TraineeFullUsernameResponse actualResponse =
                objectMapper.readValue(actualResponseJson.getEntity().getContent(), TraineeFullUsernameResponse.class);

        assertAll("Actual response doesn't equal expected response",
                () -> assertEquals(expectedResponse.isActive(), actualResponse.isActive()),
                () -> assertEquals(expectedResponse.getUsername(), actualResponse.getUsername()),
                () -> assertEquals(expectedResponse.getFirstName(), actualResponse.getFirstName()),
                () -> assertEquals(expectedResponse.getLastName(), actualResponse.getLastName()),
                () -> assertEquals(expectedResponse.getAddress(), actualResponse.getAddress()),
                () -> assertEquals(expectedResponse.getDateOfBirth(), actualResponse.getDateOfBirth()),
                () -> assertEquals(expectedResponse.getTrainers(), actualResponse.getTrainers()));
    }

    @When("user delete trainee without body")
    public void deleteTraineeWithoutBody() throws Exception {
        HttpRequest delete = configuration.createHttpRequest("/api/v1/trainee", "DELETE");
        configuration.executeRequestWithAuthToken(delete);
    }

    @When("user delete trainee with invalid body")
    public void deleteTraineeWithInvalidBody() throws Exception {
        UsernameUser request = new UsernameUser();
        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest delete = configuration.createHttpRequest("/api/v1/trainee", "DELETE");
        delete.setEntity(entity);

        configuration.executeRequestWithAuthToken(delete);
    }

    @When("user delete trainee with username {string}")
    public void deleteTraineeWithUsername(String username) throws Exception {
        UsernameUser request = new UsernameUser(username);
        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest delete = configuration.createHttpRequest("/api/v1/trainee", "DELETE");
        delete.setEntity(entity);

        configuration.executeRequestWithAuthToken(delete);
    }

    @When("user update trainee's trainers without body")
    public void updateTraineesTrainersWithoutBody() throws Exception {
        HttpRequest put = configuration.createHttpRequest("/api/v1/trainee/trainers", "PUT");
        configuration.executeRequestWithAuthToken(put);
    }

    @When("user update trainee's trainers with invalid body")
    public void updateTraineesTrainersWithInvalidBody() throws Exception {
        UpdateTraineeTrainersListRequest request = new UpdateTraineeTrainersListRequest();
        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest put = configuration.createHttpRequest("/api/v1/trainee/trainers", "PUT");
        put.setEntity(entity);

        configuration.executeRequestWithAuthToken(put);
    }

    @When("user update trainee's trainers with body:")
    public void updateTraineesTrainersWithValidBody(String jsonPayload) throws Exception {
        StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

        HttpRequest put = configuration.createHttpRequest("/api/v1/trainee/trainers", "PUT");
        put.setEntity(entity);

        configuration.executeRequestWithAuthToken(put);
    }

    @And("the response body should contain trainer with username {string}")
    public void responseBodyShouldContainTrainerWithUsername(String username) throws Exception {
        CloseableHttpResponse actualResponse = configuration.getCurrentResponse();
        TypeReference<List<TrainerResponse>> typeReference = new TypeReference<>() {};
        List<TrainerResponse> trainers = objectMapper.readValue(
                EntityUtils.toString(actualResponse.getEntity()), typeReference
        );

        boolean found = trainers.stream().anyMatch(trainer -> username.equals(trainer.getUsername()));

        assertTrue(found, "Trainer with username '" + username + "' was not found in the response.");
    }

    @When("user activate or deactivate trainee without body")
    public void activateDeactivateTraineeWithoutBody() throws Exception {
        HttpRequest patch = configuration.createHttpRequest("/api/v1/trainee/active", "PATCH");
        configuration.executeRequestWithAuthToken(patch);
    }

    @When("user activate or deactivate trainee with invalid body")
    public void activateDeactivateTraineeWithInvalidBody() throws Exception {
        UsernameIsActiveUser request = new UsernameIsActiveUser();
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpRequest patch = configuration.createHttpRequest("/api/v1/trainee/active", "PATCH");
        patch.setEntity(entity);

        configuration.executeRequestWithAuthToken(patch);
    }

    @When("user activate or deactivate trainee with username {string} and isActive {string}")
    public void activateDeactivateTraineeWithBody(String username, String isActive) throws Exception {
        UsernameIsActiveUser request = new UsernameIsActiveUser(username, Boolean.parseBoolean(isActive));
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpRequest patch = configuration.createHttpRequest("/api/v1/trainee/active", "PATCH");
        patch.setEntity(entity);

        configuration.executeRequestWithAuthToken(patch);
    }
}
