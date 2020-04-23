package it.polito.ai.es2;

import it.polito.ai.es2.dtos.CourseDTO;
import it.polito.ai.es2.dtos.StudentDTO;
import it.polito.ai.es2.dtos.TeamDTO;
import it.polito.ai.es2.services.TeamService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.Arrays;

@SpringBootApplication
public class Es2Application {
    @Autowired
    TeamService teamService;

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // -------------------------------- FOR DEBUGGING PURPOSES ONLY -------------------------------- //
    // --------------------------------             START           -------------------------------- //
    @Bean
    CommandLineRunner runner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) {
                System.out.println("---> Add course 'corso1':");
                CourseDTO courseDTO = new CourseDTO("corso1", 3, 4, false);
                teamService.addCourse(courseDTO);

                System.out.println("---> Add course 'corso2':");
                courseDTO = new CourseDTO("corso2", 1, 2, false);
                teamService.addCourse(courseDTO);

                System.out.println("---> Add course 'corso3':");
                courseDTO = new CourseDTO("corso3", 2, 4, false);
                teamService.addCourse(courseDTO);

                System.out.println("---> Add course 'corso4':");
                courseDTO = new CourseDTO("corso4", 3, 5, false);
                teamService.addCourse(courseDTO);

                System.out.println("---> Get course 'corso4':");
                System.out.println(teamService.getCourse("corso4").toString());

                System.out.println("---> Test not existing course:");
                System.out.println(teamService.getCourse("corso0").toString());

                System.out.println("---> Get updated course list:");
                teamService.getAllCourses()
                        .stream()
                        .forEach(c -> System.out.println(c.toString()));

                System.out.println("---> Add student 's111111':");
                StudentDTO studentDTO = new StudentDTO("s111111", "Rossi", "Mario");
                teamService.addStudent(studentDTO);

                System.out.println("---> Add student 's222222':");
                studentDTO = new StudentDTO("s222222", "Bianchi", "Giulio");
                teamService.addStudent(studentDTO);

                System.out.println("---> Add student 's333333':");
                studentDTO = new StudentDTO("s333333", "Verdi", "Giuseppe");
                teamService.addStudent(studentDTO);

                System.out.println("---> Add student 's444444':");
                studentDTO = new StudentDTO("s444444", "Bruni", "Giorgio");
                teamService.addStudent(studentDTO);

                System.out.println("---> Get student 's444444':");
                System.out.println(teamService.getStudent("s444444").toString());

                System.out.println("---> Test not existing student:");
                System.out.println(teamService.getStudent("s000000").toString());

                System.out.println("---> Get updated student list:");
                teamService.getAllStudents()
                        .stream()
                        .forEach(s -> System.out.println(s.toString()));

                System.out.println("---> Enroll student 's222222' to 'corso4':");
                teamService.addStudentToCourse("s222222", "corso4");
                System.out.println("---> Enroll student 's444444' to 'corso4':");
                teamService.addStudentToCourse("s444444", "corso4");

                System.out.println("---> Test enroll missing student error:");
                try {
                    teamService.addStudentToCourse("s000000", "corso4");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Test enroll to missing course error:");
                try {
                    teamService.addStudentToCourse("s444444", "corso0");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Get enrolled students of 'corso4':");
                teamService.getEnrolledStudents("corso4")
                        .stream()
                        .forEach(s -> System.out.println(s.toString()));

                System.out.println("---> Test get enrolled students of missing course error:");
                try {
                    teamService.getEnrolledStudents("corso0");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Enable 'corso4':");
                teamService.enableCourse("corso4");
                System.out.println("---> Get enabled 'corso4':");
                System.out.println(teamService.getCourse("corso4").toString());

                System.out.println("---> Test enable missing course error:");
                try {
                    teamService.enableCourse("corso0");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Disable 'corso4':");
                teamService.disableCourse("corso4");
                System.out.println("---> Get disabled 'corso4':");
                System.out.println(teamService.getCourse("corso4").toString());

                System.out.println("---> Test disable missing course error:");
                try {
                    teamService.disableCourse("corso0");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Add and enroll to 'corso3' from CSV:");
                try (Reader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("test.csv").getInputStream()))) {
                    teamService.addAndEnroll(reader, "corso3");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Test wrong CSV header error:");
                try (Reader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("testWithWrongHeader.csv").getInputStream()))) {
                    teamService.addAndEnroll(reader, "corso3");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Get courses in which 's222222' is enrolled:");
                teamService.getCourses("s222222")
                        .stream()
                        .forEach(c -> System.out.println(c.toString()));

                System.out.println("---> Test get courses of missing student error:");
                try {
                    teamService.getCourses("s000000");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Enable 'corso3':");
                teamService.enableCourse("corso3");

                System.out.println("---> Enroll student 's111111' to 'corso3':");
                teamService.addStudentToCourse("s111111", "corso3");

                System.out.println("---> Enroll student 's444444' to 'corso3':");
                teamService.addStudentToCourse("s444444", "corso3");

                System.out.println("---> Get enrolled students of 'corso3':");
                teamService.getEnrolledStudents("corso3")
                        .stream()
                        .forEach(s -> System.out.println(s.toString()));

                System.out.println("---> Propose team 'Il trio':");
                String[] memberIds = {"s222222", "s555555", "s666666"};
                TeamDTO newTeam = teamService.proposeTeam("corso3", "Il trio", Arrays.asList(memberIds));

                System.out.println("---> Get members of created team 'Il trio':");
                teamService.getMembers(newTeam.getId())
                        .stream()
                        .forEach(s -> System.out.println(s.toString()));

                System.out.println("---> Test get members of missing team error:");
                try {
                    teamService.getMembers(0L);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Get teams for 's222222':");
                teamService.getTeamsForStudent("s222222")
                        .stream()
                        .forEach(t -> System.out.println(t.toString()));

                System.out.println("---> Test get teams for missing student error:");
                try {
                    teamService.getTeamsForStudent("s000000");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Test propose team for missing course error:");
                try {
                    teamService.proposeTeam("corse0", "Errore", Arrays.asList(memberIds));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Test propose team for course not enabled error:");
                try {
                    teamService.proposeTeam("corso4", "Errore", Arrays.asList(memberIds));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Test propose team with not enough members error:");
                memberIds = new String[]{"s111111"};
                try {
                    teamService.proposeTeam("corso3", "Errore", Arrays.asList(memberIds));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Test propose team with too many members error:");
                memberIds = new String[]{"s111111", "s222222", "s444444", "s555555", "s666666"};
                try {
                    teamService.proposeTeam("corso3", "Errore", Arrays.asList(memberIds));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Test propose team with duplicated student error:");
                memberIds = new String[]{"s111111", "s111111", "s444444"};
                try {
                    teamService.proposeTeam("corso3", "Errore", Arrays.asList(memberIds));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Test propose team with missing student error:");
                memberIds = new String[]{"s111111", "s000000", "s444444"};
                try {
                    teamService.proposeTeam("corso3", "Errore", Arrays.asList(memberIds));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Test propose team with student not enrolled error:");
                memberIds = new String[]{"s111111", "s333333", "s444444"};
                try {
                    teamService.proposeTeam("corso3", "Errore", Arrays.asList(memberIds));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Test propose team with student already member error:");
                memberIds = new String[]{"s111111", "s222222", "s444444"};
                try {
                    teamService.proposeTeam("corso3", "Errore", Arrays.asList(memberIds));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Test propose team with null name error:");
                memberIds = new String[]{"s111111", "s444444"};
                try {
                    teamService.proposeTeam("corso3", null, Arrays.asList(memberIds));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Propose team 'Il duo':");
                memberIds = new String[]{"s111111", "s444444"};
                newTeam = teamService.proposeTeam("corso3", "Il duo", Arrays.asList(memberIds));

                System.out.println("---> Get members of created team 'Il duo':");
                teamService.getMembers(newTeam.getId())
                        .stream()
                        .forEach(s -> System.out.println(s.toString()));

                System.out.println("---> Get teams for course 'corso3':");
                teamService.getTeamsForCourse("corso3")
                        .stream()
                        .forEach(c -> System.out.println(c.toString()));

                System.out.println("---> Test get teams for missing course error:");
                try {
                    teamService.getTeamsForCourse("corso0");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Get students in teams for 'corso3':");
                teamService.getStudentsInTeams("corso3")
                        .stream()
                        .forEach(s -> System.out.println(s.toString()));

                System.out.println("---> Test get student in teams for missing course error:");
                try {
                    teamService.getStudentsInTeams("corso0");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                System.out.println("---> Get available students for 'corso3':");
                teamService.getAvailableStudents("corso3")
                        .stream()
                        .forEach(s -> System.out.println(s.toString()));

                System.out.println("---> Test available students for missing course error:");
                try {
                    teamService.getAvailableStudents("corso0");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        };
    }
    // --------------------------------              END            -------------------------------- //

    public static void main(String[] args) {
        SpringApplication.run(Es2Application.class, args);
    }

}
