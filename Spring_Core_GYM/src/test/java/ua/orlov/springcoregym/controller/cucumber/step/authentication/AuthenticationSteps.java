package ua.orlov.springcoregym.controller.cucumber.step.authentication;

import io.cucumber.java.Before;
import io.cucumber.java.en.When;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import ua.orlov.springcoregym.controller.cucumber.configuration.CucumberSpringConfiguration;
import ua.orlov.springcoregym.dto.user.ChangeLoginDto;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.model.HttpRequest;

@ActiveProfiles("test")
public class AuthenticationSteps {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CucumberSpringConfiguration configuration;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before("@tagAuthenticationFeature")
    public void executeSetupSQL() {
        jdbcTemplate.execute("RUNSCRIPT FROM 'src/test/resources/sql/prune_tables.sql'");
        jdbcTemplate.execute("RUNSCRIPT FROM 'src/test/resources/sql/user/populate_encrypted_users.sql'");
    }

    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsIn(String username, String password) throws Exception {
        UsernamePasswordUser loginRequest = new UsernamePasswordUser();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        String json = objectMapper.writeValueAsString(loginRequest);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/session", "POST");
        post.setEntity(entity);

        configuration.executeRequest(post);
    }

    @When("the user logs in with null fields")
    public void theUserLogsInWithNullFields() throws Exception {
        theUserLogsIn(null, null);
    }

    @When("user sends changeLoginDto with username {string} oldPassword {string} newPassword {string}")
    public void theUserChangeLogin(String username, String oldPassword, String newPassword) throws Exception {
        ChangeLoginDto changeLoginDto = new ChangeLoginDto();
        changeLoginDto.setUsername(username);
        changeLoginDto.setOldPassword(oldPassword);
        changeLoginDto.setNewPassword(newPassword);

        String json = objectMapper.writeValueAsString(changeLoginDto);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpRequest put = configuration.createHttpRequest("/api/v1/password", "PUT");
        put.setEntity(entity);

        configuration.executeRequestWithAuthToken(put);
    }

    @When("user sends changeLoginDto without body")
    public void theUserChangeLoginWithoutBody() throws Exception {
        HttpRequest put = configuration.createHttpRequest("/api/v1/password", "PUT");

        configuration.executeRequestWithAuthToken(put);
    }
}
