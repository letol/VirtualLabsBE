package it.polito.ai.es2.entities;

import it.polito.ai.es2.TeamStatus;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    private TeamStatus status = TeamStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "student_team",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> members = new ArrayList<>();

    public boolean setCourse(Course course) {
        if (this.course == course)
            return false;

        if (this.course != null)
            this.course.getTeams().remove(this);

        if (course != null && !course.getTeams().contains(this))
            course.getTeams().add(this);

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
