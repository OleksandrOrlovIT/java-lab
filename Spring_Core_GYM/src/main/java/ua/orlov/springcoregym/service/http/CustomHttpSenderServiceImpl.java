package ua.orlov.springcoregym.service.http;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import ua.orlov.springcoregym.exception.BusinessLogicException;
import ua.orlov.springcoregym.model.HttpRequest;

import java.io.IOException;

@Service
@Log4j2
@AllArgsConstructor
public class CustomHttpSenderServiceImpl implements CustomHttpSenderService {

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    private final CloseableHttpClient httpClient;

    @Override
    public boolean executeRequestWithEntity(HttpRequest request, String entity) {
        String transactionId = MDC.get(TRANSACTION_ID_HEADER);
        if (transactionId != null) {
            request.addHeader(TRANSACTION_ID_HEADER, transactionId);
        }
        request.addHeader("Connection", "close");

        log.info("Executing request with transactionId: {}", transactionId);

        request.setEntity(new StringEntity(entity, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return handleResponse(response);
        } catch (IOException e) {
            log.error("I/O error occurred during the request", e);
            throw new BusinessLogicException("Failed to execute request", e);
        }
    }

    private boolean handleResponse(CloseableHttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return true;
        } else {
            log.error("Request failed with status code: {}", statusCode);
            throw new BusinessLogicException("Request failed: " + response.getStatusLine());
        }
    }

}
