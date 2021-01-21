package it.polito.ai.es2.services;


import it.polito.ai.es2.HomeworkId;

public interface PermissionEvaluator {

    boolean teacherHasCourse(String teacherId, String courseName);
    boolean teacherHasCourseOfTeam(String teacherId, Long teamId);
    boolean teacherHasCourseOfAssignment(String teacherId, Long assignmentId);
    boolean studentEnrolledInCourse(String studentId, String courseName);
    boolean studentEnrolledInCourseOfTeam(String studentId, Long teamId);
    boolean studentEnrolledInCourseOfAssignment(String studentId, Long assignmentId);
    boolean studentHasHomework(String studentId, HomeworkId homeworkId);
}
