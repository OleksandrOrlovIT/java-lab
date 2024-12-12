package ua.orlov.springcoregym.controller.cucumber.step.training;

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
import ua.orlov.springcoregym.dto.training.CreateTrainingRequest;
import ua.orlov.springcoregym.dto.training.TraineeTrainingsRequest;
import ua.orlov.springcoregym.dto.training.TrainerTrainingRequest;
import ua.orlov.springcoregym.dto.training.TrainingFullResponse;
import ua.orlov.springcoregym.dto.trainingtype.TrainingTypeResponse;
import ua.orlov.springcoregym.model.HttpRequest;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.service.training.TrainingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class TrainingSteps {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CucumberSpringConfiguration configuration;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TrainingService trainingService;

    private TrainingFullResponse trainingFullResponse;

    @Before("@tagTrainingFeature")
    public void executeSetupSQL() {
        jdbcTemplate.execute("RUNSCRIPT FROM 'src/test/resources/sql/prune_tables.sql'");
        jdbcTemplate.execute("RUNSCRIPT FROM 'src/test/resources/sql/training/populate_trainings_encrypted.sql'");
    }

    @When("the user sends request to get trainingTypes")
    public void getTrainingTypes() throws Exception {
        HttpRequest get = configuration.createHttpRequest("/api/v1/training/types", "GET");

        configuration.executeRequestWithAuthToken(get);
    }

    @And("the response body contain trainingType with name {string}")
    public void responseBodyContainTrainingTypeWithName(String name) throws Exception {
        CloseableHttpResponse response = configuration.getCurrentResponse();

        TypeReference<List<TrainingTypeResponse>> typeReference = new TypeReference<>() {};
        List<TrainingTypeResponse> trainingTypeResponses = objectMapper.readValue(
                EntityUtils.toString(response.getEntity()), typeReference
        );

        boolean found = trainingTypeResponses.stream()
                .anyMatch(trainingType -> name.equals(trainingType.getTrainingTypeName()));

        assertTrue(found);
    }

    @When("the user sends request to get trainings by trainee and date without body")
    public void getTrainingsByTraineeAndDateWithoutBody() throws Exception {
        HttpRequest post = configuration.createHttpRequest("/api/v1/training/trainee", "POST");

        configuration.executeRequestWithAuthToken(post);
    }

    @When("the user sends request to get trainings by trainee and date with invalid body")
    public void getTrainingsByTraineeAndDateWithInvalidBody() throws Exception {
        TraineeTrainingsRequest request = new TraineeTrainingsRequest();
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/training/trainee", "POST");
        post.setEntity(entity);

        configuration.executeRequestWithAuthToken(post);
    }

    @When("the user sends request to get trainings by trainee and date with body:")
    public void getTrainingsByTraineeAndDateWithBody(String jsonPayload) throws Exception {
        TraineeTrainingsRequest request = objectMapper.readValue(jsonPayload, TraineeTrainingsRequest.class);
        request.setTrainingTypeId(configuration.getSpecializationId());

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/training/trainee", "POST");
        post.setEntity(entity);

        configuration.executeRequestWithAuthToken(post);
    }

    @And("the response body should contain trainings with size {int}")
    public void responseBodyContainTrainingTypeWithName(int size) throws Exception {
        CloseableHttpResponse response = configuration.getCurrentResponse();

        TypeReference<List<TrainingFullResponse>> typeReference = new TypeReference<>() {};
        List<TrainingFullResponse> trainingTypeResponses = objectMapper.readValue(
                EntityUtils.toString(response.getEntity()), typeReference
        );

        assertEquals(size, trainingTypeResponses.size());
    }

    @When("the user sends request to get trainings by trainer and date without body")
    public void getTrainingsByTrainerAndDateWithoutBody() throws Exception {
        HttpRequest post = configuration.createHttpRequest("/api/v1/training/trainer", "POST");

        configuration.executeRequestWithAuthToken(post);
    }

    @When("the user sends request to get trainings by trainer and date with invalid body")
    public void getTrainingsByTrainerAndDateWithInvalidBody() throws Exception {
        TrainerTrainingRequest request = new TrainerTrainingRequest();
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/training/trainer", "POST");
        post.setEntity(entity);

        configuration.executeRequestWithAuthToken(post);
    }

    @When("the user sends request to get trainings by trainer and date with body:")
    public void getTrainingsByTrainerAndDateWithBody(String jsonPayload) throws Exception {
        TrainerTrainingRequest request = objectMapper.readValue(jsonPayload, TrainerTrainingRequest.class);

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/training/trainer", "POST");
        post.setEntity(entity);

        configuration.executeRequestWithAuthToken(post);
    }

    @When("the user sends request to create training without body")
    public void createTrainingWithoutBody() throws Exception {
        HttpRequest post = configuration.createHttpRequest("/api/v1/training", "POST");

        configuration.executeRequestWithAuthToken(post);
    }

    @When("the user sends request create training with invalid body")
    public void createTrainingWithInvalidBody() throws Exception {
        TrainerTrainingRequest request = new TrainerTrainingRequest();
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/training", "POST");
        post.setEntity(entity);

        configuration.executeRequestWithAuthToken(post);
    }

    @When("the user sends request to create training with body:")
    public void CreateTrainingWithBody(String jsonPayload) throws Exception {
        CreateTrainingRequest request = objectMapper.readValue(jsonPayload, CreateTrainingRequest.class);
        request.setTrainingTypeId(configuration.getSpecializationId());

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/training", "POST");
        post.setEntity(entity);

        configuration.executeRequestWithAuthToken(post);
    }

    @When("the user sends request to create training with wrong trainingTypeId and body:")
    public void CreateTrainingWithSpecializationIdAndBody(String jsonPayload) throws Exception {
        CreateTrainingRequest request = objectMapper.readValue(jsonPayload, CreateTrainingRequest.class);

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/training", "POST");
        post.setEntity(entity);

        configuration.executeRequestWithAuthToken(post);
    }

    @When("the user sends request to delete training without trainingId")
    public void deleteTrainingWithoutTrainingId() throws Exception {
        HttpRequest delete = configuration.createHttpRequest("/api/v1/training", "DELETE");

        configuration.executeRequestWithAuthToken(delete);
    }

    @When("the user sends request to delete training with trainingId {int}")
    public void deleteTrainingWithoutTrainingId(int trainingId) throws Exception {
        HttpRequest delete =
                configuration.createHttpRequest("/api/v1/training?trainingId=" + trainingId, "DELETE");

        configuration.executeRequestWithAuthToken(delete);
    }

    @When("the user gets a first training by trainer name {string}")
    public void setTrainingByTrainerName(String trainerName) throws Exception {
        TrainerTrainingRequest request = new TrainerTrainingRequest();
        request.setUsername(trainerName);

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/training/trainer", "POST");
        post.setEntity(entity);

        configuration.executeRequestWithAuthToken(post);

        TypeReference<List<TrainingFullResponse>> typeReference = new TypeReference<>() {};
        List<TrainingFullResponse> trainingTypeResponses = objectMapper.readValue(
                EntityUtils.toString(configuration.getCurrentResponse().getEntity()), typeReference
        );

        trainingFullResponse = trainingTypeResponses.get(0);
    }

    @When("the user sends request to delete training with saved trainingId")
    public void deleteTrainingWithSavedTrainingId() throws Exception {
        Long trainingId = trainingFullResponse.getTrainingId();
        HttpRequest delete =
                configuration.createHttpRequest("/api/v1/training?trainingId=" + trainingId, "DELETE");

        configuration.executeRequestWithAuthToken(delete);
    }
}
