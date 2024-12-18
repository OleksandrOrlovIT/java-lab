package ua.orlov.springcoregym.controller.cucumber.step.trainer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ua.orlov.springcoregym.controller.cucumber.configuration.CucumberSpringConfiguration;
import ua.orlov.springcoregym.dto.trainee.UpdateTraineeRequest;
import ua.orlov.springcoregym.dto.trainer.TrainerFullUsernameResponse;
import ua.orlov.springcoregym.dto.trainer.TrainerRegister;
import ua.orlov.springcoregym.dto.trainer.TrainerResponse;
import ua.orlov.springcoregym.dto.trainer.UpdateTrainerRequest;
import ua.orlov.springcoregym.dto.user.UsernameIsActiveUser;
import ua.orlov.springcoregym.dto.user.UsernameUser;
import ua.orlov.springcoregym.model.HttpRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class TrainerSteps {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CucumberSpringConfiguration configuration;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before("@tagTrainerFeature")
    public void executeSetupSQL() {
        jdbcTemplate.execute("RUNSCRIPT FROM 'src/test/resources/sql/prune_tables.sql'");
        jdbcTemplate.execute("RUNSCRIPT FROM 'src/test/resources/sql/trainer/populate_encrypted_trainer.sql'");
    }

    @When("user register trainer without body")
    public void registerTrainerWithoutBody() throws Exception {
        HttpRequest post = configuration.createHttpRequest("/api/v1/trainer", "POST");

        configuration.executeRequest(post);
    }

    @When("user register trainer without any information")
    public void registerTrainerWithInvalidBody() throws Exception {
        TrainerRegister request = new TrainerRegister();

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/trainer", "POST");
        post.setEntity(entity);

        configuration.executeRequest(post);
    }

    @When("user registers trainer with the following details:")
    public void registerTrainerWithValidBody(String jsonPayload) throws Exception {
        TrainerRegister request = objectMapper.readValue(jsonPayload, TrainerRegister.class);
        request.setSpecializationId(configuration.getSpecializationId());
        jsonPayload = objectMapper.writeValueAsString(request);

        StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/trainer", "POST");
        post.setEntity(entity);

        configuration.executeRequest(post);
    }

    @When("user get trainer without body")
    public void getTrainerWithoutBody() throws Exception {
        HttpRequest get = configuration.createHttpRequest("/api/v1/trainer", "GET");
        configuration.executeRequestWithAuthToken(get);
    }

    @When("user get trainer with empty body")
    public void getTrainerWithEmptyBody() throws Exception {
        UsernameUser request = new UsernameUser();
        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest get = configuration.createHttpRequest("/api/v1/trainer", "GET");
        get.setEntity(entity);

        configuration.executeRequestWithAuthToken(get);
    }

    @When("user get trainer with username {string}")
    public void getTrainerWithEmptyBody(String username) throws Exception {
        UsernameUser request = new UsernameUser(username);
        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest get = configuration.createHttpRequest("/api/v1/trainer", "GET");
        get.setEntity(entity);

        configuration.executeRequestWithAuthToken(get);
    }

    @When("user update trainer without body")
    public void updateTrainerWithoutBody() throws Exception {
        HttpRequest put = configuration.createHttpRequest("/api/v1/trainer", "PUT");
        configuration.executeRequestWithAuthToken(put);
    }

    @When("user update trainer with invalid body")
    public void updateTrainerWithInvalidBody() throws Exception {
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest put = configuration.createHttpRequest("/api/v1/trainer", "PUT");
        put.setEntity(entity);

        configuration.executeRequestWithAuthToken(put);
    }

    @When("user update trainer with a body:")
    public void updateTrainerWithBody(String jsonPayload) throws Exception {
        UpdateTrainerRequest request = objectMapper.readValue(jsonPayload, UpdateTrainerRequest.class);
        request.setSpecializationId(configuration.getSpecializationId());
        jsonPayload = objectMapper.writeValueAsString(request);

        StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

        HttpRequest put = configuration.createHttpRequest("/api/v1/trainer", "PUT");
        put.setEntity(entity);

        configuration.executeRequestWithAuthToken(put);
    }

    @And("the trainer response should equal:")
    public void traineeFullResponseShouldEqual(String jsonPayload) throws Exception {
        TrainerFullUsernameResponse expectedResponse =
                objectMapper.readValue(jsonPayload, TrainerFullUsernameResponse.class);

        CloseableHttpResponse actualResponseJson = configuration.getCurrentResponse();
        TrainerFullUsernameResponse actualResponse =
                objectMapper.readValue(actualResponseJson.getEntity().getContent(), TrainerFullUsernameResponse.class);

        assertAll("Actual response doesn't equal expected response",
                () -> assertEquals(expectedResponse.isActive(), actualResponse.isActive()),
                () -> assertEquals(expectedResponse.getUsername(), actualResponse.getUsername()),
                () -> assertEquals(expectedResponse.getFirstName(), actualResponse.getFirstName()),
                () -> assertEquals(expectedResponse.getLastName(), actualResponse.getLastName()),
                () -> assertNotNull(actualResponse.getSpecialization()),
                () -> assertEquals(expectedResponse.getTrainees(), actualResponse.getTrainees()));
    }

    @When("user get trainers without trainee without body")
    public void getTrainersWithoutTraineeWithoutBody() throws Exception {
        HttpRequest get = configuration.createHttpRequest("/api/v1/trainer/without-trainee", "GET");
        configuration.executeRequestWithAuthToken(get);
    }

    @When("user get trainers without trainee with invalid body")
    public void getTrainersWithoutTraineeWithInvalidBody() throws Exception {
        UsernameUser request = new UsernameUser();
        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest get = configuration.createHttpRequest("/api/v1/trainer/without-trainee", "GET");
        get.setEntity(entity);

        configuration.executeRequestWithAuthToken(get);
    }

    @When("user get trainers without trainee with username {string}")
    public void getTrainersWithoutTraineeWithBody(String username) throws Exception {
        UsernameUser request = new UsernameUser(username);
        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest get = configuration.createHttpRequest("/api/v1/trainer/without-trainee", "GET");
        get.setEntity(entity);

        configuration.executeRequestWithAuthToken(get);
    }

    @And("the response body should contain trainers list with more than size {int}")
    public void responseBodyShouldContainTrainersWithMoreThanSize(int expectedSize) throws Exception {
        CloseableHttpResponse response = configuration.getCurrentResponse();

        TypeReference<List<TrainerResponse>> typeReference = new TypeReference<>() {};
        List<TrainerResponse> trainers = objectMapper.readValue(
                EntityUtils.toString(response.getEntity()), typeReference
        );

        assertNotNull(trainers);
        assertTrue(trainers.size() >= expectedSize);
    }

    @When("user activate or deactivate trainer without body")
    public void activateDeactivateTrainerWithoutBody() throws Exception {
        HttpRequest patch = configuration.createHttpRequest("/api/v1/trainer/active", "PATCH");
        configuration.executeRequestWithAuthToken(patch);
    }

    @When("user activate or deactivate trainer with invalid body")
    public void activateDeactivateTrainerWithInvalidBody() throws Exception {
        UsernameIsActiveUser request = new UsernameIsActiveUser();
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpRequest patch = configuration.createHttpRequest("/api/v1/trainer/active", "PATCH");
        patch.setEntity(entity);

        configuration.executeRequestWithAuthToken(patch);
    }

    @When("user activate or deactivate trainer with username {string} and isActive {string}")
    public void activateDeactivateTrainerWithBody(String username, String isActive) throws Exception {
        UsernameIsActiveUser request = new UsernameIsActiveUser(username, Boolean.parseBoolean(isActive));
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpRequest patch = configuration.createHttpRequest("/api/v1/trainer/active", "PATCH");
        patch.setEntity(entity);

        configuration.executeRequestWithAuthToken(patch);
    }
}
