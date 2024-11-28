package ua.orlov.springcoregym.security.training;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.orlov.springcoregym.model.user.User;
import ua.orlov.springcoregym.service.user.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingSecurityTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private TrainingSecurity trainingSecurity;

    @Test
    void trainingRequestHasLoggedUserThenFoundTraineeSuccess() {
        String traineeUsername = "trainee";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(traineeUsername);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.getByUsername(any()))
                .thenReturn(User.builder().username(traineeUsername).build())
                .thenThrow(EntityNotFoundException.class);

        assertTrue(trainingSecurity.trainingRequestHasLoggedUser(traineeUsername, "trainerUsername"));
        verify(userService, times(2)).getByUsername(any());
    }

    @Test
    void trainingRequestHasLoggedUserThenFoundTrainerSuccess() {
        String trainerUsername = "trainer";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(trainerUsername);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.getByUsername(any()))
                .thenThrow(EntityNotFoundException.class)
                .thenReturn(User.builder().username(trainerUsername).build());

        assertTrue(trainingSecurity.trainingRequestHasLoggedUser("traineeUsername", trainerUsername));
        verify(userService, times(2)).getByUsername(any());
    }

    @Test
    void trainingRequestHasLoggedUserThenFoundNobodyFalse() {
        String username = "user";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.getByUsername(any())).thenThrow(EntityNotFoundException.class);

        assertFalse(trainingSecurity.trainingRequestHasLoggedUser("trainee", "trainer"));
        verify(userService, times(2)).getByUsername(any());
    }

    @Test
    void trainingRequestHasLoggedUserThenFoundWrongUsernamesFalse() {
        String username = "user";

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.getByUsername(any())).thenReturn(User.builder().username(username + "asd").build());

        assertFalse(trainingSecurity.trainingRequestHasLoggedUser("trainee", "trainer"));
        verify(userService, times(2)).getByUsername(any());
    }
}
