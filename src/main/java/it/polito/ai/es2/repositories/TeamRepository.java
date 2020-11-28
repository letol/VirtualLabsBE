package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
