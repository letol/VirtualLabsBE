package it.polito.ai.es2.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.es2.utility.ResponseTypeInvitation;
import it.polito.ai.es2.utility.StudentStatusInvitation;
import it.polito.ai.es2.utility.VmStatus;
import it.polito.ai.es2.dtos.*;
import it.polito.ai.es2.entities.*;
import it.polito.ai.es2.utility.TeamStatus;
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
import java.sql.Timestamp;
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

    @Autowired
    ProposalNotificationRepository proposalNotificationRepository;


    public TeamServiceImpl() throws IOException {
        File defaultAvatarFile = ResourceUtils.getFile("classpath:img/default_user_avatar.png");
        defaultAvatar = Files.readAllBytes(defaultAvatarFile.toPath());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TEACHER') and #teacherId == authentication.principal.username")
    public CourseDTO addCourse(CourseDTO courseDTO, String teacherId) {
        try {
            System.out.println("Add course");
            Teacher teacher = teacherRepo.findById(teacherId)
                    .orElseThrow(TeacherNotFoundException::new);
            Course course = courseRepo.save(modelMapper.map(courseDTO, Course.class));
            teacher.addCourse(course);

            courseDTO = modelMapper.map(course,CourseDTO.class);

            return courseDTO;

        } catch (Exception e){
            throw new TeamServiceException("Failed to create a course");
        }

    }

    @Override
    public Optional<CourseDTO> getCourse(Long courseId) {
        return courseRepo.findById(courseId).map(c -> modelMapper.map(c, CourseDTO.class));
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
    public List<StudentDTO> getEnrolledStudents(Long courseId) {
        Optional<Course> course = courseRepo.findById(courseId);
        if (course.isPresent()) {
            return course.get().getStudents()
                    .stream()
                    .map(s -> modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        } else throw new CourseNotFoundException("Course '" + courseId + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId)) or hasRole('ROLE_ADMIN')")
    public boolean addStudentToCourse(String studentId, Long courseId) {
        Optional<Student> student = studentRepo.findById(studentId);
        if (!student.isPresent()) throw new StudentNotFoundException("Student id '" + studentId + "' not found!");

        Optional<Course> course = courseRepo.findById(courseId);
        if (!course.isPresent()) throw new CourseNotFoundException("Course '" + courseId + "' not found!");

        return student.get().addCourse(course.get());
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId)) or hasRole('ROLE_ADMIN')")
    public void enableCourse(Long courseId) {
        Optional<Course> course = courseRepo.findById(courseId);
        if (course.isPresent())
            course.get().setEnabled(true);
        else throw new CourseNotFoundException("Course '" + courseId + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId)) or hasRole('ROLE_ADMIN')")
    public void disableCourse(Long courseId) {
        Optional<Course> course = courseRepo.findById(courseId);
        if (course.isPresent())
            course.get().setEnabled(false);
        else throw new CourseNotFoundException("Course '" + courseId + "' not found!");
    }

    @Override
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
    public List<Boolean> addAll(List<StudentDTO> students) {
        return students.stream()
                .map(this::addStudent)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId)) or hasRole('ROLE_ADMIN')")
    public List<Boolean> enrollAll(List<String> studentIds, Long courseId) {
        return studentIds.stream()
                .map(s -> addStudentToCourse(s, courseId))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_TEACHER')  and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId)) or hasRole('ROLE_ADMIN')")
    public List<Boolean> addAndEnroll(Reader r, Long courseId) {
        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder<StudentDTO>(r)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<@Valid StudentDTO> studentDTOList = csvToBean.parse();
        addAll(studentDTOList);
        return enrollAll(studentDTOList.stream()
                .map(StudentDTO::getId)
                .collect(Collectors.toList()), courseId);
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
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER') ")
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
    public ProposalNotificationDTO proposeTeam(Long courseId, RequestTeamDTO teamRequest) {
        List<String> memberIds = teamRequest.getSelectedStudentsId();
        String name = teamRequest.getTeamName();
        Optional<Course> courseOptional = courseRepo.findById(courseId);
        if (!courseOptional.isPresent())
            throw new CourseNotFoundException("Course '" + courseId + "' not found!");
        Course course = courseOptional.get();
        System.out.println("Propose team "+memberIds.toString());
        if (!course.isEnabled())
            throw new CourseNotEnabledException("Course not yet enabled!");
        if (course.getTeams().stream().anyMatch(x -> x.getName().equals(name))) throw new TeamServiceException("name already used");
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
        Student student = studentRepo.findById(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        List<StudentStatusInvitation> studentStatusInvitations = new ArrayList<>();
        for (String studentI:memberIds) {
            if(!studentI.equals(student.getId())){
                studentStatusInvitations.add(new StudentStatusInvitation(studentI,ResponseTypeInvitation.NOT_REPLY));
            }

        }
        ProposalNotification proposalNotification = ProposalNotification.builder()
                .teamName(name)
                .course(course)
                .creator(student)
                .token(UUID.randomUUID().toString())
                .deadline(new Timestamp(teamRequest.getDeadline().getTime()+System.currentTimeMillis()))
                .studentsInvitedWithStatus(studentStatusInvitations).build();
        /*
        Team newTeam = teamRepo.save(new Team(name,course.getVcpu(),course.getMemory(),course.getDisk(),course));
        newMembers.forEach(newTeam::addMember);
        //newTeam.setCourse(course);
        newTeam.setDiskMAX(course.getDisk());
        newTeam.setMemoryMAX(course.getMemory());
        newTeam.setVcpuMAX(course.getVcpu());
        newTeam.setMaxVmIstance(course.getMaxVmIstance());
        newTeam.setMaxRunningVmIstance(course.getMaxRunningVmInstance());
        System.out.println("imma here");
        TeamDTO newTeamDTO = modelMapper.map(newTeam, TeamDTO.class);
        notificationService.notifyTeam(newTeamDTO, memberIds);
        */
        proposalNotification = proposalNotificationRepository.save(proposalNotification);
        notificationService.notifyTeam(proposalNotification);
        student.getNotificationsCreated().add(proposalNotification);
        return modelMapper.map(proposalNotification,ProposalNotificationDTO.class);
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId)) or" +
            "(hasRole('ROLE_TEACHER') and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId)) or hasRole('ROLE_ADMIN')")
    public List<TeamDTO> getTeamsForCourse(Long courseId) {
        Optional<Course> course = courseRepo.findById(courseId);
        if (course.isPresent())
            return course.get().getTeams()
                    .stream()
                    .map(t -> modelMapper.map(t, TeamDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException("Course '" + courseId + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId)) or" +
            "(hasRole('ROLE_TEACHER') and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId)) or hasRole('ROLE_ADMIN')")
    public List<StudentDTO> getStudentsInTeams(Long courseId) {
        if (courseRepo.findById(courseId).isPresent())
            return courseRepo.getStudentsInTeams(courseId)
                    .stream()
                    .map(s -> modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException("Course '" + courseId + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId)) or" +
            "(hasRole('ROLE_TEACHER') and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId)) or hasRole('ROLE_ADMIN')")
    public List<StudentDTO> getAvailableStudents(Long courseId) {
        if (courseRepo.findById(courseId).isPresent())
            return courseRepo.getStudentsNotInTeams(courseId)
                    .stream()
                    .map(s -> modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException("Course '" + courseId + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId)) or" +
            "(hasRole('ROLE_TEACHER') and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId)) or hasRole('ROLE_ADMIN')")
    public List<StudentDTO> getStudentsInATeam(Long courseId,Long id) {
        Optional<Course> course = courseRepo.findById(courseId);
        if (course.isPresent()){
            Optional<Team> team = teamRepo.findById(id);
            if(!team.isPresent()) throw new TeamNotFoundException("Team not found");
            return team.get().getMembers().stream().map(s -> modelMapper.map(s, StudentDTO.class)).collect(Collectors.toList());
        }
        else throw new CourseNotFoundException("Course '" + courseId + "' not found!");
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
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId)) or hasRole('ROLE_ADMIN')")
    public VmModelDTO addVmModel(VmModelDTO vmModelDTO, Long courseId) {
        Optional<Course> optionalCourse = courseRepo.findById(courseId);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();

            VmModel vmModel = modelMapper.map(vmModelDTO, VmModel.class);
            vmModel.setCourse(course);
            vmModel = vmModelRepository.save(vmModel);
            course.setVmModel(vmModel);
            return modelMapper.map(vmModel, VmModelDTO.class);

        } else throw new CourseNotFoundException("Course '" + courseId + "' not found!");

    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId) and @permissionEvaluator.studentInTeam(authentication.principal.username,#teamId))")
    public VmModelDTO getVmModel(Long courseId) {
        VmModel vmModel = courseRepo.findById(courseId).get().getVmModel();
        if(vmModel==null) throw new VmModelNotFoundException("Vm model not present");
        return modelMapper.map(vmModel,VmModelDTO.class);
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId) and @permissionEvaluator.studentInTeam(authentication.principal.username,#teamId))")
    public VmInstanceDTO createVmInstance(VmInstanceDTO vmInstanceDTO, Long courseId, Long teamId) {
        Optional<Course> optionalCourse = courseRepo.findById(courseId);
        if (optionalCourse.isPresent()) {
            VmModel vmModel = optionalCourse.get().getVmModel();
            if(vmModel==null) throw new VmModelNotFoundException("Vm model not exists for this course");
            Optional<Team> optionalTeam = teamRepo.findById(teamId);
            if (optionalTeam.isPresent()) {
                if(optionalTeam.get().getStatus()!=TeamStatus.ACTIVE) throw new TeamNotEnabledException("Team not enabled");
                String studentAuth = SecurityContextHolder.getContext().getAuthentication().getName();
                Optional<Student> student = studentRepo.findById(studentAuth);
                vmInstanceDTO.setStatus(VmStatus.SUSPENDED);
                VmInstance vmInstance = modelMapper.map(vmInstanceDTO, VmInstance.class);
                vmInstance.setVmModel(vmModel);
                vmInstance.setCreator(student.get());
                vmInstance.getOwners().add(student.get());
                optionalTeam.get().addVmIstanceToTeam(vmInstance);
                student.get().addVmOwnership(vmInstance);
                vmInstance=vmIstanceRepository.save(vmInstance);
                vmInstanceDTO.setId(vmInstance.getId());
                return vmInstanceDTO;
            } else throw new TeamNotFoundException("Team '" + teamId + "' not found!");

        } else throw new CourseNotFoundException("Course '" + courseId + "' not found!");
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId) and @permissionEvaluator.studentInTeam(authentication.principal.username,#teamId)) OR" +
            " (hasRole('ROLE_TEACHER')and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId))")
    public VmInstanceDTO getVmInstanceOfTeam(Long vmId, Long courseId, Long teamId) {
        Optional<VmInstance> vmInstance = vmIstanceRepository.findById(vmId);
        if (!vmInstance.isPresent()) throw new VmIstanceNotFound();
        return modelMapper.map(vmInstance.get(), VmInstanceDTO.class);
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId) and @permissionEvaluator.studentInTeam(authentication.principal.username,#teamId)) OR" +
            " (hasRole('ROLE_TEACHER')and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId))")
    public List<VmInstanceDTO> getVmInstancesOfTeam( Long courseId, Long teamId) {

        return teamRepo.findById(teamId).get().getVmInstances().stream().map(vm -> modelMapper.map(vm, VmInstanceDTO.class)).collect(Collectors.toList());
    }

    //todo gestire stato vm aggiungendo lo stato alla istance. Valori di default per creare il team dove li mettiamo?
    //gestire la faccenda dei nomi dei team
    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId) and @permissionEvaluator.studentInTeam(authentication.principal.username,#teamId))")
    public VmInstanceDTO changeStatusVM(VmStatus command, Long courseId, Long tid, Long vmid) {
        Student student = studentRepo.findById(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Optional<VmInstance> vmIstance = vmIstanceRepository.findById(vmid);
        Team team = teamRepo.findById(tid).get();
        if (!vmIstance.isPresent()) throw new VmIstanceNotFound();
        if (!student.getOwnedVMs().contains(vmIstance.get())) throw new VmPermissionDenied();
        try{
            team.changeStatusVm(vmIstance.get(),command);
            return modelMapper.map(vmIstance.get(), VmInstanceDTO.class);
        }catch(TooManyVmInstancesException e){
            throw new TeamServiceException(e.getMessage());
        }catch(TeamServiceException e){
            throw new TeamServiceException(e.getMessage());
        }

    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId) and @permissionEvaluator.studentInTeam(authentication.principal.username,#teamId))")
    public List<Boolean> addOwnersVM(List<String> studentsId, Long vmId, Long teamId, Long courseId) {
        List<Boolean> ret = new ArrayList<>();
        Optional<VmInstance> vmIstance = vmIstanceRepository.findById(vmId);
        if(!vmIstance.isPresent()) throw new VmIstanceNotFound("Vm Instance not found");
        for (String studentId:
             studentsId) {
            Optional<Student> student = studentRepo.findById(studentId);
            if(!student.isPresent()) {
                ret.add(false);
                break;
            }
            boolean var = vmIstance.get().addOwner(student.get());
            if(var)
            {
                studentRepo.save(student.get());
            }
            ret.add(var);
        }
        vmIstanceRepository.save(vmIstance.get());
        return ret;
    }

    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId) and @permissionEvaluator.studentInTeam(authentication.principal.username,#teamId))or(hasRole('ROLE_TEACHER')and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId))")
    public List<StudentDTO> getOwnersVm(Long vmId, Long teamId, Long courseId) {
        Optional<VmInstance> vmIstance = vmIstanceRepository.findById(vmId);
        if(!vmIstance.isPresent()) throw new VmIstanceNotFound("Vm instance not found");
        return vmIstance.get().getOwners().stream().map(p -> modelMapper.map(p,StudentDTO.class)).collect(Collectors.toList());
    }
    @Override
    @PreAuthorize("(hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId) and @permissionEvaluator.studentInTeam(authentication.principal.username,#teamId)) or (hasRole('ROLE_TEACHER')and @permissionEvaluator.courseOwner(authentication.principal.username,#courseId))")
    public StudentDTO getCreatorVm(Long vmId, Long teamId, Long courseId){
        Optional<VmInstance> vmIstance = vmIstanceRepository.findById(vmId);
        if(!vmIstance.isPresent()) throw new VmIstanceNotFound("Vm instance not found");
        return modelMapper.map(vmIstance.get().getCreator(),StudentDTO.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#courseId)")
    public List<ProposalNotificationDTO> getNotificationsForStudent(Long courseId) {
        List<ProposalNotification> proposalNotifications =proposalNotificationRepository.getProposalNotificationsForStudentByCourse(courseId,SecurityContextHolder.getContext().getAuthentication().getName());
        return proposalNotifications.stream().map(p->modelMapper.map(p,ProposalNotificationDTO.class)).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ROLE_STUDENT') and @permissionEvaluator.studentInCourse(authentication.principal.username,#name)")
    public StudentDTO getCreatorProposal(Long name, Long id) {
        Optional<ProposalNotification> optionalProposalNotification = proposalNotificationRepository.findById(id);
        if(!optionalProposalNotification.isPresent()) throw new TeamServiceException("ProposalNotification not exists");

        return modelMapper.map(optionalProposalNotification.get().getCreator(),StudentDTO.class);
    }


}
