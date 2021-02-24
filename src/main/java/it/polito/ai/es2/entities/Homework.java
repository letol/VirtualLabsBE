package it.polito.ai.es2.entities;

import it.polito.ai.es2.HomeworkId;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@IdClass(HomeworkId.class)
public class Homework {

    public enum homeworkStatus {
        NULL,
        READ,
        SUBMITTED,
        REVIEWED,
        DEFINITELY_REVIEWED,
        SCORED
    }

    @Id
    private Long assignment_id;

    @Id
    private String student_id;

    @Column(nullable = false)
    private homeworkStatus currentStatus;

    private boolean canSubmit = true;

    private int score = 0;

    @ManyToOne
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    private Assignment assignment;

    @OneToMany(mappedBy = "homework", orphanRemoval = true)
    private List<HomeworkVersion> versions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    public boolean setAssignment(Assignment assignment) {
        if (this.assignment == assignment)
            return false;

        if (this.assignment != null)
            this.assignment.getHomeworks().remove(this);

        if (assignment != null && !assignment.getHomeworks().contains(this))
            assignment.getHomeworks().add(this);

        this.assignment = assignment;
        return true;
    }

    public boolean addHomeworkVersion(HomeworkVersion homeworkVersion) {
        if (this.versions.contains(homeworkVersion))
            return false;
        else {
            this.versions.add(homeworkVersion);
            homeworkVersion.setHomework(this);
            return true;
        }
    }

    public boolean removeHomeworkVersion(HomeworkVersion homeworkVersion) {
        if (this.versions.contains(homeworkVersion)) {
            this.versions.remove(homeworkVersion);
            homeworkVersion.setHomework(null);
            return true;
        } else
            return false;
    }

    public boolean setStudent(Student student) {
        if (this.student == student)
            return false;

        if (this.student != null)
            this.student.getHomeworks().remove(this);

        if (student != null && !student.getHomeworks().contains(this))
            student.getHomeworks().add(this);

        this.student = student;
        return true;
    }
}
