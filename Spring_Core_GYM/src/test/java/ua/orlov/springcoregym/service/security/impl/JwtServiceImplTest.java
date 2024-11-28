package ua.orlov.springcoregym.service.security.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.orlov.springcoregym.model.user.User;
import ua.orlov.springcoregym.service.token.InvalidTokenService;

import java.security.Key;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    private InvalidTokenService invalidTokenService;

    private JwtServiceImpl jwtServiceImpl;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = mock(User.class);

        String testSigningKey = "63B74F4E1D5F0B2C4A3F2E685F7A1C534D7E357E1D7F5C3B495C734B64327844";
        int expirationTime = 900000;
        jwtServiceImpl = new JwtServiceImpl(testSigningKey, expirationTime, invalidTokenService);
    }

    @Test
    void extractUserName() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtServiceImpl.generateToken(userDetails);
        String extractedUsername = jwtServiceImpl.extractUserName(token);

        assertEquals("testUser", extractedUsername, "The extracted username should match the userDetails username.");
    }

    @Test
    void generateToken() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtServiceImpl.generateToken(userDetails);

        assertNotNull(token, "Token should not be null.");
        assertTrue(token.startsWith("ey"), "Token should be a valid JWT format.");

        Claims claims = Jwts.parser().setSigningKey(jwtServiceImpl.getSigningKey()).build().parseClaimsJws(token).getBody();
        assertEquals("testUser", claims.getSubject(), "The subject in the token should match the username.");
    }

    @Test
    void isTokenValid() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtServiceImpl.generateToken(userDetails);

        when(invalidTokenService.isTokenBlacklisted(token)).thenReturn(false);

        assertTrue(jwtServiceImpl.isTokenValid(token, userDetails), "Token should be valid for matching username and not expired.");

        when(invalidTokenService.isTokenBlacklisted(token)).thenReturn(true);
        assertFalse(jwtServiceImpl.isTokenValid(token, userDetails), "Token should be invalid if blacklisted.");
    }

    @Test
    void isTokenExpired() {
        when(userDetails.getUsername()).thenReturn("testUser");
        Map<String, Object> claims = new HashMap<>();
        String validToken = jwtServiceImpl.generateToken(claims, userDetails);

        assertFalse(jwtServiceImpl.isTokenExpired(validToken), "Token should not be expired immediately after generation.");

        String expiredToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(jwtServiceImpl.getSigningKey())
                .compact();

        assertThrows(ExpiredJwtException.class,
                () -> jwtServiceImpl.isTokenExpired(expiredToken), "Token with past expiration date should be expired.");
    }

    @Test
    void extractExpiration() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtServiceImpl.generateToken(userDetails);

        Date expiration = jwtServiceImpl.extractExpiration(token);

        assertNotNull(expiration, "The expiration date should not be null.");
        assertTrue(expiration.after(new Date()), "The expiration date should be in the future.");
    }

    @Test
    void extractAllClaims() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtServiceImpl.generateToken(userDetails);

        Claims claims = jwtServiceImpl.extractAllClaims(token);

        assertNotNull(claims, "Claims should not be null.");
        assertEquals("testUser", claims.getSubject(), "Claims should contain the username as subject.");
    }

    @Test
    void getSigningKey() {
        Key signingKey = jwtServiceImpl.getSigningKey();

        assertNotNull(signingKey, "Signing key should not be null.");
    }

    @Test
    void generateTokenThenSuccess() {
        User user = User.builder()
                .id(1L)
                .username("us")
                .build();

        assertNotNull(jwtServiceImpl.generateToken(user));
    }

    @Test
    void isTokenValidThenUsernameDoesntEquals() {
        User user = User.builder()
                .id(1L)
                .username("us")
                .build();

        String token = jwtServiceImpl.generateToken(user);

        User user1 = User.builder()
                .id(1L)
                .username("usasd")
                .build();

        assertFalse(jwtServiceImpl.isTokenValid(token, user1));
    }

    @Test
    void generateTokenThenException() {
        UserDetails user = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public String getPassword() {
                return "";
            }

            @Override
            public String getUsername() {
                return "";
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        };

        assertThrows(IllegalArgumentException.class, () -> jwtServiceImpl.generateToken(user));
    }

    @Test
    void isTokenValidThenExpiredToken() throws InterruptedException {
        User user = User.builder()
                .id(1L)
                .username("usasd")
                .build();

        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date())
                .signWith(jwtServiceImpl.getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        Thread.sleep(150);

        assertThrows(ExpiredJwtException.class, () -> jwtServiceImpl.isTokenValid(token, user));
    }
}
