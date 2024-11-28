package ua.orlov.springcoregym.service.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Service interface for managing JWT tokens.
 */
public interface JwtService {

    /**
     * Extracts the username from the provided JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    String extractUserName(String token);

    /**
     * Generates a JWT token for the specified user details.
     *
     * @param userDetails the details of the user for whom to generate the token
     * @return a JWT token for the user
     */
    String generateToken(UserDetails userDetails);

    /**
     * Validates the token by checking its expiry and matching it with user details.
     *
     * @param token the JWT token to validate
     * @param userDetails the details of the user to verify the token against
     * @return true if the token is valid; false otherwise
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Generates a JWT token with additional claims for the specified user details.
     *
     * @param extraClaims additional claims to include in the token
     * @param userDetails the details of the user for whom to generate the token
     * @return a JWT token containing the extra claims
     */
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    /**
     * Checks if the specified token is expired.
     *
     * @param token the JWT token
     * @return true if the token has expired; false otherwise
     */
    boolean isTokenExpired(String token);

    /**
     * Extracts the expiration date from the provided JWT token.
     *
     * @param token the JWT token
     * @return the expiration date of the token
     */
    Date extractExpiration(String token);

    /**
     * Extracts all claims from the provided JWT token.
     *
     * @param token the JWT token
     * @return the claims contained in the token
     */
    Claims extractAllClaims(String token);

    /**
     * Retrieves the signing key used for JWT token generation and validation.
     *
     * @return the signing key
     */
    Key getSigningKey();
}
