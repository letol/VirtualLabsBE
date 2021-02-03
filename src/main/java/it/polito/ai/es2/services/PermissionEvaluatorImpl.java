package it.polito.ai.es2.services;

import it.polito.ai.es2.HomeworkId;
import it.polito.ai.es2.entities.*;
import it.polito.ai.es2.repositories.*;
import it.polito.ai.es2.entities.Course;
import it.polito.ai.es2.entities.Student;
import it.polito.ai.es2.entities.Teacher;
import it.polito.ai.es2.entities.Team;
import it.polito.ai.es2.repositories.CourseRepository;
import it.polito.ai.es2.repositories.StudentRepository;
import it.polito.ai.es2.repositories.TeacherRepository;
import it.polito.ai.es2.repositories.TeamRepository;
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
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    HomeworkRepository homeworkRepository;
    @Autowired
    HomeworkVersionRepository homeworkVersionRepository;

    @Override
    public boolean teacherHasCourse(String teacherId, Long course) {
        Optional<Teacher> teacherOptional = professorRepository.findById(teacherId);
        Optional<Course> courseOptional = courseRepository.findById(course);
        if (teacherOptional.isPresent() && courseOptional.isPresent()){
            return teacherOptional.get().getCourses().contains(courseOptional.get());
        }
        return false;
    }

    @Override
    public boolean teacherHasCourseOfTeam(String teacherId, Long teamId) {
        Optional<Teacher> teacherOptional = professorRepository.findById(teacherId);
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teacherOptional.isPresent() && teamOptional.isPresent()){
            return teacherOptional.get().getCourses().contains(teamOptional.get().getCourse());
        }
        return false;
    }

    @Override
    public boolean teacherHasCourseOfAssignment(String teacherId, Long assignmentId) {
        Optional<Teacher> teacherOptional = professorRepository.findById(teacherId);
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);
        if (teacherOptional.isPresent() && assignmentOptional.isPresent()) {
            return teacherOptional.get().getCourses().contains(assignmentOptional.get().getCourse());
        }
        return false;
    }

    @Override
    public boolean teacherHasCourseOfHomeworkVersion(String teacherId, Long homeworkVersionId) {
        Optional<Teacher> teacherOptional = professorRepository.findById(teacherId);
        Optional<HomeworkVersion> homeworkVersionOptional = homeworkVersionRepository.findById(homeworkVersionId);
        if (teacherOptional.isPresent() && homeworkVersionOptional.isPresent()) {
            return teacherOptional.get().getCourses().contains(homeworkVersionOptional.get().getHomework().getAssignment().getCourse());
        }
        return false;
    }

    @Override
    public boolean studentEnrolledInCourse(String studentId, Long course) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        Optional<Course> courseOptional = courseRepository.findById(course);
        if (studentOptional.isPresent() && courseOptional.isPresent()){
            return studentOptional.get().getCourses().contains(courseOptional.get());
        }
        return false;
    }

    @Override
    public boolean studentEnrolledInCourseOfTeam(String studentId, Long teamId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (studentOptional.isPresent() && teamOptional.isPresent()){
            return studentOptional.get().getCourses().contains(teamOptional.get().getCourse());
        }
        return false;
    }

    @Override
    public boolean studentEnrolledInCourseOfAssignment(String studentId, Long assignmentId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(assignmentId);
        if (studentOptional.isPresent() && assignmentOptional.isPresent()) {
            return studentOptional.get().getCourses().contains(assignmentOptional.get().getCourse());
        }
        return false;
    }

    @Override
    public boolean studentHasHomework(String studentId, HomeworkId homeworkId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        Optional<Homework> homeworkOptional = homeworkRepository.findById(homeworkId);
        if (studentOptional.isPresent() && homeworkOptional.isPresent()) {
            return studentOptional.get().getHomeworks().contains(homeworkOptional.get());
        }
        return false;
    }

    @Override
    public boolean studentHasHomeworkVersion(String studentId, Long homeworkVersionId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        Optional<HomeworkVersion> homeworkVersionOptional = homeworkVersionRepository.findById(homeworkVersionId);
        if (studentOptional.isPresent() && homeworkVersionOptional.isPresent()) {
            return studentOptional.get().getHomeworks().contains(homeworkVersionOptional.get().getHomework());
        }
        return false;
    }

}
