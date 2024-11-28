package ua.orlov.springcoregym.service.security;

/**
 * Service interface for user authentication operations.
 */
public interface AuthenticationService {

    /**
     * Authenticates the user with the provided username and password, returning a JWT token if successful.
     *
     * @param username the username of the user attempting to log in
     * @param password the password of the user attempting to log in
     * @return a JWT token representing the authenticated session
     */
    String login(String username, String password);

    /**
     * Logs the user out by invalidating the provided token.
     *
     * @param token the JWT token to invalidate for logging out
     */
    void logout(String token);
}
