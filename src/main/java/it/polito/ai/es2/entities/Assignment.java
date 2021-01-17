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

    @NotEmpty
    @Column(nullable = false, length = 1000)
    private byte[] content;

    @Column(nullable = false)
    private Timestamp releaseDate;

    @NotEmpty
    @Column(nullable = false)
    private Timestamp expiryDate;

    @ManyToOne
    private Course course;

    @OneToMany(mappedBy = "assignment")
    private List<Homework> homeworks = new ArrayList<>();
}
