package ua.orlov.springcoregym.service.http;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import ua.orlov.springcoregym.exception.BusinessLogicException;
import ua.orlov.springcoregym.model.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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

        when(httpClient.execute(any(HttpRequest.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(200);

        boolean result = customHttpSenderService.executeRequestWithEntity(request, entity);

        assertTrue(result);
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

        assertThrows(BusinessLogicException.class, () ->customHttpSenderService.executeRequestWithEntity(request, entity));

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


        assertThrows(BusinessLogicException.class, () -> customHttpSenderService.executeRequestWithEntity(request, entity));

        verify(httpClient).execute(any(HttpRequest.class));
        verify(mockResponse, times(2)).getStatusLine();
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

        boolean result = customHttpSenderService.executeRequestWithEntity(request, entity);

        assertTrue(result);
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

        boolean result = customHttpSenderService.executeRequestWithEntity(request, entity);

        assertTrue(result);
        verify(request, times(0)).addHeader("X-Transaction-Id", "12345");
        verify(httpClient).execute(any(HttpRequest.class));

        MDC.clear();
    }

    @Test
    void executeRequestWithEntityShouldHandleIOException() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        String entity = "{\"key\":\"value\"}";

        when(httpClient.execute(any(HttpRequest.class))).thenThrow(new IOException("Simulated IO exception"));

        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> customHttpSenderService.executeRequestWithEntity(request, entity)
        );

        assertEquals("Failed to execute request", exception.getMessage());

        verify(httpClient).execute(any(HttpRequest.class));
    }
}
