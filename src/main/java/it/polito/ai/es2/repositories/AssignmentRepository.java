package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.Assignment;
import it.polito.ai.es2.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findAllByCourse(Course course);
    List<Assignment> findAllByExpiryDateBefore(Timestamp timestamp);
}
