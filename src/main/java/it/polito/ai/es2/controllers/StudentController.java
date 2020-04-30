package it.polito.ai.es2.controllers;

import it.polito.ai.es2.dtos.StudentDTO;
import it.polito.ai.es2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/students")
public class StudentController {

    @Autowired
    TeamService teamService;

    @GetMapping({"", "/"})
    List<StudentDTO> all() {
        return teamService.getAllStudents().stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    StudentDTO getOne(@PathVariable String id) {
        Optional<StudentDTO> studentDTO = teamService.getStudent(id);
        if (studentDTO.isPresent())
            return ModelHelper.enrich(studentDTO.get());
        else throw new ResponseStatusException(HttpStatus.CONFLICT, "Student id " + id + " not found!");
    }
}
