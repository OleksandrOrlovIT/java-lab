package ua.orlov.springcoregym.controller.integration.trainee;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ua.orlov.springcoregym.controller.integration.config.LoginComponent;
import ua.orlov.springcoregym.dto.trainee.*;
import ua.orlov.springcoregym.dto.user.UsernameIsActiveUser;
import ua.orlov.springcoregym.dto.user.UsernameUser;
import ua.orlov.springcoregym.model.HttpRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/trainee/populate_encrypted_trainee.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/prune_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class TraineeControllerIT {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginComponent loginComponent;

    @BeforeEach
    void setUp() {
        loginComponent = new LoginComponent(httpClient, objectMapper, randomServerPort);
    }

    @Test
    void registerTraineeWithoutBody() throws Exception {
        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/trainee/create");

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    void registerTraineeWithInvalidBody() throws Exception {
        TraineeRegister request = new TraineeRegister();

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/trainee/create");
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            String stringResponse = EntityUtils.toString(response.getEntity());
            assertTrue(stringResponse.contains("Last name is required."));
            assertTrue(stringResponse.contains("First name is required."));
        }
    }

    @Test
    void registerTraineeSuccess() throws Exception {
        TraineeRegister request = new TraineeRegister();
        request.setAddress("Address");
        request.setDateOfBirth(LocalDate.of(2020, 10, 10));
        request.setLastName("LastName");
        request.setFirstName("FirstName");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/trainee/create");
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertTrue(EntityUtils.toString(response.getEntity()).contains("\"username\":\"FirstName.LastName\""));
        }
    }

    @Test
    void getTraineeByUsernameWithoutBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/trainee/username");
        post.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTraineeByUsernameWithWrongBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        UsernameUser request = new UsernameUser();

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/trainee/username");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertEquals("{\"errors\":[\"username is required.\"],\"status\":\"BAD_REQUEST\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTraineeByUsernameNonExistentUser() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        UsernameUser request = new UsernameUser("asdasdsad");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/trainee/username");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"User not found with username = asdasdsad\",\"status\":\"NOT_FOUND\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTraineeByUsernameDifferentTrainee() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        UsernameUser request = new UsernameUser("testtrainee2");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/trainee/username");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void getTraineeByUsernameThenSuccess() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        UsernameUser request = new UsernameUser("testtrainee");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:" + randomServerPort + "/api/v1/trainee/username");
        post.setHeader("Authorization", "Bearer " + token);
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());

            String responseBody = EntityUtils.toString(response.getEntity());
            assertNotNull(responseBody);

            TraineeFullResponse trainee = objectMapper.readValue(responseBody, TraineeFullResponse.class);

            assertNotNull(trainee);

            assertEquals("Test", trainee.getFirstName());
            assertEquals("Trainee", trainee.getLastName());
            assertEquals(LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());
            assertEquals("123 Main St", trainee.getAddress());
            assertTrue(trainee.isActive());

            assertNotNull(trainee.getTrainers());
            assertEquals(1, trainee.getTrainers().size());
        }
    }

    @Test
    void updateTraineeWithoutBody() throws Exception {
        String token = loginComponent.loginAsUser("updateTrainee", "password");

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainee");
        put.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void updateTraineeWithInvalidBody() throws Exception {
        String token = loginComponent.loginAsUser("updateTrainee", "password");

        UpdateTraineeRequest request = new UpdateTraineeRequest();

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainee");
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
    void updateTraineeWithDifferentTrainee() throws Exception {
        String token = loginComponent.loginAsUser("updateTrainee", "password");
        String updatedString = "newValue";
        LocalDate newDate = LocalDate.of(2024, 10, 10);

        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername("testtrainee");
        request.setActive(true);
        request.setAddress(updatedString);
        request.setFirstName(updatedString);
        request.setLastName(updatedString);
        request.setDateOfBirth(newDate);

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainee");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}", EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void updateTraineeWithNonExistentTrainee() throws Exception {
        String token = loginComponent.loginAsUser("updateTrainee", "password");
        String updatedString = "newValue";
        LocalDate newDate = LocalDate.of(2024, 10, 10);

        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername("asdasdasd");
        request.setActive(true);
        request.setAddress(updatedString);
        request.setFirstName(updatedString);
        request.setLastName(updatedString);
        request.setDateOfBirth(newDate);

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainee");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"User not found with username = asdasdasd\",\"status\":\"NOT_FOUND\"}", EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void updateTraineeThenSuccess() throws Exception {
        String token = loginComponent.loginAsUser("updateTrainee", "password");
        String updatedString = "newValue";
        LocalDate newDate = LocalDate.of(2024, 10, 10);

        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername("updateTrainee");
        request.setActive(true);
        request.setAddress(updatedString);
        request.setFirstName(updatedString);
        request.setLastName(updatedString);
        request.setDateOfBirth(newDate);

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainee");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());

            String responseBody = EntityUtils.toString(response.getEntity());
            assertNotNull(responseBody);

            TraineeFullUsernameResponse trainee = objectMapper.readValue(responseBody, TraineeFullUsernameResponse.class);

            assertNotNull(trainee);

            assertEquals("updateTrainee", trainee.getUsername());
            assertEquals(updatedString, trainee.getFirstName());
            assertEquals(updatedString, trainee.getLastName());
            assertEquals(newDate, trainee.getDateOfBirth());
            assertEquals(updatedString, trainee.getAddress());
            assertTrue(trainee.isActive());

            assertNotNull(trainee.getTrainers());
            assertEquals(0, trainee.getTrainers().size());
        }
    }

    @Test
    void deleteTraineeByUsernameWithoutBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        HttpRequest delete = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainee", "DELETE");
        delete.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(delete)) {
            assertEquals(400, response.getStatusLine().getStatusCode());

            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void deleteTraineeByUsernameWithWrongBody() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        UsernameUser request = new UsernameUser();

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest delete = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainee", "DELETE");
        delete.setHeader("Authorization", "Bearer " + token);
        delete.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(delete)) {
            assertEquals(400, response.getStatusLine().getStatusCode());

            assertEquals("{\"errors\":[\"username is required.\"],\"status\":\"BAD_REQUEST\"}", EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void deleteTraineeByUsernameWithNonExistentTrainee() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        UsernameUser request = new UsernameUser("asdasd");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest delete = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainee", "DELETE");
        delete.setHeader("Authorization", "Bearer " + token);
        delete.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(delete)) {
            assertEquals(404, response.getStatusLine().getStatusCode());

            assertEquals("{\"message\":\"User not found with username = asdasd\",\"status\":\"NOT_FOUND\"}", EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void deleteTraineeByUsernameWithDifferentTrainee() throws Exception {
        String token = loginComponent.loginAsUser("testtrainee", "password");

        UsernameUser request = new UsernameUser("updateTrainee");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest delete = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainee", "DELETE");
        delete.setHeader("Authorization", "Bearer " + token);
        delete.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(delete)) {
            assertEquals(403, response.getStatusLine().getStatusCode());

            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}", EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void deleteTraineeByUsernameThenSuccess() throws Exception {
        String token = loginComponent.loginAsUser("deleteTrainee", "password");

        UsernameUser request = new UsernameUser("deleteTrainee");

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest delete = new HttpRequest("https://localhost:" + randomServerPort + "/api/v1/trainee", "DELETE");
        delete.setHeader("Authorization", "Bearer " + token);
        delete.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(delete)) {
            assertEquals(204, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    void updateTraineeTrainersListWithoutBody() throws Exception {
        String token = loginComponent.loginAsUser("updateTraineeTrainersUser", "password");

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainee/trainers");
        put.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void updateTraineeTrainersListWithInvalidBody() throws Exception {
        String token = loginComponent.loginAsUser("updateTraineeTrainersUser", "password");

        UpdateTraineeTrainersListRequest request = new UpdateTraineeTrainersListRequest();

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainee/trainers");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(400, response.getStatusLine().getStatusCode());

            String responseString = EntityUtils.toString(response.getEntity());
            assertTrue(responseString.contains("username is required."));
            assertTrue(responseString.contains("trainers are required."));
        }
    }

    @Test
    void updateTraineeTrainersListNonExistentTrainee() throws Exception {
        String token = loginComponent.loginAsUser("updateTraineeTrainersUser", "password");

        UpdateTraineeTrainersListRequest request = new UpdateTraineeTrainersListRequest();
        request.setUsername("asd");
        request.setTrainers(List.of(new UsernameUser("testtrainer")));

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainee/trainers");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"User not found with username = asd\",\"status\":\"NOT_FOUND\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void updateTraineeTrainersListDifferentLoggedTrainee() throws Exception {
        String token = loginComponent.loginAsUser("updateTraineeTrainersUser", "password");

        UpdateTraineeTrainersListRequest request = new UpdateTraineeTrainersListRequest();
        request.setUsername("testtrainee");
        request.setTrainers(List.of(new UsernameUser("testtrainer")));

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainee/trainers");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void updateTraineeTrainersListThenSuccess() throws Exception {
        String token = loginComponent.loginAsUser("updateTraineeTrainersUser", "password");

        UpdateTraineeTrainersListRequest request = new UpdateTraineeTrainersListRequest();
        request.setUsername("updateTraineeTrainersUser");
        request.setTrainers(List.of(new UsernameUser("testtrainer")));

        String json = objectMapper.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:" + randomServerPort + "/api/v1/trainee/trainers");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            List<?> trainers = objectMapper.readValue(EntityUtils.toString(response.getEntity()), List.class);

            assertNotNull(trainers);
            assertEquals(1, trainers.size());
        }
    }

    @Test
    void activateDeactivateTraineeWithoutBody() throws Exception {
        String token = loginComponent.loginAsUser("activatedTrainee", "password");

        UsernameIsActiveUser request = new UsernameIsActiveUser();

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPatch patch = new HttpPatch("https://localhost:" + randomServerPort + "/api/v1/trainee/active");
        patch.setHeader("Authorization", "Bearer " + token);
        patch.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(patch)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void activateDeactivateTraineeWithInvalidBody() throws Exception {
        String token = loginComponent.loginAsUser("activatedTrainee", "password");

        UsernameIsActiveUser request = new UsernameIsActiveUser();

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPatch patch = new HttpPatch("https://localhost:" + randomServerPort + "/api/v1/trainee/active");
        patch.setHeader("Authorization", "Bearer " + token);
        patch.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(patch)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            String responseString = EntityUtils.toString(response.getEntity());
            assertTrue(responseString.contains("username is required"));
        }
    }

    @Test
    void activateDeactivateTraineeDifferentTrainee() throws Exception {
        String token = loginComponent.loginAsUser("activatedTrainee", "password");

        UsernameIsActiveUser request = new UsernameIsActiveUser();
        request.setUsername("testtrainee");
        request.setActive(true);

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPatch patch = new HttpPatch("https://localhost:" + randomServerPort + "/api/v1/trainee/active");
        patch.setHeader("Authorization", "Bearer " + token);
        patch.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(patch)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void activateDeactivateTraineeNonExistentTrainee() throws Exception {
        String token = loginComponent.loginAsUser("activatedTrainee", "password");

        UsernameIsActiveUser request = new UsernameIsActiveUser();
        request.setUsername("asd");
        request.setActive(true);

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPatch patch = new HttpPatch("https://localhost:" + randomServerPort + "/api/v1/trainee/active");
        patch.setHeader("Authorization", "Bearer " + token);
        patch.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(patch)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"User not found with username = asd\",\"status\":\"NOT_FOUND\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void activateDeactivateTraineeSameIsActive() throws Exception {
        String token = loginComponent.loginAsUser("deactivatedTrainee", "password");

        UsernameIsActiveUser request = new UsernameIsActiveUser();
        request.setUsername("deactivatedTrainee");
        request.setActive(true);

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPatch patch = new HttpPatch("https://localhost:" + randomServerPort + "/api/v1/trainee/active");
        patch.setHeader("Authorization", "Bearer " + token);
        patch.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(patch)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertTrue(EntityUtils.toString(response.getEntity()).contains("Trainee is already active"));
        }
    }

    @Test
    void activateDeactivateTraineeDifferentIsActive() throws Exception {
        String token = loginComponent.loginAsUser("activatedTrainee", "password");

        UsernameIsActiveUser request = new UsernameIsActiveUser();
        request.setUsername("activatedTrainee");
        request.setActive(false);

        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON);

        HttpPatch patch = new HttpPatch("https://localhost:" + randomServerPort + "/api/v1/trainee/active");
        patch.setHeader("Authorization", "Bearer " + token);
        patch.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(patch)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
        }
    }
}
