package it.polito.ai.es2.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.es2.HomeworkId;
import it.polito.ai.es2.dtos.*;
import it.polito.ai.es2.entities.*;
import it.polito.ai.es2.TeamStatus;
import it.polito.ai.es2.exceptions.*;
import it.polito.ai.es2.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    byte[] defaultAvatar;

    @Autowired
    CourseRepository courseRepo;

    @Autowired
    StudentRepository studentRepo;

    @Autowired
    TeacherRepository teacherRepo;

    @Autowired
    TeamRepository teamRepo;
    
    @Autowired
    AssignmentRepository assignmentRepo;

    @Autowired
    HomeworkRepository homeworkRepo;

    @Autowired
    HomeworkVersionRepository homeworkVersionRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserManagementService userManagementService;

    public TeamServiceImpl() throws IOException {
        File defaultAvatarFile = ResourceUtils.getFile("classpath:img/default_user_avatar.png");
        defaultAvatar = Files.readAllBytes(defaultAvatarFile.toPath());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TEACHER') and #teacherId == authentication.principal.username")
    public boolean addCourse(CourseDTO courseDTO, String teacherId) {
        if (courseRepo.existsById(courseDTO.getName()))
            return false;
        else {
            Teacher teacher = teacherRepo.findById(teacherId)
                    .orElseThrow(TeacherNotFoundException::new);
            Course course = modelMapper.map(courseDTO, Course.class);
            teacher.addCourse(course);
            return true;
        }
    }

    @Override
    public Optional<CourseDTO> getCourse(String name) {
        return courseRepo.findById(name).map(c -> modelMapper.map(c, CourseDTO.class));
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepo.findAll()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addStudent(StudentDTO studentDTO) {
        if (studentRepo.existsById(studentDTO.getId()))
            return false;
        else {
            if (studentDTO.getAvatar() == null) {
                studentDTO.setAvatar(defaultAvatar);
            }
            Student student = modelMapper.map(studentDTO, Student.class);
            studentRepo.save(student);
            return true;
        }
    }

    @Override
    public void addAuthToStudent(StudentDTO studentDTO, User authUser) {
        Student student = studentRepo.findById(studentDTO.getId()).orElseThrow(StudentNotFoundException::new);
        student.setAuthUser(authUser);
        studentRepo.save(student);
    }

    @Override
    public Optional<StudentDTO> getStudent(String studentId) {
        return studentRepo.findById(studentId).map(s -> modelMapper.map(s, StudentDTO.class));
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepo.findAll()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addTeacher(TeacherDTO teacherDTO) {
        if (teacherRepo.existsById(teacherDTO.getId()))
            return false;
        else {
            if(teacherDTO.getAvatar() == null)
                teacherDTO.setAvatar(defaultAvatar);
            Teacher teacher = modelMapper.map(teacherDTO, Teacher.class);
            teacherRepo.save(teacher);
            return true;
        }
    }

    @Override
    public void addAuthToTeacher(TeacherDTO teacherDTO, User authUser) {
        Teacher teacher = teacherRepo.findById(teacherDTO.getId()).orElseThrow(TeacherNotFoundException::new);
        teacher.setAuthUser(authUser);
        teacherRepo.save(teacher);
    }

    @Override
    public Optional<TeacherDTO> getTeacher(String teacherId) {
        return teacherRepo.findById(teacherId).map(t -> modelMapper.map(t, TeacherDTO.class));
    }

    @Override
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepo.findAll()
                .stream()
                .map(t -> modelMapper.map(t, TeacherDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
    public List<StudentDTO> getEnrolledStudents(String courseName) {
        Optional<Course> course = courseRepo.findById(courseName);
        if (course.isPresent()) {
            return course.get().getStudents()
                    .stream()
                    .map(s -> modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        } else throw new CourseNotFoundException("Course '" + courseName + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.teacherHasCourse(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public boolean addStudentToCourse(String studentId, String courseName) {
        Optional<Student> student = studentRepo.findById(studentId);
        if (!student.isPresent()) throw new StudentNotFoundException("Student id '" + studentId + "' not found!");

        Optional<Course> course = courseRepo.findById(courseName);
        if (!course.isPresent()) throw new CourseNotFoundException("Course '" + courseName + "' not found!");

        return student.get().addCourse(course.get());
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.teacherHasCourse(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public void enableCourse(String courseName) {
        Optional<Course> course = courseRepo.findById(courseName);
        if (course.isPresent())
            course.get().setEnabled(true);
        else throw new CourseNotFoundException("Course '" + courseName + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.teacherHasCourse(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public void disableCourse(String courseName) {
        Optional<Course> course = courseRepo.findById(courseName);
        if (course.isPresent())
            course.get().setEnabled(false);
        else throw new CourseNotFoundException("Course '" + courseName + "' not found!");
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
    public List<Boolean> addAll(List<StudentDTO> students) {
        return students.stream()
                .map(this::addStudent)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.teacherHasCourse(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public List<Boolean> enrollAll(List<String> studentIds, String courseName) {
        return studentIds.stream()
                .map(s -> addStudentToCourse(s, courseName))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.teacherHasCourse(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public List<Boolean> addAndEnroll(Reader r, String courseName) {
        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder<StudentDTO>(r)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<@Valid StudentDTO> studentDTOList = csvToBean.parse();
        addAll(studentDTOList);
        return enrollAll(studentDTOList.stream()
                .map(StudentDTO::getId)
                .collect(Collectors.toList()), courseName);
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and #studentId == authentication.principal.username) or hasRole('ROLE_ADMIN')")
    public List<CourseDTO> getCourses(String studentId) {
        Optional<Student> student = studentRepo.findById(studentId);
        if (student.isPresent())
            return student.get().getCourses()
                    .stream()
                    .map(c -> modelMapper.map(c, CourseDTO.class))
                    .collect(Collectors.toList());
        else throw new StudentNotFoundException("Student id '" + studentId + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and #studentId == authentication.principal.username) or hasRole('ROLE_ADMIN')")
    public List<TeamDTO> getTeamsForStudent(String studentId) {
        Optional<Student> student = studentRepo.findById(studentId);
        if (student.isPresent())
            return student.get().getTeams()
                    .stream()
                    .map(t -> modelMapper.map(t, TeamDTO.class))
                    .collect(Collectors.toList());
        else throw new StudentNotFoundException("Student id '" + studentId + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER') and #teacherId == authentication.principal.username) or hasRole('ROLE_ADMIN')")
    public List<CourseDTO> getCoursesForTeacher(String teacherId) {
        Optional<Teacher> teacher = teacherRepo.findById(teacherId);
        if (teacher.isPresent())
            return teacher.get().getCourses()
                    .stream()
                    .map(c -> modelMapper.map(c, CourseDTO.class))
                    .collect(Collectors.toList());
        else throw new TeacherNotFoundException("Teacher id '" + teacherId + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentEnrolledInCourseOfTeam(authentication.principal.username,#teamId))or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.teacherHasCourseOfTeam(authentication.principal.username,#teamId))")
    public List<StudentDTO> getMembers(Long teamId) {
        Optional<Team> team = teamRepo.findById(teamId);
        if (team.isPresent())
            return team.get().getMembers()
                    .stream()
                    .map(s -> modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        else throw new TeamNotFoundException("Team id '" + teamId + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT')  and @permissionEvaluator.studentEnrolledInCourse(authentication.principal.username,#courseId)) or hasRole('ROLE_ADMIN')")
    public TeamDTO proposeTeam(String courseId, String name, List<String> memberIds) {
        Optional<Course> courseOptional = courseRepo.findById(courseId);
        if (!courseOptional.isPresent())
            throw new CourseNotFoundException("Course '" + courseId + "' not found!");
        Course course = courseOptional.get();

        if (!course.isEnabled())
            throw new CourseNotEnabledException("Course not yet enabled!");

        if (!memberIds.contains(SecurityContextHolder.getContext().getAuthentication().getName()))
            throw new TeamServiceException("Auth Student not present");

        if (memberIds.size() < course.getMin())
            throw new TeamMembersMinNotReachedException("Not enough members! Given " + memberIds.size() + ". Should be at least " + course.getMin() + ".");

        if (memberIds.size() > course.getMax())
            throw new TeamMembersMaxExceededException("Too many members! Given " + memberIds.size() + ". Should be at most " + course.getMax() + ".");

        Set<String> memberIdsSet = new HashSet<>(memberIds);
        if (memberIdsSet.size() < memberIds.size())
            throw new StudentDuplicatedException("Duplicated student in list!");

        List<Student> newMembers = studentRepo.findAllById(memberIdsSet);
        if (newMembers.size() < memberIdsSet.size())
            throw new StudentNotFoundException("One or more students not found!");

        if (!course.getStudents().containsAll(newMembers))
            throw new StudentNotEnrolledToCourseException("One or more students not enrolled to course '" + courseId + "'!");

        for (Team team: course.getTeams()) {
            List<Student> teamMembers = team.getMembers();
            for (Student student: newMembers) {
                if (teamMembers.contains(student))
                    throw new StudentAlreadyMemberForCourseException("Student '" + student.getId() + "' already has a team!");
            }
        }

        Team newTeam = teamRepo.save(new Team(name));
        newMembers.forEach(newTeam::addMember);
        newTeam.setCourse(course);

        TeamDTO newTeamDTO = modelMapper.map(newTeam, TeamDTO.class);
        notificationService.notifyTeam(newTeamDTO, memberIds);

        return newTeamDTO;
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentEnrolledInCourse(authentication.principal.username,#courseName)) or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.teacherHasCourse(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public List<TeamDTO> getTeamsForCourse(String courseName) {
        Optional<Course> course = courseRepo.findById(courseName);
        if (course.isPresent())
            return course.get().getTeams()
                    .stream()
                    .map(t -> modelMapper.map(t, TeamDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException("Course '" + courseName + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentEnrolledInCourse(authentication.principal.username,#courseName)) or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.teacherHasCourse(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public List<StudentDTO> getStudentsInTeams(String courseName) {
        if (courseRepo.findById(courseName).isPresent())
            return courseRepo.getStudentsInTeams(courseName)
                    .stream()
                    .map(s -> modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException("Course '" + courseName + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentEnrolledInCourse(authentication.principal.username,#courseName)) or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.teacherHasCourse(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public List<StudentDTO> getAvailableStudents(String courseName) {
        if (courseRepo.findById(courseName).isPresent())
            return courseRepo.getStudentsNotInTeams(courseName)
                    .stream()
                    .map(s -> modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException("Course '" + courseName + "' not found!");
    }

    @Override
    public void enableTeam(Long teamId) {
        Optional<Team> team = teamRepo.findById(teamId);
        if (team.isPresent())
            team.get().setStatus(TeamStatus.ACTIVE);
        else throw new TeamNotFoundException("Team id '" + teamId + "' not found!");
    }

    @Override
    public void evictTeam(Long teamId) {
        Optional<Team> team = teamRepo.findById(teamId);
        if (team.isPresent())
            teamRepo.delete(team.get());
        else throw new TeamNotFoundException("Team id '" + teamId + "' not found!");
    }

    @Override
    public CourseDTO getCourseOfTeam(Long teamId) {
        Optional<Team> team = teamRepo.findById(teamId);
        if (team.isPresent())
            return modelMapper.map(team.get().getCourse(), CourseDTO.class);
        else throw new TeamNotFoundException("Team id '" + teamId + "' not found!");
    }

    @Override
    public List<CourseDTO> getTeacherCourses(String professor){
        Optional<Teacher> professor1 = teacherRepo.findById(professor);
        if(professor1.isPresent())
        {
            return professor1.get().getCourses().
                    stream().map(p -> modelMapper.map(p,CourseDTO.class)).collect(Collectors.toList());
        } else throw new TeacherNotFoundException();
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.teacherHasCourse(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public AssignmentDTO addAssignment(AssignmentDTO assignmentDTO, String courseName) {
        Course course = courseRepo.findById(courseName).orElseThrow(CourseNotFoundException::new);
        Assignment assignment = modelMapper.map(assignmentDTO, Assignment.class);
        Assignment finalAssignment = assignmentRepo.save(assignment);
        course.addAssignment(finalAssignment);

        course.getStudents().forEach(
                student -> {
                    HomeworkDTO homeworkDTO = HomeworkDTO.builder()
                            .id(new HomeworkId(finalAssignment.getId(), student.getId()))
                            .status(Homework.homeworkStatus.NULL)
                            .build();
                    Homework finalHomework = homeworkRepo.save(
                            modelMapper.map(homeworkDTO, Homework.class)
                    );
                    finalHomework.setAssignment(finalAssignment);
                    finalHomework.setStudent(student);
                }
        );

        return modelMapper.map(finalAssignment, AssignmentDTO.class);
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentEnrolledInCourse(authentication.principal.username,#courseName)) or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.teacherHasCourse(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public List<AssignmentDTO> getAssignmentsForCourse(String courseName) {
        Course course = courseRepo.findById(courseName).orElseThrow(CourseNotFoundException::new);
        return assignmentRepo.findAllByCourse(course).stream()
                .map(assignment -> modelMapper.map(assignment, AssignmentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentEnrolledInCourseOfAssignment(authentication.principal.username,#assignmentId)) or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.teacherHasCourseOfAssignment(authentication.principal.username,#assignmentId)) or hasRole('ROLE_ADMIN')")
    public AssignmentDTO getAssignment(Long assignmentId) {
        AssignmentDTO assignmentDTOOptional = assignmentRepo.findById(assignmentId)
                .map(assignment -> modelMapper.map(assignment, AssignmentDTO.class))
                .orElseThrow(AssignmentNotFoundException::new);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().contains("ROLE_STUDENT")) {
            UserDetails principal = (UserDetails) authentication.getPrincipal();
            Homework homework = homeworkRepo.findById(new HomeworkId(assignmentId, principal.getUsername()))
                    .orElseThrow(HomeworkNotFoundException::new);
            homework.setStatus(Homework.homeworkStatus.READ);
        }

        return assignmentDTOOptional;
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.teacherHasCourseOfAssignment(authentication.principal.username,#assignmentId)) or hasRole('ROLE_ADMIN')")
    public List<HomeworkDTO> getHomeworksForAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepo.findById(assignmentId).orElseThrow(AssignmentNotFoundException::new);
        return homeworkRepo.findAllByAssignment(assignment).stream()
                .map(homework -> modelMapper.map(homework, HomeworkDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentHasHomework(authentication.principal.username,#homeworkId)) or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.teacherHasCourseOfAssignment(authentication.principal.username,#homeworkId.assignment_id)) or hasRole('ROLE_ADMIN')")
    public HomeworkDTO getHomework(HomeworkId homeworkId) {
        return homeworkRepo.findById(homeworkId)
                .map(homework -> modelMapper.map(homework, HomeworkDTO.class))
                .orElseThrow(HomeworkNotFoundException::new);
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentHasHomework(authentication.principal.username,#homeworkId))")
    public HomeworkVersionDTO submitHomeworkVersion(HomeworkVersionDTO homeworkVersionDTO, HomeworkId homeworkId) {
        Homework homework = homeworkRepo.findById(homeworkId).orElseThrow(HomeworkNotFoundException::new);
        if (homework.isCanSubmit()) {
            HomeworkVersion homeworkVersion = modelMapper.map(homeworkVersionDTO, HomeworkVersion.class);
            HomeworkVersion finalHomeworkVersion = homeworkVersionRepo.save(homeworkVersion);
            homework.addHomeworkVersion(finalHomeworkVersion);
            homework.setStatus(Homework.homeworkStatus.SUBMITTED);
            return modelMapper.map(finalHomeworkVersion, HomeworkVersionDTO.class);
        } else throw new HomeworkCannotBeSubmittedException();
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.teacherHasCourseOfAssignment(authentication.principal.username,#homeworkId.assignment_id)) or hasRole('ROLE_ADMIN')")
    public HomeworkVersionDTO reviewHomeworkVersion(HomeworkVersionDTO homeworkVersionDTO, HomeworkId homeworkId, boolean canReSubmit) {
        Homework homework = homeworkRepo.findById(homeworkId).orElseThrow(HomeworkNotFoundException::new);
        HomeworkVersion homeworkVersion = modelMapper.map(homeworkVersionDTO, HomeworkVersion.class);
        HomeworkVersion finalHomeworkVersion = homeworkVersionRepo.save(homeworkVersion);
        homework.addHomeworkVersion(finalHomeworkVersion);
        homework.setStatus(Homework.homeworkStatus.REVIEWED);
        homework.setCanSubmit(canReSubmit);
        return modelMapper.map(finalHomeworkVersion, HomeworkVersionDTO.class);
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentHasHomework(authentication.principal.username,#homeworkId)) or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.teacherHasCourseOfAssignment(authentication.principal.username,#homeworkId.assignment_id)) or hasRole('ROLE_ADMIN')")
    public List<HomeworkVersionDTO> getHomeworkVersions(HomeworkId homeworkId) {
        Homework homework = homeworkRepo.findById(homeworkId).orElseThrow(HomeworkNotFoundException::new);
        return homeworkVersionRepo.findAllByHomework(homework).stream()
                .map(homeworkVersion -> modelMapper.map(homeworkVersion, HomeworkVersionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentHasHomeworkVersion(authentication.principal.username,#homeworkVersionId)) or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.teacherHasCourseOfHomeworkVersion(authentication.principal.username,#homeworkVersionId)) or hasRole('ROLE_ADMIN')")
    public HomeworkVersionDTO getHomeworkVersion(Long homeworkVersionId) {
        return homeworkVersionRepo.findById(homeworkVersionId)
                .map(homeworkVersion -> modelMapper.map(homeworkVersion, HomeworkVersionDTO.class))
                .orElseThrow(HomeworkVersionNotFoundException::new);
    }
}
