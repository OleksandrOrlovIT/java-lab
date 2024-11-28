package ua.orlov.springcoregym.service.security.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFailureListenerTest {

    @Mock
    private ObjectFactory<HttpServletRequest> requestFactory;

    @Mock
    private LoginAttemptServiceImpl loginAttemptServiceImpl;

    @InjectMocks
    private AuthenticationFailureListener listener;

    private MockHttpServletRequest request;

    private AuthenticationFailureBadCredentialsEvent event;

    @BeforeEach
    void setUp() {
        event = mock(AuthenticationFailureBadCredentialsEvent.class);

        request = new MockHttpServletRequest();
    }

    @Test
    void onApplicationEventWhenXForwardedForHeaderIsAbsentShouldCallLoginFailedWithRemoteAddr() {
        request.setRemoteAddr("127.0.0.1");

        when(requestFactory.getObject()).thenReturn(request);

        listener.onApplicationEvent(event);

        verify(loginAttemptServiceImpl).loginFailed("127.0.0.1");
    }

    @Test
    void onApplicationEventWhenXForwardedForHeaderDoesNotContainRemoteAddrShouldCallLoginFailedWithRemoteAddr() {
        request.addHeader("X-Forwarded-For", "192.168.1.4");
        request.setRemoteAddr("192.168.1.5");

        when(requestFactory.getObject()).thenReturn(request);

        listener.onApplicationEvent(event);

        verify(loginAttemptServiceImpl).loginFailed("192.168.1.5");
    }

    @Test
    void onApplicationEventXfHeaderEmpty() {
        request.addHeader("X-Forwarded-For", "");
        request.setRemoteAddr("192.168.1.5");

        when(requestFactory.getObject()).thenReturn(request);

        listener.onApplicationEvent(event);

        verify(loginAttemptServiceImpl).loginFailed("192.168.1.5");
    }

    @Test
    void onApplicationEventXfHeaderContainsGetRemoteAddr() {
        request.addHeader("X-Forwarded-For", "192.168.1.5");
        request.setRemoteAddr("192.168.1.5");

        when(requestFactory.getObject()).thenReturn(request);

        listener.onApplicationEvent(event);

        verify(loginAttemptServiceImpl).loginFailed("192.168.1.5");
    }
}
