package it.polito.ai.es2.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;

@Entity
@Data
public class HomeworkVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Column(nullable = false, length = 1000)
    private byte[] content;

    @Column(nullable = false)
    Timestamp timestamp;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "assignment_id"),
            @JoinColumn(name = "student_id")
    })
    private Homework homework;

    public boolean setHomework(Homework homework) {
        if (this.homework == homework)
            return false;

        if (this.homework != null)
            this.homework.getVersions().remove(this);

        if (homework != null && !homework.getVersions().contains(this))
            homework.getVersions().add(this);

        this.homework = homework;
        return true;
    }
}
