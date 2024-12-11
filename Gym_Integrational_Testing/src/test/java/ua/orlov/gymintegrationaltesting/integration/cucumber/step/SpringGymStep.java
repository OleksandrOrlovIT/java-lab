package ua.orlov.gymintegrationaltesting.integration.cucumber.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.When;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import ua.orlov.gymintegrationaltesting.integration.cucumber.configuration.CucumberSpringConfiguration;
import ua.orlov.gymintegrationaltesting.model.HttpRequest;

public class SpringGymStep {

    @Autowired
    private CucumberSpringConfiguration configuration;

    @Autowired
    private ObjectMapper objectMapper;

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
    }
}
