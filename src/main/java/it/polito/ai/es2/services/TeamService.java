package it.polito.ai.es2.services;

import it.polito.ai.es2.HomeworkId;
import it.polito.ai.es2.dtos.*;
import it.polito.ai.es2.utility.VmStatus;
import it.polito.ai.es2.dtos.*;
import it.polito.ai.es2.entities.User;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    CourseDTO addCourse(CourseDTO course, String teacherId);

    Optional<CourseDTO> getCourse(Long courseId);

    List<CourseDTO> getAllCourses();

    boolean addStudent(StudentDTO student);

    void addAuthToStudent(StudentDTO studentDTO, User authUser);

    Optional<StudentDTO> getStudent(String studentId);

    List<StudentDTO> getAllStudents();

    boolean addTeacher(TeacherDTO teacher);

    void addAuthToTeacher(TeacherDTO teacherDTO, User authUser);

    Optional<TeacherDTO> getTeacher(String teacherId);

    List<TeacherDTO> getAllTeachers();

    List<StudentDTO> getEnrolledStudents(Long courseId);

    boolean addStudentToCourse(String studentId, Long courseId);

    void enableCourse(Long courseId);

    void disableCourse(Long courseId);

    List<Boolean> addAll(List<StudentDTO> students);

    List<Boolean> enrollAll(List<String> studentIds, Long courseId);

    List<Boolean> addAndEnroll(Reader r, Long courseId);

    List<CourseDTO> getCourses(String studentId);

    List<TeamDTO> getTeamsForStudent(String studentId);

    List<CourseDTO> getCoursesForTeacher(String teacherId);

    List<StudentDTO> getMembers(Long teamId);

    ProposalNotificationDTO proposeTeam(Long courseId, RequestTeamDTO team);

    List<TeamDTO> getTeamsForCourse(Long courseId);

    List<StudentDTO> getStudentsInTeams(Long courseId);

    List<StudentDTO> getAvailableStudents(Long courseId);

    void enableTeam(Long teamId);

    void evictTeam(Long teamId);

    CourseDTO getCourseOfTeam(Long teamId);

    List<CourseDTO> getTeacherCourses(String professor);

    AssignmentDTO addAssignment(AssignmentDTO assignmentDTO, String courseName);

    List<AssignmentDTO> getAssignmentsForCourse(String courseName);

    AssignmentDTO getAssignment(String courseName, Long assignmentId);

    List<HomeworkDTO> getHomeworksForAssignment(String courseName, Long assignmentId);

    List<HomeworkDTO> getHomeworksForCourse(String courseName);

    HomeworkDTO getHomework(String courseName, HomeworkId homeworkId);

    HomeworkVersionDTO submitHomeworkVersion(String courseName, HomeworkVersionDTO homeworkVersionDTO, HomeworkId homeworkId);

    HomeworkVersionDTO reviewHomeworkVersion(String courseName, HomeworkVersionDTO homeworkVersionDTO, HomeworkId homeworkId, boolean canReSubmit);

    void setScore(String courseName, HomeworkId homeworkId, int score);

    List<HomeworkVersionDTO> getHomeworkVersions(String courseName, HomeworkId homeworkId);

    HomeworkVersionDTO getHomeworkVersion(String courseName, HomeworkId homeworkId, Long homeworkVersionId);

    List<String> submitHomeworksOfExpiredAssignments();

    VmModelDTO addVmModel(VmModelDTO vmModelDTO, Long courseId);

    VmModelDTO getVmModel(Long courseId);

    VmInstanceDTO createVmInstance (VmInstanceDTO vmInstanceDTO, Long courseId, Long teamId);

    VmInstanceDTO getVmInstanceOfTeam(Long vmId, Long courseId, Long teamId);

    List<VmInstanceDTO> getVmInstancesOfTeam(Long courseId, Long teamId);

    List<StudentDTO> getStudentsInATeam(Long courseId,Long id);

    VmInstanceDTO changeStatusVM(VmStatus command, Long courseId, Long tid, Long vmid);

    List<Boolean> addOwnersVM(List<String> studentsId, Long vmId, Long teamId, Long courseId);

    List<StudentDTO> getOwnersVm(Long vmId, Long teamId, Long courseId);

    StudentDTO getCreatorVm(Long vmId, Long teamId, Long courseId);

    List<ProposalNotificationDTO> getNotificationsForStudent(Long courseId);

    StudentDTO getCreatorProposal(Long name, Long id);
}
