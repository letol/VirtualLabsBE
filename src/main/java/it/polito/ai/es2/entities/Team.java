package it.polito.ai.es2.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Team {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String name;

    private int status;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_team",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> members = new ArrayList<>();

    public boolean setCourse(Course course) {
        if (course != null)
            course.getTeams().add(this);
        else
            course.getTeams().remove(this);

        if (this.course == course)
            return false;

        this.course = course;
        return true;
    }

    public boolean addMember(Student student) {
        if (this.members.contains(student))
            return false;
        else {
            this.members.add(student);
            student.getTeams().add(this);
            return true;
        }
    }

    public boolean removeMember(Student student) {
        if (this.members.contains(student)) {
            this.members.remove(student);
            student.getTeams().remove(this);
            return true;
        } else
            return false;
    }
}
