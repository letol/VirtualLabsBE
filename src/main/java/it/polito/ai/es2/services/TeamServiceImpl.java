package it.polito.ai.es2.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.es2.dtos.CourseDTO;
import it.polito.ai.es2.dtos.StudentDTO;
import it.polito.ai.es2.dtos.TeamDTO;
import it.polito.ai.es2.entities.Course;
import it.polito.ai.es2.entities.Student;
import it.polito.ai.es2.entities.Team;
import it.polito.ai.es2.repositories.CourseRepository;
import it.polito.ai.es2.repositories.StudentRepository;
import it.polito.ai.es2.repositories.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    @Autowired
    CourseRepository courseRepo;

    @Autowired
    StudentRepository studentRepo;

    @Autowired
    TeamRepository teamRepo;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public boolean addCourse(CourseDTO course) {
        if (courseRepo.existsById(course.getName()))
            return false;
        else {
            courseRepo.save(modelMapper.map(course, Course.class));
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
    public boolean addStudent(StudentDTO student) {
        if (studentRepo.existsById(student.getId()))
            return false;
        else {
            studentRepo.save(modelMapper.map(student, Student.class));
            return true;
        }
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
    public List<StudentDTO> getEnrolledStudents(String courseName) {
        Optional<Course> course = courseRepo.findById(courseName);
        if (course.isPresent()) {
            return course.get().getStudents()
                    .stream()
                    .map(s -> modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        } else throw new CourseNotFoundException();
    }

    @Override
    public boolean addStudentToCourse(String studentId, String courseName) {
        Optional<Student> student = studentRepo.findById(studentId);
        if (!student.isPresent()) throw new StudentNotFoundException();

        Optional<Course> course = courseRepo.findById(courseName);
        if (!course.isPresent()) throw new CourseNotFoundException();

        return student.get().addCourse(course.get());
    }

    @Override
    public void enableCourse(String courseName) {
        Optional<Course> course = courseRepo.findById(courseName);
        if (course.isPresent())
            course.get().setEnabled(true);
        else throw new CourseNotFoundException();
    }

    @Override
    public void disableCourse(String courseName) {
        Optional<Course> course = courseRepo.findById(courseName);
        if (course.isPresent())
            course.get().setEnabled(false);
        else throw new CourseNotFoundException();
    }

    @Override
    public List<Boolean> addAll(List<StudentDTO> students) {
        return students.stream()
                .map(this::addStudent)
                .collect(Collectors.toList());
    }

    @Override
    public List<Boolean> enrollAll(List<String> studentIds, String courseName) {
        return studentIds.stream()
                .map(s -> addStudentToCourse(s, courseName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Boolean> addAndEnroll(Reader r, String courseName) {
        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder<StudentDTO>(r)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<StudentDTO> studentDTOList = csvToBean.parse();
        addAll(studentDTOList);
        return enrollAll(studentDTOList.stream()
                .map(StudentDTO::getId)
                .collect(Collectors.toList()), courseName);
    }

    @Override
    public List<CourseDTO> getCourses(String studentId) {
        Optional<Student> student = studentRepo.findById(studentId);
        if (student.isPresent())
            return student.get().getCourses()
                    .stream()
                    .map(c -> modelMapper.map(c, CourseDTO.class))
                    .collect(Collectors.toList());
        else throw new StudentNotFoundException();
    }

    @Override
    public List<TeamDTO> getTeamsForStudent(String studentId) {
        Optional<Student> student = studentRepo.findById(studentId);
        if (student.isPresent())
            return student.get().getTeams()
                    .stream()
                    .map(t -> modelMapper.map(t, TeamDTO.class))
                    .collect(Collectors.toList());
        else throw new StudentNotFoundException();
    }

    @Override
    public List<StudentDTO> getMembers(Long teamId) {
        Optional<Team> team = teamRepo.findById(teamId);
        if (team.isPresent())
            return team.get().getMembers()
                    .stream()
                    .map(s -> modelMapper.map(s, StudentDTO.class))
                    .collect(Collectors.toList());
        else throw new TeamNotFoundException();
    }

    @Override
    public TeamDTO proposeTeam(String courseId, String name, List<String> memberIds) {
        Optional<Course> courseOptional = courseRepo.findById(courseId);
        if (!courseOptional.isPresent())
            throw new CourseNotFoundException();
        Course course = courseOptional.get();

        if (!course.isEnabled())
            throw new CourseNotEnabledException();

        if (memberIds.size() < course.getMin())
            throw new TeamMembersMinNotReachedException();
        if (memberIds.size() > course.getMax())
            throw new TeamMembersMaxExceededException();

        Set<String> memberIdsSet = new HashSet<>(memberIds);
        if (memberIdsSet.size() < memberIds.size())
            throw new StudentDuplicatedException();

        List<Student> newMembers = studentRepo.findAllById(memberIdsSet);
        if (newMembers.size() < memberIdsSet.size())
            throw new StudentNotFoundException();

        if (!course.getStudents().containsAll(newMembers))
            throw new StudentNotEnrolledToCourseException();

        for (Team team: course.getTeams()) {
            List<Student> teamMembers = team.getMembers();
            for (Student student: newMembers) {
                if (teamMembers.contains(student))
                    throw new StudentAlreadyMemberForCourseException();
            }
        }

        Team newTeam = teamRepo.save(new Team(name));
        newMembers.forEach(newTeam::addMember);
        newTeam.setCourse(course);

        return modelMapper.map(newTeam, TeamDTO.class);
    }

    @Override
    public List<TeamDTO> getTeamsForCourse(String courseName) {
        Optional<Course> course = courseRepo.findById(courseName);
        if (course.isPresent())
            return course.get().getTeams()
                    .stream()
                    .map(t -> modelMapper.map(t, TeamDTO.class))
                    .collect(Collectors.toList());
        else throw new CourseNotFoundException();
    }

    @Override
    public List<StudentDTO> getStudentsInTeams(String courseName) {
        return courseRepo.getStudentsInTeams(courseName)
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getAvailableStudents(String courseName) {
        return courseRepo.getStudentsNotInTeams(courseName)
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }
}
