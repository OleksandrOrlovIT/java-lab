package ua.orlov.gymintegrationaltesting.integration.cucumber.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import ua.orlov.gymintegrationaltesting.integration.cucumber.configuration.CucumberSpringConfiguration;
import ua.orlov.gymintegrationaltesting.model.HttpRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainerWorkloadStep {

    @Autowired
    private CucumberSpringConfiguration configuration;

    @Autowired
    private ObjectMapper objectMapper;

    @When("user sends request to microservice to get trainer summary with username {string}")
    public void sendRequestToMicroserviceToGetTrainerSummaryWithUsername(String username) throws Exception {
        HttpRequest get = configuration
                .createTrainerWorkloadHttpRequest("/api/v1/trainer/summary/month?username=" + username, "GET");

        configuration.executeRequest(get);
    }

    @And("the trainer summary should equal to body:")
    public void theTrainerSummaryShouldEqualToBody(String jsonPayload) throws Exception {
        CloseableHttpResponse response = configuration.getCurrentResponse();

        String actualBody = objectMapper.readTree(response.getEntity().getContent()).get("body").toString();

        assertEquals(jsonPayload, actualBody);
    }

}
