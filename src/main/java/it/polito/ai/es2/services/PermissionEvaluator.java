package it.polito.ai.es2.services;


import it.polito.ai.es2.HomeworkId;

public interface PermissionEvaluator {

    boolean teacherHasCourse(String teacherId, Long course);
    boolean teacherHasCourseOfTeam(String teacherId, Long teamId);
    boolean teacherHasCourseOfAssignment(String teacherId, Long assignmentId);
    boolean teacherHasCourseOfHomeworkVersion(String teacherId, Long homeworkVersionId);
    boolean studentEnrolledInCourse(String studentId, Long course);
    boolean studentEnrolledInCourseOfTeam(String studentId, Long teamId);
    boolean studentEnrolledInCourseOfAssignment(String studentId, Long assignmentId);
    boolean studentHasHomework(String studentId, HomeworkId homeworkId);
    boolean studentHasHomeworkVersion(String studentId, Long homeworkVersionId);
}
