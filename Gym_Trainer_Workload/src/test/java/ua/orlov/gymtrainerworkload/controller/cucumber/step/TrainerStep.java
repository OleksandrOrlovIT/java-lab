package ua.orlov.gymtrainerworkload.controller.cucumber.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import ua.orlov.gymtrainerworkload.controller.cucumber.configuration.CucumberSpringConfiguration;
import ua.orlov.gymtrainerworkload.dto.TrainerWorkload;
import ua.orlov.gymtrainerworkload.model.HttpRequest;
import ua.orlov.gymtrainerworkload.model.Trainer;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        StringEntity entity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);

        HttpRequest post = configuration.createHttpRequest("/api/v1/trainer/workload", "POST");
        post.setEntity(entity);

        configuration.executeRequest(post);
    }

    @When("user sends request to get trainer summary with username {string}")
    public void sendRequestToGetTrainerSummaryWithUsername(String username) throws Exception {
        HttpRequest get = configuration.createHttpRequest("/api/v1/trainer/summary/month?username=" + username, "GET");

        configuration.executeRequest(get);
    }

    @When("user sends request to get trainer summary without request param")
    public void sendRequestToGetTrainerSummaryWithoutRequestParam() throws Exception {
        HttpRequest get = configuration.createHttpRequest("/api/v1/trainer/summary/month", "GET");

        configuration.executeRequest(get);
    }

    @And("the trainer summary should equal to body:")
    public void theTrainerSummaryShouldEqualToBody(String jsonPayload) throws Exception {
        Trainer expectedTrainer = objectMapper.readValue(jsonPayload, Trainer.class);

        CloseableHttpResponse response = configuration.getCurrentResponse();
        Trainer actualTrainer = objectMapper.readValue(response.getEntity().getContent(), Trainer.class);

        assertEquals(expectedTrainer, actualTrainer);
    }
}
