package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findAllByExpiryDateBefore(Timestamp t);
    List<Token> findAllByTeamId(Long teamId);
}
