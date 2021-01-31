package it.polito.ai.es2.controllers;

import it.polito.ai.es2.utility.VmStatus;
import it.polito.ai.es2.dtos.*;
import it.polito.ai.es2.exceptions.*;
import it.polito.ai.es2.services.*;
import lombok.extern.java.Log;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log(topic = "CourseController")
@RequestMapping("/API/courses")
public class CourseController {

    @Autowired
    TeamService teamService;

    @Autowired
    NotificationService notificationService;

    @GetMapping({"", "/"})
    List<CourseDTO> all() {
        return teamService.getAllCourses().stream()
                .map(ModelHelper::enrich)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    CourseDTO getOne(@PathVariable Long name) {
        return ModelHelper.enrich(teamService.getCourse(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Course '" + name + "' not found!")));
    }

    @GetMapping("/{name}/enrolled")
    List<StudentDTO> enrolledStudents(@PathVariable Long name) {
        try {
            return teamService.getEnrolledStudents(name).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping({"", "/"})
    CourseDTO addCourse(@RequestBody @Valid CourseDTO courseDTO, @AuthenticationPrincipal UserDetails userDetails) {
        if (teamService.addCourse(courseDTO, userDetails.getUsername()))
            return ModelHelper.enrich(courseDTO);
        else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course '" + courseDTO.getName() + "' already exists!");
    }

    @PostMapping("/{name}/enrollOne")
    Boolean enrollStudent(@PathVariable Long name, @RequestParam("studentId") String studentId) {
        try {
            return teamService.addStudentToCourse(studentId, name);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{name}/enrollMany")
    List<Boolean> enrollStudents(@PathVariable Long name, @RequestParam("file") MultipartFile file) {
        Tika tika = new Tika();
        Metadata meta = new Metadata();
        meta.add(Metadata.RESOURCE_NAME_KEY, file.getOriginalFilename());
        try {
            String mimeType = tika.detect(file.getInputStream(), meta);
            if (!mimeType.equals("text/csv"))
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, mimeType);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return teamService.addAndEnroll(reader, name);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    @PostMapping("/{name}/teams")
    TeamDTO createTeam(@RequestBody Map<String,Object> obj, @PathVariable Long name) {
        /*{
            "team": {...}
            "memberIds": [...]
    }*/
        try {
            if (obj.containsKey("team") && obj.containsKey("memberIds")) {
                log.info("TEAM");
                List<String> membersId = (List<String>) obj.get("memberIds");
                //String name = String
                log.info("Propose team to members");
                TeamDTO teamDTO = teamService.proposeTeam(name,(String) obj.get("team"),membersId);
                //notificationService.notifyTeam(teamDTO,membersId);
                //TeamDTO team = (TeamDTO) obj.get("te");
                return teamDTO;
            } else throw new ResponseStatusException(HttpStatus.CONFLICT,"Bad_input");
        }catch(CourseNotFoundException c) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        } catch (StudentNotFoundException s) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"memberIds");
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/{name}/vmModel")
    VmModelDTO createVmModel(@RequestBody VmModelDTO vmModelDTO, @PathVariable Long name) {
        try {
            return teamService.addVmModel(vmModelDTO,name);
        }catch(CourseNotFoundException c) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{name}/vmModel")
    VmModelDTO getVmModelDTO( @PathVariable Long name) {
        try {
            return teamService.getVmModel(name);
        }catch(TeamNotFoundException t){
            throw new ResponseStatusException(HttpStatus.CONFLICT, t.getMessage());
        }catch(CourseNotFoundException c) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/{name}/teams/{id}/vmInstances")
    VmInstanceDTO createVmInstance(@RequestBody VmInstanceDTO vmInstanceDTO, @PathVariable Long name, @PathVariable Long id) {
        try {
            return teamService.createVmInstance(vmInstanceDTO,name,id);
        }catch(CourseNotFoundException c) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("{name}/teams/{id}/vmInstances")
    List<VmInstanceDTO> getVmInstancesOfTeam(@PathVariable Long name, @PathVariable Long tid)
    {
        try {
            return teamService.getVmInstancesOfTeam(name,tid);
        }catch(CourseNotFoundException c) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("{name}/teams/{id}/vmInstances/{vid}")
    VmInstanceDTO getVmInstanceOfTeam(@PathVariable Long name, @PathVariable Long id, @PathVariable Long vid)
    {
        try {
            return teamService.getVmInstanceOfTeam(vid,name,id);
        }catch(CourseNotFoundException c) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }


    @GetMapping("{name}/teams/{id}/students")
    List<StudentDTO> getStudentsInTeam(@PathVariable Long name, @PathVariable Long tid)
    {
        try {
            return teamService.getStudentsInATeam(name,tid);
        } catch(TeamNotFoundException c) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,c.getMessage());
        }catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/{name}/teams/{tid}/vmInstances/{vmid}/command")
    VmInstanceDTO changeStatusVM(@RequestBody VmStatus command, @PathVariable Long name, @PathVariable Long tid, @PathVariable Long vmid) {
        try {
            return teamService.changeStatusVM(command,name,tid,vmid);
        }catch(CourseNotFoundException c) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        }catch(VmIstanceNotFound e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }catch(VmPermissionDenied e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }


    @PostMapping("/{name}/teams/{tid}/vmInstances/{vmid}/addOwners")
    List<Boolean> addOwners(@RequestBody List<String> studentIds, @PathVariable Long name, @PathVariable Long tid, @PathVariable Long vmid) {
        try {
            return teamService.addOwnersVM(studentIds,name,tid,vmid);
        }catch(CourseNotFoundException c) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        }catch(VmIstanceNotFound e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }catch(VmPermissionDenied e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{name}/teams")
    List<TeamDTO> listTeams(@PathVariable Long name) {
        try {
            return teamService.getTeamsForCourse(name);
        } catch (CourseNotFoundException c) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        }
    }

    @GetMapping("/{name}/availableStudents")
    List<StudentDTO> listFreeStudents(@PathVariable Long name){
        try {
            return teamService.getAvailableStudents(name);
        } catch (CourseNotFoundException c) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        }
    }

    @GetMapping("/{name}/studentsInTeam")
    List<StudentDTO> studentsInTeam(@PathVariable Long name){
        try {
            return teamService.getStudentsInTeams(name);
        } catch (CourseNotFoundException c) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        }
    }

    @GetMapping("/{name}/enableCourse")
    public void enableCourse(@PathVariable Long name ){
        try{
            teamService.enableCourse(name);
        }catch (CourseNotFoundException c){
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        }
    }

    @GetMapping("/{name}/disableCourse")
    public void disableCourse(@PathVariable Long name ){
        try{
            teamService.disableCourse(name);
        }catch (CourseNotFoundException c){
            throw new ResponseStatusException(HttpStatus.CONFLICT,name.toString());
        }
    }
}
