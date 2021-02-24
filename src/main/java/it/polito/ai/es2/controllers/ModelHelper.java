package it.polito.ai.es2.controllers;

import it.polito.ai.es2.dtos.*;

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

    public static AssignmentDTO enrich(Long courseId, AssignmentDTO assignmentDTO) {
        return assignmentDTO
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("assignment").slash(assignmentDTO.getId())
                        .withSelfRel())
                .add(linkTo(methodOn(CourseController.class).listHomeworks(
                        courseId,
                        assignmentDTO.getId()
                )).withRel("homeworks"));
    }

    public static HomeworkDTO enrich(Long courseId, Long assignmentId, HomeworkDTO homeworkDTO) {
        return homeworkDTO
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("assignment").slash(assignmentId)
                        .slash("homework").slash(homeworkDTO.getStudent_id())
                        .withSelfRel())
                .add(linkTo(methodOn(CourseController.class).listHomeworkVersions(
                        courseId,
                        homeworkDTO.getAssignment_id(),
                        homeworkDTO.getStudent_id()
                )).withRel("versions"));
    }

    public static HomeworkVersionDTO enrich(Long courseId, Long assignmentId, String studentId, HomeworkVersionDTO homeworkVersionDTO) {
        return homeworkVersionDTO
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("assignment").slash(assignmentId)
                        .slash("homework").slash(studentId)
                        .slash("version").slash(homeworkVersionDTO.getId())
                        .withSelfRel());
    }
    public static VmInstanceDTO enrich(Long courseId, Long teamId, VmInstanceDTO vmInstanceDTO)
    {
        return vmInstanceDTO
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("teams").slash(teamId).slash("vmInstances").slash(vmInstanceDTO.getId()).withSelfRel())
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("teams").slash(teamId).slash("vmInstances").slash(vmInstanceDTO.getId())
                        .slash("command").withRel("command"))
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("teams").slash(teamId).slash("vmInstances").slash(vmInstanceDTO.getId())
                        .slash("creator").withRel("creator"))
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("teams").slash(teamId).slash("vmInstances").slash(vmInstanceDTO.getId())
                        .slash("owners").withRel("owners"))
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("teams").slash(teamId).slash("vmInstances").slash(vmInstanceDTO.getId())
                        .slash("show").withRel("show"));


    }

    public static VmModelDTO enrich(Long courseId, VmModelDTO vmModelDTO)
    {
        return vmModelDTO
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("vmModel").slash(vmModelDTO.getId()).withSelfRel());
    }

    public static ProposalNotificationDTO enrich(Long courseId,ProposalNotificationDTO proposalNotificationDTO)
    {
        return proposalNotificationDTO
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("proposalNotifications").slash(proposalNotificationDTO.getId()).slash("creator").withRel("creator"))
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("proposalNotifications").slash(proposalNotificationDTO.getId()).slash("members").withRel("members"))
                .add(linkTo(NotificationController.class).slash("confirm")
                        .slash(proposalNotificationDTO.getToken()).withRel("accept"))
                .add(linkTo(NotificationController.class).slash("reject")
                        .slash(proposalNotificationDTO.getToken()).withRel("reject"));

    }

    public static TeamDTO enrich(Long courseId, TeamDTO teamDTO)
    {
        return teamDTO
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("teams").slash(teamDTO.getId()).withSelfRel())
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("teams").slash(teamDTO.getId()).slash("students").withRel("members"))
                .add(linkTo(CourseController.class).slash(courseId)
                        .slash("teams").slash(teamDTO.getId()).slash("vmInstances").withRel("vmInstances"));

    }
}
