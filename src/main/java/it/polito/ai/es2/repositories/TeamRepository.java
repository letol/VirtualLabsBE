package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.Student;
import it.polito.ai.es2.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findAllByCourse_IdAndMembersContaining(Long courseId, Student student);
}
