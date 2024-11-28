package ua.orlov.springcoregym.security.user;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ua.orlov.springcoregym.model.user.User;
import ua.orlov.springcoregym.service.user.UserService;

@AllArgsConstructor
@Component
public class UserSecurity {

    private final UserService userService;

    public boolean isSelf(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User foundUser = userService.getByUsername(username);
        return foundUser.getUsername().equals(currentUsername);
    }
}
