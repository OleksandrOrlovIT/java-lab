package ua.orlov.springcoregym.controller.integration.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ua.orlov.springcoregym.controller.integration.config.LoginComponent;
import ua.orlov.springcoregym.dto.user.ChangeLoginDto;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.service.security.LoginAttemptService;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "/sql/user/populate_encrypted_users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/prune_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class AuthenticationControllerIT {

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoginAttemptService loginAttemptService;

    private LoginComponent loginComponent;

    @BeforeEach
    void setUp() {
        loginComponent = new LoginComponent(httpClient, objectMapper);
    }

    @AfterEach
    void tearDown() {
        loginAttemptService.clearCache();
    }

    @Test
    void loginThenSuccess() throws Exception {
        UsernamePasswordUser usernamePasswordUser = new UsernamePasswordUser();
        usernamePasswordUser.setUsername("encryptedUser");
        usernamePasswordUser.setPassword("password");

        String json = objectMapper.writeValueAsString(usernamePasswordUser);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:8443/api/v1/session");
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void loginWrongPasswordThenBadCredentials() throws Exception {
        UsernamePasswordUser usernamePasswordUser = new UsernamePasswordUser();
        usernamePasswordUser.setUsername("encryptedUser");
        usernamePasswordUser.setPassword("asdasd");

        String json = objectMapper.writeValueAsString(usernamePasswordUser);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:8443/api/v1/session");
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void loginWrongUsernameThenBadCredentials() throws Exception {
        UsernamePasswordUser usernamePasswordUser = new UsernamePasswordUser();
        usernamePasswordUser.setUsername("sdasdasd");
        usernamePasswordUser.setPassword("asdasd");

        String json = objectMapper.writeValueAsString(usernamePasswordUser);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:8443/api/v1/session");
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void changeLoginNonAuthorizedUserThenAccessDenied() throws Exception {
        String token = loginComponent.loginAsUser("changePasswordUser", "password");

        String newPassword = "newPasswor";
        ChangeLoginDto changeLoginDto = new ChangeLoginDto();
        changeLoginDto.setUsername("testUser");
        changeLoginDto.setOldPassword("password");
        changeLoginDto.setNewPassword(newPassword);

        String json = objectMapper.writeValueAsString(changeLoginDto);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:8443/api/v1/password");
        put.setEntity(entity);

        put.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
//            assertEquals(403, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Access Denied\",\"status\":\"FORBIDDEN\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void changeLoginWrongPasswordThen() throws Exception {
        String token = loginComponent.loginAsUser("changePasswordUser2", "password");

        String newPassword = "newPasswor";
        ChangeLoginDto changeLoginDto = new ChangeLoginDto();
        changeLoginDto.setUsername("changePasswordUser2");
        changeLoginDto.setOldPassword("asdasdasd");
        changeLoginDto.setNewPassword(newPassword);

        String json = objectMapper.writeValueAsString(changeLoginDto);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:8443/api/v1/password");
        put.setEntity(entity);

        put.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Wrong password = asdasdasd\",\"status\":\"UNAUTHORIZED\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void changeLoginWrongUsernameThenBadRequest() throws Exception {
        String token = loginComponent.loginAsUser("changePasswordUser", "password");

        String newPassword = "newPasswor";
        ChangeLoginDto changeLoginDto = new ChangeLoginDto();
        changeLoginDto.setUsername("someNonExistentUserUser");
        changeLoginDto.setOldPassword("password");
        changeLoginDto.setNewPassword(newPassword);

        String json = objectMapper.writeValueAsString(changeLoginDto);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:8443/api/v1/password");
        put.setEntity(entity);

        put.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(404, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"User not found with username = someNonExistentUserUser\",\"status\":\"NOT_FOUND\"}",
                    EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void changeLoginThenSuccess() throws Exception {
        String token = loginComponent.loginAsUser("changePasswordUser", "password");

        String newPassword = "newPasswor";
        ChangeLoginDto changeLoginDto = new ChangeLoginDto();
        changeLoginDto.setUsername("changePasswordUser");
        changeLoginDto.setOldPassword("password");
        changeLoginDto.setNewPassword(newPassword);

        String json = objectMapper.writeValueAsString(changeLoginDto);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:8443/api/v1/password");
        put.setEntity(entity);

        put.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals("You successfully changed password", EntityUtils.toString(response.getEntity()));
        }

        assertNotNull(loginComponent.loginAsUser("changePasswordUser", newPassword));
    }

    @Test
    void logoutThenSuccess() throws Exception {
        String token = loginComponent.loginAsUser("testUser", "password");

        HttpPost post = new HttpPost("https://localhost:8443/api/v1/logout");
        post.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals("Logged out successfully.", EntityUtils.toString(response.getEntity()));
        }

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(403, response.getStatusLine().getStatusCode());
        }
    }

    @Test
    void loginUsernamePasswordUserWithNullFields() throws Exception {
        UsernamePasswordUser usernamePasswordUser = new UsernamePasswordUser();

        String json = objectMapper.writeValueAsString(usernamePasswordUser);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:8443/api/v1/session");
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            String stringResponse = EntityUtils.toString(response.getEntity());
            assertTrue(stringResponse.contains("password is required"));
            assertTrue(stringResponse.contains("username is required"));
        }
    }

    @Test
    void changeLoginWithoutBody() throws Exception {
        String token = loginComponent.loginAsUser("testUser", "password");

        HttpPut put = new HttpPut("https://localhost:8443/api/v1/password");
        put.setHeader("Authorization", "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            assertNotNull(EntityUtils.toString(response.getEntity()));
        }
    }

    @Test
    void changeLoginWrongInputBody() throws Exception {
        String token = loginComponent.loginAsUser("testUser", "password");

        ChangeLoginDto changeLoginDto = new ChangeLoginDto();
        String json = objectMapper.writeValueAsString(changeLoginDto);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPut put = new HttpPut("https://localhost:8443/api/v1/password");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            assertEquals(400, response.getStatusLine().getStatusCode());
            String stringResponse = EntityUtils.toString(response.getEntity());
            assertTrue(stringResponse.contains("username is required."));
            assertTrue(stringResponse.contains("oldPassword is required."));
            assertTrue(stringResponse.contains("newPassword is required."));
        }
    }

    @Test
    void loginWrong4TimesThenTooManyRequests() throws Exception {
        for(int i = 0; i < 3; i++) {
            loginComponent.loginWithBadCredentials("asdasdasd", "password");
        }

        UsernamePasswordUser usernamePasswordUser = new UsernamePasswordUser();
        usernamePasswordUser.setUsername("asdasdsadas");
        usernamePasswordUser.setPassword("password");

        String json = objectMapper.writeValueAsString(usernamePasswordUser);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:8443/api/v1/session");
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(429, response.getStatusLine().getStatusCode());
            assertEquals("{\"message\":\"Too many attempts\",\"status\":\"TOO_MANY_REQUESTS\"}", EntityUtils.toString(response.getEntity()));
        }
    }
}
