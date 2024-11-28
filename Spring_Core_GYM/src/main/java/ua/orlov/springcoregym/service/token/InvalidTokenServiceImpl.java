package ua.orlov.springcoregym.service.token;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.orlov.springcoregym.dao.token.InvalidTokenRepository;
import ua.orlov.springcoregym.model.token.InvalidToken;

@Service
@AllArgsConstructor
public class InvalidTokenServiceImpl implements InvalidTokenService {

    private final InvalidTokenRepository invalidTokenRepository;

    @Override
    public InvalidToken getByToken(String token) {
        return invalidTokenRepository.getByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token not found = " + token));
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            getByToken(token);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    @Override
    public void invalidateToken(String token) {
        InvalidToken invalidToken = InvalidToken.builder()
                .token(token)
                .build();

        invalidTokenRepository.save(invalidToken);
    }
}
