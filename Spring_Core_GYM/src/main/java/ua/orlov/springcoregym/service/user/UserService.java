package ua.orlov.springcoregym.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import ua.orlov.springcoregym.model.user.User;

/**
 * Service interface for managing user-related operations.
 * Extends Spring Security's {@link UserDetailsService} to provide user detail loading functionality.
 */
public interface UserService extends UserDetailsService {

    /**
     * Checks if the given username matches the provided password.
     *
     * @param username the username of the user
     * @param password the password to match
     * @return {@code true} if the password matches the stored password for the user, {@code false} otherwise
     */
    boolean isUserNameMatchPassword(String username, String password);

    /**
     * Changes the password for the specified user.
     * If the new password is null or does not meet the specified length, a new password will be generated.
     *
     * @param username the username of the user whose password is to be changed
     * @param oldPassword the current password of the user
     * @param newPassword the new password to set for the user
     * @return {@code true} if the password was successfully changed, {@code false} otherwise
     * @throws org.springframework.security.authentication.BadCredentialsException if the old password does not
     * match the stored password
     */
    boolean changeUserPassword(String username, String oldPassword, String newPassword);

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the {@link User} object associated with the specified username
     * @throws jakarta.persistence.EntityNotFoundException if no user is found with the specified username
     */
    User getByUsername(String username);

    /**
     * Retrieves the currently authenticated user.
     *
     * @return the {@link User} object representing the currently authenticated user
     */
    User getCurrentUser();
}
