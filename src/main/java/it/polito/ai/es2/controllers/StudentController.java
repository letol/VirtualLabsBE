package it.polito.ai.es2.controllers;

import it.polito.ai.es2.dtos.CourseDTO;
import it.polito.ai.es2.dtos.ProposalNotificationDTO;
import it.polito.ai.es2.dtos.StudentDTO;
import it.polito.ai.es2.dtos.TeamDTO;
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
@RequestMapping("/API/students")
public class StudentController {

    @Autowired
    TeamService teamService;

    @GetMapping({"", "/"})
    List<StudentDTO> all() {
        return teamService.getAllStudents()
                .stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    StudentDTO getOne(@PathVariable String id) {
        return ModelHelper.enrich(teamService.getStudent(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Student id '" + id + "' not found!")));
    }

    @PostMapping({"", "/"})
    StudentDTO addStudent(@RequestBody @Valid StudentDTO studentDTO) {
        if (teamService.addStudent(studentDTO))
            return ModelHelper.enrich(studentDTO);
        else throw new ResponseStatusException(HttpStatus.CONFLICT, "Student id '" + studentDTO.getId() + "' already exists!");
    }

    @GetMapping("/{id}/teams")
    List<TeamDTO> getStudentTeams(@PathVariable String id) {
        try {
            return teamService.getTeamsForStudent(id);
        }
        catch (TeamServiceException e)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{id}/courses/{courseId}/team")
    TeamDTO getTeamByCourse(@PathVariable String id, @PathVariable Long courseId) {
        try {
            return teamService.getStudentTeamByCourse(id,courseId);
        } catch (TeamServiceException e)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{id}/courses")
    List<CourseDTO> getStudentcourses(@PathVariable String id) {
        try {
            return teamService.getCourses(id);
        } catch (TeamServiceException e)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

    }

    @GetMapping("/{id}/courses/{courseId}/notifications/created")
    List<ProposalNotificationDTO> getNotificationsCreated(@PathVariable String id, @PathVariable Long courseId) {
        try {
            return teamService.getNotificationsCreated(id,courseId).stream().map(p -> ModelHelper.enrich(courseId,p)).collect(Collectors.toList());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{id}/courses/{courseId}/notifications/invited")
    List<ProposalNotificationDTO> getNotificationsForStudent(@PathVariable String id, @PathVariable Long courseId) {
        try {
            return teamService.getNotificationsForStudent(id,courseId).stream().map(p -> ModelHelper.enrich(courseId,p)).collect(Collectors.toList());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
