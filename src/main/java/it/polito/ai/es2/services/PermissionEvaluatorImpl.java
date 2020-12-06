package it.polito.ai.es2.services;

import it.polito.ai.es2.entities.Course;
import it.polito.ai.es2.entities.Student;
import it.polito.ai.es2.entities.Teacher;
import it.polito.ai.es2.repositories.CourseRepository;
import it.polito.ai.es2.repositories.StudentRepository;
import it.polito.ai.es2.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.Optional;


@Component(value="permissionEvaluator")
@Transactional
public class PermissionEvaluatorImpl implements PermissionEvaluator {

    @Autowired
    TeacherRepository professorRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    StudentRepository studentRepository;

    @Override
    public boolean courseOwner(String professor, String course) {
        Optional<Teacher> professor1= professorRepository.findById(professor);
        Optional<Course> course1= courseRepository.findById(course);
        if(professor1.isPresent() && course1.isPresent()){
            return professor1.get().getCourses().contains(course1.get());
        }
        return false;
    }

    @Override
    public boolean studentInCourse(String student, String course)
    {
        Optional<Student> student1= studentRepository.findById(student);
        Optional<Course> course1= courseRepository.findById(course);
        if(student1.isPresent() && course1.isPresent()){
            return student1.get().getCourses().contains(course1.get());
        }
        return false;

    }



}
