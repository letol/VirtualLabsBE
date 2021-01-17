package it.polito.ai.es2.services;


public interface PermissionEvaluator {

    boolean courseOwner(String professor, String course);
    boolean studentInCourse(String student, String course);
    boolean studentInTeam(String student, Long teamId);
}
