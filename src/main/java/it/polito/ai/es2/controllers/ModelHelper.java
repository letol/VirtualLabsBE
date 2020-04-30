package it.polito.ai.es2.controllers;

import it.polito.ai.es2.dtos.CourseDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ModelHelper {
    public static CourseDTO enrich(CourseDTO courseDTO) {
        return courseDTO.add(linkTo(CourseController.class).slash(courseDTO.getName()).withSelfRel());
    }
}
