package it.polito.ai.es2.repositories;

import it.polito.ai.es2.HomeworkId;
import it.polito.ai.es2.entities.Assignment;
import it.polito.ai.es2.entities.Course;
import it.polito.ai.es2.entities.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, HomeworkId> {
    List<Homework> findAllByAssignment(Assignment assignment);
    List<Homework> findAllByAssignment_Course(Course course);
}
