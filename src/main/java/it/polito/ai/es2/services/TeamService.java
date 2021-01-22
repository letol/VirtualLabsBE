package it.polito.ai.es2.services;

import it.polito.ai.es2.HomeworkId;
import it.polito.ai.es2.dtos.*;
import it.polito.ai.es2.entities.User;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    boolean addCourse(CourseDTO course, String teacherId);

    Optional<CourseDTO> getCourse(String name);

    List<CourseDTO> getAllCourses();

    boolean addStudent(StudentDTO student);

    void addAuthToStudent(StudentDTO studentDTO, User authUser);

    Optional<StudentDTO> getStudent(String studentId);

    List<StudentDTO> getAllStudents();

    boolean addTeacher(TeacherDTO teacher);

    void addAuthToTeacher(TeacherDTO teacherDTO, User authUser);

    Optional<TeacherDTO> getTeacher(String teacherId);

    List<TeacherDTO> getAllTeachers();

    List<StudentDTO> getEnrolledStudents(String courseName);

    boolean addStudentToCourse(String studentId, String courseName);

    void enableCourse(String courseName);

    void disableCourse(String courseName);

    List<Boolean> addAll(List<StudentDTO> students);

    List<Boolean> enrollAll(List<String> studentIds, String courseName);

    List<Boolean> addAndEnroll(Reader r, String courseName);

    List<CourseDTO> getCourses(String studentId);

    List<TeamDTO> getTeamsForStudent(String studentId);

    List<CourseDTO> getCoursesForTeacher(String teacherId);

    List<StudentDTO> getMembers(Long teamId);

    TeamDTO proposeTeam(String courseId, String name, List<String> memberIds);

    List<TeamDTO> getTeamsForCourse(String courseName);

    List<StudentDTO> getStudentsInTeams(String courseName);

    List<StudentDTO> getAvailableStudents(String courseName);

    void enableTeam(Long teamId);

    void evictTeam(Long teamId);

    CourseDTO getCourseOfTeam(Long teamId);

    List<CourseDTO> getTeacherCourses(String professor);

    AssignmentDTO addAssignment(AssignmentDTO assignmentDTO, String courseName);

    List<AssignmentDTO> getAssignmentsForCourse(String courseName);

    AssignmentDTO getAssignment(Long assignmentId);

    List<HomeworkDTO> getHomeworksForAssignment(Long assignmentId);

    HomeworkDTO getHomework(HomeworkId homeworkId);

    HomeworkVersionDTO submitHomeworkVersion(HomeworkVersionDTO homeworkVersionDTO, HomeworkId homeworkId);

    HomeworkVersionDTO reviewHomeworkVersion(HomeworkVersionDTO homeworkVersionDTO, HomeworkId homeworkId, boolean canReSubmit);

    List<HomeworkVersionDTO> getHomeworkVersions(HomeworkId homeworkId);

    HomeworkVersionDTO getHomeworkVersion(Long homeworkVersionId);
}
