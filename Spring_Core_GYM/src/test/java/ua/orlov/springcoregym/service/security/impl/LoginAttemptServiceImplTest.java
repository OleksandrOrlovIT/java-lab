package ua.orlov.springcoregym.service.security.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import ua.orlov.springcoregym.service.security.LoginAttemptService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceImplTest {

    private static final String KEY = "KEY";

    @Mock
    private ObjectFactory<HttpServletRequest> requestFactory;

    private LoginAttemptService loginAttemptService;

    private LoadingCache<String, Integer> attemptsCache;

    @BeforeEach
    void setUp() {
        int lockoutDurationMinutes = 5, maxLoginAttempt = 3;

       attemptsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(lockoutDurationMinutes, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build(CacheLoader.from(key -> 0));

        loginAttemptService = new LoginAttemptServiceImpl(attemptsCache, requestFactory, maxLoginAttempt);
    }

    @Test
    void loginFailedThenSuccess() throws ExecutionException {
        loginAttemptService.loginFailed(KEY);

        assertEquals(1, attemptsCache.get(KEY));
    }

    @Test
    void isBlockedThenBlocked() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(KEY);

        when(requestFactory.getObject()).thenReturn(request);

        for(int i = 0; i < 3; i++) {
            loginAttemptService.loginFailed(KEY);
        }

        assertTrue(loginAttemptService.isBlocked());
    }

    @Test
    void loginSucceededThenSuccess() throws ExecutionException {
        for(int i = 0; i < 2; i++) {
            loginAttemptService.loginFailed(KEY);
        }

        assertEquals(2, attemptsCache.get(KEY));

        loginAttemptService.loginSucceeded(KEY);

        assertEquals(0, attemptsCache.get(KEY));
    }

    @Test
    void getClientIPThenSuccess() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(KEY);

        when(requestFactory.getObject()).thenReturn(request);

        assertEquals(KEY, loginAttemptService.getClientIP());
    }

    @Test
    void getClientIPXfHeaderThenSuccess() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String xfHeaderIp = "192.168.1.1";
        request.addHeader("X-Forwarded-For", xfHeaderIp);
        request.setRemoteAddr("10.0.0.1");

        when(requestFactory.getObject()).thenReturn(request);

        assertEquals(xfHeaderIp, loginAttemptService.getClientIP());
    }
}
