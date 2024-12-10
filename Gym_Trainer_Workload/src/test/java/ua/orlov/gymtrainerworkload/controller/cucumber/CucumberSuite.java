package ua.orlov.gymtrainerworkload.controller.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectDirectories;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.context.ActiveProfiles;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@ActiveProfiles("test")
@Suite
@IncludeEngines("cucumber")
@SelectDirectories("src/test/resources/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "ua.orlov.gymtrainerworkload.controller.cucumber")
@ConfigurationParameter(key = Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
public class CucumberSuite {
}
