package ua.orlov.springcoregym.service.security.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ua.orlov.springcoregym.exception.BusinessLogicException;
import ua.orlov.springcoregym.model.user.User;
import ua.orlov.springcoregym.service.security.JwtService;
import ua.orlov.springcoregym.service.token.InvalidTokenService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    private final String jwtSigningKey;

    private final int expirationTime;

    private final InvalidTokenService invalidTokenService;

    public JwtServiceImpl(@Value("${token.signing.key}") String jwtSigningKey,
                          @Value("${expiration.date.milliseconds}") int expirationTime,
                          InvalidTokenService invalidTokenService) {
        this.jwtSigningKey = jwtSigningKey;
        this.invalidTokenService = invalidTokenService;
        this.expirationTime = expirationTime;
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("userName", customUserDetails.getUsername());
        } else {
            throw new BusinessLogicException("UserDetails must be an instance of User to generate a token.");
        }

        return generateToken(claims, userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);

        isTokenExpired(token);

        return (userName.equals(userDetails.getUsername()))
                && !invalidTokenService.isTokenBlacklisted(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    public Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
