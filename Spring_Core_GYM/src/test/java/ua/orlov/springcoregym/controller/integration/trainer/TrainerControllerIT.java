package ua.orlov.springcoregym.controller.integration.trainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ua.orlov.springcoregym.controller.integration.config.LoginComponent;
import ua.orlov.springcoregym.dto.trainer.*;
import ua.orlov.springcoregym.dto.user.UsernameIsActiveUser;
import ua.orlov.springcoregym.dto.user.UsernameUser;
import ua.orlov.springcoregym.model.HttpRequest;
import ua.orlov.springcoregym.service.training.TrainingTypeService;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "/sql/trainer/populate_encrypted_trainer.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/prune_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class TrainerControllerIT {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrainingTypeService trainingTypeService;

    private LoginComponent loginComponent;

    @BeforeEach
    void setUp() {
        loginComponent = new LoginComponent(httpClient, objectMapper, randomServerPort);
    }

    @Test
    void registerTrainerWithoutBody() throws Exception {
        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/trainer");

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    void registerTrainerWithInvalidBody() throws Exception {
        TrainerRegister request = new TrainerRegister();

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/trainer");
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            String stringResponse = EntityUtils.toString(response.getEntity());
            assertTrue(stringResponse.contains("Last name is required."));
            assertTrue(stringResponse.contains("First name is required."));
        }
    }

    @Test
    void registerTrainerSuccess() throws Exception {
        TrainerRegister request = new TrainerRegister();
        request.setLastName("LastName");
        request.setFirstName("FirstName");
        request.setSpecializationId(trainingTypeService.getAll().get(0).getId());

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/trainer");
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertTrue(EntityUtils.toString(response.getEntity()).contains("\"username\":\"FirstName.LastName\""));
        }
    }

    @Test
    void getTrainerByUsernameWithoutBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer1", "password");

        HttpRequest get = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainer", "GET");
        get.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainerByUsernameWithWrongBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer1", "password");

        UsernameUser request = new UsernameUser();

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest get = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainer", "GET");
        get.setHeader("Authorization", "Bearer " + token);
        get.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"errors\":[\"username is required.\"],\"status\":\"BAD_REQUEST\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainerByUsernameNonExistentUser() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer1", "password");

        UsernameUser request = new UsernameUser("asdasd");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest get = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainer", "GET");
        get.setHeader("Authorization", "Bearer " + token);
        get.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"User not found with username = asdasd\",\"status\":\"NOT_FOUND\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainerByUsernameDifferentTrainer() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer1", "password");

        UsernameUser request = new UsernameUser("testtrainer2");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest get = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainer", "GET");
        get.setHeader("Authorization", "Bearer " + token);
        get.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainerByUsernameThenSuccess() throws Exception {
        String token = loginComponent.loginAsUser("testtrainer1", "password");

        UsernameUser request = new UsernameUser("testtrainer1");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest get = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainer", "GET");
        get.setHeader("Authorization", "Bearer " + token);
        get.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals(200, response.getStatusLine().getStatusCode());

            String responseBody = EntityUtils.toString(response.getEntity());
            assertNotNull(responseBody);

            TrainerFullResponse trainer = objectMapper.readValue(responseBody, TrainerFullResponse.class);

            assertNotNull(trainer);
            assertEquals("Test1", trainer.getFirstName());
            assertEquals("Trainer1", trainer.getLastName());
            assertNotNull(trainer.getSpecialization());
            assertTrue(trainer.isActive());

            assertNotNull(trainer.getTrainees());
            assertEquals(1, trainer.getTrainees().size());
        }
    }

    @Test
    void updateTrainerWithoutBody() throws Exception {
        String token = loginComponent.loginAsUser("updateTrainer", "password");

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainer");
        put.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void updateTrainerWithInvalidBody() throws Exception {
        String token = loginComponent.loginAsUser("updateTrainer", "password");

        UpdateTrainerRequest request = new UpdateTrainerRequest();

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainer");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            String stringResponse = EntityUtils.toString(response.getEntity());
            assertTrue(stringResponse.contains("firstName is required"));
            assertTrue(stringResponse.contains("lastName is required"));
            assertTrue(stringResponse.contains("username is required"));
        }
    }

    @Test
    void updateTrainerWithDifferentTrainer() throws Exception {
        String token = loginComponent.loginAsUser("updateTrainer", "password");
        String updatedString = "newValue";

        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername("testtrainer1");
        request.setActive(true);
        request.setFirstName(updatedString);
        request.setLastName(updatedString);
        request.setSpecializationId(trainingTypeService.getAll().get(0).getId());

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainer");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}", EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void updateTrainerWithNonExistentTrainer() throws Exception {
        String token = loginComponent.loginAsUser("updateTrainer", "password");
        String updatedString = "newValue";

        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername("asdasdasd");
        request.setActive(true);
        request.setFirstName(updatedString);
        request.setLastName(updatedString);
        request.setSpecializationId(trainingTypeService.getAll().get(0).getId());

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainer");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"User not found with username = asdasdasd\",\"status\":\"NOT_FOUND\"}", EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void updateTrainerThenSuccess() throws Exception {
        String token = loginComponent.loginAsUser("updateTrainer", "password");
        String updatedString = "newValue";

        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername("updateTrainer");
        request.setActive(true);
        request.setFirstName(updatedString);
        request.setLastName(updatedString);
        request.setSpecializationId(trainingTypeService.getAll().get(0).getId());

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainer");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());

            String responseBody = EntityUtils.toString(response.getEntity());
            assertNotNull(responseBody);

            TrainerFullUsernameResponse trainer = objectMapper.readValue(responseBody, TrainerFullUsernameResponse.class);

            assertNotNull(trainer);

            assertEquals("updateTrainer", trainer.getUsername());
            assertEquals(updatedString, trainer.getFirstName());
            assertEquals(updatedString, trainer.getLastName());
            assertTrue(trainer.isActive());
            assertNotNull(trainer.getSpecialization());

            assertNotNull(trainer.getTrainees());
            assertEquals(0, trainer.getTrainees().size());
        }
    }

    @Test
    void getTrainersWithoutTraineeWithoutRequestBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee1", "password");

        HttpRequest get = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainer/without-trainee", "GET");
        get.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainersWithoutTraineeWithInvalidRequestBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee1", "password");

        UsernameUser request = new UsernameUser();

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest get = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainer/without-trainee", "GET");
        get.setHeader("Authorization", "Bearer " + token);
        get.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"errors\":[\"username is required.\"],\"status\":\"BAD_REQUEST\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainersWithoutTraineeWithNonExistentTrainee() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee1", "password");

        UsernameUser request = new UsernameUser("asdasd");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest get = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainer/without-trainee", "GET");
        get.setHeader("Authorization", "Bearer " + token);
        get.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"User not found with username = asdasd\",\"status\":\"NOT_FOUND\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainersWithoutTraineeWithDifferentLoggedTrainee() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee1", "password");

        UsernameUser request = new UsernameUser("testtrainee2");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest get = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainer/without-trainee", "GET");
        get.setHeader("Authorization", "Bearer " + token);
        get.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTrainersWithoutTraineeThenSuccess() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee2", "password");

        UsernameUser request = new UsernameUser("testtrainee2");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest get = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainer/without-trainee", "GET");
        get.setHeader("Authorization", "Bearer " + token);
        get.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(get)) {
            assertEquals(200, response.getStatusLine().getStatusCode());

            String responseBody = EntityUtils.toString(response.getEntity());
            assertNotNull(responseBody);

            List<?> trainers = objectMapper.readValue(responseBody, List.class);

            assertNotNull(trainers);
            assertTrue(trainers.size() >= 2);
        }
    }

    @Test
    void activateDeactivateTrainerWithoutBody() throws Exception {
        String token = loginComponent.loginAsUser("deactivatedTrainer", "password");

        HttpPatch patch = new HttpPatch("https://localhost:" + randomServerPort + "/api/v1/trainer/active");
        patch.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(patch)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void activateDeactivateTrainerWithInvalidBody() throws Exception {
        String token = loginComponent.loginAsUser("deactivatedTrainer", "password");

        UsernameIsActiveUser request = new UsernameIsActiveUser();

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPatch patch = new HttpPatch("https://localhost:" + randomServerPort + "/api/v1/trainer/active");
        patch.setHeader("Authorization", "Bearer " + token);
        patch.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(patch)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            String responseString = EntityUtils.toString(response.getEntity());
            assertTrue(responseString.contains("username is required"));
        }
    }

    @Test
    void activateDeactivateTrainerDifferentTrainer() throws Exception {
        String token = loginComponent.loginAsUser("deactivatedTrainer", "password");

        UsernameIsActiveUser request = new UsernameIsActiveUser();
        request.setUsername("activatedTrainer");
        request.setActive(false);

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPatch patch = new HttpPatch("https://localhost:" + randomServerPort + "/api/v1/trainer/active");
        patch.setHeader("Authorization", "Bearer " + token);
        patch.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(patch)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void activateDeactivateTrainerNonExistentTrainer() throws Exception {
        String token = loginComponent.loginAsUser("deactivatedTrainer", "password");

        UsernameIsActiveUser request = new UsernameIsActiveUser();
        request.setUsername("asd");
        request.setActive(false);

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPatch patch = new HttpPatch("https://localhost:" + randomServerPort + "/api/v1/trainer/active");
        patch.setHeader("Authorization", "Bearer " + token);
        patch.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(patch)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"User not found with username = asd\",\"status\":\"NOT_FOUND\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void activateDeactivateTrainerSameIsActive() throws Exception {
        String token = loginComponent.loginAsUser("deactivatedTrainer", "password");

        UsernameIsActiveUser request = new UsernameIsActiveUser();
        request.setUsername("deactivatedTrainer");
        request.setActive(false);

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPatch patch = new HttpPatch("https://localhost:" + randomServerPort + "/api/v1/trainer/active");
        patch.setHeader("Authorization", "Bearer " + token);
        patch.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(patch)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertTrue(EntityUtils.toString(response.getEntity()).contains("Trainer is already deactivated"));
        }
    }

    @Test
    void activateDeactivateTrainerDifferentIsActive() throws Exception {
        String token = loginComponent.loginAsUser("activatedTrainer", "password");

        UsernameIsActiveUser request = new UsernameIsActiveUser();
        request.setUsername("activatedTrainer");
        request.setActive(false);

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPatch patch = new HttpPatch("https://localhost:" + randomServerPort + "/api/v1/trainer/active");
        patch.setHeader("Authorization", "Bearer " + token);
        patch.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(patch)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }
}
