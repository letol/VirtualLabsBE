package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.Course;
import it.polito.ai.es2.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT s FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.id=:courseId")
    List<Student> getStudentsInTeams(Long courseId);

    //@Query("SELECT s FROM Student s LEFT JOIN s.teams t INNER JOIN s.courses c WHERE c.id=:courseId AND t.id IS NULL")
    @Query("SELECT s FROM Student s INNER JOIN s.courses c WHERE c.id=:courseId AND s NOT IN (SELECT s FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.id=:courseId AND t.id IS NOT NULL)")
    List<Student> getStudentsNotInTeams(Long courseId);


}
