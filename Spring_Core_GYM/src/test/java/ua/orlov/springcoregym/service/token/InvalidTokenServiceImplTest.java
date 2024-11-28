package ua.orlov.springcoregym.service.token;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.orlov.springcoregym.dao.token.InvalidTokenRepository;
import ua.orlov.springcoregym.model.token.InvalidToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvalidTokenServiceImplTest {

    private static final String TOKEN = "TOKEN";

    @Mock
    private InvalidTokenRepository invalidTokenRepository;

    @InjectMocks
    private InvalidTokenServiceImpl invalidTokenService;

    @Test
    void getByTokenThenException() {
        when(invalidTokenRepository.getByToken(TOKEN)).thenReturn(Optional.empty());

        var e = assertThrows(EntityNotFoundException.class, () -> invalidTokenService.getByToken(TOKEN));

        assertEquals("Token not found = " + TOKEN, e.getMessage());
        verify(invalidTokenRepository, times(1)).getByToken(any());
    }

    @Test
    void getByTokenThenSuccess() {
        when(invalidTokenRepository.getByToken(TOKEN)).thenReturn(Optional.of(new InvalidToken()));

        assertNotNull(invalidTokenService.getByToken(TOKEN));
        verify(invalidTokenRepository, times(1)).getByToken(any());
    }


    @Test
    void isTokenBlacklistedThenFalse() {
        when(invalidTokenRepository.getByToken(TOKEN)).thenReturn(Optional.empty());

        assertFalse(invalidTokenService.isTokenBlacklisted(TOKEN));
        verify(invalidTokenRepository, times(1)).getByToken(any());
    }

    @Test
    void isTokenBlacklistedThenTrues() {
        when(invalidTokenRepository.getByToken(TOKEN)).thenReturn(Optional.of(new InvalidToken()));

        assertTrue(invalidTokenService.isTokenBlacklisted(TOKEN));
        verify(invalidTokenRepository, times(1)).getByToken(any());
    }

    @Test
    void invalidateTokenThenSuccess() {
        when(invalidTokenRepository.save(any())).thenReturn(new InvalidToken());

        invalidTokenService.invalidateToken(TOKEN);

        verify(invalidTokenRepository, times(1)).save(any());
    }
}
