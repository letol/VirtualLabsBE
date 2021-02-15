package it.polito.ai.es2.entities;
import javax.persistence.*;

import antlr.CharScanner;
import it.polito.ai.es2.utility.VmStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmInstance {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false)
    private int vcpu;

    @NonNull
    @Column(nullable = false)
    private float disk;

    @NonNull
    @Column(nullable = false)
    private float memory;

    @NonNull
    @Column(nullable = false)
    private VmStatus status;

    @ManyToOne()
    @JoinColumn(name = "vmModel_id", nullable = false)
    private VmModel vmModel;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student creator;

    @ManyToMany(mappedBy = "ownedVMs")
    private List<Student> owners = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    public boolean addOwner(Student student) {
        if (this.owners.contains(student))
            return false;
        else {
            this.owners.add(student);
            student.getOwnedVMs().add(this);
            return true;
        }
    }


}

