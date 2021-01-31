package it.polito.ai.es2.services;


public interface PermissionEvaluator {

    boolean courseOwner(String professor, Long course);
    boolean studentInCourse(String student, Long course);
    boolean studentInTeam(String student, Long teamId);
}
