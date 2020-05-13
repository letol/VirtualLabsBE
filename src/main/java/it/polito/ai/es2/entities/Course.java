package it.polito.ai.es2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Course {

    @Id
    private String name;

    @Column(nullable = false)
    private int min;

    @Column(nullable = false)
    private int max;

    private boolean enabled;

    @ManyToOne
    private Teacher teacher;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Team> teams;

    public boolean addStudent(Student student) {
        if (this.students.contains(student))
            return false;
        else {
            this.students.add(student);
            student.getCourses().add(this);
            return true;
        }
    }

    public boolean addTeam(Team team) {
        if (this.teams.contains(team))
            return false;
        else {
            this.teams.add(team);
            team.setCourse(this);
            return true;
        }
    }

    public boolean removeTeam(Team team) {
        if (this.teams.contains(team)) {
            this.teams.remove(team);
            team.setCourse(null);
            return true;
        } else
            return false;
    }
}
