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

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 1000)
    private byte[] avatar;

    @OneToOne
    private User authUser;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_name"))
    private List<Course> courses = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    private List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<Homework> homeworks = new ArrayList<>();

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

    public boolean addHomework(Homework homework) {
        if (this.homeworks.contains(homework))
            return false;
        else {
            this.homeworks.add(homework);
            homework.setStudent(this);
            return true;
        }
    }

    public boolean removeHomework(Homework homework) {
        if (this.homeworks.contains(homework)) {
            this.homeworks.remove(homework);
            homework.setStudent(null);
            return true;
        } else
            return false;
    }
}
