package ua.orlov.gymintegrationaltesting.integration.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "ua.orlov.gymintegrationaltesting.integration.cucumber",
        plugin = {"pretty", "json:target/cucumber.json"}
)
public class CucumberRunnerIT {}
