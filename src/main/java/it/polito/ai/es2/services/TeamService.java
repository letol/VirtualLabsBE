package it.polito.ai.es2.services;

import it.polito.ai.es2.HomeworkId;
import it.polito.ai.es2.dtos.*;
import it.polito.ai.es2.exceptions.TeamServiceException;
import it.polito.ai.es2.utility.VmStatus;
import it.polito.ai.es2.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    CourseDTO addCourse(CourseDTO course, String teacherId) throws TeamServiceException;

    Optional<CourseDTO> getCourse(Long courseId);

    void deleteCourse(Long courseId) throws TeamServiceException;

    CourseDTO editCourse(CourseDTO courseDTO, String teacherId);

    List<CourseDTO> getAllCourses();

    boolean addStudent(StudentDTO student);

    void addAuthToStudent(StudentDTO studentDTO, User authUser, MultipartFile avatar) throws TeamServiceException, IOException;

    Optional<StudentDTO> getStudent(String studentId);

    List<StudentDTO> getAllStudents();

    boolean addTeacher(TeacherDTO teacher);

    TeacherDTO addTeacherToCourse(String teacherId, Long courseId) throws TeamServiceException;

    TeacherDTO removeTeacherFromCourse(String teacherId, Long courseId) throws TeamServiceException;

    void addAuthToTeacher(TeacherDTO teacherDTO, User authUser, MultipartFile avatar) throws TeamServiceException, IOException;

    Optional<TeacherDTO> getTeacher(String teacherId);

    List<TeacherDTO> getAllTeachers();

    List<TeacherDTO> getTeachersOfCourse(Long courseId) throws TeamServiceException;

    List<StudentDTO> getEnrolledStudents(Long courseId) throws TeamServiceException;

    StudentDTO addStudentToCourse(String studentId, Long courseId) throws TeamServiceException;

    StudentDTO removeStudentFromCourse(String studentId, Long courseId) throws TeamServiceException;

    void enableCourse(Long courseId) throws TeamServiceException;

    void disableCourse(Long courseId) throws TeamServiceException;

    List<Boolean> addAll(List<StudentDTO> students);

    List<Boolean> enrollAll(List<String> studentIds, Long courseId);

    List<Boolean> addAndEnroll(Reader r, Long courseId);

    List<CourseDTO> getCourses(String studentId) throws TeamServiceException;

    List<TeamDTO> getTeamsForStudent(String studentId) throws TeamServiceException;

    List<CourseDTO> getCoursesForTeacher(String teacherId) throws TeamServiceException;

    List<StudentDTO> getMembers(Long teamId) throws TeamServiceException;

    ProposalNotificationDTO proposeTeam(Long courseId, RequestTeamDTO team) throws TeamServiceException;

    List<TeamDTO> getTeamsForCourse(Long courseId) throws TeamServiceException;

    List<StudentDTO> getStudentsInTeams(Long courseId) throws TeamServiceException;

    List<StudentDTO> getAvailableStudents(Long courseId)throws TeamServiceException;

    void enableTeam(Long teamId) throws TeamServiceException;

    void evictTeam(Long teamId) throws TeamServiceException;

    CourseDTO getCourseOfTeam(Long teamId) throws TeamServiceException;

    List<CourseDTO> getTeacherCourses(String professor) throws TeamServiceException;

    AssignmentDTO addAssignment(AssignmentDTO assignmentDTO, MultipartFile content, Long courseId) throws IOException, NoSuchAlgorithmException, TeamServiceException;

    DocumentDTO getDocumentOfAssignment(Long courseId, Long assignmentId) throws IOException, TeamServiceException;

    List<AssignmentDTO> getAssignmentsForCourse(Long courseId) throws TeamServiceException;

    AssignmentDTO getAssignment(Long courseId, Long assignmentId) throws TeamServiceException;

    List<HomeworkDTO> getHomeworksForAssignment(Long courseId, Long assignmentId) throws TeamServiceException;

    List<HomeworkDTO> getHomeworksForCourse(Long courseId) throws TeamServiceException;

    HomeworkDTO getHomework(Long courseId, HomeworkId homeworkId) throws TeamServiceException;

    HomeworkVersionDTO submitHomeworkVersion(Long courseId, HomeworkId homeworkId, MultipartFile content) throws IOException, NoSuchAlgorithmException, TeamServiceException;

    HomeworkVersionDTO reviewHomeworkVersion(Long courseId, HomeworkId homeworkId, MultipartFile content, boolean canReSubmit) throws IOException, NoSuchAlgorithmException, TeamServiceException;

    HomeworkDTO setScore(Long courseId, HomeworkId homeworkId, int score) throws TeamServiceException;

    List<HomeworkVersionDTO> getHomeworkVersions(Long courseId, HomeworkId homeworkId) throws TeamServiceException;

    HomeworkVersionDTO getHomeworkVersion(Long courseId, HomeworkId homeworkId, Long homeworkVersionId) throws TeamServiceException;

    DocumentDTO getDocumentOfHomeworkVersion(Long courseId, HomeworkId homeworkId, Long homeworkVersionId) throws IOException, TeamServiceException;

    List<String> submitHomeworksOfExpiredAssignments();

    VmModelDTO addVmModel(VmModelDTO vmModelDTO, Long courseId) throws TeamServiceException;

    VmModelDTO getVmModel(Long courseId) throws TeamServiceException;

    VmInstanceDTO createVmInstance (VmInstanceDTO vmInstanceDTO, Long courseId, Long teamId) throws TeamServiceException;

    VmInstanceDTO getVmInstanceOfTeam(Long vmId, Long courseId, Long teamId) throws TeamServiceException;

    List<VmInstanceDTO> getVmInstancesOfTeam(Long courseId, Long teamId);

    List<StudentDTO> getStudentsInATeam(Long courseId,Long id);

    VmInstanceDTO changeStatusVM(VmStatus command, Long courseId, Long tid, Long vmid) throws TeamServiceException;

    List<Boolean> addOwnersVM(List<String> studentsId, Long vmId, Long teamId, Long courseId) throws TeamServiceException;

    List<StudentDTO> getOwnersVm(Long vmId, Long teamId, Long courseId) throws TeamServiceException;

    StudentDTO getCreatorVm(Long vmId, Long teamId, Long courseId) throws TeamServiceException;

    List<ProposalNotificationDTO> getNotificationsForStudent(Long courseId);

    StudentDTO getCreatorProposal(Long name, Long id) throws TeamServiceException;

    TeamDTO getStudentTeamByCourse(String id, Long courseId) throws TeamServiceException;

    List<StudentDTO> getMembersProposal(Long courseId, Long id) throws TeamServiceException;

    TeamDTO updateTeam(Long courseId, Long teamId, TeamDTO teamDTO) throws TeamServiceException;

    boolean deleteVmInstance(Long vid, Long courseId, Long teamId) throws TeamServiceException;
}
