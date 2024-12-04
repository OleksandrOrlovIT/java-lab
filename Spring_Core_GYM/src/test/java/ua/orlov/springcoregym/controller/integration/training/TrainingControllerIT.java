package ua.orlov.springcoregym.controller.integration.training;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ua.orlov.springcoregym.controller.integration.config.LoginComponent;
import ua.orlov.springcoregym.dto.training.CreateTrainingRequest;
import ua.orlov.springcoregym.dto.training.TraineeTrainingsRequest;
import ua.orlov.springcoregym.dto.training.TrainerTrainingRequest;
import ua.orlov.springcoregym.service.message.MessageSender;
import ua.orlov.springcoregym.service.training.TrainingTypeService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/training/populate_trainings_encrypted.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/prune_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class TrainingControllerIT {

    @LocalServerPort
    int randomServerPort;

    @MockBean
    private MessageSender messageSender;

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginComponent loginComponent;

    @Autowired
    private TrainingTypeService trainingTypeService;

    @BeforeEach
    void setUp() {
        loginComponent = new LoginComponent(httpClient, objectMapper, randomServerPort);
    }

    @Test
    void getTrainingTypesThenAccessDenied() throws IOException {
        HttpGet get = new HttpGet("https://localhost:" + randomServerPort + "/api/v1/training/types");

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    void getTrainingTypesThenSuccess() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        HttpGet get = new HttpGet("https://localhost:" + randomServerPort + "/api/v1/training/types");
        get.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals(200, response.getStatusLine().getStatusCode());

            String responseBody = EntityUtils.toString(response.getEntity());
            JsonNode trainingTypes = objectMapper.readTree(responseBody);

            assertEquals(1, trainingTypes.size());

            JsonNode firstTrainingType = trainingTypes.get(0);
            assertEquals("testTrainingType1", firstTrainingType.get("trainingTypeName").asText());
        }
    }

    @Test
    void getTrainingsByTraineeAndDateWithoutBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training/trainee");
        post.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Required request body is missing: public java.util.List<ua.orlov" +
                    ".springcoregym.dto.training.TrainingFullResponse> ua.orlov.springcoregym.controller.training" +
                    ".TrainingController.getTrainingsByTraineeAndDate(ua.orlov.springcoregym.dto.training" +
                    ".TraineeTrainingsRequest)\",\"status\":\"BAD_REQUEST\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainingsByTraineeAndDateWithInvalidBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        TraineeTrainingsRequest request = new TraineeTrainingsRequest();
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort +"/api/v1/training/trainee");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"errors\":[\"username is required.\"],\"status\":\"BAD_REQUEST\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainingsByTraineeAndDateWithNonExistentTrainee() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        TraineeTrainingsRequest request = new TraineeTrainingsRequest();
        request.setTrainingTypeId(1L);
        request.setUsername("asdasdasd");
        request.setTrainerUsername("testtrainer");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training/trainee");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"User not found with username = asdasdasd\",\"status\":\"NOT_FOUND\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainingsByTraineeAndDateWithDifferentTrainee() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        TraineeTrainingsRequest request = new TraineeTrainingsRequest();
        request.setTrainingTypeId(1L);
        request.setUsername("testtrainee2");
        request.setStartDate(LocalDate.parse("2024-10-01"));
        request.setEndDate(LocalDate.parse("2024-11-01"));
        request.setTrainerUsername("testtrainer");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training/trainee");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}", EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainingsByTraineeAndDateNoTrainings() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        TraineeTrainingsRequest request = new TraineeTrainingsRequest();
        request.setTrainingTypeId(1L);
        request.setUsername("testtrainee");
        request.setStartDate(LocalDate.parse("2024-11-01"));
        request.setEndDate(LocalDate.parse("2024-11-01"));
        request.setTrainerUsername("testtrainer");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort +"/api/v1/training/trainee");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals("[]", EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainingsByTraineeAndDateSuccess() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        TraineeTrainingsRequest request = new TraineeTrainingsRequest();
        request.setTrainingTypeId(trainingTypeService.getAll().get(0).getId());
        request.setUsername("testtrainee");
        request.setStartDate(LocalDate.parse("2024-10-01"));
        request.setEndDate(LocalDate.parse("2024-11-01"));
        request.setTrainerUsername("testtrainer");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training/trainee");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());

            String jsonResponse = EntityUtils.toString(response.getEntity());
            List<?> trainingsList = objectMapper.readValue(jsonResponse, List.class);

            assertEquals(2, trainingsList.size(), "Expected exactly 2 training objects in the response");
        }
    }

    @Test
    void getTrainingsByTrainerAndDateWithoutBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training/trainer");
        post.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainingsByTrainerAndDateWithInvalidBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        TrainerTrainingRequest request = new TrainerTrainingRequest();
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training/trainer");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"errors\":[\"username is required.\"],\"status\":\"BAD_REQUEST\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainingsByTrainerAndDateWithNonExistentTrainer() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        TrainerTrainingRequest request = new TrainerTrainingRequest();
        request.setUsername("asd");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training/trainer");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"User not found with username = asd\",\"status\":\"NOT_FOUND\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainingsByTrainerAndDateDifferentTrainer() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        TrainerTrainingRequest request = new TrainerTrainingRequest();
        request.setUsername("testtrainer2");
        request.setStartDate(LocalDate.parse("2024-10-01"));
        request.setEndDate(LocalDate.parse("2024-11-01"));
        request.setTraineeUsername("testtrainee");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training/trainer");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}", EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainingsByTrainerAndDateNoTrainings() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer2", "password");

        TrainerTrainingRequest request = new TrainerTrainingRequest();
        request.setUsername("testtrainer2");
        request.setStartDate(LocalDate.parse("2024-11-01"));
        request.setEndDate(LocalDate.parse("2024-11-01"));
        request.setTraineeUsername("testtrainee2");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training/trainer");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals("[]", EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainingsByTrainerAndDateSuccess() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        TrainerTrainingRequest request = new TrainerTrainingRequest();
        request.setUsername("testtrainer");
        request.setStartDate(LocalDate.parse("2024-10-01"));
        request.setEndDate(LocalDate.parse("2024-11-01"));
        request.setTraineeUsername("testtrainee");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training/trainer");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());

            String jsonResponse = EntityUtils.toString(response.getEntity());
            List<?> trainingsList = objectMapper.readValue(jsonResponse, List.class);

            assertEquals(2, trainingsList.size(), "Expected exactly 2 training objects in the response");
        }
    }

    @Test
    void createTrainingWithoutBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training");
        post.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void createTrainingWithInvalidBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        CreateTrainingRequest request = new CreateTrainingRequest();
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            String stringResponse = EntityUtils.toString(response.getEntity());
            assertTrue(stringResponse.contains("trainingDurationMinutes is required."));
            assertTrue(stringResponse.contains("trainingName is required."));
            assertTrue(stringResponse.contains("trainingDate is required."));
            assertTrue(stringResponse.contains("traineeUsername is required."));
            assertTrue(stringResponse.contains("trainerUsername is required."));
        }
    }

    @Test
    void createTrainingWithoutLoggedUser() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        CreateTrainingRequest request = new CreateTrainingRequest();
        request.setTrainingName("SomeTrainingName");
        request.setTrainingDurationMinutes(50);
        request.setTrainingDate(LocalDate.parse("2024-10-01"));
        request.setTrainingTypeId(1L);
        request.setTraineeUsername("testtrainee");
        request.setTrainerUsername("testtrainer2");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}", EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void createTrainingWithNonExistentTraineeThenNotFound() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        CreateTrainingRequest request = new CreateTrainingRequest();
        request.setTrainingName("SomeTrainingName");
        request.setTrainingDurationMinutes(50);
        request.setTrainingDate(LocalDate.parse("2024-10-01"));
        request.setTrainingTypeId(1L);
        request.setTraineeUsername("NONEXISTENTTRAINEE");
        request.setTrainerUsername("testtrainer2");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void createTrainingWithLoggedTraineeThenSuccess() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        CreateTrainingRequest request = new CreateTrainingRequest();
        request.setTrainingName("SomeTrainingName");
        request.setTrainingDurationMinutes(50);
        request.setTrainingDate(LocalDate.parse("2024-10-01"));
        request.setTrainingTypeId(trainingTypeService.getAll().get(0).getId());
        request.setTraineeUsername("testtrainee");
        request.setTrainerUsername("TrainerForCreateTraining");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    void createTrainingWithLoggedTrainerThenSuccess() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        CreateTrainingRequest request = new CreateTrainingRequest();
        request.setTrainingName("SomeTrainingName");
        request.setTrainingDurationMinutes(50);
        request.setTrainingDate(LocalDate.parse("2024-10-01"));
        request.setTrainingTypeId(trainingTypeService.getAll().get(0).getId());
        request.setTraineeUsername("traineeForCreateTraining");
        request.setTrainerUsername("testtrainer");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    void createTrainingWithNonExistentTrainingTypeId() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer", "password");

        CreateTrainingRequest request = new CreateTrainingRequest();
        request.setTrainingName("SomeTrainingName");
        request.setTrainingDurationMinutes(50);
        request.setTrainingDate(LocalDate.parse("2024-10-01"));
        request.setTrainingTypeId(-1L);
        request.setTraineeUsername("traineeForCreateTraining");
        request.setTrainerUsername("testtrainer");

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/training");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"TrainingType not found with id = -1\",\"status\":\"NOT_FOUND\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

}
