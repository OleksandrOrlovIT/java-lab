package ua.orlov.springcoregym.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;


import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void handleNoSuchElementExceptionTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/no-such-element")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"message\":\"No such element\",\"status\":\"NOT_FOUND\"}", content);
    }

    @Test
    void handleAccessDeniedExceptionTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/access-denied")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"message\":\"Access denied Exception\",\"status\":\"FORBIDDEN\"}", content);
    }

    @Test
    void handleEntityNotFoundExceptionTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/entity-not-found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"message\":\"Entity Not Found Exception\",\"status\":\"NOT_FOUND\"}", content);
    }

    @Test
    void handleIllegalArgumentExceptionTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/illegal-argument")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"message\":\"Illegal argument Exception\",\"status\":\"BAD_REQUEST\"}", content);
    }

    @Test
    void handleRuntimeExceptionTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/runtime-exception")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"message\":\"Runtime Exception\",\"status\":\"INTERNAL_SERVER_ERROR\"}", content);
    }

    @Test
    void handleGeneralExceptionTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/general-exception")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"message\":\"Exception\",\"status\":\"INTERNAL_SERVER_ERROR\"}", content);
    }

    @Test
    void handleMethodArgumentTypeMismatchException() throws Exception {
        MvcResult result = mockMvc.perform(get("/method-argument-type-mismatch")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"message\":\"Failed to convert value of type 'null' to required type 'java.lang.Integer'; Type mismatch\",\"status\":\"BAD_REQUEST\"}", content);
    }

    @Test
    void handleNoResourceFoundException() throws Exception {
        MvcResult result = mockMvc.perform(get("/no-resource-found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"message\":\"No static resource /path.\",\"status\":\"NOT_FOUND\"}", content);
    }

    @Test
    void handleMethodArgumentNotValid() {
        BindingResult bindingResult = mock(BindingResult.class);
        ObjectError error = new ObjectError("username", "username is required.");
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(error));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        ResponseEntity<?> responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("{errors=[username is required.], status=BAD_REQUEST}", responseEntity.getBody().toString());
    }

    @Test
    void handleAuthenticationExceptionTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/authentication-exception")
                        .header("Authorization", "Bearer expired-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"message\":\"Authentication failed, invalid credentials\",\"status\":\"UNAUTHORIZED\"}", content);
    }

    @Test
    void handleTooManyAttemptsExceptionTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/too-many-attempts")
                        .header("Authorization", "Bearer retry-later-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals("{\"message\":\"Too many attempts, please try again later.\",\"status\":\"TOO_MANY_REQUESTS\"}", content);
    }
}
