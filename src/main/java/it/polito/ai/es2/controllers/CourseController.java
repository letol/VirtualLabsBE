package it.polito.ai.es2.controllers;

import it.polito.ai.es2.HomeworkId;
import it.polito.ai.es2.dtos.*;
import it.polito.ai.es2.exceptions.HomeworkCannotBeSubmittedException;
import it.polito.ai.es2.exceptions.TeamServiceException;
import it.polito.ai.es2.services.NotificationService;
import it.polito.ai.es2.services.TeamService;
import it.polito.ai.es2.utility.VmStatus;
import lombok.extern.java.Log;
import org.apache.tika.Tika;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
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

    @GetMapping("/{courseId}")
    CourseDTO getOne(@PathVariable Long courseId) {
        return ModelHelper.enrich(teamService.getCourse(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Course '" + courseId + "' not found!")));
    }

    @DeleteMapping("/{courseId}")
    void removeOne(@PathVariable Long courseId) {
        try {
            teamService.deleteCourse(courseId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{courseId}/teachers")
    List<TeacherDTO> listCourseTeachers(@PathVariable Long courseId) {
        try {
            return teamService.getTeachersOfCourse(courseId).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{courseId}/addTeacher")
    TeacherDTO addTeacher(@PathVariable Long courseId, @RequestParam("teacherId") String teacherId) {
        try {
            return ModelHelper.enrich(teamService.addTeacherToCourse(teacherId, courseId));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{courseId}/removeTeacher")
    TeacherDTO removeTeacher(@PathVariable Long courseId, @RequestParam("teacherId") String teacherId) {
        try {
            return ModelHelper.enrich(teamService.removeTeacherFromCourse(teacherId, courseId));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/enrolled")
    List<StudentDTO> enrolledStudents(@PathVariable Long courseId) {
        try {
            return teamService.getEnrolledStudents(courseId).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping({"", "/"})
    CourseDTO addCourse(@RequestBody @Valid CourseDTO courseDTO, @AuthenticationPrincipal UserDetails userDetails) {
        try{
            return ModelHelper.enrich(teamService.addCourse(courseDTO, userDetails.getUsername()));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping({"","/"})
    CourseDTO editCourse(@RequestBody @Valid CourseDTO courseDTO, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ModelHelper.enrich(teamService.editCourse(courseDTO, userDetails.getUsername()));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{courseId}/enrollOne")
    StudentDTO enrollStudent(@PathVariable Long courseId, @RequestParam("studentId") String studentId) {
        try {
            return teamService.addStudentToCourse(studentId, courseId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{courseId}/unenrollStudent")
    StudentDTO unenrollStudent(@PathVariable Long courseId, @RequestParam("studentId") String studentId) {
        try {
            return teamService.removeStudentFromCourse(studentId, courseId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{courseId}/enrollMany")
    List<Boolean> enrollStudents(@PathVariable Long courseId, @RequestParam("file") MultipartFile file) {
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
            return teamService.addAndEnroll(reader, courseId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }



    @PostMapping("/{courseId}/teams")
    ProposalNotificationDTO createTeam(@RequestBody RequestTeamDTO team, @PathVariable Long courseId) {
        try {
            return ModelHelper.enrich(courseId,teamService.proposeTeam(courseId,team));

        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{courseId}/teams/{teamId}")
    TeamDTO updateTeam(@Valid @RequestBody TeamDTO teamDTO, @PathVariable Long courseId, @PathVariable Long teamId) {
        try {
            return ModelHelper.enrich(courseId,teamService.updateTeam(courseId,teamId,teamDTO));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}/teams/{teamId}/vmInstances/{vid}")
    boolean deleteVmInstance(@PathVariable Long courseId, @PathVariable Long teamId, @PathVariable Long vid)
    {
        try {
            return teamService.deleteVmInstance(vid,courseId,teamId);
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
    @PostMapping("/{courseId}/vmModel")
    VmModelDTO createVmModel(@RequestBody VmModelDTO vmModelDTO, @PathVariable Long courseId) {
        try {
            return ModelHelper.enrich(courseId,teamService.addVmModel(vmModelDTO,courseId));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/vmModel")
    VmModelDTO getVmModelDTO( @PathVariable Long courseId) {
        try {
            return ModelHelper.enrich(courseId,teamService.getVmModel(courseId));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{courseId}/teams/{id}/vmInstances")
    VmInstanceDTO createVmInstance(@Valid @RequestBody VmInstanceDTO vmInstanceDTO, @PathVariable Long courseId, @PathVariable Long id) {
        try {
            return ModelHelper.enrich(courseId,id,teamService.createVmInstance(vmInstanceDTO,courseId,id));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("{courseId}/teams/{tid}/vmInstances")
    List<VmInstanceDTO> getVmInstancesOfTeam(@PathVariable Long courseId, @PathVariable Long tid)
    {
        try {
            return teamService.getVmInstancesOfTeam(courseId,tid).stream().map(p->ModelHelper.enrich(courseId,tid,p)).collect(Collectors.toList());
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("{courseId}/teams/{id}/vmInstances/{vid}")
    VmInstanceDTO getVmInstanceOfTeam(@PathVariable Long courseId, @PathVariable Long id, @PathVariable Long vid)
    {
        try {
            return ModelHelper.enrich(courseId,id,teamService.getVmInstanceOfTeam(vid,courseId,id));
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }


    @GetMapping("{courseId}/teams/{tid}/students")
    List<StudentDTO> getStudentsInTeam(@PathVariable Long courseId, @PathVariable Long tid)
    {
        try {
            return teamService.getStudentsInATeam(courseId,tid).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{courseId}/teams/{tid}/vmInstances/{vmid}/command")
    VmInstanceDTO changeStatusVM(@RequestBody VmStatus command, @PathVariable Long courseId, @PathVariable Long tid, @PathVariable Long vmid) {
        try {
            return ModelHelper.enrich(courseId,tid,teamService.changeStatusVM(command,courseId,tid,vmid));

        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }


    @PostMapping("/{courseId}/teams/{tid}/vmInstances/{vmid}/owners")
    List<Boolean> addOwners(@RequestParam("studentIds") List<String> studentIds, @PathVariable Long courseId, @PathVariable Long tid, @PathVariable Long vmid) {
        try {
            return teamService.addOwnersVM(studentIds,vmid,tid,courseId);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/teams/{tid}/vmInstances/{vmid}/owners")
    List<StudentDTO> getOwnersVm(@PathVariable Long courseId, @PathVariable Long tid, @PathVariable Long vmid) {
        try {
            return teamService.getOwnersVm(vmid,tid,courseId).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        }catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/teams/{tid}/vmInstances/{vmid}/creator")
    StudentDTO getCreatorVm(@PathVariable Long courseId, @PathVariable Long tid, @PathVariable Long vmid) {
        try {
            return ModelHelper.enrich(teamService.getCreatorVm(vmid,tid,courseId));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }


    @PutMapping("/{courseId}/teams/{tid}/vmInstances/{vmid}")
    VmInstanceDTO updateVm(@Valid @RequestBody VmInstanceDTO vmInstanceDTO, @PathVariable Long courseId, @PathVariable Long tid, @PathVariable Long vmid) {
        try {
            return ModelHelper.enrich(courseId,tid,teamService.updateVmInstance(vmid,tid,courseId, vmInstanceDTO));
        } catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());
        }
    }

    @GetMapping("/{courseId}/teams")
    List<TeamDTO> listTeams(@PathVariable Long courseId) {
        try {
            return teamService.getTeamsForCourse(courseId).stream().map(p -> ModelHelper.enrich(courseId,p)).collect(Collectors.toList());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/availableStudents")
    List<StudentDTO> listFreeStudents(@PathVariable Long courseId){
        try {
            return teamService.getAvailableStudents(courseId).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/studentsInTeam")
    List<StudentDTO> studentsInTeam(@PathVariable Long courseId){
        try {
            return teamService.getStudentsInTeams(courseId).stream()
                    .map(ModelHelper::enrich)
                    .collect(Collectors.toList());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/enableCourse")
    public boolean enableCourse(@PathVariable Long courseId ){
        try{
            teamService.enableCourse(courseId);
            return true;
        } catch (TeamServiceException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/disableCourse")
    public boolean disableCourse(@PathVariable Long courseId ){
        try{
            teamService.disableCourse(courseId);
            return true;
        } catch (TeamServiceException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

/*
    //Only for testing
    @GetMapping("/vmstatus")
    public VmStatus getNotifications( ){
        try{
            return VmStatus.RUNNING;
        } catch (TeamServiceException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
*/
    
    @GetMapping("/{courseId}/proposalNotifications/{id}/creator")
    public StudentDTO getProposalCreator(@PathVariable Long courseId,@PathVariable Long id ){
        try{
            return ModelHelper.enrich(teamService.getCreatorProposal(courseId,id));
        } catch (TeamServiceException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/proposalNotifications/{id}/members")
    public List<StudentDTO> getProposalMembers(@PathVariable Long courseId,@PathVariable Long id ){
        try{
            return teamService.getMembersProposal(courseId,id).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        }catch (TeamServiceException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/assignments")
    List<AssignmentDTO> listAssignments(@PathVariable Long courseId) {
        try {
            return teamService.getAssignmentsForCourse(courseId).stream()
                    .map(assignmentDTO -> ModelHelper.enrich(courseId, assignmentDTO))
                    .collect(Collectors.toList());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/homeworks")
    List<HomeworkDTO> listHomeworksOfCourse(@PathVariable Long courseId) {
        try {
            return teamService.getHomeworksForCourse(courseId).stream()
                    .map(homeworkDTO -> ModelHelper.enrich(courseId, homeworkDTO.getAssignment_id(), homeworkDTO))
                    .collect(Collectors.toList());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{courseId}/assignment")
    AssignmentDTO addAssignment(@PathVariable Long courseId,
                                @RequestPart("name") String assignmentName,
                                @RequestParam("expiryDate") Long expiryDate,
                                @RequestPart("content") MultipartFile content) {
        try {
            AssignmentDTO assignmentDTO = AssignmentDTO.builder()
                    .name(assignmentName)
                    .expiryDate(new Timestamp(expiryDate))
                    .build();
            return ModelHelper.enrich(courseId, teamService.addAssignment(assignmentDTO, content, courseId));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{courseId}/assignment/{assignmentId}")
    AssignmentDTO getAssignment(@PathVariable Long courseId, @PathVariable Long assignmentId) {
        try {
            return ModelHelper.enrich(courseId, teamService.getAssignment(courseId, assignmentId));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/assignment/{assignmentId}/content")
    ResponseEntity<Resource> getContentOfAssignment(@PathVariable Long courseId, @PathVariable Long assignmentId) {
        try {
            DocumentDTO documentDTO = teamService.getDocumentOfAssignment(courseId, assignmentId);
            return ResponseEntity.ok()
                    .contentLength(documentDTO.getSize())
                    .contentType(MediaType.parseMediaType(documentDTO.getMimeType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentDTO.getName() + "\"")
                    .body(documentDTO.getContent());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
    @GetMapping("/{courseId}/teams/{tid}/vmInstances/{vmid}/show")
    ResponseEntity<Resource> showVm(@PathVariable Long courseId, @PathVariable Long tid, @PathVariable Long vmid) {
        try {
            byte[] ret = teamService.showVm(vmid,tid,courseId);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(new ByteArrayResource(ret));
        } catch(TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());
        }
    }

    @GetMapping("/{courseId}/assignment/{assignmentId}/homeworks")
    List<HomeworkDTO> listHomeworks(@PathVariable Long courseId, @PathVariable Long assignmentId) {
        try {
            return teamService.getHomeworksForAssignment(courseId, assignmentId).stream()
                    .map(homeworkDTO -> ModelHelper.enrich(courseId, assignmentId, homeworkDTO))
                    .collect(Collectors.toList());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/assignment/{assignmentId}/homework/{studentId}")
    HomeworkDTO getHomework(@PathVariable Long courseId, @PathVariable Long assignmentId, @PathVariable String studentId) {
        try {
            return ModelHelper.enrich(courseId, assignmentId, teamService.getHomework(courseId, new HomeworkId(assignmentId, studentId)));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{courseId}/assignment/{assignmentId}/homework/{studentId}/submit")
    HomeworkVersionDTO submitHomework(@PathVariable Long courseId, @PathVariable Long assignmentId, @PathVariable String studentId,
                                      @RequestPart("content") MultipartFile content) {
        try {
            return ModelHelper.enrich(courseId, assignmentId, studentId, teamService.submitHomeworkVersion(
                    courseId,
                    new HomeworkId(assignmentId, studentId),
                    content
            ));
        } catch (HomeworkCannotBeSubmittedException noSubmission) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Homework submission has been denied");
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{courseId}/assignment/{assignmentId}/homework/{studentId}/review")
    HomeworkVersionDTO reviewHomework(@PathVariable Long courseId, @PathVariable Long assignmentId, @PathVariable String studentId,
                                      @RequestPart("content") MultipartFile content,
                                      @RequestParam("canReSubmit") boolean canReSubmit) {
        try {
            return ModelHelper.enrich(courseId, assignmentId, studentId, teamService.reviewHomeworkVersion(
                    courseId,
                    new HomeworkId(assignmentId, studentId),
                    content,
                    canReSubmit
            ));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{courseId}/assignment/{assignmentId}/homework/{studentId}/setScore")
    HomeworkDTO setScore(@RequestParam("score") Integer score, @PathVariable Long courseId, @PathVariable Long assignmentId, @PathVariable String studentId) {
        try {
            return ModelHelper.enrich(courseId, assignmentId, teamService.setScore(
                    courseId,
                    new HomeworkId(assignmentId, studentId),
                    score
            ));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/assignment/{assignmentId}/homework/{studentId}/versions")
    List<HomeworkVersionDTO> listHomeworkVersions(@PathVariable Long courseId, @PathVariable Long assignmentId, @PathVariable String studentId) {
        try {
            return teamService.getHomeworkVersions(courseId, new HomeworkId(assignmentId, studentId)).stream()
                    .map(homeworkVersionDTO -> ModelHelper.enrich(courseId, assignmentId, studentId, homeworkVersionDTO))
                    .collect(Collectors.toList());
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/assignment/{assignmentId}/homework/{studentId}/version/{versionId}")
    HomeworkVersionDTO getHomeworkVersion(@PathVariable Long courseId, @PathVariable Long assignmentId, @PathVariable String studentId, @PathVariable Long versionId) {
        try {
            return ModelHelper.enrich(courseId, assignmentId, studentId, teamService.getHomeworkVersion(courseId, new HomeworkId(assignmentId, studentId), versionId));
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/assignment/{assignmentId}/homework/{studentId}/version/{versionId}/content")
    ResponseEntity<Resource> getContentOfHomeworkVersion(@PathVariable Long courseId, @PathVariable Long assignmentId, @PathVariable String studentId, @PathVariable Long versionId) {
        try {
            DocumentDTO documentDTO = teamService.getDocumentOfHomeworkVersion(courseId, new HomeworkId(assignmentId, studentId), versionId);
            return ResponseEntity.ok()
                    .contentLength(documentDTO.getSize())
                    .contentType(MediaType.parseMediaType(documentDTO.getMimeType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentDTO.getName() + "\"")
                    .body(documentDTO.getContent());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (TeamServiceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
