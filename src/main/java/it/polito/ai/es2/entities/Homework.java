package it.polito.ai.es2.entities;

import it.polito.ai.es2.HomeworkId;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Homework {

    public enum homeworkStatus {
        NULL,
        READ,
        SUBMITTED,
        REVIEWED
    }

    @EmbeddedId
    private HomeworkId id;

    @NotEmpty
    @Column(nullable = false)
    private homeworkStatus status;

    private boolean canSubmit = true;

    private int score;

    @ManyToOne
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    private Assignment assignment;

    @OneToMany(mappedBy = "homework")
    private List<HomeworkVersion> versions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;
}
