package it.polito.ai.es2.controllers;

import it.polito.ai.es2.dtos.CourseDTO;
import it.polito.ai.es2.dtos.StudentDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ModelHelper {

    public static CourseDTO enrich(CourseDTO courseDTO) {
        return courseDTO.add(linkTo(CourseController.class).slash(courseDTO.getName()).withSelfRel());
    }

    public static StudentDTO enrich(StudentDTO studentDTO) {
        return studentDTO.add(linkTo(StudentController.class).slash(studentDTO.getId()).withSelfRel());
    }
}
