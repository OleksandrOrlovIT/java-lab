package ua.orlov.springcoregym.service.http;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import ua.orlov.springcoregym.model.HttpRequest;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomHttpSenderServiceImplTest {

    @Mock
    private CloseableHttpClient httpClient;

    @InjectMocks
    private CustomHttpSenderServiceImpl customHttpSenderService;

    @Test
    void executeRequestWithEntityShouldHandleStatusCode200() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        String entity = "{\"key\":\"value\"}";

        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        HttpEntity mockHttpEntity = mock(HttpEntity.class);

        when(httpClient.execute(any(HttpRequest.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(200);
        when(mockHttpEntity.getContent()).thenReturn(new ByteArrayInputStream("Success".getBytes()));
        when(mockResponse.getEntity()).thenReturn(mockHttpEntity);

        String result = customHttpSenderService.executeRequestWithEntity(request, entity);

        assertEquals("Success", result);
        verify(httpClient).execute(any(HttpRequest.class));
    }

    @Test
    void executeRequestWithEntityShouldHandleStatusCode100() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        String entity = "{\"key\":\"value\"}";

        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        when(httpClient.execute(any(HttpRequest.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(100);

        String result = customHttpSenderService.executeRequestWithEntity(request, entity);

        assertTrue(result.startsWith("Exception: Request failed: Mock for StatusLine"));
        verify(httpClient, times(1)).execute(any(HttpRequest.class));
    }

    @Test
    void executeRequestWithEntityShouldHandleStatusCode300() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        String entity = "{\"key\":\"value\"}";

        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        when(httpClient.execute(any(HttpRequest.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(300);


        String result = customHttpSenderService.executeRequestWithEntity(request, entity);

        assertEquals("Exception: " + "Request failed: " + mockResponse.getStatusLine(), result);
        verify(httpClient).execute(any(HttpRequest.class));
        verify(mockResponse, times(3)).getStatusLine();
        verify(mockStatusLine, times(1)).getStatusCode();
    }

    @Test
    void executeRequestWithEntityShouldHandleStatusCode200WhenMdcHasTransactionId() throws Exception {
        MDC.put("X-Transaction-Id", "12345");

        HttpRequest request = mock(HttpRequest.class);
        String entity = "{\"key\":\"value\"}";

        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        mockResponse = mock(CloseableHttpResponse.class);
        when(httpClient.execute(any(HttpRequest.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(200);

        StringEntity mockEntity = new StringEntity("Success", ContentType.APPLICATION_JSON);
        when(mockResponse.getEntity()).thenReturn(mockEntity);

        String result = customHttpSenderService.executeRequestWithEntity(request, entity);

        assertEquals("Success", result);
        verify(request).addHeader("X-Transaction-Id", "12345");
        verify(httpClient).execute(any(HttpRequest.class));

        MDC.clear();
    }

    @Test
    void executeRequestWithEntityShouldHandleStatusCode200WhenMdcHasNoTransactionId() throws Exception {
        MDC.clear();

        HttpRequest request = mock(HttpRequest.class);
        String entity = "{\"key\":\"value\"}";

        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        mockResponse = mock(CloseableHttpResponse.class);
        when(httpClient.execute(any(HttpRequest.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(200);

        StringEntity mockEntity = new StringEntity("Success", ContentType.APPLICATION_JSON);
        when(mockResponse.getEntity()).thenReturn(mockEntity);

        String result = customHttpSenderService.executeRequestWithEntity(request, entity);

        assertEquals("Success", result);
        verify(request, times(0)).addHeader("X-Transaction-Id", "12345");
        verify(httpClient).execute(any(HttpRequest.class));

        MDC.clear();
    }
}
