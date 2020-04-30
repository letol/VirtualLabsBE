package it.polito.ai.es2.controllers;

import it.polito.ai.es2.dtos.CourseDTO;
import it.polito.ai.es2.dtos.StudentDTO;
import it.polito.ai.es2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/courses")
public class CourseController {

    @Autowired
    TeamService teamService;

    @GetMapping({"", "/"})
    List<CourseDTO> all() {
        return teamService.getAllCourses().stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    CourseDTO getOne(@PathVariable String name) {
        Optional<CourseDTO> courseDTO = teamService.getCourse(name);
        if (courseDTO.isPresent())
            return ModelHelper.enrich(courseDTO.get());
        else throw new ResponseStatusException(HttpStatus.CONFLICT, "Course " + name + " not found!");
    }

    @GetMapping("/{name}/enrolled")
    List<StudentDTO> enrolledStudents(@PathVariable String name) {
        try {
            return teamService.getEnrolledStudents(name).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping({"", "/"})
    CourseDTO addCourse(@RequestBody CourseDTO courseDTO) {
        if (teamService.addCourse(courseDTO))
            return ModelHelper.enrich(courseDTO);
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course '" + courseDTO.getName() + "' already exists!");
    }
}
