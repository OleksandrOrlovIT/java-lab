package ua.orlov.springcoregym.service.security;

/**
 * Service interface for managing login attempts and blocking functionality.
 */
public interface LoginAttemptService {

    /**
     * Increments the failed login attempt count for the specified key.
     *
     * @param key the key (e.g., IP address) associated with the failed login attempt
     */
    void loginFailed(final String key);

    /**
     * Checks if the client associated with the current IP address is blocked due to
     * exceeding the maximum number of login attempts.
     *
     * @return true if the client is blocked; false otherwise
     */
    boolean isBlocked();

    /**
     * Resets the login attempt count for the specified key upon successful login.
     *
     * @param key the key (e.g., IP address) associated with the successful login attempt
     */
    void loginSucceeded(final String key);

    /**
     * Retrieves the IP address of the client making the request. If the "X-Forwarded-For" header
     * is present, it will be used; otherwise, the remote address from the request will be returned.
     *
     * @return the client's IP address
     */
    String getClientIP();

    /**
     * Clears all entries from the login attempts cache, effectively resetting attempt counts.
     */
    void clearCache();
}
