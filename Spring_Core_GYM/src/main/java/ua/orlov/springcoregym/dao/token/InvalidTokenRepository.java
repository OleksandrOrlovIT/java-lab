package ua.orlov.springcoregym.dao.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.orlov.springcoregym.model.token.InvalidToken;

import java.util.Optional;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidToken, Long> {

    Optional<InvalidToken> getByToken(String token);
}
