package it.polito.ai.es2.entities;

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
        COMMITTED,
        REVIEWED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private homeworkStatus status;

    private int score;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @OneToMany(mappedBy = "homework")
    private List<HomeworkVersion> versions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
