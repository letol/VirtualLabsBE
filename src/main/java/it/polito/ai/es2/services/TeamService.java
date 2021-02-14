package it.polito.ai.es2.services;

import it.polito.ai.es2.HomeworkId;
import it.polito.ai.es2.dtos.*;
import it.polito.ai.es2.entities.VmInstance;
import it.polito.ai.es2.utility.VmStatus;
import it.polito.ai.es2.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    CourseDTO addCourse(CourseDTO course, String teacherId);

    Optional<CourseDTO> getCourse(Long courseId);

    void deleteCourse(Long courseId);

    CourseDTO editCourse(CourseDTO courseDTO, String teacherId);

    List<CourseDTO> getAllCourses();

    boolean addStudent(StudentDTO student);

    void addAuthToStudent(StudentDTO studentDTO, User authUser);

    Optional<StudentDTO> getStudent(String studentId);

    List<StudentDTO> getAllStudents();

    boolean addTeacher(TeacherDTO teacher);

    TeacherDTO addTeacherToCourse(String teacherId, Long courseId);

    TeacherDTO removeTeacherFromCourse(String teacherId, Long courseId);

    void addAuthToTeacher(TeacherDTO teacherDTO, User authUser);

    Optional<TeacherDTO> getTeacher(String teacherId);

    List<TeacherDTO> getAllTeachers();

    List<TeacherDTO> getTeachersOfCourse(Long courseId);

    List<StudentDTO> getEnrolledStudents(Long courseId);

    StudentDTO addStudentToCourse(String studentId, Long courseId);

    StudentDTO removeStudentFromCourse(String studentId, Long courseId);

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

    AssignmentDTO addAssignment(AssignmentDTO assignmentDTO, MultipartFile content, Long courseId) throws IOException, NoSuchAlgorithmException;

    DocumentDTO getDocumentOfAssignment(Long courseId, Long assignmentId) throws IOException;

    List<AssignmentDTO> getAssignmentsForCourse(Long courseId);

    AssignmentDTO getAssignment(Long courseId, Long assignmentId);

    List<HomeworkDTO> getHomeworksForAssignment(Long courseId, Long assignmentId);

    List<HomeworkDTO> getHomeworksForCourse(Long courseId);

    HomeworkDTO getHomework(Long courseId, HomeworkId homeworkId);

    HomeworkVersionDTO submitHomeworkVersion(Long courseId, HomeworkId homeworkId, MultipartFile content) throws IOException, NoSuchAlgorithmException;

    HomeworkVersionDTO reviewHomeworkVersion(Long courseId, HomeworkId homeworkId, MultipartFile content, boolean canReSubmit) throws IOException, NoSuchAlgorithmException;

    HomeworkDTO setScore(Long courseId, HomeworkId homeworkId, int score);

    List<HomeworkVersionDTO> getHomeworkVersions(Long courseId, HomeworkId homeworkId);

    HomeworkVersionDTO getHomeworkVersion(Long courseId, HomeworkId homeworkId, Long homeworkVersionId);

    DocumentDTO getDocumentOfHomeworkVersion(Long courseId, HomeworkId homeworkId, Long homeworkVersionId) throws IOException;

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

    TeamDTO getStudentTeamByCourse(String id, Long courseId);

    List<StudentDTO> getMembersProposal(Long courseId, Long id);

    TeamDTO updateTeam(Long courseId, Long teamId, TeamDTO teamDTO);

    boolean deleteVmInstance(Long vid, Long courseId, Long teamId);

    byte[] showVm(Long vmid, Long tid, Long courseId);

    VmInstanceDTO updateVmInstance(Long vmid, Long tid, Long courseId, VmInstanceDTO vmInstanceDTO);
}
