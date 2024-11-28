package ua.orlov.springcoregym.controller.integration.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AllArgsConstructor
public class LoginComponent {

    private final CloseableHttpClient httpClient;

    private final ObjectMapper objectMapper;

    public String loginAsUser(String username, String password) throws Exception {
        UsernamePasswordUser usernamePasswordUser = new UsernamePasswordUser();
        usernamePasswordUser.setUsername(username);
        usernamePasswordUser.setPassword(password);

        String json = objectMapper.writeValueAsString(usernamePasswordUser);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:8443/api/v1/session");
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(200, response.getStatusLine().getStatusCode());
            String responseBody = EntityUtils.toString(response.getEntity());
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("token").asText();
        }
    }

    public void loginWithBadCredentials(String username, String password) throws Exception {
        UsernamePasswordUser usernamePasswordUser = new UsernamePasswordUser();
        usernamePasswordUser.setUsername(username);
        usernamePasswordUser.setPassword(password);

        String json = objectMapper.writeValueAsString(usernamePasswordUser);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost post = new HttpPost("https://localhost:8443/api/v1/session");
        post.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            assertEquals(401, response.getStatusLine().getStatusCode());
        }
    }
}
