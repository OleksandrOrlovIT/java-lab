package ua.orlov.springcoregym.service.user;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.orlov.springcoregym.dao.impl.user.UserDao;
import ua.orlov.springcoregym.model.user.User;
import ua.orlov.springcoregym.service.password.PasswordService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter matchPasswordCounter;

    @Mock
    private Counter changePasswordCounter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordService passwordService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter("userService.isUserNameMatchPassword.count"))
                .thenReturn(matchPasswordCounter);
        when(meterRegistry.counter("userService.changeUserPassword.count"))
                .thenReturn(changePasswordCounter);
        userService = new UserServiceImpl(userDao, meterRegistry, passwordEncoder, passwordService);
    }

    @Test
    void isUserNameMatchPasswordThenSuccess() {
        when(userDao.getByUsername(any())).thenReturn(Optional.of(new User()));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        boolean result = userService.isUserNameMatchPassword("username", "password");

        verify(matchPasswordCounter).increment();
        verify(userDao, times(1)).getByUsername(any());
        verify(passwordEncoder, times(1)).matches(any(), any());
        assertTrue(result);
    }

    @Test
    void changeUserPasswordThenSuccess() {
        when(userDao.getByUsername(any())).thenReturn(Optional.of(new User()));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordService.getPasswordLength()).thenReturn(10);
        when(passwordEncoder.encode(any())).thenReturn("");
        when(userDao.changeUserPassword(any(), any())).thenReturn(true);

        boolean result = userService.changeUserPassword("username", "oldPassword", "1234567890");

        verify(changePasswordCounter).increment();
        verify(userDao, times(1)).getByUsername(any());
        verify(passwordEncoder, times(1)).matches(any(), any());
        verify(passwordService, times(1)).getPasswordLength();
        verify(passwordEncoder, times(1)).encode(any());
        verify(userDao, times(1)).changeUserPassword(any(), any());
        assertTrue(result);
    }

    @Test
    void changeUserPasswordGivenNullNewPasswordThenSuccess() {
        when(userDao.getByUsername(any())).thenReturn(Optional.of(new User()));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordService.generatePassword()).thenReturn("password");
        when(passwordEncoder.encode(any())).thenReturn("");
        when(userDao.changeUserPassword(any(), any())).thenReturn(true);

        boolean result = userService.changeUserPassword("username", "oldPassword", null);

        verify(changePasswordCounter).increment();
        verify(userDao, times(1)).getByUsername(any());
        verify(passwordEncoder, times(1)).matches(any(), any());
        verify(passwordService, times(1)).generatePassword();
        verify(passwordEncoder, times(1)).encode(any());
        verify(userDao, times(1)).changeUserPassword(any(), any());
        assertTrue(result);
    }

    @Test
    void changeUserPasswordGivenDiffLengthNewPasswordThenSuccess() {
        String password = "123";

        when(userDao.getByUsername(any())).thenReturn(Optional.of(new User()));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordService.generatePassword()).thenReturn("password");
        when(passwordService.getPasswordLength()).thenReturn(password.length() + 1);
        when(passwordEncoder.encode(any())).thenReturn("");
        when(userDao.changeUserPassword(any(), any())).thenReturn(true);

        boolean result = userService.changeUserPassword("username", "oldPassword", password);

        verify(changePasswordCounter).increment();
        verify(userDao, times(1)).getByUsername(any());
        verify(passwordEncoder, times(1)).matches(any(), any());
        verify(passwordService, times(1)).generatePassword();
        verify(passwordService, times(1)).getPasswordLength();
        verify(passwordEncoder, times(1)).encode(any());
        verify(userDao, times(1)).changeUserPassword(any(), any());
        assertTrue(result);
    }

    @Test
    void changeUserPasswordThenFailure() {
        String oldPassword = "oldPassword";

        when(userDao.getByUsername(any())).thenReturn(Optional.of(new User()));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        var e = assertThrows(BadCredentialsException.class,
                () ->  userService.changeUserPassword("username", oldPassword, oldPassword));

        assertEquals("Wrong password = " + oldPassword, e.getMessage());
        verify(changePasswordCounter).increment();
        verify(userDao, times(1)).getByUsername(any());
        verify(passwordEncoder, times(1)).matches(any(), any());
    }

    @Test
    void getCurrentUserThenSuccess() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("name");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userDao.getByUsername(any())).thenReturn(Optional.of(new User()));

        User user = userService.getCurrentUser();

        assertNotNull(user);
        verify(authentication, times(1)).getName();
        verify(securityContext, times(1)).getAuthentication();
        verify(userDao, times(1)).getByUsername(any());
    }

    @Test
    void getByUsernameThrowsException() {
        String username = "username";

        when(userDao.getByUsername(any())).thenReturn(Optional.empty());

        var e = assertThrows(EntityNotFoundException.class, () -> userService.getByUsername(username));

        assertEquals("User not found with username = " + username, e.getMessage());
    }
}
