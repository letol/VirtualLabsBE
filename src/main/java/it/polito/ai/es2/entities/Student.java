package it.polito.ai.es2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Student {

    @Id
    private String id;

    private String lastName;

    private String firstName;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_name"))
    private List<Course> courses = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    private List<Team> teams = new ArrayList<>();

    public boolean addCourse(Course course) {
        if (this.courses.contains(course))
            return false;
        else {
            this.courses.add(course);
            course.getStudents().add(this);
            return true;
        }
    }

    public boolean addTeam(Team team) {
        if (this.teams.contains(team))
            return false;
        else {
            this.teams.add(team);
            team.getMembers().add(this);
            return true;
        }
    }

    public boolean removeTeam(Team team) {
        if (this.teams.contains(team)) {
            this.teams.remove(team);
            team.getMembers().remove(this);
            return true;
        } else
            return false;
    }
}
