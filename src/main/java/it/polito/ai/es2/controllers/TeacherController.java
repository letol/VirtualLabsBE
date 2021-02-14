package it.polito.ai.es2.controllers;

import it.polito.ai.es2.dtos.CourseDTO;
import it.polito.ai.es2.dtos.TeacherDTO;
import it.polito.ai.es2.exceptions.TeamServiceException;
import it.polito.ai.es2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/teachers")
public class TeacherController {

    @Autowired
    TeamService teamService;

    @GetMapping({"", "/"})
    List<TeacherDTO> all() {
        return teamService.getAllTeachers()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    TeacherDTO getOne(@PathVariable String id) {
        return ModelHelper.enrich(teamService.getTeacher(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Teacher id '" + id + "' not found!")));
    }

    @PostMapping({"", "/"})
    TeacherDTO addTeacher(@RequestBody @Valid TeacherDTO teacherDTO) {
        if (teamService.addTeacher(teacherDTO))
            return ModelHelper.enrich(teacherDTO);
        else throw new ResponseStatusException(HttpStatus.CONFLICT, "Teacher id '" + teacherDTO.getId() + "' already exists!");
    }

    @GetMapping("/{id}/courses")
    public List<CourseDTO>  getTeachers(@PathVariable String id){
        try {
            return teamService.getTeacherCourses(id).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        }
        catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

    }

}
