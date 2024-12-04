package ua.orlov.springcoregym.service.security.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import ua.orlov.springcoregym.exception.TooManyAttemptsException;
import ua.orlov.springcoregym.service.token.InvalidTokenService;
import ua.orlov.springcoregym.service.user.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtServiceImpl jwtServiceImpl;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private LoginAttemptServiceImpl loginAttemptServiceImpl;

    @Mock
    private InvalidTokenService invalidTokenService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationServiceImpl;

    @Test
    void loginThenTooManyAttemptsException() {
        when(loginAttemptServiceImpl.isBlocked()).thenReturn(true);

        TooManyAttemptsException e = assertThrows(TooManyAttemptsException.class, () -> authenticationServiceImpl.login("", ""));

        assertEquals("Too many attempts", e.getMessage());
        verify(loginAttemptServiceImpl, times(1)).isBlocked();
    }

    @Test
    void loginThenSuccess() {
        when(loginAttemptServiceImpl.isBlocked()).thenReturn(false);
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("", ""));
        when(jwtServiceImpl.generateToken(any())).thenReturn("");

        assertNotNull(authenticationServiceImpl.login("", ""));

        verify(loginAttemptServiceImpl, times(1)).isBlocked();
        verify(authenticationManager, times(1)).authenticate(any());
        verify(loginAttemptServiceImpl, times(1)).loginSucceeded(any());
        verify(jwtServiceImpl, times(1)).generateToken(any());
    }

    @Test
    void loginThenException() {
        when(loginAttemptServiceImpl.isBlocked()).thenReturn(false);
        when(authenticationManager.authenticate(any()))
                .thenThrow(new AuthenticationException(""){});

        assertThrows(Exception.class, () -> authenticationServiceImpl.login("", ""));

        verify(loginAttemptServiceImpl, times(1)).isBlocked();
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void logoutThenSuccessWithBearer() {
        String token = "Bearer asdasdasd";

        assertDoesNotThrow(() -> authenticationServiceImpl.logout(token));

        verify(invalidTokenService, times(1)).invalidateToken(any());
    }

    @Test
    void logoutThenSuccessWithoutBearer() {
        String token = "asdasdasd";

        assertDoesNotThrow(() -> authenticationServiceImpl.logout(token));

        verify(invalidTokenService, times(1)).invalidateToken(any());
    }
}
