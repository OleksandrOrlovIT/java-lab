package ua.orlov.springcoregym.security.user;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSecurityTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserSecurity userSecurity;

    @Test
    void isSelfThenSuccess(){
        String username = "testUser";
        User mockUser = new User();
        mockUser.setUsername(username);

        when(authentication.getName()).thenReturn(username);
        when(userService.getByUsername(username)).thenReturn(mockUser);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        boolean result = userSecurity.isSelf(username);

        assertTrue(result);
        verify(authentication, times(1)).getName();
        verify(securityContext, times(1)).getAuthentication();
        verify(userService, times(1)).getByUsername(username);

        SecurityContextHolder.clearContext();
    }
}
