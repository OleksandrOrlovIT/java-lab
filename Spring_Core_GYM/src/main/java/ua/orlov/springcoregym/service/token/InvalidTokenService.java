package ua.orlov.springcoregym.service.token;

import ua.orlov.springcoregym.model.token.InvalidToken;

/**
 * Service interface for managing invalid or blacklisted tokens.
 */
public interface InvalidTokenService {

    /**
     * Retrieves an invalid token entity by its token value.
     *
     * @param token the token value to retrieve
     * @return the invalid token entity associated with the token
     * @throws jakarta.persistence.EntityNotFoundException if the token is not found
     */
    InvalidToken getByToken(String token);

    /**
     * Checks if the specified token is blacklisted.
     *
     * @param token the token value to check
     * @return true if the token is blacklisted; false otherwise
     */
    boolean isTokenBlacklisted(String token);

    /**
     * Marks the specified token as invalid by adding it to the blacklist.
     *
     * @param token the token to blacklist
     */
    void invalidateToken(String token);
}
