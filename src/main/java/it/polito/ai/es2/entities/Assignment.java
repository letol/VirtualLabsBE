package it.polito.ai.es2.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 1000)
    private byte[] content;

    @Column(nullable = false)
    private Timestamp releaseDate;

    @Column(nullable = false)
    private Timestamp expiryDate;

    @ManyToOne
    private Course course;

    @OneToMany(mappedBy = "assignment")
    private List<Homework> homeworks = new ArrayList<>();

    public boolean setCourse(Course course) {
        if (this.course == course)
            return false;

        if (this.course != null)
            this.course.getAssignments().remove(this);

        if (course != null && !course.getAssignments().contains(this))
            course.getAssignments().add(this);

        this.course = course;
        return true;
    }

    public boolean addHomework(Homework homework) {
        if (this.homeworks.contains(homework))
            return false;
        else {
            this.homeworks.add(homework);
            homework.setAssignment(this);
            return true;
        }
    }

    public boolean removeHomework(Homework homework) {
        if (this.homeworks.contains(homework)) {
            this.homeworks.remove(homework);
            homework.setAssignment(null);
            return true;
        } else
            return false;
    }
}
