package it.polito.ai.es2.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.es2.VmStatus;
import it.polito.ai.es2.dtos.*;
import it.polito.ai.es2.entities.*;
import it.polito.ai.es2.TeamStatus;
import it.polito.ai.es2.exceptions.*;
import it.polito.ai.es2.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    ModelMapper modelMapper;

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserManagementService userManagementService;

    @Autowired
    VmModelRepository vmModelRepository;

    @Autowired
    VmIstanceRepository vmIstanceRepository;


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
            if (teacherDTO.getAvatar() == null)
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
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.courseOwner(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public boolean addStudentToCourse(String studentId, String courseName) {
        Optional<Student> student = studentRepo.findById(studentId);
        if (!student.isPresent()) throw new StudentNotFoundException("Student id '" + studentId + "' not found!");

        Optional<Course> course = courseRepo.findById(courseName);
        if (!course.isPresent()) throw new CourseNotFoundException("Course '" + courseName + "' not found!");

        return student.get().addCourse(course.get());
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.courseOwner(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public void enableCourse(String courseName) {
        Optional<Course> course = courseRepo.findById(courseName);
        if (course.isPresent())
            course.get().setEnabled(true);
        else throw new CourseNotFoundException("Course '" + courseName + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.courseOwner(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.courseOwner(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public List<Boolean> enrollAll(List<String> studentIds, String courseName) {
        return studentIds.stream()
                .map(s -> addStudentToCourse(s, courseName))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.courseOwner(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseName))or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.courseOwner(authentication.principal.username,#courseName))")
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
    @PreAuthorize("(hasRole('ROLE_STUDENT')  and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId)) or hasRole('ROLE_ADMIN')")
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

        for (Team team : course.getTeams()) {
            List<Student> teamMembers = team.getMembers();
            for (Student student : newMembers) {
                if (teamMembers.contains(student))
                    throw new StudentAlreadyMemberForCourseException("Student '" + student.getId() + "' already has a team!");
            }
        }

        Team newTeam = teamRepo.save(new Team(name,course));
        newMembers.forEach(newTeam::addMember);
        //newTeam.setCourse(course);
        newTeam.setDiskMAX(course.getDisk());
        newTeam.setMemoryMAX(course.getMemory());
        newTeam.setVcpuMAX(course.getVcpu());
        TeamDTO newTeamDTO = modelMapper.map(newTeam, TeamDTO.class);
        notificationService.notifyTeam(newTeamDTO, memberIds);

        return newTeamDTO;
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseName)) or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.courseOwner(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseName)) or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.courseOwner(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public List<StudentDTO> getStudentsInTeams(String courseName) {
        if (courseRepo.findById(courseName).isPresent())
            return courseRepo.getStudentsInTeams(courseName)
                    .stream()
                    .map(s -> modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException("Course '" + courseName + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseName)) or" +
            "(hasRole('ROLE_PROFESSOR') and @permissionEvaluator.courseOwner(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
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
    public List<CourseDTO> getTeacherCourses(String professor) {
        Optional<Teacher> professor1 = teacherRepo.findById(professor);
        if (professor1.isPresent()) {
            return professor1.get().getCourses().
                    stream().map(p -> modelMapper.map(p, CourseDTO.class)).collect(Collectors.toList());
        } else throw new TeacherNotFoundException();
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @permissionEvaluator.courseOwner(authentication.principal.username,#courseName)) or hasRole('ROLE_ADMIN')")
    public VmModelDTO addVmModel(VmModelDTO vmModelDTO, String courseName) {
        Optional<Course> optionalCourse = courseRepo.findById(courseName);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();

            VmModel vmModel = modelMapper.map(vmModelDTO, VmModel.class);
            vmModel.setCourse(course);
            vmModel = vmModelRepository.save(vmModel);
            course.setVmModel(vmModel);
            return modelMapper.map(vmModel, VmModelDTO.class);

        } else throw new CourseNotFoundException("Course '" + courseName + "' not found!");

    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseName) and @permissionEvaluator.studentInTeam(authentication.principal.username,#teamId))")
    public VmIstanceDTO createVmIstance(VmIstanceDTO vmIstanceDTO, String courseName, Long teamId) {
        Optional<Course> optionalCourse = courseRepo.findById(courseName);
        if (optionalCourse.isPresent()) {
            Optional<Team> optionalTeam = teamRepo.findById(teamId);
            if (optionalTeam.isPresent()) {

                Optional<Student> student = studentRepo.findById(SecurityContextHolder.getContext().getAuthentication().getName());
                VmIstance vmIstance = modelMapper.map(vmIstanceDTO, VmIstance.class);
                vmIstance.getOwners().add(student.get());
                optionalTeam.get().addVmIstanceToTeam(vmIstance);
                student.get().addVmOwnership(vmIstance);
                vmIstanceRepository.save(vmIstance);
                return vmIstanceDTO;
            } else throw new TeamNotFoundException("Team '" + teamId + "' not found!");

        } else throw new CourseNotFoundException("Course '" + courseName + "' not found!");

    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseName) and @permissionEvaluator.studentInTeam(authentication.principal.username,#teamId)) OR" +
            " (hasRole('ROLE_PROFESSOR')and @permissionEvaluator.courseOwner(authentication.principal.username,#courseName))")
    public VmIstanceDTO getVmIstance(Long vmId, String courseName, Long teamId) {
        Optional<VmIstance> vmIstance = vmIstanceRepository.findById(vmId);
        if (!vmIstance.isPresent()) throw new VmIstanceNotFound();
        return modelMapper.map(vmIstance.get(), VmIstanceDTO.class);
    }

    //todo gestire stato vm aggiungendo lo stato alla istance. Valori di default per creare il team dove li mettiamo?
    //gestire la faccenda dei nomi dei team
    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseName) and @permissionEvaluator.studentInTeam(authentication.principal.username,#teamId))")
    public VmIstanceDTO changeStatusVM(String command, String courseName, Long tid, Long vmid) {
        Student student = studentRepo.findById(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Optional<VmIstance> vmIstance = vmIstanceRepository.findById(vmid);
        if (!vmIstance.isPresent()) throw new VmIstanceNotFound();
        if (!student.getOwnedVMs().contains(vmIstance.get())) throw new VmPermissionDenied();
        switch (command) {
            case "RUN":
                System.out.println("RUN received");
                if (vmIstance.get().getStatus() == VmStatus.SUSPENDED) {
                    vmIstance.get().setStatus(VmStatus.RUNNING);
                    vmIstanceRepository.save(vmIstance.get());
                }
                break;
            case "SUSPEND":
                System.out.println("STOP received");
                if (vmIstance.get().getStatus() == VmStatus.RUNNING) {
                    vmIstance.get().setStatus(VmStatus.SUSPENDED);
                    vmIstanceRepository.save(vmIstance.get());
                } else
                    break;
            default:
                break;

        }
        return modelMapper.map(vmIstance.get(), VmIstanceDTO.class);
    }

    //todo lista booleano o vmIstance?
    @Override
    public List<Boolean> addOwnersVM(List<String> studentsId, Long vmId, String courseName) {
        List<Boolean> ret = new ArrayList<>();
        Optional<VmIstance> vmIstance = vmIstanceRepository.findById(vmId);
        if(!vmIstance.isPresent()) throw new VmIstanceNotFound();
        for (String studentId:
             studentsId) {
            Optional<Student> student = studentRepo.findById(studentId);
            if(!student.isPresent()) {
                ret.add(false);
                break;
            }
            boolean var = vmIstance.get().addOwner(student.get());
            if(var = true)
            {
                studentRepo.save(student.get());
            }
            ret.add(var);
        }
        vmIstanceRepository.save(vmIstance.get());
        return ret;
    }


}
