package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {
}
