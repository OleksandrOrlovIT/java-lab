package ua.orlov.gymtrainerworkload.controller.cucumber.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.When;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import ua.orlov.gymtrainerworkload.controller.cucumber.configuration.CucumberSpringConfiguration;
import ua.orlov.gymtrainerworkload.dto.TrainerWorkload;
import ua.orlov.gymtrainerworkload.model.HttpRequest;

public class TrainerStep {

    @Autowired
    private CucumberSpringConfiguration configuration;

    @Autowired
    private ObjectMapper objectMapper;

    @When("user sends change workload request without body")
    public void sendChangeWorkloadRequestWithoutBody() throws Exception {
        HttpRequest post = configuration.createHttpRequest("/api/v1/trainer/workload", "POST");

        configuration.executeRequest(post);
    }

    @When("user sends change workload request with invalid body")
    public void sendChangeWorkloadRequestWithInvalidBody() throws Exception {
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(trainerWorkload), ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/trainer/workload", "POST");
        post.setEntity(entity);

        configuration.executeRequest(post);
    }

    @When("user sends change workload request with body:")
    public void sendChangeWorkloadRequestWithBody(String jsonPayload) throws Exception {
        System.out.println("JsonPayload: " + jsonPayload);
        StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/trainer/workload", "POST");
        post.setEntity(entity);

        configuration.executeRequest(post);
    }
}
