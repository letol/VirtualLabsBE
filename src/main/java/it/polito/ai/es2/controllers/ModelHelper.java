package it.polito.ai.es2.controllers;

import it.polito.ai.es2.dtos.*;
import it.polito.ai.es2.entities.Assignment;
import it.polito.ai.es2.entities.Course;
import it.polito.ai.es2.entities.Homework;
import it.polito.ai.es2.entities.HomeworkVersion;
import it.polito.ai.es2.exceptions.HomeworkVersionNotFoundException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ModelHelper {

    public static CourseDTO enrich(CourseDTO courseDTO) {
        return courseDTO
                .add(linkTo(CourseController.class).slash(courseDTO.getId()).withSelfRel())
                .add(linkTo(methodOn(CourseController.class).enrolledStudents(courseDTO.getId())).withRel("enrolled"))
                .add(linkTo(methodOn(CourseController.class).listTeams(courseDTO.getId())).withRel("teams"))
                .add(linkTo(methodOn(CourseController.class).studentsInTeam(courseDTO.getId())).withRel("studentsInTeam"))
                .add(linkTo(methodOn(CourseController.class).listFreeStudents(courseDTO.getId())).withRel("availableStudents"))
                .add(linkTo(methodOn(CourseController.class).listAssignments(courseDTO.getId())).withRel("assignments"));
    }

    public static StudentDTO enrich(StudentDTO studentDTO) {
        return studentDTO
                .add(linkTo(StudentController.class).slash(studentDTO.getId()).withSelfRel())
                .add(linkTo(methodOn(StudentController.class).getStudentcourses(studentDTO.getId())).withRel("courses"))
                .add(linkTo(methodOn(StudentController.class).getStudentTeams(studentDTO.getId())).withRel("teams"));
    }

    public static TeacherDTO enrich(TeacherDTO teacherDTO) {
        return teacherDTO
                .add(linkTo(TeacherController.class).slash(teacherDTO.getId()).withSelfRel())
                .add(linkTo(methodOn(TeacherController.class).getTeachers(teacherDTO.getId())).withRel("courses"));
    }

    public static AssignmentDTO enrich(String courseName, AssignmentDTO assignmentDTO) {
        return assignmentDTO
                .add(linkTo(CourseController.class).slash(courseName)
                        .slash("assignment").slash(assignmentDTO.getId())
                        .withSelfRel())
                .add(linkTo(methodOn(CourseController.class).listHomeworks(
                        courseName,
                        assignmentDTO.getId()
                )).withRel("homeworks"));
    }

    public static HomeworkDTO enrich(String courseName, Long assignmentId, HomeworkDTO homeworkDTO) {
        return homeworkDTO
                .add(linkTo(CourseController.class).slash(courseName)
                        .slash("assignment").slash(assignmentId)
                        .slash("homework").slash(homeworkDTO.getStudent_id())
                        .withSelfRel())
                .add(linkTo(methodOn(CourseController.class).listHomeworkVersions(
                        courseName,
                        homeworkDTO.getAssignment_id(),
                        homeworkDTO.getStudent_id()
                )).withRel("versions"));
    }

    public static HomeworkVersionDTO enrich(String courseName, Long assignmentId, String studentId, HomeworkVersionDTO homeworkVersionDTO) {
        return homeworkVersionDTO
                .add(linkTo(CourseController.class).slash(courseName)
                        .slash("assignment").slash(assignmentId)
                        .slash("homework").slash(studentId)
                        .slash("version").slash(homeworkVersionDTO.getId())
                        .withSelfRel());
    }
}
